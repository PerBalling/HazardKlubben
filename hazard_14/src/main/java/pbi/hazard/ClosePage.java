package pbi.hazard;

import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.WebPage;
//import org.mortbay.jetty.Server;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

//import pbi.hazard.common.StopServer;

public class ClosePage extends WebPage {
	private static Logger logger = WicketApplication.getLogger();

//	@Autowired
//	private SessionFactory sessionFactory;

	final int sleep = 0;// 3000;

	public ClosePage() {
		super();

		// System.out.println("ClosePage");
		/*
		 * Enumeration<?> enum1 = logger.getAllAppenders(); while
		 * (enum1.hasMoreElements()) { Appender app = (Appender)
		 * enum1.nextElement(); if (app instanceof FileAppender) {
		 * System.out.println("Appended File=" + ((FileAppender) app).getFile());
		 * 
		 * File f = new File(((FileAppender) app).getFile());
		 * System.out.println(f.getPath());
		 * 
		 * } }
		 */

		// try {
		// System.out.println("Server version: " + Server.getVersion());
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

//		String os = getOsName();
//		if (os.equals("windows")) {
			stopTomcat();
//		} else {
//			System.out.println("Tomcat not stopped (" + os + ")");
//			logger.info("Tomcat not stopped (" + os + ")");
//		}
	}

	private void stopTomcat() {
		try {
			// System.out.println("Wait: " + sleep / 1000 + " sec.");
			Thread.sleep(sleep);

			// StopServer.shutdown(); // Embeded Jetty

			// Stop Tomcat Server
			// <Server port="8005" shutdown="SHUTDOWN">
			Socket s = new Socket(InetAddress.getByName("127.0.0.1"), 8005);
			OutputStream out = s.getOutputStream();

			logger.info("Shutdown Tomcat Server (8005)");

			out.write(("SHUTDOWN").getBytes());
			out.flush();
			s.close();

		} catch (ConnectException e) {
			// Not Tomcat server
			System.err.println("Tomcat: " + e.getMessage());
			logger.error("Tomcat: " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
//		sessionFactory.getCurrentSession().disconnect();
	}

	public String getOsName() {
		String osName = System.getProperty("os.name").toLowerCase();
		// System.out.println(osName);
		String os = "";
		if (osName.indexOf("windows") > -1) {
			os = "windows";
		} else if (osName.indexOf("linux") > -1) {
			os = "linux";
		} else if (osName.indexOf("mac") > -1) {
			os = "mac";
		} else {
			os = osName;
		}
		// System.out.println(os);

		return os;
	}
}
