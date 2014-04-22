package com.networking.project;

import java.util.HashMap;

/**
 * Created by marco on 4/21/14.
 */
public class Bootstrap {

    /**
     * Connects a given Peer to all the previous peerids
     */
    public static void bootstrapPeer(Peer peer, int peerid,  HashMap<Integer, String> reversePeerConfig){
        int currentPeerid = peerid - 1;

        while (currentPeerid > 0){
            String address = reversePeerConfig.get(currentPeerid);
            String hostname = address.split(":")[0];
            String port = address.split(":")[1];

            RemotePeer remote = new RemotePeer(currentPeerid, "localhost", 4002);
            remote.startConnection();
            Log.logTcpConnection(peerid, currentPeerid);


            peer.onRemotePeerConnect(remote);

            currentPeerid--;
        }

    }
}
