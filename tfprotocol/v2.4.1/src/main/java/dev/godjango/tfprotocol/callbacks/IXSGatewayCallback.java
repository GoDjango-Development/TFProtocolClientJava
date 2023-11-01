package dev.godjango.tfprotocol.callbacks;

import dev.godjango.tfprotocol.misc.StatusInfo;
import dev.godjango.tfprotocol.modules.XSGateway;

public interface IXSGatewayCallback extends ISuperCallback<XSGateway> {
    default void startGateway(StatusInfo xs_gateway){
        throw new RuntimeException("Not implemented exception");
    }

    default void createIdentity(StatusInfo buildStatusInfo){
        throw new RuntimeException("Not implemented exception");
    }
}
