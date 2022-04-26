package com.nerox.Auth.Errors;

import com.nerox.Auth.Auth;
import com.nerox.Auth.TFCrypto;
import com.nerox.Auth.Models.Model;
import com.nerox.Auth.TFAuthHandler;
import com.nerox.Auth.TFLogin;
import com.nerox.Auth.TFRegister;

public class TFAuthErrors extends Exception {
    public enum ErrorsKinds{
        MACAddress,
        InitConnection,
        OnEstablishingConnection,
        OnRegistering,
        OnLogin,
        OnLogout,
        UnhandledException,
        OnEncryptingInfo,
        OnCommittingChanges,
        AlreadyLogged,
        AlreadyRegistered,
        OnValidating,
        NotNetworkInterfacesUp,
        CannotSetPrivateKey,
        CannotSetPublicKey,
        CannotDecryptSuccessfully,
    }
    public ErrorsKinds instanceError;

    public TFAuthErrors(String message,int Code){
        super(message);
        settingCode(Code);
    }
    public TFAuthErrors(String message){
        super(message);
        handlingIssues();
    }
    public TFAuthErrors(Exception exception){
        super(exception);
        handlingIssues();
    }
    public TFAuthErrors(int Code){
        settingCode(Code);
    }
    public TFAuthErrors(){
        handlingIssues();
    }

    private void settingCode(int code){
        try {
            instanceError = ErrorsKinds.values()[code];
        }catch (IndexOutOfBoundsException ex){
            instanceError = ErrorsKinds.UnhandledException;
        }

    }
    private void handlingIssues(){
        if (getStackTrace()[0].getClassName().equals(TFCrypto.class.getName())){
            if(getStackTrace()[0].getMethodName().equals("GetMACAddress")) {
                instanceError = ErrorsKinds.MACAddress;
            }else if(getStackTrace()[0].getMethodName().equals("SetPrK")) {
                instanceError = ErrorsKinds.CannotSetPrivateKey;
            }else if(getStackTrace()[0].getMethodName().equals("SetPuK")) {
                instanceError = ErrorsKinds.CannotSetPublicKey;
            }else if(getStackTrace()[0].getMethodName().equals("Decrypt")) {
                instanceError = ErrorsKinds.CannotDecryptSuccessfully;
            }
        } else if (getStackTrace()[0].getClassName().equals(TFLogin.class.getName())) {
                if (getStackTrace()[0].getMethodName().equals("Login")){
                    instanceError = ErrorsKinds.OnLogin;
                }
        } else if (getStackTrace()[0].getClassName().equals(Model.class.getName())) {
                if (getStackTrace()[0].getMethodName().equals("validate")) {
                    instanceError = ErrorsKinds.OnValidating;
                }
        } else if (getStackTrace()[0].getClassName().equals(TFRegister.class.getName())) {
                if (getStackTrace()[0].getMethodName().equals("Register")) {
                    instanceError = ErrorsKinds.AlreadyRegistered;
                }
        } else if (getStackTrace()[0].getClassName().equals(TFAuthHandler.class.getName())) {
                if (getStackTrace()[0].getMethodName().equals("encryptInfo")) {
                    instanceError = ErrorsKinds.OnEncryptingInfo;
                }else if (getStackTrace()[0].getMethodName().equals("commitDataChanges")) {
                    instanceError = ErrorsKinds.OnCommittingChanges;
                }
        } else if (getStackTrace()[0].getClassName().equals(Auth.class.getName())) {
                if (getStackTrace()[0].getMethodName().equals("init")) {
                    instanceError = ErrorsKinds.InitConnection;
                } else if (getStackTrace()[0].getMethodName().equals("EstablishConnection")) {
                    instanceError = ErrorsKinds.OnEstablishingConnection;
                } else if (getStackTrace()[0].getMethodName().equals("Register")) {
                    instanceError = ErrorsKinds.OnRegistering;
                } else if (getStackTrace()[0].getMethodName().equals("Login")) {
                    instanceError = ErrorsKinds.OnLogin;
                } else if (getStackTrace()[0].getMethodName().equals("Logout")) {
                    instanceError = ErrorsKinds.OnLogout;
                }
        } else {
                instanceError = ErrorsKinds.UnhandledException;
        }
    }
}
