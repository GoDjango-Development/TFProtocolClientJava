package com.nerox.client;

import com.nerox.client.callbacks.ITfprotocolCallback;
import com.nerox.client.connection.Easyreum;
import com.nerox.client.connection.EasyreumUDP;
import com.nerox.client.constants.TfprotocolConsts;
import com.nerox.client.misc.FileStat;
import com.nerox.client.misc.StatusInfo;
import com.nerox.client.misc.StatusServer;
import com.nerox.client.misc.FileManager;
import com.nerox.client.multithread.Easythread;
import com.nerox.client.security.Cryptography;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

import static com.nerox.client.constants.TfprotocolConsts.PutGetCommand.*;

/**
 * The tfprotocol class contains the basics commands for the interaction with the server, with it you can
 * do some basic stuffs like getting the date or echoing a message, also you can change the session key with nigma
 * command etc.... For more specifics tasks you must extend from an extension class like XSAce etc...
 * @see TfprotocolSuper
 * */
public final class Tfprotocol extends TfprotocolSuper<ITfprotocolCallback> {
    /**
     * This class is intended for be used by put Command and get Command.
     * */
    public static final class Codes{
        private Easyreum easyreum;
        public volatile boolean rcvSignal = false;
        private volatile boolean sending_signal = false;
        public boolean block;
        private long command = HPFEND;
        private Codes(Easyreum easyreum){
            this.easyreum = easyreum;
        }
        /**
         * Send a code to the server codes can only be codes described at XSAceConsts class..
         * @param command The code to be send to server..
         * @see com.nerox.client.constants.XSACEConsts
         * */
        public void sendPut(long command){
            if (!block){
                this.rcvSignal = true;
                this.command = command;
                this.easyreum.getBuilder().build(command).sendJust();
            }
        }
        /**
         * Send a code to the server codes can only be codes described at XSAceConsts class..
         * @param command The code to be send to server..
         * @see com.nerox.client.constants.XSACEConsts
         * */
        public void sendGet(long command){
            if (!block){
                this.sending_signal = true;
                this.command = command;
                this.easyreum.getBuilder().build(command).sendJust();
            }
        }
        /**Return the last command send*/
        public long getLastCommand(){
            return this.command;
        }
    }

    private Easythread easythread;
    private final Object mutex = new Object();

    public Tfprotocol(String ipServer, int portServer, String publicKey,
                      String hash, int len, String protocol,
                      ITfprotocolCallback protoHandler){
        super(ipServer, portServer, publicKey, hash, len, protocol, protoHandler);
    }
    public Tfprotocol(TfprotocolSuper protocol, ITfprotocolCallback callback){
        this.setProtoHandler(callback);
        this.easyreum = protocol.getConHandler();
    }

    public Tfprotocol(String ipServer, int portServer, InputStream publicKey,
                      String hash, int len, String protocol,
                      ITfprotocolCallback protoHandler){
        super(ipServer, portServer, publicKey, hash, len, protocol, protoHandler);
    }

    public Tfprotocol(String proxy, String ipServer, int portServer, String publicKey,
                      String hash, int len, String protocol, ITfprotocolCallback protoHandler) {
        super(proxy, ipServer, portServer, publicKey, hash, len, protocol, protoHandler);
    }

    /**
     * Retrieves the available space in the partition where the protocol folder is located. This command should be used
     * only after a failure to find out if such a failure is due to the lack of space. It should not be used to guess how
     * much space remains, and then proceed with a writing operation. The described scenario could potentially lead to a
     * race condition. If both clients at the same time retrieve the free space and then write according to that value,
     * one of them will fail.
     * */
    public void freespCommand()  {
        this.getProtoHandler().freespCallback(
                this.easyreum.getBuilder().build("FREESP").translate().getBuilder().buildStatusInfo());
    }
    /**
     * Returns the number of elapsed seconds and microseconds
     * since the epoch -separated by dot-, and arbitrary point in the time
     * continuum, which is the Gregorian calendar time Jan 1 1970 00:00
     * UTC.
     * */
    public void udateCommand() {
        this.getProtoHandler().udateCallback(
                this.easyreum.getBuilder().build("UDATE").translate().getBuilder().buildStatusInfo());
    }
    /**
     * Returns the number of elapsed seconds and nanoseconds
     * since the epoch -separated by dot-, and arbitrary point in the time
     * continuum, which is the Gregorian calendar time Jan 1 1970 00:00
     * UTC.
     * */
    public void ndateCommand() {
        this.getProtoHandler().ndateCallback(
                this.easyreum.getBuilder().build("NDATE").translate()
                        .getBuilder().buildStatusInfo());
    }
    /**
     * Command is for debug propose. Once sent the server replays back the exact same thing the client sent, including
     * the ECHO word.
     * @param value The string that is going to be echoed...
     * */
    public void echoCommand(String value) {
        this.getProtoHandler().echoCallback(
                this.easyreum.getBuilder().build("ECHO", value).translate()
                        .getBuilder().buildStatusInfo().getMessage());
    }
    /**
     * Command creates a directory at the specified path.
     * This feature allows the creation of a kind of directory that cannot be listed with any command. In order to
     * access it, the name must be known in advance. The directory name may contain any character, except the decimal
     * zero “0” and the path separator “/”, and must end with an “.sd” extension. The length of the directory name must
     * not exceed 255 characters including the extension. In order to create strong secured directories, the following
     * technique could be used: Combine a user ́s name with a very strong password and apply to them the SHA256 or any
     * other secure hash function. The resulting hash must be scanned to remove or change from it any occurrence of the
     * decimal zero ‘0’ -the null character- or the path separator ‘/’. If the hash is converted to hexadecimal
     * format -which is usual- , it is guaranteed that the resulting string will contain only numbers and letters.
     * If more security is needed, this process can be repeated recursively.
     * @param path The path to the directory that is going to be created
     * */
    public void mkdirCommand(String path) {
        this.getProtoHandler().mkdirCallback(
                this.easyreum.getBuilder().build("MKDIR",path).translate()
                        .getBuilder().buildStatusInfo());
    }


    /**
     * RMKDIR “path/to/new/dir”
     * RMKDIR command creates a directory recursively at the specified
     * @param path The path to the directory that is going to be created
     */
    public void rmkdirCommand(String path) {
        this.getProtoHandler().rmkdirCallback(
                this.easyreum.getBuilder().build("RMKDIR",path).translate()
                        .getBuilder().buildStatusInfo());
    }

    /**
     * Command deletes the specified file. This is a special command because is the only one who can operate in a locked
     * directory. If it is the case, then DEL can only delete exactly one file: The locking file.
     * @param path The target file that is going to be deleted...
     * */
    public void delCommand(String path) {
        this.getProtoHandler().delCallback(
                this.easyreum.getBuilder().build("DEL", path).translate()
                        .getBuilder().buildStatusInfo());
    }
    /**
     * Removes a specified directory recursively.
     * @param path The path of the directory that is going to be deleted...
     * */
    public void rmdirCommand(String path) {
        this.getProtoHandler().rmdirCallback(
                this.easyreum.getBuilder().build("RMDIR", path).translate()
                        .getBuilder().buildStatusInfo());
    }
    /**
     * Copy the file indicated by the first parameter of the command to the file indicated by the second one.
     * @param pathFrom The source file that is going to be copied...
     * @param pathTo The destiny file where the file of "pathFrom" is going to be copied...
     * */
    public void copyCommand(String pathFrom, String pathTo) {
        this.getProtoHandler().copyCallback(
                this.easyreum.getBuilder().build("COPY", pathFrom, "|", pathTo).translate()
                        .getBuilder().buildStatusInfo());
    }
    /**
     * Creates a new file in the specified directory.
     * @param path The path to the file to be created.
     * */
    public void touchCommand(String path){
        this.getProtoHandler().touchCallback(
                this.easyreum.getBuilder().build("TOUCH", path).translate()
                        .getBuilder().buildStatusInfo());
    }
    /**
     * Returns the number of elapsed seconds since the epoch, and arbitrary point in the time continuum, which is the
     * Gregorian calendar time Jan 1 1970 00:00 UTC.
     * */
    public void dateCommand() {
        this.getProtoHandler().dateCallback(
                    (int)this.easyreum.getBuilder().build("DATE").translate()
                            .getBuilder().buildStatusInfo().getCode(),
                    this.easyreum.getBuilder().getStatusInfo()
                );
    }
    /**
     * Returns the current date of the server in human-readable format “yyyy-mm-dd HH:MM:SS” UTC.
     * */
    public void datefCommand(){
        try {
            this.getProtoHandler().datefCallback(
                        new SimpleDateFormat("yyyy-MM-dd H:m:s")
                                .parse(
                                        this.easyreum.getBuilder().build("DATEF").translate().
                                        getBuilder().buildStatusInfo(false)
                                                .getMessage())
                    ,
                        this.easyreum.getBuilder().getStatusInfo()
                    );
        } catch (ParseException e) {
            throw new TFExceptions(e);
        }
    }
    public void dtofCommand(long timestamp){
        try {
            this.getProtoHandler().dtofCallback(
                    new SimpleDateFormat("yyyy-MM-dd H:m:s")
                            .parse(this.easyreum.getBuilder().build("DTOF",
                                    String.valueOf(timestamp))
                            .translate().getBuilder().buildStatusInfo(false).getMessage()),
                        this.easyreum.getBuilder().getStatusInfo()
                    );
        } catch (ParseException e) {
            throw new TFExceptions(e);
        }
    }
    /**
     * Converts date in Unix timestamp format -seconds since the epoch- in human-readable format “yyyy-mm-dd HH:MM:SS”
     * UTC.
     * @param timestamp The timestamp to be converted to human-readable string.
     * */
    public void dtofCommand(String timestamp) {
        this.dtofCommand(Long.parseLong(timestamp));
        /*
        String dtofCommand = "DTOF " + timestamp;
        if (isConnect && clientSocket != null && sessionKey != null) {
            Date date = null;
            StatusInfo statusInfo;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            String info = new String(clientSocket.translate(sessionKey, dtofCommand.getBytes(), getLenChannel()));
            if ("OK".equals(info.split(" ")[0])) {
                date = dateFormat.parse(Helper.converToString(info.split(" "), true));
                statusInfo = new StatusInfo(StatusServer.valueOf(info.split(" ")[0]));
            } else {
                statusInfo = StatusInfo.build(info);
            }
            this.protoHandler.dtofCallback(date, statusInfo);
        }*/
    }
    /**
     * Converts date in human-readable format “ yyyy-mm-dd HH:MM:SS” to its Unix timestamp format.
     * @param formatter The date in human-readable format...
     * */
    public void ftodCommand(String formatter) {
        this.getProtoHandler().ftodCallback((int) new Date().getTime(),
                this.easyreum.getBuilder().build("FTOD", formatter).translate()
                        .getBuilder().buildStatusInfo());
    }
    /**
     * Returns statistics of a file or directory in the form "D | F | U FILE-SIZE  LAST-ACCESS LAST-MODIFICATION" where
     * D stands for directory; F stands for file; and U stands for unknown, only one of them is reported. The
     * “FILE-SIZE” is reported in bytes in a integer of 64 bits and the number could be up to 20 digits long.
     * The “LAST-ACCESS” and “LAST-MODIFICATION” are both timestamps
     * @param path The path to the target
     * */
    public void fstatCommand(String path){
        String[] message = this.easyreum.getBuilder().build("FSTAT", path).translate()
                .getBuilder().buildStatusInfo().getMessage().split(" ");
        if (this.easyreum.getBuilder().isStatusInfoOk())
            this.getProtoHandler().fstatCallback(
                    new FileStat(message[0].charAt(0),
                            Long.parseLong(message[1]),
                            Long.parseLong(message[2]),
                            Long.parseLong(message[3])),
                        this.easyreum.getBuilder().getStatusInfo()
                    );
        else this.getProtoHandler().fstatCallback(new FileStat(FileStat.typeEnum.UNKNOWN,0,
                        0,0), this.easyreum.getBuilder().getStatusInfo());
    }
    /**
     * Update the timestamps of the file or directory to server current time.
     * @param path The path to the target which timestamp is going to be updated
     * */
    public void fupdCommand(String path) {
        this.getProtoHandler().fupdCallback(
                this.easyreum.getBuilder().build("FUPD", path).translate()
                        .getBuilder().buildStatusInfo());
    }
    /**
     * Copy recursively the source directory into a new created directory specified in the second parameter of the
     * command.
     * @param pathFrom The source target directory
     * @param pathTo The destiny target directory
     * */
    public void cpdirCommand(String pathFrom, String pathTo) {
        this.getProtoHandler().cpdirCallback(
                this.easyreum.getBuilder().build("CPDIR", pathFrom, "|", pathTo).translate()
                        .getBuilder().buildStatusInfo());
    }
    /**
     * Copies the source file specified in the second parameter into every directory in the tree that matches “pattern”
     * with the name “newname” specified in the first parameter. This is a best-effort command instead of super-reliable
     * one. If any one of the destinations are locked or the file exists instead of return afailed status code, XCOPY
     * skips it silently. The return status of this command are relative to the source file/directory.
     * @param newName a new name for the copied file
     * @param path The target source file
     * @param pattern The pattern that specified where to copy the target source file.
     * */
    public void xcopyCommand(String newName, String path, String pattern) {
        this.getProtoHandler().xcopyCallback(
                this.easyreum.getBuilder().build("XCOPY", newName, path, "|", pattern).translate()
                        .getBuilder().buildStatusInfo());
    }
    /**
     * Deletes all files that match “filename” in the specified path at first parameter, and it does recursively start
     * at the specified directory. This is a best-effort command instead of super-reliable one. If any one of the path
     * are locked or even and error occurs, instead of return a failure return status, XDEL skips it silently. The
     * return status of this command are relative to the path specified
     * @param path The parent folder where the protocol is going to search for the files...
     * @param fileName The file name that you want to erase...
     * */
    public void xdelCommand(String path, String fileName) {
        this.getProtoHandler().xdelCallback(
                this.easyreum.getBuilder().build("XDEL", path, fileName).translate()
                        .getBuilder().buildStatusInfo());
    }
    /**
     * Deletes all directories recursively that match “directory-name” starting at the specified path at first
     * parameter. This is a best-effort command instead of super-reliable one. If any one of the path are locked or even
     * an error occurs, instead of return a failure return status, XRMDIR skips it silently. The return status of this
     * command are relative to the path specified.
     * @param path The top parent path, the protocol is going to search inside it for directoryName.
     * @param directoryName The name of the directories to be deleted...
     */
    public void xrmdirCommand(String path, String directoryName) {
        this.getProtoHandler().xrmdirCallback(
                this.easyreum.getBuilder().build("XRMDIR", path, directoryName).translate()
                        .getBuilder().buildStatusInfo());
    }
    /**
     * Copy recursively source directory specified in the second parameter to every directory
     * in the tree that matches “destination-directory-pattern” with the name
     * “new-directory-name” specified at first parameter.
     * This is a best-effort command instead of super-reliable one. If any one of the
     * destinations are locked or the directory or file exists, instead of return a failure
     * return code, XCPDIR skips it silently. The return status of this command are relative
     * to the source file/directory
     * @param newDirectory The new name of the directory that will be created at specified
     *                     location which is going be really the folder target specified at the path with a new name
     * @param path The target folder that is going to be copied...
     * @param destinationPattern The pattern that is going to be used for selecting the folders...
     * */
    public void xcpdirCommand(String newDirectory, String path, String destinationPattern) {
        this.getProtoHandler().xcpdirCallback(
                this.easyreum.getBuilder().build("XCPDIR", newDirectory, path, "|",
                                destinationPattern).translate().getBuilder().buildStatusInfo());
    }
    /**
     * Specifies a filename that when it exists in a directory no operation can be done on it,
     * except to delete the lock file. This allows a temporary blocking of a directory for any
     * sort of operations except the one eliminating the locking file which is the command
     * DEL with the path to the locking file. The above statement does not apply for commands
     * that do not specify “FAILED 30” as possible return status.
     * @param lockFileName The name of the file who locks directories...
     * */
    public void lockCommand(String lockFileName) {
        this.getProtoHandler().lockCallback(
                this.easyreum.getBuilder().build("LOCK", lockFileName).translate()
                        .getBuilder().buildStatusInfo());
    }
    @Deprecated
    public void sendFileCommand(boolean isOverWritten, String path, StatusInfo statusClient, byte[] payload){
        this.sendFileCommand(isOverWritten, path, payload);
        /*StatusInfo sendFileStatus = null;
        if (isConnect() && clientSocket != null && this.easyreum.getSessionKey() != null) {
            do {
                if (statusClient == null && sendFileStatus == null) {
                    String sendFileCommand = String.format("SNDFILE %s %s", isOverWritten ? "1" : "0", path);
                    sendFileStatus = StatusInfo
                            .build(new String(this.easyreum.translate(sendFileCommand).getPayloadReceived()));
                } else {
                    if (payload != null && payload.length - 5 > getLenChannel()) {
                        sendFileStatus = StatusInfo.build("PAYLOAD_TOO_BIG");
                    } else {
                        //include logic of other step
                        switch (Objects.requireNonNull(sendFileStatus).getStatus()) {
                            case CONT:
                                String sendCont = "CONT " + new String(Objects.requireNonNull(payload));
                                sendFileStatus = StatusInfo
                                        .build(new String(this.easyreum.translate(sendCont.getBytes())
                                                .getPayloadReceived()));
                                break;
                            case OK:
                                String sendOk = "OK";
                                sendFileStatus = StatusInfo
                                        .build(new String(this.easyreum.translate(sendOk.getBytes())
                                                .getPayloadReceived()));
                                break;
                            case BREAK:
                                String sendBreak = "BREAK";
                                sendFileStatus = StatusInfo
                                        .build(new String(this.easyreum.translate(sendBreak).getPayloadReceived()));
                                break;
                            default:
                                //part of code, even if is not in use
                                break;
                        }
                    }
                }
                this.getProtoHandler().sendFileCallback(isOverWritten, path, sendFileStatus,
                        payload);
            } while (sendFileStatus.getStatus() != StatusServer.OK &&
                    sendFileStatus.getStatus() != StatusServer.FAILED);
        }*/
    }
    /**
     * Sends a file to the server.
     * @param isOverWritten The first parameter could be either “0” for false or “1” for true.
     *                     If “/path/to/filename” it exists in the server,  this flag indicates
     *                     whether it should be overwritten or not.
     * @param path The path in the server where the file be stored
     * @param payload The data of the file to be sent...
     * */
    public void sendFileCommand(boolean isOverWritten, String path, byte[] payload) {
        this.getProtoHandler().sendFileCallback(isOverWritten, path, this.easyreum.getBuilder()
                .build("SNDFILE", isOverWritten ? "1" : "0", path).translate()
                .getBuilder().buildStatusInfo(), payload);
        do{
            if (payload != null && payload.length - (StatusServer.CONT.name().length() + 1) > getLenChannel()){
                this.getProtoHandler().sendFileCallback(isOverWritten,path,
                        StatusInfo.build("PAYLOAD_TOO_BIG"), payload);
            }
            else if (payload != null){
                this.getProtoHandler().sendFileCallback(isOverWritten, path,
                    this.easyreum.getBuilder().build(StatusServer.CONT.name().getBytes(), payload).translate()
                            .getBuilder().buildStatusInfo(), payload);
            }
            else break;
        }while (this.easyreum.getBuilder().getStatusInfo().getStatus().equals(StatusServer.CONT));
        this.getProtoHandler().sendFileCallback(isOverWritten, path,
                this.easyreum.getBuilder().build(StatusServer.OK.name()).translate().getBuilder()
                        .buildStatusInfo(), payload);
        //this.easyreum.getBuilder().build(this.easyreum.getBuilder().getStatusInfo().getStatus().name()).
        //        translate();
    }
    /**
     * Receives a file from the server.
     * @param deleteAfter  The first parameter could be either “0” for false or
     * “1” for true and tells the server whether the file must be deleted after successfully
     * received by the client.
     * @param path The path to the file in the server to be retrieved
     * */
    public void rcvFileCommand(boolean deleteAfter, String path, StatusInfo statusClient) {
        this.rcvFileCommand(deleteAfter, path);
    }
    public void rcvFileCommand(boolean deleteAfter, String path) {
        this.getProtoHandler().rcvFileCallback(deleteAfter, path, this.easyreum.getBuilder()
                .build("RCVFILE", deleteAfter ? "1" : "0", path).translate()
                .getBuilder().buildStatusInfo());
        do {
            this.getProtoHandler().rcvFileCallback(deleteAfter,path,
                    this.easyreum.translate("CONT").getBuilder()
                    .buildStatusInfo());
        } while (this.easyreum.getBuilder().getStatusInfo().getStatus().equals(StatusServer.CONT));
        /*
        StatusInfo rcvFileStatus = null;
        if ( clientSocket != null && this.easyreum.getSessionKey() != null) {
            do {
                if (rcvFileStatus == null) {
                    String sendFileCommand = String.format("RCVFILE %s %s", deleteAfter ? "1" : "0", path);
                    byte[] messagebyt = this.easyreum.translate(sendFileCommand.getBytes()).getPayloadReceived();
                    String message = new String(messagebyt);
                    if (message.split(" ")[0].equals(StatusServer.CONT.name())) {
                        byte[] payload = Arrays.copyOfRange(messagebyt, 5, messagebyt.length);
                        rcvFileStatus = StatusInfo.build(message.split(" ")[0], payload);
                    } else
                        rcvFileStatus = StatusInfo.build(message);
                } else {
                    //include logic of other step
                    switch (Objects.requireNonNull(rcvFileStatus).getStatus()) {
                        case CONT:
                            String sendCont = "CONT";
                            byte[] messagebyt = this.easyreum.translate(sendCont.getBytes()).getPayloadReceived();
                            String message = new String(messagebyt);
                            if (message.split(" ")[0].equals(StatusServer.CONT.name())) {
                                byte[] payload = Arrays.copyOfRange(messagebyt, 5, messagebyt.length);
                                rcvFileStatus = StatusInfo.build(message.split(" ")[0], payload);
                            } else
                                rcvFileStatus = StatusInfo.build(message);
                            break;
                        case BREAK:
                            String sendBreak = "BREAK";
                            rcvFileStatus = StatusInfo
                                    .build(new String(
                                            this.easyreum.translate(sendBreak.getBytes()).getPayloadReceived()));
                            break;
                        default:
                            //default statement , even if is not in use
                            break;
                    }
                }
                this.getProtoHandler().rcvFileCallback(deleteAfter, path, rcvFileStatus);
            } while (rcvFileStatus.getStatus() != StatusServer.OK && rcvFileStatus.getStatus() != StatusServer.FAILED);
        }*/
    }
    /**
     * Command list the directory entries for the indicated path, if the argument is missing,
     * it lists the root directory of the protocol daemon. The return value of this command
     * is a file with the listed content. In fact, it is like issuing the command RCVFILE to
     * a temporary file with the listed content of the directory.The file returned by LS has
     * the following syntax.F | D | U /path/to/file-or-directoryThe F stands for “file”; the
     * D for “directory” and the U for “unknown”.
     * @param path The path to the folder to be listed...
     * */
    public void lsCommand(String path, StatusInfo statusClient) {
        String lsCommand = "LS " + path;
        StatusInfo lsStatus = null;
        if (this.easyreum.getSocket() != null && this.easyreum.getSessionKey() != null) {
            do {
                if (statusClient == null && lsStatus == null) {
                    String message = new String(this.easyreum.translate(lsCommand).getPayloadReceived());
                    if (message.split(" ")[0].equals(StatusServer.CONT.name()))
                        lsStatus = this.easyreum.getBuilder().buildStatusInfo();
                    else
                        lsStatus = StatusInfo.build(message);
                } else {
                    String sendCont = "CONT";
                    String message = new String(this.easyreum.translate(sendCont.getBytes()).getPayloadReceived());
                    if (message.split(" ")[0].equals(StatusServer.CONT.name()))
                        lsStatus = this.easyreum.getBuilder().buildStatusInfo();
                    else if (message.split(" ")[0].equals(StatusServer.OK.name()))
                        lsStatus = StatusInfo.build(message);
                    else
                        lsStatus = StatusInfo.build(StatusServer.CONT.name(), message.getBytes());
                }
                this.getProtoHandler().lsCallback(lsStatus);
            } while (lsStatus.getStatus() != StatusServer.OK && lsStatus.getStatus() != StatusServer.FAILED);
        }
    }
    public void lsCommand(String path){
        String lsCommand = "LS " + path;
        StatusInfo lsStatus = null;
        if (this.easyreum.getSocket() != null && this.easyreum.getSessionKey() != null) {
            do {
                if (lsStatus == null) {
                    String message = new String(this.easyreum.translate(lsCommand).getPayloadReceived());
                    if (message.split(" ")[0].equals(StatusServer.CONT.name()))
                        lsStatus = this.easyreum.getBuilder().buildStatusInfo();
                    else
                        lsStatus = StatusInfo.build(message);
                } else {
                    String sendCont = "CONT";
                    String message = new String(this.easyreum.translate(sendCont.getBytes()).getPayloadReceived());
                    if (message.split(" ")[0].equals(StatusServer.CONT.name()))
                        lsStatus = this.easyreum.getBuilder().buildStatusInfo();
                    else if (message.split(" ")[0].equals(StatusServer.OK.name()))
                        lsStatus = StatusInfo.build(message);
                    else
                        lsStatus = StatusInfo.build(StatusServer.CONT.name(), message.getBytes());
                }
                this.getProtoHandler().lsCallback(lsStatus);
            } while (lsStatus.getStatus() != StatusServer.OK && lsStatus.getStatus() != StatusServer.FAILED);
        }
    }
    /**
     * Command list the directory entries for the indicated path, if the argument is missing,
     * it lists the root directory of the protocol daemon. The return value of this command
     * is a file with the listed content. In fact, it is like issuing the command RCVFILE to
     * a temporary file with the listed content of the directory.The file returned by LS has
     * the following syntax.F | D | U /path/to/file-or-directoryThe F stands for “file”; the
     * D for “directory” and the U for “unknown”.
     * @param path The path to the folder to be listed...
     * */
    public void lsrCommand(String path) {
        this.getProtoHandler().lsrCallback(this.easyreum.getBuilder().build("LSR", path).translate()
                .getBuilder().buildStatusInfo());
        do {
            this.getProtoHandler().lsrCallback(this.easyreum.translate("CONT").getBuilder()
                    .buildStatusInfo());
        } while (
                this.easyreum.getBuilder().getStatusInfo().getStatus() == StatusServer.CONT);
    }
    @Deprecated
    public void lsrCommand(String path, StatusInfo statusClient){
        this.lsrCommand(path);
    }
    @Deprecated
    public void renamCommand(String pathOldName, String operator, String pathNewName) {
        this.renamCommand(pathOldName, pathNewName);
    }
    /**
     * Renames the file or directory specified at first parameter into the name specified at
     * second parameter. RENAM operates atomically; there is no instant at which “newname” is
     * non-existent between the operation’s steps if “newname” already exists. If a system
     * crash occurs, it is possible for both names “oldname” and “newname” to still exist,
     * but “newname” will be intact.RENAM has some restrictions to operate.1) “oldname” it
     * must exist.2) If “newname” is a directory must be empty.3) If “oldname” is a directory
     * then “newname” must not exist or it has to be an empty directory.4) The “newname”
     * must not specify a subdirectory of the directory “oldname” which is being renamed.
     * @param pathOldName The target to be renamed
     * @param pathNewName The new name for this target
     * */
    public void renamCommand(String pathOldName, String pathNewName) {
        this.getProtoHandler().renamCallback(
                this.easyreum.getBuilder().build("RENAM", pathOldName, "|", pathNewName)
                        .translate().getBuilder().buildStatusInfo()
        );
    }

    /**
     * Sets the configuration parameters for the TCP keepalive feature. This is especially
     * useful for clients behind NAT boxes. If there is some idle time in the established
     * connection -no data transmission- the NAT box could close or unset the connection
     * without the peers knowing it. In contexts where it is predictable that an established
     * connection could be ‘in silent’ for long periods of time, and it is possible that
     * clients are behind NAT boxes, it is necessary to set the TCP keepalive packets.
     * @param isOn The first parameter of the command could be 0 or 1, meaning on or off.
     * @param timeConnection The second parameter is the time (in seconds) the connection
     *                      needs to remain idle before TCP starts sending keepalive probes.
     * @param interval  The third parameter is the time (in seconds) between individual
     *                  keepalive probes.
     * @param count  The fourth parameter is the maximum number of keepalive probes TCP
     *               should send before dropping the connection.
     * */
    public void keepAliveCommand(String isOn, int timeConnection, int interval, int count) {
        this.getProtoHandler().keepAliveCallback(
                this.easyreum.getBuilder().build("KEEPALIVE", isOn, String.valueOf(timeConnection),
                                "|", String.valueOf(interval), "|", String.valueOf(count)).translate()
                        .getBuilder().buildStatusInfo());
    }

    /**Login into system this is required for many others commands...
     * @param user The username for the login action
     * @param pass The password for the login action...
     * */
    public void loginCommand(String user, String pass) {
        this.getProtoHandler().loginCallback(this.easyreum
                .getBuilder().build("LOGIN", user, pass).translate()
                .getBuilder().buildStatusInfo());
    }
    /**Change the access modifier to a new one...
     * @param pathToFile The location to the file to be modified...
     * @param octalMode The octal modes stands for 000 or 777 etc in octal representation 000 means not
     *                 permission for owner not permission for user and nor for group */
    public void chmodCommand(String pathToFile, String octalMode) {
        this.getProtoHandler().chmodCallback(this.easyreum
                .getBuilder().build("CHMOD", pathToFile, octalMode)
                .translate().getBuilder().buildStatusInfo());
    }
    /** Change the owner of a file to a new one ... this commands is similar to chown command at unix systems..
     * @param pathToFile The location to the file
     * @param user The new username who is going to owns the file
     * @param group The new group for the file*/
    public void chownCommand(String pathToFile, String user, String group) {
        this.getProtoHandler().chownCallback(this.easyreum
                .getBuilder().build("CHOWN", pathToFile, user, group)
                .translate().getBuilder().buildStatusInfo());
    }
    /**
     * Sube un archivo hacia el servidor.
     * @param payload Espera un input stream donde pueda leer la informacion a ser
     *                      subida. Puede ser cualquier objeto que herede de InputStream
     * @param pathToFile Determina el camino hacia el archivo del servidor a subir, sino existe
     *                   lo crea.
     * @param offset La posicion del puntero desde donde se va a empezar a escribir en el archivo
     *               del servidor.
     * @param bufferSize El tamano propuesto por el cliente para el buffer, el buffer definitivo
     *                   lo enviara el servidor en caso de OK
     * @param canPt Cancellation Points, determina los puntos de cancelacion donde el usuario,
     *              tendra que leer del servidor para saber si continua o no.
     * @throws IOException En caso de que o bien no se pueda leer del InputStream que el
     * usuario envio o en caso de algun error en el socket.
     * */
    public void putCanCommand(InputStream payload, String pathToFile,
                              long offset, int bufferSize, long canPt) {
        String pcanCommand = "PUTCAN " + pathToFile + " ";
        if (offset < 0) offset = 0;
        if (canPt < 0) canPt = 0;
        if (this.easyreum.getSocket() != null && this.easyreum.getSessionKey() != null) {
            Easyreum easy = new Easyreum(this.easyreum.getClient());
            easy.getBuilder().build(pcanCommand)
                    .getBuilder().build(offset)
                    .getBuilder().build((long)bufferSize)
                    .getBuilder().build(canPt).translate().clear();
            this.getProtoHandler().putCanCallback(easy.getBuilder().buildStatusInfo(),null);
            if (Objects.requireNonNull(easy.getBuilder().buildStatusInfo()).getCode() == 0) {
                easy.setHeaderSize(Long.BYTES);
                easy.setBuffer(Integer.parseInt(Objects.requireNonNull(easy.getBuilder()
                        .buildStatusInfo()).getMessage()));
                easy.setDummy(HPFCONT);
                long i = 0;
                do {
                    if (canPt > 0 && i++ == canPt) {
                        i = 0;
                        easy.receiveHeader();
                        easy.setDummyState(true);
                        if (easy.getHeader() == HPFCANCEL){
                            easy.setDummy(HPFCANCEL);
                            this.getProtoHandler().putCanCallback(null, easy);
                            break;
                        }else if (easy.getHeader() == HPFCONT){
                            easy.setDummy(HPFCONT);
                            this.getProtoHandler().putCanCallback(null, easy);
                        }else {
                            this.getProtoHandler().putCanCallback(null, easy);
                            throw new TFExceptions(StatusServer.FAILED,
                                    TFExceptions.ErrorCodes.CAN_PUT,
                                    "Some error ocurred while trying to handle canpt");
                        }
                        easy.setDummyState(false);
                        continue;
                    }
                    byte[] payl = new byte[easy.getBufferAsInt()];
                    int read = 0;
                    try {
                        read = payload.read(payl,0,(int)easy.getBuffer());
                    } catch (IOException e) {
                        throw new TFExceptions(e);
                    }
                    easy.getBuilder().build(Arrays.copyOfRange(payl,0,read)).send().clear();
                    easy.setDummy(read);
                    this.getProtoHandler().putCanCallback(null, easy);
                    if ((short) easy.getDummy() == HPFSTOP ||
                            (short) easy.getDummy() == HPFCANCEL) {
                        easy.getBuilder().build((short) easy.getDummy(),
                                new byte[0]).useCustomHeader(true).send().clear();
                        break;
                    }
                    try {
                        if (payload.available() == 0 ||
                                (short)easy.getDummy() == HPFEND) {
                            easy.getBuilder().build((int)HPFEND, new byte[0])
                                    .useCustomHeader(true).send().clear();
                            easy.setDummy(HPFEND);
                            this.getProtoHandler().putCanCallback(null, easy);
                            break;
                        }
                    } catch (IOException e) {
                        throw new TFExceptions(e);
                    }
                } while (true);
            }
        }
    }
    /**
     * Descarga un archivo desde el servidor.
     * @param output_stream Espera un output stream donde pueda escribir la informacion
     *                      descargada. Puede ser cualquier objeto que herede de OutputStream
     * @param pathToFile Determina el camino hacia el archivo del servidor a descargar.
     * @param offset La posicion del puntero desde donde se va a empezar a leer en el archivo
     *               del servidor.
     * @param bufferSize El tamano propuesto por el cliente para el buffer, el buffer definitivo
     *                   lo enviara el servidor en caso de OK
     * @param canPt Cancellation Points, determina los puntos de cancelacion donde el usuario,
     *              tendra que leer del servidor para saber si continua o no.
     * @throws IOException En caso de que o bien no se pueda escribir al OutputStream que el
     * usuario envio o en caso de algun error en el socket.
     * */
    public void getCanCommand(OutputStream output_stream, String pathToFile,
                              long offset, int bufferSize, long canPt){
        offset = Math.max(offset, 0);
        canPt = Math.max(canPt, 0);
        this.easyreum.getBuilder().build(true, "GETCAN",pathToFile)
                .getBuilder().build(offset)
                .getBuilder().build((long) bufferSize)
                .getBuilder().build(canPt).translate().clear();
        this.getProtoHandler().getCanCallback(this.easyreum.getBuilder().buildStatusInfo(), null);
        if (this.easyreum.getBuilder().getStatusInfo() != null &&
                Objects.requireNonNull(this.easyreum.getBuilder().getStatusInfo()).getCode() == 0) {
            this.easyreum.setHeaderSize(Long.BYTES);
            this.easyreum.setBuffer(Integer.parseInt(
                    Objects.requireNonNull(this.easyreum.getBuilder().getStatusInfo()).getMessage()));
            long i = 0;
            do {
                if (canPt > 0 && i++ == canPt) {
                    i = 0;
                    this.easyreum.setDummyState(true);
                    this.getProtoHandler().getCanCallback(null,
                            this.easyreum);
                    if (this.easyreum.getDummy() == null)
                        this.easyreum.setDummy(HPFCONT);
                    this.easyreum.getBuilder().build((short)this.easyreum.getDummy(),
                            new byte[0]).useCustomHeader(true).send().clear();
                    if ((short)this.easyreum.getDummy() == HPFCANCEL){
                        break;
                    }
                    this.easyreum.setDummyState(false);
                    continue;
                }
                this.easyreum.useCustomBuffer(true).receiveHeader();
                if (this.easyreum.getHeader() == HPFCANCEL){
                    this.easyreum.setDummy(HPFCANCEL);
                    this.getProtoHandler().getCanCallback(null, this.easyreum);
                    break;
                }
                else if (this.easyreum.getHeader() == HPFEND){
                    this.easyreum.setDummy(HPFEND);
                    this.getProtoHandler().getCanCallback(null, this.easyreum);
                    break;
                }
                this.easyreum.useCustomBuffer(true).receiveBody();
                try {
                    output_stream.write(this.easyreum.getData());
                } catch (IOException e) {
                    throw new TFExceptions(e);
                }
                this.easyreum.setDummy(this.easyreum.getData().length);
                this.getProtoHandler().getCanCallback(null,this.easyreum);
            } while (true);
        }
    }
    /**
     * Command is sent by the client to the server in order to terminate the TCP connection
     * */
    public void endCommand() {
        this.easyreum.getBuilder().build("END").send();
    }
    /**
     * Makes a sha256 hash of a file indicated by ‘path’. This command is intended to
     * apply a hash to a file before downloading it in order to guarantee its integrity.
     * Be aware that on very large files the time to resolve the digest function could be
     * potentially long.
     * @param path The path to the target file...
     * */
    public void sha256Command(String path){
        this.getProtoHandler().sha256Callback(this.easyreum
                .getBuilder().build("SHA256", path).translate().getBuilder().buildStatusInfo());
    }
    /**
     * Retrieves a unique key generated by the server’s instance that communicates with the
     * client. This unique key could be used later to identify that instance. One of these
     * uses, but not the only one, is to test whether the server or even the socket
     * communication line is still opened, in other words: the keepalive mechanism from the
     * client side perspective.
     * */
    public void prockeyCommand() {
        this.getProtoHandler().prockeyCallback(this.easyreum.translate("PROCKEY")
                .getBuilder().buildStatusInfo());
    }
    /**
     * In-jails the TFProtocol daemon in the directory specified by the second
     * parameter. This is achieved only if “secure_token”, specified as first parameter,
     * is permitted for that directory
     * @param secureToken This token allows you to change the jail folder to isolate your
     *                    tfprotocol instance
     * @param pathToJailDirectory The jail folder where tfprotocol will be executing (Use only
     *                            if your secure token has access to it).
     * */
    public void injailCommand(String secureToken, String pathToJailDirectory) {
        super.getProtoHandler().injailCallback(this.easyreum.getBuilder().build(
                "INJAIL", secureToken, "|", pathToJailDirectory)
                .translate().getBuilder().buildStatusInfo());
    }
    /** Get Command download data from server asynchronously to any OutputStream the user wants...
     * @param stream It is the stream where data will resides after downloading, could be in memory or in disk etc...
     * @param path The path IN THE SERVER where the data is located at...
     * @param offset The offset is used for when we are reading the data start reading since the offset position for
     * example if the data is 100 bytes long then if offset is 20 I will read since 20 to 100 skiping first 20 bytes...
     * this is useful for when we stop an operation and wanted to restart it again later...
     * @param buff_sz Is the size of the buffer, while bigger faster will be the communication, of course server wont
     *                give you always the amount you request, because may be that server's bandwidth is getting filled
     *                ... */
    public void getCommand(RandomAccessFile stream, String path, long offset, int buff_sz){
        this.getProtoHandler().getReadCallback(this.easyreum.getBuilder().build("GET", path)
                .getBuilder().build(" ")
                .getBuilder().build(offset)
                .getBuilder().build((long)buff_sz).translate()
                .getBuilder().buildLongCode());
        try{
            stream.seek(offset);
        }catch(IOException ex) {throw new TFExceptions(ex);}
        if (this.easyreum.getBuilder().isStatusInfoOk()){
            Codes codes = new Codes(this.easyreum);
            Easythread teasy;
            try {
                this.easythread = new Easythread(this.mutex,
                        this.getProtoHandler().getClass().getMethod("getWriteCallback"
                        , Codes.class));
                teasy = new Easythread(this.mutex, this.getClass().getMethod("getCommandThread",
                        RandomAccessFile.class, Codes.class));
            } catch (NoSuchMethodException e) {
                throw new TFExceptions(e, "FATAL ERROR!!! incorrect callback found, reinstall module...");
            }
            this.easythread.setup(this.getProtoHandler(), codes);
            teasy.setup(this, stream, codes);
            this.easythread.start();
            teasy.start();

            synchronized (this.mutex){
                while (!this.easythread.is_finished() || !teasy.is_finished()){
                    try {
                        this.mutex.wait();
                        if (this.easythread.is_finished() && codes.sending_signal)
                            codes.sendGet(HPFFIN);
                    } catch (InterruptedException e) {
                        throw new TFExceptions(e);
                    }
                }
            }
            // Final Handshake
            if (codes.command != HPFFIN)
                this.easyreum.getBuilder().build(HPFFIN).sendJust();
            if (this.easyreum.getHeader() != HPFFIN)
                this.easyreum.receiveHeader();
            this.getProtoHandler().getReadCallback(new StatusInfo(StatusServer.OK, HPFFIN, ""));
            this.easyreum.reset();
        }
    }
    /**
     * This command is intended to be used ONLY by the getCommand function not by user DO NOT CALL THIS METHOD DIRECTLY
     * !!!
     * @deprecated  NOT FOR DIRECT USE !!! (EVEN IF YOU CALL IT NOT ANSWER WILL BE DELIVERED BACK.
     * */
    @Deprecated
    public void getCommandThread(RandomAccessFile stream, Codes codes){
        this.easyreum.setHeaderSize(Long.BYTES);
        this.easyreum.setBuffer((int)
                Objects.requireNonNull(this.easyreum.getBuilder().getStatusInfo()).getCode());
        while (true){
            try {
                this.easyreum.receiveHeader();
                int header = this.easyreum.getHeader();
                if (!(codes.sending_signal && header > HPFEND))
                    this.getProtoHandler().getReadCallback(
                        new StatusInfo(StatusServer.OK, header, "")
                    );
                if (header <= HPFEND) {
                    codes.sending_signal = false;
                    return;
                }
                this.easyreum.receiveBody();
                if (!codes.sending_signal){
                    stream.write(this.easyreum.getData());
                }
            } catch (IOException e) {
                throw new TFExceptions(e);
            }
        }
    }
    /**
     * Upload a stream of data allowing to cancel at any moment. Asynchronously
     * @param stream It is the stream where data will resides after downloading, could be in memory or in disk etc...
     * @param path The path IN THE SERVER where the data is located at...
     * @param offset The offset is used for when we are reading the data start reading since the offset position for
     * example if the data is 100 bytes long then if offset is 20 I will read since 20 to 100 skiping first 20 bytes...
     *      this is useful for when we stop an operation and wanted to restart it again later...
     * @param buff_sz Is the size of the buffer, while bigger faster will be the communication, of course server wont
     *                give you always the amount you request, because may be that server's bandwidth is getting filled
     * */
    public void putCommand(RandomAccessFile stream, String path, long offset, int buff_sz){
        this.getProtoHandler().putStatusCallback(this.easyreum.getBuilder().build("PUT", path)
                .getBuilder().build(" ")
                .getBuilder().build(offset)
                .getBuilder().build((long)buff_sz).translate()
                .getBuilder().buildLongCode());
        try{
            stream.seek(offset);
        }catch(IOException ex){ throw new TFExceptions(ex);}
        if (this.easyreum.getBuilder().isStatusInfoOk()) {
            this.easyreum.setHeaderSize(Long.BYTES);
            this.easyreum.setBuffer((int)
                    Objects.requireNonNull(this.easyreum.getBuilder().getStatusInfo()).getCode());
            Codes codes = new Codes(this.easyreum);

            Easythread teasy;
            try {
                // Start the backend thread or the second thread...
                teasy = new Easythread(this.mutex, this.getClass().getMethod("putCommandThread",
                        RandomAccessFile.class, Codes.class));
            } catch (NoSuchMethodException e) {
                throw new TFExceptions(e, "FATAL ERROR!!! incorrect callback found, reinstall module...");
            }
            teasy.setup(this, stream, codes);
            teasy.start();
            do {
                this.easyreum.receiveHeader();
            }while(this.easyreum.getHeader() > 0);
            codes.rcvSignal = true;
            synchronized (this.mutex) {
                while (!teasy.is_finished()){// || !list_thread.is_finished()) {
                    try {
                        this.mutex.wait();
                    } catch (InterruptedException e) {
                        throw new TFExceptions(e);
                    }
                }
            }
            // Final Handshake
            if (this.easyreum.getHeader() != HPFFIN)
                this.easyreum.receiveHeader();
            codes.sendPut(HPFFIN);
            codes.block = true;
            this.getProtoHandler().putCallback(codes);
            this.easyreum.reset();
        }
    }
    /** Put Command Thread Function it is not intended to be used by user... it is needed for putCommand function use
     * putCommand() instead
     * @deprecated This method is not intended to be used for user read at description...
     * @param codes The method for codes calls
     * @param stream The stream from where we retrieve data to upload to server
     * */
    @Deprecated
    public void putCommandThread(RandomAccessFile stream, Codes codes){
        byte[] buff = new byte[this.easyreum.getBuffer()];
        int read;
        while (true){
            try {
                if (!codes.rcvSignal)
                {
                    if ((read = stream.read(buff)) <= 0){
                        this.easyreum.getBuilder().build(HPFEND).sendJust();
                        return;
                    }
                    this.easyreum.getBuilder().build(Arrays.copyOf(buff, read)).send();
                }else
                    return;
                this.getProtoHandler().putCallback(codes);
                this.getProtoHandler().putStatusCallback(new StatusInfo(StatusServer.OK,
                        read,""));
            } catch (IOException e) {
                throw new TFExceptions(e, "Some error ocurred while trying to upload data... " +
                        "retry again later");
            }
        }
    }
    /** Change the current session key to another session key with any arbitrary size
     * @param keylen Is the session key new length
     * */
    public void nigmaCommand(int keylen){
        if (keylen % 4 != 0) throw new TFExceptions(-1, "Invalid key length, key length must be multiple " +
                "of 4...");
        keylen = Math.max(8,keylen);
        this.easyreum.getBuilder().build("NIGMA", String.valueOf(keylen));
        this.easyreum.translate();
        if (StatusServer.valueOf(new String(this.easyreum.getPayloadReceived()))
                .equals(StatusServer.OK)){
            int hdr = ByteBuffer.wrap(this.easyreum.receiveBuffer(Integer.BYTES)).getInt();
            this.easyreum.setSessionKey(this.easyreum.receiveBuffer(hdr));
            this.getProtoHandler().nigmaCallback(new StatusInfo(
                    StatusServer.OK,0, new String(this.easyreum.getSessionKey())));
        }else this.getProtoHandler().nigmaCallback(this.easyreum.getBuilder().getStatusInfo());
    }
    /**
     * Removes a specified secure directory recursively. The first parameter is
     * the secure token that allows to remove the directory specified in the second
     * parameter. The secure token could be either a file or a directory named as the first
     * parameter, inside the directory specified as the second parameter.
     * @param secureToken This token allows you to remove the secure folder
     * @param pathToSD The target folder to be deleted.
     * */
    public void rmsdCommand(String secureToken, String pathToSD){
        this.getProtoHandler().rmSecureDirectoryCallback(
                this.easyreum.getBuilder().build("RMSECDIR", secureToken, " | ",pathToSD)
                        .translate().getBuilder().buildStatusInfo()
        );
    }

    public void tlbCommand(){
        this.getProtoHandler().tlbCallback(
                this.easyreum.getBuilder().build("TLB")
                        .translate().getBuilder().buildStatusInfo()
        );
    }
    public void tlbUDPCommand(){
        EasyreumUDP udp = new EasyreumUDP(this.easyreum.getClient());
        udp.getBuilder().build("TLB").send().getBuilder().buildStatusInfo();
        udp.getBuilder().build((byte)3).send();
        this.getProtoHandler().tlbUDPCallback(
                new StatusInfo(StatusServer.OK, udp.receiveBuffer(100))
        );
    }
    public boolean sdownCommand(String path, OutputStream stream, int timeout) throws TFExceptions{
        timeout *= 1000;
        int temp = 0;
        try {
            temp = this.easyreum.getSocket().getSoTimeout();
            this.easyreum.getSocket().setSoTimeout(timeout > 0? timeout: temp);
        } catch (SocketException ignored) {}
        this.easyreum.getBuilder().build("SDOWN", path).send();
        boolean hasError = false;
        do{
            this.easyreum.receiveHeader();
            if (this.easyreum.getHeader() > 0) {
                try {
                    stream.write(this.easyreum.receiveBody());
                } catch (IOException ignored) {
                    hasError = true;
                }
            }
            else break;
        }while (true);
        try{
            if (temp != 0)
                this.easyreum.getSocket().setSoTimeout(temp);
        }catch (SocketException ignored){}
        this.getProtoHandler().sdownCallback(this.easyreum.getBuilder().buildStatusInfo()); 
        return this.easyreum.getHeader() == 0 && !hasError;
    }
    public boolean supCommand(String path, InputStream stream, int timeout) throws TFExceptions{
        timeout *= 1000;
        int temp = 0;
        try {
            temp = this.easyreum.getSocket().getSoTimeout();
            this.easyreum.getSocket().setSoTimeout(timeout > 0? timeout: temp);
            this.easyreum.getBuilder().build("SUP", path).send();
            this.easyreum.setHeaderSize(Integer.BYTES);
            byte[] payload = new byte[this.getLenChannel()];
            while (stream.available() > 0){
                int read = stream.read(payload, 0, payload.length);
                if (read > 0)
                    this.easyreum.getBuilder().build(Arrays.copyOfRange(payload, 0, read)).send();
            }
            this.easyreum.getBuilder().build(0).sendJust();
            this.easyreum.receive();
        }catch (Exception exception){
            if (exception.getClass().equals(TFExceptions.class)){
                //if (((TFExceptions) exception).getOriginalException()
                //        .getClass().equals(java.net.SocketTimeoutException.class)){
                    try {
                        this.easyreum.getSocket().close();
                    }catch (IOException ignored){}
                    throw new TFExceptions(TFExceptions.ErrorCodes.ON_WRITE_OR_RECEIVE_TO_SOCKET.ordinal(),
                            "Socket exception");
                //}
            }else
                this.easyreum.getBuilder().build(-1).sendJust();
            return false;
        }finally {
            try{
                if (temp != 0)
                    this.easyreum.getSocket().setSoTimeout(temp);
            }catch (SocketException ignored){}
        }
        this.getProtoHandler().supCallback(this.easyreum.getBuilder().buildStatusInfo()); 
        return this.easyreum.getHeader() == 0;
    }
    public void fsizeCommand(String path){
        this.easyreum.getBuilder().build("FSIZE", path).send();
        long res = ByteBuffer.wrap(this.easyreum.receiveBuffer(Long.BYTES)).order(ByteOrder.BIG_ENDIAN)
                .getLong();
        this.getProtoHandler().fsizeCallback(new StatusInfo(StatusServer.OK, res, String.valueOf(res)));
    }
    public void fsizelsCommand(String path) {
        this.easyreum.getBuilder().build("FSIZELS", path).send();
        long res;
        do {
            res = ByteBuffer.wrap(this.easyreum.receiveBuffer(Long.BYTES)).order(ByteOrder.BIG_ENDIAN)
                    .getLong();
            this.getProtoHandler().fsizelsCallback(new StatusInfo(StatusServer.CONT, res, null));
        } while (res != -3);
    }
    // 26/12/2021
    public void lsv2Command(String pathToList, String pathToFileToStore) {
        this.getProtoHandler().lsv2Callback(this.easyreum.getBuilder().build("LSV2", pathToList + "@||@" + pathToFileToStore).translate()
                .getBuilder().buildStatusInfo());
    }
    public void lsrv2Command(String pathToList, String pathToFileToStore) {
        this.getProtoHandler().lsrv2Callback(this.easyreum.getBuilder().build("LSRV2", pathToList + "@||@" + pathToFileToStore).translate()
                .getBuilder().buildStatusInfo());
    }
    /**
     * LSV2DOWN lists the directory indicated by the first parameter, it send 
     * the list to the client directly. One file per line. If a line ends with 
     * slash ‘/’ it means is a directory, otherwise it could be any other file.
     * @param pathToList Path to be listed.
     * @return OK
     * @return FAILED 20 : Source path is not a directory.
     * @return FAILED 1 : Access denied to location.
     * @return FAILED 30 : Directory is locked.
     * @return FAILED 53 : Failed running LSRV2.
     */
    public void lsv2DownCommand(String pathToList) {
        this.getProtoHandler().lsv2DownCallback(
            this.easyreum.getBuilder().build("LSV2DOWN", pathToList).translate().getBuilder().buildStatusInfo());

        if(this.easyreum.getBuilder().isStatusInfoOk()){
            try {
                this.easyreum.receiveUntil(this.getClass().getMethod("DownReadHeader", int.class),
                        this, this.getProtoHandler(), "lsv2DownCallback");
                this.getProtoHandler().lsv2DownCallback(
                    this.easyreum.getHeader()==-10 ? new StatusInfo(StatusServer.OK, this.easyreum.getHeader(), "Successfull transmition")
                                                    : new StatusInfo(StatusServer.FAILED, this.easyreum.getHeader(), "Failed transmition"));
            } catch (NoSuchMethodException e) {
                throw new TFExceptions(e);
            }
        }
    }

    public boolean DownReadHeader( int header){
        return (header ==-10 || header ==-11);
    }
   /**
     * LSRV2DOWN lists the directory indicated by the first parameter using recursion,
     * it send the list to the client directly. One file per line. If a line ends with 
     * slash ‘/’ it means is a directory, otherwise it could be any other file.
     * @param pathToList Path to be listed.
     * @return OK
     * @return FAILED 20 : Source path is not a directory.
     * @return FAILED 1 : Access denied to location.
     * @return FAILED 30 : Directory is locked.
     * @return FAILED 53 : Failed running LSRV2.
     */
    public void lsrv2DownCommand(String pathToList) {
        this.getProtoHandler().lsrv2DownCallback(
            this.easyreum.getBuilder().build("LSRV2DOWN", pathToList).translate().getBuilder().buildStatusInfo());
        
        if(this.easyreum.getBuilder().isStatusInfoOk()){
            try {
                this.easyreum.receiveUntil(this.getClass().getMethod("DownReadHeader", int.class),
                    this, this.getProtoHandler(), "lsrv2DownCallback");
                this.getProtoHandler().lsrv2DownCallback(
                         this.easyreum.getHeader()==-10 ? new StatusInfo(StatusServer.OK, this.easyreum.getHeader(), "Successfull transmition")
                                                        : new StatusInfo(StatusServer.FAILED, this.easyreum.getHeader(), "Failed transmition")
                );
            } catch (NoSuchMethodException e) {
                     throw new TFExceptions(e);
            }
        }
    }
    public void ftypeCommand(String pathToFile){
        this.easyreum.getBuilder().build("FTYPE", pathToFile).send();
        byte type = this.easyreum.receiveBuffer(1)[0];
        if (type != -1)
            this.getProtoHandler().fstypeCallback(
                    TfprotocolConsts.FSTYPE.values()[type]);
        else this.getProtoHandler().fstypeCallback(
                TfprotocolConsts.FSTYPE.ERROR);
    }
    public void ftypelsCommand(String pathToFile) {
        this.easyreum.getBuilder().build("FTYPELS", pathToFile).send();
        byte type;
        while((type = this.easyreum.receiveBuffer(1)[0])!= -2){
            if (type != -1)
                this.getProtoHandler().fstypelsCallback(
                        TfprotocolConsts.FSTYPE.values()[type]);
            else this.getProtoHandler().fstypeCallback(
                    TfprotocolConsts.FSTYPE.ERROR);
        }
    }
    public void fstatlsCommand(String pathToFile){
        this.easyreum.getBuilder().build("FSTATLS", pathToFile).send();
        byte[]fstathdr;
        while((fstathdr = this.easyreum.receiveBuffer(26))[0] != -2)
            if (fstathdr[0] != -1)
                this.getProtoHandler().fstatlsCommand(fstathdr[0],
                        TfprotocolConsts.FSTYPE.values()[fstathdr[1]],
                        ByteBuffer.wrap(fstathdr, 2, Long.BYTES).getLong(),
                        ByteBuffer.wrap(fstathdr, 10, Long.BYTES).getLong(),
                        ByteBuffer.wrap(fstathdr, 18, Long.BYTES).getLong());
            else this.getProtoHandler().fstatlsCommand(fstathdr[0],
                        TfprotocolConsts.FSTYPE.ERROR,
                        0,
                        0,
                        0);
    }

    public void integrityWriteCommand(RandomAccessFile file, String path, byte[] sha256) {
        byte[] content = FileManager.ReadWholeFile(file);
        if (sha256 != null)
            this.integrityWriteCommand(content, path, sha256);
        else this.integrityWriteCommand(content, path);
    }
    public void integrityWriteCommand(RandomAccessFile file, String path) {
        this.integrityWriteCommand(file, path, null);
    }
    public void integrityWriteCommand(byte[] content, String path) {
        this.integrityWriteCommand(content, path, Cryptography.sha256(content));
    }
    public void integrityWriteCommand(byte[] content, String path, byte[] sha256) {
        this.easyreum.getBuilder().build("INTWRITE", path).send();
        this.easyreum.getBuilder().build(sha256).sendJust();
        this.getProtoHandler().integrityWriteCallback(
                this.easyreum.debug().getBuilder().build(content).translate()
                .getBuilder().buildStatusInfo());
    }
    public void integrityReadCommand(byte[] content, String path, byte[] csha256) {
        this.easyreum.getBuilder().build("INTREAD", path).send();
        this.easyreum.receiveHeader();
        if (this.easyreum.getHeader() < 0) return;
        byte[] sha256 = this.easyreum.receiveBuffer(66);
        boolean success = false;
        if (Arrays.equals(csha256 != null ? csha256 :  Cryptography.sha256(content), sha256)){
            this.getProtoHandler().integrityReadCallback(
                    new StatusInfo(StatusServer.OK, 0, "Files does matchs"));
            success = true;
        }else{
            this.getProtoHandler().integrityReadCallback(
                    new StatusInfo(StatusServer.FAILED, -1,
                        "Files doesn't match but you can ignore this anyway"));   
        }
        this.easyreum.receiveBody();
        if (success){
            StatusInfo si = this.easyreum.getBuilder().buildStatusInfo();
            si.setStatus(StatusServer.OK);
            this.getProtoHandler().integrityReadCallback(si);
        }
    }
    public void integrityReadCommand(byte[] content, String path) {
        this.integrityReadCommand(content, path, null);
    }
    public void integrityReadCommand(RandomAccessFile file, String path) {
        this.integrityReadCommand(FileManager.ReadWholeFile(file), path, null);
    }
    // Para comprobar con un hash X instead of el hash del file que es el que se usa por defecto
    public void integrityReadCommand(RandomAccessFile file, String path, byte[] sha256) {
        this.integrityReadCommand(FileManager.ReadWholeFile(file), path, sha256);
    }
    
    public int netlockCommand(int seconds, String path){
        StatusInfo si = this.easyreum.getBuilder()
                .build("NETLOCK", String.valueOf(seconds), path).translate()
                .getBuilder().buildStatusInfo();
        this.getProtoHandler().netlockCallback(si);
        return (int)(si.getCode()); // it should be the lock id
    }
    public void netlockTryCommand(String path){
        this.getProtoHandler().netlockTryCallback(this.easyreum.getBuilder()
                .build("NETLOCK_TRY", path).translate()
                .getBuilder().buildStatusInfo());
    }
    public void netunlockCommand(int lockId){
        this.getProtoHandler().netunlockCallback(this.easyreum.getBuilder()
                .build("NETUNLOCK", String.valueOf(lockId)).translate()
                .getBuilder().buildStatusInfo());
    }
    public void acquireMutexCommand(String pathTo, String token){
        this.getProtoHandler().acquireMutexCallback(this.easyreum.getBuilder()
                .build("NETMUTACQ_TRY", pathTo, token).translate()
                .getBuilder().buildStatusInfo());
    }
    public void releaseMutexCommand(String pathTo, String token){
        this.getProtoHandler().releaseMutexCallback(this.easyreum.getBuilder()
                .build("NETMUTREL", pathTo, token).translate()
                .getBuilder().buildStatusInfo());
    }
    /** 
     * Set the secure filesystem identity
     * @param sfsIdentity The identity to be set
     * @return setfsidCallback -> StatusInfo -> OK, UNKNOWN
     * **/
    public void setfsidCommand(String sfsIdentity){
        this.getProtoHandler().setfsidCallback(this.easyreum.getBuilder()
                .build("SETFSID", sfsIdentity).translate()
                .getBuilder().buildStatusInfo());
    }
    /** 
     * Set the permission mask for an identity 
     * @param sfsIdentity The identity (Should be the owner of path)
     * @param permMask The new permission mask to be set
     * @param path The path of the folder to be modified
     * @return setfsidCallback -> StatusInfo -> OK, UNKNOWN
     * **/
    public void setfspermCommand(String sfsIdentity, String permMask, String path){
        this.getProtoHandler().setfspermCallback(this.easyreum.getBuilder()
                .build("SETFSPERM", 
                    String.join(":", sfsIdentity, permMask), 
                    path).translate()
                .getBuilder().buildStatusInfo());
    }
    /** 
     * Set the permission mask for an identity 
     * @param sfsIdentity The identity (Should be the owner of path)
     * @param permMask The new permission mask to be set
     * @param path The path of the folder to be modified
     * @return setfsidCallback -> StatusInfo -> OK, UNKNOWN
     * **/
    public void remfspermCommand(String sfsIdentity, String path){
        this.getProtoHandler().remfspermCallback(this.easyreum.getBuilder()
                .build("REMFSPERM", 
                    sfsIdentity, path).translate()
                .getBuilder().buildStatusInfo());
    }
    /** 
     * Set the permission mask for an identity 
     * @param sfsIdentity The identity (Should be the owner of path)
     * @param permMask The new permission mask to be set
     * @param path The path of the folder to be modified
     * @return setfsidCallback -> StatusInfo -> OK, UNKNOWN
     * **/
    public void getfspermCommand(String sfsIdentity, String path){
        this.getProtoHandler().getfspermCallback(this.easyreum.getBuilder()
                .build("GETFSPERM", 
                    sfsIdentity, path).translate()
                .getBuilder().buildStatusInfo());
    }
    /** 
     * Set the permission mask for an identity 
     * @param permMask The new permission mask to be set
     * @return setfsidCallback -> StatusInfo -> OK, UNKNOWN
     * **/
    public void issecfsCommand(String path){
        this.getProtoHandler().issecfsCallback(this.easyreum.getBuilder()
                .build("ISSECFS", path).translate()
                .getBuilder().buildStatusInfo());
    }
}
