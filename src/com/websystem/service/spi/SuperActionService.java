package com.websystem.service.spi;

import java.rmi.RemoteException;

import com.websystem.domain.WebsystemWareHouse;

public interface SuperActionService extends SuperEntryService,AbtractActionService{
	void transfer(WebsystemWareHouse ware) throws RemoteException;

}
