package com.secure.jnet.wallet.util;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.nio.ByteBuffer;

public abstract class ByteUtility {
//    public static final byte BIT_8 = -128;
//    public static final byte BIT_7 = 64;
//    public static final byte BIT_6 = 32;
//    public static final byte BIT_5 = 16;
//    public static final byte BIT_4 = 8;
//    public static final byte BIT_3 = 4;
//    public static final byte BIT_2 = 2;
//    public static final byte BIT_1 = 1;
//    public static final byte LESS_THAN = -1;
//    public static final byte GREATER_THAN = 1;
//    public static final byte INVALID_VALUE = 2;
//    public static final byte IS_EQUAL = 0;

//    public ByteUtility() {
//    }
//
//    public static final boolean isHexString(String data) {
//        byte decision = 90;
//        if (data == null) {
//            decision = (byte)(decision ^ ~decision);
//            throw new RuntimeException("NULL is not a Hex String");
//        } else {
//            decision = (byte)(~decision & 255);
//            int dataLength = data.length();
//            if (dataLength == 0) {
//                return decision == -91;
//            } else if (dataLength % 2 != 0) {
//                decision = (byte)(decision ^ ~decision);
//                throw new RuntimeException("NULL is not a Hex String");
//            } else {
//                decision = (byte)(~decision & 255);
//                data = data.toLowerCase();
//
//                for(int i = 0; i < dataLength; ++i) {
//                    char c = data.charAt(i);
//                    if ((c < '0' || c > '9') && (c < 'a' || c > 'f')) {
//                        decision = (byte)(decision ^ ~decision);
//                        throw new RuntimeException("NULL is not a Hex String");
//                    }
//                }
//
//                return decision == 90;
//            }
//        }
//    }
//
//    public static final byte[] hexStringToByteArray(String hexString) {
//        boolean validHexString = false;
//        validHexString = isHexString(hexString);
//        if (validHexString) {
//            try {
//                byte[] data = new byte[hexString.length() / 2];
//
//                for(int i = 0; i < data.length; ++i) {
//                    data[i] = (byte)(Integer.parseInt(hexString.substring(i * 2, i * 2 + 2), 16) & 255);
//                }
//
//                return data;
//            } catch (NumberFormatException var4) {
//                return new byte[0];
//            }
//        } else {
//            throw new RuntimeException("byteArrayToShort Error");
//        }
//    }

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

//    public static final String asciiStringToHexString(String asciiString) {
//        StringBuffer hex = new StringBuffer();
//        if (asciiString != null) {
//            char[] chars = asciiString.toCharArray();
//
//            for(int i = 0; i < chars.length; ++i) {
//                hex.append(Integer.toHexString(chars[i]));
//            }
//        }
//
//        return hex.toString();
//    }
//
//    public static final byte[] asciiStringToByteArray(String asciiString) {
//        ByteArrayOutputStream baops = new ByteArrayOutputStream();
//        if (asciiString != null) {
//            char[] chars = asciiString.toCharArray();
//            char[] var3 = chars;
//            int var4 = chars.length;
//
//            for(int var5 = 0; var5 < var4; ++var5) {
//                char aCharacter = var3[var5];
//                baops.write(aCharacter);
//            }
//        }
//
//        return baops.toByteArray();
//    }

//    public static final String hexStringToAsciiString(String hexString) {
//        StringBuilder output = new StringBuilder();
//        if (hexString != null) {
//            for(int i = 0; i < hexString.length(); i += 2) {
//                String str = hexString.substring(i, i + 2);
//                output.append((char)Integer.parseInt(str, 16));
//            }
//        }
//
//        return output.toString();
//    }
//
//    public static final String byteArrayToAsciiString(byte[] asciiBytes) {
//        StringBuilder output = new StringBuilder("");
//        if (asciiBytes != null) {
//            byte[] var2 = asciiBytes;
//            int var3 = asciiBytes.length;
//
//            for(int var4 = 0; var4 < var3; ++var4) {
//                byte aByte = var2[var4];
//                output.append((char)aByte);
//            }
//        }
//
//        return output.toString();
//    }

//    public static final Boolean isBitSet(byte b, int bit) {
//        return (b & 1 << bit) != 0;
//    }
//
//    public static final byte setBit(byte b, int bit) {
//        return (byte)(b | 1 << bit);
//    }
//
//    public static final String numberToHexString(int data) {
//        String hexString = Integer.toHexString(data);
//        if (hexString.length() % 2 != 0) {
//            hexString = "0" + hexString;
//        }
//
//        return hexString.toUpperCase();
//    }

//    public static String longNumberToHexString(long data) {
//        String hexString = Long.toHexString(data);
//        if (hexString.length() % 2 != 0) {
//            hexString = "0" + hexString;
//        }
//
//        return hexString.toUpperCase();
//    }
//
//    public static final long byteArrayToLong(byte[] data) {
//        if (data == null) {
//            return 0L;
//        } else if (data.length > 0 && data.length <= 4) {
//            byte[] dataToBeConvertet = new byte[8];
//            System.arraycopy(data, 0, dataToBeConvertet, 8 - data.length, data.length);
//            ByteBuffer wrapped = ByteBuffer.wrap(dataToBeConvertet);
//            return wrapped.getLong();
//        } else {
//            throw new RuntimeException("byteArrayToShort Error");
//        }
//    }

//    public static final short byteArrayToShort(byte[] data) {
//        if (data == null) {
//            return 0;
//        } else if (data.length > 0 && data.length <= 2) {
//            byte[] dataToBeConvertet = new byte[2];
//            System.arraycopy(data, 0, dataToBeConvertet, 2 - data.length, data.length);
//            ByteBuffer wrapped = ByteBuffer.wrap(dataToBeConvertet);
//            return wrapped.getShort();
//        } else {
//             throw new RuntimeException("byteArrayToShort Error");
//        }
//    }
//
//    public static final <T extends Enum<T>> T[] stringArrayToEnums(String[] array, Class<T> type) {
//        T[] result = (T[]) Array.newInstance(type, array.length);
//
//        for(int i = 0; i < array.length; ++i) {
//            result[i] = Enum.valueOf(type, array[i]);
//        }
//
//        return result;
//    }

//    public static final BigInteger byteArrayToBigInteger(byte[] numberBytes) {
//        return new BigInteger("00" + byteArrayToHexString(numberBytes), 16);
//    }
//
//    public static final String byteToHexString(byte data) {
//        StringBuffer hexString = new StringBuffer(2);
//        int v = data & 255;
//        if (v < 16) {
//            hexString.append('0');
//        }
//
//        hexString.append(Integer.toHexString(v));
//        return hexString.toString().toUpperCase();
//    }
//
//    public static final byte[] intToByteArray(int number) {
//        return hexStringToByteArray(numberToHexString(number));
//    }
//
//    public static byte[] longToByteArray(long number) {
//        return hexStringToByteArray(longNumberToHexString(number));
//    }

}

