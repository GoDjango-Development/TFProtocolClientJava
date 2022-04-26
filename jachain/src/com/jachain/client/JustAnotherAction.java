package com.jachain.client;

import com.nerox.client.Tfprotocol;
import com.nerox.client.callbacks.ITfprotocolCallback;
import com.nerox.client.constants.XSACEConsts;
import com.nerox.client.misc.StatusInfo;
import com.nerox.client.misc.StatusServer;
import com.nerox.client.modules.XSAce;

import java.util.MissingFormatArgumentException;

public class JustAnotherAction {

    public final Ethereum eth;
    public final Bitcoin btc;
    public final Tether thr;
    public final Dai dai;
    public final Tron trx;

    private JustAnotherAction(XSAce ace, String pathToServer){
        JustAnotherCallback callback = (JustAnotherCallback) ace.getProtoHandler();
        final Object mutex = new Object();
        callback.setWaitingMutex(mutex);
        if (!ace.isConnect())
            throw new RuntimeException("XSAce is not yet connected, at this point it should be, please check" +
                    " your internet connection before creating this instance...");
        Thread backgroundRun = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    ace.setArgsCommand("python3.7", pathToServer);
                    ace.runNLCommand("python3.7");
                    ace.exitCommand();
                    try{
                        new Tfprotocol(ace, new ITfprotocolCallback() {
                            @Override
                            public void responseServerCallback(StatusInfo statusInfo) {}

                            @Override
                            public void instanceTfProtocol(Tfprotocol tfprotocol) {}

                            @Override
                            public void echoCallback(String value) {
                                System.out.println(value);
                            }
                        }).endCommand();
                    }catch (Error ignored){}

                }catch (Exception exception){
                    callback.releaseAllThreads();
                    throw new RuntimeException("Fatal error while trying to communicate with backend...\n" +
                            exception.getMessage());
                }
            }
        });
        backgroundRun.setDaemon(false);
        backgroundRun.start();
        synchronized (mutex){
            try {
                if (((StatusInfo)callback.getLastResult()).getCode() != XSACEConsts.Commands.OK
                || ((StatusInfo)callback.getLastResult()).getCode() != XSACEConsts.Commands.ERROR
                || ((StatusInfo)callback.getLastResult()).getCode() != XSACEConsts.Commands.FINISHED)
                    mutex.wait();
            } catch (InterruptedException ignored) {}
        }
        if (!((StatusInfo)callback.getLastResult()).getStatus().equals(StatusServer.OK)){
            throw new RuntimeException("Cannot successfully contact to backend please contact administrator");
        }
        callback.clearIncomingPocket();
        eth = new Ethereum(callback);
        btc = new Bitcoin(callback);
        thr = new Tether(callback);
        trx = new Tron(callback);
        dai = new Dai(callback);
    }
    public synchronized static JustAnotherAction instanceOf(XSAce ace, String pathToServer){
        return new JustAnotherAction(ace, pathToServer);
    }

    // Subclasses
    abstract class JustAnotherBase{
        protected final JustAnotherCallback callback;
        protected final Object stepMutex = new Object();
        public JustAnotherBase(JustAnotherCallback callback){
            this.callback = callback;
            if (!callback.getWaitingMutex().equals(stepMutex))
                callback.setWaitingMutex(stepMutex);
        }
        // Protected tool for class usage only should not be implemented by children
        protected String getClassName(){
            return this.getClass().getName().toLowerCase().split("\\$")[1];
        }
        // Common
        public String getPrivateKey(){
            String builder = this.getClassName() +
                    " get-prik ";
            return this.translate(builder);
        }
        public String getRawPrivateKey(){
            String builder = this.getClassName() +
                    " get-raw-prik ";
            return this.translate(builder);
        }
        public String getAddress(){
            String builder = this.getClassName() +
                    " get-address";
            return this.translate(builder);
        }
        public String getPublicKey(String of){
            StringBuilder builder = new StringBuilder();
            String env = this.getClassName();
            builder.append(env);
            builder.append(" get-pubk");
            if (env.equals(Bitcoin.class.getName().toLowerCase())){
                builder.append(" of ").append(of);
            }
            return this.translate(builder.toString());
        }
        public String getTransactionStatus(String of){
            String builder = this.getClassName() +
                    " get-tx-status" +
                    " of " + of;
            return this.translate(builder);
        }
        public String setAccount(String privateKey){
            String builder = this.getClassName() +
                    " set-account" +
                    " with " + privateKey;
            return this.translate(builder);
        }
        // Mask me and call me
        protected boolean setProvider(String url, String apiKey, String user, String pass, String address, int port,
                                      String protocol){
            StringBuilder builder = new StringBuilder();
            String env = this.getClassName();
            builder.append(env);
            builder.append(" set-provider");
            if (env.equals(Bitcoin.class.getName().toLowerCase()) && url.isEmpty()){
                builder.append(" user ").append(user);
                builder.append(" pass ").append(pass);
                builder.append(" address ").append(address);
                builder.append(" port ").append(port);
                builder.append(" protocol ").append(protocol);
            }
            else if (env.equals(Tron.class.getName().toLowerCase()) && !apiKey.isEmpty())
                builder.append(" with ").append(apiKey);
            if (!url.isEmpty())
                builder.append(" url ").append(url);
            if (env.equals(Bitcoin.class.getName().toLowerCase())){
                String test = this.translate(builder.toString());
                System.out.println(test);
                return test.split(" ", 2)[1]
                        .contains("Provider set successfully\n");
            }
            String[] res;
            if ((res = this.translate(builder.toString()).split(" ", 2)).length > 1)
                return res[1].contains("Provider set successfully\n");
            return false;
        }
        protected String translate(String builder){
            if (!callback.isRunning())
                throw new RuntimeException("Client is not running");
            //System.out.println(builder);
            this.callback.putInOutPocket(builder);
            return this.callback.retrieveFromPocket().trim();
        }
        protected String createWallet(String name, String parent){
            return createAccount(name, parent);
        }
        protected String createAccount(String name, String parent){
            StringBuilder builder = new StringBuilder();
            String env = this.getClassName();
            builder.append(env);
            builder.append(" create-account");
            if (env.equals(Bitcoin.class.getName().toLowerCase())){
                builder.append(" name ").append(name);
            }else{
                if (name != null && !name.isEmpty())
                    builder.append(" with ").append(name);
            }
            if (env.equals(Tron.class.getName().toLowerCase())){
                if (parent != null && !parent.isEmpty())
                    builder.append(" parent ").append(parent);
            }
            return this.translate(builder.toString());
        }
        protected String transfer(String to, String privateKey, float fee, boolean isReplaceable,
                                  float gasPrice, float feeLimit, String from, float amount){
            StringBuilder builder = new StringBuilder();
            String env = this.getClassName();
            builder.append(env);
            builder.append(" transfer");
            builder.append(" amount ").append(amount);
            if (env.equals(Bitcoin.class.getName().toLowerCase())){
                builder.append(" replace ").append(isReplaceable);
                builder.append(" passphrase ").append(privateKey);
                if (fee > 0){
                    builder.append(" fee ").append(fee);
                }
            }else if (env.equals(Ethereum.class.getName().toLowerCase())){
                builder.append(" gas-price ").append(gasPrice);
                builder.append(" gas-limit ").append(feeLimit);
            } else if(env.equals(Tron.class.getName().toLowerCase())){
                builder.append(" fee-limit ").append(feeLimit);
            }

            if (!privateKey.isEmpty())
                builder.append(" with ").append(privateKey);
            else
                throw new MissingFormatArgumentException("Private Key or passphrase is required for a transaction" +
                        "always...");

            if (!from.isEmpty())
                builder.append(" from ").append(from);
            if (!to.isEmpty())
                builder.append(" to ").append(to);
            return this.translate(builder.toString());
        }
        protected String getBalance(String of, String currency){
            StringBuilder builder = new StringBuilder();
            String env = this.getClassName();
            builder.append(env);
            builder.append(" get-balance ");
            if (env.equals(Bitcoin.class.getName().toLowerCase()) &&  currency != null && !currency.isEmpty()){
                builder.append(" base ").append(currency);
            }
            if (!of.isEmpty())
                builder.append(" of ").append(of);
            return this.translate(builder.toString());
        }
    }

    public final class Ethereum extends JustAnotherBase{
        public Ethereum(JustAnotherCallback callback) {
            super(callback);
        }
        // Common
        public boolean setProvider(String endpointUrl){
            return super.setProvider(endpointUrl, "", "", "", "", 0,"");
        }
        public String transfer(String to, String privateKey, String from, float amount, float gasPrice,
                               float gasLimit){
            return super.transfer(to, privateKey, 0, false, gasPrice, gasLimit, from, amount);
        }
        public String createAccount(String privateKey){
            return super.createAccount(privateKey, null);
        }
        public String createWallet(String privateKey){
            return super.createWallet(privateKey, null);
        }
        public String getBalance(String ...of){
            if (of.length > 0)
                return super.getBalance(of[0], null);
            return super.getBalance("", null);
        }
        // Unique
    }
    public final class Bitcoin extends JustAnotherBase{
        public Bitcoin(JustAnotherCallback callback) {
            super(callback);
        }
        // Common
        public boolean setProvider(String user, String pass, String address, int port,
                                   String protocol){
            return super.setProvider("", "", user, pass, address, port,protocol);
        }
        public boolean setProvider(String url){
            return super.setProvider(url, "", "", "", "", 0, "");
        }
        public String transfer(String to, float fee, boolean isReplaceable, String passphrase, float amount){
            return super.transfer(to, passphrase, fee, isReplaceable, 0, 0, "", amount);
        }
        public String createAccount(){
            return super.createAccount("", null);
        }
        public String createWallet(String name){
            return this.createWallet(name, "");
        }
        public String createWallet(String name, String passphrase){
            StringBuilder builder = new StringBuilder();
            String env = super.getClassName();
            builder.append(env);
            builder.append(" create-wallet");
            if (!name.isEmpty())
                builder.append(" name ").append(name);
            if (!passphrase.isEmpty())
                builder.append(" passphrase ").append(passphrase);
            return super.translate(builder.toString());
        }
        public String getBalance(String currency){
            return super.getBalance("", currency);
        }
        public String getBalance(){
            return super.getBalance("", "");
        }

        // Unique
        public String setPassphrase(String passphrase){
            StringBuilder builder = new StringBuilder();
            builder.append(super.getClassName());
            builder.append(" set-passphrase");
            builder.append(" with ").append(passphrase);
            return this.translate(builder.toString());
        }
        public String unlockWallet(String passphrase, long timeout){
            StringBuilder builder = new StringBuilder();
            builder.append(super.getClassName());
            builder.append(" unlock-wallet");
            builder.append(" with ").append(passphrase);
            if (timeout > 0){
                builder.append(" timeout ").append(timeout);
            }
            return this.translate(builder.toString());
        }
        public String lockWallet(){
            StringBuilder builder = new StringBuilder();
            builder.append(super.getClassName());
            builder.append(" lock-wallet");
            return this.translate(builder.toString());
        }
        public String changePassphrase(String old, String newOne){
            StringBuilder builder = new StringBuilder();
            builder.append(super.getClassName());
            builder.append(" change-passphrase");
            builder.append(" old ").append(old);
            builder.append(" new ").append(newOne);
            return this.translate(builder.toString());
        }
        public String getFee(String confTarget, String mode){
            StringBuilder builder = new StringBuilder();
            builder.append(super.getClassName());
            builder.append(" get-fee");
            if (!confTarget.isEmpty())
                builder.append(" conf-target ").append(confTarget);
            if (!mode.isEmpty())
                builder.append(" mode ").append(mode);
            return this.translate(builder.toString());
        }
        public String setFee(float amount){
            StringBuilder builder = new StringBuilder();
            builder.append(super.getClassName());
            builder.append(" set-fee");
            builder.append(" amount ").append(amount);
            return this.translate(builder.toString());
        }
    }
    public final class Dai extends JustAnotherBase{
        public Dai(JustAnotherCallback callback) {
            super(callback);
        }
        // Common
        public boolean setProvider(String endpointUrl){
            return super.setProvider(endpointUrl, "", "", "", "", 0,"");
        }
        public String transfer(String to, String privateKey, String from, float amount, float gasPrice,
                               float gasLimit){
            return super.transfer(to, privateKey, 0, false, gasPrice, gasLimit, from, amount);
        }
        public String createAccount(String privateKey){
            return super.createAccount(privateKey, null);
        }
        public String createWallet(String privateKey){
            return super.createWallet(privateKey, null);
        }
        public String getBalance(String ...of){
            if (of.length > 0)
                return super.getBalance(of[0], null);
            return super.getBalance("", null);
        }
        // Unique
    }
    public final class Tether extends JustAnotherBase{
        public Tether(JustAnotherCallback callback) {
            super(callback);
        }
        // Common
        public String createAccount(String privateKey, String parentForTron){
            return super.createAccount(privateKey, parentForTron);
        }
        public String createAccount(String privateKey){
            return super.createAccount(privateKey, "");
        }
        public boolean setProvider(String endpoint_url, String apiKey) {
            return super.setProvider(endpoint_url, apiKey, null, null, null,
                    0, null);
        }

        public String transfer(String to, String privateKey, String from, float amount, float feeLimit){
            return super.transfer(to, privateKey, 0, false, 0, feeLimit, from, amount);
        }
        public String createAccount(){
            return super.createAccount(null, null);
        }
        public String createWallet(){
            return super.createWallet(null, null);
        }
        public String getBalance(String of){
            return super.getBalance(of, null);
        }
        // Unique
        public String setTokenType(String type){
            StringBuilder builder = new StringBuilder();
            String env = super.getClassName();
            builder.append(env);
            builder.append(" set-token-type");
            builder.append(" type ").append(type);
            return this.translate(builder.toString());
        }
    }
    public final class Tron extends JustAnotherBase{
        public Tron(JustAnotherCallback callback) {
            super(callback);
        }
        // Common
        public String createAccoutn(String parent, String privateKey){
            return super.createAccount(parent, privateKey);
        }
        public boolean setProvider(String endpoint_url, String apiKey) {
            return super.setProvider(endpoint_url, apiKey, null, null, null,
                    0, null);
        }

        public String transfer(String to, String privateKey, String from, float amount, float feeLimit){
            return super.transfer(to, privateKey, 0, false, 0, feeLimit, from, amount);
        }
        public String createAccount(String privateKey, String parent){
            return super.createAccount(privateKey, parent);
        }
        public String createWallet(String privateKey, String parent){
            return super.createWallet(privateKey, parent);
        }
        public String getBalance(String...of){
            if (of.length > 0)
                return super.getBalance(of[0], null);
            return super.getBalance("", null);
        }
        // Unique
    }

}