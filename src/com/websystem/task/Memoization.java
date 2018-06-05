package com.websystem.task;

import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import com.websystem.service.spi.WebsystemComputable;

class Memoization<A extends Serializable, V> implements WebsystemComputable<A, V> {

	/**
	 * version
	 */
	private static final long serialVersionUID = 3514627307174262918L;
	ConcurrentMap<A, Future<V>> cache = new ConcurrentHashMap<A, Future<V>>();
	private final WebsystemComputable<A, V> cresult;
	private ThreadFactory threadFactory = null;

	public Memoization(WebsystemComputable<A, V> compute) {
		this(compute,null);
	}
	public Memoization(WebsystemComputable<A, V> compute,ThreadFactory threadFactory) {
		
		this.cresult = compute;
		this.threadFactory = threadFactory;
	}

	@Override
	public V compute(A arg) throws InterruptedException {
		// TODO Auto-generated method stub
		while (true) {
			Future<V> f = cache.get(arg);
			if (f == null) {
				Callable<V> eval = new Callable<V>() {

					@Override
					public V call() throws Exception {
						// TODO Auto-generated method stub
						return cresult.compute(arg);
					}

				};
				ExecutorService services = null;
				if(this.threadFactory==null){
					
					services = Executors.newCachedThreadPool();
				}else{
					services = Executors.newCachedThreadPool(this.threadFactory);
				}
				Future<V> fs = services.submit(eval);
				f = cache.putIfAbsent(arg, fs);
				if (f == null) {
					f = fs;
				}
			}
			try {
				return f.get();
			} catch (CancellationException e) {
				// TODO Auto-generated catch block
				cache.remove(arg, f);
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
	}

}
