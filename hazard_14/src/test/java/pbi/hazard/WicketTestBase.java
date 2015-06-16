package pbi.hazard;

import java.util.Locale;

import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import pbi.hazard.dao.MemberDAO;
import pbi.hazard.dao.PullOutDAO;
import pbi.hazard.dao.PullOutMemberDAO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext.xml" })
// @SuiteClasses({ TestHomePage.class })
// @TransactionConfiguration(transactionManager = "txManager", defaultRollback =
// false)
public class WicketTestBase {
	// http://paulszulc.wordpress.com/tag/wickettester/
	@Autowired
	private ApplicationContext applicationContext;

	@SpringBean
	@Autowired
	protected MemberDAO memberDao;
	@SpringBean
	@Autowired
	protected PullOutDAO pullOutDAO;
	@SpringBean
	@Autowired
	protected PullOutMemberDAO pullOutMemberDAO;

	protected WicketTester tester;

	// protected EnhancedWicketTester enhancedTester;
	// protected Clicker clicker;
	// protected Enterer enterer;
	// protected FormFiller formFiller;

	// Server server = new Server();

	public void init() {
		// populateData();
		createTester();
		// initHelpers();
	}

	/*
	 * @Before public void enableSrv() { System.out.println("enableSrv");
	 * 
	 * SocketConnector connector = new SocketConnector();
	 * 
	 * // Set some timeout options to make debugging easier.
	 * connector.setMaxIdleTime(1000 * 60 * 60); connector.setSoLingerTime(-1);
	 * connector.setPort(8080); server.setConnectors(new Connector[] { connector
	 * });
	 * 
	 * WebAppContext bb = new WebAppContext(); bb.setServer(server);
	 * bb.setContextPath("/"); bb.setWar("src/main/webapp");
	 * 
	 * server.addHandler(bb);
	 * 
	 * try { server.start(); } catch (Exception e) { e.printStackTrace();
	 * System.exit(100); } }
	 */
	/*
	 * @After public void disableSrv() { System.out.println("disableSrv");
	 * 
	 * try { server.stop(); server.join(); } catch (Exception e) {
	 * e.printStackTrace(); System.exit(100); } }
	 */
	/**
	 * Override to populate data in database for each test
	 */
	// protected void populateData() {
	// // override in test if necessary
	// }

	private void createTester() {
		// tester = new WicketTester((WebApplication);
		// applicationContext.getBean("WicketApplication");
		tester = new WicketTester(HomePage.class);
		tester.setupRequestAndResponse();
		tester.getWicketSession().setLocale(getLocale());
		tester.getApplication().addComponentInstantiationListener(
				new SpringComponentInjector(tester.getApplication(), applicationContext, true));
		// enhancedTester = new EnhancedWicketTester(tester);
		// initSessionBeforeTest((UserSession) tester.getWicketSession());
	}

	/*
	 * private void initHelpers() { clicker = new Clicker(tester, enhancedTester);
	 * formFiller = new FormFiller(tester, enhancedTester); enterer = new
	 * Enterer(tester, enhancedTester, clicker, formFiller); }
	 */

	/**
	 * Override to change locale
	 * 
	 * @return locale, default EN
	 */
	protected Locale getLocale() {
		return new Locale("DA");
	}

	/**
	 * Insert application specific properties to session
	 * 
	 * @param session
	 * @return
	 */
	// protected void initSessionBeforeTest(UserSession session) {
	// // override in test if necessary
	// }

	/**
	 * Test added to satisfy JUnit run all
	 */
	@Ignore
	@Test
	public void Dummy() {
		// System.out.println("Dummy");
	}
}