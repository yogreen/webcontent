package com.websystem.workspace.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public final class ProcessExecutor {

	private final Class<?> executeableClazz;

	private Process process = null;
	private AtomicBoolean a_isprocess = new AtomicBoolean();
	private Logger logger = Logger.getLogger(ProcessExecutor.class.getSimpleName());

	private ProcessExecutor(final Class<?> executeableClazz) {
		if (executeableClazz == null)
			throw new IllegalArgumentException("executeableClazz == null");

		try {
			executeableClazz.getMethod("main", String[].class);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new IllegalArgumentException(
					"executeableClazz has no accessible main method", e);
		}

		this.executeableClazz = executeableClazz;

		a_isprocess.set(false);
	}
	private ProcessExecutor(final Class<?> executeableClazz, builder instance) {
		this(executeableClazz);
	}

	public static class builder {
		private static ProcessExecutor pe;
		private static builder instance;
		private builder() {
		}
		public static ProcessExecutor newInstance(
				final Class<?> executeableClazz) {
			instance = onece();
			if (pe == null) {
				pe = new ProcessExecutor(executeableClazz, instance);
			}
			return pe;
		}
		private static builder onece() {
			if (instance == null) {
				instance = new builder();
			}
			return instance;
		}
	}

	public Process execute(String[] jvmOpts,
			String... params){
		String javahome = System.getProperty("java.home");
		return this.execute(javahome,jvmOpts, params);
	}
	public Process execute(String javahome, String[] jvmOpts,
			String... params) {

		final String javaRuntime = javahome + "/bin/java";
		logger.info("javahome: "+javahome);
		final String classpath = System.getProperty("java.class.path")
				+ File.pathSeparator
				+ executeableClazz.getProtectionDomain().getCodeSource()
						.getLocation().getPath();
		final String canonicalName = executeableClazz.getCanonicalName();

		try {
			final List<String> commandList = new ArrayList<String>();
			commandList.add(javaRuntime);
			commandList.add("-cp");
			commandList.add(classpath);
			if (jvmOpts != null) {

				commandList.addAll(Arrays.asList(jvmOpts));
			}
			commandList.add(canonicalName);
			if (params != null) {

				commandList.addAll(Arrays.asList(params));
			}

			final ProcessBuilder builder = new ProcessBuilder(commandList);
			builder.redirectErrorStream(true);
			ProcessBuilder.Redirect redir = builder.redirectOutput();
			builder.redirectOutput(redir);
			process = builder.start();
			if (process != null) {
				a_isprocess.set(true);
			}
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		return process;
	}

	public void destroy() {
		if (!a_isprocess.get())
			throw new IllegalStateException("process == null");
		a_isprocess.set(false);
		process.destroyForcibly();
	}
}