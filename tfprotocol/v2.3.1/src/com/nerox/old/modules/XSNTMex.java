package com.nerox.old.modules;

import com.nerox.old.TfprotocolSuper;
import com.nerox.old.callbacks.IXSNTMexCallback;
import com.nerox.old.connection.WrappedClient;

public class XSNTMex extends TfprotocolSuper<IXSNTMexCallback> {

    public XSNTMex(String ipServer, int portServer, String publicKey, String hash,
                   int len, String protocol, IXSNTMexCallback protoHandler) {
        super(ipServer, portServer, publicKey, hash, len, protocol, protoHandler);
    }
    public XSNTMex(String proxy, String ipServer, int portServer, String publicKey, String hash,
                   int len, String protocol, IXSNTMexCallback protoHandler) {
        super(proxy, ipServer, portServer, publicKey, hash, len, protocol, protoHandler);
    }
    public XSNTMex(TfprotocolSuper tfprotocol, IXSNTMexCallback protoHandler) {
        this.setProtoHandler(protoHandler);
        this.clientSocket = tfprotocol.getClient();
    }

    public void XS_NTMex() {
        super.getProtoHandler().startXSNTMexCallback(this.easyreum
                .getBuilder().build("XS_NTMEX").translate()
                .getBuilder().buildStatusInfo());
    }
    public void InskeyCommand(String key) {
        super.getProtoHandler().inskeyCallback(this.easyreum
                .getBuilder().build("INSKEY "+key).translate().getBuilder().buildStatusInfo());
    }
    public void LoadCommand(String path_to_module) {
        super.getProtoHandler().loadCallback(this.easyreum
                .getBuilder().build("LOAD "+path_to_module)
                .translate().getBuilder().buildStatusInfo());
    }
    public void RunCommand() {
        WrappedClient wc = WrappedClient.use(this.easyreum);
        super.getProtoHandler().runCallback(this.easyreum.getBuilder().build("RUN")
                .translate().getBuilder().buildStatusInfo(), wc);
    }
    public void GoBackCommand() {
        super.getProtoHandler().goBackCallback(this.easyreum.getBuilder().build("GOBACK")
                .translate().getBuilder().buildStatusInfo());
    }
    public void ExitCommand() {
        super.getProtoHandler().exitCallback(this.easyreum.getBuilder().build("EXIT")
                .translate().getBuilder().buildStatusInfo());
    }
    public void SysInfoCommand() {
        super.getProtoHandler().sysInfoCallback(this.easyreum.getBuilder().build("SYSNFO")
                .translate().getBuilder().buildStatusInfo(false));
    }

}
