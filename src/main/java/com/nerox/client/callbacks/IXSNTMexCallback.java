package com.nerox.client.callbacks;

import com.nerox.client.connection.WrappedClient;
import com.nerox.client.misc.StatusInfo;
import com.nerox.client.modules.XSNTMex;


public interface IXSNTMexCallback extends ISuperCallback<XSNTMex> {
    void startXSNTMexCallback(StatusInfo xs_ntmex);

    void inskeyCallback(StatusInfo buildStatusInfo);

    default void loadCallback(StatusInfo buildStatusInfo){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void runCallback(StatusInfo run, WrappedClient wrappedClient){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void goBackCallback(StatusInfo goback){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    void exitCallback(StatusInfo exit);

    default void sysInfoCallback(StatusInfo sysinfo){
        throw new RuntimeException("Callback is not implemented: exception");
    }
}
