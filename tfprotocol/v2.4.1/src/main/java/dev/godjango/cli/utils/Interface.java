package dev.godjango.cli.utils;

public class Interface {
    public static void print(Object... values){
        System.out.println("---->");
        for(Object o: values){
            System.out.println(o.toString().concat("\n"));
        }
        System.out.println("<----");
    }
    public static void print(byte[] values){
        System.out.println(new dev.godjango.cli.types.String("-").mult(values.length).append(">"));
        System.out.println(new String(values));
        System.out.println(new dev.godjango.cli.types.String("-").mult(values.length).prepend("<"));
    }
}
