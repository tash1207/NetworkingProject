package com.networking.project;

import java.io.File;
import java.util.HashMap;

public class Main {

	public static void main(String[] args) {
        HashMap<String, Integer> peerConfig = new HashMap<String, Integer>();
        HashMap<Integer, String> reversePeerConfig = new HashMap<Integer, String>();

        peerConfig.put("localhost:4003",1);
        peerConfig.put("localhost:4003",2);

        reversePeerConfig.put(1, "localhost:4002");
        reversePeerConfig.put(2, "localhost:4003");

        System.out.println("Spawning first peer");
        Peer peer = new Peer(1,4002);

        System.out.println("Spawning second peer");
        Peer peer2 = new Peer(2,4003);
        Main.bootstrapPeer(peer2,2,reversePeerConfig);


	}

    public static void bootstrapPeer(Peer peer, int peerid,  HashMap<Integer, String> reversePeerConfig){
        int currentPeerid = peerid - 1;

        while (currentPeerid > 0){
            String address = reversePeerConfig.get(currentPeerid);
            String hostname = address.split(":")[0];
            String port = address.split(":")[1];

            RemotePeer remote = new RemotePeer(1, "localhost", 4002);
            remote.startConnection();

            peer.onRemotePeerConnect(remote);

            currentPeerid--;
        }

    }

}
