package com.websystem.task;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class WebsystemScheduleService extends ScheduledThreadPoolExecutor {
	
	private Logger logger = Logger.getLogger(this.getClass().getSimpleName());

	public WebsystemScheduleService(int corePoolSize, ThreadFactory threadFactory,
			RejectedExecutionHandler handler) {
		super(corePoolSize, threadFactory, handler);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void beforeExecute(Thread t, Runnable r) {
		// TODO Auto-generated method stub
		logger.info(String.format("Thread: %s , Task: %s is up.", t.getName(),r.getClass().getSimpleName()));
		super.beforeExecute(t, r);
	}

	@Override
	protected void afterExecute(Runnable r, Throwable t) {
		// TODO Auto-generated method stub
		logger.info(String.format("%s is completed", r.getClass().getSimpleName()));
		super.afterExecute(r, t);
	}

	@Override
	protected void terminated() {
		// TODO Auto-generated method stub
		logger.info(String.format("%s is terminated", this.getClass().getSimpleName()));
		super.terminated();
	}
	public void shutdownRequest(long awaitTime,TimeUnit unit){
		logger.info(String.format("shutdown request come, Timeunit: %s, waitTime: %d.",unit,awaitTime));
		try {
			super.shutdown();
			super.awaitTermination(awaitTime, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			
		}
		logger.info(String.format("%s WebsystemScheduleService shutdown completed.", this.getClass().getSimpleName()));
	}
	public void shutdownRequest(){
		this.shutdownRequest(5, TimeUnit.MILLISECONDS);
	}
	

}
