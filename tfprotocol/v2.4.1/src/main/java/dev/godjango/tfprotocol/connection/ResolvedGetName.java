package dev.godjango.tfprotocol.connection;

import java.net.InetAddress;
import java.util.concurrent.Callable;

public class ResolvedGetName implements Callable {

    private final String hostname;
    //UNUSED VARIABLE
    private final int timeout;

    @Override
    public Object call() {
        try {
            InetAddress value = InetAddress.getByName(hostname);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public ResolvedGetName(String hostname, int timeout) {
        this.hostname = hostname;
        this.timeout = timeout;
    }
}
