package com.networking.project;

import java.util.HashMap;

public class Main {

	public static void main(String[] args) {
        System.out.println("Spawning first peer");
        Peer peer = new Peer();

        HashMap<String, Integer> peerConfig = new HashMap<String, Integer>();

        peerConfig.put("localhost:4002",1);
        peerConfig.put("localhost:4003",2);


        ListeningServer listeningServer = new ListeningServer(peerConfig, peer, 1, 4001);
        // Spawn the background listening server
        System.out.println("Spawning listening server!");
        (new Thread(listeningServer)).start();
        System.out.println("Server spawned");

        Peer otherPeer = new Peer();

        System.out.println("Spawning remote connection");

        RemotePeerConnection otherPeerConn = new RemotePeerConnection(2, "localhost", 4001);


        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

	}

}
