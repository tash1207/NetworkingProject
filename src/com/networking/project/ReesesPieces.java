package com.networking.project;

public class ReesesPieces {

	public static void receivedChoke() {
		
	}
	
	public static void receiveUnchoke(Message msg, RemotePeer peer, byte[] bitfield, Peer obj) {
		// should be sending back a request message
		byte[] pieceIndex = peer.retrieveRandomInterestingPiece(bitfield);
		if (pieceIndex != null) {
		    obj.request(peer, pieceIndex);
		}
	}
	
	public static void receivedInterested() {
		
	}
	
	public static void receivedNotInterested() {
		
	}
	
	public static void receiveHave(RemotePeer peer, byte[] bitfield, Peer obj) {
		// should I send an interested or non interested message?
		if (peer.hasInterestingPieces(bitfield)) {
		    obj.interested(peer);
	    } else {
			obj.notInterested(peer);
		}
	}
	
	public static void receiveBitfield(RemotePeer peer, byte[] bitfield, Peer obj) {
		// should I send an interested or non interested message?
		if (peer.hasInterestingPieces(bitfield)) {
			obj.interested(peer);
		} else {
			obj.notInterested(peer);
		}
	}
	
	public static void receiveRequest() {
		
	}
	
	public static void receivePiece(Message msg, byte[] bitfield, Peer obj) {
		// piece segment is being received
		byte[] payload = msg.getMessagePayload();
		
		if (payload.length - 4 < 0) {
			// invalid payload size
			return;
		}
		
		int pieceIndex = (payload[0] & 0xFF) << 24 |
				     (payload[1] & 0xFF) << 16 |
				     (payload[2] & 0xFF) << 8 |
				     (payload[3] & 0xFF);

		int mask = 1;
		mask = mask << (7 - (pieceIndex % 8));

		bitfield[pieceIndex / 8] |= mask;
		obj.setBitfield(bitfield);
		
		// need to send out a have message to let all peers know about the
		// newly acquired piece
		obj.sendHaves();
	}
}
