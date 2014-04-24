package com.networking.project;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by marco on 4/16/14.
 */
public class PeerDoes {

    public static void sendRequest(Peer localPeer, RemotePeer remotePeer) {
        byte[] localBitfield = localPeer.getBitfield();
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

        if (size > 0){
            int randPieceIndex = validIndices.get(randIndex);

            //remember what we requested
            localPeer.markRequested(randPieceIndex);

            localPeer.request(remotePeer, randPieceIndex);
            remotePeer.markOngoingRequest();

            System.out.println("making a request for: "+ randPieceIndex);
        }
    }

    public static void sendBitfield(Peer localPeer, RemotePeer remotePeer){
        byte [] bitfield = localPeer.getBitfield();

        localPeer.bitfield(remotePeer, bitfield);
    }


	/**
	 * Called after Peer selectNewPreferredNeighbors() Does the appropriate
	 * choking and unchoking of preferred neighbors
	 * @author Tasha
	 */
	public static void chokeAndUnchokePreferred(Peer peer, ArrayList<RemotePeer> prevPreferred, 
			ArrayList<RemotePeer> newPreferred) {
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

    /**
     * Single place where requests come from.
     * It'll check if the remote peer is unchoked and it doesn't have an ongoing request
     *
     * @param localPeer
     * @param remotePeers
     *
     * @author Polar Bear
     */
    public static void makeRequestsToRemotePeers(Peer localPeer, ConcurrentLinkedQueue<RemotePeer> remotePeers) {
        Iterator<RemotePeer> peerIterator = remotePeers.iterator();
        RemotePeer remotePeer;
        while (peerIterator.hasNext()) {
            remotePeer = peerIterator.next();

            //Check if we are unchoked from this peer
            if (remotePeer.amIChoked() == false){
                // Check if there is an ongoing request to this peer
                if (remotePeer.hasOngoingRequest() == false){
                    PeerDoes.sendRequest(localPeer, remotePeer);
                }
            }
        }

    }

	/**
	 * Called after Peer selectNewPreferredNeighbors() Does the appropriate
	 * choking and unchoking of preferred neighbors
	 * @author Tasha
	 */
	public static void chokeAndUnchokeOptimistic(Peer peer, RemotePeer optUnchoked, ArrayList<RemotePeer> preferred, 
			ConcurrentLinkedQueue<RemotePeer> remotePeers) {
		// Unchoke the optimistically unchoked remote peer
		peer.unchoke(optUnchoked);
		// Choke everyone who isn't preferred or optimistically unchoked
		Iterator<RemotePeer> it = remotePeers.iterator();
		RemotePeer remotePeer;
		while (it.hasNext()) {
			remotePeer = it.next();
			if (!preferred.contains(remotePeer) && (remotePeer.getPeerid() != optUnchoked.getPeerid())) {
				peer.choke(remotePeer);
			}
		}
	}


    /**
     * Check if all peers have downloaded everything, and shutdown if that was the case.
     */
    public static void checkIfNetworkIsDone(Peer peer, ConcurrentLinkedQueue<RemotePeer> remotePeers){
        if (peer.isFileFinishedDownloading() && remotePeers.size() > 0){
            boolean allDone = true;
            for (RemotePeer rp : remotePeers) {
                allDone &= !rp.isInterestedInPeerBitfield(peer.getBitfield());
            }

            if (allDone){
                System.out.println("Done, shutting down peer: " + peer.getPeerid());
                // Sleep for a bit so everyone is on the same page
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.exit(0);
            }
        }
    }
}
