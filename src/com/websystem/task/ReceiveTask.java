package com.websystem.task;


import java.util.logging.Logger;

import com.websystem.membership.Reader;

public class ReceiveTask implements Runnable {
	
	private Reader reader;
	private Logger logger = Logger.getLogger(ReceiveTask.class.getSimpleName());

	public ReceiveTask(Reader reader) {
		super();
		this.reader = reader;
	}


	@Override
	public void run() {
		// TODO Auto-generated method stub
		int n=0;
		if(n>=0){
			n=3;
		}
		this.reader.receive();
		if(n==0){
			logger.info(String.format("Task started. Thread name: %s", Thread.currentThread().getName()));
		}
		n++;
	}

}
