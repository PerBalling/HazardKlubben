package pbi.hazard.panel;

import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import pbi.hazard.dao.PullOutMemberDAO;
import pbi.hazard.model.Member;
import pbi.hazard.model.OnsitePlaceHolder;
import pbi.hazard.model.PullOutMember;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class MemberOnsitePullOutPanel extends Panel {
	private static final long serialVersionUID = 1L;
	private static final String strOnsite = "1";
	private static final String strNotOnsite = ".";
	private static final String strInActive = "-";
	@SpringBean
	private PullOutMemberDAO pullOutMemberDAO;

	public MemberOnsitePullOutPanel(String id, final OnsitePlaceHolder oph) {
		super(id);

		IModel<List<String>> poMembers = new LoadableDetachableModel<List<String>>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected List<String> load() {
				List<PullOutMember> allInThisPullOut = null;
				if (oph.getActualPullOut() == null) {
					// System.out.println(">>>1. Col");
				} else {
					// System.out.println(">>>" + oph.getActualPullOut().getName());
					// System.out.println(">>>"+oph.getIdx());
					allInThisPullOut = pullOutMemberDAO.getPullOutMemberList(oph.getActualPullOut(), 1);
					// System.out.println(allInThisPullOut.size());
				}
				List<String> loadList = new ArrayList<String>();
				String val = null;
				int sum = 0;
				for (Member onsiteMember : oph.getOnsiteMembers()) {
					// System.out.println(oph.getActualPullOut().getName());
					if (allInThisPullOut == null) {
						// 1. col
						loadList.add(onsiteMember.getCap().toString() + " " + onsiteMember.returnName());
					} else {
						List<Integer> allThisIds = new ArrayList<Integer>();
						for (PullOutMember pom : allInThisPullOut) {
							// System.out.println(" "+pom.getMember().getId());
							allThisIds.add(pom.getMember().getId());
							if (pom.getMember().getId().equals(onsiteMember.getId())) {
								// System.out.println(">"+pom.getMember().getId());
								if (pom.getOnsite()) {
									val = strOnsite;
									sum++;
								} else {
									val = strNotOnsite;
								}
								loadList.add(val);
							}
						}
						if (!allThisIds.contains(onsiteMember.getId())) {
							// System.out.println( "\t"+onsiteMember.returnName());
							loadList.add(strInActive);
						}
					}
					if (oph.getActualPullOut() == null) {
						val = "Sum";
					} else {
						val = Integer.toString(sum);
					}
				}
				loadList.add(val);

				String po = null;
				if (oph.getActualPullOut() == null) {
					po = loadList.size() - 1 + " Deltagere \\ Tr\u00e6kning";
				} else {
					po = oph.getActualPullOut().getName().trim();
					po = new StringBuilder().append(po.charAt(0)).append(po.charAt(po.length() - 1)).toString();
				}
				// System.out.println(">>>"+po);
				add(new Label("pullout", po));

				// System.out.println("#Was# "+oph.getActualPullOut());
				oph.getNextPullOut();
				// System.out.println("#Now# "+oph.getActualPullOut());

				return loadList;
			}
		};

		ListView<String> listView = new ListView<String>("list", poMembers) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<String> item) {
				// System.out.println(">>> Item:"+item.getModelObject());
				item.add(new Label("onsite", item.getModelObject()));
				// oph.getNextPullOut();

				if (item.getModelObject().contains(" ")) {
					// List of members
					item.add(new SimpleAttributeModifier("align", "left"));
				} else {
					item.add(new SimpleAttributeModifier("align", "center"));
				}

				if (item.getModelObject().equals(strNotOnsite)) {
					// item.add(new SimpleAttributeModifier("align", "right"));
					item.add(new SimpleAttributeModifier("style", "color:grey"));
				}
				if (item.getModelObject().equals(strInActive)) {
					item.add(new SimpleAttributeModifier("style", "color:red"));
				}
			}
		};
		add(listView);
	}
}
