package pbi.hazard;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebPage;

public class HelpPage extends WebPage {
	public HelpPage() {
		super();

		AjaxLink btnReturn = new AjaxLink("return") {

			@Override
			public void onClick(AjaxRequestTarget arg0) {
				setResponsePage(new HomePage());
			}
		};
		add(btnReturn);

	}
}
