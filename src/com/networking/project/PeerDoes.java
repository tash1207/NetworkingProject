package com.networking.project;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by marco on 4/16/14.
 */
public class PeerDoes {
    public static void sendRequest(Peer localPeer, RemotePeer remotePeer) {
        byte[] localBitfield = localPeer.getBitfield();
        System.out.println("making a request");
        ArrayList<Integer> indices = remotePeer.retrieveInterestingPieces(localBitfield);

        ArrayList<Integer> validIndices = new ArrayList<Integer>();

        for (int i = 0; i < indices.size(); i++ ){
            int index = indices.get(i);
            if (!localPeer.hasRequested(index)){
                validIndices.add(index);
            }
        }

        int size = validIndices.size();
        int randIndex = (int)Math.floor(Math.random() * size);

        int randPieceIndex = validIndices.get(randIndex);

        //remember what we requested
        localPeer.markRequested(randPieceIndex);



        localPeer.request(remotePeer, randPieceIndex);
    }
}
