package com.networking.project;

public class ReesesPieces {

	public static void receiveHave(Message msg, RemotePeer remotePeer, byte[] bitfield, Peer peer) {
		Log.logHave(peer.getPeerid(), remotePeer.getPeerid(), Util.byteToInt(msg.getMessagePayload()));
		//update remote peer bitfield
		remotePeer.updateBitfieldWithHave(msg.getMessagePayload());
		// should I send an interested or non interested message?
		if (remotePeer.hasInterestingPieces(bitfield)) {
		    peer.interested(remotePeer);
	    } else {
			peer.notInterested(remotePeer);
		}
	}
	
	public static void receiveBitfield(RemotePeer remotePeer, byte[] remoteBitfield, byte[] localBitfield, Peer peer) {
        remotePeer.setBitfield(remoteBitfield);

		// should I send an interested or non interested message?
		if (remotePeer.hasInterestingPieces(localBitfield)) {
			peer.interested(remotePeer);
		} else {
			peer.notInterested(remotePeer);
		}
	}
	
	public static void receiveRequest(Message msg, byte[] bitfield, Peer peer, RemotePeer remotePeer,
			int pieceSize, byte[][] file, int remotePeerIndex) {
		
		// check preferredNeighbors to see if remotePeer is in the list before sending the message
		if (remotePeerIndex == -1) {
			return;
		}
		
		// piece segment is being received
		byte[] payload = msg.getMessagePayload();

        if (payload.length - 4 < 0) {
			// invalid payload size
			return;
		}
		
		int pieceIndex = Util.byteToInt(payload);
		
		// check to make sure we have the piece being requested
		int mask = 1;
		mask = mask << (7 - (pieceIndex % 8));

		if ((bitfield[pieceIndex / 8] & mask) == 0) {
			// we do not have the piece
			return;
		}
		
		byte[] messagePayload = new byte[4 + pieceSize];
		
		for (int i = 0; i < 4; i++) {
			// index of file goes in first
			messagePayload[i] = payload[i];
		}
		for (int i = 0; i < pieceSize; i++) {
			messagePayload[i+4] = file[pieceIndex][i];
		}
		
		// send the piece to the remote peer
		peer.piece(remotePeer, messagePayload);
	}
	

	public static void receivePiece(Message msg, byte[] bitfield, Peer peer, RemotePeer remotePeer,
			byte[][] file, String fileName, int fileSize) {
		remotePeer.clearOngoingRequest();
	
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
		
		int pieceIndex = Util.byteToInt(payload);

		int mask = 1;
		mask = mask << (7 - (pieceIndex % 8));

		bitfield[pieceIndex / 8] |= mask;
		peer.setBitfield(bitfield);

		// need to send out a have message to let all peers know about the
		// newly acquired piece
		peer.sendHaves(Util.intToByte(pieceIndex));
		
		Log.writePartialFilePiece(peer.getPeerid(), pieceIndex, piece);
		
		file[pieceIndex] = piece;
		peer.setFile(file);
		peer.incrementNumberOfFilePieces();
		Log.logDownloadedPiece(peer.getPeerid(), remotePeer.getPeerid(), pieceIndex, peer.getNumberOfFilePieces());
		
		if (peer.isFileFinishedDownloading()) {
			Log.logCompletionOfDownload(peer.getPeerid());
			Log.writeCompleteFile(fileName, file, fileSize, peer.getPeerid());
		}
	}
}
