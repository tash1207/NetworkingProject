package com.networking.project;

import java.util.ArrayList;

/**
 * Created by marco on 4/16/14.
 */
public class PeerDoes {
    public static void sendRequest(Peer localPeer, RemotePeer remotePeer) {
        byte[] localBitfield = localPeer.getBitfield();
        System.out.println("making a request");
        ArrayList<Integer> indices = remotePeer.retrieveInterestingPieces(localBitfield);

        for (int i = 0; i < indices.size(); i++ ){
            int index = indices.get(i);
            if (!localPeer.hasRequested(index)){
                localPeer.markRequsted(index);
            }

        }



    }

}
