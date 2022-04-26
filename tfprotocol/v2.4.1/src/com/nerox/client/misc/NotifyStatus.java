package com.nerox.client.misc;

public class NotifyStatus {
    long indexNumber;
    String file;

    public NotifyStatus(long indexNumber, String file) {
        this.indexNumber = indexNumber;
        this.file = file;
    }

    public long getIndexNumber() {
        return indexNumber;
    }

    public String getFile() {
        return file;
    }
}
