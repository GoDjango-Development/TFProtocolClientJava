package com.nerox.client.keepalives;

import com.nerox.client.TFExceptions;
import com.nerox.client.TfprotocolWrapper;
import com.nerox.client.callbacks.IUDPKeepCallback;
import com.nerox.client.connection.Easyreum;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class UDPKeepAlive extends Thread {
    private final Easyreum easyreum;

    public enum ERR_CODES{
    }
    public enum TYPE{
        UDP_HOSTCHECK, UDP_PROCHECK
    }
    private byte counter = 0;
    private String prockey = "";
    private final int idle;
    private final int max_tries;
    private static boolean is_active = true;

    private static TYPE current_type;
    private static DatagramSocket socket;
    private static IUDPKeepCallback callback;
    public UDPKeepAlive(TYPE keepalive_type,
                        Easyreum easyreum, int idle, int timeout, int max_tries) {
        super();
        try {
            this.easyreum = easyreum;
            UDPKeepAlive.current_type = keepalive_type;
            UDPKeepAlive.socket = new DatagramSocket();
            int timeout1 = Math.max(timeout, 1);
            this.idle = Math.max(idle*1000, 1);
            this.max_tries = Math.max(max_tries,1);
            socket.setSoTimeout(timeout1 *1000);
            boolean success = false;
            if (keepalive_type == TYPE.UDP_PROCHECK) {
                this.easyreum.getSocket().setSoTimeout(timeout * 1000);
                TfprotocolWrapper protocol = new TfprotocolWrapper(this.easyreum);
                for (int i =0;i < max_tries; i++) {
                    this.prockey = protocol.prockeyCommand();
                    is_active = protocol.keepAliveCommand(true, 1, idle, max_tries);
                    if (this.prockey.equals("") || !is_active)
                        continue;
                    this.easyreum.getSocket().setSoTimeout(0);
                    success = true;
                    break;
                }
                if (!success)
                    this.StopConnection();
            }
        }catch (IOException e){
            throw new TFExceptions(e);
        }
    }

    private synchronized boolean StopConnection() {
        socket.close();
        this.easyreum.getClient().stopConnection();
        is_active = false;
        if (callback != null) callback.ConnectionClosed();
        return this.easyreum.getClient().getSocket().isClosed();
    }

    private void UDP_HOSTCHECK() {
        try{

            byte[] send_bytes = new byte[]{0};
            DatagramPacket send_packet = new DatagramPacket(send_bytes,
                    send_bytes.length,
                    this.easyreum.getClient().getSocket().getInetAddress(),
                    this.easyreum.getClient().getSocket().getPort());
            socket.send(send_packet);
            byte[] recv_bytes = new byte[Byte.BYTES];
            DatagramPacket recv_packet = new DatagramPacket(recv_bytes,
                    recv_bytes.length,
                    this.easyreum.getClient().getSocket().getInetAddress(),
                    this.easyreum.getClient().getSocket().getPort());
            socket.receive(recv_packet);
            if (recv_bytes[0]==1) {
                this.counter = 0;
            }
        }catch (IOException ignored){}
        finally {
            this.counter++;
            if (this.counter == this.max_tries)
            {
                this.StopConnection();
                return;
            }
            try {
                Thread.sleep(this.idle);
            } catch (InterruptedException ignored) {}
        }
    }
    private void UDP_PROCHECK(){
        try {
            ByteBuffer payload = ByteBuffer.allocate(this.prockey.getBytes().length+Byte.BYTES).
                    order(ByteOrder.BIG_ENDIAN).put((byte) 1).put(this.prockey.getBytes());
            DatagramPacket send_packet = new DatagramPacket(payload.array(),
                    payload.capacity(),
                    this.easyreum.getClient().getSocket().getInetAddress(),
                    this.easyreum.getClient().getSocket().getPort());
            socket.send(send_packet);

            byte[] bytes = new byte[Byte.BYTES];
            DatagramPacket recv_packet = new DatagramPacket(bytes,
                    bytes.length,
                    this.easyreum.getClient().getSocket().getInetAddress(),
                    this.easyreum.getClient().getSocket().getPort());
            socket.receive(recv_packet);
            if (bytes[0]==0) {
                this.StopConnection();
                return;
            }
            if (bytes[0]==1){
                this.counter = 0;
            }
        }catch (IOException ignored){}
        finally {
            this.counter++;
            if (this.counter == this.max_tries)
            {
                this.StopConnection();
                return;
            }
            try {
                Thread.sleep(this.idle);
            } catch (InterruptedException ignored) {}
        }
    }

    /*
    private void UDP_SOCKCHECK() {
    }*/
    @Override
    public void run(){
        super.run();
        while (is_active && this.easyreum.getClient().isConnect()){
            switch (current_type){
                case UDP_PROCHECK:
                    this.UDP_PROCHECK();
                    break;
                case UDP_HOSTCHECK:
                    this.UDP_HOSTCHECK();
                    break;
                default:
                    System.err.println("UNHANDLED EXCEPTION");
                    break;
            }
        }
    }
    public static IUDPKeepCallback GetCallback(){
        return callback;
    }
    public static void SetCallback(IUDPKeepCallback callback){
        UDPKeepAlive.callback = callback;
    }

    @Override
    public void interrupt() {
        this.StopConnection();
        super.interrupt();
    }

    @Override
    protected void finalize() throws Throwable {
        this.StopConnection();
        super.finalize();
    }
}
