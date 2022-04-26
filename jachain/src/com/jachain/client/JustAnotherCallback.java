package com.jachain.client;

import com.nerox.client.callbacks.IXSAceCallback;
import com.nerox.client.constants.XSACEConsts;
import com.nerox.client.misc.StatusInfo;
import com.nerox.client.modules.XSAce;

import java.util.ArrayList;

public final class JustAnotherCallback implements IXSAceCallback {
    static final String queryLimiter = "TF-RESULT: 400\n";
    private boolean isListening = false;
    private Object lastResult;
    private boolean isRunning = true;
    private final Object mutex = new Object();
    private final ArrayList<String> outPocket = new ArrayList<>();
    private final ArrayList<String> incomingPocket = new ArrayList<>();
    private Object awaitingMutex;

    void clearIncomingPocket(){
        this.incomingPocket.clear();
    }
    void releaseAllThreads(){
        this.isRunning = false;
        this.isListening = false;
        synchronized (this.mutex){
            this.mutex.notifyAll();
        }
        synchronized (this.awaitingMutex){
            this.awaitingMutex.notifyAll();
        }
    }
    private void saveInPocket(String result){
        this.incomingPocket.add(result);
    }
    public void putInOutPocket(String coin){
        this.outPocket.add(coin);
        synchronized (this.mutex){
            this.mutex.notifyAll();
        }
    }
    public boolean isRunning(){
        return this.isRunning;
    }
    public void setWaitingMutex(final Object mutex){
        this.awaitingMutex = mutex;
    }
    Object getWaitingMutex(){
        return this.awaitingMutex;
    }
    public String retrieveFromPocket(){
        try {
            while (!this.incomingPocket.contains(queryLimiter) && this.isRunning){
                synchronized (this.awaitingMutex){
                    this.awaitingMutex.wait();
                }
            }
        } catch (InterruptedException ignored) {}
        //System.out.println("Released from JAAction");
        if (this.isRunning && this.incomingPocket.size() > 0){
            String res = "";
            while (!this.incomingPocket.get(0).equals(queryLimiter)){
                res = res.concat(this.incomingPocket.get(0).replace(">> ",""));
                this.incomingPocket.remove(0);
            }
            this.incomingPocket.remove(0);
            return res;
        }
        return "";
    }


    public Object getLastResult() {
        return lastResult;
    }

    @Override
    public void startACECallback(StatusInfo statusInfo) {
        this.statusServer(statusInfo);
    }

    @Override
    public void inskeyCallback(StatusInfo statusInfo) {
        this.statusServer(statusInfo);
    }

    @Override
    public void exitCallback(StatusInfo exit) {
        statusServer(exit);
    }
    @Override
    public void runWriteNNLCallback(XSAce.Communication communication) {
        while (isRunning){
            try {
                synchronized (this.mutex){
                    this.mutex.wait();
                }
                if (!isRunning) {
                    break;
                }
                while(this.outPocket.size() > 0){
                    String nextCommand = this.outPocket.remove(0);
                    communication.send(nextCommand.getBytes());
                    if (nextCommand.equals("quit")) {
                        break;
                    }

                }
            } catch (InterruptedException ignored) {}
        }
    }

    @Override
    public void setArgsCallback(StatusInfo buildStatusInfo) {}

    @Override
    public void runReadNLCallback(StatusInfo statusInfo) {
        if (statusInfo.getCode() == XSACEConsts.Commands.OK)
            synchronized (this.awaitingMutex){
                this.awaitingMutex.notifyAll();
                return;
            }
        else if (statusInfo.getCode() == XSACEConsts.Commands.FINISHED ||
                statusInfo.getCode() == XSACEConsts.Commands.ERROR
        ) {
            this.releaseAllThreads();
        }
        else if (this.isRunning){
            if (statusInfo.getMessage() == null || statusInfo.getMessage().isEmpty())
                return;
            if (this.isListening)
                this.saveInPocket(statusInfo.getMessage());
            if (statusInfo.getMessage().equals(queryLimiter)){
                if (!this.isListening){
                    this.isListening = true;
                }
                synchronized (this.awaitingMutex){
                    this.awaitingMutex.notifyAll();
                }
                System.gc();
                System.runFinalization();
            }
        }
    }

    @Override
    public void statusServer(StatusInfo statusInfo) {
        this.lastResult = statusInfo;
    }

    @Override
    public void responseServerCallback(StatusInfo statusInfo) {
        this.statusServer(statusInfo);
    }

    @Override
    public void instanceTfProtocol(XSAce xsAce) {}
}
