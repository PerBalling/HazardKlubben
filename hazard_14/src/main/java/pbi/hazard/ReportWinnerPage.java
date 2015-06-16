package pbi.hazard;

import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MILLISECOND;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.SECOND;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DatePicker;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import pbi.hazard.common.CsvFile;
import pbi.hazard.common.DateTime;
import pbi.hazard.dao.PullOutDAO;
import pbi.hazard.model.PullOut;

/**
 * PullOutliste
 */
public class ReportWinnerPage extends WebPage {
	// private static String DATEFORMAT = "dd-MM-yyyy";
	// int count=0; See structure here: 'Hazard Trækningsliste'

	@SpringBean
	private PullOutDAO pullOutDAO;

	FeedbackPanel feedbackPanel = new FeedbackPanel("feedback");

	/**
	 * Constructor that is invoked when page is invoked without a session.
	 * 
	 * @param parameters
	 *          Page parameters
	 * 
	 *          final PageParameters parameters
	 * 
	 */
	public ReportWinnerPage() {
		super();

		PullOut pullOut = new PullOut();
		// CompoundPropertyModel<PullOut> cmodel = new
		// CompoundPropertyModel<PullOut>(pullOut);
		final CompoundPropertyModel<PullOut> filterModel = new CompoundPropertyModel<PullOut>(pullOut);

		// WicketApplication.initVars();

		AjaxLink btnReturn = new AjaxLink("return") {

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
			{
				Button btnReset = new Button("reset") {
					public void onSubmit() {
						resetFilter();
						// System.out.println(">>> btnReset.Submit");
					}
				};
				add(btnReset);

				LoadableDetachableModel<List<PullOut>> lmodel = new LoadableDetachableModel<List<PullOut>>() {

					@Override
					protected List<PullOut> load() {
						List<PullOut> listPo = pullOutDAO.getPullOut(filterModel.getObject().getStart(), filterModel.getObject()
								.getStop());
						// count=listPo.size();
						// System.out.println(count);
						return listPo;
					}
				};

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

				ListView<PullOut> listView = new ListView<PullOut>("rows", lmodel) {

					@Override
					protected void populateItem(ListItem<PullOut> listItem) {
						final PullOut pullOut = listItem.getModelObject();
						listItem.add(new Label("name", pullOut.getName()));
						listItem.add(new Label("start", DateTime.formatDate(pullOut.getStart())));
						listItem.add(new Label("stop", DateTime.formatDate(pullOut.getStop())));
						listItem.add(new Label("note", pullOut.getNote()));
						listItem.add(new Label("cap", (pullOut.getWinner() == null ? "" : String.valueOf(pullOut.getWinner()
								.getCap()))));
						listItem.add(new Label("winner", (pullOut.getWinner() == null ? "" : pullOut.getWinner().returnName())));

						// AjaxLink btnChange = new AjaxLink<Object>("change") {
						// @Override
						// public void onClick(AjaxRequestTarget arg0) {
						// setResponsePage(new CreatePullOutPage(pullOut));
						// }
						// };
						// if (pullOut.getStop() != null) { // Finished
						// btnChange.add(new SimpleAttributeModifier("disabled",
						// "disabled"));
						// }
						// listItem.add(btnChange);

						// AjaxLink btnSelect = new AjaxLink<Object>("select") {
						// @Override
						// public void onClick(AjaxRequestTarget arg0) {
						// setResponsePage(new PullOutPage(pullOut));
						//
						// }
						// };
						// if (pullOut.getStop() != null) { // Finished
						// btnSelect.add(new SimpleAttributeModifier("disabled",
						// "disabled"));
						// }
						// listItem.add(btnSelect);

						AjaxLink<Object> btnShow = new AjaxLink<Object>("show") {
							@Override
							public void onClick(AjaxRequestTarget arg0) {
								setResponsePage(new PullOutShowList(pullOut, ReportWinnerPage.this));
								// System.out.println("Vis");
							}
						};
						// if (pullOut.getStop() != null) { // Finished
						// btnShow.add(new SimpleAttributeModifier("disabled", "disabled"));
						// }
						listItem.add(btnShow);

						// See Class ListItem<T> - Direct Known Subclasses: OddEvenListItem
						if (listItem.getIndex() % 2 == 0) {
							listItem.add(new SimpleAttributeModifier("class", "even"));
						} else {
							listItem.add(new SimpleAttributeModifier("class", "odd"));
						}
					}
				};
				add(listView);

				// AjaxLink<Object> btnNew = new AjaxLink<Object>("new") {
				//
				// @Override
				// public void onClick(AjaxRequestTarget arg0) {
				// setResponsePage(new CreatePullOutPage(new PullOut()));
				// }
				//
				// };
				// add(btnNew);
			}

			@Override
			protected void onSubmit() {
				// System.out.println(">>> Form.Submit");
				super.onSubmit();
			}

			private void resetFilter() {
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.MONTH, -6); // 6 month ago
				cal.set(HOUR_OF_DAY, 0);
				cal.set(MINUTE, 0);
				cal.set(SECOND, 0);
				cal.set(MILLISECOND, 0);
				filterModel.getObject().setStart(cal.getTime());

				cal.setTime(new Date());
				cal.add(Calendar.DAY_OF_MONTH, +1); // Tomorrow
				cal.set(HOUR_OF_DAY, 0);
				cal.set(MINUTE, 0);
				cal.set(SECOND, 0);
				cal.set(MILLISECOND, 0);
				filterModel.getObject().setStop(cal.getTime());
			}

		};
		add(form);

	}

	private void SaveOutput(Date start, Date stop) {
		// System.out.println("Generate output files!");
		// System.out.println("### Start: "+start+" Stop: "+stop);
		CsvFile csvFile = new CsvFile(); // Avoid static error

		// Winner
		// List<PullOut> pullOutList = pullOutDAO.getPullOut();
		List<PullOut> pullOutList = pullOutDAO.getPullOut(start, stop);

		csvFile.CsvWinner(pullOutList, pullOutDAO);
	}
}
