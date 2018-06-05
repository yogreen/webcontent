package com.websystem.service.spi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface SuperRegisterService extends Remote{
	
	void register(String key,String value) throws RemoteException;
	String lookup(String key) throws RemoteException;
	List<String> keyList() throws RemoteException;
	List<String> values() throws RemoteException;
	void remove(String value) throws RemoteException;
	void update(String key,String value) throws RemoteException;
	void removeAll() throws RemoteException;

}
