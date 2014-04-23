package com.networking.project;


import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

public class Main {

	public static void main(String[] args) {
        HashMap<String, Integer> peerConfig = new HashMap<String, Integer>();
        HashMap<Integer, String> reversePeerConfig = new HashMap<Integer, String>();

		String whoAmI = null;
		if (args.length > 0) {
			whoAmI = args[0];
		} else {
			System.out.println("You need to enter an argument for peerID");
			return;
		}

        String st;
		try {
			BufferedReader in = new BufferedReader(new FileReader("PeerInfo.cfg"));
			while ((st = in.readLine()) != null) {
				String[] tokens = st.split("\\s+");
				if ((whoAmI != null && whoAmI.equals(tokens[0]))) {
					int peerid = Integer.valueOf(tokens[0]);
					String hostname = tokens[1];
					int port = Integer.valueOf(tokens[2]);
					boolean hasFile = ("1".equals(tokens[3]));

					System.out.println(peerid + " " + hostname + " " + port);

					peerConfig.put(hostname + ":" + port, peerid);
					reversePeerConfig.put(peerid, hostname + ":" + port);

					System.out.println("Spawning peer " + peerid);
					Peer peer = new Peer(peerid, port, hasFile);
					Bootstrap.bootstrapPeer(peer, peerid, reversePeerConfig);
					break;
				}
			}
			
			in.close();
		}
		catch (Exception ex) {
			System.out.println(ex.toString());
		}

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //TODO fix this bug here, the remote peer id is one when it should be 2
        //peer2.chokeAllRemotePeers();

    }


}
