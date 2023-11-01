package dev.godjango.tfprotocol.modules;

import java.lang.reflect.Method;

import dev.godjango.tfprotocol.*;
import dev.godjango.tfprotocol.callbacks.ISuperCallback;
import dev.godjango.tfprotocol.callbacks.IXSImeCallback;
import dev.godjango.tfprotocol.misc.StatusInfo;
import dev.godjango.tfprotocol.misc.StatusServer;
import dev.godjango.tfprotocol.multithread.Easythread;

public class XSIme extends TfprotocolSuper<IXSImeCallback>{
    private final Object mutex = new Object();
    public XSIme(String ipServer, int portServer, String publicKey, String hash, int len, String protocol, IXSImeCallback protoHandler) {
        super(ipServer, portServer, publicKey, hash, len, protocol, protoHandler);
    }
    public XSIme(String proxy, String ipServer, int portServer, String publicKey, String hash, int len, String protocol, IXSImeCallback protoHandler) {
        super(proxy, ipServer, portServer, publicKey, hash, len, protocol, protoHandler);
    }

    public XSIme(TfprotocolSuper tfprotocol, IXSImeCallback xsImeCallbackTest) throws TFExceptions {
        if (tfprotocol == null ||
        tfprotocol.getConHandler().getClient() == null ||
                tfprotocol.getConHandler().getClient().isConnect())
            throw new TFExceptions(-1,"Invalid protocol or disconnected protocol... please call " +
                    "to connect function first...");
        this.setProtoHandler(xsImeCallbackTest);
        this.easyreum = tfprotocol.getConHandler();
    }
    public synchronized void StartCommand() {
        try {
            this.getProtoHandler().startCallback(this.easyreum
                    .getBuilder().build("XSIME_START").translate()
                    .getBuilder().buildStatusInfo());
            if (this.easyreum.getBuilder().isStatusInfoOk()) {
                this.easyreum.setBuffer(16*1024);
                Easythread easythread = new Easythread(this.mutex, this.easyreum.getClass()
                        .getDeclaredMethod("receiveOptimized", Method.class, ISuperCallback.class));
                easythread.setup(this.easyreum,
                        this.getProtoHandler().getClass().getMethod("listenCallback",
                                StatusInfo.class)
                        ,this.getProtoHandler());
                easythread.setDaemon(false);
                easythread.start();
            }
        } catch (NoSuchMethodException e) {
            throw new TFExceptions(e);
        }
    }
    public synchronized void SetupCommand(boolean unique_user, boolean auto_delete, long timestamp){
        try{
            int sz = (unique_user?1:0) | (auto_delete?2:0);
            this.easyreum.getBuilder().build((byte)4).getBuilder().build(sz).sendJust();
            this.easyreum.getBuilder().build(timestamp).sendJust();
        } catch (TFExceptions exception) {
            exception.printStackTrace();
        }
    }
    public synchronized void SetPathCommand(String path){
        try {
            this.easyreum.getBuilder().build((byte)1)
                    .getBuilder().build(path.getBytes().length).sendJust();
            this.easyreum.getBuilder().build(path).sendJust();
        } catch (TFExceptions exception) {
            exception.printStackTrace();
        }
    }
    public synchronized void SetUsernamePathCommand(String username_path) {
        if (username_path.getBytes().length > 128) throw new TFExceptions(
                new StatusInfo(StatusServer.PAYLOAD_TOO_BIG,
                128, "Username path shouldnt be greater than 128 bytes, current size is "+
                username_path.getBytes().length));
        this.easyreum.getBuilder().build((byte)2)
                .getBuilder().build(username_path.getBytes().length).sendJust();
        this.easyreum.getBuilder().build(username_path).sendJust();
    }
    public synchronized void SendMessageCommand(String message, String dest, String...others) {
        StringBuilder payload = new StringBuilder();
        payload.append(dest.trim()).append(":");
        for (String user: others) payload.append(user).append(":");
        payload.append(" ").append(message);
        this.easyreum.getBuilder().build((byte)5).getBuilder().build(payload.length()).sendJust();
        this.easyreum.getBuilder().build(payload.toString()).sendJust();
    }
    public synchronized void SleepIntervalCommand(long interval){
        interval = Math.max(interval, 0);
        this.easyreum.getBuilder().build((byte)7).getBuilder().build(0).sendJust();
        this.easyreum.getBuilder().build(interval).sendJust();
    }
    public synchronized void CloseCommand(){
        this.easyreum.getBuilder().build((byte)0).getBuilder().build(new byte[4]).sendJust();
        synchronized (this.mutex){
            try {
                this.mutex.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
