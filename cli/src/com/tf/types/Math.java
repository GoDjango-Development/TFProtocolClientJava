package com.tf.types;

public class Math {
    public static int toDecimalBase(int number){
        int i = 0;
        while (number >= 10){
            number /= 10;
            i++;
        }
        return power(10, i);
    }
    public static int power(int base, int power){
        if (power == 0) base = 1;
        while (--power > 0) base*=base;
        return base;
    }
}
