package com.websystem.test.accept;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.X509Certificate;

import com.websystem.domain.WebsystemWareHouse;
import com.websystem.service.spi.WebsystemComputable;
import com.websystem.util.Pair;
import com.websystem.util.PlatformUtil;
import com.websystem.workspace.util.ActionClassLoader;
import com.websystem.workspace.util.WorkspaceInitial;

public class ComputeInstance implements WebsystemComputable<WebsystemWareHouse, byte[]> {

	/**
	 * version
	 */
	private static final long serialVersionUID = -5231783009099905459L;
	public ComputeInstance(){
	}

	@Override
	public byte[] compute(WebsystemWareHouse ware) throws InterruptedException {
		// TODO Auto-generated method stub
		WorkspaceInitial winit = WorkspaceInitial.newInstance();
		String result =null;
		Pair<String,byte[]> pair = ware.getProcesses();
		X509Certificate cert = ware.getSourceCertificate();
		try {
			winit.workFrame(cert);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Path path = null;
		try {
			path = winit.workspace();
			path = Paths.get(path.toString(),"thirdparty");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ActionClassLoader loader = new ActionClassLoader();
		String classname = pair.getFirst();
		Class<?> clazz = null;
		try {
			clazz = loader.loaderSource(classname, cert);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(clazz!=null){
			try {
				Object obj = PlatformUtil.objectUnMarshal(pair.getSecond());
				AcceptEntity ae = (AcceptEntity) clazz.cast(obj);
				result = ae.getName();
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result.getBytes();
	}

}
