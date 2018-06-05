package com.websystem.workspace.util;

import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;

import com.websystem.util.Pair;

public class KeyPairFactory {
	
	private static KeyStoreHandler handler;
	private KeyPairFactory(){
	}
	
	public static Pair<X509Certificate,RSAPrivateKey> pair(){
		if(handler==null){
			handler = new KeyStoreHandler();
		}
		return handler.loadPair();
	}

}
