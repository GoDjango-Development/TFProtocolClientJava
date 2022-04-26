package com.nerox.Auth.Models;

import com.nerox.Auth.TFCrypto;
import com.nerox.Auth.Errors.TFAuthErrors;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Username extends Model{

    @Override
    protected boolean validate(String what,String value) throws TFAuthErrors {
            Pattern pattern = Pattern.compile("[A-Za-z0-9]*");
            Matcher matcher = pattern.matcher(value);
            if (matcher.matches()){
                return true;
            }else{
                throw new TFAuthErrors("Invalid username: Username can only contain uppercase, " +
                        "lowercase alphanumeric letters and numbers");
            }
    }

    @Override
    public boolean set(String what,String value) throws TFAuthErrors {
        if (validate(null,value)){
            this.value.put(DataFields.USERNAME.name(), TFCrypto.hash(value));
            return true;
        }
        return false;
    }

    @Override
    public String get(String key) throws TFAuthErrors{
        if (this.value.size() > 0) return this.value.get(DataFields.USERNAME.name());
        throw new TFAuthErrors("INVALID FORMAT");
    }
}
