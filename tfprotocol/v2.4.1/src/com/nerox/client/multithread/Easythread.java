package com.nerox.client.multithread;

import com.nerox.client.TFExceptions;

import java.lang.reflect.Method;

/** Handler for multi thread tasks*/
public final class Easythread extends Thread {
    private volatile boolean finished;
    private Method method;
    private Object instance;
    private Object[]args;
    final Object mutex;
    private static Easythread show;
    private boolean silence_thread_errors;
    /**First step preparation for the thread...
     * @param mutex The mutex that is going to be informed when the method execution reach its end...
     * @param method The method who is going to be used asynchronous...*/
    public Easythread(Object mutex, Method method) {
        this.method = method;
        this.mutex = mutex;
    }
    /**Second step preparation for the thread setup the instance where the method it is declared along with its
     * arguments...
     * @param instance The instance where the method to be called it is declared...
     * @param args Arguments to be passed to the method when called, must be set at the exactly order required by
     * the method*/
    public void setup(Object instance, Object...args){
        this.instance = instance;
        this.args = args;
    }
    /**Run method runs the method asynchronously but why dont you use start() method instead?*/
    @Override
    public void run(){
        try {
            this.method.invoke(this.instance, this.args);
        } catch (Exception e) {
            if (!this.silence_thread_errors)
                throw new TFExceptions(e,
                        Thread.currentThread().getName()+ "\n"
                                + Thread.currentThread().getId() + "\n"
                                + this.method.getName());
        }finally {
            this.finished = true;
            synchronized (this.mutex) {
                this.mutex.notifyAll();
            }
        }
        super.run();
    }
    @Override
    public void interrupt() {
        this.silence_thread_errors = true;
        super.interrupt();
    }
    public boolean is_finished(){
        return this.finished;
    }
    public void set_show(){
        Easythread.show = this;
    }
    public static void notify_single_mutex(){
        if (Easythread.show.mutex != null)
            synchronized (Easythread.show.mutex){
                Easythread.show.mutex.notifyAll();
            }
    }
}
