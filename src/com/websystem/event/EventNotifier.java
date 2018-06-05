package com.websystem.event;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EventNotifier<T> {

	private Map<Integer, EventAction<T>> eventMap;
	private List<EventSource<T>> sources;
	public EventNotifier() {
		eventMap = new LinkedHashMap<Integer, EventAction<T>>();
		sources = new ArrayList<EventSource<T>>();
	}

	public void notifier(EventAction<T> action) {
		if(action==null||!eventMap.containsValue(action)){
			throw new IllegalStateException(String.format("action: %s is not registed.", action));
		}
		EventSource<T> source = querySource(action);
		action.action(source);
	}
	public void notifier(EventSource<T> source) {
		if(source==null||!sources.contains(sources)){
			throw new IllegalStateException(String.format("action: %s is not registed.", source));
		}
		EventAction<T> action = queryAction(source);
		action.action(source);
	}
	public EventAction<T> queryAction(EventSource<T> source) {
		EventAction<T> action = null;
		if (sources.isEmpty() || !sources.contains(source)) {
			return null;
		}
		int index = sources.indexOf(source);
		action = eventMap.get(Integer.valueOf(index));
		return action;
	}

	protected EventSource<T> querySource(EventAction<T> action) {
		EventSource<T> source = null;
		if (eventMap.isEmpty() || !eventMap.containsValue(action)) {
			return null;
		}
		int index = -109;
		for (Map.Entry<Integer, EventAction<T>> en : eventMap.entrySet()) {
			Integer key = en.getKey();
			EventAction<T> value = en.getValue();
			if (value.equals(action)) {
				index = key;
			}
		}
		if (index < 0) {
			return null;
		}
		source = sources.get(index);
		return source;
	}
	protected EventSource<T> querySource(String id) {
		EventSource<T> source = null;
		if (sources.isEmpty()) {
			return null;
		}
		for (EventSource<T> tmp : sources) {
			if (tmp.getId().equals(id)) {
				source = tmp;
				break;
			}
		}
		return source;
	}
	public void register(EventSource<T> source, EventAction<T> action) {
		if (source == null || action == null) {
			throw new IllegalStateException("non-null parameters.");
		}
		sources.add(source);
		int index = sources.indexOf(source);
		eventMap.put(Integer.valueOf(index), action);
	}
	public void remove(EventSource<T> source) {
		int index = -109;
		if (sources.contains(source)) {
			index = sources.indexOf(source);

		} else {
			return;
		}
		if (index >= 0) {
			eventMap.remove(Integer.valueOf(index));
			sources.remove(index);
		}
	}
	public void removeAll() {
		eventMap = new LinkedHashMap<Integer, EventAction<T>>();
		sources = new ArrayList<EventSource<T>>();
	}

}
