package com.websystem.service.spi;

import java.rmi.RemoteException;
import java.security.cert.X509Certificate;

import com.websystem.domain.WebsystemProcessStateEnum;


public interface SuperEntryService extends SuperService {
	public abstract byte[] accessCertificate(
			X509Certificate paramX509Certificate) throws RemoteException;

	public abstract WebsystemProcessStateEnum tryAccess()
			throws RemoteException;
	

}
