package com.networking.project;

import java.util.concurrent.ConcurrentLinkedQueue;

public class Peer {

    private ConcurrentLinkedQueue<RemotePeer> remotePeers;

	public static void sendHandshake(RemotePeer peer) {
	}
	
	public static void choke(RemotePeer peer) {
		byte messageType = 0;
		byte[] messagePayload = new byte[]{};
		Message message = new Message(messageType, messagePayload);
	}
	
	public static void unchoke(RemotePeer peer) {
		byte messageType = 1;
		byte[] messagePayload = new byte[]{};
		Message message = new Message(messageType, messagePayload);
	}
	
	public static void interested(RemotePeer peer) {
		byte messageType = 2;
		byte[] messagePayload = new byte[]{};
		Message message = new Message(messageType, messagePayload);
	}
	
	public static void notInterested(RemotePeer peer) {
		byte messageType = 3;
		byte[] messagePayload = new byte[]{};
		Message message = new Message(messageType, messagePayload);
	}
	
	public static void have(RemotePeer peer, byte[] pieceIndex) {
		byte messageType = 4;
		Message message = new Message(messageType, pieceIndex);
	}
	
	public static void bitfield(RemotePeer peer, byte[] bitfield) {
		byte messageType = 5;
		Message message = new Message(messageType, bitfield);
	}
	
	public static void request(RemotePeer peer, byte[] pieceIndex) {
		byte messageType = 6;
		Message message = new Message(messageType, pieceIndex);
	}
	
	public static void piece(RemotePeer peer, byte[] payload) {
		byte messageType = 7;
		Message message = new Message(messageType, payload);
	}

    public boolean onRemotePeerConnect(RemotePeer peer){
        return remotePeers.offer(peer);
    }

    public boolean onRemotePeerDisconnect(RemotePeer peer){
        return remotePeers.remove(peer);
    }


}
