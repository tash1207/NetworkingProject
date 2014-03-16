package com.networking.project;

public class RemotePeer {
	
	private byte[] bitfield;
	
	private int peerid;
	private String address;
	private int port;
	
	
	public RemotePeer(String address, int port){
		
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
	public boolean sendMessage(Message message){
		return false;
	}
	
	public boolean startConnection(){
		return false;
	}

}
