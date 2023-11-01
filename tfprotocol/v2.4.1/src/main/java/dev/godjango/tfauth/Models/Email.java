package dev.godjango.tfauth.Models;

import dev.godjango.tfauth.Errors.TFAuthErrors;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Email extends Model{
    @Override
    protected boolean validate(String what,String value) throws TFAuthErrors {
        Pattern pattern = Pattern.compile("[a-z0-9]+[.]?[a-z0-9]*@[a-z]+.?[a-z]*.[a-z]+");
            Matcher matcher = pattern.matcher(value);
            if (matcher.matches()){
                return true;
            }
            throw new TFAuthErrors("Invalid email: username@mail.com");
    }

    @Override
    public boolean set(String what,String value) throws TFAuthErrors {
        if(validate(null,value)) {
            this.value.put(DataFields.EMAIL.name(), value);
            return true;
        }
        return false;
    }

    @Override
    public String  get(String key) throws TFAuthErrors {
        if (value.size() > 0) return value.get(DataFields.EMAIL.name());
        throw new TFAuthErrors("INVALID FORMAT");
    }
}
