package dev.godjango.tfauth;

import dev.godjango.tfauth.Errors.TFAuthErrors;
import dev.godjango.tfauth.Models.Model;
import dev.godjango.tfauth.Models.Password;
import dev.godjango.tfauth.Models.Username;
import dev.godjango.tfprotocol.TFExceptions;
import dev.godjango.tfprotocol.TfprotocolWrapper;
import dev.godjango.tfprotocol.modules.ExtendedSub1;

import java.io.IOException;

public class TFLogin{
    /**
     * Do not instantiate me
     * */
    private TFLogin(){
    }

    protected static void login(Model model, ExtendedSub1 protocol) throws TFAuthErrors {
        try {
            TfprotocolWrapper wrapper = new TfprotocolWrapper(protocol.getConHandler());
            String attempt = Auth.access(model.get(Model.DataFields.USERNAME.name())+
                    model.get(Model.DataFields.PASSWORD.name())).concat(".sd");
            try {
                wrapper.fstatCommand(attempt);
                String temp = attempt.concat("/data");
                wrapper.touchCommand(temp);
                protocol.xs1_openCommand(temp);
            }catch (TFExceptions exceptions){
                throw new TFAuthErrors("Cannot successfully login");
            }
        }catch (IOException ex){
            throw new TFAuthErrors(ex.getMessage());
        }
    }
    public static class LoginModel extends Model {
        @Override
        public boolean validate(String what, String value) throws TFAuthErrors {
            if (what.compareTo(DataFields.USERNAME.name()) == 0){
                Username username =new Username();
                return username.set(what, value);
            }else if (what.compareTo(DataFields.PASSWORD.name()) == 0){
                Password password = new Password();
                return password.set(what,value);
            }
            return false;
        }

        @Override
        public boolean set(String what,String value) throws TFAuthErrors {
            if (validate(what,value)){
                if(what.equals(DataFields.USERNAME.name()) ||
                        what.equals(DataFields.PASSWORD.name()) )
                    this.value.put(what, TFCrypto.hash(value));
                else
                    this.value.put(what, value);
            }
            return false;
        }

        @Override
        public String get(String key) {
            return this.value.get(key);
        }
    }
}
