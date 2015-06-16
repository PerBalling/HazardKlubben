package pbi.hazard;

import java.util.Date;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.visural.wicket.behavior.beautytips.BeautyTipBehavior;

import pbi.hazard.common.CsvFile;
import pbi.hazard.common.DateTime;
import pbi.hazard.dao.MemberDAO;
import pbi.hazard.dao.PullOutDAO;
import pbi.hazard.model.Member;

public class MemberList extends WebPage {

	FeedbackPanel feedbackPanel = new FeedbackPanel("feedback");
	private Model<Integer> countModel = new Model<Integer>();

	@SpringBean
	private MemberDAO memberDAO;
	@SpringBean
	private PullOutDAO pullOutDAO;

	public MemberList() {
		Member member = new Member();
		final CompoundPropertyModel<Member> filterModel = new CompoundPropertyModel<Member>(member);
		Form<Member> form = new Form<Member>("form", filterModel) {
			{
				feedbackPanel.setOutputMarkupId(true);
				add(feedbackPanel);

				TextField<String> filterCap = new TextField<String>("capFilter", filterModel.<String> bind("cap"));
				add(filterCap);

				TextField<String> filterFirstName = new TextField<String>("firstnameFilter",
						filterModel.<String> bind("firstName"));
				add(filterFirstName);

				TextField<String> filterLastName = new TextField<String>("lastnameFilter",
						filterModel.<String> bind("lastName"));
				add(filterLastName);

				filterModel.getObject().setActive(true);
				add(new CheckBox("activeFilter", filterModel.<Boolean> bind("active")));

				DateTextField dateTextStop = new DateTextField("dateFilter", filterModel.<Date> bind("updated"), DateTime.searchFormat);
				add(dateTextStop);
				DatePicker datePickerStop = new DatePicker();
				dateTextStop.add(datePickerStop);

				LoadableDetachableModel<List<Member>> lmodel = new LoadableDetachableModel<List<Member>>() {

					@Override
					protected List<Member> load() {
						List<Member> memberList = memberDAO.getFilteredMembersSql(filterModel.getObject());
						countModel.setObject(new Integer(memberList.size()));

						return memberList;
					}
				};

				ListView<Member> listView = new ListView<Member>("rows", lmodel) {

					@Override
					protected void populateItem(ListItem<Member> listItem) {

						final Member member = listItem.getModelObject();
						// Dynamic Tooltip v1.1 - Add image: 
						// http://www.jroller.com/ruudmarco/entry/tooltip_tutioral_part_2_dynamic
						listItem.add(new Label("cap", String.valueOf(member.getCap())).add(new BeautyTipBehavior(pullOutDAO
								.toolTip(member))));
						listItem.add(new Label("firstname", member.getFirstName()));
						listItem.add(new Label("lastname", member.getLastName()));
						listItem.add(new CheckBox("active", new PropertyModel<Boolean>(member, "getActive")));
						listItem.add(new Label("updated", DateTime.formatDate(member.getUpdated())));

						listItem.add(new AjaxLink<Object>("edit") {

							@Override
							public void onClick(AjaxRequestTarget arg0) {
								setResponsePage(new MemberDetailPage(member));
							}
						});
						if (listItem.getIndex() % 2 == 0) {
							listItem.add(new SimpleAttributeModifier("class", "even"));
						} else {
							listItem.add(new SimpleAttributeModifier("class", "odd"));
						}
					}
				};
				add(listView);

				add(new Label("count", countModel));

				AjaxLink<Object> btnNew = new AjaxLink<Object>("new") {

					@Override
					public void onClick(AjaxRequestTarget arg0) {
						setResponsePage(new MemberDetailPage(new Member()));
					}

				};
				add(btnNew);

				AjaxLink<Object> btnReturn = new AjaxLink<Object>("return") {

					@Override
					public void onClick(AjaxRequestTarget arg0) {
						// Perhaps use history.back()
						SaveOutput();
						setResponsePage(new HomePage());
					}

				};
				add(btnReturn);

			}

			@Override
			protected void onSubmit() {
				super.onSubmit();
			}
		};
		add(form);
		
		SaveOutput();

	}


	private void SaveOutput() {
		// System.out.println("Generate output files!");
		CsvFile csvFile = new CsvFile(); // Avoid error: Cannot make a static reference to the non-static method 

		// Members
		List<Member> activeMembers = memberDAO.getMembers();	//getByActive(Boolean.TRUE);
		// System.out.println(activeMembers);
		csvFile.CsvMembers(activeMembers);
	}
}
