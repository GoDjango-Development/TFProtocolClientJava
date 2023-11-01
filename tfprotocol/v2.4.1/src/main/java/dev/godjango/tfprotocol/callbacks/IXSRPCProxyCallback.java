package dev.godjango.tfprotocol.callbacks;

import dev.godjango.tfprotocol.misc.StatusInfo;
import dev.godjango.tfprotocol.modules.XSRPCProxy;

public interface IXSRPCProxyCallback extends ISuperCallback<XSRPCProxy>{
    void startRPCProxyCallback(StatusInfo si);
    void receivePayload(StatusInfo si);
}
