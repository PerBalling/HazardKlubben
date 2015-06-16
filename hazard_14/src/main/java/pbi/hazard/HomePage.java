package pbi.hazard;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;

public class HomePage extends WebPage {

	public HomePage() {
		super();

		Label version = new Label("version", ""); // Ver 1.2
		// Get version from maven buildtime
		//http://stackoverflow.com/questions/2712970/how-to-get-maven-artifact-version-at-runtime
		add(version);

		AjaxLink helpbutton = new AjaxLink("help") {

			@Override
			public void onClick(AjaxRequestTarget arg0) {

				setResponsePage(new HelpPage());
			}

		};
		add(helpbutton);

		AjaxLink memberlistbutton = new AjaxLink("memberlist") {

			@Override
			public void onClick(AjaxRequestTarget arg0) {

				setResponsePage(new MemberList());
			}

		};
		add(memberlistbutton);

		AjaxLink pullOutbutton = new AjaxLink("pullOut") {

			@Override
			public void onClick(AjaxRequestTarget arg0) {

				setResponsePage(new PullOutListPage());
			}

		};
		add(pullOutbutton);

		AjaxLink btnReports = new AjaxLink("reports") {

			@Override
			public void onClick(AjaxRequestTarget arg0) {

				setResponsePage(new ReportPage());
			}

		};
		add(btnReports);

		AjaxLink shutDownbutton = new AjaxLink("shutdown") {

			@Override
			public void onClick(AjaxRequestTarget target) {
				setResponsePage(new ClosePage());
			}

		};
		add(shutDownbutton);

		// TEST - BEGIN
		// AjaxLink modalbutton = new AjaxLink("modal") {
		//
		// @Override
		// public void onClick(AjaxRequestTarget target) {
		// setResponsePage(new ModalWindowPage());
		// }
		//
		// };
		// add(modalbutton);
		// TEST - END

	}
}
