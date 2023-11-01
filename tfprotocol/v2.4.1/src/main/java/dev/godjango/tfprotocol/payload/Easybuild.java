package dev.godjango.tfprotocol.payload;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import dev.godjango.tfprotocol.connection.SuperEasy;
import dev.godjango.tfprotocol.misc.StatusInfo;
import dev.godjango.tfprotocol.misc.StatusServer;

public final class Easybuild {
    private final SuperEasy superEasy;
    private StatusInfo status_info;

    public Easybuild(SuperEasy superEasy){
        this.superEasy = superEasy;
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
        return res;
    }

    private void buildStatusInfo(byte[] message, boolean...parse_code) {
        String message_as_string = new String(message);
        int last_ocurrence = message_as_string.length();
        StatusServer status = StatusServer.UNKNOWN;
        long code = this.superEasy.getHeader();
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
    public SuperEasy build(String message){
        this.superEasy.setPayloadSend(ByteBuffer.allocate(this.superEasy
                .getPayloadSend().length + message.getBytes().length)
                .put(this.superEasy.getPayloadSend())
                .put(message.getBytes()).array());
        return this.superEasy;
    }
    public SuperEasy build(String... message){
        StringBuilder whole = new StringBuilder();
        for (String temp: message) whole.append(" ").append(temp);
        this.superEasy.setPayloadSend(
                ByteBuffer.allocate(this.superEasy.getPayloadSend().length +
                whole.toString().trim().getBytes().length)
                .put(this.superEasy.getPayloadSend()).put(whole.toString().trim()
                        .getBytes()).array());
        return this.superEasy;
    }
    public SuperEasy build(byte[]... message){
        byte[] whole = new byte[0];
        for (byte[] temp: message) {
            whole = add(whole, temp);
        }
        this.superEasy.setPayloadSend(whole);
        return this.superEasy;
    }
    public SuperEasy build(boolean not_trim, String... message){
        if (!not_trim) this.build(message);
        else {
            StringBuilder whole = new StringBuilder();
            for (String temp : message) whole.append(temp).append(" ");
            this.superEasy.setPayloadSend(ByteBuffer.allocate(
                    this.superEasy.getPayloadSend().length +
                    whole.toString().getBytes().length)
                    .put(this.superEasy.getPayloadSend()).put(whole.toString().getBytes()).array());
        }
        return this.superEasy;
    }
    public SuperEasy build(byte arg){
        this.superEasy.setPayloadSend(ByteBuffer.allocate(
                this.superEasy.getPayloadSend().length + Byte.BYTES)
                    .put(this.superEasy.getPayloadSend()).put(arg).array());
        return this.superEasy;
    }
    public SuperEasy build(int arg){
        this.superEasy.setPayloadSend(ByteBuffer.allocate(
                this.superEasy.getPayloadSend().length + Integer.BYTES)
                    .put(this.superEasy.getPayloadSend()).putInt(arg).array());
        return this.superEasy;
    }
    public SuperEasy build(long arg){
        this.superEasy.setPayloadSend(ByteBuffer.allocate(
                this.superEasy.getPayloadSend().length + Long.BYTES)
                    .put(this.superEasy.getPayloadSend()).putLong(arg).array());
        return this.superEasy;
    }
    public SuperEasy build(byte[] payload){
        this.superEasy.setPayloadSend(ByteBuffer.allocate(
                this.superEasy.getPayloadSend().length + payload.length)
                    .put(payload).array());
        return this.superEasy;
    }
    public SuperEasy build(int header_value, byte[] content){
        this.superEasy.setHeader(header_value);
        this.superEasy.setPayloadSend(
                ByteBuffer.allocate(this.superEasy.getPayloadSend().length + content.length)
                    .put(content).array());
        return this.superEasy;
    }
    public StatusInfo buildLongCode(){
        this.buildStatusInfo();
        if (this.superEasy.getPayloadReceived().length > Long.BYTES)
            this.status_info.setCode((int)ByteBuffer.wrap(
                    this.superEasy.getPayloadReceived(),
                    this.superEasy.getPayloadReceived().length-Long.BYTES,
                    Long.BYTES)
                    .order(ByteOrder.BIG_ENDIAN).getLong());
        return this.status_info;
    }
    public SuperEasy buildStatusInfo(String status_msg, int status_code, String message){
        this.status_info = new StatusInfo(StatusServer.valueOf(status_msg),
                status_code,
                message);
        return this.superEasy;
    }
    public SuperEasy buildStatusInfo(StatusServer status, int status_code, String message){
        this.status_info = new StatusInfo(status,
                status_code,
                message);
        return this.superEasy;
    }
    public SuperEasy buildStatusInfo(StatusServer status, String message){
        this.status_info = new StatusInfo(status,
                status.ordinal(),
                message);
        return this.superEasy;
    }
    public StatusInfo buildStatusInfo(boolean...parse_code){
        if (this.superEasy.getPayloadReceived() == null) return this.getStatusInfo();
        this.buildStatusInfo(this.superEasy.getPayloadReceived(),parse_code);
        return this.status_info;
    }
    public StatusInfo getStatusInfo(){
        if (this.status_info != null)
            return this.status_info;
        else if (this.superEasy.getClient() == null)
            return StatusInfo.build("Client hasnt been created yet, did you called connect?");
        else if (!this.superEasy.getClient().isConnect())
            return StatusInfo.build("Client isnt connected...");
        else if (!this.superEasy.getClient().getSocket().isConnected())
            return StatusInfo.build("Socket isnt connected...");
        else if (this.superEasy.getClient().getSocket().isClosed())
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
