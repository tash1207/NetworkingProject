package com.networking.project;

import java.nio.ByteBuffer;

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

	public byte[] toByteArray() {
		ByteBuffer b = ByteBuffer.allocate(messageLength + 4);
		b.putInt(messageLength);
		b.put(messageType);
		b.put(messagePayload);

		return b.array();
	}
	
    public Message(byte[] serializedMessage) {
        //parse the message here
    }

}
