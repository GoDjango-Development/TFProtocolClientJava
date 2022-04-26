package com.nerox.Auth;
import com.nerox.Auth.Errors.TFAuthErrors;
import com.nerox.Auth.Models.Model;
import com.nerox.client.Tfprotocol;
import com.nerox.client.TfprotocolWrapper;
import com.nerox.client.misc.StatusServer;
import com.nerox.client.modules.ExtendedSub1;

/**
 * How should this work...
 * 1- You instantiate this class.
 * 1.1- (AUTO) The connection is established.
 * 1.2- (CONDITION) If the connection is not established or previously established and passed to
*       the constructor, the class with end.
 * 2- There is available three options:
 * 2.1- Login
 * 2.1.1- You must create a model, instancing the class TFLogin.LoginModel.
 * 2.1.2- You must pass the created model to the Login function.
 * 2.1.3- Wait for the response.
 * 2.2- Register
 * 2.2.1- You must create a model, instancing the class TFRegister.RegisterModel.
 * 2.2.2- You must pass the created model to the Register function.
 * 2.1.3- Wait for the response.
 * 2.3- Logout
 * 2.3.1- Call this function and Wait for the response.
 * */
public final class Auth {
    // Enums
    protected enum States {
        Idle,
        Login,
        Register,
        Handling_in,
        Handling_out
    }
    // Statics Variables
    public static States currentState = States.Idle;

    // Variables de instancia
    private String IP;
    private int PORT;
    private int LEN ;
    private String PROTO;
    private String PUK;
    private String HASH;

    // Constants
    private static final String PATH = "secure.sd";

    // Other classes references
    private ExtendedSub1 extended;
    static TFCrypto crypto;

    // Class Results
    Model user;

    // Constructors
    /**
     * This constructor receives a connection already stablished.
     * and use it for Authentication functions...
     * @param tf Connection previously created
     * */
    public Auth(ExtendedSub1 tf, Tfprotocol tfprotocol)throws TFAuthErrors
    {
        assert tf.isConnect() && tf.getConHandler().equals(tfprotocol.getConHandler());
        this.extended = tf;
        init();
    }

    /**
     * This constructor create an instance of tfprotocol
     * @param ip The Internet Protocol Address for connect to the server...
     * @param port The open port of tfprotocol service on server...
     * @param proto The protocol version, for example: 0.0, 1.0 etc
     * @param len UPDATE THIS DOC!!!!
     * @param puk The public key with which the server encrypt the message
     * @param hash UPDATE DOC!!!
     * **/
    public Auth(String ip, int port, String puk, String hash, int len, String proto) throws TFAuthErrors
    {
        this.IP = ip;
        this.PORT = port;
        this.PROTO = proto;
        this.LEN = len;
        this.PUK = puk;
        this.HASH = hash;
        init();
    }

    // Private Methods
    /**
     * Initialize the class, for overloading the constructors and not need to rewrite the code,
     * I put this in a separate function...
    */
    private void init() throws TFAuthErrors{
        crypto = new TFCrypto();
        AuthCallback extendedCallback = new AuthCallback();
        if (extended == null){
            extended = new ExtendedSub1(IP, PORT, PUK,
                    HASH, LEN, PROTO, extendedCallback);
        }else {
            extended.setProtoHandler(extendedCallback);
        }
        if (extended.connect().isConnect()) {
            new TfprotocolWrapper(extended.getConHandler()).mkdirCommand(PATH);
        } else {
            throw new TFAuthErrors("Cannot connect succesfully to " + this.IP);
        }
    }

    public ExtendedSub1 getProtocol(){
        return this.extended;
    }
    // Public Methods
    /**
     * The public method to access to the register auth action...
     * */
    public void register(Model rm) throws TFAuthErrors{
        if (currentState != States.Idle) throw new TFAuthErrors("Auth instance is not idle," +
                "please check you are not doing some other stuffs");
        if (!extended.isConnect()) {
            throw new TFAuthErrors("You cant register while you are offline...");
        }
        if( this.user != null){
            throw new TFAuthErrors("You are already logged in please logout before trying to register as" +
                    " other user...",
                    TFAuthErrors.ErrorsKinds.AlreadyLogged.ordinal());
        }
        currentState = States.Register;
        crypto.genKey();
        TFRegister.register(rm, extended);
        this.user = rm;
        currentState = States.Idle;
    }

    /**
     * The public method to access to the login auth action...
     * */
    public void login(Model model) throws TFAuthErrors{
        if (currentState != States.Idle) throw new TFAuthErrors("Auth instance is not idle," +
                "please check you are not doing some other stuffs");
        if (!extended.isConnect()) {
            throw new TFAuthErrors("You can not login while you are offline...");
        }
        if( this.user != null){
            throw new TFAuthErrors("You are already logged in please logout before trying to login again...",
                    TFAuthErrors.ErrorsKinds.AlreadyLogged.ordinal());
        }

        currentState = States.Login;
        TFLogin.login(model, extended);
        this.user = model;
        currentState = States.Idle;
    }

    public boolean deleteUser(String username, String password) {
        return new TfprotocolWrapper(extended.getConHandler())
                .rmdirCommand(access(TFCrypto.hash(username)+TFCrypto.hash(password)+".sd"))
                .equals(StatusServer.OK);
    }

    // Key handle it
    public boolean checkKey(){
        return !(crypto.key == null || crypto.key.equals(""));
    }
    public void setEncryptionKey(String key){
        crypto.key = key;
    }
    public String getEncryptionKey() throws TFAuthErrors {
        if (!checkKey()) crypto.genKey();
        return crypto.key;
    }

    public Model getUser(){
        return this.user;
    }
    /**
     * Close the connection
     * **/
    public void logout() {
        this.extended.disconnect();
        this.user = null;
    }

    public boolean isLogged(){
        return this.user != null;
    }

    //Static Functions

    /**
     * This function concat the path standard directory to access to the accounts...
     * Each account is under that directory, if you change the value of the standard directory
     * You will not be able to login or register no more.... (At least not of a secure way)...
     * */
    static String access(String param){
        return PATH + "/" +param;
    }
}