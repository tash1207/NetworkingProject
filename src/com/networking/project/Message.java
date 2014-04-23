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
	

    public static byte[] createHandshake(int peerid){
        ByteBuffer message = ByteBuffer.allocate(32);

        byte[] hello = ("HELLO").getBytes();
        ByteBuffer zeros = ByteBuffer.allocate(23);

        message.put(hello);
        message.put(zeros.array());
        message.putInt(peerid);

        return message.array();
    }

    public static int parseHandshake(byte[] handshake){
    	ByteBuffer message = ByteBuffer.allocate(28);
        byte[] hello = ("HELLO").getBytes();
        ByteBuffer zeros = ByteBuffer.allocate(23);

        message.put(hello);
        message.put(zeros.array());
        
        byte[] messageBytes = message.array();
        
        for (int i = 0; i < messageBytes.length; i++) {
        	if (messageBytes[i] != handshake[i]) {
        		return -1;
        	}
        }
        
        ByteBuffer remotePeerid = ByteBuffer.allocate(4);
        for (int i = 0; i < 4; i++) {
        	remotePeerid.put(handshake[messageBytes.length+i]);
        }
        
        return Util.byteToInt(remotePeerid.array());
    }

    public byte getMessageType() {
    	return this.messageType;
    }

    public byte[] getMessagePayload() {
        return this.messagePayload;
    }

	public byte[] toByteArray() {
		ByteBuffer b = ByteBuffer.allocate(messageLength + 4);
		b.putInt(messageLength);
		b.put(messageType);
		b.put(messagePayload);

		return b.array();
	}

    public Message(byte[] serializedMessage) {
    	try {
    		if (serializedMessage.length < 4) {
    			//checking for message validity
    			this.messagePayload = null;
    			this.messageType = -1;
    			this.messageLength = -1;
    			return;
    		}
    		//parse the message here
        	this.messageLength = (serializedMessage[3] & 0xFF) |
        			        (serializedMessage[2] & 0xFF) << 8 |
        			        (serializedMessage[1] & 0xFF) << 16 |
        			        (serializedMessage[0] & 0xFF) << 24;
        	this.messageType = serializedMessage[4];
        	ByteBuffer b = ByteBuffer.allocate(messageLength - 1);

        	for (int i = 0; i < messageLength - 1; i++) {
        		b.put(serializedMessage[i+5]);
        	}
        	this.messagePayload = b.array();
        	
    	} catch (Exception e) {
    		// serializedMessage not correct length
            e.printStackTrace();
    	}
    }

}
