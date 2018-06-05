package com.websystem.demo;

import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;



import com.websystem.service.spi.SuperNamingService;

public class AcceptMembershipMain {

	public static void main(String[] args) throws NotBoundException, ClassNotFoundException, IOException, Exception{
		// TODO Auto-generated method stub
		/*SuperNamingService service2 = (SuperNamingService) Naming.lookup("rmi://192.168.1.105:3357/cctvservice");
		System.out.println(service2.tryAccess());
		System.out.println(service2.accessCertificate(null));
		System.out.println(service2.tryAccess());
		System.out.println(service2.inputKeys());*/
		
		String uri = "rmi://192.168.5.1:2156";
		
		

	}

}
