package com.networking.project;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class RemotePeer {
	
	private byte[] bitfield;
	
	private int peerid;
	private String address;
	private int port;
	
	private OutputStream out;
	private InputStream in;
	
	private RemotePeerConnection conn;

	public RemotePeer(String address, int port){
		
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
	public boolean hasInterestingPieces(byte[] otherBitfield){
		return false;
	}
	
	/**
	 * Send a message to the remote peer. Will return true or false if the message was sent successfully.
	 * @param message
	 * @return boolean
	 */
	public boolean sendMessage(byte[] message){
		//do something with this.out
		//returns false if output stream isn't initialized yet
		return false;
	}
	
	public boolean startConnection(){
		// connect to the remote peer and keep the connection open
		// we want to keep a reference of the output stream
		// we need all that running in a separate thread 

		
		RemotePeerConnection conn;
		return false;
	}

	public void updateOutputStream(OutputStream out){
		this.out = out;
	}
	
	public void updateInputStream(InputStream in) {
		this.in = in;
	}

}
