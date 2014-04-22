package com.networking.project;

import java.nio.ByteBuffer;

/**
 * Created by marco on 3/16/14.
 */
public class Util {

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String byteToHex(byte b) {
        char[] hexChars = new char[2];
        int v = b & 0xFF;
        hexChars[0] = hexArray[v >>> 4];
        hexChars[1] = hexArray[v & 0x0F];
        return new String(hexChars);
    }
    
    public static int byteToInt(byte[] bytes) {
    	int byteInt = 0;
    	int byteLength = bytes.length;
    	
    	for (int i = 0; i < 4; i++) {
    		byteInt |= (bytes[i] & 0xFF) << ((byteLength - i - 1) * 3);
    	}
    	return byteInt;

    }
    
    public static byte[] intToByte(int number) {
    	ByteBuffer b = ByteBuffer.allocate(4);
    	b.putInt(number);
    	return b.array();
    }
}
