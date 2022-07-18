package com.nerox.client.modules;

import com.nerox.client.*;
import com.nerox.client.callbacks.IXSMysqlCallback;
import com.nerox.client.misc.StatusInfo;
import com.nerox.client.misc.StatusServer;

import java.io.IOException;

public final class XSMysql extends TfprotocolSuper<IXSMysqlCallback> {

    public XSMysql(String ipServer, int portServer, String publicKey, String hash,
                   int len, String protocol, IXSMysqlCallback protoHandler) {
        super(ipServer, portServer, publicKey, hash, len, protocol, protoHandler);
    }
    public XSMysql(String proxy,String ipServer, int portServer, String publicKey, String hash,
                   int len, String protocol, IXSMysqlCallback protoHandler) {
        super(proxy, ipServer, portServer, publicKey, hash, len, protocol, protoHandler);
    }

    public XSMysql(TfprotocolSuper tfprotocol, IXSMysqlCallback protoHandler) throws IOException {
        this.setProtoHandler(protoHandler);
        this.easyreum = tfprotocol.getConHandler();
    }

    public void XS_MySQLCommand() {
        this.getProtoHandler().startXSMySqlCallback(this.easyreum
                .getBuilder().build("XS_MYSQL").translate()
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
    public void LastRowIdCommand(String db_id) {
        super.getProtoHandler().lastRowIdCallback(this.easyreum
                .getBuilder().build("LASTROWID",db_id).
                        translate().getBuilder().buildStatusInfo());
    }
    public void BlobInCommand(String db_id, String db_table, String file_name
            , String file_path) {
        this.easyreum.validateArgs(db_id, db_table, file_name, file_path);
            super.getProtoHandler().blobInCallback(this.easyreum
                    .getBuilder().build("BLOBIN",db_id,db_table,
                    file_name,file_path).translate().getBuilder().buildStatusInfo());
    }
    public void BlobOutCommand(String db_id, String db_table, String file_name
            , String file_path) {
        this.easyreum.validateArgs(db_id, db_table, file_name, file_path);
        super.getProtoHandler().blobOutCallback(this.easyreum
                .getBuilder().build("BLOBOUT",db_id,db_table,
                file_name,file_path).translate().getBuilder().buildStatusInfo());
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
