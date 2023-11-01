package dev.godjango.cli.types;

import java.util.Arrays;

public class SpeciaList<S> {
    private String[] array;
    private S[] slaveArray;

    public SpeciaList(int size){
        this.array = new String[size];
        bleach();
    }

    public SpeciaList(String[] array){
        this.array = array;
        bleach();
    }

    public SpeciaList(String[] array, S[] slaveArray){
        if (array.length != slaveArray.length) System.out.println("Unmatched");
        this.array = array;
        this.slaveArray = slaveArray;
        bleach();
    }

    public String get(int index){
        assert (this.array.length - 1 == index);
        return this.array[index];
    }

    public int find(String key){
        int result = 0;
        for (String t: this.array) {
            if (t != null && key.equals(t)) return result;
            result ++;
        }
        return -1;
    }
    public String getNextAsArgument(String key){
        int argIndex;
        argIndex = this.find(key.clone().prepend(String.convert("--")));
        if (argIndex == -1) argIndex = this.find(key.clone().getValueAt(0).prepend(String.convert("-")));
        if (argIndex == -1) return null;
        String result = this.get(argIndex + 1);
        this.remove(argIndex);
        this.remove(argIndex);
        return result;
    }
    public String getNext(String key){
        int result = 0;
        for (String t: this.array) {
            if (t != null && key.equals(t)) return array[result+1];
            result ++;
        }
        return null;
    }

    public String compress(){
        String result = new String();
        for(String string: this.array){
            result.append(string, " ");
        }
        result.eatLast();
        return result;
    }

    public void swap(int old, int newPos){
        String t = array[old];
        array[old] = array[newPos];
        array[newPos] = t;
        if(slaveArray != null) {
            S temp = this.slaveArray[old];
            this.slaveArray[old] = this.slaveArray[newPos];
            this.slaveArray[newPos] = temp;
        }
    }

    public void bleach(){
        for(int i = 0; i < this.array.length; i++){
            if (this.array[i]==null) remove(i);
        }
    }
    public S getSlave(String index){
        int cursor = 0;
        for (String string: this.array){
            if(string.equals(index)) return this.slaveArray[cursor];
            cursor++;
        }
        return this.slaveArray[cursor];
    }
    public void insert(String string){
        String[] temp = new String[this.array.length + 1];
        System.arraycopy(array, 0, temp, 0, this.array.length);
        temp[array.length] = string;
        this.array = temp;
    }
    public void insert(String string, int to){
        String[] temp = new String[this.array.length + 1];
        System.arraycopy(array, 0, temp, 0, to);
        temp[to] = string;
        System.arraycopy(array, to + 1, temp, to + 1, array.length - to);
        this.array = temp;
    }

    public java.lang.String toString(){
        return Arrays.toString(this.array);
    }

    public void remove(String string){
        for(int i = 0; i < array.length; i++){
            if(array[i].equals(string)) remove(i);
        }
    }
    public void removeNextTo(String string){
        for(int i = 0; i < array.length; i++){
            if(array[i].equals(string)) remove(i + 1);
        }
    }
    public void removeRange(int from, int to){
        for(; from < to; from++){
            remove(from);
        }
    }
    public void remove(int index){
        String[] temp = new String[this.array.length - 1];
        System.arraycopy(array, 0, temp, 0, index);
        System.arraycopy(array, index + 1, temp, index, array.length - 1 - index);
        this.array = temp;
        if (slaveArray != null){
            nullSlaveAt(index);
        }
    }
    public void nullSlaveAt(int index){
        S[] temp = (S[])(new Object[this.slaveArray.length - 1]);
        System.arraycopy(slaveArray, 0, temp, 0, index);
        System.arraycopy(slaveArray, index + 1, temp, index, slaveArray.length - 1 - index);
        this.slaveArray = temp;
    }
}
