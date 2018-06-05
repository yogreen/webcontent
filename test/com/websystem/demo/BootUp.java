package com.websystem.demo;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.entry.boot.BootLauncher;
import com.websystem.task.WebsystemThreadPoolParameter;
import com.websystem.workspace.util.WebsystemThreadFactoryInstance;

public class BootUp {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		/**
		 * Manifest-Version: 1.0
           Created-By: Apache Ant 1.5.1
           Extension-Name: Struts Framework
           Specification-Title: Struts Framework
           Specification-Vendor: Apache Software Foundation
           Specification-Version: 1.1
           Implementation-Title: Struts Framework
           Implementation-Vendor: Apache Software Foundation
           Implementation-Vendor-Id: org.apache
           Implementation-Version: 1.1
           Class-Path: commons-beanutils.jar commons-collections.jar commons-digester.jar commons-logging.jar commons-validator.jar 
                       jakarta-oro.jar struts-legacy.jar
		 */
		
		LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
		RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();
		WebsystemThreadFactoryInstance factory = new WebsystemThreadFactoryInstance(
				"boot", Thread.NORM_PRIORITY, false);
		int n = Runtime.getRuntime().availableProcessors();
		WebsystemThreadPoolParameter parame = new WebsystemThreadPoolParameter(
				1, n + 1, 3, TimeUnit.SECONDS, queue, factory, handler);
		BootLauncher.launcher(parame, "serviceMessage", false, true);
	}

}
