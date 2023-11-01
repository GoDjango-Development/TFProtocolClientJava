package dev.godjango.tfprotocol;

import dev.godjango.tfprotocol.connection.Easyreum;
import dev.godjango.tfprotocol.misc.FileStat;
import dev.godjango.tfprotocol.misc.StatusServer;

/**This is an in progress class it is not finished yet, and it is intended to be used for some inner operations so far
 * @see Tfprotocol */
public class TfprotocolWrapper extends TfprotocolSuper{
    public TfprotocolWrapper(Easyreum easyreum){
        this.easyreum = easyreum;
    }
    public String sha256Command(String path){
        return new String(this.easyreum.getBuilder().build("SHA256", path).translate().getData());
    }
    public String prockeyCommand(){
        return new String(this.easyreum.getBuilder().build("PROCKEY").translate().getData()).split(" ")[1];
    }
    public boolean keepAliveCommand(boolean is_on, int time_connection, int interval, int count){
        return this.easyreum.getBuilder().build("KEEPALIVE",
                        (is_on ? "1" : "0"),
                        String.valueOf(time_connection) ,"|",
                        String.valueOf(interval),"|",
                        String.valueOf(count)).translate()
                .getBuilder().buildStatusInfo()
                .getStatus().equals(StatusServer.OK);
    }
    public long dateCommand(){
        return Long.parseLong(this.easyreum.getBuilder()
                .build("DATE").translate()
                .getBuilder().buildStatusInfo().getMessage());
    }

    // TESTING PURPOSE
    public String echoCommand(String value) throws TFExceptions {
        return new String(this.easyreum
                .getBuilder().build("ECHO", value).translate().getData());
    }
    public StatusServer rmdirCommand(String path){
        return this.easyreum.getBuilder().build("RMDIR", path).translate()
                        .getBuilder().buildStatusInfo().getStatus();
    }
    public FileStat fstatCommand(String path){
        String[] message = this.easyreum.getBuilder().build("FSTAT", path).translate()
                .getBuilder().buildStatusInfo().getMessage().split(" ");
        if (this.easyreum.getBuilder().isStatusInfoOk())
            return new FileStat(message[0].charAt(0),
                            Long.parseLong(message[1]),
                            Long.parseLong(message[2]),
                            Long.parseLong(message[3]));
        throw new TFExceptions(StatusServer.FAILED, TFExceptions.ErrorCodes.ON_COMMAND_EXECUTION,
                "Command failed duing execution");
    }

    /**
     * Creates a new file in the specified directory.
     * @param path The path to the file to be created.
     * */
    public boolean touchCommand(String path){
        return this.easyreum.getBuilder().build("TOUCH", path).translate()
                .getBuilder().buildStatusInfo().getStatus().equals(StatusServer.OK);
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
    public boolean mkdirCommand(String path) {
        return this.easyreum.getBuilder().build("MKDIR",path).translate()
                        .getBuilder().buildStatusInfo().getStatus().equals(StatusServer.OK);
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
    public boolean renamCommand(String pathOldName, String pathNewName) {
        return this.easyreum.getBuilder().build("RENAM", pathOldName, "|", pathNewName)
                        .translate().getBuilder().buildStatusInfo().getStatus().equals(StatusServer.OK);
    }
}
