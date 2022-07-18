package com.nerox.client;

import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.nerox.client.keepalives.UDPKeepAlive;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    Callback callback;
    Tfprotocol tfprotocol;
    public AppTest(){
        this.callback = new Callback();
            // Get current path
        String path = System.getProperty("basedir");
        try{
            FileInputStream fis = new FileInputStream(this.joinPaths(path, "src/test/files/PublicKey.pem"));
            this.tfprotocol = new Tfprotocol("localhost", 10345, fis, 
            "testhash", 36, "0.0", callback);
        }catch(FileNotFoundException ignored){
            assertTrue("File not found", false);
        }
        this.tfprotocol.connect(UDPKeepAlive.TYPE.UDP_PROCHECK, 3, 3, 3);
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
    @Test
    public void tfprotocolDoesConnect()
    {
        assertTrue(this.tfprotocol.isConnect());
    }
    @Test
    public void loginCommand(){
        this.tfprotocol.loginCommand("defusr", "pwd");
    }
    @Test
    public void setfsidCommand(){
        this.tfprotocol.setfsidCommand("new identity");
    }
    @Test
    public void setfspermCommand(){
        this.tfprotocol.setfspermCommand("identity", "path", "/");
    }
}
