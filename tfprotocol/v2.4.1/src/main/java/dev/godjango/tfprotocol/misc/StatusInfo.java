package dev.godjango.tfprotocol.misc;

import java.util.Arrays;

public class StatusInfo {

    private StatusServer status;
    private long code = 0;
    private String message = "";
    private byte[] payload;
    private int opcode;
    private int sz;
    public StatusInfo(StatusServer status, long code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
    public StatusInfo(int opcode, int sz, String message) {
        this.opcode = opcode;
        this.sz = sz;
        this.message = message;
    }
    public StatusInfo(StatusServer status, byte[] payload) {
        this.status = status;
        this.payload = payload;
    }

    public StatusInfo(byte[] payload) {
        this.payload = payload;
    }

    public StatusInfo(StatusServer status) {
        this.status = status;
    }

    public static StatusInfo build(String message) {
        String[] values = message.split(" ");
        StatusInfo status = null;
        if (values.length == 1) //case ok
            status = new StatusInfo(StatusServer.valueOf(values[0]), 0, "");
        if (status == null)
            try {
                status = new StatusInfo(StatusServer.valueOf(values[0]), Integer.parseInt(values[1]), concatMessage(values));
            } catch (Exception ex) //parse of module
            {
                status = new StatusInfo(StatusServer.valueOf(values[0]), Integer.parseInt(values[2]), concatMessage(values));
            }
        return status;
    }

    public static StatusInfo build(byte[] payload) {
        return new StatusInfo(payload);
    }

    public static StatusInfo build(String message, byte[] payload) {
        String[] values = message.split(" ");
        StatusInfo statusInfo = null;
        if (values.length == 1) //case ok
            statusInfo = new StatusInfo(StatusServer.valueOf(values[0]), 0, "");
        else
            statusInfo = new StatusInfo(StatusServer.valueOf(values[0]), Integer.parseInt(values[1]), concatMessage(values));
        statusInfo.setPayload(payload);
        return statusInfo;
    }

    private static String concatMessage(String[] values) {
        StringBuilder msg = new StringBuilder();
        for (int i = 3; i < values.length; i++) {
            msg.append(values[i] + " ");
        }
        return msg.toString();
    }

    public StatusServer getStatus() {
        return status;
    }

    public int getOpcode() {
        return opcode;
    }

    public int getSz() {
        return sz;
    }

    public void setStatus(StatusServer status) {
        this.status = status;
    }

    public long getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public byte[] getPayload() {
        return payload;
    }

    public StatusInfo setPayload(byte[] payload) {
        this.payload = payload;
        return this;
    }

    @Override
    public String toString() {
        return "StatusInfo{" +
                "status=" + status +
                ", code=" + code +
                ", message='" + message + '\'' +
                ", payload=" + Arrays.toString(payload) +
                ", opcode=" + opcode +
                ", sz=" + sz +
                '}';
    }
}
