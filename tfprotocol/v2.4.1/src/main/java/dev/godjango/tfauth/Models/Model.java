package dev.godjango.tfauth.Models;
import dev.godjango.tfauth.Errors.TFAuthErrors;

import java.util.HashMap;

public abstract class Model {
    public enum DataFields{
        MAC,
        EMAIL,
        BIRTHDAY,
        USERNAME,
        PASSWORD,
    }
    public HashMap<String,String> value = new HashMap<>();
    protected abstract boolean validate(String what,String value) throws TFAuthErrors, dev.godjango.tfauth.Errors.TFAuthErrors;
    public abstract boolean set(String what,String value) throws TFAuthErrors, dev.godjango.tfauth.Errors.TFAuthErrors;
    public abstract String get(String key) throws TFAuthErrors, dev.godjango.tfauth.Errors.TFAuthErrors;
}
