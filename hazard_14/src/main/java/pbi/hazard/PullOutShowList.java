package pbi.hazard;

import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.visural.wicket.behavior.beautytips.BeautyTipBehavior;

import pbi.hazard.common.DateTime;
import pbi.hazard.common.StaticImage;
import pbi.hazard.dao.PullOutDAO;
import pbi.hazard.dao.PullOutMemberDAO;
import pbi.hazard.model.Member;
import pbi.hazard.model.PullOut;
import pbi.hazard.model.PullOutMember;

public class PullOutShowList extends WebPage {
	private static final String ONSITE_PIC = "pic/Onsite_Green.gif";
	private static final String NOTONSITE_PIC = "pic/Onsite_Gray.gif";
	// private static final String LASTONSITE_PIC = "pic/Onsite_Yellow.gif";
	private static final String ONSITETEXT = "Tilstede";
	private static final String NOTONSITETEXT = "Fraværende";

	FeedbackPanel feedbackPanel = new FeedbackPanel("feedback");
	private Model<Integer> countModel = new Model<Integer>();

	@SpringBean
	private PullOutMemberDAO pullOutMemberDAO;
	@SpringBean
	private PullOutDAO pullOutDAO;

	public PullOutShowList(final PullOut pullOut, final Page callingPage) {
		Member member = new Member();
		final CompoundPropertyModel<Member> filterModel = new CompoundPropertyModel<Member>(member);
		Form<Member> form = new Form<Member>("form", filterModel) {
			{
				feedbackPanel.setOutputMarkupId(true);
				add(feedbackPanel);

				LoadableDetachableModel<List<PullOutMember>> lmodel = new LoadableDetachableModel<List<PullOutMember>>() {

					@Override
					protected List<PullOutMember> load() {
						List<PullOutMember> pomList = pullOutMemberDAO.getPullOutMemberList(pullOut, 2);

						countModel.setObject(new Integer(pomList.size()));

						return pomList;
					}
				};

				ListView<PullOutMember> listView = new ListView<PullOutMember>("rows", lmodel) {
					int adjustPulled = 1; // Index base=0

					@Override
					protected void populateItem(ListItem<PullOutMember> listItem) {

						final PullOutMember pom = listItem.getModelObject();
						Label lblCap = new Label("cap", String.valueOf(pom.getMember().getCap()));
						Label lblName = new Label("fullname", pom.getMember().returnName());
						// Mark winner (if all are pulled)
						if (adjustPulled == 1 && listItem.getIndex() == countModel.getObject().intValue() - 1) {
							lblCap.add(new SimpleAttributeModifier("class", "winner"));
							lblName.add(new SimpleAttributeModifier("class", "winner"));
						}
						else {
							if (pom.getOnsite()) {
								lblCap.add(new SimpleAttributeModifier("class", "onsitelist"));
								lblName.add(new SimpleAttributeModifier("class", "onsitelist"));
							}
							if (pom.getMember().returnName().toLowerCase().contains("blanko")) {
								lblCap.add(new SimpleAttributeModifier("class", "blankolist"));
								lblName.add(new SimpleAttributeModifier("class", "blankolist"));
							}
						}
						lblCap.add(new BeautyTipBehavior(pullOutDAO.toolTip(pom.getMember())));
						listItem.add(lblCap);
						listItem.add(lblName);

						Model<String> modelOnsite = new Model<String>(pom.getOnsite() ? ONSITE_PIC : NOTONSITE_PIC);
						StaticImage imgOnsite = new StaticImage("onsite", modelOnsite);
						imgOnsite.add(new SimpleAttributeModifier("alt", (pom.getOnsite() ? ONSITETEXT : NOTONSITETEXT)));
						imgOnsite.add(new SimpleAttributeModifier("title", (pom.getOnsite() ? ONSITETEXT : NOTONSITETEXT)));
						listItem.add(imgOnsite);
						// listItem.add(new Label("pulled",
						// DateTime.formatDate(pom.getPulledOut())));
						String lblPulled = null;
						if (pom.getPulledOut() == null) {
							adjustPulled--; // Do not count items not pulled
							// (sorted first in list)
							lblPulled = "-";
						} else {
							lblPulled = String.valueOf(listItem.getIndex() + adjustPulled);
							// if (listItem.getIndex()==countModel.getObject().intValue()-1) {
							// lblPulled +=" Vinder";
							// }
						}
						listItem.add(new Label("pulled", lblPulled));

						// listItem.add(new AjaxLink<Object>("edit") {
						//
						// @Override
						// public void onClick(AjaxRequestTarget arg0) {
						// setResponsePage(new MemberDetailPage(pom, PullOutShowList.this));
						// }
						// });
						if (listItem.getIndex() % 2 == 0) {
							listItem.add(new SimpleAttributeModifier("class", "even"));
						} else {
							listItem.add(new SimpleAttributeModifier("class", "odd"));
						}
					}
				};
				add(listView);

				add(new Label("pulloutid", pullOut.getName()));
				add(new Label("pulloutdate", DateTime.formatDate(pullOut.getStop() == null ? pullOut.getStart() : pullOut
						.getStop())));
				add(new Label("count", countModel));
				add(new Label("note", pullOut.getNote() == null ? "" : pullOut.getNote()));

				AjaxLink btnChange = new AjaxLink<Object>("change") {
					@Override
					public void onClick(AjaxRequestTarget arg0) {
						setResponsePage(new CreatePullOutPage(pullOut, PullOutShowList.this));
					}
				};
				add(btnChange);

				// AjaxLink<Object> btnNew = new AjaxLink<Object>("new") {
				//
				// @Override
				// public void onClick(AjaxRequestTarget arg0) {
				// setResponsePage(new MemberDetailPage(new Member(),
				// PullOutShowList.this));
				// }
				//
				// };
				// add(btnNew);

				AjaxLink<Object> btnReturn = new AjaxLink<Object>("return") {

					@Override
					public void onClick(AjaxRequestTarget arg0) {
						// Perhaps use history.back()
						setResponsePage(callingPage);
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

	}
}
