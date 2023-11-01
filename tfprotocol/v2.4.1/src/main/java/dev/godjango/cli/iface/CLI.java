package dev.godjango.cli.iface;

import dev.godjango.tfprotocol.TFExceptions;
import dev.godjango.tfprotocol.Tfprotocol;
import dev.godjango.cli.consts.Commands;
import dev.godjango.cli.protocol.Tfcallback;
import dev.godjango.cli.types.Array;
import dev.godjango.cli.types.SpeciaList;
import dev.godjango.cli.types.String;

import java.io.*;
import java.util.HashMap;

import static dev.godjango.cli.types.Array.readAllBytes;
import static dev.godjango.cli.types.Array.readLine;
import static dev.godjango.cli.utils.Interface.print;


public class CLI {

    private interface TfCommand{
        void run(SpeciaList args);
    }
    private Tfprotocol protocol;
    private byte[] data;

    public static final java.lang.String path = System.getenv("HOME").concat(File.separator)
            .concat(".tfprotocol").concat(File.separator);

    private CLI() {
        this.initArgs();
    }
    private void initArgs(){
        commands.put(String.convert(Commands.copy.name()),
                (args) -> protocol.copyCommand(args.get(0).toString(), args.get(1).toString()));
        commands.put(String.convert(Commands.chmod.name()),
                (args) -> protocol.chmodCommand(args.get(0).toString(), args.get(1).toString()));
        commands.put(String.convert(Commands.chown.name()),
                (args) -> protocol.chownCommand(args.get(0).toString(), args.get(1).toString(), args.get(2).toString()));
        commands.put(String.convert(Commands.cpdir.name()),
                (args) -> protocol.cpdirCommand(args.get(0).toString(), args.get(1).toString()));
        commands.put(String.convert(Commands.date.name()),
                (args) -> protocol.dateCommand());
        commands.put(String.convert(Commands.datef.name()),
                (args) -> protocol.datefCommand());
        commands.put(String.convert(Commands.del.name()),
                (args) -> protocol.delCommand(args.get(0).toString()));
        commands.put(String.convert(Commands.end.name()),
                (args) -> protocol.endCommand());
        commands.put(String.convert(Commands.echo.name()),
                (args) -> protocol.echoCommand(args.compress().toString()));
        commands.put(String.convert(Commands.freesp.name()),
                (args) -> protocol.freespCommand());
        commands.put(String.convert(Commands.fsize.name()),
                (args) -> protocol.fsizeCommand(args.get(0).toString()));
        commands.put(String.convert(Commands.fsizels.name()),
                (args) -> protocol.fsizelsCommand(args.get(0).toString()));
        commands.put(String.convert(Commands.fstat.name()),
                (args) -> protocol.fstatCommand(args.get(0).toString()));
        commands.put(String.convert(Commands.fstatls.name()),
                (args) -> protocol.fstatlsCommand(args.get(0).toString()));
        commands.put(String.convert(Commands.ftod.name()),
                (args) -> protocol.ftodCommand(args.get(0).toString()));
        commands.put(String.convert(Commands.ftype.name()),
                (args) -> protocol.ftypeCommand(args.get(0).toString()));
        commands.put(String.convert(Commands.ftypels.name()),
                (args) -> protocol.ftypelsCommand(args.get(0).toString()));
        commands.put(String.convert(Commands.fupd.name()),
                (args) -> protocol.fupdCommand(args.get(0).toString()));
        commands.put(String.convert(Commands.get.name()),
                (args) -> {
                    try {
                        protocol.getCommand(new RandomAccessFile(args.get(0).toString(), "rw"),
                                args.get(1).toString(), args.get(2).parseToLong(), args.get(3).parseToInteger());
                    } catch (FileNotFoundException e) {
                        throw new TFExceptions(e);
                    }
                });
        commands.put(String.convert(Commands.getcan.name()),
                (args) -> {
                    try {
                        protocol.getCanCommand(new FileOutputStream(args.get(0).toString()),
                                args.get(1).toString(), args.get(2).parseToLong(),
                                args.get(3).parseToInteger(), args.get(4).parseToLong());
                    } catch (FileNotFoundException e) {
                        throw new TFExceptions(e);
                    }
                });
        commands.put(String.convert(Commands.injail.name()),
                (args) -> protocol.injailCommand(args.get(0).toString(), args.get(1).toString()));
        commands.put(String.convert(Commands.keepalive.name()),
                (args) -> protocol.keepAliveCommand(args.get(0).toString(), args.get(1).parseToInteger(),
                        args.get(2).parseToInteger(), args.get(3).parseToInteger()));
        commands.put(String.convert(Commands.lock.name()),
                (args) -> protocol.lockCommand(args.get(0).toString()));
        commands.put(String.convert(Commands.login.name()),
                (args) -> protocol.loginCommand(args.get(0).toString(), args.get(1).toString()));
        commands.put(String.convert(Commands.ls.name()),
                (args) -> protocol.lsCommand(args.get(0).toString()));
        commands.put(String.convert(Commands.lsr.name()),
                (args) -> protocol.lsrCommand(args.get(0).toString()));
        commands.put(String.convert(Commands.lsrv2.name()),
                (args) -> protocol.lsrv2Command(args.get(0).toString(), args.get(1).toString()));
        commands.put(String.convert(Commands.lsv2.name()),
                (args) -> protocol.lsv2Command(args.get(0).toString(), args.get(1).toString()));
        commands.put(String.convert(Commands.mkdir.name()),
                (args) -> protocol.mkdirCommand(args.get(0).toString()));
        commands.put(String.convert(Commands.ndate.name()),
                (args) -> protocol.ndateCommand());
        commands.put(String.convert(Commands.nigma.name()),
                (args) -> protocol.nigmaCommand(args.get(0).parseToInteger()));
        commands.put(String.convert(Commands.prockey.name()),
                (args) -> protocol.prockeyCommand());
        commands.put(String.convert(Commands.put.name()),
                (args) -> {
                    try {
                        protocol.putCommand(new RandomAccessFile(args.get(0).toString(), "rw"),
                                args.get(1).toString(), args.get(2).parseToLong(), args.get(3).parseToInteger());
                    } catch (FileNotFoundException e) {
                        throw new TFExceptions(e);
                    }
                });
        commands.put(String.convert(Commands.putcan.name()),
                (args) -> {
                    try {
                        protocol.putCanCommand(new FileInputStream(args.get(0).toString()),
                                args.get(1).toString(), args.get(2).parseToLong(),
                                args.get(3).parseToInteger(),
                                args.get(4).parseToLong());
                    } catch (FileNotFoundException e) {
                        throw new TFExceptions(e);
                    }
                });
        commands.put(String.convert(Commands.rcvfile.name()),
                (args) -> protocol.rcvFileCommand(Boolean.parseBoolean(args.get(0).toString()), args.get(1).toString()));
        commands.put(String.convert(Commands.rename.name()),
                (args) -> protocol.renamCommand(args.get(0).toString(), args.get(1).toString()));
        commands.put(String.convert(Commands.rmdir.name()),
                (args) -> protocol.rmdirCommand(args.get(0).toString()));
        commands.put(String.convert(Commands.rmsd.name()),
                (args) -> protocol.rmsdCommand(args.get(0).toString(), args.get(1).toString()));
        commands.put(String.convert(Commands.sdown.name()),
                (args) -> {
                    try {
                        protocol.sdownCommand(args.get(0).toString(), new FileOutputStream(args.get(1).toString()), args.get(2).parseToInteger());
                    } catch (FileNotFoundException e) {
                        throw new TFExceptions(e);
                    }
                });
        commands.put(String.convert(Commands.sendfile.name()),
                (args) -> protocol.sendFileCommand(Boolean.parseBoolean(args.get(0).toString()),
                        args.get(1).toString(), args.get(2).toBytes()));
        commands.put(String.convert(Commands.sup.name()),
                (args) -> {
                    try {
                        protocol.supCommand(args.get(0).toString(), new FileInputStream(args.get(1).toString()), args.get(2).parseToInteger());
                    } catch (FileNotFoundException e) {
                        throw new TFExceptions(e);
                    }
                });
        commands.put(String.convert(Commands.sha256.name()),
                (args) -> protocol.sha256Command(args.get(0).toString()));
        commands.put(String.convert(Commands.tlb.name()),
                (args) -> protocol.tlbCommand());
        commands.put(String.convert(Commands.tlbudp.name()),
                (args) -> protocol.tlbUDPCommand());
        commands.put(String.convert(Commands.touch.name()),
                (args) -> protocol.touchCommand(args.get(0).toString()));
        commands.put(String.convert(Commands.udate.name()),
                (args) -> protocol.udateCommand());
        commands.put(String.convert(Commands.xcopy.name()),
                (args) -> protocol.xcopyCommand(args.get(0).toString(), args.get(1).toString(), args.get(2).toString()));
        commands.put(String.convert(Commands.xcpdir.name()),
                (args) -> protocol.xcpdirCommand(args.get(0).toString(), args.get(1).toString(), args.get(2).toString()));
        commands.put(String.convert(Commands.xdel.name()),
                (args) -> protocol.xdelCommand(args.get(0).toString(), args.get(1).toString()));
        commands.put(String.convert(Commands.xrmdir.name()),
                (args) -> protocol.xrmdirCommand(args.get(0).toString(), args.get(1).toString()));
    }

    private final HashMap<String, TfCommand> commands = new HashMap<>();
    public void help() {
        FileInputStream is;
        byte[] doc = new byte[0];
        try {
            is = new FileInputStream(path.concat("docs").concat(File.separator).concat("README.md"));
            doc = readAllBytes(is);
        } catch (IOException e) {
            print("Cannot succesfully retrieve help...");
        }
        print(doc);
    }

    private void commands(String command, SpeciaList args){
        TfCommand slave = commands.get(command);
        if (slave != null) {
            if (protocol != null && protocol.isConnect()) init(args);
            slave.run(args);
        }
        else help();
    }

    private java.lang.String getAddress(String attempt) {
        if (attempt != null) return attempt.toString();
        FileInputStream is;
        try {
            is = new FileInputStream(this.path.concat(".data"));
            this.data = readAllBytes(is);
        } catch (IOException e) {
            return null;
        }
        return new java.lang.String(readLine(data, 0, 1));
    }
    private int getPort(String attempt){
        if (attempt != null) return Integer.parseInt(attempt.toString());
        FileInputStream is;
        try {
            is = new FileInputStream(this.path.concat(".data"));
            this.data = readAllBytes(is);
        } catch (IOException e) {
            return -1;
        }
        return Integer.parseInt(new java.lang.String(readLine(data, 1, 1)));
    }
    private int getLength(String attempt){
        if (attempt != null) return Integer.parseInt(attempt.toString());
        FileInputStream is;
        try {
            is = new FileInputStream(this.path.concat(".data"));
            this.data = readAllBytes(is);
        } catch (IOException e) {
            return -1;
        }
        return Integer.parseInt(new java.lang.String(readLine(data, 3, 1)));
    }

    private java.lang.String getHash(String attempt) {
        if (attempt != null) return attempt.toString();
        FileInputStream is;
        try {
            is = new FileInputStream(this.path.concat(".data"));
            this.data = readAllBytes(is);
        } catch (IOException e) {
            return null;
        }
        return new java.lang.String(readLine(data, 2, 1));
    }
    private java.lang.String getVersion(String attempt) {
        if (attempt != null) return attempt.toString();
        FileInputStream is;
        try {
            is = new FileInputStream(this.path.concat(".data"));
            this.data = readAllBytes(is);
        } catch (IOException e) {
            return null;
        }
        return new java.lang.String(readLine(data, 4, 1));
    }

    public void init(SpeciaList args){
        if (this.protocol != null) return;
        Tfcallback tfcallback = new Tfcallback();
        InputStream stream;
        String key = args.getNextAsArgument(String.convert("key"));
        try {
            if (key != null)
                stream = new FileInputStream(key.toString());
            else stream = new FileInputStream(path.concat(".key"));
        } catch (FileNotFoundException exception) {
            throw new TFExceptions(TFExceptions.ErrorCodes.FILE_NOT_FOUND.ordinal(),
                    "You need at least one public key in order to establish the communication");
        }
        java.lang.String address = getAddress(args.getNextAsArgument(String.convert("address")));
        int port = getPort(args.getNextAsArgument(String.convert("port")));
        java.lang.String hash = getHash(args.getNextAsArgument(String.convert("hash")));
        int length = getLength(args.getNextAsArgument(String.convert("length")));
        java.lang.String version = getVersion(args.getNextAsArgument(String.convert("version")));
        protocol = new Tfprotocol(
                address,
                port,
                stream,
                hash,
                length,
                version,
                tfcallback);
        protocol.connect();
    }


    public static void main(java.lang.String[] args){
        CLI cli = new CLI();
        if (args.length == 0){
            cli.help();
            return;
        }
        String command = new String(args[0].toString().toLowerCase());
        args = (java.lang.String[]) Array.remove(0, args);
        SpeciaList params = new SpeciaList(Array.toArrayOfString(args));
        if (new String(Commands.help.name()).equals(command))
            cli.help();
        else if(new String(Commands.init.name()).equals(command))
            cli.init(params);
        else {
            cli.init(params);
            cli.commands(command, params);
        }
    }
}
