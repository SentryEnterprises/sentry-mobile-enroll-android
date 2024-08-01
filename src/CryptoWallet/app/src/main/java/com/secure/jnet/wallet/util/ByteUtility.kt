package com.secure.jnet.wallet.util;

public abstract class ByteUtility {

    public static final String byteArrayToHexString(byte[] data) {
        if (data == null) {
            return "";
        } else {
            StringBuffer hexString = new StringBuffer(data.length * 2);

            for(int i = 0; i < data.length; ++i) {
                int currentByte = data[i] & 255;
                if (currentByte < 16) {
                    hexString.append('0');
                }

                hexString.append(Integer.toHexString(currentByte));
            }

            return hexString.toString().toUpperCase();
        }
    }
}

