package com.websystem.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.util.List;

import com.websystem.domain.WebsystemWorkspaceSecureInfo;
import com.websystem.security.RSACipher;
import com.websystem.service.spi.WebsystemWorkspaceConstance;
import com.websystem.util.Pair;
import com.websystem.util.PlatformUtil;

class KeyStoreHandler {

	private WebsystemWorkspaceSecureInfo secureInfo;

	public KeyStoreHandler(WebsystemWorkspaceSecureInfo secureInfo) {
		super();
		this.secureInfo = secureInfo;
	}

	@SuppressWarnings("unchecked")
	Pair<X509Certificate, RSAPrivateKey> loadPair() {
		RSACipher cipher = new RSACipher();
		RSAPrivateKey prv = null;
		X509Certificate x509 = null;
		Pair<X509Certificate, RSAPrivateKey> pair = null;
		InputStream ins = null;
		try {

			String dir = secureInfo.getKeystoreCommonKeyDir();
			Path path = Paths
					.get(dir,
							WebsystemWorkspaceConstance.WEBSYS_SECURITY_COMMON_KEYPAIR_PRV_FILE_KEY);

			byte[] codes = Files.readAllBytes(path);
			prv = (RSAPrivateKey) PlatformUtil.objectUnMarshal(codes);

			path = Paths.get(dir).getParent();
			path = Paths
					.get(path.toString(),
							"data",
							WebsystemWorkspaceConstance.WEBSYS_SECURITY_ENTRY_ALIAS_FILE);
			codes = Files.readAllBytes(path);
			List<byte[]> codelist = (List<byte[]>) PlatformUtil
					.objectUnMarshal(codes);
			String alias = (String) cipher.RSAdecode(codelist, prv);
			path = Paths
					.get(path.getParent().toString(),
							WebsystemWorkspaceConstance.WEBSYS_SECURITY_ENTRY_PASSWORD_FILE);
			codes = Files.readAllBytes(path);
			codelist = (List<byte[]>) PlatformUtil.objectUnMarshal(codes);
			String password = (String) cipher.RSAdecode(codelist, prv);
			path = Paths
					.get(path.getParent().toString(),
							WebsystemWorkspaceConstance.WEBSYS_SECURITY_ENTRY_PROTECTED_PASSWORD_FILE);
			codes = Files.readAllBytes(path);
			codelist = (List<byte[]>) PlatformUtil.objectUnMarshal(codes);
			String p_password = (String) cipher.RSAdecode(codelist, prv);
			path = Paths.get(new File(dir).getParent(),"keystore",WebsystemWorkspaceConstance.WEBSYS_SECURITY_KEYSTORE_DEFAULT_FILENAME);
			KeyStore ks = KeyStore.getInstance("jks");
			ins = new FileInputStream(path.toFile());
			ks.load(ins, password.toCharArray());
			x509 = (X509Certificate) ks.getCertificateChain(alias)[0];
			prv = (RSAPrivateKey) ks.getKey(alias, p_password.toCharArray());
			pair = new Pair<X509Certificate, RSAPrivateKey>(x509, prv);

		} catch (Exception e) {
			throw new IllegalStateException(e);
		} finally {
			if (ins != null) {
				try {
					ins.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return pair;
	}
	
	public static void main(String[] args) {
		WebsystemWorkspaceSecureInfo secureInfo = new WebsystemWorkspaceSecureInfo();
		String comm = "F:\\netpaper\\workspace\\build\\workspace\\security\\commonkey";
		secureInfo.setKeystoreCommonKeyDir(comm);
		KeyStoreHandler han = new KeyStoreHandler(secureInfo);
		System.out.println(han.loadPair().getFirst());
	}

}
