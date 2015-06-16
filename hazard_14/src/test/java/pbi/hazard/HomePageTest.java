package pbi.hazard;

import org.junit.Before;
import org.junit.Test;

//TODO http://www.theserverlabs.com/blog/2008/09/17/automated-integration-testing-with-selenium-maven-and-jetty/
//TODO http://comsysto.wordpress.com/2010/06/04/test-driven-development-with-apache-wicket-and-spring-framework/

/**
 * Simple test using the WicketTester
 */
// public class TestHomePage extends TestCase {
public class HomePageTest extends WicketTestBase {

	// public class MyTestSuite {
	// @BeforeClass
	// static public void setUp() throws Exception {
	// WebPageTestBasicContext.beforePageTests();
	// }
	//
	// @AfterClass
	// static public void tearDown() throws Exception {
	// WebPageTestBasicContext.afterPageTests();
	// }

	// }

	/*
	 * @Override public void setUp() { tester = new WicketTester(new
	 * WicketApplication()); }
	 */

	@Before
	public void init() {
		super.init();
	}

	@Test
	public void HomePageButtons() {
		// start and render the test page
		tester.startPage(HomePage.class);
		// assert rendered page class
		tester.assertRenderedPage(HomePage.class);
		tester.executeAjaxEvent("help", "onclick");
		tester.assertRenderedPage(HelpPage.class);
		// System.out.println(tester.getLastRenderedPage());
		// System.out.println(tester.getPreviousRenderedPage());

		tester.startPage(HomePage.class);
		tester.executeAjaxEvent("memberlist", "onclick");
		tester.assertRenderedPage(MemberList.class);
		// tester.startPage(MemberList.class);
		tester.executeAjaxEvent("form:return", "onclick");
		tester.assertRenderedPage(HomePage.class);

		tester.startPage(HomePage.class);
		tester.executeAjaxEvent("pullOut", "onclick");
		tester.assertRenderedPage(PullOutListPage.class);
		tester.executeAjaxEvent("form:return", "onclick");
		tester.assertRenderedPage(HomePage.class);

		tester.startPage(HomePage.class);
		tester.executeAjaxEvent("reports", "onclick");
		tester.assertRenderedPage(ReportPage.class);
		tester.executeAjaxEvent("return", "onclick");
		tester.assertRenderedPage(HomePage.class);

		// ShutDown only works for Tomcat (not Jetty, WicketTester)
		tester.startPage(HomePage.class);
		tester.executeAjaxEvent("shutdown", "onclick");
		tester.assertRenderedPage(ClosePage.class);

		// tester. list of buttons
		// TEST: button shortcuts

		// tester.assertVisible("modal");
		// tester.isEnabled("modal");
	}

}
