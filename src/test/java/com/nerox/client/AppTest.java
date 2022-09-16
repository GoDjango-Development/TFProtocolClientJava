package com.nerox.client;

import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ByteArrayInputStream;

import com.nerox.client.keepalives.UDPKeepAlive;
import com.nerox.client.security.Cryptography;
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
            this.tfprotocol = new Tfprotocol(
                "localhost", 
                10345, 
                fis, 
                "testhash", 
                36, 
                "0.0", 
                callback);
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
    public void runAllTests(){
        this.tfprotocolDoesConnect();
        //this.loginCommand();
        //this.setfsidCommand();
        //this.setfspermCommand();
        this.supCommand();
        this.statCommand();
    }
    
    public void tfprotocolDoesConnect()
    {
        assertTrue(this.tfprotocol.isConnect());
    }
 
    public void loginCommand(){
        this.tfprotocol.loginCommand("user_your_system_user_here", "pwd");
    }
   
    public void setfsidCommand(){
        this.tfprotocol.setfsidCommand("new identity");
    }
    
    public void setfspermCommand(){
        this.tfprotocol.setfspermCommand("identity", "path", "/");
    }

    public void supCommand(){
        ByteArrayInputStream stream = new ByteArrayInputStream(Cryptography.getRandomBytes(99));
        assertTrue(
            this.tfprotocol.supCommand(
                "operadores/34f5e8394fced10a0df515647ed582b93b5aa7cb743ce93ec7a287b15aa63014.sd/34f5e8394fced10a0df515647ed582b93b5aa7cb743ce93ec7a287b15aa63014.json", 
                stream, 
                0)
        );
    }
    public void statCommand(){
        this.tfprotocol.fstatCommand("operadores/34f5e8394fced10a0df515647ed582b93b5aa7cb743ce93ec7a287b15aa63014.sd/34f5e8394fced10a0df515647ed582b93b5aa7cb743ce93ec7a287b15aa63014.json");
    }
}
