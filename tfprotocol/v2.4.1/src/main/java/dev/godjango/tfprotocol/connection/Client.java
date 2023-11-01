package dev.godjango.tfprotocol.connection;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.concurrent.*;

import dev.godjango.tfprotocol.TFExceptions;
import dev.godjango.tfprotocol.misc.StatusInfo;

public class Client {
    OutputStream dataOutputStream = null;
    InputStream inputStream = null;
    private Socket socket;
    private DatagramSocket udpSocket;
    private final String address;
    private final int port;
    private boolean isConnect = false;
    private int bufSize = 512 * 1024;
    //Thread to resolve host name
    ExecutorService executor = Executors.newFixedThreadPool(1, new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread t = Executors.defaultThreadFactory().newThread(r);
            t.setDaemon(true);
            return t;
        }
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
     * Instantiate the datagram socket.
     * @throws TFExceptions if an IOException occurred while creating the socket.
     * */
    public void initUDPSocket(){
        try {
            this.udpSocket = new DatagramSocket();
        } catch (SocketException e) {
            throw new TFExceptions(e);
        }
    }

    /**
     * Returns the client datagram socket ready to be used in UDP communications if hadn't been created
     * then it creates it
     * @return DatagramSocket The datagram socket which is centralized in client class
     * @throws TFExceptions if an IOException is thrown meanwhile is creating the socket (Only if wasn't
     * created previously)
     * */
    public DatagramSocket getUDPSocket(){
        if (this.udpSocket == null) this.initUDPSocket();
        return this.udpSocket;
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
        super.finalize(); // TODO This needs further research
        this.stopConnection();
    }
}