package com.nerox.client.callbacks;

import com.nerox.client.misc.StatusInfo;
import com.nerox.client.modules.XSMysql;

public interface IXSMysqlCallback extends ISuperCallback<XSMysql> {
    void startXSMySqlCallback(StatusInfo xs_mysql);

    default void openCallback(StatusInfo open){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void execCallback(StatusInfo exec){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void execOfCallback(StatusInfo execof){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void closeCallback(StatusInfo close){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void lastRowIdCallback(StatusInfo close){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void blobInCallback(StatusInfo blobin){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void blobOutCallback(StatusInfo blobout){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void exitCallback(StatusInfo exit){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void terminateCallback(StatusInfo terminate){
        throw new RuntimeException("Callback is not implemented: exception");
    }
}
