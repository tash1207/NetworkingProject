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

    private int remotePeerId;
	

	public RemotePeerConnection(String hostname, int port, int localPeerId){
		this.remotePeer = remotePeer;
		this.hostname = hostname;
		this.port = port;

        try {
            sock = new Socket(hostname, port);

            this.checkHandshake(sock, localPeerId);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public RemotePeerConnection(Socket sock, int localPeerId){
        this.checkHandshake(sock, localPeerId);
    }


    public int getRemotePeerId(){
        return remotePeerId;
    }

    public void checkHandshake(Socket sock, int localPeerId){
        // TODO: log handshake

        try {
            in = sock.getInputStream();
            out = sock.getOutputStream();

            this.remotePeerId = sendHandShake(out, in, localPeerId);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void sendMessage(byte[] message){
        try {
            System.out.println("Sending message!");
            out.write(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveRemotePeerRef(RemotePeer rp){
        this.remotePeer = rp;
    }

    public static void parseInputStreamAndAddToQueue(InputStream is, RemotePeer remotePeer){
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

                Thread.sleep(1000);
                is.read(restMsg);

                ByteBuffer wholeMessage = ByteBuffer.allocate(4+msgLengthInt);

                wholeMessage.put(msgLength);
                wholeMessage.put(restMsg);

                remotePeer.appendReceivedMessageToQueue(wholeMessage.array());
                System.out.println("Reading message");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static int sendHandShake(OutputStream os, InputStream is, int localPeerId){
        byte[] message = Message.createHandshake(localPeerId);
        int remotePeerId = -1;
        try {
            os.write(message);

            // We wait for a handshake

            byte[] handshakeReply = new byte[32];
            is.read(handshakeReply);
            remotePeerId = (Message.parseHandshake(handshakeReply));



        } catch (IOException e) {
            e.printStackTrace();
        }

        return remotePeerId;
    }
	
	public void run(){
        RemotePeerConnection.parseInputStreamAndAddToQueue(in, remotePeer);
	}
}
