package com.websystem.service.instance;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.websystem.service.spi.SuperRegisterService;

public class RegisterServiceInstance extends UnicastRemoteObject
		implements
			SuperRegisterService {

	/**
	 * version
	 */
	private static final long serialVersionUID = -6348841391736914182L;
	private AtomicReference<RegisterServiceHandler> a_handler;
	private Lock lock;

	public RegisterServiceInstance() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
		RegisterServiceHandler handler = new RegisterServiceHandler();
		a_handler = new AtomicReference<RegisterServiceHandler>();
		a_handler.set(handler);
		lock = new ReentrantLock();
	}

	@Override
	public void register(String key, String value) throws RemoteException {
		// TODO Auto-generated method stub
		lock.lock();
		try{
			RegisterServiceHandler handler = a_handler.get();
			handler.register(key, value);
		}finally{
			lock.unlock();
		}
	}

	@Override
	public String lookup(String key) throws RemoteException {
		// TODO Auto-generated method stub
		RegisterServiceHandler handler = a_handler.get();
		return handler.lookup(key);
	}

	@Override
	public List<String> keyList() throws RemoteException {
		// TODO Auto-generated method stub
		RegisterServiceHandler handler = a_handler.get();
		return handler.keyList();
	}

	@Override
	public List<String> values() throws RemoteException {
		// TODO Auto-generated method stub
		RegisterServiceHandler handler = a_handler.get();
		return handler.values();
	}

	@Override
	public void remove(String value) throws RemoteException {
		// TODO Auto-generated method stub
		lock.lock();
		try{
			
			RegisterServiceHandler handler = a_handler.get();
			handler.remove(value);
			
		}finally{
			lock.unlock();
		}
	}

	@Override
	public void removeAll() throws RemoteException {
		// TODO Auto-generated method stub
		lock.lock();
		try{
			
			RegisterServiceHandler handler = a_handler.get();
			handler.removeAll();
			
		}finally{
			lock.unlock();
		}
	}

	@Override
	public void update(String key, String value) throws RemoteException {
		// TODO Auto-generated method stub
		lock.lock();
		try{
			
			RegisterServiceHandler handler = a_handler.get();
			handler.update(key, value);
			
		}finally{
			lock.unlock();
		}
	}

}
