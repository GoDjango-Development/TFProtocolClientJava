package com.nerox.client;

import static org.junit.Assert.assertTrue;

import com.nerox.client.callbacks.ITfprotocolCallback;
import com.nerox.client.constants.TfprotocolConsts;
import com.nerox.client.misc.FileStat;
import com.nerox.client.misc.StatusInfo;
import com.nerox.client.misc.StatusServer;

import junit.framework.TestCase;

public class RmkdirCommandTest  extends TestCase{
	CallbackImpl callback;
    Tfprotocol tfprotocol;
    
    String ipServer;
	int portServe;
	String publicKey;
	String hash;
	String protocol;
	int len;
	
	
	
	public RmkdirCommandTest() {
		
		this.ipServer="tfproto.expresscuba.com";
		this.portServe=10345;
		this.publicKey="-----BEGIN PUBLIC KEY-----\r\n"
				+ "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA3B7iLfTJ/Lkfgz9/Mq6n\r\n"
				+ "3tBWNaP/818mcEyA5phFv1wyBk9hkroXGX0/J/unxRGF1ax3etNEbN1RASTcZNKT\r\n"
				+ "zeEcwC0yf5Mn+6hmZDOIiDqr1pSAPyNm1soiY3V27/bUhcX0rnCql5Mb7QlfkbK6\r\n"
				+ "o4CddL8pOG99tlSFaTYoXWgUhK5DXF1xptlz0DHv9STuNkukuy+LmAHTJYks1B/w\r\n"
				+ "BC7CZyTtg4VPbHJ11VYXWI6dLTmOuoaTDrJGc/mGOK86zPOchgnAeYekwtBWlk/N\r\n"
				+ "59VtaAWcfgzYnDET45qidcQfF+TIXxjf386igq8rlWTI0+Rh0e/GlYEpRp2YJPMC\r\n"
				+ "AwIDAQAB\r\n"
				+ "-----END PUBLIC KEY-----";
		this.hash="testhash";
		this.protocol="2.4.1";
		this.len=64;
		
		this.callback=new CallbackImpl();
		
	}
    
    
	public void testsRmkdir(){
    	this.tfprotocol=new Tfprotocol(ipServer,portServe,publicKey,hash,len,protocol,
        		callback
			);
    	
        
        this.tfprotocol.connect();
        assertTrue(this.tfprotocol.isConnect());
        try {
        	
        	this.tfprotocol.rmkdirCommand("/rene/java/etc");
            assertTrue("Server status OK after RMKDIR command execution "
            		,this.callback.serverStatusInRmkdir==StatusServer.OK);
            
            this.tfprotocol.fstatCommand("/rene/java/etc");
            assertTrue("Server status OK after FSTAT command execution "
            		,this.callback.serverStatusInFstat==StatusServer.OK);
            
        }catch (Exception e) {
        	assertTrue("Error during execution of MK tests "
            		,false);
		}
        
        
        this.tfprotocol.disconnect();
        
    }
	
}

class CallbackImpl implements ITfprotocolCallback{

	Tfprotocol tfprotocol;
	StatusServer serverStatusInFstat;
	StatusServer serverStatusInRmkdir;
	@Override
    public void rmkdirCallback(StatusInfo status) {
        System.out.println("RMKDIR");
        this.statusServer(status);
        this.serverStatusInRmkdir=status.getStatus();
    }

	
    

    @Override
    public void responseServerCallback(StatusInfo si) {
        this.statusServer(si);
    }

    @Override
    public void instanceTfProtocol(Tfprotocol instance) {
        this.tfprotocol = instance;
    }


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
    public void supCallback(StatusInfo sup) {
        this.statusServer(sup);
    }

    

    @Override
    public void mkdirCallback(StatusInfo status) {
        System.out.println("MKDIR");
        this.statusServer(status);
    }
    
    @Override
    public void fstatCallback(FileStat filestat, StatusInfo status) {
    	System.out.println(filestat);
        this.serverStatusInFstat=status.getStatus();
        this.statusServer(status);
    }
    


  
}