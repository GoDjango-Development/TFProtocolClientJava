package com.nerox.client.modules;

import com.nerox.client.Tfprotocol;
import com.nerox.client.TfprotocolSuper;
import com.nerox.client.callbacks.IXSRPCProxyCallback;

public class XSRPCProxy extends TfprotocolSuper<IXSRPCProxyCallback>{
  public boolean startCommand(){
    super.getProtoHandler().startRPCProxyCallback(
      this.easyreum.getBuilder().build("XS_RPCPROXY").translate()
      .getBuilder().buildStatusInfo()
    );
  }
  
} 
