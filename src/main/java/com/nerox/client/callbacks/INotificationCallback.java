package com.nerox.client.callbacks;

import com.nerox.client.misc.NotifyStatus;
import com.nerox.client.misc.StatusInfo;
import com.nerox.client.modules.Notification;

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
