package com.websystem.service.spi;

import java.rmi.RemoteException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public interface AbtractActionService extends SuperService {

	void clean() throws RemoteException;
	void cleanAfter(long time, TimeUnit unit) throws RemoteException;

	byte[] delegateCompute(String classname,String methodName,Map<String,byte[]> constructorParames,Map<String,byte[]> methodParames)
			throws RemoteException;

}
