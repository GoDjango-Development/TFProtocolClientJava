package com.nerox.Auth;

import com.nerox.Auth.Errors.TFAuthErrors;
import com.nerox.client.callbacks.IExtendedSub1Callback;
import com.nerox.client.misc.StatusInfo;
import com.nerox.client.misc.StatusServer;
import com.nerox.client.modules.ExtendedSub1;

import java.io.IOException;

public final class AuthCallback implements IExtendedSub1Callback {
    private ExtendedSub1 es;
    public static TFAuthHandler payloadHandler;
    static int fd = -1;
    private static void setFileDescriptor(){
        fd = fd == -1 ? 0 : fd+1;
    }

    @Override
    public void instanceTfProtocol(ExtendedSub1 tfprotocol) {
        es = tfprotocol;
        payloadHandler = new TFAuthHandler(tfprotocol);
    }

    @Override
    public void responseServerCallback(StatusInfo statusInfo) {
    }

    @Override
    public void xs1_openCallback(StatusInfo statusInfo){
        setFileDescriptor();
        if (Auth.currentState != Auth.States.Idle){
            if (StatusServer.OK == statusInfo.getStatus()){
                try {
                    if (Auth.currentState == Auth.States.Login){
                        this.es.xs1_readCommand(fd,payloadHandler.getDataAsBytesArray().length+3);
                    }else if (Auth.currentState == Auth.States.Register){
                        //Creating the payload for registered user
                        this.es.xs1_writeCommand(fd, payloadHandler.getDataAsBytesArray());
                    }else if (Auth.currentState == Auth.States.Handling_in){
                        this.es.xs1_readCommand(fd,100);
                    }else if (Auth.currentState == Auth.States.Handling_out){
                        this.es.xs1_writeCommand(fd,payloadHandler.getDataAsBytesArray());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    try {
                        this.es.xs1_closeCommand(fd);
                    } catch (IOException ignored) {}
                }
            }else if (StatusServer.FAILED == statusInfo.getStatus()){
                //System.err.println("Data file couldn't be opened, auth data will not be save it ...");
            }
        }
    }

    @Override
    public void xs1_closeCallback(StatusInfo statusInfo) {
        if (Auth.currentState != Auth.States.Idle){
            if (StatusServer.OK == statusInfo.getStatus()){
                fd--;
                //System.out.println("Data file closed successfully...");
            }else if (StatusServer.FAILED == statusInfo.getStatus()){
                //System.err.println("ERROR: Data file still open...");
            }
        }
    }

    @Override
    public void xs1_readCallback(StatusInfo statusInfo) {
        if (statusInfo != null && StatusServer.OK == statusInfo.getStatus()){
            if (Auth.currentState != Auth.States.Idle) {
                try {
                    String temp = TFCrypto.toString(statusInfo.getPayload(), "read");
                    temp = temp.replaceAll(":",": ");
                    payloadHandler.retrieveData(temp, true);
                } catch (TFAuthErrors tfAuthErrors) {
                    tfAuthErrors.printStackTrace();
                }
            }
        }
    }

    @Override
    public void xs1_writeCallback(StatusInfo statusInfo) {
        if (Auth.currentState != Auth.States.Idle){
            if (StatusServer.OK == statusInfo.getStatus()){
            }else if (StatusServer.FAILED == statusInfo.getStatus()){
            }
        }
    }
}
