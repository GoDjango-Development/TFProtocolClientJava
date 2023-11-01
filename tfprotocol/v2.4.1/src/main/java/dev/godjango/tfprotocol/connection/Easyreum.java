package dev.godjango.tfprotocol.connection;

import static dev.godjango.tfprotocol.constants.TfprotocolConsts.PutGetCommand.HPFCANCEL;
import static dev.godjango.tfprotocol.constants.TfprotocolConsts.PutGetCommand.HPFFIN;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

import dev.godjango.tfprotocol.TFExceptions;
import dev.godjango.tfprotocol.Tfprotocol;
import dev.godjango.tfprotocol.callbacks.ISuperCallback;
import dev.godjango.tfprotocol.misc.StatusInfo;
import dev.godjango.tfprotocol.misc.StatusServer;
import dev.godjango.tfprotocol.payload.Easybuild;
import dev.godjango.tfprotocol.security.Cryptography;

public final class Easyreum extends SuperEasy{

    private int header;
    private int buffer;
    private final int std_len_channel = 512*1024;
    private int len_channel = 512*1024;
    private byte[] payload_buffer_send = new byte[0];
    private byte[] payload_buffer_recv = new byte[0];
    private byte[] header_buffer_write = new byte[4];
    private byte[] header_buffer_read = new byte[4];

    private final Easybuild builder; // Caution not to change name for this variable without change reset() function
    private boolean use_custom_buffer = false;
    private boolean use_custom_header = false;
    private Object dummy;
    private boolean debug = false;
    private Object dummy_state;
    private Client client; // Caution not to change name for this variable without change reset() function
    private Cryptography.Xor xorOutput; // Caution not to change name for this variable without change reset() function
    private Cryptography.Xor xorInput; // Caution not to change name for this variable without change reset() function
    // Constructors
    public Easyreum(){
        this.builder = new Easybuild(this);}

    public Easyreum(Client client, byte[] sessionKey) {
        this.client = client;
        this.setSessionKey(sessionKey);
        this.builder = new Easybuild(this);
    }
    public Easyreum(Client client) {
        this.client = client;
        this.builder = new Easybuild(this);
    }
    protected byte[] decryptXor(byte[] payload){
        if (this.xorInput != null){
            this.xorInput.decrypt(payload);
        }
        return payload;
    }
    protected byte[] encryptXor(byte[] payload){
        if (this.xorOutput != null){
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
    protected synchronized void writeHeader(){
        this.exceptionGuard();
        if (this.debug){
            System.out.println("Will write to socket(length): " + this.payload_buffer_send.length);
            System.out.println("Header capacity: " + this.header_buffer_read.length);
        }
        ByteBuffer bb = ByteBuffer.wrap(this.header_buffer_write).order(ByteOrder.BIG_ENDIAN);
        switch (header_buffer_write.length){
            case Integer.BYTES:
                bb.putInt(this.use_custom_header?this.header:this.payload_buffer_send.length);
                break;
            case Long.BYTES:
                bb.putLong(this.use_custom_header?this.header:this.payload_buffer_send.length);
                break;
            default:
                break;
        }
        try {
            this.client.dataOutputStream.write(this.encryptXor(bb.array()));
        } catch (IOException e) {
            throw new TFExceptions(e);
        }
        if (this.debug){
            System.out.println("Wrote to socket(length): " + this.payload_buffer_send.length);
        }
    }
    protected synchronized void writeBody() {
        this.exceptionGuard();
        if (this.debug){
            System.out.println("Will write to socket: " + new String(this.payload_buffer_send));
        }
        try {
            this.client.dataOutputStream.write(this.encryptXor(this.payload_buffer_send));
        } catch (IOException e) {
            throw new TFExceptions(TFExceptions.ErrorCodes.ON_WRITE_OR_RECEIVE_TO_SOCKET.ordinal(),
                    "Cannot succesfully write to socket");
        }
        if (this.debug){
            System.out.println("Wrote to socket: " + new String(this.payload_buffer_send));
        }
        this.clear();
    }
    protected void readHeader(){
        this.exceptionGuard();
        if (this.debug){
            System.out.println("Will read from socket(length): " + this.header_buffer_read.length);
        }
        ByteBuffer temp = ByteBuffer.allocate(this.header_buffer_read.length);
        int bytes = 0;
        while (bytes < this.header_buffer_read.length) {
            int read = 0;
            try {
                read = this.client.inputStream.read(this.header_buffer_read, 0,
                        this.header_buffer_read.length - bytes);
            } catch (IOException e) {
                throw new TFExceptions(e, "Cannot read from socket ...");
            }
            if (read >= 0)
                temp.put(this.header_buffer_read, 0, read);
            else{
                break;
            }
            bytes += read;
        }
        temp = ByteBuffer.wrap(decryptXor(temp.array()));
        switch (this.header_buffer_read.length){
            case Long.BYTES:
                this.header = (int)temp.getLong();
                break;
            case Integer.BYTES:
                this.header = temp.getInt();
                break;
            default:
                break;
        }
        if (this.debug){
            System.out.println("Read from socket(length): " + this.header_buffer_read.length);
            System.out.println("Read from socket: " + this.header);
        }
    }
    protected void readBody() {
        this.exceptionGuard();
        if (this.debug){
            System.out.println("Will read from socket(length): " + this.header);
        }
        byte[] buffer = new byte[this.use_custom_buffer ? this.buffer: this.len_channel];
        ByteBuffer container;
        try {
            container = ByteBuffer.allocate(Math.max(this.header,0));
        }catch (OutOfMemoryError heapSpace){
            throw new TFExceptions(TFExceptions.ErrorCodes.ON_WRITE_OR_RECEIVE_TO_SOCKET.ordinal()
                    ,"Heap space not enough server answer " +
                    "with a very high header number"
                    + "\nheader: " + this.header);
        }
        int bytes = 0;
        while (bytes < this.header) {
            int read = 0;
            try {
                read = this.client.inputStream.read(buffer, 0, this.header - bytes);
            } catch (IOException e) {
                throw new TFExceptions(e, "Cannot read from socket ...");
            }
            container.put(buffer,0,read);
            bytes += read;
        }
        this.payload_buffer_recv = container.array();
        this.decryptXor(this.payload_buffer_recv);
        if (this.debug){
            System.out.println("Read from socket(length): " + this.header);
            System.out.println("Read from socket: " + new String(this.payload_buffer_recv));
            this.debug = false;
        }
    }
    public Easyreum restart(Client client){
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
    public Easyreum reset(){
        Easyreum temp = new Easyreum();
        for (Field field: this.getClass().getDeclaredFields()){
            try {
                if (field.getName().equals("client") ||
                        field.getName().equals("builder") ||
                        field.getName().equals("xorInput") ||
                        field.getName().equals("xorOutput")) continue;
                this.getClass().getDeclaredField(field.getName()).set(this,
                        this.getClass().getDeclaredField(field.getName()).get(temp));
            } catch (IllegalAccessException | NoSuchFieldException ignored) { }
        }
        return this;
    }

    public synchronized Easyreum translate(byte[] message) {
        this.payload_buffer_send = message;
        this.writeHeader();
        this.writeBody();
        this.readHeader();
        this.readBody();
        return this;
    }
    public synchronized Easyreum translate(String message) {
        return this.translate(message.getBytes());
    }
    public synchronized Easyreum translate(){
        this.writeHeader();
        this.writeBody();
        this.readHeader();
        this.readBody();
        return this;
    }
    public synchronized Easyreum translate(boolean use_custom_header){
        this.use_custom_header = use_custom_header;
        return translate();
    }
    public synchronized Easyreum send() {
        this.writeHeader();
        this.writeBody();
        return this;
    }
    public synchronized Easyreum receive(){
        this.readHeader();
        this.readBody();
        return this;
    }
    public synchronized void sendJust(){
        this.writeBody();
    }
    public byte[] receiveBuffer(int buffer){
        byte[] rcv = new byte[buffer];
        try {
            int read = 0;
            while
            ((read += this.client.getSocket().getInputStream().read(rcv, read, rcv.length - read) )< buffer);

        } catch (IOException exception) {
            throw new TFExceptions(TFExceptions.ErrorCodes.ON_WRITE_OR_RECEIVE_TO_SOCKET.ordinal(),
                    "Cannot succesfully receive data from socket");
        }
        return this.decryptXor(rcv);
    }
    public byte[] receiveBuffer(byte[] buffer){
        try {
            this.client.getSocket().getInputStream().read(buffer);
        } catch (IOException exception) {
            throw new TFExceptions(TFExceptions.ErrorCodes.ON_WRITE_OR_RECEIVE_TO_SOCKET.ordinal(),
                    "Cannot succesfully receive data from socket");
        }
        return this.decryptXor(buffer);
    }
    public byte[] receiveBuffer(byte[] buffer, boolean not_encrypt){
        try {
            this.client.getSocket().getInputStream().read(buffer);
        } catch (IOException exception) {
            throw new TFExceptions(TFExceptions.ErrorCodes.ON_WRITE_OR_RECEIVE_TO_SOCKET.ordinal(),
                    "Cannot succesfully receive data from socket");
        }
        if (not_encrypt) return buffer;
        return this.decryptXor(buffer);
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
    public Easyreum debug(){
        this.debug = !this.debug;
        return this;
    }
    public Easyreum receiveOptimized(Method method, ISuperCallback callback) throws IOException,
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
    public Easyreum useCustomBuffer(boolean use_custom_buffer){
        this.use_custom_buffer = use_custom_buffer;
        return this;
    }
    public Easyreum usePermanentCustBuf(int buffer){
        if (buffer > 0) this.len_channel = buffer;
        else this.len_channel = this.std_len_channel;
        return this;
    }
    public Easyreum useCustomHeader(boolean use_custom_header){
        this.use_custom_header = use_custom_header;
        return this;
    }
    public Easyreum clear(){
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
    public Socket getSocket(){
        return this.client.getSocket();
    }
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
}