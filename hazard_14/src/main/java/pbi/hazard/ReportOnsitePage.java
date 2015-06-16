package pbi.hazard;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import pbi.hazard.common.DateTime;
import pbi.hazard.dao.PullOutDAO;
import pbi.hazard.dao.PullOutMemberDAO;
import pbi.hazard.model.Member;
import pbi.hazard.model.OnsitePlaceHolder;
import pbi.hazard.model.PullOut;
import pbi.hazard.model.PullOutMember;
import pbi.hazard.panel.MemberOnsiteDayPanel;

/**
 * PullOutliste
 */
public class ReportOnsitePage extends WebPage {
	private static int MONTHBACK = -13;

	@SpringBean
	private PullOutDAO pullOutDAO;
	@SpringBean
	private PullOutMemberDAO pullOutMemberDAO;

	FeedbackPanel feedbackPanel = new FeedbackPanel("feedback");

	PullOut pullOutModel = new PullOut();
	CompoundPropertyModel<PullOut> filterModel = new CompoundPropertyModel<PullOut>(pullOutModel);
	OnsitePlaceHolder oph = new OnsitePlaceHolder();

	/**
	 * Constructor that is invoked when page is invoked without a session.
	 * 
	 * @param parameters
	 *          Page parameters
	 * 
	 *          final PageParameters parameters
	 * 
	 */
	public ReportOnsitePage() {
		super();

		AjaxLink btnReturn = new AjaxLink("return") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget arg0) {
				SaveOutput(filterModel.getObject().getStart(), filterModel.getObject().getStop());
				setResponsePage(new ReportPage());
			}

		};
		add(btnReturn);

		feedbackPanel.setOutputMarkupId(true);
		add(feedbackPanel);

		Form<PullOut> form = new Form<PullOut>("form", filterModel) {
			private static final long serialVersionUID = 1L;
			{
				// System.out.println("Form");
				/*
				 * Filter
				 */
				resetFilter();

				DateTextField dateTextStart = new DateTextField("dateTextStart", filterModel.<Date> bind("start"),
						DateTime.searchFormat);
				add(dateTextStart);
				DatePicker datePickerStart = new DatePicker();
				// datePickerStart.setShowOnFieldClick(true);
				dateTextStart.add(datePickerStart);

				DateTextField dateTextStop = new DateTextField("dateTextStop", filterModel.<Date> bind("stop"),
						DateTime.searchFormat);
				add(dateTextStop);
				DatePicker datePickerStop = new DatePicker();
				dateTextStop.add(datePickerStop);

				// AjaxFormComponentUpdatingBehavior changeStartBinded = new
				// AjaxFormComponentUpdatingBehavior("onchange") {
				// private static final long serialVersionUID = 1L;
				//
				// @Override
				// protected void onUpdate(AjaxRequestTarget target) {
				// target.addComponent(dateTextStart);
				// //
				// System.out.println("changed: "+filterModel.getObject().getStart());
				// changeStopFilter();
				// dateTextStop.setModelValue(new
				// String[]{DateTime.formatDateSearch(filterModel.getObject().getStop())});
				// }
				// };
				// dateTextStart.add(changeStartBinded);

				// Dates
				IModel<List<Date>> colsList = new LoadableDetachableModel<List<Date>>() {
					private static final long serialVersionUID = 1L;

					@Override
					protected List<Date> load() {
						/*
						 * List
						 */
						List<PullOut> listPullOut = pullOutDAO.getPullOutWinner(filterModel.getObject().getStart(), filterModel
								.getObject().getStop());
						// SysOutData(listPullOut);

						// oph.setPullOutDay((Date) col.getModelObject());
						List<PullOut> fullList = new ArrayList<PullOut>();
						fullList.add(null);
						fullList.addAll(listPullOut);

						oph.setPullOuts(fullList);

						oph.setOnsiteMembers(pullOutMemberDAO.getMemberList(listPullOut));

						List<Date> dateList = new ArrayList<Date>();
						dateList.add(null); // Header col
						if (listPullOut.size() > 0) {
							Date current = new Date(0); // Very old date
							for (PullOut pullOut : listPullOut) {
								// System.out.println("Date: "+DateTime.formatSqlDate(current)+"\t"+DateTime.formatSqlDate(pullOut.getStart())+"\t"+DateTime.formatSqlDate(pullOut.getStop()));
								// System.out.println("\t"+DateTime.compareDates(current,
								// pullOut.getStart())+"\t"+DateTime.compareDates(current,
								// pullOut.getStop())+"\t"+DateTime.compareDates(pullOut.getStart(),
								// pullOut.getStop()));
								if (DateTime.compareDates(current, pullOut.getStart()) != 0) {
									current = pullOut.getStart();
									dateList.add(DateTime.resetTime(current).getTime());
								}
							}
						}
						return dateList;
					}
				};

				add(new ListView<Date>("cols", colsList) {
					private static final long serialVersionUID = 1L;

					public void populateItem(ListItem<Date> col) {
						// if (col.getModelObject() == null) {
						// oph.setHeader(true);
						// } else {
						// oph.setHeader(false);
						// }
						// panel: http://osdir.com/ml/java.wicket.user/2005-08/msg00932.html
						// System.out.println("  >" + oph.getActualPullOut());
						// System.out.println("  >" + col.getModelObject());
						col.add(new MemberOnsiteDayPanel("panel", oph));
						// if (oph.hasMorePullOuts()) {
						// oph.getNextPullOut();
						// }
					}
				});

			}

			@Override
			protected void onSubmit() {
				// Show();
				// System.out.println("Submit");
				// System.out.println(filterModel.getObject().getStart());
				// System.out.println(filterModel.getObject().getStop());
				super.onSubmit();
			}

			private void resetFilter() {
				// System.out.println("resetFilter");
				Calendar c = Calendar.getInstance();
				c.add(Calendar.MONTH, MONTHBACK);
				filterModel.getObject().setStart(c.getTime());
				filterModel.getObject().setStop(new Date());
			}

			// /**
			// * Start and Stop dates must be within MONTHBACK diff
			// */
			// private void changeStopFilter() {
			// Calendar c = Calendar.getInstance();
			// c.setTime(filterModel.getObject().getStart());
			// c.add(Calendar.MONTH, -MONTHBACK);
			// System.out.println(filterModel.getObject().getStop());
			// filterModel.getObject().setStop(c.getTime());
			// System.out.println(filterModel.getObject().getStop());
			// }

		};
		add(form);

	}

	/*
	 * SQL
	 * 
	 * SELECT * FROM PULLOUT where winner_id is not null and start > '2013-01-01'
	 * and stop < '2013-12-12' order by id
	 * 
	 * 
	 * SELECT * FROM PULLOUTMEMBER where PULLOUT_ID in (50,51,53,56,58,60) order
	 * by member_id, pullout_id
	 * 
	 * 
	 * SELECT pom.*,p.* FROM PULLOUTMEMBER pom, PULLOUT p where
	 * pom.pullout_id=p.id and PULLOUT_ID in (50,51,53,56,58,60)
	 * 
	 * 
	 * SELECT * FROM PULLOUTMEMBER where onsite and pulledout > '2013-01-01' and
	 * pulledout < '2013-12-12' order by member_id, pullout_id
	 * 
	 * 
	 * SELECT distinct member_id FROM PULLOUTMEMBER where onsite and PULLOUT_ID in
	 * (50,51,53,56,58,60) order by member_id
	 * 
	 * 
	 * SELECT * FROM PULLOUTMEMBER where member_id in (39,98,114,133,191,219) and
	 * pulledout > '2013-01-01' and pulledout < '2013-12-12' order by
	 * member_id,pullout_id
	 * 
	 * 
	 * SELECT * FROM PULLOUTMEMBER where member_id in (39,98,114,133,191,219) and
	 * PULLOUT_ID in (50,51,53,56,58,60) order by member_id,pullout_id
	 */

	// private void UpdatePanel() {
	// System.out.println("UpdatePanel");
	// };

	protected void SysOutData(List<PullOut> listPullOut) {
		System.out.println("SysOutData");
		// System.out.println(listPullOut.size());
		if (listPullOut == null) {
			System.out.println("Warning: No Data");
		} else {
			Date date1 = new Date(); // Now
			for (PullOut pullOut : listPullOut) {
				if (DateTime.compareDates(date1, pullOut.getStart()) != 0) {
					date1 = pullOut.getStart();
					System.out.print(DateTime.formatDateSearch(date1) + "\t");
				}
			}
			System.out.println();

			PullOut firstPullOut = new PullOut(); // null
			for (PullOut pullOut : listPullOut) {
				// Same date?
				if (DateTime.compareDates(firstPullOut.getStart(), pullOut.getStart()) != 0) {
					firstPullOut = pullOut;
					System.out.print("\t");
				}
				System.out.print(pullOut.getId() - firstPullOut.getId() + 1 + " ");
			}
			// System.out.println();

			firstPullOut = new PullOut(); // null
			Member m = new Member(); // null
			List<PullOutMember> listMembers = pullOutMemberDAO.getOnsiteList(listPullOut);
			for (PullOutMember pom : listMembers) {
				if (DateTime.compareDates(firstPullOut.getStart(), pom.getPullOut().getStart()) != 0) {
					firstPullOut = pom.getPullOut();
					System.out.print("\t");
				}
				if (m != pom.getMember()) {
					m = pom.getMember();
					System.out.print(System.getProperty("line.separator") + m.getCap() + " " + m.returnName() + "\t");
				}
				if (pom.getOnsite()) {
					System.out.print("1 ");
				} else {
					System.out.print("0 ");
				}
			}
			System.out.println();
			// System.out.println(listMembers.size());

		}
		/*
		 * Dato. 24-02-2012 20-04-2012 15-06-2012 07-09-2012 23-02-2013
		 * 06-04-2013<br> Træk. 1 2 3 4 5 1 2 3 4 5 1 2 3 4 5 1 2 3 5 6 1 2 4 1 3 5
		 * <br> 17 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 0 0 0 0 0 0 <br> 25 1 1 1
		 * 1 1 1 1 1 1 1 0 0 0 0 0 1 1 1 1 1 0 0 0 0 0 0 <br> 36 0 0 0 0 0 0 0 0 0 0
		 * 1 1 1 1 1 0 0 0 0 0 0 0 0 0 0 0 <br> 40 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 0 0
		 * 0 0 0 1 0 0 0 0 0 <br> 51 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 0 0 0 0
		 * 0 0 <br> 59 0 0 0 0 0 1 1 1 1 1 0 1 1 1 1 1 1 1 1 1 0 0 0 0 0 0 <br> 64 1
		 * 1 1 1 1 0 1 1 1 1 1 1 1 1 1 0 0 0 0 0 0 0 0 0 0 0 <br> 70 1 1 1 1 1 0 0 0
		 * 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 <br> 99 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
		 * 0 0 0 0 0 0 1 1 1 1 1 <br> 115 0 0 0 0 0 0 0 0 0 0 0 1 1 1 1 0 0 0 0 0 1
		 * 0 0 0 0 0 <br> 123 0 0 0 0 0 0 0 0 0 0 0 1 1 1 1 0 0 0 0 0 0 0 0 0 0 0
		 * <br> 134 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1 0 0 0 0 0 <br> 137 1 1
		 * 1 1 1 0 0 0 0 0 1 1 1 1 1 0 1 1 1 1 0 0 0 0 0 0 <br> 150 0 0 0 0 0 0 0 0
		 * 0 1 1 1 1 1 1 0 0 0 0 0 0 0 0 0 0 0 <br> 151 0 0 0 0 0 0 0 0 0 0 0 1 1 1
		 * 1 0 0 0 0 0 0 0 0 0 0 0 <br> 158 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1
		 * 0 0 0 0 0 0 <br> 165 0 0 0 0 0 0 0 0 0 0 1 1 1 1 1 0 0 0 0 0 0 0 0 0 0 0
		 * <br> 169 1 1 0 0 0 1 1 1 0 0 0 1 1 1 1 0 0 0 0 0 0 0 0 0 0 0 <br> 175 1 1
		 * 1 1 1 0 0 0 0 0 0 0 0 0 0 1 1 1 1 1 0 0 0 0 0 0 <br> 178 0 0 0 0 0 0 0 0
		 * 0 0 0 0 0 0 1 0 0 0 0 0 0 0 0 0 0 0 <br> 179 0 0 0 0 0 0 0 0 0 0 1 1 1 1
		 * 1 0 0 0 0 0 0 0 0 0 0 0 <br> 185 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 0 0 0 0 0
		 * 0 0 0 0 0 0 <br> 193 0 0 0 0 0 0 0 0 0 0 1 1 1 1 1 0 0 0 0 0 0 0 0 0 0 0
		 * <br> 194 1 1 1 1 1 0 0 0 0 0 0 0 0 0 0 1 1 1 1 1 1 0 0 0 0 0 <br> 195 0 0
		 * 0 0 0 0 0 0 0 0 1 1 1 1 1 0 0 0 0 0 0 0 0 0 0 0 <br> 196 0 0 0 0 0 0 0 0
		 * 0 0 1 1 1 1 1 0 0 0 0 0 0 0 0 0 0 0 <br> 202 0 0 0 0 0 0 0 0 0 0 1 1 1 1
		 * 1 1 1 1 1 1 0 0 0 0 0 0 <br> 207 1 1 1 1 1 0 0 0 0 0 1 1 1 1 1 0 0 0 0 0
		 * 0 0 0 0 0 0 <br> 219 1 1 1 1 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
		 * <br> 220 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1 1 1 1 1 0 0 0 0 0 0 <br> 222 0 0
		 * 0 0 0 1 1 1 1 1 1 1 1 1 1 0 0 0 0 0 1 0 0 0 0 0 <br> 226 1 1 1 1 1 0 0 0
		 * 0 0 1 1 1 1 1 1 1 1 1 1 0 0 0 0 0 0 <br> 228 0 0 0 0 0 1 1 1 1 1 1 1 1 1
		 * 1 0 0 0 0 0 0 0 0 0 0 0
		 */
	};

	private void SaveOutput(Date start, Date stop) {
		// System.out.println("Generate output files!");
		// System.out.println("### Start: "+start+" Stop: "+stop);
		// CsvFile csvFile = new CsvFile(); // Avoid static error

		// // Winner
		// // List<PullOut> pullOutList = pullOutDAO.getPullOut();
		// List<PullOut> pullOutList = pullOutDAO.getPullOut(start, stop);
		//
		// csvFile.CsvWinner(pullOutList, pullOutDAO);
	}
}
