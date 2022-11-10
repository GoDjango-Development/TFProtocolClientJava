package com.nerox.client.modules;

import java.io.InputStream;
import java.lang.reflect.Method;

import com.nerox.client.TfprotocolSuper;
import com.nerox.client.callbacks.IXSRPCProxyCallback;
import com.nerox.client.misc.StatusInfo;


public class XSRPCProxy extends TfprotocolSuper<IXSRPCProxyCallback>{

  public XSRPCProxy(String ipServer, int portServer, String publicKey, 
    String hash, int len, String protocol, IXSRPCProxyCallback protoHandler) {
    super(ipServer, portServer, publicKey, hash, len, protocol, protoHandler);
  }

  public XSRPCProxy(String proxy, String ipServer, int portServer, String publicKey,
    String hash, int len, String protocol, IXSRPCProxyCallback protoHandler) {
    super(proxy, ipServer, portServer, publicKey, hash, len, protocol, protoHandler);
  }

  public XSRPCProxy(TfprotocolSuper tfprotocol, IXSRPCProxyCallback protoHandler) {
    this.setProtoHandler(protoHandler);
    this.easyreum = tfprotocol.getConHandler();
  }
  
  public XSRPCProxy(String ipServer, int portServer, InputStream publicKey,
    String hash, int len, String protocol,
    IXSRPCProxyCallback protoHandler){
    super(ipServer, portServer, publicKey, hash, len, protocol, protoHandler);
  }

  public void startCommand(){
    this.getProtoHandler().startRPCProxyCallback(
      this.easyreum.getBuilder().build("XS_RPCPROXY").translate()
      .getBuilder().buildStatusInfo()
    );
  }
  public boolean sendHashCommand(String hash){
    this.easyreum.getBuilder().build(hash).send();
    this.easyreum.receiveHeader();
    return this.easyreum.getHeader() == 0;
  }
  public int sendPayload(String payload){
    return this.sendPayload(payload.getBytes());
  }
  public int sendPayload(byte[] payload){
    this.easyreum.getBuilder().build(payload).send();
    this.easyreum.getBuilder().build(0).sendJust();
    do{
      this.getProtoHandler().receivePayload(this.easyreum.receive().getBuilder().buildStatusInfo());
    }while(this.easyreum.getHeader() > 0);
    this.easyreum.receiveHeader();
    return this.easyreum.getHeader();
  }

  public void exitCommand(){
    this.easyreum.getBuilder().build(-1).sendJust();
  }
} 
