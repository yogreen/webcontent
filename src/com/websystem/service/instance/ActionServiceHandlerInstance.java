package com.websystem.service.instance;

import java.rmi.RemoteException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import javax.naming.Name;
import javax.naming.NamingException;

import com.websystem.domain.WebsystemProcessStateEnum;
import com.websystem.service.spi.ActionHandlerService;

public class ActionServiceHandlerInstance implements ActionHandlerService {

	private WebsystemNamingServiceHandler nhandler;
	ArrayBlockingQueue<X509Certificate> queue_cert;
	LinkedBlockingDeque<X509Certificate> queue_certs;

	public ActionServiceHandlerInstance(WebsystemNamingServiceHandler nhandler) {
		super();
		this.nhandler = nhandler;
		queue_cert = new ArrayBlockingQueue<X509Certificate>(1);
		queue_certs = new LinkedBlockingDeque<X509Certificate>();
	}

	@Override
	public void bind(String name, byte[] contents) throws RemoteException {
		// TODO Auto-generated method stub
		try {
			this.nhandler.bindContents(name, contents);
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			throw new RemoteException(e.getMessage(),e);
		}

	}

	@Override
	public void clean() throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public void cleanAfter(long time, TimeUnit unit) throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public List<String> inputKeys() throws RemoteException {
		// TODO Auto-generated method stub
		return this.nhandler.inputKeys();
	}

	@Override
	public byte[] lookup(Name name) throws RemoteException {
		// TODO Auto-generated method stub
		return this.nhandler.lookup(name);
	}

	@Override
	public byte[] lookup(String name) throws RemoteException {
		// TODO Auto-generated method stub
		return this.nhandler.lookup(name);
	}

	@Override
	public List<String> realKeys() throws RemoteException {
		// TODO Auto-generated method stub
		return this.nhandler.realKeys();
	}

	@Override
	public WebsystemProcessStateEnum tryAccess() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void unbind(String name) throws RemoteException {
		// TODO Auto-generated method stub
		this.nhandler.unbind(name);

	}
	
	void offer_cert(X509Certificate cert){
		queue_cert.offer(cert);
		queue_certs.offer(cert);
	}

}
