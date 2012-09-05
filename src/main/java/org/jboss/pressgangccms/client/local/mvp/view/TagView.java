package org.jboss.pressgangccms.client.local.mvp.view;

import org.jboss.pressgangccms.client.local.mvp.presenter.TagPresenter;
import org.jboss.pressgangccms.client.local.mvp.presenter.TagPresenter.TagPresenterDriver;
import org.jboss.pressgangccms.client.local.mvp.presenter.TopicPresenter.TopicPresenterDriver;
import org.jboss.pressgangccms.client.local.mvp.view.base.BaseTemplateView;
import org.jboss.pressgangccms.client.local.resources.strings.PressGangCCMSUI;
import org.jboss.pressgangccms.client.local.ui.SplitType;
import org.jboss.pressgangccms.client.local.ui.UIUtilities;
import org.jboss.pressgangccms.client.local.ui.editor.tagview.RESTTagV1BasicDetailsEditor;
import org.jboss.pressgangccms.client.local.ui.editor.topicview.RESTTopicV1BasicDetailsEditor;
import org.jboss.pressgangccms.rest.v1.entities.RESTTagV1;
import org.jboss.pressgangccms.rest.v1.entities.RESTTopicV1;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.PushButton;

public class TagView extends BaseTemplateView implements TagPresenter.Display
{
	public static final String HISTORY_TOKEN = "TagView";
	
	/** The GWT Editor Driver */
	private final TagPresenterDriver driver = GWT.create(TagPresenterDriver.class);
	
	private boolean readOnly = false;
	
	private final PushButton save = UIUtilities.createPushButton(PressGangCCMSUI.INSTANCE.Save());
	
	public PushButton getSave()
	{
		return save;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public SimpleBeanEditorDriver getDriver()
	{
		return driver;
	}
	
	public TagView()
	{
		super(PressGangCCMSUI.INSTANCE.PressGangCCMS(), PressGangCCMSUI.INSTANCE.Tags());
		
		populateTopActionBar();
	}

	protected void populateTopActionBar()
	{
		addActionButton(this.getSave());
		addRightAlignedActionButtonPaddingPanel();
	}

	public void initialize(final RESTTagV1 tag, final boolean readOnly)
	{
		this.readOnly = readOnly;
		
		/* SearchUIProjectsEditor is a grid */
		final RESTTagV1BasicDetailsEditor editor = new RESTTagV1BasicDetailsEditor(this.readOnly);
	    /* Initialize the driver with the top-level editor */
	    driver.initialize(editor);
	    /* Copy the data in the object into the UI */
	    driver.edit(tag);
	    /* Add the projects */
	    this.getPanel().setWidget(editor);
	} 
}