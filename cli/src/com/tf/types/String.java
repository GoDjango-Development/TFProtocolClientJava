package com.tf.types;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class String {
    private byte[] bytes;
    public String(){
        this.bytes = new byte[0];
    }
    public String(java.lang.String string){
        this.bytes = string.getBytes(StandardCharsets.UTF_8);
    }
    public String(String string){
        this.bytes = string.bytes.clone();
    }
    public String(byte[] value){
        this.bytes = value;
    }
    public String(byte value){
        this.bytes = new byte[1];
        this.bytes[0] = value;
    }
    public static String convert(java.lang.String string){
        return new String(string);
    }
    public static String convert(Object target){
        return new String(target.toString());
    }
    public java.lang.String toString(){
        return new java.lang.String(this.bytes);
    }
    public String clone(){
        return new String(this);
    }
    public byte[] toBytes (){
        return this.bytes.clone();
    }
    public int parseToInteger(){
        return Integer.parseInt(new java.lang.String(this.bytes));
    }
    public long parseToLong(){
        return Long.parseLong(new java.lang.String(this.bytes));
    }

    public void set(byte[] value){
        this.bytes = value;
    }
    public void set(String value){
        this.bytes = value.bytes;
    }
    public int getLength(){
        return this.bytes.length;
    }
    public char getCharAt(int index){
        return (char)this.bytes[index];
    }
    public String getValueAt(int index){
        return new String(this.bytes[index]);
    }
    // Operation multiply
    public String mult(int val){
        if (val == 0) return new String();
        this.bytes = Arrays.copyOf(this.bytes, this.bytes.length * val);
        for(int i = this.bytes.length/val; i < this.bytes.length; i++){
            this.bytes[i] = this.bytes[i%(this.bytes.length/val)];
        }
        return this;
    }
    // Operation add
    public String prepend(String element){
        byte[] result = new byte[this.bytes.length + element.bytes.length];
        System.arraycopy(element.bytes, 0, result, 0, element.bytes.length);
        System.arraycopy(this.bytes, 0, result, element.getLength(), this.bytes.length);
        this.bytes = result;
        return this;
    }
    public String prepend(java.lang.String element){
        return this.prepend(new String(element));
    }
    public String append(byte[] second){
        byte[] result = new byte[this.bytes.length + second.length];
        System.arraycopy(this.bytes, 0, result, 0, this.bytes.length);
        System.arraycopy(second, 0, result, this.bytes.length, second.length);
        this.set(result);
        return this;
    }
    public String append(String second){
        this.append(second.bytes);
        return this;
    }
    public String append(java.lang.String second){
        return this.append(String.convert(second).bytes);
    }
    public String append(String second, java.lang.String separator){
        if (second == null) return this;
        if (this.bytes.length > 0) this.append(convert(separator));
        this.append(second.bytes);
        return this;
    }
    public String trimEnd(){
        while (this.bytes[this.bytes.length-1] == " ".getBytes()[0]){
            eatLast();
        }
        return this;
    }
    public String trimStart(){
        while (this.bytes[0] == " ".getBytes()[0]){
            eatBegin();
        }
        return this;
    }
    public String eatLast(){
        System.arraycopy(this.bytes, 0, this.bytes, 0, this.bytes.length-1);
        return this;
    }
    public String eatBegin(){
        System.arraycopy(this.bytes, 0, this.bytes, 1, this.bytes.length-1);
        return this;
    }
    public char getLastChar(){
        return (char) this.bytes[this.bytes.length -1];
    }
    public java.lang.String getLastCharAsString(){
        return "" + (char)this.bytes[this.bytes.length -1];
    }
    // Operation equals
    public boolean equals(String value){
        return Arrays.equals(this.bytes, value.bytes);
    }
    public boolean equals(java.lang.String value){
        return new String(value).equals(this);
    }
    public boolean equals(Object value){
        return String.convert(value).equals(this);
    }
    public int hashCode(){
        return Arrays.hashCode(this.bytes);
    }
}
