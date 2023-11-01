package com.nerox.old.connection;

import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.util.HashMap;

public class Easyproxy {
    public static HashMap<Proxy.Type, InetSocketAddress> parse_address(String address){
        HashMap<Proxy.Type, InetSocketAddress> res = new HashMap<>();
        String proto;
        String user = "";
        String pass = "";
        String addr;
        int port;
        proto = address.split("://")[0].trim().toUpperCase();
        address = address.replaceFirst(proto.toLowerCase()+"://","");
        if (address.contains("@")) {
            user = address.split("@")[0].split(":")[0];
            pass = address.split("@")[0].split(":")[1];
            address = address.replaceFirst(user+":"+pass+"@","");
            Authenticator.setDefault(
                    new Auth(user, pass)
            );

            System.setProperty("http.proxyUser", user);
            System.setProperty("http.proxyPassword", pass);
            //System.setProperty("http.auth.preference","basic");
            System.setProperty("jdk.http.auth.tunneling.disabledSchemes","");
        }
        addr = address.split(":")[0];
        port = Integer.parseInt(address.split(":")[1]);
        res.put(Proxy.Type.valueOf(proto),
                InetSocketAddress.createUnresolved(addr, port));
        return res;
    }
}
class Auth extends Authenticator{
    final String user;
    final char[] pass;
    Auth(String user, String pass){
        this.user = user;
        this.pass = pass.toCharArray();
    }
    @Override
    public PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(this.user, this.pass);
    }
}
