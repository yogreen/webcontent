package com.websystem.accept.instance;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;




import com.websystem.accept.NetPackage;
import com.websystem.accept.monitor.AbstractAcceptObservable;
import com.websystem.accept.monitor.SuperAcceptObserver;
import com.websystem.domain.ActionStateEnum;
import com.websystem.membership.Sender;
import com.websystem.service.instance.RegisterServiceInstance;
import com.websystem.service.spi.SuperRegisterService;

public class NetPackageActionMonitor implements SuperAcceptObserver {

	private String ruri;

	public NetPackageActionMonitor(String registeruri) {
		super();
		this.ruri = registeruri;

	}

	@Override
	public <S> void update(AbstractAcceptObservable able, S source) {
		// TODO Auto-generated method stub
		Sender sender;
		if (source instanceof NetPackage) {
			NetPackage pack = (NetPackage) source;
			try {
				sender = Sender.newInstance(pack.getSubject(), false);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				throw new RuntimeException(e.getMessage(),e);
			}
			Map<String,String> members = pack.getMembers();
			SuperRegisterService rservice = null;
			try {
				rservice = pair();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				throw new RuntimeException(e.getMessage(),e);
			}
			
			List<String> uris = new ArrayList<String>();
			List<String> keys = new ArrayList<String>();
			try {
				uris = rservice.values();
				keys = rservice.keyList();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				throw new RuntimeException(e.getMessage(),e);
			}
			if(uris!=null&&!uris.isEmpty()){
				for(String uri : uris){
					int n = uris.indexOf(uri);
					String key = keys.get(n);
					Remote remote = null;
					try {
						remote = Naming.lookup(uri);
					} catch (MalformedURLException | RemoteException
							| NotBoundException e) {
						// TODO Auto-generated catch block
					}
					if(remote==null){
						try {
							rservice.remove(key);
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							throw new RuntimeException(e.getMessage(),e);
						}
					}else{
						if(remote instanceof RegisterServiceInstance){
							RegisterServiceInstance service = (RegisterServiceInstance) remote;
							
							for(Map.Entry<String, String> en:members.entrySet()){
								String mkey = en.getKey();
								String mvalue = en.getValue();
								try {
									rservice.register(mkey, mvalue);
									service.register(mkey, mvalue);
								} catch (RemoteException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
							}
						}
						
					}
				}
			}else{
				for(Map.Entry<String, String> en:members.entrySet()){
					String mkey = en.getKey();
					String mvalue = en.getValue();
					try {
						rservice.register(mkey, mvalue);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
			try {
				uris = rservice.values();
				keys = rservice.keyList();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				throw new RuntimeException(e.getMessage(),e);
			}
			members = new LinkedHashMap<String, String>();
			for(int i=0;i<keys.size();i++){
				members.put(keys.get(i), uris.get(i));
			}
			NetPackage callback = new NetPackage();
			callback.setSubject(pack.getSubject());
			callback.setId(pack.getId());
			callback.setActionState(ActionStateEnum.UPDATE_DONE);
			callback.setMembers(members);
			callback.setRegisterServiceURI(pack.getRegisterServiceURI());
			try {
				callback.setHeaderSource(callback.packageHeader());
			} catch (SocketException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			try {
				callback.setHeaderSource(pack.packageHeader());
			} catch (SocketException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				sender.send(callback);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
			

	}
	
	SuperRegisterService pair() throws RemoteException{
		SuperRegisterService rservice = null;
		try {
			rservice = (SuperRegisterService) Naming.lookup(ruri);
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			throw new RemoteException(e.getMessage(),e);
		}
		return rservice;
	}

}
