package com.nerox.old;

import com.nerox.old.connection.Easyreum;
import com.nerox.old.misc.StatusServer;

/**This is an in progress class it is not finished yet, and it is intended to be used for some inner operations so far
 * @see Tfprotocol */
public class TfprotocolWrapper{
    private final Easyreum easyreum;
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
}
