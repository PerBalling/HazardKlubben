package pbi.hazard;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import pbi.hazard.common.CsvFile;
import pbi.hazard.common.DateTime;
import pbi.hazard.dao.PullOutDAO;
import pbi.hazard.model.Member;
import pbi.hazard.model.PullOut;
import pbi.hazard.model.PullOutMember;

/**
 * PullOutliste
 */
public class PullOutListPage extends WebPage {

	@SpringBean
	private PullOutDAO pullOutDAO;

	/**
	 * Constructor that is invoked when page is invoked without a session.
	 * 
	 * @param parameters
	 *          Page parameters
	 * 
	 *          final PageParameters parameters
	 * 
	 */
	public PullOutListPage() {

		PullOut pullOut = new PullOut();
		CompoundPropertyModel<PullOut> cmodel = new CompoundPropertyModel<PullOut>(pullOut);

		WicketApplication.initVars();

		Form<PullOut> form = new Form<PullOut>("form", cmodel) {
			{

				LoadableDetachableModel<List<PullOut>> lmodel = new LoadableDetachableModel<List<PullOut>>() {

					@Override
					protected List<PullOut> load() {
						return pullOutDAO.getPullOut();
					}
				};

				ListView<PullOut> listView = new ListView<PullOut>("rows", lmodel) {

					@Override
					protected void populateItem(ListItem<PullOut> listItem) {
						final PullOut pullOut = listItem.getModelObject();
						listItem.add(new Label("name", pullOut.getName()));
						listItem.add(new Label("start", DateTime.formatDate(pullOut.getStart())));
						listItem.add(new Label("stop", DateTime.formatDate(pullOut.getStop())));
						listItem.add(new Label("winner", (pullOut.getWinner() == null ? "" : pullOut.getWinner().returnName())));
						listItem.add(new Label("note", pullOut.getNote()));

						AjaxLink btnChange = new AjaxLink<Object>("change") {
							@Override
							public void onClick(AjaxRequestTarget arg0) {
								setResponsePage(new CreatePullOutPage(pullOut, PullOutListPage.this));
							}
						};
						// if (pullOut.getStop() != null) { // Finished
						// btnChange.add(new SimpleAttributeModifier("disabled",
						// "disabled"));
						// }
						listItem.add(btnChange);

						AjaxLink btnShow = new AjaxLink<Object>("show") {
							@Override
							public void onClick(AjaxRequestTarget arg0) {
								setResponsePage(new PullOutShowList(pullOut, PullOutListPage.this));
								// System.out.println("Vis");
							}
						};
						// if (pullOut.getStop() != null) { // Finished
						// btnShow.add(new SimpleAttributeModifier("disabled", "disabled"));
						// }
						listItem.add(btnShow);

						if (pullOut.getStop() != null) { // Finished
							Label lblSelect = new Label("select", "-");
							lblSelect.add(new SimpleAttributeModifier("hidden", "hidden")); // disabled
							listItem.add(lblSelect);
						}
						else {
							AjaxLink btnSelect = new AjaxLink<Object>("select") {
								@Override
								public void onClick(AjaxRequestTarget arg0) {
									setResponsePage(new PullOutPage(pullOut));

								}
							};
							listItem.add(btnSelect);
						}

						// See Class ListItem<T> - Direct Known Subclasses: OddEvenListItem
						if (listItem.getIndex() % 2 == 0) {
							listItem.add(new SimpleAttributeModifier("class", "even"));
						} else {
							listItem.add(new SimpleAttributeModifier("class", "odd"));
						}
					}
				};
				add(listView);

				AjaxLink<Object> btnNew = new AjaxLink<Object>("new") {

					@Override
					public void onClick(AjaxRequestTarget arg0) {
						setResponsePage(new CreatePullOutPage(new PullOut(), PullOutListPage.this));
					}

				};
				add(btnNew);

				AjaxLink btnReturn = new AjaxLink("return") {

					@Override
					public void onClick(AjaxRequestTarget arg0) {
						// Perhaps use history.back()
						setResponsePage(new HomePage());
					}

				};
				add(btnReturn);

			}
		};
		add(form);

		// System.out.println("SaveOutput()");
		SaveOutput();
	}

	private void SaveOutput() {
		// System.out.println("Generate output files!");
		CsvFile csvFile = new CsvFile(); // Avoid static error

		// Winner
		List<PullOut> pullOutList = pullOutDAO.getPullOut();
		// logger.info(pullOut);
		csvFile.CsvWinner(pullOutList, pullOutDAO);
	}

}
