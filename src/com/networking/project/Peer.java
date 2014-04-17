package com.networking.project;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Peer {
	private byte[] bitfield;
	
	int numPreferredNeighbors;
	int unchokingInterval;
	int optimisticUnchokingInterval;
	String fileName;
	int fileSize;
	int pieceSize;

    private int peerid;
    private ConcurrentLinkedQueue<RemotePeer> remotePeers;
	private ArrayList<RemotePeer> chokedRemotePeers;
	private ArrayList<RemotePeer> unchokedRemotePeers;
	private HashSet<RemotePeer> interestedRemotePeers;

    
    /**
     *  "Whenever a peer starts, it should read the file Common.cfg and set up the corresponding variables."
     */
    public Peer(int peerid, int port) {

        ListeningServer listeningServer = new ListeningServer(this, peerid, port);
        (new Thread(listeningServer)).start();

    	String[] values = readCommonConfig();
		if (values != null) {
			this.numPreferredNeighbors = Integer.parseInt(values[0]);
			this.unchokingInterval = Integer.parseInt(values[1]);
			this.optimisticUnchokingInterval = Integer.parseInt(values[2]);
			
			fileName = values[3];
			fileSize = Integer.parseInt(values[4]);
			pieceSize = Integer.parseInt(values[5]);
		}
		
		remotePeers = new ConcurrentLinkedQueue<RemotePeer>();
		interestedRemotePeers = new HashSet<RemotePeer>();
		chokedRemotePeers = new ArrayList<RemotePeer>();
		unchokedRemotePeers = new ArrayList<RemotePeer>();

        setTimers();
		// TODO instantiate remotePeers and call setTimers();
    }

    public byte[] getBitfield (){
        return bitfield;
    }

    /**
     * Read the Common.cfg file and only return the values not the keys
     * @return
     */
	public static String[] readCommonConfig() {
		String st;
		String[] values = new String[6];
		try {
			BufferedReader in = new BufferedReader(new FileReader("Common.cfg"));
			int i = 0;
			while((st = in.readLine()) != null) {
				String[] tokens = st.split("\\s+");
				values[i] = tokens[1];
				//System.out.println(tokens[1]);
				i++;
			}
			in.close();
			return values;
		}
		catch (Exception ex) {
			System.out.println(ex.toString());
			return null;
		}
	}

    public void readRemoteMessages(){
        Iterator<RemotePeer> peerIterator = remotePeers.iterator();
        RemotePeer remotePeer;
        while (peerIterator.hasNext()){
            remotePeer = peerIterator.next();
            Message m = remotePeer.getNextIncomingMessage();

            if (m != null){
                System.out.println("Message from " + remotePeer.getPeerid() + ": " + Util.byteToHex(m.getMessageType()));
                System.out.println("Payload from " + remotePeer.getPeerid() + ": " + Util.bytesToHex(m.getMessagePayload()));
                System.out.println("--------------------------------------------------------------------------------");
            }
        }

    }
	
	public void setTimers() {
        /* TODO fix this
		// Set timer for selecting new preferred neighbors
		TimerTask task_preferred = new TimerTask() {
			
			@Override
			public void run() {
				selectNewPreferredNeighbors();
			}
		};
		Timer timer_preferred = new Timer();
		timer_preferred.scheduleAtFixedRate(task_preferred, 0, unchokingInterval * 1000);
		
		// Set timer for optimistically unchoking a neighbor
		TimerTask task_unchoke = new TimerTask() {
			
			@Override
			public void run() {
				getAndRemoveRandomChokedPeer();
			}
		};
		Timer timer_unchoke = new Timer();
		timer_unchoke.scheduleAtFixedRate(task_unchoke, 0, optimisticUnchokingInterval * 1000);
		*/

        // Set timer for reading messages
        TimerTask taskRemoteMessages = new TimerTask() {

            @Override
            public void run() {
                readRemoteMessages();
            }
        };
        Timer timerRemoteMessage = new Timer();
        timerRemoteMessage.scheduleAtFixedRate(taskRemoteMessages, 0, 1 * 1000);
	}

	public static void sendHandshake(RemotePeer peer) {
	}

	public static void choke(RemotePeer peer) {
		byte messageType = 0;
		byte[] messagePayload = new byte[] {};
		Message message = new Message(messageType, messagePayload);
        peer.sendMessage(message);
	}

	public static void unchoke(RemotePeer peer) {
		byte messageType = 1;
		byte[] messagePayload = new byte[] {};
		Message message = new Message(messageType, messagePayload);
	}

	public static void interested(RemotePeer peer) {
		byte messageType = 2;
		byte[] messagePayload = new byte[] {};
		Message message = new Message(messageType, messagePayload);
	}

	public static void notInterested(RemotePeer peer) {
		byte messageType = 3;
		byte[] messagePayload = new byte[] {};
		Message message = new Message(messageType, messagePayload);
	}

	public static void have(RemotePeer peer, byte[] pieceIndex) {
		byte messageType = 4;
		Message message = new Message(messageType, pieceIndex);
	}

	public static void bitfield(RemotePeer peer, byte[] bitfield) {
		byte messageType = 5;
		Message message = new Message(messageType, bitfield);
	}

	public static void request(RemotePeer peer, byte[] pieceIndex) {
		byte messageType = 6;
		Message message = new Message(messageType, pieceIndex);
	}

	public static void piece(RemotePeer peer, byte[] payload) {
		byte messageType = 7;
		Message message = new Message(messageType, payload);
	}

    public boolean onRemotePeerConnect(RemotePeer peer){
        System.out.println("got a remote peer connected!!");
        return remotePeers.offer(peer);
    }

	public boolean onRemotePeerDisconnect(RemotePeer peer) {
		return remotePeers.remove(peer);
	}

    public boolean hasRequested(int pieceIndex){

    }

    public boolean markRequested(int pieceIndex){

        return true;

    }

	public void sendHaves() {
		for (RemotePeer peer : remotePeers) {
			have(peer, bitfield);
		}
	}

    /**
     * Returns an ArrayList of RemotePeers of size numPreferredNeighbors who have the largest download rates
     * to the peer and are interested in the peer
     * @return
     */
    public ArrayList<RemotePeer> selectNewPreferredNeighbors() {
    	// Check if the peer has completed downloading the file
    	boolean hasEntireFile = true;
    	for (int i = 0; i < bitfield.length; i++) {
    		if (bitfield[i] != 0xFFFF) hasEntireFile = false;
    	}
    	
    	ArrayList<RemotePeer> preferredNeighbors = new ArrayList<RemotePeer>();
    	
    	// Randomly select preferred neighbors if peer has entire file
    	if (hasEntireFile) {
    		ArrayList<RemotePeer> interestedPeers = new ArrayList<RemotePeer>(interestedRemotePeers);
    		for (int i = 0; i < numPreferredNeighbors; i++) {
        		int index = (int)(Math.random() * interestedPeers.size());
        		preferredNeighbors.add(interestedPeers.get(index));
    		}
    	}
    	
    	// Otherwise, select interested remote peers with highest download rates
    	else {
	    	Map<Integer, RemotePeer> download_rates = new TreeMap<Integer, RemotePeer>(Collections.reverseOrder());
	    	
	    	// Iterate over all interested remote peers and find their download rates
	    	Iterator<RemotePeer> it_peers = interestedRemotePeers.iterator();
	    	while (it_peers.hasNext()) {
	    		RemotePeer rp = it_peers.next();
	    		download_rates.put(rp.getDownloadRate(), rp);
	    	}
	    	// Iterate over our sorted map of download rates and return <numPreferredNeighbors> remote peers
	    	Iterator<Integer> it_rates = download_rates.keySet().iterator();
	    	int i = 0;
	    	while (it_rates.hasNext() && i < numPreferredNeighbors) {
	    		preferredNeighbors.add(download_rates.get(it_rates.next()));
	    		//System.out.println(preferredNeighbors.get(i).getPeerid() + ": " + preferredNeighbors.get(i).getDownloadRate());
	    		i++;
	    	}
    	}
    	
    	Log.logPreferredNeighbors(peerid, preferredNeighbors);

    	return preferredNeighbors;
    }

    public void chokeAllRemotePeers(){
        Iterator<RemotePeer> peerIterator =  remotePeers.iterator();
        while(peerIterator.hasNext()){
            choke(peerIterator.next());
        }

    }
	
    /**
     * Return an interested RemotePeer that has been added to the unchoked list
     * and removed from the choked list.
     * 
     * If there are no choked remote peers that are interested, then no peer is
     * returned.
     * 
     * @return
     */
	public RemotePeer getAndRemoveRandomChokedPeer() {
		if (interestedRemotePeers.size() == 0) {
			return null;
		}

        HashSet<RemotePeer> chokedPeers = new HashSet<RemotePeer>(chokedRemotePeers);
        chokedPeers.retainAll(interestedRemotePeers);
        ArrayList<RemotePeer> interestedChokedPeers = new ArrayList<RemotePeer>(chokedPeers);
        
        if (interestedChokedPeers.size() == 0) {
        	return null;
        }

		int index = (int)(Math.random() * interestedChokedPeers.size());
		RemotePeer randomChokedPeer = interestedChokedPeers.get(index);

		this.chokedRemotePeers.remove(randomChokedPeer);
		this.unchokedRemotePeers.add(randomChokedPeer);

		return randomChokedPeer;
	}
	
	public boolean addUnchokedPeer(RemotePeer peer) {
		return this.unchokedRemotePeers.add(peer);
	}
	
	public boolean addChokedPeer(RemotePeer peer) {
		return this.chokedRemotePeers.add(peer);
	}
	
	public boolean removeChokedRemotePeer(RemotePeer peer) {
		return this.chokedRemotePeers.remove(peer);
	}
	
	public boolean removeUnchokedRemotePeer(RemotePeer peer) {
		return this.unchokedRemotePeers.remove(peer);
	}
	
	public boolean addInterestedRemotePeer(RemotePeer peer) {
		return this.interestedRemotePeers.add(peer);
	}

	public boolean removeInterestedRemotePeer(RemotePeer peer) {
		return this.interestedRemotePeers.remove(peer);
	}
	
	public void parseRemotePeerMessages() {
		Iterator<RemotePeer> iter = remotePeers.iterator();
		
		while (iter.hasNext()) {
			RemotePeer currentPeer = iter.next();
			Message incomingMessage = currentPeer.getNextIncomingMessage();
			parseAndReplyToMessage(incomingMessage, currentPeer);
		}
	}
	
	public void setBitfield(byte[] newBitfield) {
		bitfield = newBitfield;
	}
	
	/**
	 * Parses a message and responds appropriately.
	 */
	public void parseAndReplyToMessage(Message msg, RemotePeer peer) {
		byte messageType = msg.getMessageType();
		switch(messageType) {
		// choke
		case 0:
			ReesesPieces.receivedChoke();
			break;

		// unchoke
		case 1:
			ReesesPieces.receiveUnchoke(msg, peer, bitfield);
			break;

		// interested
		case 2:
			ReesesPieces.receivedInterested();
			break;

		// uninterested
		case 3:
			ReesesPieces.receivedNotInterested();
			break;

		// have
		case 4:
			ReesesPieces.receiveHave(peer, bitfield);
			break;

		// bitfield
		case 5:
			ReesesPieces.receiveBitfield(peer, bitfield);
			break;

		// request
		case 6:
			ReesesPieces.receiveRequest();
			break;
		// piece 
		case 7:
			ReesesPieces.receivePiece(msg, bitfield, this);
			break;	
		default:
				break;
		}
	}
}
