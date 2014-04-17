package com.networking.project;

public class ReesesPieces {

	public static void receivedChoke() {
		
	}
	
	public static void receiveUnchoke(Message msg, RemotePeer peer, byte[] bitfield) {
		// should be sending back a request message
		byte[] pieceIndex = peer.retrieveRandomInterestingPiece(bitfield);
		if (pieceIndex != null) {
		    Peer.request(peer, pieceIndex);
		}
	}
	
	public static void receivedInterested() {
		
	}
	
	public static void receivedNotInterested() {
		
	}
	
	public static void receiveHave(RemotePeer peer, byte[] bitfield) {
		// should I send an interested or non interested message?
		if (peer.hasInterestingPieces(bitfield)) {
		    Peer.interested(peer);
	    } else {
			Peer.notInterested(peer);
		}
	}
	
	public static void receiveBitfield(RemotePeer peer, byte[] bitfield) {
		// should I send an interested or non interested message?
		if (peer.hasInterestingPieces(bitfield)) {
			Peer.interested(peer);
		} else {
			Peer.notInterested(peer);
		}
	}
	
	public static void receiveRequest() {
		
	}
	
	public static void receivePiece(Message msg, byte[] bitfield) {
		// piece segment is being received
		byte[] payload = msg.getMessagePayload();
					
		for (int i = 0; i < bitfield.length; i++) {
						
		}
	}
}
