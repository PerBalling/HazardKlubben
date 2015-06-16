package pbi.hazard;

import java.util.Date;
import java.util.Calendar;

import org.apache.log4j.Logger;
import org.apache.wicket.Page;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;

import pbi.hazard.common.DefaultFocusBehavior;
import pbi.hazard.dao.PullOutDAO;
import pbi.hazard.dao.PullOutMemberDAO;
import pbi.hazard.model.PullOut;
import pbi.hazard.panel.MemberPanel;

/**
 * New PullOut
 */
public class CreatePullOutPage extends WebPage {
	private final static String PULLOUTNAME = "Trækning ";
	private static Logger logger = WicketApplication.getLogger();

	@SpringBean
	private PullOutDAO pullOutDAO;
	@SpringBean
	private PullOutMemberDAO pullOutMemberDAO;

	private PullOut pullOut;
	private Boolean newPullOut = true;
	private Boolean copyMembers = false;
	private PullOut pullOutCopy;
	// private String lblpullOutCopy = new String();

	FeedbackPanel feedbackPanel = new FeedbackPanel("feedback");

	/**
	 * @param callingPullOut
	 *          Either create new PullOut or change existing PullOut
	 */
	public CreatePullOutPage(PullOut callingPullOut, final Page callingPage) {

		pullOut = callingPullOut;
		// pullOutCopy = pullOutDao.getLastClosed();
		pullOutCopy = pullOutDAO.getNewestPullOut();
		// System.out.println(pullOutCopy);
		// logger.info(pullOutDao.getNewestPullOut());
		// PullOut pullOutCopy2 = pullOutDao.getNewestPullOut();
		// System.err.println(pullOutCopy2);
		Calendar copyStart = Calendar.getInstance();
		Calendar initStart = Calendar.getInstance();
		initStart.add(Calendar.MONTH, -13);
		copyStart.setTime((pullOutCopy == null ? initStart.getTime() : pullOutCopy.getStart()));
		Calendar now = Calendar.getInstance();

		// Always copy members if new pullout on the same day
		if (now.getTimeInMillis() - copyStart.getTimeInMillis() < WicketApplication.getTimeSinceLastPullout()) {
			copyMembers = true;
		}

		if (pullOut.getId() == null) { // New
			pullOut.setName(PULLOUTNAME + String.valueOf(pullOutDAO.startedToday() + 1));
			pullOut.setStart(new Date());
		} else { // Change
			newPullOut = false;
		}

		Label lblHead = new Label("head", newPullOut ? "Opret" : "Ret");
		add(lblHead);

		final CompoundPropertyModel<PullOut> poModel = new CompoundPropertyModel<PullOut>(pullOut);

		Form<PullOut> form = new Form<PullOut>("form", poModel) {
			{
				feedbackPanel.setOutputMarkupId(true);
				add(feedbackPanel);

				TextField<String> txtName = new TextField<String>("name", poModel.<String> bind("name"));
				txtName.setLabel(new Model<String>("Navn"));
				txtName.setRequired(true);
				txtName.add(new StringValidator.MaximumLengthValidator(20));
				add(txtName);

				// add(new Label("save", newPullOut ? "Opret" : " Gem "));

				/*
				 * http://wicketstuff.org/wicket13/compref/?wicket:bookmarkablePage=:org
				 * .apache.wicket.examples.compref.ButtonPage. When you add a Wicket
				 * Button to a form, and that button is clicked, by default the button's
				 * onSubmit method is called first, and after that the form's onSubmit
				 * button is called.
				 */
				// Button copyButton = new Button("copy") {
				// public void onSubmit() {
				// copyMembers = true;
				// }
				// };
				// add(copyButton);

				// if (pullOutCopy.getId() == null || !newPullOut) {
				// copyButton.add(new SimpleAttributeModifier("disabled", "disabled"));
				// lblpullOutCopy = "";
				// } else {
				// lblpullOutCopy = pullOutCopy.getName() + " [" +
				// DateTime.formatTime(pullOutCopy.getStop()) + "]";
				// }
				//
				// Label lblLatestPullOut = new Label("today", lblpullOutCopy);
				// add(lblLatestPullOut);

				// Mark for copy members
				Label lblCopyPullOut = new Label("copy", copyMembers ? "*" : "");
				add(lblCopyPullOut);

				TextField<String> txtNote = new TextField<String>("note", poModel.<String> bind("note"));
				add(txtNote);

				if (newPullOut || pullOut.getStop() == null) {
					txtNote.add(new SimpleAttributeModifier("disabled", "disabled"));
					txtName.add(new DefaultFocusBehavior());
				} else {
					txtNote.add(new DefaultFocusBehavior());
				}
			}

			@Override
			protected void onSubmit() {
				savePullout();

				if (newPullOut) {
					setResponsePage(new PullOutPage(pullOut)); // Start PullOut
				} else {
					// setResponsePage(new PullOutListPage()); // Return to list
					setResponsePage(callingPage); // Return to caller
				}
			}

		};
		add(form);

		if (newPullOut) {
			// Active Member Panel
			add(new MemberPanel("memberPanel"));
		} else {
			// No panel
			add(new Label("memberPanel"));
		}
		// End of Page layout
	}

	private void savePullout() {
		logger.info("- - - - - - - - - - - -");
		logger.info("New Pullout: " + pullOut.getName());
		logger.info("- - - - - - - - - - - -");
		pullOutDAO.savePullOut(pullOut);

		// Link Members to new PullOut
		if (newPullOut) {
			if (copyMembers) {
				// Link the same members as used for the last closed PullOut
				// Re-use Onsite attr.
				pullOutMemberDAO.savePullOutCopyMembers(pullOut, pullOutCopy);
			} else {
				pullOutMemberDAO.savePullOutMembers(pullOut);
			}
		}
	}

}