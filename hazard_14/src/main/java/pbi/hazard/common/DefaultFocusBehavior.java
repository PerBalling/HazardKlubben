package pbi.hazard.common;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.form.FormComponent;

/***
 * @see http://www.nabble.com/Default-Focus-Behavior--td15934889.html
 *      http://javathoughts
 *      .capesugarbird.com/2009/01/wicket-and-default-focus-behavior.html
 * */
public class DefaultFocusBehavior extends AbstractBehavior {
	private static final long serialVersionUID = -4891399118136854774L;
	private Component component;

	@Override
	public void bind(Component component) {
		if (!(component instanceof FormComponent)) {
			throw new IllegalArgumentException("DefaultFocusBehavior: component must be instanceof FormComponent");
		}
		this.component = component;
		component.setOutputMarkupId(true);
	}

	@Override
	public void renderHead(IHeaderResponse iHeaderResponse) {
		super.renderHead(iHeaderResponse);
		// iHeaderResponse.renderOnLoadJavascript("document.getElementById('" +
		// component.getMarkupId() + "').focus();");
		iHeaderResponse.renderOnLoadJavascript("document.getElementById('" + component.getMarkupId() + "').select();");
	}
}

