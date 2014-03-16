package com.networking.project;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;


public class RemotePeerConnection implements Runnable{
	private RemotePeer remotePeer;
	private String hostname;
	private int port;
	
	private OutputStream out;
	private InputStream in;
	
	private Socket sock;
	

	public RemotePeerConnection(int peerid, String hostname, int port){
		this.remotePeer = remotePeer;
		this.hostname = hostname;
		this.port = port;

        try {
            sock = new Socket(hostname, port);
            RemotePeerConnection.checkHandshake(sock, peerid);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public RemotePeerConnection(int peerid, Socket sock){
        RemotePeerConnection.checkHandshake(sock, peerid);
    }

    public static void checkHandshake(Socket sock, int peerid){
        try {
            InputStream in = sock.getInputStream();
            OutputStream out = sock.getOutputStream();

            if ( sendHandShake(out, in, peerid) ) {
                //remotePeer.onConnect();
                System.out.println("Sucessful connection! from "+ peerid);
            }else{
                System.err.println("Error in establishing handshake");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void sendMessage(byte[] message){
        try {
            out.write(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void parseInputStreamAndAddToQueue(InputStream is, RemotePeer peer){
        while(true){
            byte[] msgLength = new byte[4];

            try {
                is.read(msgLength);

                //parse the msgLength byte array to an int.
                int msgLengthInt =  msgLength[3] & 0xFF |
                                    (msgLength[2] & 0xFF) << 8 |
                                    (msgLength[1] & 0xFF) << 16 |
                                    (msgLength[0] & 0xFF) << 24;

                byte[] restMsg = new byte[msgLengthInt];

                is.read(restMsg);

                ByteBuffer wholeMessage = ByteBuffer.allocate(4+msgLengthInt);

                wholeMessage.put(msgLength);
                wholeMessage.put(restMsg);

                peer.appendReceivedMessageToQueue(wholeMessage.array());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean sendHandShake(OutputStream os, InputStream is, int peerid){
        byte[] message = Message.createHandshake(peerid);
        try {
            os.write(message);

            // We wait for a handshake

            byte[] handshakeReply = new byte[32];
            is.read(handshakeReply);
            if (Message.checkHandshake(handshakeReply)){
                // Success!
                return true;
            }else{
                return false;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
	
	public void run(){
		try {
            RemotePeerConnection.parseInputStreamAndAddToQueue(in, remotePeer);
		} catch (Exception e) {
			System.out.println("SOMETHING REAL BAD HAPPENED OPENING THE SOCKET" + e);
		}
	}
}
