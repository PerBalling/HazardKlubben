package pbi.hazard;

// https://cwiki.apache.org/WICKET/unit-testing-pages.html

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.util.tester.FormTester;
import org.junit.Before;
import org.junit.Test;

import pbi.hazard.common.DateTime;
import pbi.hazard.model.Member;
import pbi.hazard.model.PullOut;
import pbi.hazard.model.PullOutMember;

/**
 * Simple test using the WicketTester
 */
// public class TestHomePage extends TestCase {
public class PullOutPageTest extends WicketTestBase {
	private static Logger logger = WicketApplication.getLogger();
	// æøå csv ascii-convert
	private String PullOutName = "Tæster" + DateTime.formatDayTime(WicketApplication.getBeginTime());

	@Before
	public void init() {
		super.init();
	}

	@Test
	public void PullOutTest() {
		// Test in right order
		PullOutListView();
		PullOutCopy();
	}

	public void PullOutListView() {
		// start and render the test page
		tester.startPage(PullOutListPage.class);
		// assert rendered page class
		tester.assertRenderedPage(PullOutListPage.class);
		tester.assertNoErrorMessage();
		tester.assertNoInfoMessage();

		// Get list of active members from DB
		List<Member> memberList = memberDao.getByActive(Boolean.TRUE);
		// System.out.println(memberList.size());
		// [Member [id=35, cap=36, firstName=Kai S., lastName=Munk, active=true],
		// Member [id=63, cap=64, firstName=Flemming T., lastName=Pedersen,
		// active=true], Member [id=65, cap=66, firstName=Peter Hjort,
		// lastName=Pedersen, active=true], Member [id=185, cap=188,
		// firstName=Frank, lastName=Jacobsen, active=true], Member [id=191,
		// cap=194, firstName=Christian, lastName=Weithøft, active=true], Member
		// [id=193, cap=196, firstName=Dennis, lastName=Bohnstedt Hansen,
		// active=true]]
		int MinCap = Integer.MAX_VALUE;
		int MaxCap = 0;
		List<Integer> active = new ArrayList<Integer>(memberList.size());
		Member member;
		for (Iterator<Member> iterator = memberList.iterator(); iterator.hasNext();) {
			member = (Member) iterator.next();
			active.add(member.getCap());
			MinCap = Math.min(MinCap, member.getCap());
			MaxCap = Math.max(MaxCap, member.getCap());
		}
		// System.out.println(MinCap);
		// System.out.println(MaxCap);
		// System.out.println(active);

		WicketApplication.setTimeSinceLastPullout(0);
		tester.executeAjaxEvent("form:new", "onclick");
		tester.assertRenderedPage(CreatePullOutPage.class);

		FormTester formTester0 = tester.newFormTester("form");
		// formTester1.setValue("name", "Tester123");
		// System.out.println("Tester" + DateTime.formatDayTime(new Date()));
		formTester0.setValue("name", PullOutName);
		tester.assertLabel("form:copy", "");
		// System.out.println("NoCopy");

		formTester0.submit();

		tester.assertRenderedPage(PullOutPage.class);

		/*
		 * Calendar cal1 = Calendar.getInstance(); cal1.add(Calendar.YEAR, -1); //
		 * TST cal1.set(HOUR_OF_DAY, 0); cal1.set(MINUTE, 0); cal1.set(SECOND, 0);
		 * cal1.set(MILLISECOND, 0); Calendar cal2 = Calendar.getInstance();
		 */
		// System.out.println(cal1.getTime());
		// System.out.println(cal2.getTime());
		// List<PullOut> poList = pullOutDao.getPullOut(cal1.getTime(),
		// cal2.getTime());
		List<PullOut> poList = pullOutDAO.getPullOut();
		// System.out.println(poList.size());
		// TODO: This proc. only finds closed pullouts
		for (Iterator<PullOut> iterator = poList.iterator(); iterator.hasNext();) {
			PullOut pullOut = (PullOut) iterator.next();
			// System.out.println(pullOut.getName() + "\t" +
			// DateTime.formatDate(pullOut.getStart()));
		}
		// List<PullOutMember> pomList =
		// pullOutMemberDAO.getPullOutMemberList(pullOut, 2);

		// http://paulszulc.wordpress.com/2009/08/17/wickettestbase-base-class-for-testing-wicket-spring-web-applications/

		// Test PullOutPage
		FormTester formTester;
		formTester = tester.newFormTester("pullform");
		Integer pull = 0;
		formTester.setValue("pull_cap", pull.toString());
		formTester.submit();
		tester
				.assertErrorMessages(new String[] { pull.toString() + " skal være mellem " + MinCap + " og " + MaxCap + "." });
		// tester.assertNoInfoMessage();

		/*
		 * formTester = tester.newFormTester("pullform");
		 * formTester.setValue("pull_cap", active.get(0).toString());
		 * formTester.submit(); // tester.assertErrorMessages(new String[] { "0" });
		 * // tester.assertPageLink("", PullOutPage.class);
		 * 
		 * 
		 * formTester = tester.newFormTester("pullform");
		 * formTester.setValue("pull_cap", active.get(0).toString());
		 * formTester.submit(); tester.assertErrorMessages(new String[] {
		 * "0 skal være mellem " + MinCap + " og " + MaxCap + "." });
		 */

		tester.assertComponent("finishform:close", Button.class);
		tester.isEnabled("finishform:close");
		// System.out.println(formTester.getForm().getId());
		formTester = tester.newFormTester("finishform");
		// System.out.println(formTester.getForm().getId());
		formTester.submit();
		// Afslut ikke - Der mangler: 1
		tester.assertInfoMessages(new String[] { "Afslut ikke - Der mangler: " + active.size() });

		formTester = tester.newFormTester("finishform");
		formTester.setValue("skip", true);
		formTester.submit();
		// Afslut ikke - Beskrivelse mangler
		tester.assertInfoMessages(new String[] { "Afslut ikke - Der mangler: " + active.size(),
				"Afslut ikke - Note mangler" });

		tester.assertComponent("returnform:pulloutlist", Button.class);
		tester.isEnabled("returnform:pulloutlist");
		// Pull all
		List<Integer> rndActive = RandomList(active);
		// System.out.println(rndActive);

		// Onsite
		// formTester = tester.newFormTester("pullform");
		// formTester.setValue("onsite", pull.toString());
		List<PullOutMember> cols = (List<PullOutMember>) tester.getComponentFromLastRenderedPage("cols")
				.getDefaultModelObject();
		List<PullOutMember> firstCol = (List<PullOutMember>) cols.get(0);
		// System.out.println(tester.getTagByWicketId("onsitepulled").getValue());
		// System.out.println(WicketApplication.getInfoLog());
		tester.assertLabel("onsitepulled", "0-0:0");
		tester.executeAjaxEvent("cols:0:rows:0:onsite", "onclick");
		tester.assertLabel("onsitepulled", "1-0:1");
		// System.out.println(WicketApplication.getInfoLog());
		// System.out.println(tester.getTagByWicketId("onsitepulled").getValue());
		// System.out.println("cols:0:rows:" + String.valueOf(firstCol.size() - 1) +
		// ":onsite");
		tester.executeAjaxEvent("cols:0:rows:" + String.valueOf(firstCol.size() - 1) + ":onsite", "onclick");
		// tester.submitForm("pullform");
		// formTester = tester.newFormTester("pullform");
		// formTester.submit();
		// tester.startPage(tester.getLastRenderedPage());
		// tester.assertLabel("onsitepulled", "2-0:2");
		// System.out.println(tester.getTagByWicketId("onsitepulled").getValue());
		// tester.executeAjaxEvent("cols:0:rows:0:onsite", "onClick");
		// System.out.println(tester.getTagByWicketId("onsitepulled").getValue());
		// System.out.println(WicketApplication.getInfoLog());
		tester.executeAjaxEvent("cols:0:rows:0:onsite", "onclick");
		// System.out.println(tester.getTagByWicketId("onsitepulled").getValue());
		// System.out.println(WicketApplication.getInfoLog());
		// [21:14:44 29. okt 2012 21:14, 21:14:44 __8 Steen Rohde Nielsen Tilstede,
		// 21:14:44 651 Test Tilstede, 21:14:44 651 Test Fraværende]

		// System.out.println(memberList.size());
		for (int i = 0; i < rndActive.size(); i++) {
			// System.out.println(tester.getTagByWicketId("pulled").getValue());
			// 9-0:9
			// System.out.println(WicketApplication.getInfoLog());
			formTester = tester.newFormTester("pullform");
			pull = rndActive.get(i);
			formTester.setValue("pull_cap", pull.toString());
			formTester.submit();

			tester.assertRenderedPage(PullOutPage.class);
			tester.assertNoErrorMessage();
			tester.assertNoInfoMessage();

			if (i == 3) {
				formTester = tester.newFormTester("pullform");
				pull = rndActive.get(0); // unset first
				formTester.setValue("pull_cap", pull.toString());
				formTester.submit();

				tester.assertRenderedPage(PullOutPage.class);
				tester.assertNoErrorMessage();
				tester.assertInfoMessages(new String[] { "Kun den seneste udtrukne (" + rndActive.get(3)
						+ ") kan sættes 'Med igen'." });
			}
		}
		// System.out.println(tester.getTagByWicketId("pulled").getValue());
		// 9-9:0
		tester.isDisabled("returnform:pulloutlist");
		formTester = tester.newFormTester("finishform");
		formTester.setValue("note", "Tester_OK");
		formTester.submit();
		tester.assertInfoMessages(new String[] {});
	}

	// Copy has been changed to be performed automatically
	public void PullOutCopy() {
		// start and render the test page
		tester.startPage(PullOutListPage.class);

		tester.executeAjaxEvent("form:new", "onclick");
		tester.assertRenderedPage(CreatePullOutPage.class);

		FormTester formTester0 = tester.newFormTester("form");
		// formTester1.setValue("name", "Tester123");
		// System.out.println("Tester" + DateTime.formatDayTime(new Date()));
		formTester0.setValue("name", PullOutName);
		tester.assertLabel("form:copy", "*");
		// System.out.println("Copy");
		// formTester0.submit("copy");
		formTester0.submit();

		tester.assertRenderedPage(PullOutPage.class);
		// tester.assertComponent("finishform:close", Button.class);
		// tester.isEnabled("finishform:close");
		// formTester = tester.newFormTester("finishform");
		// formTester.submit();

		// Return immediately
		// tester.startPage(PullOutPage.class);
		// tester.executeAjaxEvent("returnform:pulloutlist", "onclick");
		tester.executeAjaxEvent("returnform:pulloutlist", "onclick");
		tester.assertRenderedPage(PullOutListPage.class);
		tester.assertNoErrorMessage();
		tester.assertNoInfoMessage();
	}

	@Test
	public void PullOutShow() {
		// start and render the test page
		tester.startPage(PullOutListPage.class);
		// assert rendered page class
		tester.assertRenderedPage(PullOutListPage.class);
		tester.assertNoErrorMessage();
		tester.assertNoInfoMessage();

		logger.debug("RenderedPage=PullOutListPage");
		// FormTester formTester = tester.newFormTester("form");
		ListView<PullOut> listView = (ListView<PullOut>) tester.getComponentFromLastRenderedPage("form:rows");
		assertNotNull(listView);

		// System.out.println(listView.size());
		// System.out.println(listView);

		List<PullOut> poList = (List<PullOut>) listView.getList();
		for (Iterator<PullOut> iterator = poList.iterator(); iterator.hasNext();) {
			PullOut po = (PullOut) iterator.next();
			// System.out.println(po.getName() + "\t" +
			// DateTime.formatDayTime(po.getStart()));
			logger.debug(po);
			break; // Only show first line
		}
		// System.out.println(PullOutName);
		logger.debug(PullOutName);
		logger.debug(poList.get(0).getName());
		assertEquals(PullOutName, poList.get(0).getName());

		// Test PullOutShowList page
		tester.executeAjaxEvent("form:rows:0:show", "onclick"); // Show top row
		tester.assertRenderedPage(PullOutShowList.class);

		tester.startPage(PullOutListPage.class);
		tester.executeAjaxEvent("form:rows:0:change", "onclick"); // Edit top row
		tester.assertRenderedPage(CreatePullOutPage.class);
		tester.assertNoErrorMessage();
		tester.assertNoInfoMessage();
		// Edit -> fields disabled
		FormTester formTester1 = tester.newFormTester("form");
		formTester1.submit();
		tester.assertRenderedPage(PullOutListPage.class);
		tester.assertNoErrorMessage();
		tester.assertNoInfoMessage();
	}

	@Test
	public void PulloutToString() {
		PullOut p = new PullOut();
		assertEquals(p.toString(), "PullOut [id=null, name=null, start=null, stop=null, note=null, winner=null]");
	}

	@Test
	public void PulloutMemberToString() {
		PullOutMember pom = new PullOutMember();
		assertEquals(pom.toString(), "PullOutMember [id=null, pullOut=null, member=null, pulledOut=null, onsite=null]");
	}

	/**
	 * @param inputList
	 * @return
	 */
	private List<Integer> RandomList(List<Integer> inputList) {
		List<Integer> rndList = new ArrayList<Integer>(0);
		Random randomGenerator = new Random();
		int randomInt;
		while (rndList.size() < inputList.size()) {
			randomInt = randomGenerator.nextInt(inputList.size());
			if (!rndList.contains(inputList.get(randomInt))) {
				rndList.add(inputList.get(randomInt));
			}
		}
		// System.out.println("¤¤¤ "+rndList);
		return rndList;
	}

	@Test
	public void PullOutEdit() {
		// start and render the test page
		tester.startPage(PullOutListPage.class);
		// assert rendered page class
		tester.assertRenderedPage(PullOutListPage.class);
		tester.assertNoErrorMessage();
		tester.assertNoInfoMessage();

		logger.debug("RenderedPage=PullOutListPage");
		// FormTester formTester = tester.newFormTester("form");
		ListView<PullOut> listView = (ListView<PullOut>) tester.getComponentFromLastRenderedPage("form:rows");
		assertNotNull(listView);

		// Test PullOutShowList page
		tester.executeAjaxEvent("form:rows:0:change", "onclick"); // Show top row
		tester.assertRenderedPage(CreatePullOutPage.class);
	}

	@Test
	public void PullOutShowListEdit() {
		// start and render the test page
		tester.startPage(PullOutListPage.class);
		// assert rendered page class
		tester.assertRenderedPage(PullOutListPage.class);
		tester.assertNoErrorMessage();
		tester.assertNoInfoMessage();

		logger.debug("RenderedPage=PullOutListPage");
		// FormTester formTester = tester.newFormTester("form");
		ListView<PullOut> listView = (ListView<PullOut>) tester.getComponentFromLastRenderedPage("form:rows");
		assertNotNull(listView);

		// Test PullOutShowList page
		tester.executeAjaxEvent("form:rows:0:show", "onclick"); // Show top row
		tester.assertRenderedPage(PullOutShowList.class);

		tester.clickLink("form:change", true); // Edit
		// tester.executeAjaxEvent("form:change", "onclick"); // Edit
		tester.assertRenderedPage(CreatePullOutPage.class);
		tester.assertNoErrorMessage();
		tester.assertNoInfoMessage();

	}

	@Test
	public void PullOutShowListSelect() {
		// start and render the test page
		tester.startPage(PullOutListPage.class);
		// assert rendered page class
		tester.assertRenderedPage(PullOutListPage.class);
		tester.assertNoErrorMessage();
		tester.assertNoInfoMessage();

		logger.debug("RenderedPage=PullOutListPage");
		// FormTester formTester = tester.newFormTester("form");
		ListView<PullOut> listView = (ListView<PullOut>) tester.getComponentFromLastRenderedPage("form:rows");
		assertNotNull(listView);

		// Test PullOutShowList page
		tester.executeAjaxEvent("form:rows:0:select", "onclick"); // Show top row
		tester.assertRenderedPage(PullOutPage.class);

	}

}
