package pbi.hazard.panel;

import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.visural.wicket.behavior.beautytips.BeautyTipBehavior;

import pbi.hazard.dao.MemberDAO;
import pbi.hazard.dao.PullOutDAO;
import pbi.hazard.model.Member;

import java.util.List;

/**
 * 
 */
public class MemberPanel extends Panel {

	@SpringBean
	private MemberDAO memberDao;
	@SpringBean
	private PullOutDAO pullOutDAO;

	private IModel<List<Member>> memberModel = new LoadableDetachableModel<List<Member>>() {
		@Override
		protected List<Member> load() {
			// return memberDao.getActiveMembers();
			// return memberDao.getMockMembers();
			return memberDao.getByActive(Boolean.TRUE);
		}
	};

	public MemberPanel(String id) {
		super(id);
		ListView<Member> listView = new ListView<Member>("memberList", memberModel) {

			@Override
			protected void populateItem(ListItem<Member> listItem) {
				Member member = listItem.getModelObject();
				listItem.add(new Label("cap", String.valueOf(member.getCap())).add(new BeautyTipBehavior(pullOutDAO
						.toolTip(member))));
				listItem.add(new Label("firstname", member.getFirstName()));
				listItem.add(new Label("lastname", member.getLastName()));
				// listItem.add(new Label("active",
				// String.valueOf(member.getActive())));
				
				if (listItem.getIndex() % 2 == 0) {
					listItem.add(new SimpleAttributeModifier("class", "even"));
				} else {
					listItem.add(new SimpleAttributeModifier("class", "odd"));
				}
			}
		};
		add(listView);
		add(new Label("count", String.valueOf(listView.getViewSize())));

	}
}
