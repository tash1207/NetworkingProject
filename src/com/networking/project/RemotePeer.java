package com.networking.project;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.*;

public class RemotePeer {
	
	private byte[] bitfield;
	
	private int peerid;
	private String address;
	private int port;
	
	private OutputStream out;
	private InputStream in;
	
	private RemotePeerConnection conn;
	
	private ConcurrentLinkedQueue<Message> messageQueue;
    private ConcurrentLinkedQueue<Message> outgoingMessageQueue;

	public RemotePeer(String address, int port){
		//We need to create the socket here
	}
	
	public RemotePeer(String address, int port, OutputStream out, InputStream in, Socket sock){
	}
	
	public int getPeerid() {
		return peerid;
	}

	/**
	 * Compares two bitfields and sees if there is anything of interest.
	 * Meaning if there is a piece that this remote peer has that the input
	 * bitfield doesn't have.
	 * @param message
	 * @return
	 */
	public boolean hasInterestingPieces(byte[] peerBitfield) {
		// Check if RemotePeer bitfield has a 1 where Peer bitfield has a 0
		for (int i = 0; i < bitfield.length; i++) {
			if (bitfield[i] > peerBitfield[i]) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Send a message to the remote peer. Will return true or false if the message was sent successfully.
	 * @param message
	 * @return boolean
	 */
	public boolean sendMessage(Message m) {
		return false;
	}

    /**
     * returns a download rate in Kb/s
     * @return
     */
    public int getDownloadRate() {
        return 0;
    }

    public Message getNextIncomingMessage() {
        return messageQueue.poll();
    }

    public boolean appendReceivedMessageToQueue(byte[] message) {
        // Parse the byte data into a message

        Message parsedMessage = new Message(message);

        // put it into the queue
        return messageQueue.offer(parsedMessage);
    }

    public Message getNextOutgoingMessage() {
        return outgoingMessageQueue.poll();
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

		
		RemotePeerConnection conn;
		return false;
	}

	public void updateOutputStream(OutputStream out) {
		this.out = out;
	}
	
	public void updateInputStream(InputStream in) {
		this.in = in;
	}

}
