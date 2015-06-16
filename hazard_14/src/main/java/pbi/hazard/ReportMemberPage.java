package pbi.hazard;

import java.util.ArrayList;
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
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.visural.wicket.behavior.beautytips.BeautyTipBehavior;

import pbi.hazard.common.CsvFile;
import pbi.hazard.common.DateTime;
import pbi.hazard.dao.MemberDAO;
import pbi.hazard.dao.PullOutDAO;
import pbi.hazard.model.Member;

/*
 * EXCEL - See: http://www.javabeat.net/articles/310-displaying-data-using-datatable-in-apache-wicket-3.html
 * Simple: http://www.mkyong.com/java/how-to-export-data-to-csv-file-java/
 */

public class ReportMemberPage extends WebPage {

	FeedbackPanel feedbackPanel = new FeedbackPanel("feedback");
	private Model<Integer> countModel = new Model<Integer>();

	@SpringBean
	private MemberDAO memberDAO;
	@SpringBean
	private PullOutDAO pullOutDAO;

	// final
	Member member = new Member();
	// final
	CompoundPropertyModel<Member> filterModel = new CompoundPropertyModel<Member>(member);

	public ReportMemberPage() {
		super();

		AjaxLink btnReturn = new AjaxLink("return") {

			@Override
			public void onClick(AjaxRequestTarget arg0) {
				setResponsePage(new ReportPage());
			}
		};
		add(btnReturn);

		feedbackPanel.setOutputMarkupId(true);
		add(feedbackPanel);

		Form<Member> form = new Form<Member>("form", filterModel) {
			{
				TextField<String> filterCap = new TextField<String>("capFilter", filterModel.<String> bind("cap"));
				add(filterCap);

				TextField<String> filterFirstName = new TextField<String>("firstnameFilter",
						filterModel.<String> bind("firstName"));
				add(filterFirstName);

				TextField<String> filterLastName = new TextField<String>("lastnameFilter",
						filterModel.<String> bind("lastName"));
				add(filterLastName);

				TextField<String> filterMail = new TextField<String>("mailFilter", filterModel.<String> bind("email1"));
				add(filterMail);

				TextField<String> filtercontactId = new TextField<String>("contactIdFilter",
						filterModel.<String> bind("contactId"));
				add(filtercontactId);

				resetFilter(); // TODO: Why here?
				add(new CheckBox("activeFilter", filterModel.<Boolean> bind("active")));

				ArrayList<String> paymentList = new ArrayList<String>();
				// al.add("");
				paymentList.add(null);
				paymentList.addAll(memberDAO.getPaymentList());
				DropDownChoice<String> ddc = new DropDownChoice<String>("paymentFilter", filterModel.<String> bind("payment"),
						paymentList);
				// filterModel.getObject().setPayment(null);//Default to null
				add(ddc);

				DateTextField dateTextStop = new DateTextField("dateFilter", filterModel.<Date> bind("updated"),
						DateTime.searchFormat);
				add(dateTextStop);
				DatePicker datePickerStop = new DatePicker();
				dateTextStop.add(datePickerStop);

				// AjaxLink<Object> btnReset = new AjaxLink<Object>("reset") {
				//
				// @Override
				// public void onClick(AjaxRequestTarget arg0) {
				// resetFilter();
				// // arg0.getPage().
				// // setResponsePage(new ReportPage());
				// }
				//
				// };
				Button btnReset = new Button("reset") {
					public void onSubmit() {
						resetFilter();
						// System.out.println(">>> btnReset.Submit");
					}
				};
				add(btnReset);

				LoadableDetachableModel<List<Member>> lmodel = new LoadableDetachableModel<List<Member>>() {

					@Override
					protected List<Member> load() {
						List<Member> memberList = memberDAO.getFilteredMembers(filterModel.getObject());
						countModel.setObject(new Integer(memberList.size()));

						return memberList;
					}
				};

				ListView<Member> listView = new ListView<Member>("caprow", lmodel) {

					@Override
					protected void populateItem(ListItem<Member> listItem) {

						// final
						Member member = listItem.getModelObject();
						listItem.add(new Label("cap", String.valueOf(member.getCap())).add(new BeautyTipBehavior(pullOutDAO
								.toolTip(member))));

						oddEven(listItem);
					}
				};
				add(listView);

				ListView<Member> fname = new ListView<Member>("fnamerow", lmodel) {

					@Override
					protected void populateItem(ListItem<Member> listItem) {

						// final
						Member member = listItem.getModelObject();
						listItem.add(new Label("firstname", member.getFirstName()));

						oddEven(listItem);
					}
				};
				add(fname);

				ListView<Member> lname = new ListView<Member>("lnamerow", lmodel) {

					@Override
					protected void populateItem(ListItem<Member> listItem) {

						// final
						Member member = listItem.getModelObject();
						listItem.add(new Label("lastname", (member.getLastName().length() == 0 ? "-" : member.getLastName())));

						oddEven(listItem);
					}
				};
				add(lname);

				ListView<Member> active = new ListView<Member>("activerow", lmodel) {

					@Override
					protected void populateItem(ListItem<Member> listItem) {

						// final
						Member member = listItem.getModelObject();
						// listItem.add(new CheckBox("active", new
						// PropertyModel<Boolean>(member, "getActive")));
						Label lblActive = new Label("active", (member.getActive() ? "A" : "I"));
						if (member.getActive()) {
							lblActive.add(new SimpleAttributeModifier("class", "active"));
						} else {
							lblActive.add(new SimpleAttributeModifier("class", "inactive"));
						}
						listItem.add(lblActive);
						oddEven(listItem);
					}
				};
				add(active);

				ListView<Member> email = new ListView<Member>("emailrow", lmodel) {

					@Override
					protected void populateItem(ListItem<Member> listItem) {

						// final
						Member member = listItem.getModelObject();

						String eMail = (member.getEmail1() == null ? "," : member.getEmail1());
						if (eMail.length() == 0) {
							eMail += ",";
						}
						if (member.getEmail2() != null) {
							eMail += " , ";
							eMail += member.getEmail2();
						}
						Label lblEmail = new Label("email", eMail == null ? "," : eMail);
						listItem.add(lblEmail);
						oddEven(listItem);
					}
				};
				add(email);

				ListView<Member> phone = new ListView<Member>("phonerow", lmodel) {

					@Override
					protected void populateItem(ListItem<Member> listItem) {

						// final
						Member member = listItem.getModelObject();
						listItem.add(new Label("phone", (member.getPhone() == null ? "-" : member.getPhone())));

						oddEven(listItem);
					}
				};
				add(phone);

				ListView<Member> contact = new ListView<Member>("contactrow", lmodel) {

					@Override
					protected void populateItem(ListItem<Member> listItem) {

						// final
						Member member = listItem.getModelObject();
						listItem.add(new Label("contact", (member.getContactId() == null ? "-" : member.getContactId())));

						oddEven(listItem);
					}
				};
				add(contact);

				ListView<Member> payment = new ListView<Member>("paymentrow", lmodel) {

					@Override
					protected void populateItem(ListItem<Member> listItem) {

						// final
						Member member = listItem.getModelObject();
						listItem.add(new Label("payment", (member.getPayment() == null ? "-" : member.getPayment())));

						oddEven(listItem);
					}
				};
				add(payment);

				ListView<Member> note = new ListView<Member>("noterow", lmodel) {

					@Override
					protected void populateItem(ListItem<Member> listItem) {

						// final
						Member member = listItem.getModelObject();
						listItem.add(new Label("note", (member.getNotes() == null ? "-" : member.getNotes())));

						oddEven(listItem);
					}
				};
				add(note);

				ListView<Member> update = new ListView<Member>("updaterow", lmodel) {

					@Override
					protected void populateItem(ListItem<Member> listItem) {

						// final
						Member member = listItem.getModelObject();
						listItem.add(new Label("updated", DateTime.formatDate(member.getUpdated())));
						oddEven(listItem);
					}
				};
				add(update);

				add(new Label("count", countModel));

			}

			@Override
			protected void onSubmit() {
				// System.out.println(">>> Form.Submit");
				super.onSubmit();
			}

			private void resetFilter() {
				// filterModel = new CompoundPropertyModel<Member>(member);

				filterModel.getObject().setCap(null);
				filterModel.getObject().setContactId(null);
				filterModel.getObject().setEmail1(null);
				filterModel.getObject().setFirstName(null);
				filterModel.getObject().setLastName(null);
				filterModel.getObject().setPhone(null);
				filterModel.getObject().setActive(true);
				filterModel.getObject().setPayment(null);
				filterModel.getObject().setUpdated(null);
			}
		};
		add(form);

		SaveOutput();
	}

	private void oddEven(ListItem<Member> listItem) {
		if (listItem.getIndex() % 2 == 0) {
			listItem.add(new SimpleAttributeModifier("class", "even"));
		} else {
			listItem.add(new SimpleAttributeModifier("class", "odd"));
		}
	}

	private void SaveOutput() {
		// System.out.println("Generate output files!");
		CsvFile csvFile = new CsvFile(); // Avoid static error

		// Members
		List<Member> activeMembers = memberDAO.getMembers();	//.getByActive(Boolean.TRUE);
		// System.out.println(activeMembers);
		csvFile.CsvMembers(activeMembers);
	}

}
