package pbi.hazard;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.junit.Before;
import org.junit.Test;

/**
 * Simple test using the WicketTester
 */
// public class TestHomePage extends TestCase {
public class HelpPageTest extends WicketTestBase {

	@Before
	public void init() {
		super.init();
	}

	@Test
	public void HelpPageButton() {
		// System.out.println("HelpPageButtons");

		// start and render the test page
		tester.startPage(HelpPage.class);
		// assert rendered page class
		tester.assertRenderedPage(HelpPage.class);

		tester.executeAjaxEvent("return", "onclick");
		tester.assertRenderedPage(HomePage.class);
	}

/*	@Test
	public void HelpPageImgButton() {
		tester.startPage(HelpPage.class);
		tester.assertRenderedPage(HelpPage.class);

    String doc = tester.getServletResponse().getDocument();
    System.out.println(doc);

    imgreturn.click ??? No Wicket component
		tester.assertRenderedPage(HomePage.class);
	}
*/
}
