package pbi.hazard;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.util.tester.FormTester;
import org.junit.Before;
import org.junit.Test;

import pbi.hazard.common.DateTime;
import pbi.hazard.model.PullOut;
import pbi.hazard.model.PullOutMember;

/**
 * Simple test using the WicketTester
 */
// public class TestHomePage extends TestCase {
public class ReportPageTest extends WicketTestBase {
	@Before
	public void init() {
		super.init();
	}

	@Test
	public void ReportPageMembers() {
		tester.startPage(ReportPage.class);
		tester.assertRenderedPage(ReportPage.class);
		tester.executeAjaxEvent("memberlist", "onclick");
		tester.assertRenderedPage(ReportMemberPage.class);

		tester.startPage(ReportMemberPage.class);
		tester.executeAjaxEvent("return", "onclick");
		tester.assertRenderedPage(ReportPage.class);
		tester.assertNoErrorMessage();
		tester.assertNoInfoMessage();

		// Again
		tester.startPage(ReportPage.class);
		tester.assertRenderedPage(ReportPage.class);
		tester.executeAjaxEvent("memberlist", "onclick");
		tester.assertRenderedPage(ReportMemberPage.class);

		// Form has to be activated in order to have the return button to operate!
		FormTester formTester1 = tester.newFormTester("form");
		// Set search filter to find all members
		formTester1.setValue("capFilter", "0");
		formTester1.setValue("activeFilter", false);
		formTester1.submit();
		tester.assertRenderedPage(ReportMemberPage.class);
		tester.assertLabel("form:count", String.valueOf(memberDao.getMembers().size()));

		FormTester formTester2 = tester.newFormTester("form");
		// Set search filter to find all members
		formTester2.setValue("capFilter", String.valueOf(Integer.MAX_VALUE));
		formTester2.setValue("firstnameFilter", "f_name");
		formTester2.setValue("lastnameFilter", "l_name");
		formTester2.setValue("mailFilter", "g_mail");
		formTester2.setValue("contactIdFilter", "0");
		DropDownChoice<String> paymentFilter = (DropDownChoice<String>) tester
				.getComponentFromLastRenderedPage("form:paymentFilter");
		formTester2.select("paymentFilter", paymentFilter.getChoices().size() - 1);
		formTester2.setValue("activeFilter", false);
		formTester2.submit();
		tester.assertRenderedPage(ReportMemberPage.class);
		// No one matches these filters
		tester.assertLabel("form:count", String.valueOf(0));

		FormTester formTester3 = tester.newFormTester("form");
		formTester3.submit("reset");
		tester.assertRenderedPage(ReportMemberPage.class);
		tester.assertNoErrorMessage();
		tester.assertNoInfoMessage();

		tester.startPage(ReportMemberPage.class);
		tester.executeAjaxEvent("return", "onclick");
		tester.assertRenderedPage(ReportPage.class);
		tester.assertNoErrorMessage();
		tester.assertNoInfoMessage();
	}

	@Test
	public void ReportPageWinner() {
		tester.startPage(ReportPage.class);
		tester.executeAjaxEvent("winner", "onclick");
		tester.assertRenderedPage(ReportWinnerPage.class);
		tester.assertNoErrorMessage();
		tester.assertNoInfoMessage();

		tester.startPage(ReportWinnerPage.class);
		tester.executeAjaxEvent("return", "onclick");
		tester.assertRenderedPage(ReportPage.class);
		tester.assertNoErrorMessage();
		tester.assertNoInfoMessage();

		// Again
		tester.startPage(ReportPage.class);
		tester.executeAjaxEvent("winner", "onclick");
		tester.assertRenderedPage(ReportWinnerPage.class);
		tester.assertNoErrorMessage();
		tester.assertNoInfoMessage();

		FormTester formTester2 = tester.newFormTester("form");
		formTester2.submit("reset");
		tester.assertRenderedPage(ReportWinnerPage.class);
		tester.assertNoErrorMessage();
		tester.assertNoInfoMessage();

		FormTester formTester4 = tester.newFormTester("form");
		formTester4.setValue("dateTextStart", "a");
		formTester4.setValue("dateTextStop", "b");
		formTester4.submit();
		tester.assertNoInfoMessage();
		tester.assertErrorMessages(new String[] { "'a' er ikke en valid Date.", "'b' er ikke en valid Date." });

		FormTester formTester3 = tester.newFormTester("form");
		// Set search filter
		Calendar cal = Calendar.getInstance();
		// cal.setTime(new Date());
		cal.add(Calendar.MONTH, -1);
		// formTester3.setValue("dateTextStart",
		// DateTime.formatDateSearch(WicketApplication.getBeginTime()));
		formTester3.setValue("dateTextStart", DateTime.formatDateSearch(cal.getTime()));
		cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, +1); // Tomorrow
		formTester3.setValue("dateTextStop", DateTime.formatDateSearch(cal.getTime()));
		formTester3.submit();
		// System.out.println(formTester3.getTextComponentValue("dateTextStart"));
		// System.out.println(formTester3.getTextComponentValue("dateTextStop"));
		// tester.submitForm("form"); // Note: Input fields get null'ed
		// System.out.println(formTester3.getTextComponentValue("dateTextStart"));
		// System.out.println(formTester3.getTextComponentValue("dateTextStop"));
		tester.assertRenderedPage(ReportWinnerPage.class);
		tester.assertNoErrorMessage();
		tester.assertNoInfoMessage();

		// System.out.println(formTester3.getTextComponentValue("rows"));

		tester.executeAjaxEvent("form:rows:0:show", "onclick"); // Top row - Show
		tester.assertRenderedPage(PullOutShowList.class);
		tester.assertNoErrorMessage();
		tester.assertNoInfoMessage();

		FormTester formTester5 = tester.newFormTester("form");
		// System.out.println(formTester5.getForm().getPage());
		// tester.startPage(PullOutShowList.class);
		tester.executeAjaxEvent("form:return", "onclick");
		tester.assertRenderedPage(ReportWinnerPage.class);
		tester.assertNoErrorMessage();
		tester.assertNoInfoMessage();

		tester.startPage(ReportWinnerPage.class);
		tester.executeAjaxEvent("return", "onclick");
		tester.assertRenderedPage(ReportPage.class);
		tester.assertNoErrorMessage();
		tester.assertNoInfoMessage();
	}

	@Test
	public void ReportPageOnsite() {
		tester.startPage(ReportPage.class);
		tester.executeAjaxEvent("onsite", "onclick");
		tester.assertRenderedPage(ReportOnsitePage.class);
		tester.assertNoErrorMessage();
		tester.assertNoInfoMessage();

		tester.startPage(ReportOnsitePage.class);
		tester.executeAjaxEvent("return", "onclick");
		tester.assertRenderedPage(ReportPage.class);
		tester.assertNoErrorMessage();
		tester.assertNoInfoMessage();

		// Again
		tester.startPage(ReportPage.class);
		tester.executeAjaxEvent("onsite", "onclick");
		tester.assertRenderedPage(ReportOnsitePage.class);
		tester.assertNoErrorMessage();
		tester.assertNoInfoMessage();

		FormTester formTester1 = tester.newFormTester("form");
		formTester1.setValue("dateTextStart", "a");
		formTester1.setValue("dateTextStop", "b");
		formTester1.submit();
		tester.assertNoInfoMessage();
		tester.assertErrorMessages(new String[] { "'a' er ikke en valid Date.", "'b' er ikke en valid Date." });

		FormTester formTester2 = tester.newFormTester("form");
		// Set search filter
		Calendar cal = Calendar.getInstance();
		// cal.setTime(new Date());
		cal.add(Calendar.MONTH, -1);
		// formTester3.setValue("dateTextStart",
		// DateTime.formatDateSearch(WicketApplication.getBeginTime()));
		formTester2.setValue("dateTextStart", DateTime.formatDateSearch(cal.getTime()));
		cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, 1); // Tomorrow
		formTester2.setValue("dateTextStop", DateTime.formatDateSearch(cal.getTime()));
		formTester2.submit();
		// System.out.println(formTester3.getTextComponentValue("dateTextStart"));
		// System.out.println(formTester3.getTextComponentValue("dateTextStop"));
		// tester.submitForm("form"); // Note: Input fields get null'ed
		// System.out.println(formTester3.getTextComponentValue("dateTextStart"));
		// System.out.println(formTester3.getTextComponentValue("dateTextStop"));
		tester.assertRenderedPage(ReportOnsitePage.class);
		tester.assertNoErrorMessage();
		tester.assertNoInfoMessage();

		// FormTester formTester3 = tester.newFormTester("form");
		// // Set search filter
		// cal = Calendar.getInstance();
		// // Tomorrow
		// cal.add(Calendar.DAY_OF_MONTH, 1);
		// formTester3.setValue("dateTextStart",
		// DateTime.formatDateSearch(cal.getTime()));
		// formTester3.setValue("dateTextStop",
		// DateTime.formatDateSearch(cal.getTime()));
		// // No Onsite
		// formTester3.submit();
		// tester.assertNoInfoMessage();
		// tester.assertNoErrorMessage();

		// Test DAO
		cal = Calendar.getInstance();
		Date d2 = cal.getTime();
		cal.add(Calendar.MONTH, -13);
		Date d1 = cal.getTime();
		List<PullOut> listPullOut = pullOutDAO.getPullOutWinner(d1, d2);
		List<PullOutMember> listMembers = pullOutMemberDAO.getOnsiteList(listPullOut);
		// Empty List
		listPullOut = pullOutDAO.getPullOutWinner(d1, d1);
		listMembers = pullOutMemberDAO.getOnsiteList(listPullOut);
	}

	// tester.startPage(ReportPage.class);
	// tester.executeAjaxEvent("extra", "onclick");
	// tester.assertRenderedPage(ReportPage.class);

}
