package com.websystem.task;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import com.websystem.task.WebsystemExecutorService;

public class WebsystemExecutorContext {

	private WebsystemExecutorService service = null;
	public WebsystemExecutorContext(long keepAlive, TimeUnit unit,
			BlockingQueue<Runnable> queue, ThreadFactory factory) {
		service = new WebsystemExecutorService(keepAlive, unit, queue, factory);
	}
	public WebsystemExecutorContext(WebsystemThreadPoolParameter parameter) {
		int core = parameter.getCoreThreadnum();
		int max = parameter.getMaxThreadnum();
		long keep = parameter.getKeepAliveTimeLength();
		TimeUnit unit = parameter.getUnit();
		BlockingQueue<Runnable> queue = parameter.getWorkQueue();
		ThreadFactory factory = parameter.getThreadFactory();
		RejectedExecutionHandler handler = parameter.getHandler();
		this.service = new WebsystemExecutorService(core,max,keep,unit,queue,factory,handler);
	}
	public WebsystemExecutorContext(WebsystemExecutorService service) {
		this.service = service;
	}

	public <R> R completionExecute(Callable<R> task, boolean isShutdown) {
		R result = null;
		Future<R> future = this.service.submit(task);
		try {
			result = future.get();
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			Thread.currentThread().interrupt();
		}
		if (isShutdown) {
			if (result != null) {
				this.service.shutdownNow();
			}
		}
		return result;
	}
	public void executors(Runnable task, boolean isShutDown) {
		if (isShutDown) {
			this.service.shutdownRequest(20, TimeUnit.MILLISECONDS);
		}
		this.service.execute(task);
	}
	public void executors(Runnable task, long shutdownAfter, TimeUnit unit) {
		this.service.shutdownRequest(shutdownAfter, unit);
		this.service.execute(task);
	}

}
