package com.websystem.service.spi;

public interface Computable<I, R> {
	
	R compute(I input) throws InterruptedException;

}
