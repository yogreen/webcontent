package com.websystem.service.instance;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;

import com.websystem.service.spi.ActionHandlerService;

public abstract class AbstractActionInvocationHandler
		implements
			InvocationHandler {

	protected ActionHandlerService service;
	private final AccessControlContext acc = AccessController.getContext();

	public AbstractActionInvocationHandler(ActionHandlerService service) {
		super();
		this.service = service;
	}
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		// TODO Auto-generated method stub
		 AccessControlContext acc = this.acc;
	        if ((acc == null) && (System.getSecurityManager() != null)) {
	            throw new SecurityException("AccessControlContext is not set");
	        }
	        return AccessController.doPrivileged(new PrivilegedAction<Object>() {
	            public Object run() {
	                return join(proxy, method, args);
	            }
	        }, acc);
	}
	protected abstract Object join(Object proxy, Method method, Object[] args);
	

}
