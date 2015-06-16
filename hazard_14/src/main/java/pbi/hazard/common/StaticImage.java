package pbi.hazard.common;
/*
 * https://cwiki.apache.org/WICKET/how-to-load-an-external-image.html
 */

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class StaticImage extends WebComponent
{
  /**
  * @param id wicket id on the page
  * @param model reference the external URL from which the image is gotten
  *          for ex.: "http://images.google.com/img/10293.gif"
  */
  public StaticImage(String id, IModel urlModel)
  {
    super( id, urlModel );
  }

  protected void onComponentTag(ComponentTag tag)
  {
    super.onComponentTag( tag );
    checkComponentTag( tag, "img" );
    tag.put( "src", getDefaultModelObjectAsString() );
  }
}
