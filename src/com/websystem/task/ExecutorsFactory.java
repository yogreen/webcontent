package com.websystem.task;

public class ExecutorsFactory {

	private ExecutorsFactory() {
	}

	public static WebsystemExecutorService executorFactory(
			WebsystemThreadPoolParameter parame) {

		WebsystemExecutorService service = new WebsystemExecutorService(
				parame.getCoreThreadnum(), parame.getMaxThreadnum(),
				parame.getKeepAliveTimeLength(), parame.getUnit(),
				parame.getWorkQueue(), parame.getThreadFactory(),
				parame.getHandler());
		return service;

	}
	public static WebsystemScheduleService scheduleFactory(
			WebsystemThreadPoolParameter parame) {

		WebsystemScheduleService service = new WebsystemScheduleService(
				parame.getCoreThreadnum(), parame.getThreadFactory(),
				parame.getHandler());
		return service;

	}

}
