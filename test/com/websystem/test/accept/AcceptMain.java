package com.websystem.test.accept;

import java.io.IOException;
import java.rmi.Naming;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.List;

import com.websystem.domain.WebsystemProcessStateEnum;
import com.websystem.domain.WebsystemWareHouse;
import com.websystem.security.RSACipher;
import com.websystem.service.spi.SuperActionService;
import com.websystem.service.spi.WebsystemComputable;
import com.websystem.util.Pair;
import com.websystem.util.PlatformUtil;

public class AcceptMain {
	
	public static void main(String[] args) throws Exception {
		AcceptEntity ae = new AcceptEntity();
		ae.setName("cctv");
		byte[] codes = null;
		
		KeyStoreHandler kshandler = new KeyStoreHandler();
		Pair<X509Certificate,RSAPrivateKey> kspair = kshandler.loadPair();
		X509Certificate local_cert = kspair.getFirst();
		RSAPrivateKey local_prv = kspair.getSecond();
		
		String uri = "rmi://192.168.1.108:3011/Master@serviceMessage_entry_eh0";
		SuperActionService actionService = (SuperActionService) Naming.lookup(uri);
		WebsystemProcessStateEnum se = actionService.tryAccess();
		System.out.println(se);
		codes = actionService.accessCertificate(local_cert);
		X509Certificate rm_cert = null;
		RSACipher cipher = new RSACipher();
		List<byte[]> codelist = (List<byte[]>) PlatformUtil.objectUnMarshal(codes);
		rm_cert = (X509Certificate) cipher.RSAdecode(codelist, local_prv);
		System.out.println(rm_cert);
		WebsystemComputable<WebsystemWareHouse, byte[]> comp = new ComputeInstance();
		codes = PlatformUtil.objectMarshal(ae);
		String key = AcceptEntity.class.getName();
		codelist = cipher.RSAencode(ae, (RSAPublicKey)rm_cert.getPublicKey());
		codes =  PlatformUtil.objectMarshal(codelist);
		Pair<String,byte[]> pair = new Pair<String,byte[]>(key,codes);
		WebsystemWareHouse ware = new WebsystemWareHouse();
		ware.setProcesses(pair);
		//ware.setClasses_depends(pair);
		ware.setSourceCertificate(local_cert);
		//codes = actionService.delegateCompute(comp, ware);
	}

}
