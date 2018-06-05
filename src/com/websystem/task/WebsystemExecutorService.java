package com.websystem.task;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class WebsystemExecutorService extends ThreadPoolExecutor {

	private Logger logger = Logger.getLogger(this.getClass().getSimpleName());
	public WebsystemExecutorService(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
		this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
				threadFactory, new ThreadPoolExecutor.AbortPolicy());
		// TODO Auto-generated constructor stub
	}
	public WebsystemExecutorService(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory,
			RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
				threadFactory, handler);
		// TODO Auto-generated constructor stub
	}
	public WebsystemExecutorService(long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
		this(1, Runtime.getRuntime().availableProcessors() + 1, keepAliveTime,
				unit, workQueue, threadFactory);
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
		logger.info(String.format("%s shutdown completed.",this.getClass().getSimpleName()));
	}
	public void shutdownRequest(){
		this.shutdownRequest(30,TimeUnit.MILLISECONDS);
	}
	

}
