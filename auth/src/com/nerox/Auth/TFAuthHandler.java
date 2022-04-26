package com.nerox.Auth;

import com.nerox.Auth.Errors.TFAuthErrors;
import com.nerox.Auth.Models.Email;
import com.nerox.Auth.Models.Model;
import com.nerox.Auth.Models.Model.DataFields;
import com.nerox.client.modules.ExtendedSub1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class TFAuthHandler {
    private final ExtendedSub1 extended;

    private HashMap<String,Model> customModel = new HashMap<>();
    private final ArrayList<String> authData;
    private boolean isPendingChanges = false;
    protected TFAuthHandler(ExtendedSub1 extendedSub1){
        this.extended = extendedSub1;
        authData = new ArrayList<>();
    }
    protected void setPendingChanges(){
        isPendingChanges = true;
    }
    protected void unsetPendingChanges(){
        isPendingChanges = false;
    }
    public boolean getPendingChanges(){
        return  isPendingChanges;
    }

    public void setCustomModel(Model yourModel) {
        this.customModel.put(yourModel.getClass().getName(),yourModel);
    }
    public void setCustomModel(Model yourModel,String CustomName) {
        this.customModel.put(CustomName, yourModel);
    }

    protected void initStandardData() throws TFAuthErrors {
        // MAC Address Encrypted
        authData.add(makeData(DataFields.MAC,TFCrypto.getMAC()));
        authData.add(makeData(DataFields.EMAIL,"username@mail.dom"));
        authData.add(makeData(DataFields.BIRTHDAY,"01/01/1990"));
    }
    public void retrieveData(String retrieve,boolean fromCallback) throws TFAuthErrors {
        String [] temp = retrieve.split("\n");
        authData.clear();
        for (String t: temp){
            if (fromCallback){
                authData.add(t);
            }else
                authData.add(encryptInfo(t));
        }
        if (!fromCallback) setPendingChanges();
    }

    protected String encryptInfo(String data) throws TFAuthErrors {
        String[] temp = data.split(": ");
        if (temp[0].isEmpty()) return "";
        if (temp.length > 2) throw new TFAuthErrors("Data format incorrect");
        return temp[0] + ": "+ Arrays.toString(Auth.crypto.encrypt(temp[1]));
    }

    public String getData(){
        StringBuilder res = new StringBuilder();
        for (String i : this.authData){
            res.append(i).append("\n");
        }
        return res.toString();
    }
    public String getData(String key){
        for (String d: authData){
            if (d.startsWith(key)){
                return d.split(": ")[1];
            }
        }
        return "";
    }


    public String getDataCleared(){
        StringBuilder res = new StringBuilder();
        for (String i : this.authData){
            String[] temp = i.split(": ");
            if (temp.length != 2) continue;
            res.append(temp[0]);
            res.append(": ");
            res.append(Auth.crypto.decrypt(TFCrypto.toBytes(temp[1])));
            res.append("\n");
        }
        return res.toString();
    }
    public String getDataCleared(String key){
        StringBuilder result = new StringBuilder();
        for (String d: authData){
            if (d.startsWith(key)){
                String[]temp = d.split(": ");
                return result.append(temp[0]).append(": ")
                        .append(Auth.crypto.decrypt(TFCrypto.toBytes(temp[1])))
                        .append("\n").toString();
            }
        }
        return "";
    }
    public String getDataCleared(int key){
        StringBuilder result = new StringBuilder();
        String[] temp = authData.get(key).split(": ");
        return result.append(temp[0]).append(": ")
                        .append(Auth.crypto.decrypt(TFCrypto.toBytes(temp[1])))
                        .append("\n").toString();
    }

    public byte[] getDataAsBytesArray(){
        return getData().getBytes();
    }

    public void alterData(int which,String value) throws TFAuthErrors {
        String target = authData.get(which).substring(0, authData.get(which).indexOf(":"));
        if (target.compareTo(DataFields.MAC.name()) == 0){
            setInto(which,TFCrypto.getMAC());
        }
        else if (target.compareTo(DataFields.EMAIL.name()) == 0){
            Email email = new Email();
            if (email.set(DataFields.EMAIL.name(),value)) {
                setInto(which, value);
            }
        }
        else {
            for (String k : this.customModel.keySet()){
                if (target.equals(k)){
                    if (this.customModel.get(k).set(target,value)){
                        setInto(which,value);
                    }
                }
            }
            setInto(which, value);
        }
    }
    public void alterData(String which,String value) throws TFAuthErrors {
        if (which.compareTo(DataFields.MAC.name()) == 0){
            setInto(which,TFCrypto.getMAC());
        }
        else if (which.compareTo(DataFields.EMAIL.name()) == 0){
            Email email = new Email();
            if (email.set(DataFields.EMAIL.name(),value)) {
                setInto(which, value);
            }
        }else
            setInto(which, value);
    }

    public void addData(String data) throws TFAuthErrors {
        try {
            this.authData.add(makeData(data));
        } catch (Exception e) {
            throw new TFAuthErrors(e.getMessage());
        }
        setPendingChanges();
    }
    public void addData(String key, String value) throws TFAuthErrors {
        try {
            this.authData.add(makeData(key, value));
        } catch (Exception e) {
            throw new TFAuthErrors(e.getMessage());
        }
        setPendingChanges();
    }

    private void setInto(String key,String value) throws TFAuthErrors {
        int counter = 0;
        for (String k: authData){
            if(k.contains(key+":")){
                authData.set(counter,makeData(key,value));
            }
            counter++;
        }
    }
    private void setInto(int index,String value){
        authData.set(index,
                authData.get(index).substring(0,
                        authData.get(index).indexOf(": ")+2)+TFCrypto.toString(Auth.crypto.encrypt(value)));
        setPendingChanges();
    }

    public void eraseData(String key){
        int counter = 0;
        for (String k: authData){
            if(k.contains(key+":")){
                authData.remove(counter);
            }
            counter++;
        }
    }
    public void eraseData(int index){
        authData.remove(index);
    }
    protected void clearAllData(){
        this.authData.clear();
    }

    private String makeData(DataFields field,String data) throws TFAuthErrors {
        if (this.customModel.containsKey(field.name())) {
            Model cm = this.customModel.get(field.name());
            if (cm.set(field.name(), data)) {
                return field.name() + ": " + TFCrypto.toString(Auth.crypto.encrypt(data));
            }else{
                throw new TFAuthErrors("Invalid data");
            }
        }else {
            return field.name() + ": " + TFCrypto.toString(Auth.crypto.encrypt(data));
        }
    }
    private String makeData(String field,String data) throws TFAuthErrors {
        if (this.customModel.containsKey(field)) {
            Model cm = this.customModel.get(field);
            if (cm.set(field, data)) {
                return field + ": " + TFCrypto.toString(Auth.crypto.encrypt(data));
            }else{
                throw new TFAuthErrors("Invalid data");
            }
        }else {
            return field + ": " + TFCrypto.toString(Auth.crypto.encrypt(data));
        }
    }
    private String makeData(String wholeValue) throws TFAuthErrors {
        String[] inParts = wholeValue.split(": ");
        if (this.customModel.containsKey(inParts[0])) {
            Model cm = this.customModel.get(inParts[0]);
            if (cm.set(inParts[0], inParts[1])) {
                return inParts[0] + ": " + TFCrypto.toString(Auth.crypto.encrypt(inParts[1]));
            }else{
                throw new TFAuthErrors("Invalid data");
            }
        }else {
            return inParts[0] + ": " + TFCrypto.toString(Auth.crypto.encrypt(inParts[1]));
        }
    }
    public void retrieveDataOnServer(Model user) throws TFAuthErrors {
        Auth.currentState = Auth.States.Handling_in;
            try {
                clearAllData();
                this.extended.xs1_openCommand(Auth.access(
                        user.get(DataFields.USERNAME.name()))+
                                user.get(DataFields.PASSWORD.name())+".sd/data");
                this.extended.xs1_readCommand(AuthCallback.fd,100);
            } catch (IOException e) {
                throw new TFAuthErrors("Cannot retrieve data from server successfully");
            }
    }
    public void commitDataChanges(Model user) throws TFAuthErrors {
        Auth.currentState = Auth.States.Handling_out;
        if (user == null || user.value.keySet().size() == 0){
            throw new TFAuthErrors("Invalid user");
        }
        if (getPendingChanges()){
            try {
                this.extended.xs1_openCommand(Auth.access(
                        user.get(DataFields.USERNAME.name()))+
                        user.get(DataFields.PASSWORD.name())+".sd/data");
                unsetPendingChanges();
            } catch (IOException e) {
                throw new TFAuthErrors("Cannot commit successfully");
            }
        }
    }

}
