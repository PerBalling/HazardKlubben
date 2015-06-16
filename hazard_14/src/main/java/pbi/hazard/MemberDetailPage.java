package pbi.hazard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.StringValidator;
import pbi.hazard.common.SetFocusBehavior;
import pbi.hazard.dao.MemberDAO;
import pbi.hazard.dao.PullOutDAO;
import pbi.hazard.model.Member;
import pbi.hazard.panel.MemberWonPanel;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class MemberDetailPage extends WebPage {

// 20121006: Dirty flag removed. See zip archive to restore
	
	@SpringBean
	private MemberDAO memberDAO;
	@SpringBean
	private PullOutDAO pullOutDAO;
//	@Autowired
//	private SessionFactory sessionFactory;

	// 20121006: MemberString removed. Back button - validate data; Not fully implemented. 
	// 20121006: See zip file archive to restore.
	
	// ObjectCloner objectCloner = new ObjectCloner(); // Avoid error: Cannot

	// public MemberDetailPage(Member member, final Page callingPage) {
	public MemberDetailPage(Member member) {
		final CompoundPropertyModel<Member> memberModel = new CompoundPropertyModel<Member>(member);
		// make a static reference to the non-static method

		if (member.getId() == null) {
			// New Member
			member.setCap(memberDAO.getMaxCap() + 1);
			member.setActive(true);
		} else {
			// Use Member from DB
			// (could have been changed after calling page was loaded)
			memberModel.setObject(memberDAO.getMemberById(member.getId()));
		}

		/*
		 * List<PullOut> Wins = pullOutDAO.findWhenWon(member); //
		 * System.out.println("Wins: "+Wins.size()); for (Iterator<PullOut> iterator
		 * = Wins.iterator(); iterator.hasNext();) { PullOut pullOut = (PullOut)
		 * iterator.next(); //
		 * System.out.println("Date: "+DateTime.formatDate(pullOut.getStop())); }
		 */

		
		// TODO Change page header Create or Change Member
		Form<Member> form = new Form<Member>("form", memberModel) {
			{
				// See:
				// http://wicketstuff.org/wicket/forminput/wicket/bookmarkable/org.apache.wicket.examples.forminput.FormInput?2

				Button btnReturn = new Button("return");
				btnReturn.setDefaultFormProcessing(false);
				// One other option you should know of is the 'defaultFormProcessing'
				// property of Button components. When you set this to false (default is
				// true), all validation and formupdating is bypassed and the onSubmit
				// method of that button is called directly, and the onSubmit method of
				// the parent form is not called. A common use for this is to create a
				// cancel button.
				btnReturn.add(new AjaxEventBehavior("onclick") {

					@Override
					protected void onEvent(AjaxRequestTarget target) {
							setResponsePage(new MemberList());
					}
				});
				add(btnReturn);

				Label memberCap = new Label("cap", memberModel.<Integer> bind("cap"));
				add(memberCap);

				TextField<String> memberFirstName = new TextField<String>("firstname", memberModel.<String> bind("firstName"));
				// setLabel used when setRequired/Validator raises warning
				memberFirstName.setLabel(new Model<String>("Fornavn"));
				memberFirstName.setRequired(true);
				memberFirstName.add(StringValidator.minimumLength(2));
				add(memberFirstName);

				TextField<String> memberLastName = new TextField<String>("lastname", memberModel.<String> bind("lastName"));
				memberLastName.setLabel(new Model<String>("Efternavn"));
				memberLastName.add(StringValidator.minimumLength(2));
				add(memberLastName);

				TextField<String> memberNickName = new TextField<String>("nickname", memberModel.<String> bind("nickName"));
//				memberNickName.add(createAjax());
				add(memberNickName);

				TextField<String> memberEmail1 = new TextField<String>("email1", memberModel.<String> bind("email1"));
				memberEmail1.setLabel(new Model<String>("Email1"));
				memberEmail1.add(EmailAddressValidator.getInstance());
				add(memberEmail1);

				TextField<String> memberEmail2 = new TextField<String>("email2", memberModel.<String> bind("email2"));
				memberEmail2.setLabel(new Model<String>("Email2"));
				memberEmail2.add(EmailAddressValidator.getInstance());
				add(memberEmail2);

				TextField<String> memberPhone = new TextField<String>("phone", memberModel.<String> bind("phone"));
				add(memberPhone);

				TextField<String> memberContactId = new TextField<String>("contactid", memberModel.<String> bind("contactId"));
//				memberContactId.setLabel(new Model<String>("Lønnr"));
				memberContactId.setLabel(new Model<String>("L\u00f8nnr"));
				memberContactId.setRequired(true);
				add(memberContactId);

				TextField<String> memberNotes = new TextField<String>("notes", memberModel.<String> bind("notes"));
				add(memberNotes);

				final List<String> dbChoices = memberDAO.getPaymentList();
				// System.out.println(dbChoices);
				// [-, 0, 24697882, CSC, Direkte, Fejl, TDC, Via Susan]

				// http://www.wicket-library.com/wicket-examples/ajax/autocomplete

				AutoCompleteTextField<String> memberPayment = new AutoCompleteTextField<String>("payment",
						memberModel.<String> bind("payment")) {

					@Override
					protected Iterator<String> getChoices(String input) {
						if (Strings.isEmpty(input)) {
							List<String> emptyList = Collections.emptyList();
							return emptyList.iterator();
						}

						List<String> choices = new ArrayList<String>();
						for (Iterator<String> iterator = dbChoices.iterator(); iterator.hasNext();) {
							String strChoice = (String) iterator.next();

							if (strChoice.toUpperCase().startsWith(input.toUpperCase()) || input.matches(".")) {
								// Matches one character or startsWith input
								choices.add(strChoice);
							}
						}

						return choices.iterator();
					}
				};

				// TextField<String> memberPayment = new TextField<String>("payment",
				// memberModel.<String> bind("payment"));
				memberPayment.setLabel(new Model<String>("Betaling"));
				memberPayment.setRequired(true);
				add(memberPayment);

				add(new CheckBox("active", memberModel.<Boolean> bind("active")));

				// AjaxLink<Object> returnbutton = new AjaxLink<Object>("return") {
				// @Override
				// public void onClick(AjaxRequestTarget arg0) {
				// // Perhaps use history.back()
				// setResponsePage(new MemberList());
				// }
				// };
				// add(returnbutton);

			}

			@Override
			protected void onSubmit() {
//				System.out.println("Form:onSubmit");
				memberDAO.saveMember(getModelObject());
				setResponsePage(new MemberList());
			}

		};
		add(form);
		form.add(new SetFocusBehavior(form));
		// AjaxFormValidatingBehavior.addToAllFormComponents(form,"onblur");
		// AjaxFormComponentUpdatingBehavior("onblur");

		add(new MemberWonPanel("memberWonPanel", memberModel));

		FeedbackPanel feedbackPanel = new FeedbackPanel("feedback");
		feedbackPanel.setOutputMarkupId(true);
		add(feedbackPanel);

	}

//	private AjaxFormComponentUpdatingBehavior createAjax() {
//		return new AjaxFormComponentUpdatingBehavior("onchange") {
//			@Override
//			protected void onUpdate(AjaxRequestTarget target) {
//				// do your task
//				System.out.println("AjaxFormComponentUpdatingBehavior");
//				Dirty = true;
//			}
//		};
//	}

}
