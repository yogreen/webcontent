/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.websystem.workspace.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.cert.X509Certificate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Logger;

import com.websystem.workspace.util.WebsystemWorkspaceConfiguation;

/**
 * <p>
 * Utility class for building class loaders for Catalina. The factory method
 * requires the following parameters in order to build a new class loader (with
 * suitable defaults in all cases):
 * </p>
 * <ul>
 * <li>A set of directories containing unpacked classes (and resources) that
 * should be included in the class loader's repositories.</li>
 * <li>A set of directories containing classes and resources in JAR files. Each
 * readable JAR file discovered in these directories will be added to the class
 * loader's repositories.</li>
 * <li><code>ClassLoader</code> instance that should become the parent of the
 * new class loader.</li>
 * </ul>
 *
 * @author Craig R. McClanahan
 */
final class ActionClassLoaderFactory {

	private static final Logger log = Logger
			.getLogger(ActionClassLoaderFactory.class.getSimpleName());
	private static WebsystemWorkspaceConfiguation config = WebsystemWorkspaceConfiguation
			.newInstance();

	// --------------------------------------------------------- Public Methods

	/**
	 * Create and return a new class loader, based on the configuration defaults
	 * and the specified directory paths:
	 *
	 * @param unpacked
	 *            Array of pathnames to unpacked directories that should be
	 *            added to the repositories of the class loader, or
	 *            <code>null</code> for no unpacked directories to be considered
	 * @param packed
	 *            Array of pathnames to directories containing JAR files that
	 *            should be added to the repositories of the class loader, or
	 *            <code>null</code> for no directories of JAR files to be
	 *            considered
	 * @param parent
	 *            Parent class loader for the new class loader, or
	 *            <code>null</code> for the system class loader.
	 * @return the new class loader
	 *
	 * @exception Exception
	 *                if an error occurs constructing the class loader
	 */
	public static ClassLoader createClassLoader(File unpacked[], File packed[],
			final ClassLoader parent) throws Exception {

		log.info("Creating new class loader");

		// Construct the "class path" for this class loader
		Set<URL> set = new LinkedHashSet<>();

		// Add unpacked directories
		if (unpacked != null) {
			for (int i = 0; i < unpacked.length; i++) {
				File file = unpacked[i];
				if (!file.canRead())
					continue;
				file = new File(file.getCanonicalPath() + File.separator);
				URL url = file.toURI().toURL();
					log.info("  Including directory " + url);
				set.add(url);
			}
		}

		// Add packed directory JAR files
		if (packed != null) {
			for (int i = 0; i < packed.length; i++) {
				File directory = packed[i];
				if (!directory.isDirectory() || !directory.canRead())
					continue;
				String filenames[] = directory.list();
				if (filenames == null) {
					continue;
				}
				for (int j = 0; j < filenames.length; j++) {
					String filename = filenames[j].toLowerCase(Locale.ENGLISH);
					if (!filename.endsWith(".jar"))
						continue;
					File file = new File(directory, filenames[j]);
						log.info("  Including jar file "
								+ file.getAbsolutePath());
					URL url = file.toURI().toURL();
					set.add(url);
				}
			}
		}

		// Construct the class loader itself
		final URL[] array = set.toArray(new URL[set.size()]);
		return AccessController
				.doPrivileged(new PrivilegedAction<URLClassLoader>() {
					@Override
					public URLClassLoader run() {
						if (parent == null)
							return new URLClassLoader(array);
						else
							return new URLClassLoader(array, parent);
					}
				});
	}

	/**
	 * Create and return a new class loader, based on the configuration defaults
	 * and the specified directory paths:
	 *
	 * @param repositories
	 *            List of class directories, jar files, jar directories or URLS
	 *            that should be added to the repositories of the class loader.
	 * @param parent
	 *            Parent class loader for the new class loader, or
	 *            <code>null</code> for the system class loader.
	 * @return the new class loader
	 *
	 * @exception Exception
	 *                if an error occurs constructing the class loader
	 */
	public static ClassLoader createClassLoader(List<Repository> repositories,
			final ClassLoader parent, X509Certificate cert) throws Exception {

			log.info("Creating new class loader");

		// Construct the "class path" for this class loader
		Set<URL> set = new LinkedHashSet<>();

		if (repositories != null) {
			for (Repository repository : repositories) {
				if (repository.getType() == RepositoryType.URL) {
					URL url = buildClassLoaderUrl(repository.getLocation());
						log.info("  Including URL " + url);
					set.add(url);
				} else if (repository.getType() == RepositoryType.DIR) {
					File directory = new File(repository.getLocation());
					directory = directory.getCanonicalFile();
					if (!validateFile(directory, RepositoryType.DIR, cert)) {
						continue;
					}
					URL url = buildClassLoaderUrl(directory);
						log.info("  Including directory " + url);
					set.add(url);
				} else if (repository.getType() == RepositoryType.JAR) {
					File file = new File(repository.getLocation());
					file = file.getCanonicalFile();
					if (!validateFile(file, RepositoryType.JAR, cert)) {
						continue;
					}
					URL url = buildClassLoaderUrl(file);
						log.info("  Including jar file " + url);
					set.add(url);
				} else if (repository.getType() == RepositoryType.GLOB) {
					File directory = new File(repository.getLocation());
					directory = directory.getCanonicalFile();
					if (!validateFile(directory, RepositoryType.GLOB, cert)) {
						continue;
					}
						log.info("  Including directory glob "
								+ directory.getAbsolutePath());
					String filenames[] = directory.list();
					if (filenames == null) {
						continue;
					}
					for (int j = 0; j < filenames.length; j++) {
						String filename = filenames[j]
								.toLowerCase(Locale.ENGLISH);
						if (!filename.endsWith(".jar"))
							continue;
						File file = new File(directory, filenames[j]);
						file = file.getCanonicalFile();
						if (!validateFile(file, RepositoryType.JAR, cert)) {
							continue;
						}
							log.info("    Including glob jar file "
									+ file.getAbsolutePath());
						URL url = buildClassLoaderUrl(file);
						set.add(url);
					}
				}
			}
		}

		// Construct the class loader itself
		final URL[] array = set.toArray(new URL[set.size()]);
			for (int i = 0; i < array.length; i++) {
				log.info("  location " + i + " is " + array[i]);
			}

		return AccessController
				.doPrivileged(new PrivilegedAction<URLClassLoader>() {
					@Override
					public URLClassLoader run() {
						if (parent == null)
							return new URLClassLoader(array);
						else
							return new URLClassLoader(array, parent);
					}
				});
	}

	private static boolean validateFile(File file, RepositoryType type,
			X509Certificate cert) throws IOException {
		if (RepositoryType.DIR == type || RepositoryType.GLOB == type) {
			if (!file.isDirectory() || !file.canRead()) {
				String msg = "Problem with directory [" + file + "], exists: ["
						+ file.exists() + "], isDirectory: ["
						+ file.isDirectory() + "], canRead: [" + file.canRead()
						+ "]";

				Path p = config.storagePath();
				
				p = Paths.get(p.getParent().toString(), "thirdparty",
						parserPath(cert));
				File home = new File(p.toString());
				home = home.getCanonicalFile();
				File base = new File(p.toString());
				base = base.getCanonicalFile();
				File defaultValue = new File(base, "lib");

				// Existence of ${catalina.base}/lib directory is optional.
				// Hide the warning if Tomcat runs with separate catalina.home
				// and catalina.base and that directory is absent.
				if (!home.getPath().equals(base.getPath())
						&& file.getPath().equals(defaultValue.getPath())
						&& !file.exists()) {
					log.info(msg);
				} 
				return false;
			}
		} else if (RepositoryType.JAR == type) {
			if (!file.canRead()) {
				log.info("Problem with JAR file [" + file + "], exists: ["
						+ file.exists() + "], canRead: [" + file.canRead()
						+ "]");
				return false;
			}
		}
		return true;
	}

	static String parserPath(X509Certificate cert) {

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

	/*
	 * These two methods would ideally be in the utility class
	 * org.apache.tomcat.util.buf.UriUtil but that class is not visible until
	 * after the class loaders have been constructed.
	 */
	private static URL buildClassLoaderUrl(String urlString)
			throws MalformedURLException {
		// URLs passed to class loaders may point to directories that contain
		// JARs. If these URLs are used to construct URLs for resources in a JAR
		// the URL will be used as is. It is therefore necessary to ensure that
		// the sequence "!/" is not present in a class loader URL.
		String result = urlString.replaceAll("!/", "%21/");
		return new URL(result);
	}

	private static URL buildClassLoaderUrl(File file)
			throws MalformedURLException {
		// Could be a directory or a file
		String fileUrlString = file.toURI().toString();
		fileUrlString = fileUrlString.replaceAll("!/", "%21/");
		return new URL(fileUrlString);
	}

	public static enum RepositoryType {
		DIR, GLOB, JAR, URL
	}

	public static class Repository {
		private final String location;
		private final RepositoryType type;

		public Repository(String location, RepositoryType type) {
			this.location = location;
			this.type = type;
		}

		public String getLocation() {
			return location;
		}

		public RepositoryType getType() {
			return type;
		}
	}
}
