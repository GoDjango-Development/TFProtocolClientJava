package com.nerox.client.connection;

import com.nerox.client.TFExceptions;
import com.nerox.client.Tfprotocol;
import com.nerox.client.callbacks.ISuperCallback;
import com.nerox.client.misc.StatusInfo;
import com.nerox.client.misc.StatusServer;
import com.nerox.client.payload.Easybuild;
import com.nerox.client.security.Cryptography;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;

import static com.nerox.client.constants.TfprotocolConsts.PutGetCommand.HPFCANCEL;
import static com.nerox.client.constants.TfprotocolConsts.PutGetCommand.HPFFIN;

public abstract class SuperEasy {
    protected int header;
    protected int buffer;
    protected final int std_len_channel = 512*1024;
    protected int len_channel = 512*1024;
    protected byte[] payload_buffer_send = new byte[0];
    protected byte[] payload_buffer_recv = new byte[0];
    protected byte[] header_buffer_write = new byte[4];
    protected byte[] header_buffer_read = new byte[4];

    protected final Easybuild builder;
    protected boolean use_custom_buffer = false;
    protected boolean use_custom_header = false;
    protected Object dummy;
    protected Object dummy_state;
    protected Client client;
    protected Cryptography.Xor xorOutput;
    protected Cryptography.Xor xorInput;

    // Constructors
    public SuperEasy(){
        this.builder = new Easybuild(this);}

    public SuperEasy(Client client, byte[] sessionKey) {
        this.client = client;
        this.setSessionKey(sessionKey);
        this.builder = new Easybuild(this);
    }

    public SuperEasy(Client client) {
        this.client = client;
        this.builder = new Easybuild(this);
    }
    protected byte[] decryptXor(byte[] payload){
        if (this.xorInput != null){
            //System.out.println("Decrypting");
            this.xorInput.decrypt(payload);
        }
        return payload;
    }
    protected byte[] encryptXor(byte[] payload){
        if (this.xorOutput != null){
            //System.out.println("Encrypting");
            xorOutput.encrypt(payload);
        }
        return payload;
    }
    protected void exceptionGuard(){
        if (this.client == null || this.client.getSocket().isClosed()
                || !this.client.getSocket().isConnected())
        {
            throw new TFExceptions(TFExceptions.ErrorCodes.ON_WRITE_OR_RECEIVE_TO_SOCKET.ordinal(),
                    "Socket is closed or not connected");
        }
    }
    // Single Thread
    protected abstract void writeHeader();
    protected abstract void writeBody();
    protected abstract void readHeader();
    protected abstract void readBody();

    public SuperEasy restart(Client client){
        if (client.getSocket().isClosed() || !client.getSocket().isConnected())
            throw new TFExceptions(new IOException("Client is not connected"));
        this.client = client;
        return this;
    }
    public void setSessionKey(byte[] sessionKey){
        this.xorInput = new Cryptography.Xor(sessionKey);
        this.xorOutput = new Cryptography.Xor(sessionKey);
    }
    public void setClient(Client client){
        this.client = client;
    }
    public SuperEasy reset(){
        Easyreum temp = new Easyreum();
        for (Field field: this.getClass().getDeclaredFields()){
            try {
                if (field.getName().equals("client") ||
                        field.getName().equals("builder")) continue;
                this.getClass().getDeclaredField(field.getName()).set(this,
                        this.getClass().getDeclaredField(field.getName()).get(temp));
            } catch (IllegalAccessException | NoSuchFieldException ignored) { }
        }
        return this;
    }

    public synchronized SuperEasy translate(byte[] message) {
        this.payload_buffer_send = message;
        this.writeHeader();
        this.writeBody();
        this.readHeader();
        this.readBody();
        return this;
    }
    public synchronized SuperEasy translate(String message) {
        return this.translate(message.getBytes());
    }
    public synchronized SuperEasy translate(){
        this.writeHeader();
        this.writeBody();
        this.readHeader();
        this.readBody();
        return this;
    }
    public synchronized SuperEasy translate(boolean use_custom_header){
        this.use_custom_header = use_custom_header;
        return translate();
    }
    public synchronized SuperEasy send() {
        this.writeHeader();
        this.writeBody();
        return this;
    }
    public synchronized SuperEasy receive(){
        this.readHeader();
        this.readBody();
        return this;
    }
    public synchronized void sendJust(){
        this.writeBody();
    }

    public void receiveUntil(long condition_in_header, ISuperCallback callback
            , String method){
        try {
            Method method_object = callback.getClass().getDeclaredMethod(method, StatusInfo.class);
            while (true){
                this.receiveHeader();
                if (this.header == condition_in_header) break;
                this.receiveBody();
                method_object.invoke(callback,new StatusInfo(StatusServer.OK,
                        this.header,
                        new String(this.payload_buffer_recv)));
            }
        }catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex){
            throw new TFExceptions(ex, TFExceptions.ErrorCodes.UNHANDLED_EXCEPTION.ordinal());
        }
    }
    public void receiveUntil(Method methodToEvalCond, Object mtecObj, ISuperCallback callback
            , String method){
        try {
            if (methodToEvalCond.getReturnType() != Boolean.TYPE)
                throw new TFExceptions(TFExceptions.ErrorCodes.ILLEGAL_ARGUMENTS.ordinal(),
                        "The method you gave me to evaluate header do not return a boolean type object," +
                                "so you must gave me a valid method");
            Method method_object = callback.getClass().getDeclaredMethod(method,StatusInfo.class);
            while (true){
                this.receiveHeader();
                if (methodToEvalCond.invoke(mtecObj, this.header).equals(true)) break;
                this.receiveBody();
                method_object.invoke(callback, new StatusInfo(StatusServer.OK,
                        this.header,
                        new String(this.payload_buffer_recv)
                ).setPayload(this.payload_buffer_recv));
            }
        }catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex){
            throw new TFExceptions(ex, TFExceptions.ErrorCodes.UNHANDLED_EXCEPTION.ordinal());
        }
    }

    public void putReceiveUntil(Method mthd_to_eval_cond, Tfprotocol.Codes codes){

        do {
            this.receiveHeader();
            if (header == HPFCANCEL) {
                codes.rcvSignal = true;
            }else if(header == HPFFIN) break;
        } while (true);
    }

    public byte[] receiveHeader(){
        this.readHeader();
        return this.header_buffer_read;
    }
    public byte[] receiveBody() {
        this.readBody();
        return this.payload_buffer_recv;
    }
    public void setHeaderSize(int header_size){
        this.header_buffer_write = new byte[header_size];
        this.header_buffer_read = new byte[header_size];
    }
    public SuperEasy receiveOptimized(Method method, ISuperCallback callback) throws IOException,
            InvocationTargetException, IllegalAccessException {
        do{
            byte[] buffer = new byte[(int)this.buffer];
            byte[] header = new byte[1];
            byte[] sz = new byte[4];
            byte[] full_header = new byte[header.length+sz.length];
            ByteBuffer payload = null;
            int size = 0;
            if (this.client.inputStream.read(full_header)>0){
                this.decryptXor(full_header);
                if (full_header[0] != 0){
                    size = ByteBuffer.wrap(full_header,1,Integer.BYTES).getInt();
                    if (!(size <= 0)) {
                        int bytes = 0;
                        payload = ByteBuffer.allocate(size);
                        while (bytes < size) {
                            int read = this.client.inputStream
                                    .read(buffer, 0, size - bytes);
                            payload.put(buffer,0,read);
                            bytes += read;
                        }
                        payload = ByteBuffer.wrap(Objects.requireNonNull(this.decryptXor(payload.array())));
                    }
                }
                method.invoke(callback,
                        new StatusInfo(full_header[0],
                                size,
                                (payload!=null)?new String(payload.array()):""));
                if (full_header[0] == 0) return this;
            }else return this;
        }while (true);
    }
    public SuperEasy useCustomBuffer(boolean use_custom_buffer){
        this.use_custom_buffer = use_custom_buffer;
        return this;
    }
    public SuperEasy usePermanentCustBuf(int buffer){
        if (buffer > 0) this.len_channel = buffer;
        else this.len_channel = this.std_len_channel;
        return this;
    }
    public SuperEasy useCustomHeader(boolean use_custom_header){
        this.use_custom_header = use_custom_header;
        return this;
    }
    public SuperEasy clear(){
        this.payload_buffer_send = new byte[0];
        this.use_custom_header = false;
        this.use_custom_buffer = false;
        return this;
    }
    // Public Getters
    public byte[] getSessionKey() {
        return Cryptography.Xor.getStaticSessionKey();
    }
    public int getHeader(){
        return this.header;
    }
    public int getBuffer(){
        return this.buffer;
    }
    public int getBufferAsInt(){
        return this.buffer;
    }
    public byte[] getData(){return this.payload_buffer_recv;}
    public Client getClient(){
        return client;
    }
    public Object getDummy(){
        return this.dummy;
    }
    public Object getDummyState(){
        return this.dummy_state;
    }
    public abstract Object getSocket();
    public byte[] getPayloadSend(){return this.payload_buffer_send;}
    public byte[] getPayloadReceived(){return this.payload_buffer_recv;}
    public Easybuild getBuilder(){return this.builder;}
    // Public Setter
    public void setDummy(Object value){
        this.dummy = value;
    }
    public void setBuffer(int buffer){
        this.buffer = buffer;
    }
    public void setDummyState(Object value) {this.dummy_state = value;}
    public void setPayloadSend(byte[] payload){this.payload_buffer_send = payload;}
    public void setHeader(int header){
        this.header = header;
    }
    // Checkers
    public void validateArgs(Object... args) {
        boolean is_valid = true;
        for (Object arg:args){
            is_valid &= (arg != null && !arg.toString().isEmpty());
        }
        if (!is_valid)
            throw new TFExceptions(TFExceptions.ErrorCodes.ILLEGAL_ARGUMENTS.ordinal(),
                    "INVALID ARGUMENTS");
    }
    public abstract byte[] receiveBuffer(int buffer);
    public abstract byte[] receiveBuffer(byte[] buffer);
    public abstract byte[] receiveBuffer(byte[] buffer, boolean not_encrypt);
}
