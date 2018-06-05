package com.websystem.service;

import java.rmi.RemoteException;
import java.security.cert.X509Certificate;

import com.websystem.domain.LoaderEnum;
import com.websystem.service.spi.SuperService;
import com.websystem.util.Pair;

public interface LocalService extends SuperService {
	LoaderEnum access(X509Certificate cert) throws RemoteException;
	Pair<String[],String[]> load(X509Certificate cert)throws RemoteException;

}
