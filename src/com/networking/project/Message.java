package com.networking.project;

public class Message {
	/**
	 * Content differs based on messageType
	 * <br>
	 * <br>have - 4 byte index
	 * <br>bitfield - bitfield
	 * <br>request - 4 byte index
	 * <br>piece - 4 byte index + piece content
	 * 
	 */
	byte[] messagePayload;
	/**
	 * choke, unchoke, interested, not interested, have, bitfield, request, piece
	 */
	byte messageType;
	/**
	 * Length of the messageType + messagePayload but not the messageLength itself
	 */
	int messageLength;
	
	public Message(byte messageType, byte[] messagePayload) {
		this.messagePayload = messagePayload;
		this.messageType = messageType;
		messageLength =  1 + messagePayload.length;
	}
	
}
