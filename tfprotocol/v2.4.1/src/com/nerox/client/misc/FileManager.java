package com.nerox.client.misc;

import com.nerox.client.TFExceptions;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

public class FileManager {
    private FileManager(){
        // Not should be instantiated
    }
    public static byte[] ReadWholeFile(RandomAccessFile file) {
        int read = 0;
        ByteBuffer content = ByteBuffer.allocate(4096);
        byte[] buffer = new byte[4096];
        try{
            while (true){
                int t = file.read(buffer);
                if (t == -1)
                    break;
                read += t;
                if (read >= content.capacity())
                    content.limit(read);
                content.put(buffer);
            }
        }catch (Exception ex){
            throw new TFExceptions(ex, "Cannot read the file");
        }
        return content.array();
    }
}