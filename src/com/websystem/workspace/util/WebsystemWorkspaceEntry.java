package com.websystem.workspace.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Logger;

import com.websystem.domain.WebsystemWorkspaceSecureEntry;
import com.websystem.security.WebsystemSecurityConstance;
import com.websystem.service.spi.WebsystemWorkspaceConstance;

public class WebsystemWorkspaceEntry {

	private Path basePath = null;
	private Path workspacePath = null;
	private Logger logger = Logger.getLogger(WebsystemWorkspaceEntry.class.getSimpleName());
	private static final String SECURE_ENTRY_ITEM="META-INF/configs/secure_entry.properties";
	private ResourceBundle bundle = null;

	public WebsystemWorkspaceEntry() {
		this(null);
	}

	public WebsystemWorkspaceEntry(Path basePath) {
		if (basePath != null) {
			this.basePath = basePath;
		} else {
			this.basePath = workspaceBasePath();
		}
		try {
			this.workspacePath = createWorkspace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e.getMessage(),e);
		}
		try{
			bundle = ResourceBundle.getBundle(SECURE_ENTRY_ITEM.split("\\.")[0]);
		}catch(Exception e){
			throw new RuntimeException(e.getMessage(),e);
		}
	}
	
	public WebsystemWorkspaceSecureEntry securityEntry(){
		WebsystemWorkspaceSecureEntry sentry = new WebsystemWorkspaceSecureEntry();
		sentry.setEntry_alias(bundle.getString(WebsystemWorkspaceConstance.WEBSYS_SECURITY_STORE_PROTECTED_PASSWORD_KEY));
		sentry.setEntry_password(bundle.getString(WebsystemWorkspaceConstance.WEBSYS_SECURITY_STORE_ENTRY_PASSWORD_KEY));
		sentry.setEntry_protected_password(bundle.getString(WebsystemWorkspaceConstance.WEBSYS_SECURITY_STORE_PROTECTED_PASSWORD_KEY));
		return sentry;
	}

	public boolean checkSecureWorkspace() {
		if (getWorkspacePath() == null) {
			return false;
		}
		Path securePath = Paths.get(workspacePath.toString(), "security");
		String[] filenames = {
				WebsystemSecurityConstance.WEBSYS_SECURITY_COMMON_KEYPAIR_PRV_FILE_KEY,
				WebsystemSecurityConstance.WEBSYS_SECURITY_COMMON_KEYPAIR_PUB_FILE_KEY,
				WebsystemSecurityConstance.WEBSYS_SECURITY_ENTRY_ALIAS_FILE,
				WebsystemSecurityConstance.WEBSYS_SECURITY_ENTRY_PASSWORD_FILE,
				WebsystemSecurityConstance.WEBSYS_SECURITY_ENTRY_PROTECTED_PASSWORD_FILE,
				WebsystemSecurityConstance.WEBSYS_SECURITY_KEYSTORE_DEFAULT_FILENAME,"complete"};
		boolean[] bools = new boolean[filenames.length];
		Arrays.fill(bools, false);
		Set<String> caches = new HashSet<String>();
		try {
			Files.walkFileTree(securePath, new SimpleFileVisitor<Path>() {

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
					String fname = file.toFile().getName();
					switch (fname) {
						case WebsystemSecurityConstance.WEBSYS_SECURITY_COMMON_KEYPAIR_PRV_FILE_KEY :
							bools[0] = true;
							caches.add(file.toString());
							break;
						case WebsystemSecurityConstance.WEBSYS_SECURITY_COMMON_KEYPAIR_PUB_FILE_KEY :
							bools[1] = true;
							caches.add(file.toString());
							break;
						case WebsystemSecurityConstance.WEBSYS_SECURITY_ENTRY_ALIAS_FILE :
							bools[2] = true;
							caches.add(file.toString());
							break;
						case WebsystemSecurityConstance.WEBSYS_SECURITY_ENTRY_PASSWORD_FILE :
							bools[3] = true;
							caches.add(file.toString());
							break;
						case WebsystemSecurityConstance.WEBSYS_SECURITY_ENTRY_PROTECTED_PASSWORD_FILE :
							bools[4] = true;
							caches.add(file.toString());
							break;
						case WebsystemSecurityConstance.WEBSYS_SECURITY_KEYSTORE_DEFAULT_FILENAME :
							bools[5] = true;
							caches.add(file.toString());
							break;
						case "complete" :
							bools[6] = true;
							caches.add(file.toString());
							break;
						

						default :
							break;
					}
					return FileVisitResult.CONTINUE;
				}

			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
		List<String> filters = new ArrayList<String>();
		Iterator<String> iter = caches.iterator();
		int i = 0;
		while (iter.hasNext()) {
			if (i == 7) {
				break;
			}
			if (!bools[i]) {
				filters.add(iter.next());
			}
			i++;
		}
		if (!filters.isEmpty()) {
			FileNotFoundException e = new FileNotFoundException(String.format(
					"Files: %s is not found", filters));
			throw new RuntimeException(e);
		}
		return true;
	}
	public Path configEntry() throws IOException {
		Path workspace = this.workspacePath;
		Path entry = null;
		entry = Paths.get(workspace.toString(), "configs");
		if (!entry.toFile().exists()) {
			Files.createDirectory(entry);
		}
		Path tmp = Paths.get(entry.toString(),"blockchain");
		if(!tmp.toFile().exists()){
			
			Files.createDirectory(tmp);
			tmp = Paths.get(tmp.toString(),"block.list");
			Files.createFile(tmp);
			tmp = Paths.get(tmp.getParent().toString(),"chain.list");
			Files.createFile(tmp);
		}
		tmp = Paths.get(entry.toString(),"policy");
		if(!tmp.toFile().exists()){
			Files.createDirectory(tmp);
		}

		return entry;
	}
	Path createWorkspace() throws IOException {
		Path path = null;
		String tokens = File.separator;
		String basepath = this.basePath.toString();
		int lastindex = basepath.lastIndexOf(tokens);
		tokens = basepath.substring(lastindex + 1, basepath.length());
		if (!tokens.toLowerCase().equals("workspace")) {
			path = Paths.get(basepath, "workspace");
			if (!path.toFile().exists()) {
				Files.createDirectories(path);
			}
		} else {
			path = Paths.get(basepath);
		}
		return path;
	}

	public Path dependsEntry() throws IOException {
		Path workspace = this.workspacePath;
		Path entry = null;
		entry = Paths.get(workspace.toString(), "depends");
		if (!entry.toFile().exists()) {
			Files.createDirectory(entry);
		}
		entry = Paths.get(entry.toString(), "classes");
		if (!entry.toFile().exists()) {
			Files.createDirectory(entry);
		}
		entry = entry.getParent();
		entry = Paths.get(entry.toString(), "lib");
		if (!entry.toFile().exists()) {
			Files.createDirectory(entry);
		}
		entry = entry.getParent();
		entry = Paths.get(entry.toString(), "configs");
		if (!entry.toFile().exists()) {
			Files.createDirectory(entry);
			
		}
		entry = entry.getParent();
		return entry;
	}

	public Path getWorkspacePath() {
		return workspacePath;
	}

	public Path javaHomeEntry() throws IOException {
		Path javahome = null;
		Path workpath = this.workspacePath;
		String key = "javahome";
		File[] files = workpath.toFile().listFiles();
		for (File file : files) {
			String tmp = file.getName();
			if (tmp.toLowerCase().equals(key)) {
				javahome = Paths.get(workpath.toString(), tmp);
			} else {
				javahome = Paths.get(workpath.toString(), key);
				if (!javahome.toFile().exists()) {
					Files.createDirectory(javahome);
				}
			}
		}
		return javahome;

	}
	public Path secureEntry() throws IOException {
		Path workspace = this.workspacePath;
		Path entry = null;
		entry = Paths.get(workspace.toString(), "security");
		if (!entry.toFile().exists()) {
			Files.createDirectory(entry);
		}
		entry = Paths.get(entry.toString(), "commonkey");
		if (!entry.toFile().exists()) {
			Files.createDirectory(entry);
		}
		entry = entry.getParent();
		entry = Paths.get(entry.toString(), "keystore");
		if (!entry.toFile().exists()) {
			Files.createDirectory(entry);
		}
		entry = entry.getParent();
		entry = Paths.get(entry.toString(), "data");
		if (!entry.toFile().exists()) {
			Files.createDirectory(entry);
		}
		entry = entry.getParent();
		entry = Paths.get(entry.toString(), "policy");
		if (!entry.toFile().exists()) {
			Files.createDirectory(entry);
		}
		entry = entry.getParent();
		return entry;
	}

	public Path storageEntry() throws IOException {
		Path workspace = this.workspacePath;
		Path entry = null;
		entry = Paths.get(workspace.toString(), "storage");
		if (!entry.toFile().exists()) {
			Files.createDirectory(entry);
		}

		return entry;
	}
	public URL[] thirdPartyClasspath(String principal) throws IOException {
		URL[] urls = null;

		List<URL> urllist = new ArrayList<URL>();
		Path thirdpath = this.thirdPartyEntry(principal);
		Files.walkFileTree(thirdpath, new SimpleFileVisitor<Path>() {

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
				String filename = file.toFile().getName();
				if (filename.endsWith("class") || filename.endsWith("jar")) {
					File tmp = file.toFile();
					URL url = tmp.toURI().toURL();
					urllist.add(url);
				}
				return FileVisitResult.CONTINUE;
			}

		});

		urls = new URL[urllist.size()];
		urllist.toArray(urls);
		return urls;

	}
	public Path thirdPartyEntry(String principal) throws IOException {
		if (principal == null || principal.trim().isEmpty()) {
			throw new RuntimeException(
					"parameter \"principal\" must not be null or empty.");
		}
		Path workspace = this.workspacePath;
		Path entry = null;
		entry = Paths.get(workspace.toString(), "thirdparty");
		if (!entry.toFile().exists()) {
			Files.createDirectory(entry);
		}
		entry = Paths.get(entry.toString(), principal, "classes");
		if (!entry.toFile().exists()) {
			Files.createDirectories(entry);
		}
		entry = entry.getParent();
		entry = Paths.get(entry.toString(), "lib");
		if (!entry.toFile().exists()) {
			Files.createDirectory(entry);
		}
		entry = entry.getParent();
		entry = Paths.get(entry.toString(), "configs");
		if (!entry.toFile().exists()) {
			Files.createDirectory(entry);
		}
		entry = entry.getParent();
		return entry;
	}

	Path workspaceBasePath() {
		Path path = null;
		URL url = WebsystemWorkspaceEntry.class.getProtectionDomain().getCodeSource().getLocation();
		String source = new File(url.getFile()).getPath();
		logger.info(String.format("source location: %s", source));
		if(source.contains("WEB-INF")){
			int n = source.indexOf("WEB-INF");
			source = source.substring(0,n);
			path = Paths.get(source);
			return path;
		}else if(source.contains("classes")){
			int n = source.indexOf("classes");
			source = source.substring(0,n);
			path = Paths.get(source);
			return path;
		}else if(source.contains("lib")){
			if(source.contains("workspace")){
				int n = source.indexOf("workspace");
				source = source.substring(0,n);
				path = Paths.get(source);
				return path;
			}
			int n = source.indexOf("lib");
			source = source.substring(0,n);
			path = Paths.get(source);
			return path;
		}else{
			path = Paths.get(System.getProperty("user.dir"));
			return path;
		}
		
	}

	public URL[] workspaceClasspath() throws IOException {
		URL[] urls = null;
		List<URL> urllist = new ArrayList<URL>();
		Path workspace = Paths.get(this.dependsEntry().toString(), "classes");
		Files.walkFileTree(workspace, new SimpleFileVisitor<Path>() {

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
				String filename = file.toFile().getName();
				if (filename.endsWith("class") || filename.endsWith("jar")) {
					File tmp = file.toFile();
					URL url = tmp.toURI().toURL();
					urllist.add(url);
				}
				return FileVisitResult.CONTINUE;
			}

		});

		urls = new URL[urllist.size()];
		urllist.toArray(urls);
		return urls;

	}
	public Path workspace(){
		return this.basePath;
	}
	public static void main(String[] args) {
		WebsystemWorkspaceEntry wwe  = new WebsystemWorkspaceEntry();
		System.out.println(wwe.workspaceBasePath());
	}

}
