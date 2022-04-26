package com.nerox.old.modules;

import com.nerox.old.TFExceptions;
import com.nerox.old.TfprotocolSuper;
import com.nerox.old.callbacks.IExtendedSub1Callback;
import com.nerox.old.connection.Easyreum;
import com.nerox.old.misc.StatusInfo;
import com.nerox.old.misc.StatusServer;

import java.io.IOException;

public class ExtendedSub1 extends TfprotocolSuper<IExtendedSub1Callback> {

    private Easyreum easyreum;

    public ExtendedSub1(String ipServer, int portServer, String privateKey, String hash, int len, String protocol,
            IExtendedSub1Callback protoHandler) {
        super(ipServer, portServer, privateKey, hash, len, protocol, protoHandler);
    }
    public ExtendedSub1(String proxy, String ipServer, int portServer, String privateKey, String hash, int len, String protocol,
            IExtendedSub1Callback protoHandler) {
        super(proxy, ipServer, portServer, privateKey, hash, len, protocol, protoHandler);
    }
    public ExtendedSub1(TfprotocolSuper tfprotocol, IExtendedSub1Callback extendedSub1) {
        if (tfprotocol == null ||
                tfprotocol.getClient() == null || !tfprotocol.getClient().isConnect())
            throw new TFExceptions(-1,"Invalid protocol or disconnected protocol... please call " +
                    "to connect function first...");
        this.setProtoHandler(extendedSub1);
        this.easyreum = new Easyreum(tfprotocol.getClient());
    }

    public boolean checkPresent() throws IOException, TFExceptions {
        //if (clientExtended != null) {
            return !this.easyreum.getBuilder().build("XS1_OPEN").translate()
                    .getBuilder().buildStatusInfo()
                    .getStatus().equals(StatusServer.UNKNOWN);
            //StatusInfo checkPresent = StatusInfo.build(new String(clientExtended.translate(clientExtended.getSessionKey(),
            //        "XS1_OPEN".getBytes(), clientExtended.getBufSize())));
            //return !checkPresent.getStatus().equals(StatusServer.UNKNOWN);
        //}
        //return false;
    }

    //EXTENDED SUBSYSTEM 1
    public void xs1_openCommand(String path, StringBuilder...decriptor) throws IOException, TFExceptions {
        //String xs1_openCommand = "XS1_OPEN " + path;
        this.getProtoHandler().xs1_openCallback(this.easyreum.getBuilder().build("XS1_OPEN",path)
                .translate().getBuilder().buildStatusInfo());
        if (decriptor!= null && decriptor.length > 0 && decriptor[0] != null)
            decriptor[0].delete(0,decriptor[0].length())
                    .append(this.easyreum.getBuilder().getStatusInfo().getCode());
        //if (clientExtended.isConnect()) {
        //    StatusInfo xs1_openStatus = StatusInfo.build(new String(clientExtended.translate(clientExtended.getSessionKey(),
        //            xs1_openCommand.getBytes(), clientExtended.getBufSize())));
        //    getProtoHandler().xs1_openCallback(xs1_openStatus);
        //}
    }

    //public void startConnection() throws Exception {
    //    clientExtended = super.connect();
    //}

    public void xs1_closeCommand(int fileDescriptor) throws IOException, TFExceptions {
        //String xs1_closeCommand = "XS1_CLOSE " + fileDescriptor;
        this.getProtoHandler().xs1_closeCallback(this.easyreum
                .getBuilder().build("XS1_CLOSE", String.valueOf(fileDescriptor))
                .translate()
                .getBuilder().buildStatusInfo());
        //if (clientExtended.isConnect()) {
        //    StatusInfo xs1_closeStatus = StatusInfo.build(new String(clientExtended.translate(clientExtended.getSessionKey(),
        //            xs1_closeCommand.getBytes(), clientExtended.getBufSize())));
        //    getProtoHandler().xs1_closeCallback(xs1_closeStatus);
        //}
    }

    //public void stopConnection() throws Exception {
    //    super.disconnect();
    //}

    public void xs1_truncCommand(int fileDescriptor, long newSize) throws IOException {
        //String xs1_truncCommand = String.format("XS1_TRUNC %s %s", fileDescriptor, newSize);
        this.getProtoHandler().xs1_truncCallback(this.easyreum.getBuilder()
                .build("XS1_TRUNC",String.valueOf(fileDescriptor),
                String.valueOf(newSize))
                .getBuilder().buildStatusInfo());
        //if (clientExtended.isConnect()) {
        //    StatusInfo xs1_truncStatus = StatusInfo.build(new String(
        //            clientExtended.translate(clientExtended.getSessionKey(),
        //            xs1_truncCommand.getBytes(), clientExtended.getBufSize())));
        //    getProtoHandler().xs1_truncCallback(xs1_truncStatus);
        //}
    }

    public void xs1_seekCommand(int fileDescriptor, long offset, XS1_FDENUM whence) throws IOException, TFExceptions {
        //String xs1_seekCommand = String.format("XS1_SEEK %s %s %s", fileDescriptor, offset, whence.name());
        this.getProtoHandler().xs1_seekCallback(this.easyreum
                .getBuilder().build("XS1_SEEK", String.valueOf(fileDescriptor),
                        String.valueOf(offset), whence.name())
                .translate().getBuilder().buildStatusInfo());
        //if (clientExtended.isConnect()) {
        //    StatusInfo xs1_seekStatus = StatusInfo.build(new String(
        //            clientExtended.translate(clientExtended.getSessionKey(),
        //            xs1_seekCommand.getBytes(), clientExtended.getBufSize())));
        //    getProtoHandler().xs1_seekCallback(xs1_seekStatus);
        //}
    }

    public void xs1_lockCommand(int fileDescriptor, XS1_LOCKTYE lockType) throws IOException, TFExceptions {
        //String xs1_lockCommand = String.format("XS1_LOCK %s %s", fileDescriptor, lockType.name());
        this.getProtoHandler().xs1_lockCallback(this.easyreum
                .getBuilder().build("XS1_LOCK", String.valueOf(fileDescriptor),
                        lockType.name()).translate().getBuilder().buildStatusInfo());
        //if (clientExtended.isConnect()) {
        //    StatusInfo xs1_lockStatus = StatusInfo.build(new String(
        //            clientExtended.translate(clientExtended.getSessionKey(),
        //            xs1_lockCommand.getBytes(), clientExtended.getBufSize())));
        //    getProtoHandler().xs1_lockCallback(xs1_lockStatus);
        //}
    }

    public void xs1_readCommand(int fileDescriptor, int byteToRead) throws IOException, TFExceptions {
        //String xs1_readCommand = String.format("XS1_READ %s %s", fileDescriptor, byteToRead);
        this.easyreum.getBuilder().build("XS1_READ", String.valueOf(fileDescriptor),
                String.valueOf(byteToRead));
        //StatusInfo xs1_readStatus = null;
        //if (clientExtended.isConnect()) {
            if (byteToRead < 0 || byteToRead > (1024*512)-3) {
                //xs1_readStatus = StatusInfo.build("PAYLOAD_TOO_BIG");
                this.easyreum.getBuilder().setStatusInfo(StatusInfo.build("PAYLOAD_TOO_BIG"));
            } else {
                //String message = new String(clientExtended.translate(
                //        clientExtended.getSessionKey(), xs1_readCommand.getBytes(),
                //        clientExtended.getBufSize()));
                this.easyreum.translate().getBuilder().buildStatusInfo();
                //if (message.split(" ")[0].equals(StatusServer.OK.name()))
                //    xs1_readStatus = StatusInfo.build(message.split(" ")[0],
                //            Helper.converToString(message.split(" "), false).getBytes());
            }
            //getProtoHandler().xs1_readCallback(xs1_readStatus);
            this.getProtoHandler().xs1_readCallback(this.easyreum.getBuilder().getStatusInfo());
        //}
    }

    public void xs1_writeCommand(int fileDescriptor, byte[] payload) throws IOException, TFExceptions {
        //int byteToWrite = payload.length;
        //String xs1_writeCommand = String.format("XS1_WRITE %s %s %s", fileDescriptor, byteToWrite, new String(payload));
        this.easyreum.getBuilder().build("XS1_WRITE", String.valueOf(fileDescriptor),
                String.valueOf(payload.length), new String(payload));
        //StatusInfo xs1_writeStatus;
        //if (clientExtended.isConnect()) {
            if (payload.length > (1024*512)-28) {
                //xs1_writeStatus = StatusInfo.build("PAYLOAD_TOO_BIG");
                this.easyreum.getBuilder().setStatusInfo(StatusInfo.build("PAYLOAD_TOO_BIG"));
            } else {
                //xs1_writeStatus = StatusInfo.build(new String(
                //        clientExtended.translate(clientExtended.getSessionKey(),
                //        xs1_writeCommand.getBytes(), clientExtended.getBufSize())));
                this.easyreum.translate().getBuilder().buildStatusInfo();
            }
            //getProtoHandler().xs1_writeCallback(xs1_writeStatus);
            this.getProtoHandler().xs1_writeCallback(this.easyreum.getBuilder().getStatusInfo());
        //}
    }

    public enum XS1_FDENUM {
        CUR, SET, END
    }

    public enum XS1_LOCKTYE {
        LOCK, LOCKW, UNLOCK
    }

}
