package pbi.hazard;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import pbi.hazard.panel.MemberPanel;

public class ReportPage extends WebPage {
	// @SpringBean
	// private MemberDAO memberDao;

	public ReportPage() {
		super();

		AjaxLink btnReturn = new AjaxLink("return") {

			@Override
			public void onClick(AjaxRequestTarget arg0) {
				setResponsePage(new HomePage());
			}
		};
		add(btnReturn);

		AjaxLink btnMemberList = new AjaxLink("memberlist") {

			@Override
			public void onClick(AjaxRequestTarget arg0) {
				// System.out.println("memberlist");
				/*
				 * 
				 * LoadableDetachableModel<List<PullOut>> lmodel = new
				 * LoadableDetachableModel<List<PullOut>>() {
				 * 
				 * @Override protected List<PullOut> load() { return
				 * pullOutDao.getPullOut(); } };
				 * 
				 * ListView<PullOut> listView = new ListView<PullOut>("rows", lmodel) {
				 */

				// List<?> l = memberDao.getMailList();
				// System.out.println(l);

				// setResponsePage(new PullOutListPage());
				setResponsePage(new ReportMemberPage());
			}

		};
		add(btnMemberList);

		AjaxLink btnWinner = new AjaxLink("winner") {

			@Override
			public void onClick(AjaxRequestTarget target) {
				// System.out.println("winner");
				setResponsePage(new ReportWinnerPage());
			}

		};
		add(btnWinner);

		AjaxLink btnOnsite = new AjaxLink("onsite") {

			@Override
			public void onClick(AjaxRequestTarget target) {
				// System.out.println("onsite");
				setResponsePage(new ReportOnsitePage());
				// setResponsePage(new HomePage());
			}

		};
		add(btnOnsite);

		// AjaxLink btnStatistics = new AjaxLink("statistics") {
		//
		// @Override
		// public void onClick(AjaxRequestTarget target) {
		// // System.out.println("btnStatistics");
		// // setResponsePage(new ReportOnsitePage1()); // tmp
		// setResponsePage(new HomePage());
		// }
		//
		// };
		// add(btnStatistics);

		if (true) {
			// Active Member Panel
			add(new MemberPanel("reportPanel"));
		} else {
			// No panel
			add(new Label("reportPanel"));
		}

		/*
		 * 
		 * Ekstern: SELECT CAP, EMAIL1, EMAIL2, FIRSTNAME, LASTNAME, NOTES, PAYMENT,
		 * PHONE FROM PUBLIC.MEMBER where ACTIVE=true {and PAYMENT = 'Direkte'}
		 * {order by PAYMENT}
		 * 
		 * 
		 * Vinder:
		 * 
		 * select concat(concat(day(p.start), left(monthname(p.start), 3)),
		 * substring(p.start, 12, 5)) "Start", substring(p.stop, 12, 5) "Stop",
		 * p.name, p.note, m.cap, m.firstname, m.lastname from pullout p left outer
		 * join member m on p.winner_id = m.id where datediff('dd', p.stop,
		 * current_timestamp) < 90 order by p.stop
		 * 
		 * 
		 * 
		 * Tilstede:
		 * 
		 * select p.stop, p.name, p.note, count(pom.onsite) "OnSite" from
		 * pulloutmember pom left outer join pullout p on p.id = pom.pullout_id
		 * where pom.onsite=true and datediff('dd', p.stop, current_timestamp) < 90
		 * group by p.name, p.note, p.stop, pom.pullout_id order by p.stop
		 * 
		 * 
		 * 
		 * Tilstede 2:
		 * 
		 * 
		 * SELECT m.cap ,m.firstname ,m.lastname ,to_char( p.stop, 'YYYY-MM-DD')
		 * "Dato" ,count(m.id) "Antal" FROM PULLOUTMEMBER pm, member m, pullout p
		 * where pm.MEMBER_ID=m.id and pm.PULLOUT_ID =p.id and pm.ONSITE and
		 * to_char( p.stop, 'YYYY')='2012' group by m.cap,m.firstname ,m.lastname
		 * ,"Dato" order by "Dato",m.cap
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * Varighed:
		 * 
		 * select p.*, datediff('mi', p.start, p.stop) "Duration" from pullout p
		 * where datediff('dd', p.stop, current_timestamp) < 90 order by p.stop
		 * 
		 * 
		 * current_date()
		 * 
		 * https://forum.hibernate.org/viewtopic.php?p=2387727
		 * 
		 * 
		 * http://www.techinfopad.com/spring/100210572-detachedcriteria-on-date-column
		 * -type.html highestDate = new Date(); // modify the date, parse it from a
		 * string, bind it from web field or whatever ... DetachedCriteria c =
		 * DetachedCriteria.forClass(CustomTable.class);
		 * c.add(Restrictions.lt("reportDate", highestDate));
		 * 
		 * 
		 * ??? SELECT p.NAME, p.NOTE, p.START, p.STOP, m.cap, m.firstname,
		 * m.lastname FROM PUBLIC.PULLOUT p left outer JOIN member m ON p.winner_id
		 * = m.id order by p.stop
		 * 
		 * 
		 * SELECT pm.ID, pm.ONSITE, pm.PULLEDOUT, pm.MEMBER_ID, pm.PULLOUT_ID,
		 * m.cap, m.firstname, m.lastname FROM PUBLIC.PULLOUTMEMBER pm, member m
		 * where pm.MEMBER_ID=m.id order by PULLOUT_ID, PULLEDOUT
		 * 
		 * 
		 * UPDATE PUBLIC.MEMBER SET EMAIL1 = REPLACE(email1,';','')
		 * 
		 * 
		 * SELECT ID, ONSITE, PULLEDOUT, MEMBER_ID, PULLOUT_ID FROM
		 * PUBLIC.PULLOUTMEMBER where pullout_id=61 and pulledout=(select
		 * max(pulledout) from PULLOUTMEMBER where pullout_id=61)
		 * 
		 * 
		 * 
		 * SELECT limit 0 2 pom.ID, pom.ONSITE, pom.PULLEDOUT, pom.MEMBER_ID,
		 * pom.PULLOUT_ID, m.firstname, m.lastname FROM PUBLIC.PULLOUTMEMBER pom,
		 * member m where pom.MEMBER_ID=m.id and pom.PULLOUT_ID =4 order by
		 * PULLEDOUT desc
		 * 
		 * 
		 * 
		 * SELECT pom.ID, pom.ONSITE, pom.PULLEDOUT, pom.MEMBER_ID, pom.PULLOUT_ID,
		 * m.firstname, m.lastname FROM PUBLIC.PULLOUTMEMBER pom, member m where
		 * pom.MEMBER_ID=m.id and pom.PULLOUT_ID = 1 order by PULLEDOUT desc LIMIT 3
		 * OFFSET 0
		 * 
		 * 
		 * 
		 * Hibernate query.setFirstResult(n); query.setMaxResults(m);
		 */

	}
}
