package com.nerox.client.modules;

import com.nerox.client.TfprotocolSuper;
import com.nerox.client.callbacks.IXSGatewayCallback;
import com.nerox.client.misc.StatusInfo;
import com.nerox.client.misc.StatusServer;

import java.util.ArrayList;

public class XSGateway extends TfprotocolSuper<IXSGatewayCallback> {
    public XSGateway(String proxy, String ipServer, int portServer,
                           String publicKey, String hash, int len, String protocol,
                           IXSGatewayCallback protoHandler) {
        super(proxy, ipServer, portServer, publicKey, hash, len, protocol, protoHandler);
    }
    public XSGateway(String ipServer, int portServer,
                     String publicKey, String hash, int len, String protocol,
                     IXSGatewayCallback protoHandler) {
        super(ipServer, portServer, publicKey, hash, len, protocol, protoHandler);
    }
    public XSGateway(IXSGatewayCallback callback){
        super(callback);
    }
    public XSGateway(){
        super();
    }
    private ArrayList<StatusInfo> onThreadHolder = new ArrayList<>();
    private final Object mutex = new Object();
    private boolean isActive = false;
    public StatusInfo getOnThreadHolder(){
        return (onThreadHolder.size() > 0) ? onThreadHolder.remove(0):
                new StatusInfo(StatusServer.FAILED, 0,"Not messages available from server");
    }

    public void startGateway(){
        super.getProtoHandler().startGateway(this.easyreum
                .getBuilder().build("XS_GATEWAY").translate()
                .getBuilder().buildStatusInfo());
        this.isActive = true;
    }
    public void createIdentity(String id){
        super.getProtoHandler().createIdentity(this.easyreum
                .getBuilder().build(id).translate()
                .getBuilder().buildStatusInfo());
    }
    public void sendMessage(String destinyId, String message){
        synchronized (this.mutex){
            this.easyreum.getBuilder().build(destinyId).send();
            this.easyreum.getBuilder().build(message).send();
        }
    }
    public void killReceivingOnInterval(){
        synchronized (this.mutex){
            this.isActive = false;
            this.mutex.notifyAll();
        }
    }
    public void receiveMessageInInterval(int interval){
        new Thread(() -> {
            synchronized (this.mutex){
                while (isActive){
                    onThreadHolder.add(easyreum.receive().getBuilder().buildStatusInfo());
                    try {
                        mutex.wait(interval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    public byte[] receiveMessage(){
        return this.easyreum.receive().getPayloadReceived();
    }
    public void endGateway(){
        this.killReceivingOnInterval();
        this.easyreum.getBuilder().build(-1).sendJust();
    }
}
