package pbi.hazard;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.ITestPageSource;
import org.junit.Before;
import org.junit.Test;

import pbi.hazard.common.DateTime;
import pbi.hazard.model.Member;

/**
 * Simple test using the WicketTester
 */
// public class TestHomePage extends TestCase {
public class MemberListTest extends WicketTestBase {

	@Before
	public void init() {
		super.init();
	}

	@SuppressWarnings("unchecked")
	@Test
	/**
	 * Test count
	 */
	public void MemberListView() {
		// start and render the test page
		tester.startPage(MemberList.class);
		// assert rendered page class
		tester.assertRenderedPage(MemberList.class);
		tester.assertNoErrorMessage();
		tester.assertNoInfoMessage();

		// tester.assertListView("form:rows", Arrays.asList("1","2"));

		// Get list of active members from DB
		List<Member> memberList = memberDao.getByActive(Boolean.TRUE);
		ListView<Member> listView = (ListView<Member>) tester.getComponentFromLastRenderedPage("form:rows");
		assertEquals(memberList.size(), listView.size()); // Default view values

		// Compare with label
		tester.assertLabel("form:count", String.valueOf(memberList.size()));

		// Filter: All members
		memberList = memberDao.getMembers(); // Get list of all members from DB
		FormTester formTester1 = tester.newFormTester("form");
		// Set search filter to find all members
		formTester1.setValue("capFilter", "0");
		formTester1.setValue("activeFilter", false);
		formTester1.submit();
		listView = (ListView<Member>) tester.getComponentFromLastRenderedPage("form:rows");
		assertEquals(memberList.size(), listView.size()); // Updated view values
	}

	@SuppressWarnings("unchecked")
	@Test
	public void MemberListEdit() {
		tester.startPage(MemberList.class);
		FormTester formTester2 = tester.newFormTester("form");
		formTester2.setValue("capFilter", "1");
		// formTester2.setValue("lastnameFilter", "balling"); // 158
//		formTester2.setValue("firstnameFilter", "ch"); 
//		formTester2.setValue("lastnameFilter", "weithøft"); // 194 (æøå)
		formTester2.setValue("activeFilter", false);
		Calendar cal=Calendar.getInstance();
		cal.set(2010, 1, 1);
		formTester2.setValue("dateFilter", DateTime.formatDateSearch(cal.getTime()));
		formTester2.submit();
		ListView<Member> listView = (ListView<Member>) tester.getComponentFromLastRenderedPage("form:rows");
		assertEquals(1, listView.size()); // Search view values

		tester.executeAjaxEvent("form:rows:0:edit", "onclick"); // Edit top row member
		tester.assertRenderedPage(MemberDetailPage.class);

		String incomplete_mail = "pballing"; // incomplete email
		// System.out.println(tester.getLastRenderedPage());
		FormTester formTester3 = tester.newFormTester("form"); // MemberDetailPage
		formTester3.setValue("email1", incomplete_mail); // stay on page
		formTester3.submit();
		tester.assertRenderedPage(MemberDetailPage.class);
		tester.assertErrorMessages(new String[] { "'" + incomplete_mail + "' er ikke en gyldig emailadresse." });

		FormTester formTester4 = tester.newFormTester("form"); // MemberDetailPage
		// formTester4.setValue("lastname", "pballing");
		// formTester4.setValue("email1", "pballing@csc.dk"); // complete email -
		// return
		formTester4.setValue("email1", "c.weithoeft@gefiberpost.dk");
		formTester4.submit();
		tester.assertRenderedPage(MemberList.class);
		tester.assertNoErrorMessage();
		tester.assertNoInfoMessage();
	}

	@Test
	public void MemberListNewBack() {
		tester.startPage(MemberList.class);
		// New member test
		tester.executeAjaxEvent("form:new", "onclick");
		tester.assertRenderedPage(MemberDetailPage.class);

		// startPage with parameter(s)
		tester.startPage(new ITestPageSource() {
			public Page getTestPage() {
				return new MemberDetailPage(new Member());
			}
		});
		tester.executeAjaxEvent("form:return", "onclick");
		tester.assertRenderedPage(MemberList.class);
		tester.assertNoErrorMessage();
		tester.assertNoInfoMessage();
	}

	@Test
	public void MemberListNew() {
		tester.startPage(MemberList.class);
		// New member test
		tester.executeAjaxEvent("form:new", "onclick");
		tester.assertRenderedPage(MemberDetailPage.class);

		FormTester formTester5 = tester.newFormTester("form"); // MemberDetailPage
		formTester5.submit();
		tester.assertRenderedPage(MemberDetailPage.class);
		tester.assertErrorMessages(new String[] { "'Fornavn' skal udfyldes.", "'Lønnr' skal udfyldes.",
				"'Betaling' skal udfyldes." });

		// Validate input
		FormTester formTester6 = tester.newFormTester("form"); // MemberDetailPage
		formTester6.setValue("firstname", "f");
		formTester6.setValue("lastname", "e");
		formTester6.setValue("contactid", "0");
		formTester6.setValue("payment", "0");
		formTester6.submit();
		tester.assertRenderedPage(MemberDetailPage.class);
		tester
				.assertErrorMessages(new String[] { "'f' skal være mindst 2 bogstaver.", "'e' skal være mindst 2 bogstaver." });

		// Validate input
		FormTester formTester7 = tester.newFormTester("form"); // MemberDetailPage
		formTester7.setValue("active", false);
		formTester7.setValue("firstname", "Test");
		formTester7.setValue("lastname", "Tester");
		formTester7.setValue("contactid", "0");
		formTester7.setValue("payment", "0");
		formTester7.submit();
		tester.assertRenderedPage(MemberList.class);

		// listView = (ListView<Member>)
		// tester.getComponentFromLastRenderedPage("form:rows");
		// System.out.println(listView.getList());

		// tester.getWicketSession().dirty();
		// Check DB values saved OK

		// HomePage homePage = (HomePage) tester.getLastRenderedPage();
		// BookForm bookForm = homePage.getForm();
		// BookListView bookListView = bookForm.getBookListView();
		// List<BookListItem> bookItems = toLinkedList(bookListView.iterator());
		// {
		// // Test values of 1st book
		// BookListItem bookListItem = bookItems.get(0); // Note: no hassle with
		// run-time component path id's
		// assertEquals("first", bookListItem.getNameField().getValue());
		// assertEquals("", bookListItem.getAuthorField().getValue());
		// assertEquals("-1", bookListItem.getTypeChoice().getValue());
		// }
	}

	@Test
	public void MemberListEditBack() {
		tester.startPage(MemberList.class);
		// New member test
		tester.executeAjaxEvent("form:new", "onclick");
		tester.assertRenderedPage(MemberDetailPage.class);
		FormTester formTester = tester.newFormTester("form"); // MemberDetailPage
		formTester.setValue("firstname", "Test");
		formTester.setValue("lastname", "Tester");
		// formTester.submitLink("return", false);
		formTester.submit("return");
		// tester.executeAjaxEvent("return", "onClick");
		// tester.assertRenderedPage(MemberList.class);
		tester.assertRenderedPage(MemberDetailPage.class); // If Dirty: Stay on page

		// <input type="button" value="Tilbage" style="width: 0px" name="return"
		// id="return" accesskey="0" onclick="JavaScript:history.back()"/>
	}
}
