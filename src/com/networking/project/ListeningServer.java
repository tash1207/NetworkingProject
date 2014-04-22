package com.networking.project;

import java.net.ServerSocket;
import java.net.Socket;

public class ListeningServer implements Runnable{

    private final int localPeerId;
    private ServerSocket server;
    private int peerid;
    private Peer localPeer;
    private int port;


    public ListeningServer(Peer localPeer, int localPeerId, int port){
        this.localPeer = localPeer;
        this.port = port;
        this.localPeerId = localPeerId;
    }
	
	public static void attachRemotePeer(Socket sock, Peer localPeer, int localPeerId){
        String hostname = sock.getInetAddress().getHostName();
        int port = sock.getPort();

        RemotePeerConnection conn = new RemotePeerConnection(sock, localPeerId);

        // after the handshake we have the remote peer id
        int remotePeerId = conn.getRemotePeerId();

        RemotePeer remotePeer = new RemotePeer(remotePeerId, conn);

        Log.logConnectedFrom(localPeerId, remotePeerId);

        conn.saveRemotePeerRef(remotePeer);
        (new Thread(conn)).start();

        localPeer.onRemotePeerConnect(remotePeer);
	}
	
	public void run(){
		// We need to setup a listening server
		try {
			server = new ServerSocket(port);

			try {
				// Continously check for new connections
				while (true) {
                    // TODO: log that we've accepted a connection from another peer
					final Socket sock = server.accept();

					new Thread() {
						public void run() {
							ListeningServer.attachRemotePeer(sock, localPeer, localPeerId);
						}
					}.start();
				}
			} catch (Exception e) {
				System.out.println("Error in thread: " + e);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		//spawn a new thread anytime a conncetion happens
		
		//create a RemotePeer object and give it to the local peer object
	}

}
