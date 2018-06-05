package com.websystem.task;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.websystem.service.spi.Computable;


public class WebsystemSimpleComputeSchedule<I,R> {
	
	private I input;
	private Computable<I, R> comp;
	private ScheduledExecutorService service = null;
	private Logger logger = Logger.getLogger(this.getClass().getSimpleName());
	public WebsystemSimpleComputeSchedule(Computable<I, R> comp,I input,ThreadFactory threadFactory){
		this.comp = comp;
		this.input = input;
		ThreadFactory factory = threadFactory;
		int corePoolSize = Runtime.getRuntime().availableProcessors();
		service = Executors.newScheduledThreadPool(corePoolSize+1, factory);
	}
	
	public R delayScheduleCallback(long delay,TimeUnit unit) throws InterruptedException, ExecutionException{
		logger.info("compute begin.");
		R result= null;
		result =this.comp.compute(input);
		ScheduledFuture<R> future = service.schedule(new Callable<R>(){

			@Override
			public R call() throws Exception {
				// TODO Auto-generated method stub
				R result= comp.compute(input);
				return result;
			}
			
		}, delay, unit);
		result = future.get();
		future.cancel(true);
		requestShutdown();
		logger.info(String.format("%s compute completed. result: %s", input.getClass().getName(),result.getClass().getName()));
		return result;
	}
	public void delaySchedule(long delay,TimeUnit unit) throws InterruptedException, ExecutionException{
		logger.info("Task beigin now.");
		service.scheduleAtFixedRate(new Runnable(){
			
			@Override
			public void run(){
				// TODO Auto-generated method stub
				try {
					comp.compute(input);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					Thread.currentThread().interrupt();
				}
			}
			
		}, delay, delay==0?5:delay/2, unit);
		service.shutdown();
		service.awaitTermination(delay, unit);
		logger.info("Task completed.");
		
	}
	public void peroidySchedule(long initdelay,long delay,TimeUnit unit) throws InterruptedException, ExecutionException{
		service.scheduleWithFixedDelay(new Runnable(){
			
			@Override
			public void run(){
				// TODO Auto-generated method stub
				try {
					comp.compute(input);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					Thread.currentThread().interrupt();
				}
			}
			
		}, initdelay,delay, unit);
		
	}
	public void requestShutdown(long waitime,TimeUnit unit) throws InterruptedException{
		logger.info("shut down request begin.");
		service.shutdown();
		service.awaitTermination(waitime, unit);
		logger.info("shut down completed.");
	}
	public void requestShutdown() throws InterruptedException{
		requestShutdown(20,TimeUnit.MILLISECONDS);
	}

}
