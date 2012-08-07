package org.jboss.pressgangccms.client.local.presenter.base;

import javax.inject.Inject;

import org.jboss.errai.bus.client.api.ErrorCallback;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.enterprise.client.jaxrs.api.RestClient;
import org.jboss.pressgangccms.client.local.constants.Constants;
import org.jboss.pressgangccms.client.local.events.SearchViewEvent;
import org.jboss.pressgangccms.client.local.resources.ImageResources;
import org.jboss.pressgangccms.client.local.strings.PressGangCCMSUI;
import org.jboss.pressgangccms.client.local.view.SearchView;
import org.jboss.pressgangccms.client.local.view.WelcomeView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * The base interface for all presenters
 */
public interface Presenter
{
	/**
	 * Called when the presenter is to be displayed
	 * 
	 * @param container
	 *            The container that will host the presneter
	 */
	abstract public void go(final HasWidgets container);

	/**
	 * This interface defines the base methods that the presenters expect to
	 * see.
	 * 
	 * @author Matthew Casperson
	 * 
	 */
	public interface TemplateInterface
	{
		/**
		 * @return The panel that should be added as a child to the root panel
		 */
		Panel getTopLevelPanel();
	}

	/**
	 * This class provides a way to build pages from a standard template. The
	 * views are expected to extend this class, as well as a Display interface
	 * defined in the Presenter classes.
	 * 
	 * In this way Errai can know which class to instantiate (because the View
	 * class extends the Presenters own Display interface), the View can build a
	 * standard template (because it extends this class), and the Presenter can
	 * work with the view through the TemplateInterface.
	 * 
	 * @author Matthew Casperson
	 */
	public abstract class TemplateDisplay implements TemplateInterface
	{


		@Inject	private HandlerManager eventBus;
		
		private final VerticalPanel topLevelPanel = new VerticalPanel();
		private final Label applicationTitle = new Label();
		private final Label pageTitle = new Label();
		private final HorizontalPanel shortcutAndContentPanel = new HorizontalPanel();
		private final VerticalPanel shortcutPanel = new VerticalPanel();
		private final VerticalPanel contentPanel = new VerticalPanel();
		private final HorizontalPanel topActionPanel = new HorizontalPanel();
		private final HorizontalPanel footerPanel = new HorizontalPanel();

		private final PushButton search;
		private final PushButton bug;

		public Panel getTopLevelPanel()
		{
			return this.topLevelPanel;
		}

		public TemplateDisplay(final String applicationName, final String pageName)
		{
			applicationTitle.setText(applicationName);
			applicationTitle.addStyleName("ApplicationTitle");
			applicationTitle.addStyleName("SpacedElement");
			topLevelPanel.add(applicationTitle);

			pageTitle.setText(pageName);
			pageTitle.addStyleName("PageTitle");
			pageTitle.addStyleName("SpacedElement");
			topLevelPanel.add(pageTitle);

			topLevelPanel.add(shortcutAndContentPanel);
			shortcutAndContentPanel.add(shortcutPanel);
			shortcutAndContentPanel.add(contentPanel);
			
			contentPanel.add(topActionPanel);

			contentPanel.add(getContentPanel());

			topLevelPanel.add(footerPanel);

			/* Build the shortcut panel */
			search = new PushButton(new Image(Constants.resources.search()), new Image(Constants.resources.searchDown()));
			search.getUpHoveringFace().setImage(new Image(Constants.resources.searchHover()));
			search.addStyleName("SpacedElement");
			shortcutPanel.add(search);
			
			bug = new PushButton(new Image(Constants.resources.bug()), new Image(Constants.resources.bugDown()));
			bug.getUpHoveringFace().setImage(new Image(Constants.resources.bugHover()));
			bug.addStyleName("SpacedElement");
			shortcutPanel.add(bug);
			
			bind();
		}

		private void bind()
		{
			search.addClickHandler(new ClickHandler()
			{
				@Override
				public void onClick(final ClickEvent event)
				{
					eventBus.fireEvent(new SearchViewEvent());
				}
			});
			
			bug.addClickHandler(new ClickHandler()
			{
				@Override
				public void onClick(final ClickEvent event)
				{
					Window.open(Constants.BUGZILLA_URL, "_blank", "");
				}
			});
		}

		abstract protected Panel getContentPanel();
	}
}
