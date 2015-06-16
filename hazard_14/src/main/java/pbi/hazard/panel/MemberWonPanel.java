package pbi.hazard.panel;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import pbi.hazard.common.DateTime;
import pbi.hazard.dao.PullOutDAO;
import pbi.hazard.model.Member;
import pbi.hazard.model.PullOut;

import java.util.List;

/**
 * 
 */
public class MemberWonPanel extends Panel {

	@SpringBean
	private PullOutDAO pullOutDAO;

	Member member;
	
	private IModel<List<PullOut>> poModel = new LoadableDetachableModel<List<PullOut>>() {
		@Override
		protected List<PullOut> load() {
			return pullOutDAO.findWhenWon(member);
		}
	};

	public MemberWonPanel(String id, IModel<Member> model) {
		super(id, model);
		member=model.getObject();
		ListView<PullOut> listView = new ListView<PullOut>("wonList", poModel) {
			
			@Override
			protected void populateItem(ListItem<PullOut> listItem) {
				PullOut po = listItem.getModelObject();
				listItem.add(new Label("name", po.getName()));
				listItem.add(new Label("date", DateTime.formatDate(po.getStop())));
			}
		};
		add(listView);
		add(new Label("count",String.valueOf(listView.getViewSize())));

	}
}
