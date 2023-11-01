package dev.godjango.tfprotocol;

import java.io.IOException;
import java.io.InputStream;

import dev.godjango.tfprotocol.callbacks.ISuperCallback;
import dev.godjango.tfprotocol.connection.Client;
import dev.godjango.tfprotocol.connection.Easyreum;
import dev.godjango.tfprotocol.keepalives.UDPKeepAlive;
import dev.godjango.tfprotocol.misc.StatusInfo;
import dev.godjango.tfprotocol.misc.StatusServer;
import dev.godjango.tfprotocol.misc.TCPTimeouts;
import dev.godjango.tfprotocol.security.Cryptography;

/**
 * The top tfprotocol class, all other classes of tfprotocol extends from this one, callbacks extends from
 * ISuperCallback, all classes which extends from this one must have in order to be used another class who extends from
 * ISuperCallback, if doesn't then the user won't be able to instantiate the extended subclass...
 * @param <ICallbackImplementation> The implementation of the callback to be used
 * @see ISuperCallback
 */
public class TfprotocolSuper<ICallbackImplementation extends ISuperCallback>{
    protected Easyreum easyreum;
    private TCPTimeouts tcpTimeOut;
    private String publicKey;
    private String hash;
    private String protocol;
    private int len;
    private ICallbackImplementation protoHandler;
    private byte[] sessionKeyNotification = null;
    private long sleepTimeSeconds = 3000;
    protected int lenChannel = 512*1024;
    /**
     * Constructor of tfprotocol
     * @param ipServer The ip address where the protocol server is running
     * @param portServer The tcp port where de protocol server is listening
     * @param publicKey The previous shared rsa public key for the initial encryption of the communication
     * @param hash The hash to be used by the server in order to test the integrity of the communication
     * @param len The length desired for the session key which will be returned by the server once the initial payload
     *            is send to the server.
     * @param protocol The desired version of the protocol..
     * @param protoHandler The instance of the callback handler which must extends from ISuperCallback
     * @see ISuperCallback
     * */
    public TfprotocolSuper(String ipServer, int portServer,
                           String publicKey, String hash, int len, String protocol,
                           ICallbackImplementation protoHandler) {
        this.publicKey = publicKey;
        this.hash = hash;
        this.protocol = protocol;
        this.protoHandler = protoHandler;
        this.len = len;
        this.easyreum = new Easyreum(new Client(ipServer, portServer));
        tcpTimeOut = TCPTimeouts.getInstance(protoHandler);
    }
    /**
     * Constructor of tfprotocol
     * @param ipServer The ip address where the protocol server is running
     * @param portServer The tcp port where de protocol server is listening
     * @param publicKey The previous shared rsa public key for the initial encryption of the communication
     * @param hash The hash to be used by the server in order to test the integrity of the communication
     * @param len The length desired for the session key which will be returned by the server once the initial payload
     *            is send to the server.
     * @param protocol The desired version of the protocol..
     * @param protoHandler The instance of the callback handler which must extends from ISuperCallback
     * @see ISuperCallback
     * */
    public TfprotocolSuper(String ipServer, int portServer,
                           InputStream publicKey, String hash, int len, String protocol,
                           ICallbackImplementation protoHandler) {
        byte[] pk = new byte[1000];
        int read;
        try {
            read = publicKey.read(pk);
        } catch (IOException e) {
            throw new TFExceptions(TFExceptions.ErrorCodes.UNHANDLED_EXCEPTION.ordinal(),
                    "Cannot read successfully the file");
        }
        this.publicKey = new String(pk, 0, read);
        this.hash = hash;
        this.protocol = protocol;
        this.protoHandler = protoHandler;
        this.len = len;
        this.easyreum = new Easyreum(new Client(ipServer, portServer));
        tcpTimeOut = TCPTimeouts.getInstance(protoHandler);
    }
    /**
     * Constructor of tfprotocol
     * @param proxy The proxy address for connect through a proxy, proxy auth isn't supported yet..
     *              Ex( http://proxy.com:8080)
     * @param ipServer The ip address where the protocol server is running
     * @param portServer The tcp port where de protocol server is listening
     * @param publicKey The previous shared rsa public key for the initial encryption of the communication
     * @param hash The hash to be used by the server in order to test the integrity of the communication
     * @param len The length desired for the session key which will be returned by the server once the initial payload
     *            is send to the server.
     * @param protocol The desired version of the protocol..
     * @param protoHandler The instance of the callback handler which must extends from ISuperCallback
     * @see ISuperCallback
     * @see dev.godjango.tfprotocol.connection.Easyproxy
     * */
    public TfprotocolSuper(String proxy, String ipServer, int portServer,
                           String publicKey, String hash, int len, String protocol,
                           ICallbackImplementation protoHandler) {
        this.publicKey = publicKey;
        this.hash = hash;
        this.protocol = protocol;
        this.protoHandler = protoHandler;
        this.len = len;
        tcpTimeOut = TCPTimeouts.getInstance(protoHandler);

        this.easyreum = new Easyreum(new Client(proxy, ipServer, portServer));
    }
    protected TfprotocolSuper(ICallbackImplementation callback){
        this.setProtoHandler(callback);
    }
    protected TfprotocolSuper(){
    }
    /**
     * Return the sleep time in seconds...
     * */
    public long getSleepTimeSeconds() {
        return sleepTimeSeconds;
    }
    /**
     * Set the sleep time in seconds
     * @param sleepTimeSeconds The time in seconds
     * */
    public void setSleepTimeSeconds(long sleepTimeSeconds) {
        this.sleepTimeSeconds = sleepTimeSeconds * 1000;
    }
    /**
     * It is tfprotocol connected? Well this functions answer that...
     * */
    public boolean isConnect() {
        return this.easyreum.getClient().getSocket().isConnected();
    }
    /**
     * If for some reason you want to force the boolean variable to true you can set it here, but still will not be able
     * to communicate with backend this is used just for backward compatibility...
     * @param connect The new value
     * @deprecated This is no longer useful and will be deleted in january 2022
     * */
    @Deprecated
    public void setConnect(boolean connect) {
        return;
    }

    public String getPublicKey(){
        return this.publicKey;
    }

    public String getProtocolVersion(){
        return this.protocol;
    }

    /**
     * Get the callback for this protocol instance...
     * */
    public ICallbackImplementation getProtoHandler() {
        return protoHandler;
    }
    /**Set the callback handler
     * @param protoHandler Callback Handler*/
    public void setProtoHandler(ICallbackImplementation protoHandler) {
        this.protoHandler = protoHandler;
    }
    /**
     * Connect to the server with no keep-alive mechanism enabled...
     * */
    public final Client connect() {
        StatusInfo status;
        int cont = 0;
        do {
            cont++;
            status = this.easyreum.getClient().startConnection(tcpTimeOut.getConnectTimeout(), tcpTimeOut.getDnsResolutionTimeout());
            if (status.getStatus().equals(StatusServer.OK)) {
                break;
            }
        } while (cont < tcpTimeOut.getConnectRetry());
        if (!status.getStatus().equals(StatusServer.OK)) {
            protoHandler.responseServerCallback(status);
        } else {
            //send protocol to use
            this.easyreum.translate(this.protocol).getBuilder().buildStatusInfo();
            if (this.easyreum.getBuilder().isStatusInfoOk()) {
                //send public key and retrieve session key
                byte[] sessionKey = Cryptography.getRandomBytes(len);
                byte[] sessionKeyEncrypted = Cryptography.RSA.encrypt(sessionKey, publicKey);
                this.easyreum.translate(sessionKeyEncrypted).getBuilder().buildStatusInfo();
                if (this.easyreum.getBuilder().isStatusInfoOk()) {
                    this.easyreum.setSessionKey(sessionKey);
                }
                if (this.easyreum.getSessionKey() != null) {
                    //send Hash
                    this.easyreum.translate(hash.getBytes()).getBuilder().buildStatusInfo();
                    if (this.easyreum.getBuilder().isStatusInfoOk()) {
                        this.protoHandler.instanceTfProtocol(this);
                    } else
                        protoHandler.statusServer(this.easyreum.getBuilder().getStatusInfo());
                }
            } else {
                protoHandler.statusServer(this.easyreum.getBuilder().getStatusInfo());
            }
        }
        return this.easyreum.getClient();
    }
    /**
     * Connect to the server with a keep-alive mechanism enabled...
     * @param keep_alive Must be one of the UDPKeepAlive.TYPE values any other will not work...
     * @param idle The time that the keep-alive mechanism will be quiet before sending the first UDP package...
     * @param timeout The maximum time the proves can last before assuming this probe as failed.
     * @param max_tries The maximum amount of tries before dropping abruptly the connection.
     * */
    public final Client connect(UDPKeepAlive.TYPE keep_alive, int idle, int timeout, int max_tries) {
        int cont = 0;
        do {
            this.easyreum.getBuilder().setStatusInfo(this.easyreum.getClient()
                    .startConnection(tcpTimeOut.getConnectTimeout(),
                            tcpTimeOut.getDnsResolutionTimeout()));
            if (this.easyreum.getBuilder().isStatusInfoOk()) break;
        } while (++cont < tcpTimeOut.getConnectRetry());
        if (!this.easyreum.getBuilder().isStatusInfoOk()) {
            protoHandler.responseServerCallback(this.easyreum.getBuilder().getStatusInfo());
        }else {
            //System.out.println("Sending protocol to server...");
            //send protocol to use
            this.easyreum.translate(this.protocol).getBuilder().buildStatusInfo();
            //System.out.println("Asking did the server recognize the protocol?");
            if (easyreum.getBuilder().isStatusInfoOk()) {
                //System.out.println("The server does recognize your protocol version requested");
                //send public key and retrieve session key
                byte[] sessionKey = Cryptography.getRandomBytes(len);
                byte[] sessionKeyEncrypted  = Cryptography.RSA.encrypt(sessionKey, publicKey);
                this.easyreum.translate(sessionKeyEncrypted).getBuilder().buildStatusInfo();

                if (easyreum.getBuilder().isStatusInfoOk()) {
                    this.easyreum.setSessionKey(sessionKey);
                    this.easyreum.translate(hash.getBytes()).getBuilder().buildStatusInfo();

                    if (easyreum.getBuilder().isStatusInfoOk()) {
                        this.protoHandler.instanceTfProtocol(this);
                    } else
                        protoHandler.statusServer(easyreum.getBuilder().getStatusInfo());
                }
            }
        }
        if (!easyreum.getBuilder().isStatusInfoOk()) {
            protoHandler.statusServer(easyreum.getBuilder().getStatusInfo());
            try {
                this.easyreum.getClient().getSocket().close();
                throw new TFExceptions(StatusServer.DISCONNECTED,
                        TFExceptions.ErrorCodes.ON_WRITE_OR_RECEIVE_TO_SOCKET,
                        "Cannot successfully connect to the server");
            } catch (IOException e) {
                throw new TFExceptions(e);
            }
        } else {
            UDPKeepAlive udp_ka;
            udp_ka = new UDPKeepAlive(keep_alive,
                    this.easyreum, idle, timeout, max_tries);
            udp_ka.setDaemon(true);
            udp_ka.start();
        }
        return this.easyreum.getClient();
    }
    /**
     * Disconnect the protocol
     * */
    public void disconnect() {
        this.easyreum.getClient().stopConnection();
    }
    /**
     * Get the length of the channel...
     * */
    public int getLenChannel() {
        return lenChannel;
    }
    /**
     * Get the session key notification...
     * */
    public byte[] getSessionKeyNotification() {
        return sessionKeyNotification;
    }
    /**
     * Set the session key notification
     * @param sessionKeyNotification The session key notification
     * */
    public void setSessionKeyNotification(byte[] sessionKeyNotification) {
        this.sessionKeyNotification = sessionKeyNotification;
    }
    /**
     * Get the tcp timeout used for establish the connection..
     * */
    public TCPTimeouts getTcpTimeOut() {
        return tcpTimeOut;
    }
    /** Set the tcp timeout
     * @param tcpTimeOut The tcp timeout
     * */
    public void setTcpTimeOut(TCPTimeouts tcpTimeOut) {
        this.tcpTimeOut = tcpTimeOut;
    }

    private void setLenChannel(int lenChannel){
        this.lenChannel = lenChannel;
    }
    public Easyreum getConHandler(){
        return this.easyreum;
    }
}
