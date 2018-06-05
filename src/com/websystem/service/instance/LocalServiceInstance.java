package com.websystem.service.instance;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import com.websystem.domain.LoaderEnum;
import com.websystem.service.LocalService;
import com.websystem.util.Pair;
import com.websystem.workspace.util.WebsystemWorkspaceConfiguation;

public class LocalServiceInstance extends UnicastRemoteObject
		implements
			LocalService {

	/**
	 * version
	 */
	private static final long serialVersionUID = 5101456103095916665L;
	private WebsystemWorkspaceConfiguation config;
	private LoaderEnum le;
	private ArrayBlockingQueue<X509Certificate> queue;
	public LocalServiceInstance() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
		config = WebsystemWorkspaceConfiguation.newInstance();
		le = LoaderEnum.LOAD;
		queue = new ArrayBlockingQueue<X509Certificate>(1);
	}

	@Override
	public LoaderEnum access(X509Certificate cert) throws RemoteException {
		// TODO Auto-generated method stub
		if (cert == null) {
			le = LoaderEnum.UNLOAD;
			return le;
		}
		
		le = LoaderEnum.LOAD;
		queue.offer(cert);
		return le;
	}

	@Override
	public Pair<String[], String[]> load(X509Certificate cert)
			throws RemoteException {
		// TODO Auto-generated method stub
		Pair<String[], String[]> pair = null;
		if (le == LoaderEnum.LOAD && cert != null) {
			long n0 = -1;
			long n1 = -2;
			X509Certificate qcert = queue.peek();
			n0 = qcert.getSerialNumber().longValue();
			n1 = cert.getSerialNumber().longValue();
			if (n0 == n1) {
				
				pair = pair(cert);

				queue.remove();
				le = LoaderEnum.UNLOAD;
			}
		}
		return pair;
	}

	Pair<String[], String[]> pair(X509Certificate cert) throws RemoteException {
		Pair<String[], String[]> pair = null;
		try {
			Path basepath = config.defaultWorkspace();
			String[] classFiles = null;
			String[] jarFiles = null;
			List<String> clist = new ArrayList<String>();
			List<String> jlist = new ArrayList<String>();
			Path walkpath = Paths.get(basepath.toString(), "depends");
			Files.walkFileTree(walkpath, new SimpleFileVisitor<Path>() {

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
					String name = file.toString();
					if (name.endsWith(".class")) {
						int n= name.lastIndexOf("classes");
						name = name.substring(0,n)+"classes";
						clist.add(name);
					} else if (name.endsWith(".jar")) {
						jlist.add(name);
					}
					return FileVisitResult.CONTINUE;
				}

			});

			walkpath = Paths.get(basepath.toString(), "thirdparty",
					parserUser(cert));
			Files.walkFileTree(walkpath, new SimpleFileVisitor<Path>() {

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
					String name = file.toString();
					if (name.endsWith(".class")) {
						int n= name.lastIndexOf("classes");
						name = name.substring(0,n)+"classes";
						clist.add(name);
					} else if (name.endsWith(".jar")) {
						jlist.add(name);
					}
					return FileVisitResult.CONTINUE;
				}

			});
			
			int n = clist.size();
			classFiles = new String[n];
			clist.toArray(classFiles);
			n = jlist.size();
			jarFiles = new String[n];
			jlist.toArray(jarFiles);
			pair = new Pair<String[],String[]>(classFiles,jarFiles);

		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			throw new RemoteException(e.getMessage(), e);
		}
		return pair;
	}

	String parserUser(X509Certificate cert) {
		String name = cert.getSubjectX500Principal().getName();
		long num = cert.getSerialNumber().longValue();
		String[] tmps = name.split(",");
		for (String tmp:tmps) {
			if (tmp.toLowerCase().contains("o=")) {
				name = tmp;
				break;
			}
		}
		name = name.split("=")[1];
		name = name + "@" + num;
		return name;
	}

}
