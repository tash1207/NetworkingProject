package com.networking.project;

public class ReesesPieces {

	public static void receivedChoke() {
		
	}
	
	public static void receiveUnchoke(Message msg, RemotePeer remotePeer, byte[] bitfield, Peer peer) {
        // We'll mark that this peer has unchoked us, so when we send requests the will show up
        remotePeer.setUnchoked();
	}
	
	public static void receivedInterested() {
		
	}
	
	public static void receivedNotInterested() {
		
	}
	
	public static void receiveHave(RemotePeer remotePeer, byte[] bitfield, Peer peer) {
		// should I send an interested or non interested message?
		if (remotePeer.hasInterestingPieces(bitfield)) {
		    peer.interested(remotePeer);
	    } else {
			peer.notInterested(remotePeer);
		}
	}
	
	public static void receiveBitfield(RemotePeer remotePeer, byte[] bitfield, Peer peer) {
		// should I send an interested or non interested message?
		if (remotePeer.hasInterestingPieces(bitfield)) {
			peer.interested(remotePeer);
		} else {
			peer.notInterested(remotePeer);
		}
	}
	
	public static void receiveRequest() {
		
	}
	
	public static void receivePiece(Message msg, byte[] bitfield, Peer peer, byte[][] file) {
		// piece segment is being received
		byte[] payload = msg.getMessagePayload();
		
		if (payload.length - 4 < 0) {
			// invalid payload size
			return;
		}
		
		byte[] piece = new byte[payload.length-4];
		
		for (int i = 0; i < payload.length-4; i++) {
			piece[i] = payload[i+4];
		}
		
		int pieceIndex = (payload[0] & 0xFF) << 24 |
				     (payload[1] & 0xFF) << 16 |
				     (payload[2] & 0xFF) << 8 |
				     (payload[3] & 0xFF);

		int mask = 1;
		mask = mask << (7 - (pieceIndex % 8));

		bitfield[pieceIndex / 8] |= mask;
		peer.setBitfield(bitfield);
		
		// need to send out a have message to let all peers know about the
		// newly acquired piece
		peer.sendHaves();
		
		file[pieceIndex] = piece;
		peer.setFile(file);
	}
}
