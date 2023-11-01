package dev.godjango.tfprotocol.callbacks;

import java.io.IOException;

import dev.godjango.tfprotocol.misc.NotifyStatus;
import dev.godjango.tfprotocol.misc.StatusInfo;
import dev.godjango.tfprotocol.modules.Notification;

public interface INotificationCallback extends ISuperCallback<Notification>{
    //NOTIFICATION SYSTEM
    default void addntfyCallback(StatusInfo status){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void startnfyCallback(Notification notification, NotifyStatus status) throws IOException{
        throw new RuntimeException("Callback is not implemented: exception");
    }
}
