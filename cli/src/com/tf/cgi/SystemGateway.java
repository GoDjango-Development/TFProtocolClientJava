package com.tf.cgi;

import com.tf.iface.CLI;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.Socket;

public class SystemGateway {
    Socket socket;
    int port = 65535;
    public int getPid() {
        String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
        System.out.println(pid);
        return Integer.parseInt(pid);
    }
    public SystemGateway(){
        while (port>0)
            try {

                this.socket = new Socket(InetAddress.getLoopbackAddress(), port--);
                OutputStream stream = new FileOutputStream(CLI.path.concat(".pid"));
                stream.write("AUTO GENERATED FILE: DO NOT MODIFY THIS DIRECTLY PLEASE KILL THIS PROCESS INSTEAD".getBytes());
                stream.write(String.valueOf(port).getBytes());
                stream.write(String.valueOf(getPid()).getBytes());
                break;
            } catch (IOException ignored) {}
    }

    public void start(){

    }
}
