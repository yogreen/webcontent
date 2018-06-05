package com.websystem.service.instance;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.websystem.domain.WebsystemProcessStateEnum;
import com.websystem.domain.WebsystemWareHouse;
import com.websystem.security.RSACipher;
import com.websystem.service.spi.SuperActionService;
import com.websystem.util.Pair;
import com.websystem.util.PlatformUtil;
import com.websystem.workspace.util.ActionClassLoader;
import com.websystem.workspace.util.ObjectExchangeFactory;
import com.websystem.workspace.util.WebsystemWorkspaceConfiguation;

public class ActionServiceInstance extends UnicastRemoteObject
		implements
			SuperActionService {

	/**
	 * version
	 */
	private static final long serialVersionUID = -6264054892779415732L;
	private ActionClassLoader actionloader;
	private ArrayBlockingQueue<X509Certificate> cert_queue;
	private LinkedBlockingQueue<X509Certificate> certs_queue;
	private RSACipher cipher = null;
	private ArrayBlockingQueue<X509Certificate> clean_use;
	private WebsystemWorkspaceConfiguation config;
	private KeyStoreHandler keyHandler;
	private WebsystemProcessStateEnum state;
	private ArrayBlockingQueue<WebsystemWareHouse> ware_queue;
	public ActionServiceInstance() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
		cert_queue = new ArrayBlockingQueue<X509Certificate>(1);
		certs_queue = new LinkedBlockingQueue<X509Certificate>();
		clean_use = new ArrayBlockingQueue<X509Certificate>(1);
		keyHandler = new KeyStoreHandler();
		state = WebsystemProcessStateEnum.ENTRY;
		cipher = new RSACipher();
		config = WebsystemWorkspaceConfiguation.newInstance();
		ware_queue = new ArrayBlockingQueue<WebsystemWareHouse>(1);
		actionloader = new ActionClassLoader();
	}

	@Override
	public byte[] accessCertificate(X509Certificate paramX509Certificate)
			throws RemoteException {
		// TODO Auto-generated method stub
		byte[] codes = null;
		List<byte[]> codelist = new ArrayList<byte[]>();
		if (state == WebsystemProcessStateEnum.ENTRY) {

			X509Certificate local_cert = localPair().getFirst();
			X509Certificate input_cert = paramX509Certificate;
			if (input_cert == null) {
				input_cert = local_cert;
			}
			join(paramX509Certificate);
			RSAPublicKey pub = (RSAPublicKey) input_cert.getPublicKey();
			try {
				codelist = cipher.RSAencode(local_cert, pub);
				codes = PlatformUtil.objectMarshal(codelist);
			} catch (IOException | GeneralSecurityException e) {
				// TODO Auto-generated catch block
				throw new RemoteException(e.getMessage(), e);
			}
		}
		state = WebsystemProcessStateEnum.NOW;
		return codes;
	}

	/**
	 * @param cert_source
	 * @throws RemoteException
	 */
	private Path basePath(X509Certificate cert_source) throws RemoteException {
		Path base = null;
		try {
			base = config.storagePath();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new RemoteException(e.getMessage(), e);
		}
		base = Paths.get(base.getParent().toString(), "thirdparty",
				parserPath(cert_source));
		return base;
	}

	@Override
	public void clean() throws RemoteException {
		// TODO Auto-generated method stub
		long n1 = -1;
		long n2 = -2;
		X509Certificate cert_source = clean_use.peek();
		X509Certificate cert_input = cert_queue.peek();
		if (cert_source != null && cert_input != null
				&& state == WebsystemProcessStateEnum.END_COMPLETE) {

			n1 = cert_source.getSerialNumber().longValue();
			n2 = cert_input.getSerialNumber().longValue();
			if (n1 != n2) {
				try {
					Path path = basePath(cert_source);
					Files.walkFileTree(path, new SimpleFileVisitor<Path>() {

						@Override
						public FileVisitResult preVisitDirectory(Path dir,
								BasicFileAttributes attrs) throws IOException {
							// TODO Auto-generated method stub
							File file = dir.toFile();
							if (file.list().length == 0) {
								file.delete();
							}
							return FileVisitResult.CONTINUE;
						}

						@Override
						public FileVisitResult visitFile(Path file,
								BasicFileAttributes attrs) throws IOException {
							// TODO Auto-generated method stub
							File f = file.toFile();
							f.delete();
							if (f.getParentFile().list().length == 0) {
								f.getParentFile().delete();
							}
							return FileVisitResult.CONTINUE;
						}

					});
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			clean_use.remove();
		}

		if (state == WebsystemProcessStateEnum.END_COMPLETE) {
			state = WebsystemProcessStateEnum.END_SUCCESSFUL;
		}

	}

	@Override
	public void cleanAfter(long time, TimeUnit unit) throws RemoteException {
		// TODO Auto-generated method stub
		ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
		service.schedule(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					clean();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					Thread.currentThread().interrupt();
				}
			}

		}, time, unit);
		service.shutdown();

	}

	@Override
	public byte[] delegateCompute(String classname, String methodName,
			Map<String, byte[]> constructorParames,
			Map<String, byte[]> methodParames) throws RemoteException {
		// TODO Auto-generated method stub
		byte[] codes = null;
		WebsystemWareHouse input = ware_queue.peek();
		if (state == WebsystemProcessStateEnum.END_COMPLETE && input != null) {
			long n1 = -1;
			long n2 = -2;
			X509Certificate cert_source = input.getSourceCertificate();
			X509Certificate cert_input = cert_queue.peek();
			if (cert_source != null && cert_input != null) {

				n1 = cert_source.getSerialNumber().longValue();
				n2 = cert_input.getSerialNumber().longValue();
			}
			if (n1 == n2) {
				RSAPrivateKey prv = localPair().getSecond();
				try {
					Method method = null;
					Pair<Class<?>[], Object[]> mpair = exchange(methodParames,
							cert_input, prv);
					Pair<Class<?>[], Object[]> cpair = exchange(
							constructorParames, cert_input, prv);
					Class<?> clazz = actionloader.loaderSource(classname,
							cert_source);
					Object obj = null;
					if (cpair == null) {

						obj = ObjectExchangeFactory.newInstance(clazz, null);
					} else {
						obj = ObjectExchangeFactory.newInstance(clazz,
								cpair.getSecond());
					}
					if (mpair == null) {
						method = clazz.getMethod(methodName);
						obj = method.invoke(obj);
					} else {
						Class<?>[] cs = exchange(methodParames, cert_input, prv)
								.getFirst();
						Object[] objs = exchange(methodParames, cert_input, prv)
								.getSecond();
						method = clazz.getMethod(methodName, cs);
						obj = method.invoke(obj, objs);
					}
					List<byte[]> codelist = cipher.RSAencode(obj,
							(RSAPublicKey) cert_source.getPublicKey());
					codes = PlatformUtil.objectMarshal(codelist);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new RemoteException(e.getMessage(), e);
				}

			}
		}
		if (!certs_queue.isEmpty()) {
			certs_queue.remove();
		}
		ware_queue.remove();
		clean_use.offer(cert_queue.remove());
		if (certs_queue.peek() != null) {

			cert_queue.offer(certs_queue.peek());
		}
		state = WebsystemProcessStateEnum.END_COMPLETE;
		return codes;
	}

	private Pair<Class<?>[], Object[]> exchange(
			Map<String, byte[]> methodParames, X509Certificate cert,
			RSAPrivateKey prv) throws RemoteException {
		Pair<Class<?>[], Object[]> pair = null;
		if (methodParames == null || methodParames.isEmpty()) {
			return null;
		}
		int n = methodParames.size();
		Class<?>[] clazzes = new Class<?>[n];
		Object[] os = new Object[n];
		int i = 0;

		for (Map.Entry<String, byte[]> en : methodParames.entrySet()) {
			if (i == n) {
				break;
			}
			try {
				clazzes[i] = actionloader.loaderSource(en.getKey(), cert);
				os[i] = exchange0(en.getValue(), prv);
				i++;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				throw new RemoteException(e.getMessage(), e);
			}

		}
		pair = new Pair<Class<?>[], Object[]>(clazzes, os);

		return pair;
	}

	@SuppressWarnings("unchecked")
	private Object exchange0(byte[] codes, RSAPrivateKey prv) throws Exception {
		Object pair = null;

		List<byte[]> codelist = (List<byte[]>) PlatformUtil
				.objectUnMarshal(codes);
		pair = cipher.RSAdecode(codelist, prv);
		return pair;
	}

	private void join(X509Certificate cert) {
		cert_queue.offer(cert);
		certs_queue.offer(cert);
	}

	Pair<X509Certificate, RSAPrivateKey> localPair() {
		return keyHandler.loadPair();
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

	@SuppressWarnings("unchecked")
	@Override
	public void transfer(WebsystemWareHouse ware) throws RemoteException {
		// TODO Auto-generated method stub
		if (state == WebsystemProcessStateEnum.NOW) {
			this.ware_queue.offer(ware);
			long n1 = -1;
			long n2 = -2;
			X509Certificate cert_source = ware.getSourceCertificate();
			X509Certificate cert_input = cert_queue.peek();
			if (cert_source != null && cert_input != null) {

				n1 = cert_source.getSerialNumber().longValue();
				n2 = cert_input.getSerialNumber().longValue();
				List<byte[]> codelist = new ArrayList<byte[]>();
				Pair<List<String>, List<byte[]>> clazzpair = null;
				Pair<String, byte[]> pair = null;
				String basename = basePath(cert_source).toString();
				Path bpath = Paths.get(basename);

				if (n1 == n2) {
					clazzpair = ware.getClasses_depends();
					RSAPrivateKey prv = localPair().getSecond();
					if (clazzpair != null) {

						List<String> names = clazzpair.getFirst();
						codelist = clazzpair.getSecond();
						int size = names.size();
						for (int i = 0; i < size; i++) {
							String name = names.get(i);
							byte[] tmps = codelist.get(i);
							if (name.contains(".")) {
								name = name.replaceAll("\\.", File.separator);
								name = new File(name).getPath() + ".class";
							}
							try {
								List<byte[]> list = (List<byte[]>) PlatformUtil
										.objectUnMarshal(tmps);
								tmps = (byte[]) cipher.RSAdecode(list, prv);
								Path path = Paths.get(bpath.toString(),
										"classes", name);
								if (!path.toFile().exists()) {
									String tmp = name;
									int n = tmp.lastIndexOf(File.separator);
									tmp = tmp.substring(0, n);
									Files.createDirectories(Paths.get(
											bpath.toString(), "classes", tmp));
									Files.createFile(path);
								}
								Files.write(path, tmps,
										StandardOpenOption.WRITE);
							} catch (ClassNotFoundException | IOException
									| InvalidKeyException
									| NoSuchAlgorithmException
									| NoSuchPaddingException
									| IllegalBlockSizeException
									| BadPaddingException e) {
								// TODO Auto-generated catch block
								throw new RemoteException(e.getMessage(), e);
							}
						}
					}
					pair = ware.getJar_depends();
					if (pair != null) {
						String name = pair.getFirst();

						byte[] tmps = pair.getSecond();
						try {
							codelist = (List<byte[]>) PlatformUtil
									.objectUnMarshal(tmps);
							tmps = (byte[]) cipher.RSAdecode(codelist, prv);
							Path path = Paths.get(basename, "lib");
							if (!path.toFile().exists()) {
								Files.createDirectories(path);
							}
							path = Paths.get(path.toString(), name);
							if (!path.toFile().exists()) {
								Files.createFile(path);
							}
							Files.write(path, tmps, StandardOpenOption.WRITE);

						} catch (ClassNotFoundException | IOException
								| InvalidKeyException
								| NoSuchAlgorithmException
								| NoSuchPaddingException
								| IllegalBlockSizeException
								| BadPaddingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					pair = ware.getCommon_depends();
					if (pair != null) {
						String name = pair.getFirst();

						byte[] tmps = pair.getSecond();
						try {
							codelist = (List<byte[]>) PlatformUtil
									.objectUnMarshal(tmps);
							tmps = (byte[]) cipher.RSAdecode(codelist, prv);
							Path path = Paths.get(basename, "configs");
							if (!path.toFile().exists()) {
								Files.createDirectories(path);
							}
							path = Paths.get(path.toString(), name);
							if (!path.toFile().exists()) {
								Files.createFile(path);
							}
							Files.write(path, tmps, StandardOpenOption.WRITE);

						} catch (ClassNotFoundException | IOException
								| InvalidKeyException
								| NoSuchAlgorithmException
								| NoSuchPaddingException
								| IllegalBlockSizeException
								| BadPaddingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				}
			}

			state = WebsystemProcessStateEnum.END_COMPLETE;

		}

	}
	@Override
	public WebsystemProcessStateEnum tryAccess() throws RemoteException {
		// TODO Auto-generated method stub
		if (state == WebsystemProcessStateEnum.END_COMPLETE
				|| state == WebsystemProcessStateEnum.END_SUCCESSFUL) {
			state = WebsystemProcessStateEnum.ENTRY;
		}
		return state;
	}

}
