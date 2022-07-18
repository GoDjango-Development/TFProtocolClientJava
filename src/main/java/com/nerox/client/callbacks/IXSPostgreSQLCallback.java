package com.nerox.client.callbacks;

import com.nerox.client.misc.StatusInfo;
import com.nerox.client.modules.XSPostgresql;

public interface IXSPostgreSQLCallback extends ISuperCallback<XSPostgresql> {
    void startXSPostGreSQL(StatusInfo xs_postgresql);

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

    default void exitCallback(StatusInfo exit){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void terminateCallback(StatusInfo terminate){
        throw new RuntimeException("Callback is not implemented: exception");
    }
}
