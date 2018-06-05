package com.websystem.service.boot;

import com.websystem.service.spi.WebsystemBootService;

public class WebsystemBootAction {
	private static WebsystemBootAction action = null;
	protected WebsystemBootAction (WebsystemBootService service){
		
	}
	public static WebsystemBootAction join(WebsystemBootService service){
		
		return join(service,null);
	}
	public static WebsystemBootAction join(WebsystemBootService service,String subject){
		if(action==null){
			action = new WebsystemBootAction(service);
		}
		return action;
	}

}
