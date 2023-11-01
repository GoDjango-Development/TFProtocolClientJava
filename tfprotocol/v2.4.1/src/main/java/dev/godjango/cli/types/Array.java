package dev.godjango.cli.types;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class Array{
    private final int[] array;
    public Array(int[] param){
        this.array = param;
    }
    public static String[] toArrayOfString(java.lang.Object[] array){
        String[] result = new String[array.length];
        int i = 0;
        for (java.lang.Object o: array){
            if(o == null) continue;
            result[i++] = new String(o.toString());
        }
        return result;
    }
    public static Object[] remove(int index, Object[] array){
        System.arraycopy(array, 0, array, index, index);
        System.arraycopy(array, index+1, array, index, array.length-1);
        return Arrays.copyOf(array, array.length - 1);
    }

    public static byte[] readLine(byte[] payload, int index, int length){
        byte[] temp = new byte[payload.length];
        int read = 0;
        int lines = 0;
        for(byte b: payload){
            if(b != System.lineSeparator().getBytes()[0]){
                if (lines == (length + index)) break;
                if(lines < index) continue;
                temp[read] = b;
                read++;
            }
            else{
                lines++;
            }
        }
        return Arrays.copyOfRange(temp, 0, read);
    }
    public static byte[] readAllBytes(InputStream is) throws IOException {
        byte[] res = new byte[0];
        int read;
        while(is.available() > 0){
            byte[] buffer = new byte[2048];
            read = is.read(buffer);
            res = Arrays.copyOf(res, res.length + read);
            if (read >= 0)
                System.arraycopy(buffer, 0, res, res.length - read, read);
        }
        return res;
    }
}
