package com.websystem.workspace.util;

import java.lang.Thread.UncaughtExceptionHandler;

public class ThreadRuntimeExceptionHandler implements UncaughtExceptionHandler {

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		// TODO Auto-generated method stub
		e.printStackTrace();
		String msg = String.format("Thread: %s catch Exception: %s", t.getName(),e.getClass().getName()); 
		RuntimeException runt = new RuntimeException(msg);
		runt.printStackTrace();

	}

}
