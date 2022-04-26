package com.nerox.old.callbacks;

import com.nerox.old.Tfprotocol;
import com.nerox.old.connection.Easyreum;
import com.nerox.old.misc.Filestat;
import com.nerox.old.misc.StatusInfo;

import java.util.Date;

public interface ITfprotocolCallback extends ISuperCallback<Tfprotocol>{

    default void echoCallback(String value){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void mkdirCallback(StatusInfo status){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void delCallback(StatusInfo status){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void rmdirCallback(StatusInfo status){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void copyCallback(StatusInfo status){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void touchCallback(StatusInfo status){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void dateCallback(Integer timestamp, StatusInfo status){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void datefCallback(Date date, StatusInfo status){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void dtofCallback(Date date, StatusInfo status){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void ftodCallback(Integer timestamp, StatusInfo status){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void fstatCallback(Filestat filestat, StatusInfo status){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void fupdCallback(StatusInfo status){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void cpdirCallback(StatusInfo status){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void xcopyCallback(StatusInfo status){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void xdelCallback(StatusInfo status){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void xrmdirCallback(StatusInfo status){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void xcpdirCallback(StatusInfo status){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void lockCallback(StatusInfo status){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void sendFileCallback(boolean isOverwritten, String path,
                                  StatusInfo sendToServer, byte[] payload){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void rcvFileCallback(boolean deleteAfter, String path,
                                 StatusInfo sendToServer){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void lsCallback(StatusInfo status) {
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void lsrCallback(StatusInfo status) {
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void renamCallback(StatusInfo status){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void keepAliveCallback(StatusInfo status){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    // Update n4b3ts3
    default void loginCallback(StatusInfo loginStatus){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void chmodCallback(StatusInfo chmodStatus){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void chownCallback(StatusInfo chownStatus){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void getCanCallback(StatusInfo pcanStatus, Easyreum easyreum) {
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void putCanCallback(StatusInfo statusInfo, Easyreum easyreum){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void sha256Callback(StatusInfo statusInfo){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void prockeyCallback(StatusInfo statusInfo){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void freespCallback(StatusInfo buildStatusInfo){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void udateCallback(StatusInfo udate){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void ndateCallback(StatusInfo ndate){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void getWriteCallback(Tfprotocol.Codes codes){
        throw new RuntimeException("Callback is not implemented: exception");
    }
    default void getReadCallback(StatusInfo statusInfo){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void putCallback(Tfprotocol.Codes codes){
        throw new RuntimeException("Callback is not implemented: exception");
    }
    default void putStatusCallback(StatusInfo statusInfo){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void nigmaCallback(StatusInfo bld_stat_inf){
        throw new RuntimeException("Callback is not implemented: exception");
    }
    default void rmSecureDirectoryCallback(StatusInfo bld_stat_inf){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void injailCallback(StatusInfo injail){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void tlbCallback(StatusInfo tlb){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void sdownCallback(StatusInfo sdown){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void supCallback(StatusInfo sup){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void fsizeCallback(StatusInfo fsize){
        throw new RuntimeException("Callback is not implemented: exception");
    }

    default void fsizelsCallback(StatusInfo fsizels){
        throw new RuntimeException("Callback is not implemented: exception");
    }
}
