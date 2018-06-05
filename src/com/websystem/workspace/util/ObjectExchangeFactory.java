package com.websystem.workspace.util;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public final class ObjectExchangeFactory {

	@SuppressWarnings("unchecked")
	static <T> T exchange(Class<T> componentType) {
		if(componentType==null){
			return null;
		}
		T entity = null;
		String name = componentType.getName();
		switch (name) {
			case "byte" :
				Byte b = Byte.valueOf((byte) 0);
				entity = (T) b;

				break;
			case "short" :
				Short s = Short.valueOf((short)0);
				entity = (T) s;

				break;
			case "int" :
				Integer i = Integer.valueOf(0);
				entity = (T) i;

				break;
			case "char" :
				char ch = Character.MIN_CODE_POINT;
				Character c = Character.valueOf(ch);
				entity = (T) c;

				break;
			case "float" :
				Float f = Float.valueOf(0f);
				entity = (T) f;
				
				break;
			case "dobule" :
				Double d = Double.valueOf(0.0);
				entity = (T) d;

				break;
			case "long" :
				Long l = Long.valueOf(0);
				entity = (T) l;

				break;
			case "boolean" :
				Boolean bool = Boolean.valueOf(false);
				entity = (T) bool;

				break;

			default :
				break;
		}
		return entity;
	}

	public static <T> T newInstance(Class<T> componentType,
			Object[] constructorParames) throws NoSuchMethodException,
			SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		T entity = exchange(componentType);
		if (entity != null) {
			return entity;
		}

		Constructor<T> contr = null;
		if (constructorParames == null || constructorParames.length == 0) {
			contr = componentType.getConstructor();
			entity = contr.newInstance();
			return entity;
		} else {
			int n = constructorParames.length;
			Class<?>[] clazzses = new Class<?>[n];
			for (int i = 0; i < n; i++) {
				Object obj = constructorParames[i];
				Class<?> clazz = obj.getClass();
				clazzses[i] = clazz;
			}
			contr = componentType.getConstructor(clazzses);
			entity = contr.newInstance(constructorParames);
			return entity;
		}
	}

	public static <T> T newInstance(String name, Object[] constructorParames)
			throws NoSuchMethodException, SecurityException,
			InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			ClassNotFoundException {
		T entity = null;
		entity = newInstance(name, constructorParames, Thread.currentThread()
				.getContextClassLoader());
		return entity;
	}

	@SuppressWarnings("unchecked")
	public static <T> T newInstance(String name, Object[] constructorParames,
			ClassLoader loader) throws NoSuchMethodException,
			SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			ClassNotFoundException {
		T entity = null;
		Class<T> clazz = (Class<T>) loader.loadClass(name);
		if (clazz != null) {
			return newInstance(clazz, constructorParames);
		}
		return entity;
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] objectArray(Class<T> componentType, int length)
			throws ClassNotFoundException, NoSuchMethodException,
			SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		T entity =  exchange(componentType);
		
		if (entity != null) {
			Class<T> clazz = (Class<T>) Thread.currentThread()
					.getContextClassLoader()
					.loadClass(entity.getClass().getName());
			return (T[]) Array.newInstance(clazz, length);
		}
		return (T[]) Array.newInstance(componentType, length);
	}

	private ObjectExchangeFactory() {
	}
}
