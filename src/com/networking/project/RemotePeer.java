package com.networking.project;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RemotePeer implements Connectable{


    private int bytesDownloadedSoFar = 0;

	private byte[] bitfield;
	
	private int peerid;
    private int localPeerId;
	private String hostname;
	private int port;
	
	private RemotePeerConnection conn;
	
	private ConcurrentLinkedQueue<Message> messageQueue = new ConcurrentLinkedQueue<Message>();
    private ConcurrentLinkedQueue<Message> outgoingMessageQueue = new ConcurrentLinkedQueue<Message>();

    private boolean isChoked = true;
    private boolean onGoingRequest = false;

    private Queue<Connectable> connectablesToNotify;

	public RemotePeer(int localPeerId, int remotePeerid, String hostname, int port){
        this.peerid = remotePeerid;
        this.localPeerId = localPeerId;

        this.hostname = hostname;
        this.port = port;
	}
	
	public RemotePeer(int remotePeerId, RemotePeerConnection remotePeerConn){
        this.peerid = remotePeerId;
        conn = remotePeerConn;

	}
	
	public int getPeerid() {
		return peerid;
	}

    public void setBitfield(byte[] bitfield) {
        this.bitfield = bitfield;
    }

    public void setPeerid(int peerid){
        this.peerid = peerid;
    }

    public boolean amIChoked(){
        return isChoked;
    }

    public void setChoked(){
        isChoked = true;
    }
    public void setUnchoked(){
        isChoked = false;
    }

    public boolean hasOngoingRequest(){
        return onGoingRequest;
    }

    public void markOngoingRequest(){
        onGoingRequest = true;
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println("Timeout for remote peer request "+ peerid );

                onGoingRequest = false;
            }
        }.start();
    }

    public void clearOngoingRequest(){
        onGoingRequest = false;
    }

    /**
     * Compares two bitfields and sees if there is anything of interest.
     * Meaning if there is a piece that this remote peer has that the input
     * bitfield doesn't have.
     * @param peerBitfield
     * @return
     */
    public boolean hasInterestingPieces(byte[] peerBitfield) {
        return compareBitfields(bitfield, peerBitfield);
    }

    public boolean isInterestedInPeerBitfield(byte[] peerBitfield) {
        if (bitfield == null) return true;
        return compareBitfields(peerBitfield, bitfield);
    }

    /**
     * Compares two bitfields and sees if there is anything of interest.
     * Meaning if there is a piece that this remote peer has that the input
     * bitfield doesn't have.
     * @param peerBitfield
     * @return
     */
    public boolean compareBitfields(byte[] remoteBitfield, byte[] peerBitfield){
        // Check if RemotePeer bitfield has a 1 where Peer bitfield has a 0
        for (int i = 0; i < remoteBitfield.length; i++) {
            for (int j = 1; j < 512; j = j<<1 ) {
                if ((peerBitfield[i] & j) < (remoteBitfield[i] & j)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public void updateBitfieldWithHave(int pieceIndex) {
    	int mask = 1;
		mask = mask << (7 - (pieceIndex % 8));

		if ((bitfield[pieceIndex / 8] & mask) == 0) {
			// we do not have the piece
			return;
		}
    }

	/**
	 * Returns a bitfield that contains a 1 for a random piece that the
	 * remote peer has but the peer does not.
	 * 
	 * Returns null if there are no such pieces.
	 * @param peerBitfield
	 * @return
	 */
	public ArrayList<Integer> retrieveInterestingPieces(byte[] peerBitfield) {
		ArrayList<Integer> indices = new ArrayList<Integer>();
		
		// assuming each bit is one index, i.e. if there are 4 bytes, 32 possible indices
        for (int i = 0; i < bitfield.length; i++) {
            for (int j = 1; j < 8; j += 1 ) {
                int mask = 1<<j;
                if ((peerBitfield[i] & mask) < (bitfield[i] & mask)) {
					indices.add(i*8 + (7-j));
				}
			}
		}
		
        return indices;

		//ByteBuffer pieceBitfield = ByteBuffer.allocate(peerBitfield.length);
		//int pieceIndex = indices.get((int)(Math.random() * indices.size()));
	}

	/**
	 * Send a message to the remote peer. Will return true or false if the message was sent successfully.
	 * @param m
	 * @return boolean
	 */
	public boolean sendMessage(Message m){
        conn.sendMessage(m.toByteArray());
        return true;
	}

    /**
     * returns a download rate in Kb/s
     * @return
     */
    public int getDownloadRate() {
        int t = bytesDownloadedSoFar;
        bytesDownloadedSoFar = 0;
        return t;
    }

    public Message getNextIncomingMessage() {
        return messageQueue.poll();
    }

    public boolean appendReceivedMessageToQueue(byte[] message) {
        // Parse the byte data into a message

        System.out.println("Remote peer got a message!");
        bytesDownloadedSoFar += message.length;

        Message parsedMessage = new Message(message);

        // put it into the queue
        return messageQueue.offer(parsedMessage);
    }

    public Message getNextOutgoingMessage() {
        return outgoingMessageQueue.poll();
    }

    public boolean attachConnectable(Connectable c){
        return connectablesToNotify.offer(c);
    }

    // We'll Call this method when we start the connection
    public void onConnect() {


    }

    // If we need to do anything special if we get disconnected, we do it here
    public void onDisconnect() {

    }
	
	public boolean startConnection() {
		// connect to the remote peer and keep the connection open
		// we want to keep a reference of the output stream
		// we need all that running in a separate thread 

		
		conn = new RemotePeerConnection(hostname, port, localPeerId);
        conn.saveRemotePeerRef(this);
        (new Thread(conn)).start();

		return true;
	}

}
