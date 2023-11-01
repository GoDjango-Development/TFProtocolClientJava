package dev.godjango.tfprotocol.modules;
import static dev.godjango.tfprotocol.constants.XSACEConsts.Commands.*;

import java.nio.ByteBuffer;

import dev.godjango.tfprotocol.TFExceptions;
import dev.godjango.tfprotocol.Tfprotocol;
import dev.godjango.tfprotocol.TfprotocolSuper;
import dev.godjango.tfprotocol.callbacks.IXSAceCallback;
import dev.godjango.tfprotocol.connection.Easyreum;
import dev.godjango.tfprotocol.misc.StatusInfo;
import dev.godjango.tfprotocol.misc.StatusServer;
import dev.godjango.tfprotocol.multithread.Easythread;

public class XSAce extends TfprotocolSuper<IXSAceCallback> {

    public static final class Communication{
        private final Easyreum easyreum;
        private Communication(Easyreum easyreum){
            this.easyreum = easyreum;
        }

        public synchronized void send(byte[] message){
            easyreum.getBuilder().build(message).send();
        }
        public synchronized void send_signal(long signal){
            easyreum.getBuilder().build(signal).sendJust();
        }
        public synchronized int recv(byte[] buffer){
            byte[] res = easyreum.receive().getPayloadReceived();
            int i;
            for (i = 0; i < res.length; i++)
                buffer[i] = res[i];
            return easyreum.getPayloadReceived().length;
        }
    }
    private Easythread easythread;
    private final Object mutex = new Object();

    public XSAce(String ipServer, int portServer, String publicKey, String hash, int len, String protocol, IXSAceCallback protoHandler) {
        super(ipServer, portServer, publicKey, hash, len, protocol, protoHandler);
    }

    public XSAce(String proxy, String ipServer, int portServer, String publicKey, String hash, int len, String protocol, IXSAceCallback protoHandler) {
        super(proxy, ipServer, portServer, publicKey, hash, len, protocol, protoHandler);
    }

    public XSAce(IXSAceCallback protoHandler) {
        super(protoHandler);
    }

    public XSAce(Tfprotocol tfprotocol, IXSAceCallback xsAceCallbackTest) {
        this.setProtoHandler(xsAceCallbackTest);
        this.easyreum = tfprotocol.getConHandler();
    }

    /**
    * Start the module in the server side so since this command is called until an exit command is called or
    * until you reconnect to the main module you will be able to use Arbitrary Command Execution module.
    * **/
    public void startCommand() {
        super.getProtoHandler().startACECallback(this.easyreum
                .getBuilder().build("XS_ACE").translate()
                .getBuilder().buildStatusInfo());
    }
    /**
     * Insert a key to unlock access to module, without this you won't be able to interact with the module.
     * */
    public void inskeyCommand(String key) {
        super.getProtoHandler().inskeyCallback(this.easyreum
                .getBuilder().build("INSKEY "+key)
                .translate().getBuilder().buildStatusInfo());
    }

    /** Get out from ACE module... Main protocol will now be listening in the server side...*/
    public void exitCommand() {
        super.getProtoHandler().exitCallback(this.easyreum.getBuilder().build("EXIT")
                .translate().getBuilder().buildStatusInfo());
    }
    /** Exits the subsystem without freeing the allocated
     resources and without uninstalling the permission key. Equally, the
     arguments set by the setArgs command remains untouched.*/
    public void goBackCommand() {
        super.getProtoHandler().goBackCallback(this.easyreum.getBuilder().build("GOBACK")
                .translate().getBuilder().buildStatusInfo());
    }
    /** Sets the optional arguments that will be passed to module
     *  being executed by the RUN_NL and RUN_BUF commands.
     *  The return status of this command could be:
     *  @param args First arg must be the program who will launch the program you wants to call... for example:
     *
     *              setArgsCommand("/usr/bin/sh", "/usr/bin/ls")
     *              setArgsCommand("/usr/bin/python", "/path_to_py_file/py_file.py")
     *
     *              The second arg must be the opened file or program, which is going to be opened with arg1
     *              program...
     *  */
    public void setArgsCommand(String... args) {
        this.easyreum.validateArgs(args);
        StringBuilder formated_args = new StringBuilder();
        for (String arg: args){
            formated_args.append(arg).append(" ");
        }
        super.getProtoHandler().setArgsCallback(this.easyreum.getBuilder().build("SETARGS",
                ((formated_args.length()>1)?formated_args.toString().trim():""))
                .translate().getBuilder().buildStatusInfo());
    }

    /**
     * Returns the buffer size for the RUN_NL command in a
     * 32bit signed integer, in its text representation.
     * */
    public void runNnlszCommand() {
        super.getProtoHandler().runNNLSizeCallback(this.easyreum.getBuilder().build("RUNNL_SZ")
                .translate().getBuilder().buildStatusInfo());
    }

    /** Returns the buffer size for the RUN_BUF command in
     a 64bit signed integer, in its text representation.*/
    public void runBszCommand() {
        super.getProtoHandler().runBufSZCallback(this.easyreum.getBuilder().build("RUNBUF_SZ")
                .translate().getBuilder().buildStatusInfo());
    }
    /**
     * Sets the buffer size for the RUN_BUF. The
     * argument ‘buffer-size’ could be any integer that ranges from 8 to
     * 2^63-1 in its text representation
     * */
    public void setRbufCommand(long buffer_size) {
        buffer_size = Math.max(buffer_size,8);
        super.getProtoHandler().setRunBufCallback(this.easyreum
                .getBuilder().build("SET_RUNBUF "+buffer_size)
                .translate().getBuilder().buildStatusInfo());
    }

    /**
     * Sets the buffer size for the RUN_NL. The argument
     * ‘buffer-size’ could be any integer that ranges from 8 to 2^63-1 in its
     * text representation.
     * */
    public void setRunNLCommand(long buffer_size) {
        buffer_size = Math.max(buffer_size, 8);
        super.getProtoHandler().setRunLNCallback(this.easyreum
                .getBuilder().build("SET_RUNNL "+buffer_size)
                .translate().getBuilder().buildStatusInfo());
    }
    public void setWorkingDirectory(String workingDirectory) {
        super.getProtoHandler().setWorkingDirectoryCallback(this.easyreum
                .getBuilder().build("SETWDIR "+workingDirectory)
                .translate().getBuilder().buildStatusInfo());
    }
    public void runBackground(String pathToFile) {
        super.getProtoHandler().runBackgroundCallback(this.easyreum
                .getBuilder().build("RUN_BK", pathToFile)
                .translate().getBuilder().buildStatusInfo());
    }

    /**Runs the program indicated in the first parameter, passing to it,
     as a parameter the optional second parameter, if present. This
     command will handover the entire responsibility of communication
     and encryption layers to the module being run. In order to work
     properly the platform in which the TF PROTOCOL is running
     should implement the basic input/output system through file
     descriptors, and those must be inheritable through the process of
     creating subprocesses or child processes. In fact, what the ACE
     subsystem does is to set the standard input, standard output and
     standard error -file descriptors- to the TCP socket descriptor of the
     server protocol.
     @param optionalSecondArgument
     The main usage of the second parameter is to run interpreted
     programs - modules in ACE subsystem terminology -. For example,
     it could execute a python program, or a Java software; in such a
     case, a python interpreter, a Java Runtime Environment or any
     software capable of executing the interpreted-language module
     passed as the second parameter, should be passed to the RUN
     command as a first parameter. It could be a typical script too.
     In the case of passing as first parameter a module capable to run on
     its own, then the second parameter of the command could be
     omitted.*/
    public void runCommand(String pathToModule, String... optionalSecondArgument) {
        this.easyreum.validateArgs(pathToModule);
        this.easyreum.getBuilder().build("RUN", pathToModule,
                (optionalSecondArgument.length > 0?optionalSecondArgument[0]:""))
                .translate();
        this.getProtoHandler().runReadCallback(this.easyreum.getBuilder().getStatusInfo());
        if (this.easyreum.getBuilder().isStatusInfoOk())
            super.getProtoHandler().runWriteCallback(new Communication(this.easyreum));
        this.getProtoHandler().runReadCallback(
                new StatusInfo(StatusServer.OK,
                        ByteBuffer.wrap(this.easyreum.receiveBuffer(Integer.BYTES)).getInt(),
                        "")
        );
    }
    /*
    public void runNlCommand(String path_to_module) {
        if (this.easythread == null || !this.easythread.on_mirror()){
            this.easyreum.val_args(path_to_module);
            try {
                this.easyreum.get_builder().build("RUN_NL", path_to_module).send();
                this.easyreum.set_hdr_sz(Long.BYTES);
                this.easyreum.rcv_hdr();
                if (this.easyreum.get_hdr() == OK) {
                    this.protoHandler.runNNLCallback(new StatusInfo(StatusServer.OK,
                            OK, ""));
                } else if (this.easyreum.get_hdr() == FINISHED) {
                    this.protoHandler.runNNLCallback(new StatusInfo(StatusServer.OK,
                            FINISHED, "HANDSHAKE"));
                    this.easyreum.get_builder().build(OK).send_just();
                } else if (this.easyreum.get_hdr() == ERROR) {
                    throw new TFExceptions(new StatusInfo(StatusServer.FAILED, ERROR,
                            new String(this.easyreum.receive_buffer(256))));
                } else {
                    throw new TFExceptions(this.easyreum.get_hdr(),
                            "UNKNOWN CODE ERROR");
                }
                Easythread teasy;
                try {
                    runnl_com.setup(this.easyreum, this.mutex);
                    this.easythread = new Easythread(this.mutex,
                            this.getClass().getMethod("runNlCommand", String.class));
                    this.easythread.setup(this, path_to_module);
                    this.easythread.use_mirror();
                    this.easythread.start();
                    teasy = new Easythread(this.mutex,
                            this.getProtoHandler().getClass().getMethod("runNNLCallback",
                                    StatusInfo.class));
                    teasy.setup(this.getProtoHandler(), new StatusInfo("WRITE".getBytes()));
                    teasy.start();
                } catch (NoSuchMethodException e) {
                    throw new TFExceptions(-1, "Invalid XSAce class, download it again...");
                }
                synchronized (this.mutex) {
                    while (!this.easythread.is_finished()) {
                        try {
                            this.mutex.wait();
                            if (runnl_com.is_touched())
                                runnl_com.commit();
                            else if (runnl_com.signal_await) {
                                runnl_com.commit_signal();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                this.easyreum.get_builder().build(OK).send_just();
            } catch (IOException e) {
                throw new TFExceptions(e,ERR_CODES.COMMUNICATION_ERROR.ordinal());
            }finally {
                this.easyreum.set_hdr_sz(Integer.BYTES);
                this.easythread = null;
            }
        }else {
            do{
                try {
                    this.easyreum.rcv_hdr();
                    if (this.easyreum.get_hdr() == FINISHED){
                        break;
                    }
                    this.easyreum.rcv_bdy();
                    this.getProtoHandler().runNNLCallback(new StatusInfo(this.easyreum.get_data()));
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }while (true);
        }
    }
    public void RunBUFCommand(String path_to_module) {
        if (this.easythread == null || !this.easythread.on_mirror()){
            this.easyreum.val_args(path_to_module);
            try {
                this.easyreum.get_builder().build("RUN_BUF", path_to_module).send();
                this.easyreum.set_hdr_sz(Long.BYTES);
                this.easyreum.rcv_hdr();
                if (this.easyreum.get_hdr() == OK) {
                    this.protoHandler.runBufCallback(new StatusInfo(StatusServer.OK,
                            OK, ""));
                }else if(this.easyreum.get_hdr() == FINISHED){
                    this.protoHandler.runBufCallback(new StatusInfo(StatusServer.OK,
                                    FINISHED, "HANDSHAKE"));
                    this.easyreum.get_builder().build(OK).send_just();
                }else if (this.easyreum.get_hdr() == ERROR){
                    throw new TFExceptions(new StatusInfo(StatusServer.FAILED, ERROR,
                                    new String(this.easyreum.receive_buffer(256))));
                }else{
                        throw new TFExceptions(this.easyreum.get_hdr(),
                                "UNKNOWN CODE ERROR");
                }
                try {
                    runbf_com.setup(this.easyreum, this.mutex);
                    this.easythread = new Easythread(this.mutex,
                            this.getClass().getMethod("RunBUFCommand", String.class));
                    this.easythread.setup(this, path_to_module);
                    this.easythread.use_mirror();
                    this.easythread.start();
                    Easythread teasy = new Easythread(this.mutex,
                            this.getProtoHandler().getClass().getMethod("runBufCallback",
                                    StatusInfo.class));
                    teasy.setup(this.getProtoHandler(), new StatusInfo("".getBytes()));
                    teasy.start();
                } catch (NoSuchMethodException e) {
                    throw new TFExceptions(-1, "Invalid XSAce class, download it again...");
                }

                synchronized (this.mutex){
                    while(!this.easythread.is_finished()){
                        try {
                            this.mutex.wait();
                            if (runbf_com.is_touched()){
                                runbf_com.commit();
                            }else if (runbf_com.signal_await) {
                                runbf_com.commit_signal();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                this.easyreum.get_builder().build(OK).send_just();
            } catch (IOException e) {
                throw new TFExceptions(e,ERR_CODES.COMMUNICATION_ERROR.ordinal());
            }finally {
                this.easyreum.set_hdr_sz(Integer.BYTES);
                this.easythread = null;
            }
        }else {
            do{
                try {
                    this.easyreum.rcv_hdr();
                    if (this.easyreum.get_hdr() == FINISHED){
                        break;
                    }
                    this.easyreum.rcv_bdy();
                    this.getProtoHandler().runBufCallback(new StatusInfo(this.easyreum.get_data()));
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }while (true);
        }
    }
    */
    public void runNLCommand(String path, String ... args) {
        this.easyreum.getBuilder().build("RUN_NL", path).getBuilder().build(args).send();
        this.easyreum.setHeaderSize(Long.BYTES);
        this.easyreum.receiveHeader();
        this.getProtoHandler().runReadNLCallback(new StatusInfo(StatusServer.OK, this.easyreum.getHeader(),
                ""));
        if (this.easyreum.getHeader() != ERROR)
        {
            try {
                this.easythread = new Easythread(this.mutex, this.getProtoHandler().getClass()
                        .getMethod("runWriteNNLCallback", Communication.class));
                this.easythread.setup(this.getProtoHandler(), new Communication(this.easyreum));
                this.easythread.start();

            } catch (NoSuchMethodException e) {
                throw new TFExceptions(e);
            }
            try {
                this.easyreum.receiveUntil(this.getClass().getMethod("checkHeader", long.class),
                        this, this.getProtoHandler(), "runReadNLCallback");
                this.getProtoHandler().runReadNLCallback(
                        new StatusInfo(StatusServer.OK, this.easyreum.getHeader(), "")
                );
            } catch (NoSuchMethodException e) {
                throw new TFExceptions(e);
            }
            synchronized (this.mutex){
                while (!this.easythread.is_finished()) {
                    try {
                        this.mutex.wait();
                    } catch (InterruptedException e) {
                        throw new TFExceptions(e);
                    }
                }
            }
        }
        else{
            this.getProtoHandler().runReadNLCallback(new StatusInfo(StatusServer.FAILED
                    ,ERROR,
                    new String(this.easyreum.receiveBuffer(ERROR_BUFFER))));
        }
        this.easyreum.getBuilder().build(OK).sendJust();
        this.easyreum.setHeaderSize(Integer.BYTES);
        this.getProtoHandler().runReadNLCallback(
                new StatusInfo(StatusServer.OK,
                        ByteBuffer.wrap(this.easyreum.receiveBuffer(Integer.BYTES)).getInt(),
                        "")
        );
    }

    public void runBufCommand(String path, String ... args) {
        this.easyreum.getBuilder().build("RUN_BUF", path).getBuilder().build(args).send();
        this.easyreum.setHeaderSize(Long.BYTES);
        this.easyreum.receiveHeader();
        this.getProtoHandler().runReadBufCallback(new StatusInfo(StatusServer.OK, this.easyreum.getHeader(),
                ""));
        if (this.easyreum.getHeader() != ERROR)
        {
            try {
                this.easythread = new Easythread(this.mutex, this.getProtoHandler().getClass()
                        .getMethod("runWriteBufCallback", Communication.class));
                this.easythread.setup(this.getProtoHandler(), new Communication(this.easyreum));
                this.easythread.start();
            } catch (NoSuchMethodException e) {
                throw new TFExceptions(e);
            }
            try {
                this.easyreum.receiveUntil(this.getClass().getMethod("checkHeader", long.class),
                        this, this.getProtoHandler(), "runReadBufCallback");
                this.getProtoHandler().runReadBufCallback(
                        new StatusInfo(StatusServer.OK, this.easyreum.getHeader(), "")
                );
            } catch (NoSuchMethodException e) {
                throw new TFExceptions(e);
            }
            synchronized (this.mutex){
                while (!this.easythread.is_finished()) {
                    try {
                        this.mutex.wait();
                    } catch (InterruptedException e) {
                        throw new TFExceptions(e);
                    }
                }
            }
        }
        else{
            this.getProtoHandler().runReadBufCallback(new StatusInfo(StatusServer.FAILED
                    ,ERROR,
                    new String(this.easyreum.receiveBuffer(ERROR_BUFFER))));
        }
        this.easyreum.getBuilder().build(OK).sendJust();
        this.easyreum.setHeaderSize(Integer.BYTES);
        this.getProtoHandler().runReadBufCallback(
                new StatusInfo(StatusServer.OK,
                        ByteBuffer.wrap(this.easyreum.receiveBuffer(Integer.BYTES)).getInt(),
                        "")
        );
    }

    public boolean checkHeader(long header){
        return (header == FINISHED || header == ERROR);
    }
}
