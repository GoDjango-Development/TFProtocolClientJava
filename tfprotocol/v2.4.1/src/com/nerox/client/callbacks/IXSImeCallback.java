package com.nerox.client.callbacks;
import com.nerox.client.misc.StatusInfo;
import com.nerox.client.modules.XSIme;

public interface IXSImeCallback extends ISuperCallback<XSIme> {
    void startCallback(StatusInfo xsime_start);

    default void tellCallback(StatusInfo sendJust){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    void exitCallback(StatusInfo exitCallback);

    default void listenCallback(StatusInfo listenCallback){
        throw new RuntimeException("Callback is not implemented: exception");
    }
}
