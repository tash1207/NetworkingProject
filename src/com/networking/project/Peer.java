package com.networking.project;

public class Peer {
	
	public static void sendHandshake(int peerID){
	}
	
	public static void choke(int peerID){
		byte messageType = 0;
	}
	
	public static void unchoke(int peerID){
		byte messageType = 1;
	}
	
	public static void interested(int peerID){
		byte messageType = 2;
	}
	
	public static void notInterested(int peerID){
		byte messageType = 3;
	}
	
	public static void have(int peerID, byte pieceIndex){
		byte messageType = 4;
	}
	
	public static void bitfield(int peerID){
		byte messageType = 5;
	}
	
	public static void request(int peerID, byte pieceIndex){
		byte messageType = 6;
	}
	
	public static void piece(int peerID, byte[] payload){
		byte messageType = 7;
	}
	
	

}
