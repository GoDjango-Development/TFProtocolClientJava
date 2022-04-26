package com.nerox.old.connection;

import com.nerox.old.TFExceptions;
import com.nerox.old.misc.StatusInfo;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Client {
    OutputStream dataOutputStream = null;
    InputStream inputStream = null;
    private Socket socket;
    private final String address;
    private final int port;
    private boolean isConnect = false;
    private int bufSize = 512 * 1024;
    //Thread to resolve host name
    ExecutorService executor = Executors.newFixedThreadPool(1, r -> {
        Thread t = Executors.defaultThreadFactory().newThread(r);
        t.setDaemon(true);
        return t;
    });
    Future<Boolean> result = null;

    public Client(String address, int port) {
        this.address = address;
        this.port = port;
        this.socket = new Socket();
    }

    // PROXY VERSION
    public Client(String prox_adrss, String address, int port) {
        this.address = address;
        this.port = port;
        HashMap<Proxy.Type, InetSocketAddress> parts = Easyproxy.parse_address(prox_adrss);
        this.socket = new Socket(new Proxy((Proxy.Type) parts.keySet().toArray()[0],
                (SocketAddress) parts.values().toArray()[0]));
    }

    /**
     * Metodo encargado de iniciar la conexion mediante socket con el servidor.
     *
     * @return StatusInfo : Respuesta del servidor
     * @see StatusInfo
     */
    public StatusInfo startConnection(int timeOut, int dnsResolution) {
        try {
            boolean isReachable;
            if (dnsResolution < 0)
                dnsResolution = 20 * 1000;
            if (dnsResolution > 0) {
                result = executor.submit(new ResolvedGetName(address, dnsResolution));
                isReachable = result.get(dnsResolution, TimeUnit.MILLISECONDS);
            } else {
                InetAddress step = InetAddress.getByName(address);
                isReachable = step.isReachable(dnsResolution);
            }
            if (isReachable) {
                socket.connect(new InetSocketAddress(address, port), timeOut);
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                inputStream = new DataInputStream(socket.getInputStream());
                if (socket == null)
                    return StatusInfo.build("DISCONNECTED 0 null pointer exception");
                isConnect = true;
                return StatusInfo.build("OK");
            } else
                return StatusInfo.build("DISCONNECTED 0 time out dns");
        } catch (Exception ex) {
            return StatusInfo.build("DISCONNECTED 0 time out dns ");
        } finally {
            executor.shutdown();
        }
    }

    public void stopConnection() {
        try{
            if (socket != null)
                socket.close();
            if (dataOutputStream != null)
                dataOutputStream.close();
            if (inputStream != null)
                inputStream.close();
            this.isConnect = false;
        }catch (IOException ex){
            throw new TFExceptions(ex);
        }
    }

    public boolean isConnect() {
        return isConnect;
    }

    public int getBufSize() {
        return bufSize;
    }

    public void setBufSize(int bufSize) {
        this.bufSize = bufSize;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        this.stopConnection();
    }
}