package com.nerox.old.callbacks;

import com.nerox.old.misc.StatusInfo;
import com.nerox.old.modules.ExtendedSub1;

public interface IExtendedSub1Callback extends ISuperCallback<ExtendedSub1> {
    //EXTENDED SUBSYSTEM
    default void xs1_openCallback(StatusInfo status){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void xs1_closeCallback(StatusInfo status){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void xs1_truncCallback(StatusInfo status){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void xs1_seekCallback(StatusInfo status){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void xs1_lockCallback(StatusInfo status){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void xs1_readCallback(StatusInfo status){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void xs1_writeCallback(StatusInfo status){
        throw new RuntimeException("Callback is not implemented: exception");
    }
}
