package pbi.hazard.panel;

import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import pbi.hazard.common.DateTime;
import pbi.hazard.model.OnsitePlaceHolder;
import pbi.hazard.model.PullOut;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 
 */
public class MemberOnsiteDayPanel extends Panel {
	private static final long serialVersionUID = 1L;

	// @SpringBean
	// private PullOutMemberDAO pullOutMemberDAO;

	int cols = 1;

	public MemberOnsiteDayPanel(String id, final OnsitePlaceHolder oph) {
		super(id);

		final WebMarkupContainer cell = new WebMarkupContainer("cell");
		// System.out.println(Integer.toString(cols));
		// cell.add(new SimpleAttributeModifier("colspan", "3"));
		add(cell);

		IModel<List<PullOut>> pullOuts = new LoadableDetachableModel<List<PullOut>>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected List<PullOut> load() {
				List<PullOut> listPullOuts = new ArrayList<PullOut>();
				// System.out.println(oph.getHeader());
				// if (oph.getHeader()) {
				// l.add(null);
				// oph.setHeader(false);
				// }

				// add(new Label("date", poMembers.getObject().);
				// add(new Label("date",
				// DateTime.formatDateSearch(oph.getPullOutDay())));
				Label dateLabel = null;
				// if (oph.getActualPullOut()==null) {
				// dateLabel = new Label("date", "Dag");
				// }
				// else {
				// System.out.println(" >> Date: "+oph.getPullOutDay());
				if (oph.getActualDayPullOut() == null) {
					dateLabel = new Label("date", "Dag");
					cell.add(new SimpleAttributeModifier("align", "right"));
				} else {
					dateLabel = new Label("date", DateTime.formatDateSearch(oph.getActualDayPullOut().getStart()));
				}
				// }
				cell.add(dateLabel);

				// System.out.println("Load: " + oph.getActualDayPullOut());
				if (oph.getActualDayPullOut() == null) {
					listPullOuts.add(null);
					oph.getNextDayPullOut();
				} else {
					Date thisDay = oph.getActualDayPullOut().getStart();
					// System.out.println(thisDay);
					// System.out.println(oph.getActualDayPullOut().getStart());
					while (DateTime.compareDates(thisDay, oph.getActualDayPullOut().getStart()) == 0) {
						listPullOuts.add(oph.getActualDayPullOut());
						if (oph.getNextDayPullOut() == null) {
							break; // Stop at last item
						}
					}
				}
				// for (PullOut pullOut : oph.getPullOuts()) {
				// if (pullOut!=null&&oph.getActualPullOut()!=null) {
				// if (DateTime.compareDates(pullOut.getStart(),
				// oph.getActualPullOut().getStart()) == 0) {
				// l.add(pullOut);
				// }
				// }
				// }

				cols = listPullOuts.size();
				return listPullOuts;
				// for list_of_pullouts
				// if oph.getday compareto pullout.getstart
				// then l.add pullout
				// List<String> l = new ArrayList<String>();
				// l.add("Dag1");
				// l.add("Dag2");
				// // l.add("Dag3");
				// get list of pullouts from a specific day
				// return oph.getPullOutDates();
			}
		};

		ListView<PullOut> listView = new ListView<PullOut>("list", pullOuts) {
			private static final long serialVersionUID = 1L;

			// int idx=0;
			@Override
			protected void populateItem(ListItem<PullOut> item) {
				cell.add(new SimpleAttributeModifier("colspan", Integer.toString(cols)));
				// System.out.println(" >>"+item.getModelObject());
				// item.add(new Label("panel", "panel"));
				// System.out.println(Integer.toString(poMembers.getObject().size()));
				// System.out.println(" >>Item: " + item.getModelObject());
				// oph.setActualPullOut(item.getModelObject()); // null: Header
				// oph.setIdx(++idx);
				item.add(new MemberOnsitePullOutPanel("panel", oph));
			}
		};
		add(listView);

	}
}
