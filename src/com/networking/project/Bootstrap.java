package com.networking.project;

import java.util.HashMap;

/**
 * Created by marco on 4/21/14.
 */
public class Bootstrap {

    /**
     * Connects a given Peer to all the previous peerids
     */
    public static void bootstrapPeer(Peer peer, int localPeerId, HashMap<Integer, String> reversePeerConfig){
        int currentPeerid = localPeerId - 1;

        while (currentPeerid > 0){
            String address = reversePeerConfig.get(currentPeerid);
            String hostname = address.split(":")[0];
            String port = address.split(":")[1];

            RemotePeer remote = new RemotePeer(localPeerId, currentPeerid, hostname, Integer.valueOf(port));
            remote.startConnection();
            Log.logTcpConnection(localPeerId, currentPeerid);

            peer.onRemotePeerConnect(remote);

            currentPeerid--;
        }

    }
}
