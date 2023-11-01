package dev.godjango.tfprotocol.modules;

import java.io.IOException;

import dev.godjango.tfprotocol.*;
import dev.godjango.tfprotocol.callbacks.IXSQLiteCallback;
import dev.godjango.tfprotocol.misc.StatusInfo;
import dev.godjango.tfprotocol.misc.StatusServer;

public final class XSQLite extends TfprotocolSuper<IXSQLiteCallback> {

    public XSQLite(String ipServer, int portServer, String publicKey, String hash,
                   int len, String protocol, IXSQLiteCallback protoHandler) {
        super(ipServer, portServer, publicKey, hash, len, protocol, protoHandler);
    }
    public XSQLite(String proxy, String ipServer, int portServer, String publicKey, String hash,
                   int len, String protocol, IXSQLiteCallback protoHandler) {
        super(proxy,ipServer, portServer, publicKey, hash, len, protocol, protoHandler);
    }

    public XSQLite(TfprotocolSuper tfprotocol, IXSQLiteCallback protoHandler) throws IOException {
        this.setProtoHandler(protoHandler);
        this.easyreum = tfprotocol.getConHandler();
    }

    public void XS_SQLiteCommand(){
        super.getProtoHandler().startXSSQLiteCallback(this.easyreum
                .getBuilder().build("XS_SQLITE").translate()
                .getBuilder().buildStatusInfo());
        this.easyreum.setHeaderSize(Long.BYTES);
    }
    public void OpenCommand(String path_to_database, StringBuilder... db_id){
        this.easyreum.getBuilder().build("OPEN "
                + path_to_database).translate().getBuilder().buildStatusInfo();
        if (this.easyreum.getBuilder().isStatusInfoOk() && db_id.length > 0 && db_id[0] != null) {
            db_id[0].delete(0, db_id[0].length());
            db_id[0].append(this.easyreum.getBuilder().getStatusInfo().getCode());
        }
        super.getProtoHandler().openCallback(this.easyreum.getBuilder().getStatusInfo());
    }
    public void ExecCommand(String db_id, String sql_query){
        super.getProtoHandler().execCallback(this.easyreum.getBuilder().build("EXEC "
                + db_id + " " + sql_query).translate().getBuilder().buildStatusInfo());
        if (this.easyreum.getBuilder().isStatusInfoOk())
            this.easyreum.receiveUntil(0,super.getProtoHandler(),
                    "execCallback");
    }
    public void ExecOfCommand(String db_id, String path_to_file, String sql_query){
        super.getProtoHandler().execOfCallback(this.easyreum.getBuilder().build("EXECOF "
                +path_to_file + " " + db_id + " " + sql_query).translate()
                .getBuilder().buildStatusInfo());
    }
    public void CloseCommand(String db_id){
        this.easyreum.validateArgs(db_id);
        super.getProtoHandler().closeCallback(this.easyreum
                .getBuilder().build("CLOSE "
                + db_id).translate()
                .getBuilder().buildStatusInfo());
    }
    public void LastRowIdCommand(String db_id) {
        super.getProtoHandler().lastRowIdCallback(this.easyreum
                .getBuilder().build("LASTROWID "
                + db_id).translate()
                .getBuilder().buildStatusInfo());
    }
    public void SoftHeapCommand(long size) {
        super.getProtoHandler().softHeapCallback(this.easyreum
                .getBuilder().build("SOFTHEAP "
                + size).translate()
                .getBuilder().buildStatusInfo());
    }
    public void HardHeapCommand(long size) {
        super.getProtoHandler().hardHeapCallback(this.easyreum
                .getBuilder().build("HARDHEAP "
                + size).translate()
                .getBuilder().buildStatusInfo());
    }
    public void BlobInCommand(String db_id, String db_table,
                              String filename, String filepath) {
        this.easyreum.validateArgs(db_id, db_table, filename, filepath);
        super.getProtoHandler().blobinCallback(this.easyreum
                .getBuilder().build("BLOBIN "
                + db_id + " " + db_table + " " + filename + " " + filepath)
                .translate()
                .getBuilder().buildStatusInfo());
    }
    public void BlobOutCommand(String db_id, String db_table,
                              String filename, String filepath) {
        this.easyreum.validateArgs(db_id, db_table, filename, filepath);
        super.getProtoHandler().bloboutCallback(this.easyreum
                .getBuilder().build("BLOBOUT "
                + db_id + " " + db_table + " " + filename + " " + filepath)
                .translate()
                .getBuilder().buildStatusInfo());
    }

    public void ExitCommand() {
        this.easyreum
                .getBuilder().build("EXIT").send();
        super.getProtoHandler().exitCallback(new StatusInfo(StatusServer.OK,
                StatusServer.OK.ordinal(),"EXIT"));
        this.easyreum.setHeaderSize(Integer.BYTES);
    }
    public void TerminateCommand() {
        this.easyreum
                .getBuilder().build("TERMINATE").send();
        super.getProtoHandler().terminateCallback(new StatusInfo(StatusServer.OK,
                StatusServer.OK.ordinal(),"TERMINATE"));
        this.easyreum.setHeaderSize(Integer.BYTES);
    }
}
