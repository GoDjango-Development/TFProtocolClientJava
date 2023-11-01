package dev.godjango.tfauth.Models;

import dev.godjango.tfauth.TFCrypto;
import dev.godjango.tfauth.Errors.TFAuthErrors;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Password extends Model{
    @Override
    public boolean validate(String what,String value) throws TFAuthErrors {
        short pwdMin = 8;
        short pwdMax = 50;
        if (value.length() < pwdMin || value.length() > pwdMax){
            throw new TFAuthErrors("Invalid password length: Password must be between "+ pwdMin+
                    " and "+ pwdMax +" characters lengths...");
        }
        Pattern pattern = Pattern.compile("[A-Za-z0-9!*]*");
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    @Override
    public boolean set(String what,String value) throws TFAuthErrors {
        if(validate(null,value)){
            this.value.put(DataFields.PASSWORD.name(), TFCrypto.hash(value));
            return true;
        }
        return false;
    }

    @Override
    public String get(String key) throws TFAuthErrors {
        if (value.size() > 0) return value.get(DataFields.PASSWORD.name());
        throw new TFAuthErrors("INVALID FORMAT");
    }
}
