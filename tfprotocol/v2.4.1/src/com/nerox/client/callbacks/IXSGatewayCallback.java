package com.nerox.client.callbacks;

import com.nerox.client.misc.StatusInfo;
import com.nerox.client.modules.XSGateway;

public interface IXSGatewayCallback extends ISuperCallback<XSGateway> {
    default void startGateway(StatusInfo xs_gateway){
        throw new RuntimeException("Not implemented exception");
    }

    default void createIdentity(StatusInfo buildStatusInfo){
        throw new RuntimeException("Not implemented exception");
    }
}
