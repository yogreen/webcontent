package com.websystem.service.instance;

import java.io.IOException;
import java.rmi.RemoteException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingException;

import com.websystem.naming.spi.DefaultNameParser;
import com.websystem.service.spi.WebsystemWorkspaceConstance;
import com.websystem.util.PlatformUtil;

class WebsystemNamingServiceHandler {

	private Lock lock = null;
	Context context;
	ConcurrentHashMap<String, String> entrySet;
	private Logger logger = Logger.getLogger("WebsystemNamingServiceHandler");
	private NameParser parser;
	private RSAPrivateKey prvkey = null;
	public WebsystemNamingServiceHandler(Context baseContext) {
		context = baseContext;
		parser = new DefaultNameParser();
		entrySet = new ConcurrentHashMap<String, String>();
		lock = new ReentrantLock();
		if (this.context != null) {
			try {
				KeyStoreHandler kshandle = keyStoreHandlerFactory();
				X509Certificate x509 = kshandle.loadPair().getFirst();
				RSAPrivateKey prv = kshandle.loadPair().getSecond();
				/*this.context
						.bind(WebsystemWorkspaceConstance.WEBSYS_RSA_PRIVATE_KEY,
								prv);*/
				prvkey = prv;
				this.context.bind(
						WebsystemWorkspaceConstance.WEBSYS_X509CERT_KEY, x509);
				/*entrySet.put(
						WebsystemWorkspaceConstance.WEBSYS_RSA_PRIVATE_KEY,
						WebsystemWorkspaceConstance.WEBSYS_RSA_PRIVATE_KEY);*/
				entrySet.put(WebsystemWorkspaceConstance.WEBSYS_X509CERT_KEY,
						WebsystemWorkspaceConstance.WEBSYS_X509CERT_KEY);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void bind(String name, String value) throws NamingException {
		lock.lock();
		try {
			String caches = name;
			if (caches.contains("/")) {
				caches = caches.replaceAll("/", "\\.");
			}
			this.entrySet.put(name, caches);
			Object obj = null;
			try {
				obj = this.context.lookup(name);
			} catch (Exception e) {

			}
			if (obj == null) {

				this.context.bind(caches, value);
			}
			logger.info(String.format("binding %s with %s.", name, caches));
		} finally {
			lock.unlock();
		}

	}
	public void bindContents(String name, byte[] contents)
			throws NamingException {
		lock.lock();
		try {
			String caches = name;
			if (caches.contains("/")) {
				caches = caches.replaceAll("/", "\\.");
			}
			this.entrySet.put(name, caches);
			Object obj = null;
			try {
				obj = this.context.lookup(name);
			} catch (Exception e) {

			}
			if (obj == null) {

				this.context.bind(caches, contents);
			}
			logger.info(String.format("binding %s with %s.", name, caches));
		} finally {
			lock.unlock();
		}

	}

	public void bindInputCert(X509Certificate cert) throws NamingException {
		Object obj = null;
		long seria = cert.getSerialNumber().longValue();
		String key = "input_cert_x509"+"@"+seria;
		try {
			obj = this.context.lookup(key);
		} catch (Exception e) {

		}
		if (obj == null) {
			this.entrySet.put(key, key);
			this.context.bind(key, cert);
		}
	}
	public void bindLocalCert(X509Certificate cert) throws NamingException {
		Object obj = null;
		try {
			obj = this.context
					.lookup(WebsystemWorkspaceConstance.WEBSYS_X509CERT_KEY);
		} catch (Exception e) {

		}
		if (obj == null) {
			this.entrySet.put(WebsystemWorkspaceConstance.WEBSYS_X509CERT_KEY,
					WebsystemWorkspaceConstance.WEBSYS_X509CERT_KEY);
			this.context.bind(WebsystemWorkspaceConstance.WEBSYS_X509CERT_KEY,
					cert);
		}
	}
	public void bindLocalPrivateKey(RSAPrivateKey prv) throws NamingException {
		this.prvkey = prv;
	}

	public synchronized void clean() throws RemoteException {
		// TODO Auto-generated method stub
		try {
			List<String> realKeys = realKeys();
			for (String key : realKeys) {
				Object obj = this.context.lookup(key);
				if (obj != null) {
					unbind(key);
				}
			}
			logger.info(String.format("%s is clearing.", "entrySet"));
			entrySet = new ConcurrentHashMap<String, String>();
			logger.info("Clean Context space is over.");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	NameParser getParser() {
		return this.parser;
	}

	public List<String> inputKeys() {
		// TODO Auto-generated method stub
		List<String> inputs = new ArrayList<String>();
		if (entrySet.isEmpty()) {
			logger.info("entrySet is empty. Return empty.");
			return inputs;
		}
		for (Map.Entry<String, String> entry : entrySet.entrySet()) {
			inputs.add(entry.getKey());
		}
		return inputs;
	}

	KeyStoreHandler keyStoreHandlerFactory() throws RemoteException {
		
		KeyStoreHandler handler = new KeyStoreHandler();
		return handler;
	}
	public byte[] lookup(Name name) throws RemoteException {
		// TODO Auto-generated method stub
		Object obj = null;
		byte[] codes = null;
		String input = name.toString();
		if (entrySet.containsKey(input)) {
			String rkey = entrySet.get(input);
			try {
				obj = this.context.lookup(rkey);
				codes = PlatformUtil.objectMarshal(obj);
			} catch (NamingException | IOException e) {
				// TODO Auto-generated catch block
				throw new RemoteException(e.getMessage(), e);
			}
			return codes;
		} else {
			throw new RemoteException("name is not found.");
		}

	}
	public byte[] lookup(String name) throws RemoteException {
		// TODO Auto-generated method stub
		byte[] codes = null;
		try {
			codes = lookup(this.parser.parse(name));
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			throw new RemoteException(e.getMessage(), e);
		}
		return codes;
	}

	public List<String> realKeys() {
		// TODO Auto-generated method stub
		List<String> inputs = new ArrayList<String>();
		if (entrySet.isEmpty()) {
			logger.info("entrySet is empty. Return empty.");
			return inputs;
		}
		for (Map.Entry<String, String> entry : entrySet.entrySet()) {
			inputs.add(entry.getValue());
		}
		return inputs;
	}
	public void unbind(Name name) throws RemoteException {
		lock.lock();
		try {

			String cn = name.toString();
			if (this.entrySet.containsKey(cn)) {
				try {
					this.entrySet.remove(name.toString());
					this.context.unbind(name);
				} catch (NamingException e) {
					// TODO Auto-generated catch block
					throw new RemoteException(e.getMessage(), e);
				}

			}
			throw new RemoteException(String.format("name %s is not found.",
					name.toString()));
		} finally {
			lock.unlock();
		}

	}

	public void unbind(String name) throws RemoteException {
		// TODO Auto-generated method stub
		try {
			unbind(parser.parse(name));
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			throw new RemoteException(e.getMessage(), e);
		}

	}
	
	RSAPrivateKey getPrivatKey(){
		return prvkey;
	}

}
