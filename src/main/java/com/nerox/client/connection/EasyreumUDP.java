package com.nerox.client.connection;

import com.nerox.client.TFExceptions;
import com.nerox.client.payload.Easybuild;

import java.io.IOException;
import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public final class EasyreumUDP extends SuperEasy{
    private final Easybuild builder;

    public EasyreumUDP(Client client){
        this.setClient(client);
        builder = new Easybuild(this);
    }

    @Override
    protected void writeHeader() {
        this.exceptionGuard();
        try {
            DatagramPacket packet = new DatagramPacket(this.header_buffer_write,
                    0, this.header_buffer_write.length);
            packet.setAddress(this.client.getSocket().getInetAddress());
            packet.setPort(this.client.getSocket().getPort());
            this.client.getUDPSocket().send(packet);
        } catch (IOException e) {
            throw new TFExceptions(e);
        }
    }

    @Override
    protected void writeBody() {
        this.exceptionGuard();
        try {
            DatagramPacket packet = new DatagramPacket(this.payload_buffer_send,
                    0, this.payload_buffer_send.length);
            packet.setAddress(this.client.getSocket().getInetAddress());
            packet.setPort(this.client.getSocket().getPort());
            this.client.getUDPSocket().send(packet);
        } catch (IOException e) {
            throw new TFExceptions(e);
        }
    }

    @Override
    protected void readHeader() {
        this.exceptionGuard();
        try {
            DatagramPacket packet = new DatagramPacket(this.header_buffer_read,
                    0, this.header_buffer_read.length);
            packet.setAddress(this.client.getSocket().getInetAddress());
            packet.setPort(this.client.getSocket().getPort());
            this.client.getUDPSocket().receive(packet);
            System.out.println(Arrays.toString(packet.getData()));
            this.header = ByteBuffer.wrap(this.header_buffer_read).order(ByteOrder.BIG_ENDIAN).getInt();
        } catch (IOException e) {
            throw new TFExceptions(e);
        }
    }

    @Override
    protected void readBody() {
        this.exceptionGuard();
        try {
            while (true){
                DatagramPacket packet = new DatagramPacket(this.payload_buffer_recv,0, this.header);
                packet.setAddress(this.client.getSocket().getInetAddress());
                packet.setPort(this.client.getSocket().getPort());
                this.client.getUDPSocket().receive(packet);
                if (packet.getLength() == this.header) break;
            }
        } catch (IOException e) {
            throw new TFExceptions(e);
        }
    }
    public byte[] receiveBuffer(int buffer){
        try {
            byte[] bufferArray = new byte[buffer];
            DatagramPacket packet = new DatagramPacket(bufferArray,
                    0, buffer);
            packet.setAddress(this.client.getSocket().getInetAddress());
            packet.setPort(this.client.getSocket().getPort());
            this.client.getUDPSocket().receive(packet);
            System.out.println(Arrays.toString(packet.getData()));
            return bufferArray;
        } catch (IOException e) {
            throw new TFExceptions(e);
        }
    }
    public byte[] receiveBuffer(byte[] buffer){
        return this.decryptXor(buffer);
    }
    public byte[] receiveBuffer(byte[] buffer, boolean not_encrypt){
        return this.decryptXor(buffer);
    }
    @Override
    public Object getSocket() {
        return this.client.getUDPSocket();
    }
}
