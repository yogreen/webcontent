package com.websystem.workspace.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import javax.security.auth.x500.X500Principal;

import com.websystem.domain.WebsystemWorkspaceSecureEntry;
import com.websystem.security.CommonRSAKeyGen;
import com.websystem.security.RSACipher;
import com.websystem.security.WebsystemKeygenX509CertAction;
import com.websystem.service.spi.WebsystemWorkspaceConstance;
import com.websystem.util.NetWorkToolkit;
import com.websystem.util.PlatformUtil;
import com.websystem.workspace.util.WebsystemWorkspaceEntry;

public class WorkspaceInitial {

	private static final String ITEM="META-INF/configs/websystem.xml";
	private static final String ITEM_P="META-INF/configs/websys_policy.pol";
	
	private static WorkspaceInitial winit = null;

	public static void main(String[] args) throws Exception {
		WorkspaceInitial init = WorkspaceInitial.newInstance();
		System.out.println(init.checkSecureEntry());
		init.copyXML();
	}

	public static WorkspaceInitial newInstance() {
		if (winit == null) {
			winit = new WorkspaceInitial();
		}
		return winit;
	}
	
	private Logger logger = Logger.getLogger(WorkspaceInitial.class.getSimpleName());
	
	private WebsystemKeygenX509CertAction keygen;

	private WebsystemWorkspaceEntry workentry;

	protected WorkspaceInitial() {
		workentry = new WebsystemWorkspaceEntry();
		keygen = new WebsystemKeygenX509CertAction();
	}

	public Path copyXML() throws IOException{
		URL url = Thread.currentThread().getContextClassLoader().getResource(ITEM);
		String us = url.getFile();
		logger.info(String.format("Class source: %s", us));
		if(us.contains("!")){
			us = us.split("!")[0];
		}
		
		File file = new File(us);
		logger.info(String.format("spilt result: %s", file.getPath()));
		/*url = file.toURI().toURL();
		us = new File(url.getPath()).getPath();*/
		if(us.contains("file:")&&us.indexOf("file:")==0){
			us = us.replace("file:", " ").trim();
			us = new File(us).getPath();
		}
		logger.info(String.format(" XML location: %s", us));
		Path path = Paths.get(us);
		LinkedHashMap<String, byte[]> map = new LinkedHashMap<String, byte[]>();
		if(us.endsWith(".jar")){
			FileSystem fs = FileSystems.newFileSystem(path, null);
			Files.walkFileTree(fs.getPath("/"),new SimpleFileVisitor<Path>(){

				@Override
				public FileVisitResult preVisitDirectory(Path dir,
						BasicFileAttributes attrs) throws IOException {
					// TODO Auto-generated method stub
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(Path file,
						BasicFileAttributes attrs) throws IOException {
					// TODO Auto-generated method stub
					if(map.size()==2){
						return FileVisitResult.TERMINATE;
					}
					String xmlf = file.toString();
					if(xmlf.endsWith(".xml")){
						map.put("xml",Files.readAllBytes(file));
					}else if(xmlf.endsWith(".xsd")){
						map.put("xsd",Files.readAllBytes(file));
					}
					return FileVisitResult.CONTINUE;
				}
				
			});
		}else{
			Files.walkFileTree(path.getParent(),new SimpleFileVisitor<Path>(){

				@Override
				public FileVisitResult preVisitDirectory(Path dir,
						BasicFileAttributes attrs) throws IOException {
					// TODO Auto-generated method stub
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(Path file,
						BasicFileAttributes attrs) throws IOException {
					// TODO Auto-generated method stub
					if(map.size()==2){
						return FileVisitResult.TERMINATE;
					}
					String xmlf = file.toFile().getName();
					if(xmlf.endsWith(".xml")){
						map.put("xml",Files.readAllBytes(file));
					}else if(xmlf.endsWith(".xsd")){
						map.put("xsd",Files.readAllBytes(file));
					}
					return FileVisitResult.CONTINUE;
				}
				
			});
		}
		path = Paths.get(this.workentry.configEntry().toString(),"websystem.xml");
		if(!path.toFile().exists()){
			Files.createFile(path);
			Files.write(path, map.get("xml"), StandardOpenOption.WRITE);
		}
		path = Paths.get(this.workentry.configEntry().toString(),"schema.xsd");
		if(!path.toFile().exists()){
			Files.createFile(path);
			Files.write(path, map.get("xsd"), StandardOpenOption.WRITE);
		}
		path = path.getParent();
		logger.info(String.format("copy xml file to %s completed", path));
		return path;
		
	}
	public Path copyPolicy() throws IOException{
		URL url = Thread.currentThread().getContextClassLoader().getResource(ITEM_P);
		String us = url.getFile();
		if(us.contains("!")){
			us = us.split("!")[0];
		}
		
		File file = new File(us);
		logger.info(String.format("spilt result: %s", file.getPath()));
		
		if(us.contains("file:")&&us.indexOf("file:")==0){
			us = us.replace("file:", " ").trim();
			us = new File(us).getPath();
		}
		logger.info(String.format(" Policy location: %s", us));
		Path path = Paths.get(us);
		LinkedHashMap<String, byte[]> map = new LinkedHashMap<String, byte[]>();
		if(us.endsWith(".jar")){
			FileSystem fs = FileSystems.newFileSystem(path, null);
			Files.walkFileTree(fs.getPath("/"),new SimpleFileVisitor<Path>(){
				
				@Override
				public FileVisitResult preVisitDirectory(Path dir,
						BasicFileAttributes attrs) throws IOException {
					// TODO Auto-generated method stub
					return FileVisitResult.CONTINUE;
				}
				
				@Override
				public FileVisitResult visitFile(Path file,
						BasicFileAttributes attrs) throws IOException {
					// TODO Auto-generated method stub
					if(map.size()==1){
						return FileVisitResult.TERMINATE;
					}
					String pf = file.toString();
					if(pf.endsWith(".pol")){
						map.put("policy",Files.readAllBytes(file));
					}
					return FileVisitResult.CONTINUE;
				}
				
			});
		}else{
			Files.walkFileTree(path.getParent(),new SimpleFileVisitor<Path>(){
				
				@Override
				public FileVisitResult preVisitDirectory(Path dir,
						BasicFileAttributes attrs) throws IOException {
					// TODO Auto-generated method stub
					return FileVisitResult.CONTINUE;
				}
				
				@Override
				public FileVisitResult visitFile(Path file,
						BasicFileAttributes attrs) throws IOException {
					// TODO Auto-generated method stub
					if(map.size()==1){
						return FileVisitResult.TERMINATE;
					}
					String pf = file.toString();
					if(pf.endsWith(".pol")){
						map.put("policy",Files.readAllBytes(file));
					}
					return FileVisitResult.CONTINUE;
				}
				
			});
		}
		path = Paths.get(this.workentry.configEntry().toString(),"policy",WebsystemWorkspaceConstance.WEBSYS_WORKSPACE_POLICY_FILE_NAME);
		if(!path.toFile().exists()){
			if(!path.getParent().toFile().exists()){
				
				Files.createDirectory(path.getParent());
			}
			Files.createFile(path);
			Files.write(path, map.get("policy"), StandardOpenOption.WRITE);
		}
		
		path = path.getParent();
		logger.info(String.format("copy policy file to %s completed", path));
		return path;
		
	}

	public X509Certificate createKeystore() throws IOException {
		// TODO Auto-generated method stub
		X509Certificate cert = null;
		Path sp = this.workentry.secureEntry();
		WebsystemWorkspaceSecureEntry sentry = this.workentry
				.securityEntry();
		String alias = sentry.getEntry_alias();
		String epass = sentry.getEntry_password();
		String p_pass = sentry.getEntry_protected_password();
		Path tmp = null;
		tmp = Paths.get(sp.toString(), "data", "complete");
		if (!tmp.toFile().exists()) {
			Files.createFile(tmp);
		}
		DataOutputStream douts = null;
		DataInputStream dins = null;
		String dname = principal();
		try {
			douts = new DataOutputStream(new FileOutputStream(
					tmp.toFile()));
			dins = new DataInputStream(new FileInputStream(tmp.toFile()));
			int n = 0;
			try {

				n = dins.readInt();
			} catch (Exception e) {
				n = 0;
			}
			if (n == 0) {
				douts.writeInt(0);
				douts.close();

				cert = this.keygen.createX509Certificate(dname);
				tmp = Paths
						.get(sp.toString(),
								"keystore",
								WebsystemWorkspaceConstance.WEBSYS_SECURITY_KEYSTORE_DEFAULT_FILENAME);
				if (!tmp.toFile().exists()) {
					Files.createFile(tmp);
				}
				this.keygen.writeCertificateToKeyStore(cert, alias, epass,
						p_pass, tmp.toString());
				CommonRSAKeyGen ckeygen = new CommonRSAKeyGen(2048, "RSA", true);
				RSAPublicKey pub = ckeygen.createRSAPublicKey();
				RSAPrivateKey prv = ckeygen.createRSAPrivateKey();
				RSACipher cipher = new RSACipher();
				List<byte[]> blist = null;
				tmp = Paths
						.get(sp.toString(),
								"data",
								WebsystemWorkspaceConstance.WEBSYS_SECURITY_ENTRY_ALIAS_FILE);
				if (!tmp.toFile().exists()) {
					Files.createFile(tmp);
				}
				blist = cipher.RSAencode(alias, pub);
				Files.write(tmp, PlatformUtil.objectMarshal(blist),
						StandardOpenOption.WRITE);
				tmp = Paths
						.get(sp.toString(),
								"data",
								WebsystemWorkspaceConstance.WEBSYS_SECURITY_ENTRY_PASSWORD_FILE);
				if (!tmp.toFile().exists()) {
					Files.createFile(tmp);
				}
				blist = cipher.RSAencode(epass, pub);
				Files.write(tmp, PlatformUtil.objectMarshal(blist),
						StandardOpenOption.WRITE);
				tmp = Paths
						.get(sp.toString(),
								"data",
								WebsystemWorkspaceConstance.WEBSYS_SECURITY_ENTRY_PROTECTED_PASSWORD_FILE);
				if (!tmp.toFile().exists()) {
					Files.createFile(tmp);
				}
				blist = cipher.RSAencode(p_pass, pub);
				Files.write(tmp, PlatformUtil.objectMarshal(blist),
						StandardOpenOption.WRITE);
				tmp = Paths
						.get(sp.toString(),
								"commonkey",
								WebsystemWorkspaceConstance.WEBSYS_SECURITY_COMMON_KEYPAIR_PRV_FILE_KEY);
				if (!tmp.toFile().exists()) {
					Files.createFile(tmp);
				}
				Files.write(tmp, PlatformUtil.objectMarshal(prv),
						StandardOpenOption.WRITE);
				tmp = Paths
						.get(sp.toString(),
								"commonkey",
								WebsystemWorkspaceConstance.WEBSYS_SECURITY_COMMON_KEYPAIR_PUB_FILE_KEY);
				if (!tmp.toFile().exists()) {
					Files.createFile(tmp);
				}
				Files.write(tmp, PlatformUtil.objectMarshal(pub),
						StandardOpenOption.WRITE);
				tmp = Paths.get(sp.toString(), "data", "complete");
				douts = new DataOutputStream(new FileOutputStream(tmp.toFile()));
				douts.writeInt(1);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new IOException(e.getMessage(), e);
		} finally {
			if (dins != null) {
				dins.close();
			}
			if (douts != null) {
				douts.close();
			}
		}
		cert = this.keygen.loaderFromKeyStore(alias, epass, Paths.get(sp.toString(),"keystore",
				WebsystemWorkspaceConstance.WEBSYS_SECURITY_KEYSTORE_DEFAULT_FILENAME).toString());
		
				
		return cert;
	}
	
	String principal() {
		String principal = null;
		Locale locale = Locale.getDefault();
		String c = "C=" + locale.getDisplayCountry();
		String cn = "CN=" + locale.getISO3Country();
		String o = "O=" + System.getProperty("user.name");
		String ou = "OU=" + System.getProperty("os.name");
		NetWorkToolkit kit = NetWorkToolkit.builder.newInstance();
		String st = null;
		String n = null;
		try {
			n = kit.getFirstNonLocalhostNetworkInterface().getFirst().getName();
			st = kit.getFirstNonLocalhostNetworkInterface().getSecond()
					.getHostAddress();

		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		st = st + "@" + kit.macView().get(n).get(0);
		st = "ST=" + st;
		String l = "L=" + locale.getDisplayName();
		principal = o + "," + c + "," + cn + "," + ou + "," + l + "," + st;
		X500Principal xp = new X500Principal(principal);
		return xp.getName();
	}
	
	public void workFrame(X509Certificate cert) throws IOException {
		this.workentry.configEntry();
		this.workentry.dependsEntry();
		this.workentry.storageEntry();
		this.workentry.javaHomeEntry();
		String principal = principal().split(",")[0].split("=")[1] + "@"
				+ cert.getSerialNumber().longValue();
		this.workentry.thirdPartyEntry(principal);
	}
	
	public boolean checkSecureEntry(){
		return this.workentry.checkSecureWorkspace();
	}
	
	public Path workspace() throws IOException{
		return this.workentry.workspace();
	}
	
	public Path storagePath() throws IOException{
		return this.workentry.storageEntry();
	}

}
