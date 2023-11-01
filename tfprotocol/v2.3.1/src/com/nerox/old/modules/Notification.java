package com.nerox.old.modules;

import com.nerox.old.TfprotocolSuper;
import com.nerox.old.callbacks.INotificationCallback;
import com.nerox.old.connection.Client;
import com.nerox.old.misc.NotifyStatus;

import java.io.IOException;

public class Notification extends TfprotocolSuper<INotificationCallback> {

    private Client clientNotification = null;

    public Notification(String ipServer, int portServer, String publicKey, String hash, int len, String protocol, INotificationCallback protoHandler) {
        super(ipServer, portServer, publicKey, hash, len, protocol, protoHandler);
    }
    public Notification(String proxy, String ipServer, int portServer, String publicKey, String hash, int len, String protocol, INotificationCallback protoHandler) {
        super(proxy, ipServer, portServer, publicKey, hash, len, protocol, protoHandler);
    }
    public Notification(TfprotocolSuper tfprotocol, INotificationCallback protoHandler) throws IOException {
        this.setProtoHandler(protoHandler);
    }

    public void startnfyCommand(double interval) throws IOException {
        String stringValue = String.valueOf(interval);
        String startnfyCommand = String.format("STARTNTFY %s", stringValue);
        NotifyStatus notify = null;
        do {
            String message = new String(this.easyreum.translate(startnfyCommand).getPayloadReceived());
            notify = new NotifyStatus(Long.parseLong(message.split(" ")[0]), message.split(" ")[1]);
            getProtoHandler().startnfyCallback(this, notify);
        } while (true);
    }

    public void startConnection() throws Exception {
        clientNotification = super.connect();
    }

    public void stopConnection() throws Exception {
        super.disconnect();
    }

    public void addntfyCommand(String token, String path) {
        super.getProtoHandler().addntfyCallback(this.easyreum
                .getBuilder().build("ADDNTFY", token, path).translate()
                .getBuilder().buildStatusInfo());
    }

    public void sendToServer(notifyStatus notify) {
        this.easyreum.translate(notify.name())
                .getBuilder().buildStatusInfo();
    }

    public enum notifyStatus {
        OK, DEL
    }

}
