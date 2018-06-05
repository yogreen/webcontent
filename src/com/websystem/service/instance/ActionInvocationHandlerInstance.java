package com.websystem.service.instance;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.websystem.service.spi.ActionHandlerService;

public class ActionInvocationHandlerInstance
		extends
			AbstractActionInvocationHandler {

	private ArrayBlockingQueue<X509Certificate> cert_queue;
	private LinkedBlockingQueue<X509Certificate> certs_queue;
	public ActionInvocationHandlerInstance(ActionHandlerService service) {
		super(service);
		// TODO Auto-generated constructor stub
		cert_queue = new ArrayBlockingQueue<X509Certificate>(1);
		certs_queue = new LinkedBlockingQueue<X509Certificate>();
	}

	@Override
	protected  Object join(Object proxy, Method method, Object[] args) {
		// TODO Auto-generated method stub
		X509Certificate cert = null;
		try {
			cert = cert_queue.take();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cert_queue.offer(cert);
		String methodName = method.getName();
		if (method.getDeclaringClass() == Object.class)  {
            // Handle the Object public methods.
            if (methodName.equals("hashCode"))  {
                return new Integer(System.identityHashCode(proxy));
            } else if (methodName.equals("equals")) {
                return (proxy == args[0] ? Boolean.TRUE : Boolean.FALSE);
            } else if (methodName.equals("toString")) {
                return proxy.getClass().getName() + '@' + Integer.toHexString(proxy.hashCode());
            }
        }
		if(method.getDeclaringClass() == Method.class){
			throw new RuntimeException("can't support this operation.");
		}
		String name = (String) args[0];
		long n = cert.getSerialNumber().longValue();
		name = name+"@"+n;
		if(methodName.equals("bind")){
			byte[] codes = (byte[]) args[1];
			try {
				this.service.bind(name, codes);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				return null;
				
			}
		}else if(methodName.equals("lookup")){
			try {
				return this.service.lookup(name);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				return null;
			}
		}else if(methodName.equals("inputKeys")){
			List<String> inputKeys = new ArrayList<String>();
			List<String> list = new ArrayList<String>();
			try {
				inputKeys = this.service.inputKeys();
				for(String key:inputKeys){
					list.add(key.split("@")[0]);
				}
				return list;
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				return Collections.emptyList();
			}
		}else if(methodName.equals("unbind")){
			try {
				this.service.unbind(name);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				return null;
			}
		}else{
			try {
				return method.invoke(service, args);
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				// TODO Auto-generated catch block
				return null;
			}
		}
		return null;
	}
	
	public void offer(X509Certificate cert){
		cert_queue.offer(cert);
		certs_queue.offer(cert);
	}

}
