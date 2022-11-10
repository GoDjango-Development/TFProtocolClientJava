package com.nerox.client.callbacks;

import com.nerox.client.misc.StatusInfo;
import com.nerox.client.modules.XSRPCProxy;

public interface IXSRPCProxyCallback extends ISuperCallback<XSRPCProxy>{
    void startRPCProxyCallback(StatusInfo si);
    void receivePayload(StatusInfo si);
}
