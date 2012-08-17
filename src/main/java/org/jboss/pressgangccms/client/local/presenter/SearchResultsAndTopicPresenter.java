package org.jboss.pressgangccms.client.local.presenter;

import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.pressgangccms.client.local.presenter.base.TemplatePresenter;
import org.jboss.pressgangccms.client.local.resources.strings.PressGangCCMSUI;
import org.jboss.pressgangccms.client.local.restcalls.RESTCalls;
import org.jboss.pressgangccms.client.local.view.SearchResultsAndTopicView;
import org.jboss.pressgangccms.client.local.view.base.BaseTemplateViewInterface;
import org.jboss.pressgangccms.client.local.view.base.TopicViewInterface;
import org.jboss.pressgangccms.rest.v1.collections.RESTTopicCollectionV1;
import org.jboss.pressgangccms.rest.v1.entities.RESTTopicV1;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.CellPreviewEvent.Handler;

@Dependent
public class SearchResultsAndTopicPresenter extends TemplatePresenter
{
	public interface Display extends BaseTemplateViewInterface
	{
		SimplePanel getTopicResultsPanel();

		SimplePanel getTopicViewPanel();

		SimplePanel getTopicViewActionButtonsPanel();

		SimplePanel getTopicResultsActionButtonsPanel();
	}

	@Inject
	private Display display;

	@Inject
	private TopicPresenter.Display topicViewDisplay;

	@Inject
	private TopicXMLPresenter.Display topicXMLDisplay;

	@Inject
	private TopicRenderedPresenter.Display topicRenderedDisplay;

	@Inject
	private SearchResultsPresenter.Display searchResultsDisplay;
	
	@Inject
	private TopicXMLErrorsPresenter.Display topicXMLErrorsDisplay;

	/**
	 * This will reference the selected view, so as to maintain the view between
	 * clicks
	 */
	private TopicViewInterface selectedView;

	private String queryString;

	private RESTTopicV1 selectedTopic;
	
	/** Keeps a reference to the list of topics being displayed */
	private List<RESTTopicV1> currentList;
	
	/** Keeps a reference to the start row */
	private Integer tableStartRow;

	@Override
	public void go(final HasWidgets container)
	{
		container.clear();
		container.add(display.getTopLevelPanel());

		display.getTopicResultsActionButtonsPanel().setWidget(searchResultsDisplay.getTopActionPanel());
		display.getTopicResultsPanel().setWidget(searchResultsDisplay.getPanel());

		bind();
	}

	private void bind()
	{
		super.bind(display);

		final AsyncDataProvider<RESTTopicV1> provider = new AsyncDataProvider<RESTTopicV1>()
		{
			@Override
			protected void onRangeChanged(final HasData<RESTTopicV1> display)
			{
				tableStartRow = display.getVisibleRange().getStart();
				final int length = display.getVisibleRange().getLength();
				final int end = tableStartRow + length;

				final RESTCalls.RESTCallback<RESTTopicCollectionV1> callback = new RESTCalls.RESTCallback<RESTTopicCollectionV1>()
				{
					@Override
					public void begin()
					{
						startProcessing();
					}

					@Override
					public void generalException(final Exception ex)
					{
						stopProcessing();
					}

					@Override
					public void success(final RESTTopicCollectionV1 retValue)
					{
						try
						{
							currentList = retValue.getItems();
							updateRowData(tableStartRow, currentList);
							updateRowCount(retValue.getSize(), true);
						}
						finally
						{
							stopProcessing();
						}
					}

					@Override
					public void failed()
					{
						stopProcessing();
					}
				};

				RESTCalls.getTopicsFromQuery(callback, queryString, tableStartRow, end);
			}
		};

		/* Respond to row clicks */

		searchResultsDisplay.getResults().addCellPreviewHandler(new Handler<RESTTopicV1>()
		{
			@Override
			public void onCellPreview(final CellPreviewEvent<RESTTopicV1> event)
			{
				/* Check to see if this was a click event */
				final boolean isClick = "click".equals(event.getNativeEvent().getType());

				if (isClick)
				{				
					selectedTopic = event.getValue();

					final RESTCalls.RESTCallback<RESTTopicV1> callback = new RESTCalls.RESTCallback<RESTTopicV1>()
					{
						@Override
						public void begin()
						{
							startProcessing();
						}

						@Override
						public void generalException(final Exception ex)
						{
							stopProcessing();
						}

						@Override
						public void success(final RESTTopicV1 retValue)
						{
							try
							{
								/* default to the topic details view */
								if (selectedView == null)
								{
									selectedView = topicRenderedDisplay;
									updateTopicDisplay();
								}

								selectedView.initialize(retValue);
							}
							finally
							{
								stopProcessing();
							}
						}

						@Override
						public void failed()
						{
							stopProcessing();
						}
					};

					RESTCalls.getTopic(callback, selectedTopic.getId());
				}
			}
		});

		final ClickHandler topicViewClickHandler = new ClickHandler()
		{
			@Override
			public void onClick(final ClickEvent event)
			{
				/* Sync any changes back to the underlying object */
				flushChanges();
				
				if (selectedTopic != null)
				{
					selectedView = topicViewDisplay;
					selectedView.initialize(selectedTopic);
					updateTopicDisplay();
				}
			}
		};

		final ClickHandler topicXMLClickHandler = new ClickHandler()
		{
			@Override
			public void onClick(final ClickEvent event)
			{
				/* Sync any changes back to the underlying object */
				flushChanges();
				
				if (selectedTopic != null)
				{
					selectedView = topicXMLDisplay;
					selectedView.initialize(selectedTopic);
					updateTopicDisplay();
				}
			}
		};

		final ClickHandler topicRenderedClickHandler = new ClickHandler()
		{
			@Override
			public void onClick(final ClickEvent event)
			{
				/* Sync any changes back to the underlying object */
				flushChanges();
				
				if (selectedTopic != null)
				{
					selectedView = topicRenderedDisplay;
					selectedView.initialize(selectedTopic);
					updateTopicDisplay();
				}
			}
		};
		
		final ClickHandler topicXMLErrorsClickHandler = new ClickHandler()
		{
			@Override
			public void onClick(final ClickEvent event)
			{
				/* Sync any changes back to the underlying object */
				flushChanges();
				
				if (selectedTopic != null)
				{
					selectedView = topicXMLErrorsDisplay;
					selectedView.initialize(selectedTopic);
					updateTopicDisplay();
				}
			}
		};

		/* Build up a click handler to save the topic */
		final ClickHandler saveClickHandler = new ClickHandler()
		{
			@Override
			public void onClick(final ClickEvent event)
			{
				if (selectedTopic != null)
				{
					final RESTCalls.RESTCallback<RESTTopicV1> callback = new RESTCalls.RESTCallback<RESTTopicV1>()
					{
						@Override
						public void begin()
						{
							startProcessing();
						}

						@Override
						public void generalException(final Exception ex)
						{
							stopProcessing();
						}

						@Override
						public void success(final RESTTopicV1 retValue)
						{
							try
							{
								/* Update the local collection of topics */
								retValue.cloneInto(selectedTopic, true);
								/* Update the list of topics */
								provider.updateRowData(tableStartRow, currentList);
								/* Update the edit window */
								selectedView.initialize(selectedTopic);
								
								Window.alert(PressGangCCMSUI.INSTANCE.SaveSuccess());
							}
							finally
							{
								stopProcessing();
							}
						}

						@Override
						public void failed()
						{
							stopProcessing();
						}
					};
					
					/* Sync any changes back to the underlying object */
					flushChanges();
					
					/* Create a new instance of the topic, with all the properties set to explicitly update */
					final RESTTopicV1 updateTopic = selectedTopic.clone(true);
					updateTopic.explicitSetBugzillaBugs_OTM(updateTopic.getBugzillaBugs_OTM());
					updateTopic.explicitSetProperties(updateTopic.getProperties());
					updateTopic.explicitSetSourceUrls_OTM(updateTopic.getSourceUrls_OTM());
					updateTopic.explicitSetTags(updateTopic.getTags());
					updateTopic.explicitSetDescription(updateTopic.getDescription());
					updateTopic.explicitSetLocale(updateTopic.getLocale());
					updateTopic.explicitSetTitle(updateTopic.getTitle());
					updateTopic.explicitSetXml(updateTopic.getXml());
					
					RESTCalls.saveTopic(callback, updateTopic);
				}
			}
		};

		topicViewDisplay.getFields().addClickHandler(topicViewClickHandler);
		topicViewDisplay.getXml().addClickHandler(topicXMLClickHandler);
		topicViewDisplay.getRendered().addClickHandler(topicRenderedClickHandler);
		topicViewDisplay.getSave().addClickHandler(saveClickHandler);
		topicViewDisplay.getXmlErrors().addClickHandler(topicXMLErrorsClickHandler);
		
		topicXMLDisplay.getFields().addClickHandler(topicViewClickHandler);
		topicXMLDisplay.getXml().addClickHandler(topicXMLClickHandler);
		topicXMLDisplay.getRendered().addClickHandler(topicRenderedClickHandler);
		topicXMLDisplay.getSave().addClickHandler(saveClickHandler);
		topicXMLDisplay.getXmlErrors().addClickHandler(topicXMLErrorsClickHandler);

		topicRenderedDisplay.getFields().addClickHandler(topicViewClickHandler);
		topicRenderedDisplay.getXml().addClickHandler(topicXMLClickHandler);
		topicRenderedDisplay.getRendered().addClickHandler(topicRenderedClickHandler);
		topicRenderedDisplay.getSave().addClickHandler(saveClickHandler);
		topicRenderedDisplay.getXmlErrors().addClickHandler(topicXMLErrorsClickHandler);
		
		topicXMLErrorsDisplay.getFields().addClickHandler(topicViewClickHandler);
		topicXMLErrorsDisplay.getXml().addClickHandler(topicXMLClickHandler);
		topicXMLErrorsDisplay.getRendered().addClickHandler(topicRenderedClickHandler);
		topicXMLErrorsDisplay.getSave().addClickHandler(saveClickHandler);
		topicXMLErrorsDisplay.getXmlErrors().addClickHandler(topicXMLErrorsClickHandler);
		
		topicXMLDisplay.getLineWrap().addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(final ClickEvent event)
			{
				topicXMLDisplay.getEditor().setUseWrapMode(!topicXMLDisplay.getEditor().getUserWrapMode());
			}
		});

		searchResultsDisplay.setProvider(provider);
	}

	/**
	 * Sets the topic display view
	 */
	private void updateTopicDisplay()
	{
		display.getTopicViewActionButtonsPanel().clear();
		display.getTopicViewPanel().clear();

		display.getTopicViewActionButtonsPanel().setWidget(selectedView.getTopActionPanel());
		display.getTopicViewPanel().setWidget(selectedView.getPanel());
	}
	
	private void flushChanges()
	{
		/* Sync any changes back to the underlying object, except for the xml errros because they are read only */
		if (selectedView != null && selectedView != topicXMLErrorsDisplay && selectedView.getDriver() != null)
			selectedView.getDriver().flush();
	}

	@Override
	public void parseToken(final String historyToken)
	{
		queryString = historyToken.replace(SearchResultsAndTopicView.HISTORY_TOKEN + ";", "");
	}

	private void stopProcessing()
	{
		display.setSpinnerVisible(false);
	}

	private void startProcessing()
	{
		display.setSpinnerVisible(true);
	}
}
