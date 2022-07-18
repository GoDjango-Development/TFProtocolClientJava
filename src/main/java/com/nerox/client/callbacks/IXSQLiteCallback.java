package com.nerox.client.callbacks;

import com.nerox.client.misc.StatusInfo;
import com.nerox.client.modules.XSQLite;

public interface IXSQLiteCallback extends ISuperCallback<XSQLite> {
    void startXSSQLiteCallback(StatusInfo statusInfo);
    default void openCallback(StatusInfo statusInfo){
        throw new RuntimeException("Callback is not implemented: exception");
    }
    default void execCallback(StatusInfo buildStatusInfo){
        throw new RuntimeException("Callback is not implemented: exception");
    }
    default void execOfCallback(StatusInfo buildStatusInfo){
        throw new RuntimeException("Callback is not implemented: exception");
    }
    default void closeCallback(StatusInfo buildStatusInfo){
        throw new RuntimeException("Callback is not implemented: exception");
    }
    default void lastRowIdCallback(StatusInfo buildStatusInfo){
        throw new RuntimeException("Callback is not implemented: exception");
    }
    default void softHeapCallback(StatusInfo buildStatusInfo){
        throw new RuntimeException("Callback is not implemented: exception");
    }
    default void hardHeapCallback(StatusInfo buildStatusInfo){
        throw new RuntimeException("Callback is not implemented: exception");
    }
    default void blobinCallback(StatusInfo buildStatusInfo){
        throw new RuntimeException("Callback is not implemented: exception");
    }
    default void bloboutCallback(StatusInfo buildStatusInfo){
        throw new RuntimeException("Callback is not implemented: exception");
    }
    default void exitCallback(StatusInfo exit){
        throw new RuntimeException("Callback is not implemented: exception");
    }
    default void terminateCallback(StatusInfo terminate){
        throw new RuntimeException("Callback is not implemented: exception");
    }
}
