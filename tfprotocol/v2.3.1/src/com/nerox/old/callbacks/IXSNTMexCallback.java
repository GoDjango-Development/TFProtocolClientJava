package com.nerox.old.callbacks;

import com.nerox.old.connection.WrappedClient;
import com.nerox.old.misc.StatusInfo;
import com.nerox.old.modules.XSNTMex;


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
