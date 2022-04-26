package com.nerox.old.callbacks;

import com.nerox.old.misc.StatusInfo;
import com.nerox.old.modules.XSAce;

public interface IXSAceCallback extends ISuperCallback<XSAce> {
    void startACECallback(StatusInfo xsace);

    void inskeyCallback(StatusInfo buildStatusInfo);

    default void exitCallback(StatusInfo exit){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void goBackCallback(StatusInfo goback){
        throw new RuntimeException("Callback is not implemented: exception");
    }
    default void runBackgroundCallback(StatusInfo runBk){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void setArgsCallback(StatusInfo buildStatusInfo){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void runNNLSizeCallback(StatusInfo runnl_sz){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void runBufSZCallback(StatusInfo exit){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void setRunBufCallback(StatusInfo buildStatusInfo){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void setRunLNCallback(StatusInfo buildStatusInfo){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void runWriteCallback(XSAce.Communication com){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void runReadCallback(StatusInfo buildStatusInfo){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void runWriteNNLCallback(XSAce.Communication com){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void runReadNLCallback(StatusInfo buildStatusInfo){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void runWriteBufCallback(XSAce.Communication com){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void runReadBufCallback(StatusInfo buildStatusInfo){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void setWorkingDirectoryCallback(StatusInfo buildStatus){
        throw new RuntimeException("Callback is not implemented: exception");
    }
}
