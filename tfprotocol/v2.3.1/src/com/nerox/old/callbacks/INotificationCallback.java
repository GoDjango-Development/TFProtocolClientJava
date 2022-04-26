package com.nerox.old.callbacks;

import com.nerox.old.misc.NotifyStatus;
import com.nerox.old.misc.StatusInfo;
import com.nerox.old.modules.Notification;

import java.io.IOException;

public interface INotificationCallback extends ISuperCallback<Notification>{
    //NOTIFICATION SYSTEM
    default void addntfyCallback(StatusInfo status){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void startnfyCallback(Notification notification, NotifyStatus status) throws IOException{
        throw new RuntimeException("Callback is not implemented: exception");
    }
}
