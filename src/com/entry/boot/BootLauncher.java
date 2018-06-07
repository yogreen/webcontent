package com.entry.boot;

import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.management.MalformedObjectNameException;

import com.websystem.accept.NotifyInstance;
import com.websystem.accept.instance.NetPackageActionMonitor;
import com.websystem.accept.monitor.SuperAcceptObserver;
import com.websystem.membership.MembershipAddress;
import com.websystem.task.WebsystemThreadPoolParameter;
import com.websystem.workspace.util.WebsystemThreadFactoryInstance;

public class BootLauncher {

	private static Logger logger = Logger.getLogger(BootLauncher.class
			.getSimpleName());
	private static String[] subjects = null;

	public static void launcher(WebsystemThreadPoolParameter parame,
			String subject, boolean isIPV6support, boolean isOpenListener)
			throws MalformedObjectNameException, ClassNotFoundException {
		BootInitial boot = BootInitial.newInstance(parame, subject,
				isIPV6support);
		boot.bootMemberships();
		if (isOpenListener) {
			String uri = null;
			for (String tmp : boot.uriList()) {
				if (tmp.contains("register")) {
					uri = tmp;
					break;
				}
			}
			boot.bindURI();
			SuperAcceptObserver observer = new NetPackageActionMonitor(uri);
			NotifyInstance ni = boot.notifyInstance();
			ni.register(observer);
			boot.openNotify(observer);

		}
		logger.info("Service boot launcher is completed.");
	}

	static void usage() {
		System.err
				.println("usage: Entry depends message: [-ipv6support yes|no] [-maxpoolsize ] [-subject]");
		MembershipAddress maddr = MembershipAddress.newInstance();
		subjects = maddr.subjects();
	}

	public static void main(String[] args) {
		String[] input = new String[3];
		usage();
		Scanner scan = new Scanner(System.in);
		scan.useDelimiter("\n");
		System.out.print("\n#>entry -ipv6support: ");
		input[0] = scan.next().trim();
		boolean flag = input[0].equalsIgnoreCase("yes")?true:input[0].equalsIgnoreCase("no");
		if(!flag){
			System.out.println(input[0]);
			System.err.println("must input yes|no");
			System.exit(-1);
		}
		System.out.print("#>entry -maxpoolsize: ");
		input[1]=scan.next().trim();
		try{
			Long.parseLong(input[1]);
		}catch(NumberFormatException e){
			logger.info(String.format("Exception: %s, message: %s", e.getClass().getSimpleName(),e.getMessage()));
			System.out.println();
			System.out.print("#>entry -maxpoolsize: ");
			try{
				input[1]=scan.next().trim();
				Long.parseLong(input[1]);
			}catch(Exception e1){
				System.exit(-1);
			}
		}
		System.out.print("#>entry -subject: ");
		input[2]=scan.next().trim();
		
		for(int i=0;i<subjects.length;i++){
			String subject=subjects[i];
			if(input[2].equals(subject)){
				flag = true;
			}
		}
		if(!flag){
			logger.info(String.format("%s is not found in websystem.xml", input[2]));
			System.out.print("#>entry -subject: ");
			input[2]=scan.next().trim();
			for(int i=0;i<subjects.length;i++){
				String subject=subjects[i];
				if(input[2].equals(subject)){
					flag = true;
				}
			}
			if(!flag){
				System.exit(-1);
			}
		}
		scan.close();
		if(input[0].equalsIgnoreCase("yes")){
			input[0] = "true";
		}else{
			input[0] = "false";
		}
		if(input[1]==null||"".equals(input[1].trim())){
			int n = Runtime.getRuntime().availableProcessors()+1;
			input[1] = ""+n;
		}else if(Long.parseLong(input[1])>=Integer.MAX_VALUE){
			int n = Integer.MAX_VALUE-10000;
			input[1] = ""+n;
		}else if(Long.parseLong(input[1])<=0){
			int n = Runtime.getRuntime().availableProcessors()+1;
			input[1] = ""+n;
		}
		System.out.println("       ");
		LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
		RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();
		WebsystemThreadFactoryInstance factory = new WebsystemThreadFactoryInstance(
				"boot", Thread.NORM_PRIORITY, false);
		WebsystemThreadPoolParameter parame = new WebsystemThreadPoolParameter(
				1, Integer.parseInt(input[1]), 3, TimeUnit.SECONDS, queue, factory, handler);
		try {
			BootLauncher.launcher(parame, input[2], Boolean.parseBoolean("false"), true);
		} catch (MalformedObjectNameException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}
	}

}
