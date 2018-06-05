package com.websystem.workspace.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.security.auth.x500.X500Principal;

import com.websystem.security.WebsystemKeygenX509CertAction;
import com.websystem.service.spi.WebsystemWorkspaceConstance;
import com.websystem.util.WebsystemXMLBeanUtil;
import com.websystem.xml.bean.Websystem;

public class WebsystemWorkspaceConfiguation {

	private static ResourceBundle bundle;
	private static WebsystemWorkspaceConfiguation config = null;
	public static <R> Path classLocation(Class<R> type) {

		Path p = null;
		URL url = type.getProtectionDomain().getCodeSource().getLocation();
		String us = url.getPath();
		File file = new File(us);
		p = Paths.get(file.getPath());
		return p;

	}
	public static WebsystemWorkspaceConfiguation newInstance() {
		if (config == null) {
			config = new WebsystemWorkspaceConfiguation();
		}
		return config;
	}
	private X509Certificate cert = null;
	private Logger logger = Logger.getLogger("WebsystemWorkspaceConfiguation");
	private WorkspaceInitial winit;
	private Path workspace;
	private WebsystemWorkspaceConfiguation() {
		super();
		// TODO Auto-generated constructor stub
		String item = WebsystemWorkspaceConstance.WEBSYS_WORKSPACE_ENTRY_ITEM;
		item = item.substring(0, item.lastIndexOf("."));
		bundle = ResourceBundle.getBundle(item);
		winit = WorkspaceInitial.newInstance();

		try {
			workspace = winit.workspace();
			cert = certificate();
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e.getMessage(), e);
		}

	}

	X509Certificate certificate() throws IOException, ClassNotFoundException {
		newKeyStore();
		X509Certificate cert = null;
		ResourceBundle bundle = ResourceBundle
				.getBundle("META-INF/configs/secure_entry");
		String alias = bundle
				.getString(WebsystemWorkspaceConstance.WEBSYS_SECURITY_STORE_ENTRY_ALIAS_KEY);
		String epass = bundle
				.getString(WebsystemWorkspaceConstance.WEBSYS_SECURITY_STORE_ENTRY_PASSWORD_KEY);
		// String p_pass =
		// bundle.getString(WebsystemWorkspaceConstance.WEBSYS_SECURITY_STORE_PROTECTED_PASSWORD_KEY);
		Path path = Paths.get(this.workspace.toString(), "workspace");
		path = Paths
				.get(path.toString(),
						"security",
						"keystore",
						WebsystemWorkspaceConstance.WEBSYS_SECURITY_KEYSTORE_DEFAULT_FILENAME);
		WebsystemKeygenX509CertAction kaction = new WebsystemKeygenX509CertAction();
		cert = kaction.loaderFromKeyStore(alias, epass, path.toString());
		createWorkspace(cert);
		return cert;
	}
	public Path copyPolicy() throws IOException {
		return this.winit.copyPolicy();
	}
	void createWorkspace(X509Certificate cert) throws ClassNotFoundException,
			IOException {
		if (cert != null) {
			winit.workFrame(cert);
		} else {
			throw new RuntimeException("field \"cert\" must not be null.");
		}

	}

	public Path currentClassLocation() throws MalformedURLException,
			ClassNotFoundException {
		Path p = null;
		String source = WebsystemWorkspaceConfiguation.class.getName();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		loader.loadClass(source);
		source = source.replaceAll("\\.", "/") + ".class";
		URL url = loader.getResource(source);
		source = url.getFile();
		if (source.contains("!")) {
			source = source.split("!")[0];
			url = new URL(source);
			source = url.getFile();
		}
		File file = new File(source);
		p = Paths.get(file.getPath());
		return p;
	}
	public Path defaultWorkspace() throws ClassNotFoundException, IOException {
		return Paths.get(this.workspace.toString(), "workspace");
	}

	public X509Certificate loadCertificate() {
		return cert;
	}

	public String loadXML_Dir() throws IOException {

		String xdir = null;
		xdir = this.winit.copyXML().toString();
		if (xdir == null || "".equals(xdir.trim())) {
			xdir = bundle
					.getString(WebsystemWorkspaceConstance.WEBSYS_WORKSPACE_XML_DIR_KEY);
		}
		return xdir;
	}

	public String log4jConfigFile() {
		String source = null;
		try {

			source = bundle
					.getString(WebsystemWorkspaceConstance.WEBSYS_LOG4J_PROPERTY_KEY);

		} catch (Exception e) {
			logger.info(String.format("%s is not found.",
					WebsystemWorkspaceConstance.WEBSYS_LOG4J_PROPERTY_KEY));
			return null;
		}
		if (source == null || "".equals(source.trim())) {
			logger.info(String.format("%s is not found.",
					WebsystemWorkspaceConstance.WEBSYS_LOG4J_PROPERTY_KEY));
			return null;
		}
		return source;
	}

	public Path logPath() throws IOException {
		Path path = null;
		String source = storagePath().toString();
		path = Paths.get(source, "logs");
		if (!path.toFile().exists()) {
			Files.createDirectories(path);
		}
		return path;
	}

	void newKeyStore() throws IOException {
		Path p = Paths
				.get(this.workspace.toString(),
						"workspace",
						"security",
						"keystore",
						WebsystemWorkspaceConstance.WEBSYS_SECURITY_KEYSTORE_DEFAULT_FILENAME);
		if (!p.toFile().exists()) {
			cert = winit.createKeystore();
		} else {
			return;
		}
	}

	boolean parserDName(String dname) {
		if (dname == null) {
			logger.info(String.format("\"%s\" is null, return \"false\".",
					"dname"));
			return false;
		}
		if (dname.trim().isEmpty()) {
			logger.info(String.format("\"%s\" is empty, return \"false\".",
					"dname"));
			return false;
		}
		X500Principal x500 = null;
		try {

			x500 = new X500Principal(dname);

		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		return x500 != null;
	}

	public Websystem queryXMLBean() throws IOException, ClassNotFoundException {
		Websystem web = null;
		Path path = Paths.get(defaultWorkspace().toString(), "configs");
		Path[] qp = new Path[2];
		if (qp[0] == null) {
			Files.walkFileTree(path, new SimpleFileVisitor<Path>() {

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
					int n = 0;
					if (n == 2) {
						return FileVisitResult.TERMINATE;
					}
					if (name.endsWith(WebsystemWorkspaceConstance.WEBSYS_WORKSPACE_XML_FILE_NAME)
							&& qp[0] == null) {

						qp[0] = file;
						logger.info(String.format("XML has found: %s.", file
								.toFile().getName()));
						n++;
					} else if (name.endsWith("schema.xsd") && qp[1] == null) {
						qp[1] = file;
						logger.info(String.format("Schema has found: %s.", file
								.toFile().getName()));
						n++;
					}

					return FileVisitResult.CONTINUE;
				}

			});
		}

		if (qp[0] != null) {
			WebsystemXMLBeanUtil<Websystem> util = new WebsystemXMLBeanUtil<Websystem>(
					qp[0], qp[1]);
			web = util.unMarshaller(Websystem.class);
			logger.info(String.format("load XMLBean: %s is complete.Path: %s",
					web.getClass().getSimpleName(), qp[0]));
			return web;
		}
		String source = loadXML_Dir();
		path = Paths.get(source);
		Files.walkFileTree(path, new SimpleFileVisitor<Path>() {

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
				int n = 0;
				if (n == 2) {
					return FileVisitResult.TERMINATE;
				}
				if (name.endsWith(WebsystemWorkspaceConstance.WEBSYS_WORKSPACE_XML_FILE_NAME)
						&& qp[0] == null) {

					qp[0] = file;
					logger.info(String.format("XML has found: %s.", file
							.toFile().getName()));
					n++;
				} else if (name.endsWith("schema.xsd") && qp[1] == null) {
					qp[1] = file;
					logger.info(String.format("Schema has found: %s.", file
							.toFile().getName()));
					n++;
				}

				return FileVisitResult.CONTINUE;
			}

		});
		if (qp[0] == null) {
			logger.info(String.format("XMLBean: %s is not found, return null.",
					Websystem.class.getSimpleName()));
			return null;
		}
		WebsystemXMLBeanUtil<Websystem> util = new WebsystemXMLBeanUtil<Websystem>(
				qp[0], qp[1]);
		web = util.unMarshaller(Websystem.class);
		logger.info(String.format("load XMLBean: %s is complete.Path: %s", web
				.getClass().getSimpleName(), path));
		return web;
	}

	public List<String> readBlockList() throws IOException {
		List<String> blocks = Collections.emptyList();
		Path path = Paths.get(loadXML_Dir(), "blockchain", "block.list");
		File blockfile = path.toFile();
		if (blockfile.exists() && !blockfile.isDirectory()
				&& blockfile.length() > 0) {
			blocks = Files.readAllLines(path);
			logger.info("Load block.list successful.");
		} else if (blockfile.length() == 0) {
			logger.info("block.list is empty, return empty.");
		}
		return blocks;
	}

	public List<String> readChainList() throws IOException {
		List<String> chains = Collections.emptyList();
		Path path = Paths.get(loadXML_Dir(), "blockchain", "chain.list");
		File chainfile = path.toFile();
		if (chainfile.exists() && !chainfile.isDirectory()
				&& chainfile.length() > 0) {
			chains = Files.readAllLines(path);
			logger.info("Load chain.list successful.");
		} else if (chainfile.length() == 0) {
			logger.info("chain.list is empty, return empty.");
		}
		return chains;
	}

	public Path storagePath() throws IOException {
		return this.winit.storagePath();
	}

}
