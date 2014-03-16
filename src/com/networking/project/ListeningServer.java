package com.networking.project;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ListeningServer implements Runnable{
	
	private ServerSocket server;
	private Peer localPeer; 
	
	
	public static void main(String[] args) {
		System.out.println("HEY world");
		(new Thread(new ListeningServer())).start();
		
	}
	
	public static void attachRemotePeer(Socket sock){
		System.out.println("Attached the remote peer");
		try {
				OutputStream out = sock.getOutputStream();
				InputStream in = sock.getInputStream();
			
				System.out.println("Data: " + in.read());
				out.write(97);
				sock.close();


		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run(){
		// We need to setup a listening server
		try {
			server = new ServerSocket(4001);

			try {
				// Continously check for new connections
				while (true) {
					final Socket sock = server.accept();
					new Thread() {
						public void run() {
							ListeningServer.attachRemotePeer(sock);
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
