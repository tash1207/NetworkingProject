package com.networking.project;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class ListeningServer implements Runnable{

    private final int localPeerId;
    private ServerSocket server;
    private int peerid;
    private HashMap<String, Integer> peeridMap;
    private Peer localPeer;
    private int port;


    public ListeningServer(HashMap<String, Integer> peeridMap, Peer localPeer, int localPeerId, int port){
        this.peeridMap = peeridMap;
        this.localPeer = localPeer;
        this.port = port;
        this.localPeerId = localPeerId;
    }
	
	public static void attachRemotePeer(HashMap<String, Integer> peeridMap, Socket sock, Peer localPeer, int localPeerId){
        String hostname = sock.getInetAddress().getHostName();
        int port = sock.getPort();

        //int remotePeerid = peeridMap.get(hostname+":"+port);

        RemotePeerConnection conn = new RemotePeerConnection(localPeerId, sock);

        RemotePeer remotePeer = new RemotePeer(localPeerId, conn);

        localPeer.onRemotePeerConnect(remotePeer);
	}
	
	public void run(){
		// We need to setup a listening server
		try {
			server = new ServerSocket(port);

			try {
				// Continously check for new connections
				while (true) {
					final Socket sock = server.accept();
					new Thread() {
						public void run() {
							ListeningServer.attachRemotePeer(peeridMap, sock, localPeer, localPeerId);
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
