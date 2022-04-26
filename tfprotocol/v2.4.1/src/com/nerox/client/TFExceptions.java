package com.nerox.client;

import com.nerox.client.misc.StatusInfo;
import com.nerox.client.misc.StatusServer;

public final class TFExceptions extends RuntimeException {
    public enum ErrorCodes {
        ON_WRITE_OR_RECEIVE_TO_SOCKET,
        ILLEGAL_ARGUMENTS,
        UNHANDLED_EXCEPTION,
        CAN_PUT,
        ON_COMMAND_EXECUTION,
        FILE_NOT_FOUND
    }

    private StatusInfo status_info;
    private Exception originalException;

    public TFExceptions(Exception ex){
        this.status_info = new StatusInfo(StatusServer.FAILED,
                ex.hashCode(), ex.getClass().getName() + ": " +  ex.getMessage() + "\n");
        this.originalException = ex;
    }
    public TFExceptions(Exception ex, String message){
        this.status_info = new StatusInfo(StatusServer.FAILED,
                ex.hashCode(), ex.getClass().getName() + ": " +  ex.getMessage() + "\n" + message);
        this.originalException = ex;
    }
    public TFExceptions(StatusInfo status_info){
        this.status_info = status_info;
    }
    public TFExceptions(Exception exception, int code){
        this.status_info = new StatusInfo(StatusServer.FAILED,code,
                exception.getClass().getName() + ": " +  exception.getMessage() + "\n");
        this.originalException = exception;
    }
    public TFExceptions(int code, String message){
        this.status_info = new StatusInfo(StatusServer.FAILED, code, message);
    }
    public TFExceptions(StatusServer statusServer, ErrorCodes errorCodes, String message){
        this.status_info = new StatusInfo(statusServer, errorCodes.ordinal(), message);
    }

    public StatusInfo getStatusInfo(){
        return this.status_info;
    }
    public byte[] getPayload(){
        return this.status_info.getPayload() == null ?
                this.status_info.getMessage().getBytes():
                this.status_info.getPayload();
    }
    public long getCode(){
        return this.status_info.getCode();
    }
    public Exception getOriginalException(){
        return this.originalException;
    }
    @Override
    public String toString() {
        return "\n--------------------------------------------------\n" +
                this.status_info.getStatus() + "\n" +
                this.status_info.getCode() + "\n" +
                this.status_info.getMessage() + "\n" +
                "--------------------------------------------------\n";
    }

    @Override
    public String getMessage() {
        return this.status_info.getMessage();
    }
}
