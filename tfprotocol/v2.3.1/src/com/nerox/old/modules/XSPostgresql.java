package com.nerox.old.modules;

import com.nerox.old.TFExceptions;
import com.nerox.old.TfprotocolSuper;
import com.nerox.old.callbacks.IXSPostgreSQLCallback;
import com.nerox.old.misc.StatusInfo;
import com.nerox.old.misc.StatusServer;

public class XSPostgresql extends TfprotocolSuper<IXSPostgreSQLCallback> {

    public XSPostgresql(String ipServer, int portServer, String publicKey,
                        String hash, int len, String protocol, IXSPostgreSQLCallback protoHandler) {
        super(ipServer, portServer, publicKey, hash, len, protocol, protoHandler);
    }

    public XSPostgresql(String proxy, String ipServer, int portServer, String publicKey,
                        String hash, int len, String protocol, IXSPostgreSQLCallback protoHandler) {
        super(proxy, ipServer, portServer, publicKey, hash, len, protocol, protoHandler);
    }

    public XSPostgresql(TfprotocolSuper tfprotocol, IXSPostgreSQLCallback protoHandler) {
        this.setProtoHandler(protoHandler);
    }

    public void XS_PostGreSQLCommand() throws TFExceptions {
        super.getProtoHandler().startXSPostGreSQL(this.easyreum
                .getBuilder().build("XS_POSTGRESQL").translate()
                .getBuilder().buildStatusInfo());
        this.easyreum.setHeaderSize(Long.BYTES);
    }
    public void OpenCommand(String host, int port, String user, String password, String dbname,
                            StringBuilder...db_id) {
            this.easyreum.getBuilder().build("OPEN",host,
                    String.valueOf(port),user,password,dbname).translate()
                    .getBuilder().buildStatusInfo();
            if (this.easyreum.getBuilder().isStatusInfoOk() && db_id.length>0 && db_id[0] != null)
            {
                db_id[0].delete(0,db_id[0].length());
                db_id[0].append(this.easyreum.getBuilder().getStatusInfo().getCode());
            }
            super.getProtoHandler().openCallback(this.easyreum.getBuilder().getStatusInfo());
    }
    public void ExecCommand(String db_id, String sql_query) {
        super.getProtoHandler().execCallback(this.easyreum.getBuilder().build("EXEC",
                db_id, sql_query).translate().getBuilder().buildStatusInfo());
        if (this.easyreum.getBuilder().isStatusInfoOk())
            this.easyreum.receiveUntil(0,super.getProtoHandler(),
                    "execCallback");
    }
    public void ExecOfCommand(String db_id, String path_to_file, String sql_query) {
        super.getProtoHandler().execOfCallback(this.easyreum.getBuilder().build("EXECOF",
                path_to_file, db_id, sql_query).translate().getBuilder().buildStatusInfo());
    }
    public void CloseCommand(String db_id) {
        this.easyreum.validateArgs(db_id);
        super.getProtoHandler().closeCallback(this.easyreum.getBuilder().build("CLOSE",db_id).
                translate().getBuilder().buildStatusInfo());
    }
    public void ExitCommand() {
        this.easyreum.getBuilder().build("EXIT").send();
        super.getProtoHandler().exitCallback(new StatusInfo(StatusServer.OK,
                StatusServer.OK.ordinal(),"EXIT"));
        this.easyreum.setHeaderSize(Integer.BYTES);
    }
    public void TerminateCommand() {
        this.easyreum.getBuilder().build("TERMINATE").send();
        super.getProtoHandler().terminateCallback(new StatusInfo(StatusServer.OK,
                StatusServer.OK.ordinal(),"TERMINATE"));
        this.easyreum.setHeaderSize(Integer.BYTES);
    }
}
