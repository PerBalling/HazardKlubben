package pbi.hazard;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import pbi.hazard.common.DateTime;
import pbi.hazard.model.Member;
import pbi.hazard.model.PullOut;

/**
 * Application object for your web application. If you want to run this
 * application without deploying, run the Start class.
 * 
 * @see pbi.hazard.Start#main(String[])
 */
public class WicketApplication extends WebApplication {
	private static Logger logger = Logger.getRootLogger();
	// Global variable
	private static List<String> infoLog = new ArrayList<String>();
	private static int lastPulled = 0;
	private static int lastOnsitePulled = 0; // 20121027: Changed from Cap to Id
	private static Date beginTime = new Date();
	private static long timeSinceLastPullout = 0;

	/**
	 * Constructor
	 */
	public WicketApplication() {
	}

	@Override
	protected void init() {
		// System.out.println("WicketApplication - init");
		super.init();

		// Bind Spring to Wicket
		addComponentInstantiationListener(new SpringComponentInjector(this));
	}

	/**
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	public Class<HomePage> getHomePage() {
		return HomePage.class;
	}

	/**
	 * @param logger
	 *          the logger to set
	 */
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	/**
	 * @return the logger
	 */
	public static Logger getLogger() {
		return logger;
	}

	public static void updateInfoLog(String info, int maxSize) {
		if (infoLog.size() >= maxSize) {
			infoLog.remove(0);
		}
		infoLog.add(DateTime.formatTime(new Date()) + " " + info);
	}

	public static List<String> getInfoLog() {
		return infoLog;
	}

	/**
	 * @param lastPulled
	 *          the lastPulled to set
	 */
	public static void setLastPulled(int lastPulled) {
		WicketApplication.lastPulled = lastPulled;
	}

	/**
	 * @return the lastPulled
	 */
	public static int getLastPulled() {
		return lastPulled;
	}

	/**
	 * @param lastOnsitePulled
	 *          the lastOnsitePulled to set
	 */
	public static void setLastOnsitePulled(int lastOnsitePulled) {
		// System.out.println("### setLastOnsitePulled=" + lastOnsitePulled);
		WicketApplication.lastOnsitePulled = lastOnsitePulled;
	}

	/**
	 * @return the lastOnsitePulled
	 */
	public static int getLastOnsitePulled() {
		return lastOnsitePulled;
	}

	public static void initVars() {
		WicketApplication.infoLog = new ArrayList<String>();
		setLastPulled(0);
		setLastOnsitePulled(0);
		setTimeSinceLastPullout(6);
	}

	/**
	 * @return the beginTime
	 */
	public static Date getBeginTime() {
		return beginTime;
	}

	/**
	 * @param timeSinceLastPullout
	 *          the timeSinceLastPullout to set [HH]
	 *          use "0" for junit
	 */
	public static void setTimeSinceLastPullout(long timeSinceLastPullout) {
		// 24*60*60*1000=86.400.000
		WicketApplication.timeSinceLastPullout = timeSinceLastPullout * 60 * 60 * 1000;
	}

	/**
	 * @return the timeSinceLastPullout
	 */
	public static long getTimeSinceLastPullout() {
		return timeSinceLastPullout;
	}
	
}
