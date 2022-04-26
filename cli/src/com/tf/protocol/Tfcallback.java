package com.tf.protocol;

import com.nerox.client.Tfprotocol;
import com.nerox.client.callbacks.ITfprotocolCallback;
import com.nerox.client.connection.Easyreum;
import com.nerox.client.constants.TfprotocolConsts;
import com.nerox.client.misc.FileStat;
import com.nerox.client.misc.StatusInfo;

import java.util.Date;

public class Tfcallback implements ITfprotocolCallback {
    @Override
    public void responseServerCallback(StatusInfo status) {

    }

    @Override
    public void instanceTfProtocol(Tfprotocol instance) {
    }

    @Override
    public void statusServer(StatusInfo status) {
        System.out.println(status);
    }

    @Override
    public void echoCallback(String value) {
        System.out.println(value);
    }

    @Override
    public void mkdirCallback(StatusInfo status) {
        statusServer(status);
    }

    @Override
    public void delCallback(StatusInfo status) {
        statusServer(status);
    }

    @Override
    public void rmdirCallback(StatusInfo status) {
        statusServer(status);
    }

    @Override
    public void copyCallback(StatusInfo status) {
        statusServer(status);
    }

    @Override
    public void touchCallback(StatusInfo status) {
        statusServer(status);
    }

    @Override
    public void dateCallback(Integer timestamp, StatusInfo status) {
        System.out.println(timestamp.toString());
        statusServer(status);
    }

    @Override
    public void datefCallback(Date date, StatusInfo status) {
        System.out.println(date.toString());
        statusServer(status);
    }

    @Override
    public void dtofCallback(Date date, StatusInfo status) {
        System.out.println(date.toString());
        statusServer(status);
    }

    @Override
    public void ftodCallback(Integer timestamp, StatusInfo status) {
        System.out.println(timestamp.toString());
        statusServer(status);
    }

    @Override
    public void fstatCallback(FileStat filestat, StatusInfo status) {
        System.out.println(filestat.toString());
        statusServer(status);
    }

    @Override
    public void fupdCallback(StatusInfo status) {
        statusServer(status);
    }

    @Override
    public void cpdirCallback(StatusInfo status) {
        statusServer(status);
    }

    @Override
    public void xcopyCallback(StatusInfo status) {
        statusServer(status);
    }

    @Override
    public void xdelCallback(StatusInfo status) {
        statusServer(status);
    }

    @Override
    public void xrmdirCallback(StatusInfo status) {
        statusServer(status);
    }

    @Override
    public void xcpdirCallback(StatusInfo status) {
        statusServer(status);
    }

    @Override
    public void lockCallback(StatusInfo status) {
        statusServer(status);
    }

    @Override
    public void sendFileCallback(boolean isOverwritten, String path, StatusInfo sendToServer, byte[] payload) {
        System.out.println(isOverwritten);
        System.out.println(path);
        System.out.println(sendToServer);
        System.out.println(payload);
    }

    @Override
    public void rcvFileCallback(boolean deleteAfter, String path, StatusInfo sendToServer) {
        System.out.println(deleteAfter);
        System.out.println(path);
        statusServer(sendToServer);
    }

    @Override
    public void lsCallback(StatusInfo status) {
        statusServer(status);
    }

    @Override
    public void lsrCallback(StatusInfo status) {
        statusServer(status);
    }

    @Override
    public void renamCallback(StatusInfo status) {
        statusServer(status);
    }

    @Override
    public void keepAliveCallback(StatusInfo status) {
        statusServer(status);
    }

    @Override
    public void loginCallback(StatusInfo loginStatus) {
        statusServer(loginStatus);
    }

    @Override
    public void chmodCallback(StatusInfo chmodStatus) {
        statusServer(chmodStatus);
    }

    @Override
    public void chownCallback(StatusInfo chownStatus) {
        statusServer(chownStatus);
    }

    @Override
    public void getCanCallback(StatusInfo pcanStatus, Easyreum easyreum) {
        statusServer(pcanStatus);
    }

    @Override
    public void putCanCallback(StatusInfo statusInfo, Easyreum easyreum) {
        statusServer(statusInfo);
    }

    @Override
    public void sha256Callback(StatusInfo statusInfo) {
        statusServer(statusInfo);
    }

    @Override
    public void prockeyCallback(StatusInfo statusInfo) {
        statusServer(statusInfo);
    }

    @Override
    public void freespCallback(StatusInfo buildStatusInfo) {
        statusServer(buildStatusInfo);
    }

    @Override
    public void udateCallback(StatusInfo udate) {
        statusServer(udate);
    }

    @Override
    public void ndateCallback(StatusInfo ndate) {
        statusServer(ndate);
    }

    @Override
    public void getWriteCallback(Tfprotocol.Codes codes) {
    }

    @Override
    public void getReadCallback(StatusInfo statusInfo) {
        statusServer(statusInfo);
    }

    @Override
    public void putCallback(Tfprotocol.Codes codes) {
    }

    @Override
    public void putStatusCallback(StatusInfo statusInfo) {
        statusServer(statusInfo);
    }

    @Override
    public void nigmaCallback(StatusInfo statusInfo) {
        statusServer(statusInfo);
    }

    @Override
    public void rmSecureDirectoryCallback(StatusInfo statusInfo) {
        statusServer(statusInfo);
    }

    @Override
    public void injailCallback(StatusInfo injail) {
        statusServer(injail);
    }

    @Override
    public void tlbCallback(StatusInfo tlb) {
        statusServer(tlb);
    }

    @Override
    public void sdownCallback(StatusInfo sdown) {
        statusServer(sdown);
    }

    @Override
    public void supCallback(StatusInfo sup) {
        statusServer(sup);
    }

    @Override
    public void fsizeCallback(StatusInfo fsize) {
        statusServer(fsize);
    }

    @Override
    public void fsizelsCallback(StatusInfo fsizels) {
        statusServer(fsizels);
    }

    @Override
    public void tlbUDPCallback(StatusInfo tlb) {
        statusServer(tlb);
    }

    @Override
    public void lsv2Callback(StatusInfo buildStatusInfo) {
        statusServer(buildStatusInfo);
    }

    @Override
    public void fstypeCallback(TfprotocolConsts.FSTYPE value) {
        System.out.println(value.name());
    }

    @Override
    public void fstypelsCallback(TfprotocolConsts.FSTYPE value) {
        System.out.println(value.name());
    }

    @Override
    public void fstatlsCommand(byte b, TfprotocolConsts.FSTYPE value, long wrap, long wrap1, long wrap2) {
        System.out.println(b);
        System.out.println(value);
        System.out.println(wrap);
        System.out.println(wrap1);
        System.out.println(wrap2);
    }

    @Override
    public void lsrv2Callback(StatusInfo statusInfo) {
        statusServer(statusInfo);
    }
}
