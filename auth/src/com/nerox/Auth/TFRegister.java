package com.nerox.Auth;

import com.nerox.Auth.Errors.TFAuthErrors;
import com.nerox.Auth.Models.Email;
import com.nerox.Auth.Models.Model;
import com.nerox.Auth.Models.Password;
import com.nerox.Auth.Models.Username;
import com.nerox.client.TFExceptions;
import com.nerox.client.TfprotocolWrapper;
import com.nerox.client.modules.ExtendedSub1;

import java.io.IOException;

/**
 * This class is meant to be used for register action,
 * simulate a client side Authentication OneWayRSAEncrypted register.
 * NOTICE: This Auth class and whole module is only a client auth which means that
 * You can register in the server and store your data with RSA protection and that data is
 * fully save at least that you give to someone you Private Key.
 * This module warranties only the privacy of the user data, but not warranties the Integrity neither
 * the Availability, these two feature, depends whole on server firewall, and server architecture,
 * You can share your account with other devices, you only need to copy and paste the /data/rsa.pri and
 * /data/rsa.pub into your other device respective path.
 * Any issue with this class please contact me at n4b3ts3@yahoo.com, thanks :)
 * */
public class TFRegister {
    // Statics
    private TFRegister(){
    }
    /**
     * This functions allows you to simulate a register action into server,
     * create a folder with the username and password, both encrypted with RSA,
     * the user must have public key and username and password to read inside folder.
     * !!!WARNING: SERVER MUST ACT...
     * @param rm Register Model Param stores and verify the data before we send it to server.
     * @param protocol This protocol is for use callback and global instance for actions, keeping
     *                   of that way, continuity between Auth main class and this class.
     * */
    protected static void register(Model rm, ExtendedSub1 protocol) throws TFAuthErrors {
        try {
            String attempt = Auth.access(rm.get(Model.DataFields.USERNAME.name())+
                    rm.get(Model.DataFields.PASSWORD.name())).concat(".temp.sd");
            TfprotocolWrapper wrapper = new TfprotocolWrapper(protocol.getConHandler());

            try {
                wrapper.fstatCommand(attempt.replace(".temp.sd",".sd"));
                throw new TFAuthErrors("User already exists...");
            }catch (TFExceptions ignored){}

            wrapper.rmdirCommand(attempt);
            wrapper.mkdirCommand(attempt);
            String temp = attempt.concat("/data");
            wrapper.touchCommand(temp);
            protocol.xs1_openCommand(temp);
            wrapper.renamCommand(attempt, attempt.replace(".temp.sd",".sd"));
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    /**
     * Register Model is a class who extends from AuthModel which means that this class is a Model for
     * authentication, everytime the user wants to send data to his account, data must be validate it,
     * (ALSO SHOULD BE VALIDATE IT IN SERVER SIDE), this class is the one who validate data before send it to
     * to server with Register Action.
     * */
    public static class RegisterModel extends Model {
        @Override
        protected boolean validate(String what, String value) throws TFAuthErrors{
            if (what.compareTo(DataFields.USERNAME.name()) == 0)
            {
                return new Username().set(null,value);
            }else if(what.compareTo(DataFields.PASSWORD.name()) == 0){
                return new Password().set(null,value);
            }else if(what.compareTo(DataFields.EMAIL.name()) == 0){
                return new Email().set(null,value);
            }
            return false;
        }

        @Override
        public boolean set(String what, String value) throws TFAuthErrors{
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
