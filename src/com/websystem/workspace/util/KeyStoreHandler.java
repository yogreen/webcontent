package com.websystem.workspace.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.util.ResourceBundle;

import com.websystem.service.spi.WebsystemWorkspaceConstance;
import com.websystem.util.Pair;
import com.websystem.workspace.util.WebsystemWorkspaceConfiguation;

class KeyStoreHandler {

	private WebsystemWorkspaceConfiguation config;

	public KeyStoreHandler() {
		config = WebsystemWorkspaceConfiguation.newInstance();
	}

	Pair<X509Certificate, RSAPrivateKey> loadPair() {
		RSAPrivateKey prv = null;
		X509Certificate x509 = null;
		Pair<X509Certificate, RSAPrivateKey> pair = null;
		InputStream ins;
		try {
			ResourceBundle bundle = ResourceBundle.getBundle("META-INF/configs/secure_entry");
			String alias = bundle.getString(WebsystemWorkspaceConstance.WEBSYS_SECURITY_STORE_ENTRY_ALIAS_KEY);
			String epass = bundle.getString(WebsystemWorkspaceConstance.WEBSYS_SECURITY_STORE_ENTRY_PASSWORD_KEY);
			String p_pass = bundle.getString(WebsystemWorkspaceConstance.WEBSYS_SECURITY_STORE_PROTECTED_PASSWORD_KEY);
			
			KeyStore ks;
			Path path = Paths.get(config.defaultWorkspace().toString(),"security","keystore",WebsystemWorkspaceConstance.WEBSYS_SECURITY_KEYSTORE_DEFAULT_FILENAME);
			ks = KeyStore.getInstance("jks");
			ins = new FileInputStream(path.toFile());
			ks.load(ins, epass
					.toCharArray());
			x509 = (X509Certificate) ks.getCertificateChain(alias)[0];
			prv = (RSAPrivateKey) ks.getKey(alias, p_pass.toCharArray());
			pair = new Pair<X509Certificate, RSAPrivateKey>(x509, prv);
		} catch (KeyStoreException | NoSuchAlgorithmException
				| CertificateException | IOException
				| UnrecoverableKeyException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			throw new IllegalStateException(e.getMessage(), e);
		}
		return pair;
	}

}
