package com.websystem.service.instance;

import java.lang.reflect.Proxy;

import com.websystem.service.spi.ActionHandlerService;

public class ActionServiceProxy {
	
	public static ActionHandlerService proxyFactory(ActionHandlerService target){
		ActionInvocationHandlerInstance hanlder = new ActionInvocationHandlerInstance(target);
		return (ActionHandlerService) Proxy.newProxyInstance(target.getClass().getClassLoader(), new Class[]{ActionHandlerService.class}, hanlder);
	}

}
