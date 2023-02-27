package com.nerox.client;

import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import com.nerox.client.keepalives.UDPKeepAlive;
import com.nerox.client.modules.XSRPCProxy;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class RPCProxyTest {
    RPCProxyCallback callback;
    XSRPCProxy proxy;
    public RPCProxyTest(){
        this.callback = new RPCProxyCallback();
            // Get current path
        String path = System.getProperty("basedir");
        try{
            FileInputStream fis = new FileInputStream(this.joinPaths(path, "src/test/files/PublicKey.pem"));
            this.proxy = new XSRPCProxy(
                "localhost", 
                10346,
                fis,
                "testhash", 
                36, 
                "0.0", 
                this.callback);
        }catch(FileNotFoundException ignored){
            assertTrue("File not found", false);
        }
        this.proxy.connect();
    }
    /**
     * Rigorous Test :-)
     */
    private String normalizePath(String path){
        return this.normalizePath(path, "/");
    }

    private String normalizePath(String path, String usingSeparator){
        String sep = System.getProperty("file.separator");
        return path.replace(usingSeparator, sep);
    }
    /**  
     * Join N paths togethers normalizing them with the system separators 
    **/
    private String joinPaths(String ...paths){
        StringBuilder sb = new StringBuilder();
        String sep = System.getProperty("file.separator");
        for(String path : paths){
            String rpath = normalizePath(path);
            if (!rpath.endsWith(sep))
                sb.append(sep);
            sb.append(rpath);
        }
        return sb.toString();
    }
    
    //@Test
    public void runAllTests(){
        this.proxyDoesConnect();
        this.testMainProtocol();
        this.startRPCProxy();
        this.testSendHash();
        this.testSendData();
        this.testExitCommand();
        this.testMainProtocol();
    }
    
    public void proxyDoesConnect()
    {
        assertTrue(this.proxy.isConnect());
        assertTrue("Status Info is not OK", this.proxy.easyreum.getBuilder().isStatusInfoOk());
    }
    public void startRPCProxy(){
        this.proxy.startCommand();
        assertTrue("Status Info is not OK", this.proxy.easyreum.getBuilder().isStatusInfoOk());
    }
    public void testSendHash(){
        assertTrue("Status Info is not OK", this.proxy.sendHashCommand("test"));
    }
    public void testSendData(){
        assertTrue("Status Info is not OK", this.proxy.sendPayload("Hola mundo") == 0);
    }
    public void testExitCommand(){
        this.proxy.exitCommand();
        // No unit test for this as not confirmation is given
    }
    public void testMainProtocol(){
        new Tfprotocol(this.proxy, new Callback()).echoCommand("hola mundo");
        assertTrue("Status Info is not OK", this.proxy.easyreum.getHeader() >= 0);
    }
}
