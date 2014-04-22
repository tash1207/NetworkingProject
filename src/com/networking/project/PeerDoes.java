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

	/**
	 * Called after Peer selectNewPreferredNeighbors() Does the appropriate
	 * choking and unchoking of preferred neighbors
	 * @author Tasha
	 */
	public static void chokeAndUnchoke(Peer peer, ArrayList<RemotePeer> prevPreferred, ArrayList<RemotePeer> newPreferred) {
		// Unchoke new preferred peers that were not previously preferred peers
		for (RemotePeer remotePeer : newPreferred) {
			// If they were previously preferred neighbors, don't need to send unchoke
			if (prevPreferred.contains(remotePeer)) {
				prevPreferred.remove(remotePeer);
			} else {
				peer.unchoke(remotePeer);
			}
		}
		// All previously unchoked neighbors that are not new preferred should be choked
		for (RemotePeer remotePeer : prevPreferred) {
			peer.choke(remotePeer);
		}
	}

}
