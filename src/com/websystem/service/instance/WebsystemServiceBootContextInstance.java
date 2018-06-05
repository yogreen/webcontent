package com.websystem.service.instance;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.RMISocketFactory;
import java.util.logging.Logger;

import com.websystem.service.spi.SuperRegisterService;
import com.websystem.service.spi.SuperService;
import com.websystem.service.spi.WebsystemWorkspaceConstance;
import com.websystem.service.spi.WebsystemBootService;
import com.websystem.workspace.util.WebsystemServiceSocketFactory;

public class WebsystemServiceBootContextInstance
		implements
			WebsystemBootService {

	private boolean beRunning = false;
	private boolean initalUsage = false;
	private Logger logger = Logger
			.getLogger(WebsystemServiceBootContextInstance.class
					.getSimpleName());

	public WebsystemServiceBootContextInstance() {
		super();
		// TODO Auto-generated constructor stub

		try {
			RMISocketFactory
					.setSocketFactory(new WebsystemServiceSocketFactory());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
	}

	@Override
	public void restartAction(String host, int port, String servicename,
			SuperService servicebean) throws Exception {
		// TODO Auto-generated method stub
		if (initalUsage && beRunning) {

			String scheme = WebsystemWorkspaceConstance.WEBSYS_WORKSPACE_SERVICE_SCHEME;
			String uri = scheme + host + ":" + port + "/" + servicename;
			try {
				new URI(uri);
			} catch (URISyntaxException e) {
				e.printStackTrace();
				return;
			}
			SuperService obj = null;
			try {

				obj = (SuperService) Naming.lookup(uri);
			} catch (Exception e) {

			}
			if (obj == null) {
				Naming.bind(uri, servicebean);
				return;
			}
			Naming.unbind(uri);
			Naming.bind(uri, servicebean);
			logger.info(String.format("*****%s service rebinding.*****",
					servicename));

		}

	}

	@Override
	public void startAction(String host, int port, String servicename,
			SuperService servicebean) throws RemoteException,
			MalformedURLException, AlreadyBoundException {
		// TODO Auto-generated method stub
		String scheme = WebsystemWorkspaceConstance.WEBSYS_WORKSPACE_SERVICE_SCHEME;
		String uri = scheme + host + ":" + port + "/" + servicename;
		SuperService nservicebean = null;
		try {

			nservicebean = (SuperService) Naming.lookup(uri);
		} catch (Exception e) {

		}
		if (nservicebean == null) {
			initalUsage = false;
			beRunning = false;
		}
		
		if (!initalUsage) {

			try {
				new URI(uri);
			} catch (URISyntaxException e) {
				throw new RemoteException(e.getMessage(),e);
			}
			LocateRegistry.createRegistry(port);
			Naming.bind(uri, servicebean);
			logger.info(String.format("*****%s service binding.*****",
					servicename));
		}
		initalUsage = true;
		beRunning = true;
	}

	@Override
	public void stopAction(String host, int port, String servicename)
			throws Exception {
		// TODO Auto-generated method stub
		if (beRunning) {

			String scheme = WebsystemWorkspaceConstance.WEBSYS_WORKSPACE_SERVICE_SCHEME;
			String uri = scheme + host + ":" + port + "/" + servicename;
			try {
				new URI(uri);
			} catch (URISyntaxException e) {
				throw new RemoteException(e.getMessage(),e);
			}
			SuperService obj = null;
			try {

				obj = (SuperService) Naming.lookup(uri);
			} catch (Exception e) {

			}
			if (obj == null) {
				return;
			}
			Naming.unbind(uri);
			logger.info(String
					.format("*****%s service stop.*****", servicename));
		}
		initalUsage = false;
		beRunning = false;
	}

	@Override
	public void startRegister(String host, int port, String servicename,
			SuperRegisterService servicebean) throws Exception {
		String scheme = WebsystemWorkspaceConstance.WEBSYS_WORKSPACE_SERVICE_SCHEME;
		String uri = scheme + host + ":" + port + "/" + servicename;
		try {
			new URI(uri);
		} catch (URISyntaxException e) {
			throw new RemoteException(e.getMessage(),e);
		}
		SuperRegisterService nservicebean = null;
		try {

			nservicebean = (SuperRegisterService) Naming.lookup(uri);
		} catch (Exception e) {

		}
		if(nservicebean!=null){
			throw new RemoteException(String.format("%s already bound %s", servicename,nservicebean.getClass().getName()));
		}
		
		LocateRegistry.createRegistry(port);
		Naming.bind(uri, servicebean);
		logger.info(String.format("*****%s service binding.*****",
				servicename));
		
		
	}
	

}
