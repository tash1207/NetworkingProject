package com.networking.project;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;

/**
 * Static class for logging all things that need to be logged
 * @author Tasha
 *
 */
public class Log {
	/**
	 * Whenever a peer makes a TCP connection to another peer, it generates the following log:
	 * @param peerid the ID of the peer that generates the log
	 * @param remote_peerid the ID of the remote peer which [peerid] initiates a connection with
	 */
	public static void logTcpConnection(int peerid, int remote_peerid) {
		Date date = new Date();
		String log_text = date.toString() + ": Peer " + peerid + " makes a connection to peer " + remote_peerid + ".\n";
		saveLog(peerid, log_text);
	}

	/**
	 * Whenever a peer is connected from another peer, it generates the following log:
	 * @param peerid the ID of the peer that generates the log
	 * @param remote_peerid the ID of the peer that has initiated a TCP connection to [peerid].
	 */
	public static void logConnectedFrom(int peerid, int remote_peerid) {
		Date date = new Date();
		String log_text = date.toString() + ": Peer " + peerid + " is connected from peer " + remote_peerid + ".\n";
		saveLog(peerid, log_text);
	}

	/**
	 * Whenever a peer changes its preferred neighbors, it generates the following log:
	 * @param peerid the ID of the peer that generates the log
	 * @param preferredNeighbors
	 */
	public static void logPreferredNeighbors(int peerid, ArrayList<RemotePeer> preferredNeighbors) {
		// Log the new preferred neighbors
		Date date = new Date();
		String log_text = date.toString() + ": Peer " + peerid + " has the preferred neighbors [";
		for (int i = 0; i < preferredNeighbors.size(); i++) {
			log_text = log_text + preferredNeighbors.get(i).getPeerid();
			if (i != preferredNeighbors.size() - 1) {
				log_text = log_text + ", ";
			}
		}
		log_text = log_text + "].\n";
		saveLog(peerid, log_text);
	}

	/**
	 * Whenever a peer changes its optimistically-unchoked neighbor, it generates the following log:
	 * @param peerid the ID of the peer that generates the log
	 * @param neighbor_id the peer ID of the optimistically-unchoked neighbor
	 */
	public static void logOptimisticallyUnchokedNeighbor(int peerid, int neighbor_id) {
		Date date = new Date();
		String log_text = date.toString() + ": Peer " + peerid + " has the optimistically-unchoked neighbor " + 
				neighbor_id + ".\n";
		saveLog(peerid, log_text);
	}

	/**
	 * Whenever a peer is unchoked by a neighbor (which means when the peer receives an unchoking message 
	 * from a neighbor), it generates the following log:
	 * @param peerid represents the peer who is unchoked
	 * @param remote_peerid represents the peer who unchokes [peerid].
	 */
	public static void logUnchoking(int peerid, int remote_peerid) {
		Date date = new Date();
		String log_text = date.toString() + ": Peer " + peerid + " is unchoked by " + remote_peerid + ".\n";
		saveLog(peerid, log_text);
	}

	/**
	 * Whenever a peer is choked by a neighbor (which means when the peer receives a choking message from a neighbor), 
	 * it generates the following log: 
	 * @param peerid represents the peer who is choked
	 * @param remote_peerid represents the peer who chokes [peerid]
	 */
	public static void logChoking(int peerid, int remote_peerid) {
		Date date = new Date();
		String log_text = date.toString() + ": Peer " + peerid + " is choked by " + remote_peerid + ".\n";
		saveLog(peerid, log_text);
	}

	/**
	 * Whenever a peer receives a 'have' message, it generates the following log:
	 * @param peerid represents the peer who received the 'have' message
	 * @param remote_peerid represents the peer who sent the message
	 * @param piece_index the piece index contained in the message
	 */
	public static void logHave(int peerid, int remote_peerid, int piece_index) {
		Date date = new Date();
		String log_text = date.toString() + ": Peer " + peerid + " received a 'have' message from " + remote_peerid + 
				" for the piece " + piece_index + ".\n";
		saveLog(peerid, log_text);
	}

	/**
	 * Whenever a peer receives an 'interested' message, it generates the following log:
	 * @param peerid represents the peer who received the 'interested' message
	 * @param remote_peerid represents the peer who sent the message
	 */
	public static void logInterested(int peerid, int remote_peerid) {
		Date date = new Date();
		String log_text = date.toString() + ": Peer " + peerid + " received an 'interested' message from " + 
				remote_peerid + ".\n";
		saveLog(peerid, log_text);
	}

	/**
	 * Whenever a peer receives a 'not interested' message, it generates the following log:
	 * @param peerid represents the peer who received the 'not interested' message
	 * @param remote_peerid represents the peer who sent the message
	 */
	public static void logNotInterested(int peerid, int remote_peerid) {
		Date date = new Date();
		String log_text = date.toString() + ": Peer " + peerid + " received a 'not interested' message from " + 
				remote_peerid + ".\n";
		saveLog(peerid, log_text);
	}

	/**
	 * Whenever a peer finishes downloading a piece, it generates the following log:
	 * @param peerid the peer who downloaded the piece
	 * @param remote_peerid the peer who sent the piece
	 * @param piece_index the piece index the peer has downloaded
	 * @param num_pieces the number of pieces [peerid] has now
	 */
	public static void logDownloadedPiece(int peerid, int remote_peerid, int piece_index, int num_pieces) {
		Date date = new Date();
		String log_text = date.toString() + ": Peer " + peerid + " has downloaded the piece " + piece_index + " from " +  
				remote_peerid + ". Now the number of pieces it has is " + num_pieces + ".\n";
		saveLog(peerid, log_text);
	}

	/**
	 * Whenever a peer finishes downloading the complete file, it generates the following log:
	 * @param peerid
	 */
	public static void logCompletionOfDownload(int peerid) {
		Date date = new Date();
		String log_text = date.toString() + ": Peer " + peerid + " has downloaded the complete file.\n";
		saveLog(peerid, log_text);
	}

	public static void saveLog(int peerid, String log_text) {
		System.out.println(log_text);

		try {
			File log = new File("log_peer_" + peerid + ".log");
			if (!log.exists()) {
				log.createNewFile();
			}
			PrintWriter out = new PrintWriter(new FileWriter(log, true));
			out.append(log_text);
			out.close();
		} catch (IOException e) {

		}
	}
	
	public static void writePartialFilePiece(int peerid, int pieceIndex, byte[] filePiece) {
		try {
			File partial = new File("peer_" + peerid + "/part_" + pieceIndex);
			BufferedOutputStream bos = null;

			//create an object of FileOutputStream
			FileOutputStream fos = new FileOutputStream(partial);

			//create an object of BufferedOutputStream
			bos = new BufferedOutputStream(fos);
			bos.write(filePiece);
			bos.close();
			fos.close();
		} catch (IOException e) {

		}
	}
	
	public static void writeCompleteFile(String fileName, byte[][] file) {
		try {
			File completeFile = new File("peer_" + peerid + "/" + fileName);
			BufferedOutputStream bos = null;

			//create an object of FileOutputStream
			FileOutputStream fos = new FileOutputStream(completeFile);
			
			bos = new BufferedOutputStream(fos);
			
			for (int i = 0; i < file.length; i++) {
				bos.write(file[i]);
			}
			bos.close();
			fos.close();
		} catch (IOException e) {
            e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		logTcpConnection(1, 2);
		logConnectedFrom(2, 1);

		ArrayList<RemotePeer> peers = new ArrayList<RemotePeer>();
		//peers.add(new RemotePeer(2, "hostname", 8080));
		//peers.add(new RemotePeer(3, "tashawych", 8080));

		logPreferredNeighbors(1, peers);
		logOptimisticallyUnchokedNeighbor(1, 3);
		logUnchoking(1, 3);
		logChoking(1, 4);
		logHave(1, 3, 214);
		logInterested(3, 1);
		logNotInterested(4, 1);

		logDownloadedPiece(1, 3, 214, 7);
		logCompletionOfDownload(1);
	}

}
