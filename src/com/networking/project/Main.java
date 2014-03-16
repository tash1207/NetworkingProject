package com.networking.project;


import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

public class Main {

	public static void main(String[] args) {
        HashMap<String, Integer> peerConfig = new HashMap<String, Integer>();
        HashMap<Integer, String> reversePeerConfig = new HashMap<Integer, String>();

        String st;
		try {
			BufferedReader in = new BufferedReader(new FileReader("PeerInfo.cfg"));
			while((st = in.readLine()) != null) {
				String[] tokens = st.split("\\s+");
				System.out.println(tokens[0] + " " + tokens[1] + " " + tokens[2]);
				
				peerConfig.put(tokens[1] + ":" + tokens[2], Integer.valueOf(tokens[0]));
				reversePeerConfig.put(Integer.valueOf(tokens[0]), tokens[1] + ":" + tokens[2]);
				
				// TODO set peer bitfield with tokens[3]
			}
			
			in.close();
		}
		catch (Exception ex) {
			System.out.println(ex.toString());
		}


        System.out.println("Spawning first peer");
        Peer peer = new Peer(1,4002);

        System.out.println("Spawning second peer");
        Peer peer2 = new Peer(2,4003);
        Main.bootstrapPeer(peer2,2,reversePeerConfig);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        peer2.chokeAllRemotePeers();

        //TODO fix this bug here, the remote peer id is one when it should be 2
        //peer2.chokeAllRemotePeers();


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
