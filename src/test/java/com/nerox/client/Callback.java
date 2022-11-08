package com.nerox.client;
import com.nerox.client.callbacks.ITfprotocolCallback;
import com.nerox.client.misc.StatusInfo;
import com.nerox.client.misc.StatusServer;
import com.nerox.client.misc.FileStat;

public class Callback implements ITfprotocolCallback{
    Tfprotocol tfprotocol;
    @Override
    public void statusServer(StatusInfo status) {
        System.out.println("------------------------------");
        System.out.println(status.getStatus());
        System.out.println(status.getMessage());
        System.out.println(status.getPayload()!=null ?new String(status.getPayload()):"");
        System.out.println(status.getCode());
        System.out.println("------------------------------");
    }
    @Override
    public void loginCallback(StatusInfo info){
        this.statusServer(info);
    }
    @Override
    public void responseServerCallback(StatusInfo status) {

    }

    @Override
    public void instanceTfProtocol(Tfprotocol instance) {
        this.tfprotocol = instance;
    }

    @Override
    public void dateCallback(Integer timestamp, StatusInfo status) {
        this.statusServer(status);
    }

    @Override
    public void datefCallback(java.util.Date date, StatusInfo status) {
        this.statusServer(status);
    }

    @Override
    public void dtofCallback(java.util.Date date, StatusInfo status) {
        this.statusServer(status);
    }
    @Override
    public void injailCallback(StatusInfo injail) {
        this.statusServer(injail);
    }

    @Override
    public void ftodCallback(Integer timestamp, StatusInfo status) {
        this.statusServer(status);
    }

    @Override
    public void fstatCallback(FileStat filestat, StatusInfo status) {
        this.statusServer(status);
        System.out.println(filestat);
    }

    @Override
    public void sendFileCallback(boolean isOverwritten, String path, StatusInfo sendToServer, byte[] payload) {
        this.statusServer(sendToServer);
        sendToServer.setStatus(StatusServer.OK);
    }

    @Override
    public void rcvFileCallback(boolean deleteAfter, String path, StatusInfo sendToServer) {
        this.statusServer(sendToServer);
    }

    @Override
    public void lsCallback(StatusInfo status) {
        this.statusServer(status);
    }

    @Override
    public void lsrCallback(StatusInfo status) {
        this.statusServer(status);
    }

    @Override
    public void putCallback(Tfprotocol.Codes codes) {
    }

    @Override
    public void putStatusCallback(StatusInfo statusInfo) {
        this.statusServer(statusInfo);
    }

    @Override
    public void echoCallback(String value) {
        System.out.println(value);
    }

    @Override
    public void tlbCallback(StatusInfo tlb) {
        this.statusServer(tlb);
    }

    @Override
    public void supCallback(StatusInfo sup) {
        System.out.println("Sup Callback");
        this.statusServer(sup);
    }

    @Override
    public void sdownCallback(StatusInfo sdown) {
        this.statusServer(sdown);
    }

    @Override
    public void tlbUDPCallback(StatusInfo tlb) {
        this.statusServer(tlb);
    }

    @Override
    public void fsizeCallback(StatusInfo fsize) {
        this.statusServer(fsize);
    }

    @Override
    public void fsizelsCallback(StatusInfo fsizels) {
        this.statusServer(fsizels);
    }

    @Override
    public void integrityWriteCallback(StatusInfo statusInfo) {
        this.statusServer(statusInfo);
    }
    @Override
    public void netlockCallback(StatusInfo statusInfo) {
        this.statusServer(statusInfo);
    }
    @Override
    public void netlockTryCallback(StatusInfo statusInfo) {
        this.statusServer(statusInfo);
    }
    @Override
    public void integrityReadCallback(StatusInfo statusInfo) {
        this.statusServer(statusInfo);
    }
    @Override
    public void releaseMutexCallback(StatusInfo statusInfo) {
        this.statusServer(statusInfo);
    }
    @Override
    public void acquireMutexCallback(StatusInfo statusInfo) {
        this.statusServer(statusInfo);
    }
    @Override
    public void netunlockCallback(StatusInfo statusInfo) {
        this.statusServer(statusInfo);
    }
    @Override
    public void xcpdirCallback(StatusInfo status) {
        this.statusServer(status);
    }
    @Override
    public void setfsidCallback(StatusInfo statusInfo){
        this.statusServer(statusInfo);
    }
    @Override
    public void setfspermCallback(StatusInfo statusInfo){
        this.statusServer(statusInfo);
    }
    // Override remfspermCallback
    @Override
    public void remfspermCallback(StatusInfo statusInfo){
        this.statusServer(statusInfo);
    }
    // Override getfspermCallback
    @Override
    public void getfspermCallback(StatusInfo statusInfo){
        this.statusServer(statusInfo);
    }
    // Override issecfsCallback
    @Override
    public void issecfsCallback(StatusInfo statusInfo){
        this.statusServer(statusInfo);
    }
}
