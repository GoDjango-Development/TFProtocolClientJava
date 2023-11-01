package dev.godjango.tfprotocol.misc;

import dev.godjango.tfprotocol.callbacks.ISuperCallback;

public class TCPTimeouts { // TODO This class needs to be reviewed as seems like is not usefull at all

    private int connectTimeout = 120 * 1000; //2 minutos de espera
    private int connectRetry = 3; //Intentos de reconexión
    private int dnsResolutionTimeout = 20 * 1000; // 20 segundos de espera
    private static TCPTimeouts tcpTimeouts;
    private static ISuperCallback callback;

    private TCPTimeouts() {
    }

    /**
     * @return TCPTimeouts objeto único de la clase
     */
    public static TCPTimeouts getInstance(ISuperCallback callback) {
        if (tcpTimeouts == null) {
            tcpTimeouts = new TCPTimeouts();
        }
        TCPTimeouts.callback = callback;
        return tcpTimeouts;
    }

    @Override
    public TCPTimeouts clone() {
        try {
            throw new CloneNotSupportedException();
        } catch (CloneNotSupportedException ex) {
            System.out.println("No se puede clonar un objeto de la clase");
        }
        return null;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        if (connectTimeout < 0) {
            callback.statusServer(new StatusInfo(StatusServer.FAILED, -1, "connectTimeout has to be a non-negative number"));
        } else
            this.connectTimeout = connectTimeout;
    }

    public int getConnectRetry() {
        return connectRetry;
    }

    public void setConnectRetry(int connectRetry) {
        if (connectRetry < 0) {
            callback.statusServer(new StatusInfo(StatusServer.FAILED, -1, "connectRetry has to be a non-negative number"));
        } else
            this.connectRetry = connectRetry;
    }

    public int getDnsResolutionTimeout() {
        return dnsResolutionTimeout;
    }

    public void setDnsResolutionTimeout(int dnsResolutionTimeout) {
        this.dnsResolutionTimeout = dnsResolutionTimeout;
    }
}
