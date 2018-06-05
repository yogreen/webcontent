package com.websystem.service.instance;

import java.rmi.RemoteException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

class RegisterServiceHandler {

	private X509Certificate current_cert = null;
	private ConcurrentHashMap<String, String> container;
	private KeyStoreHandler keyhandler = null;
	RegisterServiceHandler() {
		container = new ConcurrentHashMap<String, String>();
		keyhandler = new KeyStoreHandler();
		current_cert = keyhandler.loadPair().getFirst();
	}
	void register(String key, String value) throws RemoteException {
		// TODO Auto-generated method stub
		X509Certificate cert = current_cert;
		if (cert == null || key == null || "".equals(key.trim())) {
			throw new RemoteException(
					"X509Certificate or key must not be null or empty.");
		}
		long seria = cert.getSerialNumber().longValue();
		String ckey = key + "@" + seria;
		container.put(ckey, value);

	}

	String lookup(String key) throws RemoteException {
		// TODO Auto-generated method stub
		X509Certificate cert = current_cert;
		if (cert == null || key == null || "".equals(key.trim())) {
			throw new RemoteException(
					"X509Certificate or key must not be null or empty.");
		}
		long seria = cert.getSerialNumber().longValue();
		boolean flag = this.container.containsKey(key+"@"+seria);
		if (!flag) {
			throw new RemoteException(
					String.format("Key: %s is not found", key));
		}

		return this.container.get(key);
	}

	List<String> keyList() throws RemoteException {
		// TODO Auto-generated method stub
		Set<String> keyset = this.container.keySet();
		String[] keys = new String[keyset.size()];
		keyset.toArray(keys);
		for(int i=0;i<keys.length;i++){
			keys[i] =keys[i].split("@")[0]; 
		}
		return Arrays.asList(keys);
	}

	List<String> values() throws RemoteException {
		// TODO Auto-generated method stub
		Collection<String> vs = this.container.values();
		String[] keys = new String[vs.size()];
		vs.toArray(keys);
		return Arrays.asList(keys);
	}

	void remove(String value) throws RemoteException {
		X509Certificate cert = current_cert;
		if (cert == null || value == null || "".equals(value.trim())) {
			throw new RemoteException(
					"X509Certificate or key must not be null or empty.");
		}
		long seria = cert.getSerialNumber().longValue();
		if (this.container.isEmpty()) {
			return;
		}
		if (!this.container.containsKey(value+"@"+seria)) {
			return;
		}
		this.container.remove(value+"@"+seria);

	}
	void removeAll() throws RemoteException {
		// TODO Auto-generated method stub
		X509Certificate cert = current_cert;
		if (cert == null) {
			throw new RemoteException(
					"X509Certificate or key must not be null or empty.");
		}
		if (!this.container.isEmpty())
			this.container = new ConcurrentHashMap<String, String>();
	}
	
	void update(String key, String value) throws RemoteException {
		// TODO Auto-generated method stub
		X509Certificate cert = current_cert;
		if (cert == null || value == null || "".equals(value.trim())) {
			throw new RemoteException(
					"X509Certificate or key must not be null or empty.");
		}
		long seria = cert.getSerialNumber().longValue();
		if(this.container.containsKey(key+"@"+seria)){
			this.container.remove(key);
			this.container.put(key, value);
		}
		
	}

}
