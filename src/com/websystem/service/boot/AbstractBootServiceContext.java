package com.websystem.service.boot;

import com.websystem.service.instance.WebsystemServiceBootContextInstance;
import com.websystem.service.spi.SuperService;
import com.websystem.service.spi.WebsystemBootService;

public abstract class AbstractBootServiceContext
		implements
			WebsystemBootService {

	protected WebsystemServiceBootContextInstance bootContext =null;

	protected AbstractBootServiceContext() {
		super();
		// TODO Auto-generated constructor stub
		bootContext = new WebsystemServiceBootContextInstance();
	}

	@Override
	public abstract void startAction(String host, int port, String servicename,
			SuperService servicebean) throws Exception;

	@Override
	public abstract void restartAction(String host, int port,
			String servicename, SuperService servicebean)
			throws Exception;

	@Override
	public abstract void stopAction(String host, int port, String servicename);

}
