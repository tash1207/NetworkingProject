package com.networking.project;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


public class RemotePeerConnection implements Runnable{
	private RemotePeer remotePeer;
	private String hostname;
	private int port;
	
	private OutputStream out;
	private InputStream in;
	
	private Socket sock;
	

	public RemotePeerConnection(RemotePeer remotePeer, String hostname, int port){
		this.remotePeer = remotePeer;
		this.hostname = hostname;
		this.port = port;
	}
	
	public void run(){
		try {
           sock = new Socket(hostname, port);
           remotePeer.onConnect();
           
           in = sock.getInputStream();
           out = sock.getOutputStream();
           
           remotePeer.updateOutputStream(out);
           remotePeer.updateInputStream(in);
		} catch (Exception e) {
			System.out.println("SOMETHING REAL BAD HAPPENED OPENING THE SOCKET" + e);
		}
	}
}
