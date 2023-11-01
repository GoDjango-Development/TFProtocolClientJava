package dev.godjango.jachain;

import dev.godjango.tfprotocol.keepalives.UDPKeepAlive;
import dev.godjango.tfprotocol.modules.XSAce;

public final class JustAnotherChain{
    private final JustAnotherAction action;
    private final XSAce ace;
    private static final String pathToServerSide = "/opt/PychainFramework/__main__.py";
    public JustAnotherChain(String address, int port, String publicKey, String hash, int length,
                            String versionProtocol, String serverKey) {
        JustAnotherCallback callback = new JustAnotherCallback();
        this.ace = new XSAce(
                address,
                port,
                publicKey,
                hash,
                length,
                versionProtocol,
                callback
        );
        this.ace.connect(UDPKeepAlive.TYPE.UDP_PROCHECK, 5, 5, 3);
        UDPKeepAlive.SetCallback(callback::releaseAllThreads);
        this.ace.startCommand();
        this.ace.inskeyCommand(serverKey);
        this.action = JustAnotherAction.instanceOf(this.ace, pathToServerSide);
    }

    public JustAnotherAction doAction(){
        return this.action;
    }
    public void endJAC(){
        try {
            this.finalize();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void finalize() throws Throwable {
        ((JustAnotherCallback)this.ace.getProtoHandler()).putInOutPocket("quit");
        super.finalize();
    }
}