package com.tests.main;

import java.io.RandomAccessFile;
import java.util.Arrays;
import com.nerox.client.TFExceptions;
import com.nerox.client.Tfprotocol;
import com.nerox.client.callbacks.ITfprotocolCallback;
import com.nerox.client.keepalives.UDPKeepAlive;
import com.nerox.client.misc.FileStat;
import com.nerox.client.misc.StatusInfo;
import com.nerox.client.misc.StatusServer;
import com.nerox.client.security.Cryptography;


public class Main{
    public static void main(String[] args){
        /*
            System.out.println(Cryptography.sha256("Hola mundo".getBytes()).length);
            System.out.println(Arrays.toString(Cryptography.sha256("Hola mundo".getBytes())));
            System.out.println(new String(Cryptography.sha256("Hola mundo".getBytes())));
        */
        //if (true){
        //    return;
        //}
        /*Cryptography.Xor xor =
                new Cryptography.Xor("/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home/bin/java".getBytes());
        byte[] payload = "Hola mundo".getBytes();
        xor.encrypt(payload);
        System.out.println(new String(payload));
        Cryptography.Xor xorDecrypt =
                new Cryptography.Xor("/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home/bin/java".getBytes());
        xorDecrypt.decrypt(payload);
        System.out.println(new String(payload));
        if (true) return;*/
        ITfprotocolCallback callback = new ITfprotocolCallback() {
            Tfprotocol tfprotocol;
            @Override
            public void statusServer(StatusInfo status) {
                System.out.println("------------------------------");
                System.out.println(status.getStatus());
                System.out.println(status.getMessage());
                System.out.println(status.getPayload()!=null ?new String(status.getPayload()):"");
                System.out.println(status.getCode());
                System.out.println("------------------------------");
            }
            @Override
            public void loginCallback(StatusInfo info){
                this.statusServer(info);
            }
            @Override
            public void responseServerCallback(StatusInfo status) {

            }

            @Override
            public void instanceTfProtocol(Tfprotocol instance) {
                this.tfprotocol = instance;
            }

            @Override
            public void dateCallback(Integer timestamp, StatusInfo status) {
                this.statusServer(status);
            }

            @Override
            public void datefCallback(java.util.Date date, StatusInfo status) {
                this.statusServer(status);
            }

            @Override
            public void dtofCallback(java.util.Date date, StatusInfo status) {
                this.statusServer(status);
            }
            @Override
            public void injailCallback(StatusInfo injail) {
                // TODO Auto-generated method stub
                this.statusServer(injail);
            }

            @Override
            public void ftodCallback(Integer timestamp, StatusInfo status) {
                this.statusServer(status);
            }

            @Override
            public void fstatCallback(FileStat filestat, StatusInfo status) {
                this.statusServer(status);
                System.out.println(filestat);
            }

            @Override
            public void sendFileCallback(boolean isOverwritten, String path, StatusInfo sendToServer, byte[] payload) {
                this.statusServer(sendToServer);
                sendToServer.setStatus(StatusServer.OK);
            }

            @Override
            public void rcvFileCallback(boolean deleteAfter, String path, StatusInfo sendToServer) {
                this.statusServer(sendToServer);
            }

            @Override
            public void lsCallback(StatusInfo status) {
                this.statusServer(status);
            }

            @Override
            public void lsrCallback(StatusInfo status) {
                this.statusServer(status);
            }

            @Override
            public void putCallback(Tfprotocol.Codes codes) {
            }

            @Override
            public void putStatusCallback(StatusInfo statusInfo) {
                this.statusServer(statusInfo);
            }

            @Override
            public void echoCallback(String value) {
                System.out.println(value);
            }

            @Override
            public void tlbCallback(StatusInfo tlb) {
                this.statusServer(tlb);
            }

            @Override
            public void supCallback(StatusInfo sup) {
                this.statusServer(sup);
            }

            @Override
            public void sdownCallback(StatusInfo sdown) {
                this.statusServer(sdown);
            }

            @Override
            public void tlbUDPCallback(StatusInfo tlb) {
                this.statusServer(tlb);
            }

            @Override
            public void fsizeCallback(StatusInfo fsize) {
                this.statusServer(fsize);
            }

            @Override
            public void fsizelsCallback(StatusInfo fsizels) {
                this.statusServer(fsizels);
            }

            @Override
            public void integrityWriteCallback(StatusInfo statusInfo) {
                this.statusServer(statusInfo);
            }
            @Override
            public void netlockCallback(StatusInfo statusInfo) {
                this.statusServer(statusInfo);
            }
            @Override
            public void netlockTryCallback(StatusInfo statusInfo) {
                this.statusServer(statusInfo);
            }
            @Override
            public void integrityReadCallback(StatusInfo statusInfo) {
                this.statusServer(statusInfo);
            }
            @Override
            public void releaseMutexCallback(StatusInfo statusInfo) {
                this.statusServer(statusInfo);
            }
            @Override
            public void acquireMutexCallback(StatusInfo statusInfo) {
                this.statusServer(statusInfo);
            }
            @Override
            public void netunlockCallback(StatusInfo statusInfo) {
                this.statusServer(statusInfo);
            }
        };
        /*IXSGatewayCallback callback = new IXSGatewayCallback() {
            @Override
            public void responseServerCallback(StatusInfo statusInfo) {

            }

            @Override
            public void instanceTfProtocol(XSGateway xsGateway) {

            }

            @Override
            public void statusServer(StatusInfo status) {
                System.out.println("------------------------------>");
                System.out.println(status.getStatus().name());
                System.out.println(status.getCode());
                System.out.println(status.getMessage());
                System.out.println("------------------------------>");
            }

            @Override
            public void startGateway(StatusInfo xs_gateway) {
                this.statusServer(xs_gateway);
            }

            @Override
            public void createIdentity(StatusInfo buildStatusInfo) {
                this.statusServer(buildStatusInfo);
            }
        };*/
        /*Tfprotocol tfprotocol = new Tfprotocol( "expresscuba.com",
                11001,"-----BEGIN PUBLIC KEY-----\n" +
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsYTmDfFGU/SN5y1Da3xT\n" +
                "suU4OaVEbTr5hh//K18sLjEkhES77BUjt1mDvOff2geiy+TU7dSXApIe2lq1ZTKa\n" +
                "+RhLhNz2rob3nrLLwG5BkMZ9nzBpQw9EU3xzSLCJBEcUvGs1THCs8FGtbuYxCOco\n" +
                "qBMF/1VeMYUbaY8j2mwH9kqHKv6l1y4PmoEXGCA2gJVB3U2D3TeqPYZHcQjk7eAz\n" +
                "M8iXYsIK6b9ln+FyJWvJ3R1X/KnuINXHZEFPNqbn+H68RiNpPPavI0Cql6+mOtXc\n" +
                "jje9Q14d6mKhOWoMB3sjzuF9fz2eb8eALxnKGsJcTyOLxG040j8kvuPGaBmNq7sB\n" +
                "rQIDAQAB\n" +
                "-----END PUBLIC KEY-----\n","encaja",36,"2.4.1",
                callback);*/
        /*Tfprotocol tfprotocol = new Tfprotocol( "192.168.0.132",//"tfproto.expresscuba.com",
                10345,"-----BEGIN PUBLIC KEY-----\n" +
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAq2K/X7ZSKlrMBIrSY9G/\n"+
                "LLB+0injPCX17U7vb8XedQbjBT2AJ+qxmT4VLR1EWnvdUdvxaX9kRahI4hlSnfWl\n"+
                "IddPfJRPH97Rk0OlMEQBclhD4WL88T8o4lVu0nuo8UAjqY0As6g6ZCG1s/Wfr64N\n"+
                "aSvFr8NAYIaTQ6PbKxiypTythsSAkp5eAMkaje/ZAhkY1h0zMz09eg17veED8dIb\n"+
                "R5sc7j05ndDOGucqY4+u9F/CZQNyOysKcFYjtYz/pStBPYY8CcU82SwW0Y2tbzy2\n"+
                "j30ADzroySlQw+VjcffrGJao9qea1GWGwGMv4d4baMxrZeid2uB7NMUdljW8owWa\n"+
                "VwIDAQAB\n"+
                "-----END PUBLIC KEY-----\n","testhash",36,"0.0",
                callback);*/
        
            Tfprotocol tfprotocol = new Tfprotocol("tfproto.expresscuba.com",
                10345, 
                "-----BEGIN PUBLIC KEY-----\n"+
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAq2K/X7ZSKlrMBIrSY9G/\n"+
                "LLB+0injPCX17U7vb8XedQbjBT2AJ+qxmT4VLR1EWnvdUdvxaX9kRahI4hlSnfWl\n"+
                "IddPfJRPH97Rk0OlMEQBclhD4WL88T8o4lVu0nuo8UAjqY0As6g6ZCG1s/Wfr64N\n"+
                "aSvFr8NAYIaTQ6PbKxiypTythsSAkp5eAMkaje/ZAhkY1h0zMz09eg17veED8dIb\n"+
                "R5sc7j05ndDOGucqY4+u9F/CZQNyOysKcFYjtYz/pStBPYY8CcU82SwW0Y2tbzy2\n"+
                "j30ADzroySlQw+VjcffrGJao9qea1GWGwGMv4d4baMxrZeid2uB7NMUdljW8owWa\n"+
                "VwIDAQAB\n"+
                "-----END PUBLIC KEY-----",
                "testhash",
                36,"0.0",
                callback);
        /*
        Tfprotocol tfprotocol = new Tfprotocol("127.0.0.1",
                10345, 
                "-----BEGIN PUBLIC KEY-----\n"+
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAq2K/X7ZSKlrMBIrSY9G/\n"+
                "LLB+0injPCX17U7vb8XedQbjBT2AJ+qxmT4VLR1EWnvdUdvxaX9kRahI4hlSnfWl\n"+
                "IddPfJRPH97Rk0OlMEQBclhD4WL88T8o4lVu0nuo8UAjqY0As6g6ZCG1s/Wfr64N\n"+
                "aSvFr8NAYIaTQ6PbKxiypTythsSAkp5eAMkaje/ZAhkY1h0zMz09eg17veED8dIb\n"+
                "R5sc7j05ndDOGucqY4+u9F/CZQNyOysKcFYjtYz/pStBPYY8CcU82SwW0Y2tbzy2\n"+
                "j30ADzroySlQw+VjcffrGJao9qea1GWGwGMv4d4baMxrZeid2uB7NMUdljW8owWa\n"+
                "VwIDAQAB\n"+
                "-----END PUBLIC KEY-----",
                "testhash",
                36,"0.0",
                callback);*/
        /*XSGateway gateway = new XSGateway("tfproto.expresscuba.com",
                10345,"-----BEGIN PUBLIC KEY-----\n"+
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAq2K/X7ZSKlrMBIrSY9G/\n"+
                "LLB+0injPCX17U7vb8XedQbjBT2AJ+qxmT4VLR1EWnvdUdvxaX9kRahI4hlSnfWl\n"+
                "IddPfJRPH97Rk0OlMEQBclhD4WL88T8o4lVu0nuo8UAjqY0As6g6ZCG1s/Wfr64N\n"+
                "aSvFr8NAYIaTQ6PbKxiypTythsSAkp5eAMkaje/ZAhkY1h0zMz09eg17veED8dIb\n"+
                "R5sc7j05ndDOGucqY4+u9F/CZQNyOysKcFYjtYz/pStBPYY8CcU82SwW0Y2tbzy2\n"+
                "j30ADzroySlQw+VjcffrGJao9qea1GWGwGMv4d4baMxrZeid2uB7NMUdljW8owWa\n"+
                "VwIDAQAB\n"+
                "-----END PUBLIC KEY-----\n","testhash",36,"0.0",
                callback);*/
        try {
            //System.out.println(new String(Cryptography.getRandomBytes(99)));
            tfprotocol.connect(UDPKeepAlive.TYPE.UDP_PROCHECK, 3, 3, 3);
            //tfprotocol.injailCommand("user", "/");
            //tfprotocol.loginCommand("etherbeing", "pwd");
            tfprotocol.echoCommand("Start");
            //tfprotocol.lsCommand("/");
            //tfprotocol.fstatCommand("new.test");
            RandomAccessFile file = new RandomAccessFile("/var/tfdb/new.test", "rw");
            try{
                //tfprotocol.lsCommand("/");
                //int lockId = tfprotocol.netlockCommand(5, "t.txt");
                //tfprotocol.netlockTryCommand("new.test");
                //tfprotocol.acquireMutexCommand("t.txt", "123");
                tfprotocol.integrityReadCommand(file, "t.txt");
                //tfprotocol.netunlockCommand(lockId);
                //tfprotocol.releaseMutexCommand("t.txt", "123");
            }catch(Exception ignored){
                System.out.println(ignored);
            }
            file.close();
            //tfprotocol.integrityWriteCommand();
            tfprotocol.echoCommand("End");
            //gateway.connect(UDPKeepAlive.TYPE.UDP_PROCHECK, 3, 3, 3);
            /*gateway.startGateway();
            gateway.createIdentity("n4b3ts3");
            //gateway.receiveMessageInInterval(5000);
            while (true){
                byte[] receive = new byte[1000];
                int received = gateway.receiveMessage(receive);
                System.out.println(new String(Arrays.copyOfRange(receive, 0, received)));
                byte[] buffer = new byte[1000];
                int read = System.in.read(buffer);
                System.out.println("Will write " + read + " bytes");
                String payload = new String(Arrays.copyOfRange(buffer, 0, read));
                if (payload.equals("quit")) break;
                gateway.sendMessage("luismi", payload);
            }
            gateway.endGateway();*/
            //tfprotocol.echoCommand("Start");
            //tfprotocol.echoCommand("Hola mundo");
            //tfprotocol.dateCommand();
            //tfprotocol.datefCommand();
            //tfprotocol.dtofCommand(1634186811);
            //tfprotocol.ftodCommand("2021-10-13 00:54:41");
            //tfprotocol.sendFileCommand(true, "send", Cryptography.getRandomBytes(99));
            //tfprotocol.rcvFileCommand(false, "send");
            //tfprotocol.fstatCommand("send");
            //tfprotocol.lsCommand("/", null);
            //tfprotocol.lsrCommand("/");
        /*
        try {
            RandomAccessFile raf = new RandomAccessFile("/tmp/tmp.txt", "rw");
            tfprotocol.putCommand(raf, "raf.test", 0, 1000);
            tfprotocol.lsCommand("");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/

            //tfprotocol.lsCommand("");
            //tfprotocol.fstatCommand("send");
            //tfprotocol.fsizeCommand("apue.pdf");
            //tfprotocol.fsizelsCommand("b.txt");
            //tfprotocol.tlbCommand();
            //FileOutputStream stream1 = new FileOutputStream("apue.pdf");
            //tfprotocol.sdownCommand("apue.pdf", stream1, 5);
            //System.out.println(stream1);
            //ByteArrayInputStream stream =
            //        new ByteArrayInputStream(Cryptography.getRandomBytes(1_000_000));
            //FileInputStream stream = new FileInputStream("apue.pdf");
            //System.out.println(tfprotocol.supCommand("apue.pdf", stream, 0));

            //FileOutputStream stream1 = new FileOutputStream("apue.pdf");
            //System.out.println(tfprotocol.sdownCommand("apue.pdf", stream1, 0));

            //tfprotocol.echoCommand("End");
        }catch (Exception ex){
            System.err.println(new TFExceptions(ex).toString());
        }finally {
            //gateway.disconnect();
            tfprotocol.disconnect();
        }
    }
}