package com.websystem.workspace.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import com.websystem.service.LocalService;
import com.websystem.service.spi.WebsystemWorkspaceConstance;
import com.websystem.util.Pair;

public class ActionClassLoader {

	private class WatchDir {
		private X509Certificate cert;
		private int count;
		private final boolean recursive;
		private boolean trace = false;
		private final WatchService watcher;
		private LocalService service = null;

		/**
		 * Creates a WatchService and registers the given directory
		 */
		public WatchDir(Path dir, X509Certificate cert, boolean recursive)
				throws IOException {
			this.watcher = FileSystems.getDefault().newWatchService();
			this.recursive = recursive;
			this.cert = cert;
			this.trace = true;
			

			if (recursive) {
				logger.info(String.format("Scanning %s ...\n", dir));
				registers(dir);
				
				logger.info("Done.");
			} else {
				register(dir);
			}

			// enable trace after initial registration
		}

		@SuppressWarnings("unchecked")
		<T> WatchEvent<T> cast(WatchEvent<?> event) {
			return (WatchEvent<T>) event;
		}

		/**
		 * Process all events for keys queued to the watcher
		 */
		void processEvents() {
			try {
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			for (;;) {

				// wait for key to be signalled
				WatchKey key;
				try {
					key = watcher.take();
				} catch (InterruptedException x) {
					return;
				}

				for (WatchEvent<?> event : key.pollEvents()) {
					WatchEvent.Kind<?> kind = event.kind();

					// TBD - provide example of how OVERFLOW event is handled
					if (kind == StandardWatchEventKinds.OVERFLOW) {
						continue;
					}

					// Context for directory entry event is the file name of
					// entry
					WatchEvent<Path> ev = cast(event);
					Path name = ev.context();
					Path child = ((Path) key.watchable()).resolve(name);

					// print out event
					logger.info(String.format("%s: %s\n", event.kind().name(), child));

					// if directory is created, and watching recursively, then
					// register it and its sub-directories
					if (recursive
							&& (kind == StandardWatchEventKinds.ENTRY_CREATE)) {
						try {
							if (Files.isDirectory(child,
									LinkOption.NOFOLLOW_LINKS)) {
								registers(child);
								try {
									this.service = local();
									this.service.access(cert);
									this.service.load(cert);
								} catch (NotBoundException e) {
									// TODO Auto-generated catch block
									
								}
								
							}
						} catch (IOException x) {
							// ignore to keep sample readbale
						}
					}
				}

				// reset key
				boolean valid = key.reset();
				if (!valid) {
					// directory no longer accessible
					count--;
					if (count == 0)
						break;
				}
			}
		}

		/**
		 * Register the given directory with the WatchService
		 */
		private void register(Path dir) throws IOException {
			dir.register(watcher,
					StandardWatchEventKinds.ENTRY_CREATE,
					StandardWatchEventKinds.ENTRY_DELETE,
					StandardWatchEventKinds.ENTRY_MODIFY);
			count++;
			if (trace){
				
				logger.info(String.format("register: %s\n", dir));
				try {
					logger.info("newLoader is completed.");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}

		/**
		 * Register the given directory, and all its sub-directories, with the
		 * WatchService.
		 */
		private void registers(final Path start) throws IOException {
			// register directory and sub-directories
			Files.walkFileTree(start, new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(Path dir,
						BasicFileAttributes attrs) throws IOException {
					// TODO Auto-generated method stub
					String name = dir.toString();
					try {
						List<File[]> filelist = classSources(cert);
						for (File[] files : filelist) {
							File[] tmps = files;
							for (int i = 0; i < tmps.length; i++) {
								if (tmps[i].getPath().equals(name)) {

									register(dir);
									
								}
							}
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return FileVisitResult.CONTINUE;
				}

			});
			
		}
		

	}
	private WebsystemWorkspaceConfiguation config;

	private ClassLoader loader;

	private Logger logger = Logger.getLogger(ActionClassLoader.class
			.getSimpleName());

	public ActionClassLoader() {
		// TODO Auto-generated constructor stub
		config = WebsystemWorkspaceConfiguation.newInstance();
		Path path = null;
		Path p = null;
		try {
			path = policyPath();
			p = config.storagePath();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e.getMessage(), e);
		}
		if (!path.toFile().exists()) {
			throw new IllegalStateException(
					String.format(
							"File: %s is not found",
							WebsystemWorkspaceConstance.WEBSYS_WORKSPACE_POLICY_FILE_NAME));
		}
		p = Paths.get(p.getParent().toString(), "thirdparty");
		System.setProperty("thirdparty_dir", p.toString());
		System.setProperty(
				WebsystemWorkspaceConstance.WEBSYS_WORKSPACE_POLICY_SYSTEM_KEY,
				path.toString());
		if (System.getSecurityManager() == null) {

			SecurityManager sm = new SecurityManager();
			System.setSecurityManager(sm);
		}
	}
	List<File[]> classSources(X509Certificate cert) throws Exception {
		List<File[]> sources = new ArrayList<File[]>();
		List<File> unpacks = new ArrayList<File>();
		List<File> jarpacks = new ArrayList<File>();
		Path basepath = config.defaultWorkspace();
		Path depends = Paths.get(basepath.toString(), "depends", "classes");
		unpacks.add(depends.toFile());
		depends = Paths.get(basepath.toString(), "thirdparty",
				parserPath(cert), "classes");
		unpacks.add(depends.toFile());
		depends = Paths.get(basepath.toString(), "depends", "lib");
		jarpacks.add(depends.toFile());
		depends = Paths.get(basepath.toString(), "thirdparty",
				parserPath(cert), "lib");
		jarpacks.add(depends.toFile());
		File[] unpack = null;
		File[] jarpack = null;
		if (!unpacks.isEmpty()) {
			int n = unpacks.size();
			unpack = new File[n];
			unpacks.toArray(unpack);
		}
		if (!jarpacks.isEmpty()) {
			int n = jarpacks.size();
			jarpack = new File[n];
			jarpacks.toArray(jarpack);
		}
		sources.add(unpack);
		sources.add(jarpack);
		return sources;
	}
	ClassLoader defaultLoader(X509Certificate cert) throws Exception {
		
		File[] unpack = classSources(cert).get(0);
		File[] jarpack = classSources(cert).get(1);
		
		loader = ActionClassLoaderFactory.createClassLoader(unpack,
				jarpack, null);
		return loader;
	}
	ClassLoader loaderFactory(X509Certificate cert) throws Exception{
		loader = newClassLoader(cert);
		if (loader == null) {
			loader = defaultLoader(cert);
		}

		return loader;
	}
	public Class<?> loaderSource(String name, X509Certificate cert)
			throws Exception {
		Class<?> clazz = null;
		loader = loaderFactory(cert);
		clazz = loader.loadClass(name);
		return clazz;
	}
	LocalService local() throws MalformedURLException, RemoteException, NotBoundException{
		LocalService service = (LocalService) Naming.lookup("rmi://localhost:1099/service@loader");
		
		return service;
	}
	ClassLoader newClassLoader(X509Certificate cert) throws Exception {
		execute(cert);
		//ExecutorService service1 = Executors.newFixedThreadPool(1);
		LocalService service = local();
		service.access(cert);
		Pair<String[],String[]> pair = service.load(cert);
		if(pair==null){
			return null;
		}
		String[] first = pair.getFirst();
		String[] second = pair.getSecond();
		File[] cfiles = new File[first.length];
		File[] jfiles = new File[second.length];
		if(first!=null){
			
			for(int i=0;i<first.length;i++){
				File file = new File(first[i]);
				cfiles[i] = file;
			}
		}
		if(second!=null){
			
			for(int i=0;i<second.length;i++){
				File file = new File(second[i]);
				jfiles[i] = file;
			}
		}
		
		loader = ActionClassLoaderFactory.createClassLoader(cfiles, jfiles, null);
		logger.info(String.format(" loader is: %s", loader));
		return loader;
	}
	void execute(X509Certificate cert) throws ClassNotFoundException, IOException{
		Path basepath = config.defaultWorkspace();
		Path depends0 = Paths.get(basepath.toString());
		

		ExecutorService eservice = Executors.newFixedThreadPool(1);
		eservice.execute(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					WatchDir wdir = new WatchDir(depends0, cert, true);
					wdir.processEvents();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
	}
	String parserPath(X509Certificate cert) {

		long serianum = cert.getSerialNumber().longValue();
		String name = cert.getSubjectX500Principal().getName();
		String[] tmps = name.split(",");
		for (String tmp : tmps) {
			if (tmp.toLowerCase().contains("o=")) {
				name = tmp;
			}
		}
		name = name.split("=")[1];
		name = name + "@" + serianum;

		return name;
	}
	
	Path policyPath() throws IOException {
		Path path = null;
		path = Paths.get(config.storagePath().getParent().toString(),
				"configs", "policy",
				WebsystemWorkspaceConstance.WEBSYS_WORKSPACE_POLICY_FILE_NAME);
		return path;
	}
}
