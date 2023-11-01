package com.nerox.old.payload;

import com.nerox.old.connection.Easyreum;
import com.nerox.old.misc.StatusInfo;
import com.nerox.old.misc.StatusServer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class Easybuild {
    private final Easyreum easyreum;
    private StatusInfo status_info;

    public Easybuild(Easyreum easyreum){
        this.easyreum = easyreum;
    }
    /**
     * This is a helper tool feel yourself free to use it in order to concat two byte arrays with a whitespace
     * in the middle...  ;)
     * @param arr1 The first array for the sum
     * @param arr2 The second one
     * @return The resulting byte array.
     * */
    public byte[] add(byte[] arr1, byte[] arr2){
        if (arr1.length == 0) return arr2;
        byte[] res = new byte[arr1.length + arr2.length + 1];
        for (int i = 0; i < res.length; i ++){
            if (i < arr1.length)
                res[i] = arr1[i];
            else if(i==arr1.length)
                res[i] = ' ';
            else res[i] = arr2[(i-1)%(arr1.length)];
        }
        System.out.println(new String(res));
        return res;
    }

    private void buildStatusInfo(byte[] message, boolean...parse_code) {
        //System.out.println("Understanding what server said...");
        //System.out.println("Server said: " + new String(message));
        String message_as_string = new String(message);
        int last_ocurrence = message_as_string.length();
        StatusServer status = StatusServer.UNKNOWN;
        long code = this.easyreum.getHeader();
        for (StatusServer ss: StatusServer.values()) {
            int indexOf = message_as_string.indexOf(ss.name());
            if (indexOf >= 0 && indexOf < last_ocurrence){
                status = ss;
                last_ocurrence = indexOf;
                if (indexOf == 0)break;
            }
        }
        message_as_string = message_as_string.replaceFirst(status.name(),"").trim()
                .replaceFirst(" : ", "");
        if ((!StatusServer.valueOf(status.name()).equals(StatusServer.FAILED)
                && message_as_string.isEmpty())||
                (parse_code.length>0&&!parse_code[0]))
            code = status.ordinal();
        else{
            String[] temp = message_as_string.split("[^0-9]+");
            if (temp.length > 0 && !temp[0].isEmpty())
            {
                code = Long.parseLong(temp[0]);
                message_as_string = message_as_string.replaceFirst(String.valueOf(code),"").trim();
            }
        }
        this.status_info = new StatusInfo(status, code, message_as_string);
    }
    public Easyreum build(String message){
        this.easyreum.setPayloadSend(ByteBuffer.allocate(this.easyreum
                .getPayloadSend().length + message.getBytes().length)
                .put(this.easyreum.getPayloadSend())
                .put(message.getBytes()).array());
        return this.easyreum;
    }
    public Easyreum build(String... message){
        StringBuilder whole = new StringBuilder();
        for (String temp: message) whole.append(" ").append(temp);
        this.easyreum.setPayloadSend(
                ByteBuffer.allocate(this.easyreum.getPayloadSend().length +
                whole.toString().trim().getBytes().length)
                .put(this.easyreum.getPayloadSend()).put(whole.toString().trim()
                        .getBytes()).array());
        return this.easyreum;
    }
    public Easyreum build(byte[]... message){
        byte[] whole = new byte[0];
        for (byte[] temp: message) {
            whole = add(whole, temp);
        }
        this.easyreum.setPayloadSend(whole);
        System.out.println(whole.length);
        return this.easyreum;
    }
    public Easyreum build(boolean not_trim, String... message){
        if (!not_trim) this.build(message);
        else {
            StringBuilder whole = new StringBuilder();
            for (String temp : message) whole.append(temp).append(" ");
            this.easyreum.setPayloadSend(ByteBuffer.allocate(
                    this.easyreum.getPayloadSend().length +
                    whole.toString().getBytes().length)
                    .put(this.easyreum.getPayloadSend()).put(whole.toString().getBytes()).array());
        }
        return this.easyreum;
    }
    public Easyreum build(byte arg){
        this.easyreum.setPayloadSend(ByteBuffer.allocate(
                this.easyreum.getPayloadSend().length + Byte.BYTES)
                    .put(this.easyreum.getPayloadSend()).put(arg).array());
        return this.easyreum;
    }
    public Easyreum build(int arg){
        this.easyreum.setPayloadSend(ByteBuffer.allocate(
                this.easyreum.getPayloadSend().length + Integer.BYTES)
                    .put(this.easyreum.getPayloadSend()).putInt(arg).array());
        return this.easyreum;
    }
    public Easyreum build(long arg){
        this.easyreum.setPayloadSend(ByteBuffer.allocate(
                this.easyreum.getPayloadSend().length + Long.BYTES)
                    .put(this.easyreum.getPayloadSend()).putLong(arg).array());
        return this.easyreum;
    }
    public Easyreum build(byte[] payload){
        this.easyreum.setPayloadSend(ByteBuffer.allocate(
                this.easyreum.getPayloadSend().length + payload.length)
                    .put(payload).array());
        return this.easyreum;
    }
    public Easyreum build(int header_value, byte[] content){
        this.easyreum.setHeader(header_value);
        this.easyreum.setPayloadSend(
                ByteBuffer.allocate(this.easyreum.getPayloadSend().length + content.length)
                    .put(content).array());
        return this.easyreum;
    }
    public StatusInfo buildLongCode(){
        this.buildStatusInfo();
        if (this.easyreum.getPayloadReceived().length > Long.BYTES)
            this.status_info.setCode((int)ByteBuffer.wrap(
                    this.easyreum.getPayloadReceived(),
                    this.easyreum.getPayloadReceived().length-Long.BYTES,
                    Long.BYTES)
                    .order(ByteOrder.BIG_ENDIAN).getLong());
        return this.status_info;
    }
    public Easyreum buildStatusInfo(String status_msg, int status_code, String message){
        this.status_info = new StatusInfo(StatusServer.valueOf(status_msg),
                status_code,
                message);
        return this.easyreum;
    }
    public Easyreum buildStatusInfo(StatusServer status, int status_code, String message){
        this.status_info = new StatusInfo(status,
                status_code,
                message);
        return this.easyreum;
    }
    public Easyreum buildStatusInfo(StatusServer status, String message){
        this.status_info = new StatusInfo(status,
                status.ordinal(),
                message);
        return this.easyreum;
    }
    public StatusInfo buildStatusInfo(boolean...parse_code){
        if (this.easyreum.getPayloadReceived() == null) return this.getStatusInfo();
        this.buildStatusInfo(this.easyreum.getPayloadReceived(),parse_code);
        return this.status_info;
    }
    public StatusInfo getStatusInfo(){
        if (this.status_info != null)
            return this.status_info;
        else if (this.easyreum.getClient() == null)
            return StatusInfo.build("Client hasnt been created yet, did you called connect?");
        else if (!this.easyreum.getClient().isConnect())
            return StatusInfo.build("Client isnt connected...");
        else if (!this.easyreum.getClient().getSocket().isConnected())
            return StatusInfo.build("Socket isnt connected...");
        else if (this.easyreum.getClient().getSocket().isClosed())
            return StatusInfo.build("Socket is closed..");
        else if (this.status_info == null)
            return StatusInfo.build("Status info is null, build it first or set it...");
        return null;
    }
    public StatusInfo setStatusInfo(StatusInfo status_info){
        return (this.status_info = status_info);
    }

    public boolean isStatusInfoOk(){
        return this.status_info.getStatus().equals(StatusServer.OK);
    }
}
