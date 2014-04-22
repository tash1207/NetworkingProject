package com.networking.project;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Peer {
	private byte[] bitfield;
	
	int numPreferredNeighbors;
	int unchokingInterval;
	int optimisticUnchokingInterval;
	String fileName;
	int fileSize;
	int pieceSize;

    private int peerid, numberOfFilePieces;
    private ConcurrentLinkedQueue<RemotePeer> remotePeers;
	private ArrayList<RemotePeer> chokedRemotePeers;
	private ArrayList<RemotePeer> unchokedRemotePeers;
	private ArrayList<RemotePeer> preferredRemotePeers;
	private HashSet<RemotePeer> interestedRemotePeers;
	private byte[][] file;

    private HashSet<Integer> requestedPieces = new HashSet<Integer>();


    private final ExecutorService pool = Executors.newFixedThreadPool(10);

    
    /**
     *  "Whenever a peer starts, it should read the file Common.cfg and set up the corresponding variables."
     */
    public Peer(int peerid, int port, boolean hasFile) {

        this.peerid = peerid;

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

			System.out.println(numPreferredNeighbors + " " + unchokingInterval + " " + optimisticUnchokingInterval + " "
			+ fileName + " " + fileSize + " " + pieceSize);
		}
		
		remotePeers = new ConcurrentLinkedQueue<RemotePeer>();
		interestedRemotePeers = new HashSet<RemotePeer>();
		chokedRemotePeers = new ArrayList<RemotePeer>();
		unchokedRemotePeers = new ArrayList<RemotePeer>();
		file = new byte[fileSize/pieceSize][];
		bitfield = new byte[fileSize/pieceSize];
		preferredRemotePeers = new ArrayList<RemotePeer>();
		this.peerid = peerid;
		
		numberOfFilePieces = hasFile ? fileSize/pieceSize : 0;
		if (hasFile) {
			// Initialize bitfield to all 1s
            for (int i=0; i<bitfield.length; i++ ) {
				bitfield[i] = (byte) 0xff;
			}
            byte[] bytes = new byte[pieceSize];
            try {
            	BufferedInputStream buf = new BufferedInputStream(new FileInputStream("peer_" + peerid + "/" + fileName));
            	for (int i = 0; i < fileSize / pieceSize; i++) {
            		buf.read(bytes, 0, pieceSize);
            		file[i] = bytes;
            	}
            	buf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            
		}

        setTimers();
    }

    public byte[] getBitfield (){
        return bitfield;
    }
    
    public int getPeerid() {
    	return peerid;
    }
    
    public int getNumberOfFilePieces() {
    	return numberOfFilePieces;
    }
    
    public boolean isFileFinishedDownloading() {
    	return numberOfFilePieces == fileSize/pieceSize;
    }
    
    public void incrementNumberOfFilePieces() {
    	numberOfFilePieces += 1;
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

	public void setTimers() {
		// Set timer for selecting new preferred neighbors
		TimerTask task_preferred = new TimerTask() {
			
			@Override
			public void run() {
				PeerDoes.chokeAndUnchokePreferred(Peer.this, preferredRemotePeers, selectNewPreferredNeighbors());
			}
		};
		Timer timer_preferred = new Timer();
		timer_preferred.scheduleAtFixedRate(task_preferred, 0, unchokingInterval * 1000);
		
		// Set timer for optimistically unchoking a neighbor
		TimerTask task_unchoke = new TimerTask() {
			
			@Override
			public void run() {
				RemotePeer optUnchoke = getAndRemoveRandomChokedPeer();
				if (optUnchoke != null)
					PeerDoes.chokeAndUnchokeOptimistic(Peer.this, optUnchoke, preferredRemotePeers, remotePeers);
			}
		};
		Timer timer_unchoke = new Timer();
		timer_unchoke.scheduleAtFixedRate(task_unchoke, 0, optimisticUnchokingInterval * 1000);


        // Set timer for reading messages
        TimerTask taskRemoteMessages = new TimerTask() {

            @Override
            public void run() {
                readRemoteMessages();
            }
        };
        Timer timerRemoteMessage = new Timer();
        timerRemoteMessage.scheduleAtFixedRate(taskRemoteMessages, 0, 1 * 1000);

        // Set timer for the peer does loop
        TimerTask taskPeerDoes = new TimerTask() {

            @Override
            public void run() {
                peerDoesLoop();
            }
        };
        Timer timerPeerDoes = new Timer();
        timerPeerDoes.scheduleAtFixedRate(taskPeerDoes, 0, 1 * 1000);
	}

    public void peerDoesLoop(){
       PeerDoes.makeRequestsToRemotePeers(this, remotePeers);
    }

	public void choke(RemotePeer peer) {
		byte messageType = 0;
		byte[] messagePayload = new byte[] {};
		Message message = new Message(messageType, messagePayload);
        Log.logChoking(peerid, peer.getPeerid());
        peer.sendMessage(message);
	}

	public void unchoke(RemotePeer peer) {
		byte messageType = 1;
		byte[] messagePayload = new byte[] {};
		Message message = new Message(messageType, messagePayload);
		Log.logUnchoking(peerid, peer.getPeerid());
        peer.sendMessage(message);
	}

	public void interested(RemotePeer peer) {
		byte messageType = 2;
		byte[] messagePayload = new byte[] {};
		Message message = new Message(messageType, messagePayload);
		Log.logInterested(peerid, peer.getPeerid());
        peer.sendMessage(message);
	}

	public void notInterested(RemotePeer peer) {
		byte messageType = 3;
		byte[] messagePayload = new byte[] {};
		Message message = new Message(messageType, messagePayload);
		Log.logNotInterested(peerid, peer.getPeerid());
        peer.sendMessage(message);
	}

	public void have(RemotePeer peer, byte[] pieceIndex) {
		byte messageType = 4;
		Message message = new Message(messageType, pieceIndex);
		int piece_index = (pieceIndex[0] & 0xFF) << 24 | (pieceIndex[1] & 0xFF) << 16 |
			     (pieceIndex[2] & 0xFF) << 8 | (pieceIndex[3] & 0xFF);
		Log.logHave(peerid, peer.getPeerid(), piece_index);
        peer.sendMessage(message);
	}

	public void bitfield(RemotePeer peer, byte[] bitfield) {
		byte messageType = 5;
		Message message = new Message(messageType, bitfield);
        peer.sendMessage(message);
	}

	public void request(RemotePeer peer, int pieceIndex) {
		byte messageType = 6;
        byte[] pieceIndexBuffer = ByteBuffer.allocate(4).putInt(pieceIndex).array();

        Message message = new Message(messageType, pieceIndexBuffer);

        peer.sendMessage(message);
	}

	public void piece(RemotePeer peer, byte[] payload) {
		byte messageType = 7;
		Message message = new Message(messageType, payload);
        peer.sendMessage(message);
	}

    public boolean onRemotePeerConnect(RemotePeer remotePeer){
        System.out.println("got a remote peer connected!!");
        PeerDoes.sendBitfield(this, remotePeer);
        return remotePeers.offer(remotePeer);
    }

	public boolean onRemotePeerDisconnect(RemotePeer peer) {
		return remotePeers.remove(peer);
	}

    public boolean hasRequested(int pieceIndex){
        return requestedPieces.contains(pieceIndex);
    }

    public boolean markRequested(final int pieceIndex){
        // We will mark the piece as requested, and after a minute, take it out.
        // If it has downloaded then we will never see this piece index again
        // If it hasn't then we will be able to retry this piece

        requestedPieces.add(pieceIndex);

        new Thread() {
            public void run() {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println("Taking "+ pieceIndex + " out of the requested set");


                requestedPieces.remove(pieceIndex);
            }
        }.start();

        return true;
    }

	public void sendHaves(byte[] pieceIndex) {
		for (RemotePeer peer : remotePeers) {
			have(peer, pieceIndex);
		}
	}

    /**
     * Returns an ArrayList of RemotePeers of size numPreferredNeighbors who have the largest download rates
     * to the peer and are interested in the peer
     * @return
     */
    public ArrayList<RemotePeer> selectNewPreferredNeighbors() {
    	ArrayList<RemotePeer> preferredNeighbors = new ArrayList<RemotePeer>();

		// Randomly select preferred neighbors if peer has entire file
		if (isFileFinishedDownloading()) {
			ArrayList<RemotePeer> interestedPeers = new ArrayList<RemotePeer>(interestedRemotePeers);
			if (numPreferredNeighbors >= interestedPeers.size())
				preferredNeighbors = interestedPeers;
			else {
				for (int i = 0; i < numPreferredNeighbors; i++) {
					int index = (int) (Math.random() * interestedPeers.size());
					preferredNeighbors.add(interestedPeers.get(index));
					interestedPeers.remove(index);
				}
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
    	preferredRemotePeers = preferredNeighbors;
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
     * @return the new optimistically unchoked peer
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

		Log.logOptimisticallyUnchokedNeighbor(peerid, randomChokedPeer.getPeerid());
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
                parseAndReplyToMessage(m, remotePeer);
            }
        }

    }

	public void setBitfield(byte[] newBitfield) {
		bitfield = newBitfield;
	}
	
	public void setFile(byte[][] newFile) {
		file = newFile;
	}
	
	/**
	 * Parses a message and responds appropriately.
	 */
	public void parseAndReplyToMessage(Message msg, RemotePeer remotePeer) {
		byte messageType = msg.getMessageType();
		switch(messageType) {
		// choke
		case 0:
			Log.logChoking(peerid, remotePeer.getPeerid());
			remotePeer.setChoked();
			break;

		// unchoke
		case 1:
			Log.logUnchoking(peerid, remotePeer.getPeerid());
			remotePeer.setUnchoked();
			break;

		// interested
		case 2:
			Log.logInterested(peerid, remotePeer.getPeerid());
			this.addInterestedRemotePeer(remotePeer);
			break;

		// uninterested
		case 3:
			Log.logNotInterested(peerid, remotePeer.getPeerid());
			this.removeInterestedRemotePeer(remotePeer);
			break;

		// have
		case 4:
			ReesesPieces.receiveHave(msg, remotePeer, bitfield, this);
			break;

		// bitfield
		case 5:
			ReesesPieces.receiveBitfield(remotePeer, msg.getMessagePayload(), bitfield, this);
			break;

		// request
		case 6:
			ReesesPieces.receiveRequest(msg, bitfield, this, remotePeer, pieceSize, file,
					preferredRemotePeers.indexOf(remotePeer));
			break;
		// piece 
		case 7:
			ReesesPieces.receivePiece(msg, bitfield, this, remotePeer, file, fileName);
			break;	
		default:
				break;
		}
	}
}
