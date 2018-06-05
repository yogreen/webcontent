package com.websystem.service.boot;

import com.websystem.service.spi.SuperRegisterService;
import com.websystem.service.spi.SuperService;

public class WebsystemBootInstance extends AbstractBootServiceContext {

	public WebsystemBootInstance() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void restartAction(String host, int port, String servicename,
			SuperService servicebean) throws Exception {
		// TODO Auto-generated method stub
		super.bootContext.restartAction(host, port, servicename, servicebean);
	}

	@Override
	public void startAction(String host, int port, String servicename,
			SuperService servicebean) throws Exception {
		// TODO Auto-generated method stub
		super.bootContext.startAction(host, port, servicename, servicebean);

	}

	@Override
	public void stopAction(String host, int port, String servicename) {
		// TODO Auto-generated method stub
		try {
			super.bootContext.stopAction(host, port, servicename);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new IllegalStateException(e.getMessage(), e);
		}

	}

	@Override
	public void startRegister(String host, int port, String servicename,
			SuperRegisterService servicebean) throws Exception {
		// TODO Auto-generated method stub
		super.bootContext.startRegister(host, port, servicename, servicebean);
	}

}
