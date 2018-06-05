package com.websystem.event;

public interface EventListener<T> {
	
	void performedAction(EventAction<T> action);

}
