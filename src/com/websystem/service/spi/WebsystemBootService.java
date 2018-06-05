package com.websystem.service.spi;

public interface WebsystemBootService {

	void startAction(String host, int port, String servicename,
			SuperService servicebean) throws Exception;
	void startRegister(String host, int port, String servicename,
			SuperRegisterService servicebean) throws Exception;
	void restartAction(String host, int port, String servicename,
			SuperService servicebean) throws Exception;
	void stopAction(String host, int port, String servicename) throws Exception;

}
