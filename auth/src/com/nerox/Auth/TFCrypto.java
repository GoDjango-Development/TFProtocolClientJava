package com.nerox.Auth;

import com.nerox.Auth.Errors.TFAuthErrors;

import java.math.BigInteger;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Formatter;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * Class dedicated to Cryptographic tasks
 * */
public class TFCrypto {
    // Class Variables
    private boolean InterfaceDetected = false;
    private static String Mac = "";
    // Statics
    protected String key;

    /**
     * A simple constructor we do the magic...
     * */
    public TFCrypto() throws TFAuthErrors {
        getMACAddress();
    }

    // Statics
    public static String hash(String primaryKey){
        String hashed;
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        assert digest != null;
        digest.reset();
        digest.update(primaryKey.getBytes(StandardCharsets.UTF_8));
        hashed = String.format("%x",new BigInteger(1,digest.digest()));
        return hashed;
    }
    /**
     * An util tool used to convert a byte array into a string...
     * @param arr The byte array to be parsed...
     * */
    public static String toString(byte[] arr){
        if (arr == null || arr.length == 0) return "";
        byte[] b2 = new byte[arr.length+1];
        b2[0]=1;
        System.arraycopy(arr,0,b2,1,arr.length);
        return new BigInteger(b2).toString(36);
    }
    public static String toString(byte[] arr, String format){
        if (arr == null || arr.length == 0) return "";
        Formatter formatter = new Formatter();
        StringBuilder result = new StringBuilder();
        for (byte a : arr){
            if (format.equals("mac")){
                formatter.format("%x",a);
                result = new StringBuilder(formatter.toString());
            }else if(format.equals("read")) {
                result.append((char) a);
            }

        }
        return result.toString();
    }
    /**
     * An util tool used to convert a string into a byte array...
     * @param str The string to be parsed...
     * */
    public static byte[] toBytes(String str){
        byte[] b2 = new BigInteger(str,36).toByteArray();
        return Arrays.copyOfRange(b2,1,b2.length);
    }

    public static String getMAC(){
        return Mac;
    }


    // Non Statics
    protected void genKey() throws TFAuthErrors{
        String tfsign = "0xff";
        String hashed_mac_01 = hash(Mac);
        String hashed_mac_02 = hash(hashed_mac_01);
        String hashed_mac_03 = hash(hashed_mac_02);
        String checksum = hashed_mac_03.substring(0,4);

        Random ran = new Random();
        ran.setSeed(LocalTime.now().getNano());
        IntStream random = ran.ints(250,'0','z');
        StringBuilder randomPart = new StringBuilder();
        for (int rand : random.toArray()){
            if (Character.isLetterOrDigit(((char) rand))){
                randomPart.append(((char) rand));
            }
        }
        String randPart = hash(randomPart.toString());
        key = tfsign + checksum + randPart;
    }
    public String toString(){
        return key;
    }
    public byte[] encrypt(String message){
        String realKey = key.replaceFirst("0xff","");
        StringBuilder encrypted = new StringBuilder();
        int temp = 0;
        for (int te: message.toCharArray()){
            int temp1 = te ^ (int) realKey.charAt(temp);
            encrypted.append((char)temp1);
            temp = (realKey.length() > (temp + 1) )? temp + 1: 0;
        }
        return encrypted.toString().getBytes();
    }
    public String decrypt(byte[] encrypted_message){
        assert (key != null);
        String realKey = key.replaceFirst("0xff","");
        StringBuilder decrypted = new StringBuilder();
        int temp = 0;
        for (byte te: encrypted_message){
            int temp1 = te ^ (int) realKey.charAt(temp);
            decrypted.append((char) (temp1));
            temp = (realKey.length() > (temp + 1) )? temp + 1: 0;
        }
        return decrypted.toString();
    }

    // Private Functions Caution
    /**
     * Returns the MAC address of the device which is attempting to use the TFAuth model.
     * */
    private void getMACAddress() throws TFAuthErrors{
        Enumeration<NetworkInterface> n;
        try {
            n = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            throw new TFAuthErrors(TFAuthErrors.ErrorsKinds.NotNetworkInterfacesUp.ordinal());
        }
        while (n.hasMoreElements()){
            NetworkInterface nif =  n.nextElement();
            try {
                if (nif.isLoopback()) return;
                if (nif.getName().equals("wlan0")||
                        nif.getName().equals("eth0"))
                {
                    InterfaceDetected = true;
                    TFCrypto.Mac = toString(nif.getHardwareAddress(), "mac");
                }

            } catch (SocketException se) {
                se.printStackTrace();
            }
        }
        if (!InterfaceDetected){
            throw new TFAuthErrors();
        }
    }
}