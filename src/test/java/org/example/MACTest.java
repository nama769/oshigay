package org.example;

import static org.junit.Assert.assertTrue;

import java.net.InetAddress;
import java.net.NetworkInterface;

public class MACTest{
    public static void main(String[] args) throws Exception{
        byte[] mac = NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getHardwareAddress();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mac.length; i++) {
            sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
        }
        String MACaddr = sb.toString();
        System.out.println(MACaddr);
        System.out.println(sb.length());
    }
//    public void handle(byte[] data)
//    public static void main() throws Exception {
//        byte[] mac = NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getHardwareAddress();
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < mac.length; i++) {
//            sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
//        }
//        System.out.println(sb.toString());
//    }

}