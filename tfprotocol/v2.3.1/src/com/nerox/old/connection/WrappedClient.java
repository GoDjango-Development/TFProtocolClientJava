package com.nerox.old.connection;

public final class WrappedClient{
    private Easyreum easyreum;
    private WrappedClient(){
    }
    public static WrappedClient use(Easyreum easyreum){
        WrappedClient wrappedClient = new WrappedClient();
        wrappedClient.setEasyConnection(easyreum);
        return wrappedClient;
    }
    private void setEasyConnection(Easyreum easyreum){
        this.easyreum = easyreum;
    }
    public final void standard_write(byte[] message) {
        this.easyreum.getBuilder().build(message).send();
    }
    public final void raw_write(byte[] message) {
        this.easyreum.getBuilder().build(message).sendJust();
    }
    public final byte[] raw_read(byte[] buffer) {
        return this.easyreum.receiveBuffer(buffer);
    }
    public final byte[] standard_read(int hdr_size, int buffer_size) {
        this.easyreum.setHeaderSize(hdr_size);
        this.easyreum.setBuffer(buffer_size);
        this.easyreum.receive();
        return this.easyreum.getPayloadReceived();
    }
}
