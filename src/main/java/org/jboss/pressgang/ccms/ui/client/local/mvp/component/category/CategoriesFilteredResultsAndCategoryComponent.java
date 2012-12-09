package org.jboss.pressgang.ccms.ui.client.local.mvp.component.category;

import static org.jboss.pressgang.ccms.ui.client.local.utilities.GWTUtilities.stringEqualsEquatingNullWithEmptyString;

import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.pressgang.ccms.rest.v1.collections.RESTCategoryCollectionV1;
import org.jboss.pressgang.ccms.rest.v1.collections.RESTTagCollectionV1;
import org.jboss.pressgang.ccms.rest.v1.collections.base.RESTBaseCollectionItemV1;
import org.jboss.pressgang.ccms.rest.v1.collections.base.RESTBaseUpdateCollectionItemV1;
import org.jboss.pressgang.ccms.rest.v1.collections.items.RESTCategoryCollectionItemV1;
import org.jboss.pressgang.ccms.rest.v1.collections.items.RESTTagCollectionItemV1;
import org.jboss.pressgang.ccms.rest.v1.collections.items.join.RESTTagInCategoryCollectionItemV1;
import org.jboss.pressgang.ccms.rest.v1.collections.join.RESTTagInCategoryCollectionV1;
import org.jboss.pressgang.ccms.rest.v1.entities.RESTCategoryV1;
import org.jboss.pressgang.ccms.rest.v1.entities.RESTTagV1;
import org.jboss.pressgang.ccms.rest.v1.entities.RESTTopicV1;
import org.jboss.pressgang.ccms.rest.v1.entities.join.RESTTagInCategoryV1;
import org.jboss.pressgang.ccms.ui.client.local.constants.Constants;
import org.jboss.pressgang.ccms.ui.client.local.mvp.component.base.children.AddPossibleChildCallback;
import org.jboss.pressgang.ccms.ui.client.local.mvp.component.base.children.GetExistingCollectionCallback;
import org.jboss.pressgang.ccms.ui.client.local.mvp.component.base.children.UpdateAfterChildModfiedCallback;
import org.jboss.pressgang.ccms.ui.client.local.mvp.component.base.orderedchildren.SetNewChildSortCallback;
import org.jboss.pressgang.ccms.ui.client.local.mvp.component.base.searchandedit.BaseSearchAndEditComponent;
import org.jboss.pressgang.ccms.ui.client.local.mvp.component.base.searchandedit.DisplayNewEntityCallback;
import org.jboss.pressgang.ccms.ui.client.local.mvp.component.base.searchandedit.GetNewEntityCallback;
import org.jboss.pressgang.ccms.ui.client.local.mvp.events.CategoriesFilteredResultsAndCategoryViewEvent;
import org.jboss.pressgang.ccms.ui.client.local.mvp.presenter.category.CategoriesFilteredResultsAndCategoryPresenter;
import org.jboss.pressgang.ccms.ui.client.local.mvp.presenter.category.CategoriesFilteredResultsAndCategoryPresenter.Display;
import org.jboss.pressgang.ccms.ui.client.local.mvp.presenter.category.CategoryFilteredResultsPresenter;
import org.jboss.pressgang.ccms.ui.client.local.mvp.presenter.category.CategoryPresenter;
import org.jboss.pressgang.ccms.ui.client.local.mvp.presenter.category.CategoryTagPresenter;
import org.jboss.pressgang.ccms.ui.client.local.mvp.view.base.BaseTemplateViewInterface;
import org.jboss.pressgang.ccms.ui.client.local.mvp.view.category.CategoryViewInterface;
import org.jboss.pressgang.ccms.ui.client.local.preferences.Preferences;
import org.jboss.pressgang.ccms.ui.client.local.resources.strings.PressGangCCMSUI;
import org.jboss.pressgang.ccms.ui.client.local.restcalls.BaseRestCallback;
import org.jboss.pressgang.ccms.ui.client.local.restcalls.RESTCalls;
import org.jboss.pressgang.ccms.ui.client.local.restcalls.RESTCalls.RESTCallback;
import org.jboss.pressgang.ccms.ui.client.local.ui.editor.categoryview.RESTCategoryV1BasicDetailsEditor;
import org.jboss.pressgang.ccms.ui.client.local.utilities.GWTUtilities;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;

/**
 * The component that adds logic to the category search and edit view.
 * 
 * @author Matthew Casperson
 * 
 */
@Dependent
public class CategoriesFilteredResultsAndCategoryComponent
        extends
        BaseSearchAndEditComponent<CategoryFilteredResultsPresenter.Display, CategoriesFilteredResultsAndCategoryPresenter.Display, RESTCategoryV1, RESTCategoryCollectionV1, RESTCategoryCollectionItemV1, CategoryViewInterface, CategoryPresenter.Display, RESTCategoryV1BasicDetailsEditor>
        implements CategoriesFilteredResultsAndCategoryPresenter.LogicComponent {

    @Inject
    private HandlerManager eventBus;

    private CategoryTagPresenter.Display tagDisplay;
    private CategoryTagPresenter.LogicComponent tagComponent;

    /**
     * Used when moving children up and down
     */
    private final SetNewChildSortCallback<RESTTagInCategoryV1, RESTTagInCategoryCollectionV1, RESTTagInCategoryCollectionItemV1> sortCallback = new SetNewChildSortCallback<RESTTagInCategoryV1, RESTTagInCategoryCollectionV1, RESTTagInCategoryCollectionItemV1>() {

        @Override
        public void setSort(final RESTTagInCategoryCollectionItemV1 child, final int index) {
            child.getItem().explicitSetRelationshipSort(index);
            /* Set any unchanged items to updated */
            if (RESTBaseCollectionItemV1.UNCHANGED_STATE.equals(child.getState())) {
                child.setState(RESTBaseUpdateCollectionItemV1.UPDATE_STATE);
            }
        }
    };

    @Override
    public void bind(final int topicId, final String pageId,
            final CategoryFilteredResultsPresenter.Display filteredResultsDisplay,
            final CategoryFilteredResultsPresenter.LogicCompnent filteredResultsComponent,
            final CategoryPresenter.Display entityPropertiesView, final CategoryTagPresenter.Display tagDisplay,
            final CategoryTagPresenter.LogicComponent tagComponent,
            final CategoriesFilteredResultsAndCategoryPresenter.Display display, final BaseTemplateViewInterface waitDisplay) {

        this.tagDisplay = tagDisplay;
        this.tagComponent = tagComponent;

        /* A call back used to get a fresh copy of the entity that was selected */
        final GetNewEntityCallback<RESTCategoryV1> getNewEntityCallback = new GetNewEntityCallback<RESTCategoryV1>() {

            @Override
            public void getNewEntity(final Integer id, final DisplayNewEntityCallback<RESTCategoryV1> displayCallback) {
                final RESTCallback<RESTCategoryV1> callback = new BaseRestCallback<RESTCategoryV1, BaseTemplateViewInterface>(
                        waitDisplay, new BaseRestCallback.SuccessAction<RESTCategoryV1, BaseTemplateViewInterface>() {
                            @Override
                            public void doSuccessAction(final RESTCategoryV1 retValue, final BaseTemplateViewInterface display) {
                                displayCallback.displayNewEntity(retValue);
                            }
                        });
                RESTCalls.getCategory(callback, id);
            }
        };

        super.bind(topicId, pageId, Preferences.CATEGORY_VIEW_MAIN_SPLIT_WIDTH, entityPropertiesView, entityPropertiesView,
                filteredResultsDisplay, filteredResultsComponent, display, waitDisplay, getNewEntityCallback);

        /* Bind the logic to add and remove possible children */
        tagComponent
                .bindPossibleChildrenListButtonClicks(
                        new GetExistingCollectionCallback<RESTTagInCategoryV1, RESTTagInCategoryCollectionV1, RESTTagInCategoryCollectionItemV1>() {

                            @Override
                            public RESTTagInCategoryCollectionV1 getExistingCollection() {
                                return filteredResultsComponent.getProviderData().getDisplayedItem().getItem().getTags();
                            }

                        }, new AddPossibleChildCallback<RESTTagV1, RESTTagCollectionV1, RESTTagCollectionItemV1>() {

                            @Override
                            public void createAndAddChild(final RESTTagCollectionItemV1 copy) {

                                tagComponent.setSortOrderOfChildren(sortCallback);

                                final RESTTagInCategoryV1 newChild = new RESTTagInCategoryV1();
                                newChild.setId(copy.getItem().getId());
                                newChild.setName(copy.getItem().getName());
                                newChild.explicitSetRelationshipSort(Constants.NEW_CHILD_SORT_ORDER);
                                filteredResultsComponent.getProviderData().getDisplayedItem().getItem().getTags()
                                        .addNewItem(newChild);
                            }

                        }, new UpdateAfterChildModfiedCallback() {

                            @Override
                            public void updateAfterChidModfied() {
                                /*
                                 * refresh the list of tags in the category
                                 */
                                tagComponent.refreshExistingChildList(filteredResultsComponent.getProviderData()
                                        .getDisplayedItem().getItem());

                                /*
                                 * refresh the list of possible tags
                                 */
                                tagComponent.refreshPossibleChildList();
                            }

                        });
        bindExistingChildrenRowClick();
    }

    @Override
    protected void loadAdditionalDisplayedItemData() {

        /* Display the tags that are added to the category */
        tagComponent.refreshExistingChildList(filteredResultsComponent.getProviderData().getDisplayedItem().getItem());

        /* Get a new collection of tags */
        tagComponent.refreshPossibleChildrenDataAndList();
    }

    @Override
    protected void initializeViews(final List<CategoryViewInterface> filter) {
        /* We need to initialize the view so the celltable buttons can display the correct labels */
        if (viewIsInFilter(filter, tagDisplay))
            tagDisplay.initialize(filteredResultsComponent.getProviderData().getDisplayedItem().getItem(), false);

        /* Initialize the properties view */
        if (viewIsInFilter(filter, entityPropertiesView))
            entityPropertiesView
                    .initialize(this.filteredResultsComponent.getProviderData().getDisplayedItem().getItem(), false);
    }

    private void bindExistingChildrenRowClick() {
        tagDisplay.getExistingChildUpButtonColumn().setFieldUpdater(
                new FieldUpdater<RESTTagInCategoryCollectionItemV1, String>() {

                    @Override
                    public void update(final int index, final RESTTagInCategoryCollectionItemV1 object, final String value) {
                        tagComponent.moveTagsUpAndDown(filteredResultsComponent.getProviderData().getDisplayedItem().getItem(),
                                object, false, sortCallback);
                    }

                });

        tagDisplay.getExistingChildDownButtonColumn().setFieldUpdater(
                new FieldUpdater<RESTTagInCategoryCollectionItemV1, String>() {

                    /**
                     * Swap the sort value for the tag that was selected with the tag below it.
                     */
                    @Override
                    public void update(final int index, final RESTTagInCategoryCollectionItemV1 object, final String value) {
                        tagComponent.moveTagsUpAndDown(filteredResultsComponent.getProviderData().getDisplayedItem().getItem(),
                                object, true, sortCallback);
                    }
                });
    }

    @Override
    protected void bindActionButtons() {
        /**
         * A click handler used to display the category fields view
         */
        final ClickHandler categoryDetailsClickHandler = new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                switchView(entityPropertiesView);
            }

        };

        /**
         * A click handler used to display the category tags view
         */
        final ClickHandler categoryTagsClickHandler = new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                switchView(tagDisplay);
            }

        };

        /**
         * A click handler used to save any changes to the category
         */
        final ClickHandler saveClickHandler = new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {

                /* Was the tag we just saved a new tag? */
                final boolean wasNewEntity = filteredResultsComponent.getProviderData().getDisplayedItem().returnIsAddItem();

                /* Sync the UI to the underlying object */
                entityPropertiesView.getDriver().flush();

                final RESTCallback<RESTCategoryV1> callback = new BaseRestCallback<RESTCategoryV1, Display>(display,
                        new BaseRestCallback.SuccessAction<RESTCategoryV1, Display>() {
                            @Override
                            public void doSuccessAction(final RESTCategoryV1 retValue, final Display display) {
                                retValue.cloneInto(filteredResultsComponent.getProviderData().getSelectedItem().getItem(), true);
                                retValue.cloneInto(filteredResultsComponent.getProviderData().getDisplayedItem().getItem(),
                                        true);

                                /* This category is no longer a new category */
                                filteredResultsComponent.getProviderData().getDisplayedItem()
                                        .setState(RESTBaseCollectionItemV1.UNCHANGED_STATE);

                                tagComponent.refreshExistingChildList(filteredResultsComponent.getProviderData()
                                        .getDisplayedItem().getItem());
                                tagComponent.refreshPossibleChildrenDataAndList();

                                updateDisplayAfterSave(wasNewEntity);

                                Window.alert(PressGangCCMSUI.INSTANCE.SaveSuccess());
                            }
                        }) {
                };

                if (filteredResultsComponent.getProviderData().getDisplayedItem() != null) {

                    /*
                     * If this is a new category, it needs to be saved in order to get the tag id to complete the category
                     * updates. Upon success, the categories will be updated.
                     */
                    final boolean unsavedTagChanges = unsavedCategoryChanges() || unsavedTagChanges();

                    if (unsavedTagChanges) {

                        final RESTCategoryV1 category = new RESTCategoryV1();
                        category.setId(filteredResultsComponent.getProviderData().getDisplayedItem().getItem().getId());
                        category.explicitSetName(filteredResultsComponent.getProviderData().getDisplayedItem().getItem()
                                .getName());
                        category.explicitSetDescription(filteredResultsComponent.getProviderData().getDisplayedItem().getItem()
                                .getDescription());
                        category.explicitSetTags(filteredResultsComponent.getProviderData().getDisplayedItem().getItem()
                                .getTags());

                        if (wasNewEntity) {
                            RESTCalls.createCategory(callback, category);
                        } else {
                            RESTCalls.saveCategory(callback, category);
                        }
                    } else {
                        Window.alert(PressGangCCMSUI.INSTANCE.NoUnsavedChanges());
                    }
                }
            }
        };

        for (final CategoryViewInterface view : new CategoryViewInterface[] { entityPropertiesView, tagDisplay }) {
            view.getDetails().addClickHandler(categoryDetailsClickHandler);
            view.getChildren().addClickHandler(categoryTagsClickHandler);
            view.getSave().addClickHandler(saveClickHandler);
        }

    }

    /**
     * Binds behaviour to the tag search and list view
     */
    @Override
    protected void bindFilteredResultsButtons() {
        filteredResultsDisplay.getEntitySearch().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                if (isOKToProceed())
                    eventBus.fireEvent(new CategoriesFilteredResultsAndCategoryViewEvent(filteredResultsComponent.getQuery(),
                            GWTUtilities.isEventToOpenNewWindow(event)));
            }
        });

        filteredResultsDisplay.getCreate().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {

                /* The 'selected' tag will be blank. This gives us something to compare to when checking for unsaved changes */
                final RESTCategoryV1 selectedEntity = new RESTCategoryV1();
                selectedEntity.setId(Constants.NULL_ID);
                final RESTCategoryCollectionItemV1 selectedTagWrapper = new RESTCategoryCollectionItemV1(selectedEntity);

                /* The displayed tag will also be blank. This is the object that our data will be saved into */
                final RESTCategoryV1 displayedEntity = new RESTCategoryV1();
                displayedEntity.setId(Constants.NULL_ID);
                displayedEntity.setTags(new RESTTagInCategoryCollectionV1());
                final RESTCategoryCollectionItemV1 displayedTagWrapper = new RESTCategoryCollectionItemV1(displayedEntity,
                        RESTBaseCollectionItemV1.ADD_STATE);

                filteredResultsComponent.getProviderData().setSelectedItem(selectedTagWrapper);
                filteredResultsComponent.getProviderData().setDisplayedItem(displayedTagWrapper);

                tagComponent.refreshExistingChildList(filteredResultsComponent.getProviderData().getDisplayedItem().getItem());
                tagComponent.refreshPossibleChildrenDataAndList();

                initializeViews();

                switchView(lastDisplayedView == null ? entityPropertiesView : lastDisplayedView);
            }
        });
    }

    @Override
    public boolean hasUnsavedChanges() {
        /* sync the UI with the underlying tag */
        if (filteredResultsComponent.getProviderData().getDisplayedItem() != null) {
            entityPropertiesView.getDriver().flush();

            return unsavedCategoryChanges() || unsavedTagChanges();
        }
        return false;
    }

    /**
     * Compare the selected and displayed category, and see if any of the fields have changed
     * 
     * @return true if there are unsaved changes, false otherwise
     */
    private boolean unsavedCategoryChanges() {
        return !(stringEqualsEquatingNullWithEmptyString(filteredResultsComponent.getProviderData().getSelectedItem().getItem()
                .getName(), filteredResultsComponent.getProviderData().getDisplayedItem().getItem().getName()) && stringEqualsEquatingNullWithEmptyString(
                filteredResultsComponent.getProviderData().getSelectedItem().getItem().getDescription(),
                filteredResultsComponent.getProviderData().getDisplayedItem().getItem().getDescription()));
    }

    /**
     * Check to see if there are any added, removed or modified tags in the category
     * 
     * @return true if there are modified tags, false otherwise
     */
    private boolean unsavedTagChanges() {
        return !filteredResultsComponent.getProviderData().getDisplayedItem().getItem().getTags()
                .returnDeletedAddedAndUpdatedCollectionItems().isEmpty();
    }

    @Override
    protected void switchView(final CategoryViewInterface displayedView) {

        super.switchView(displayedView);

        /* Show any wait dialogs from the new view, and update the view with the currently displayed entity */
        if (displayedView != null) {
            displayedView.setViewShown(true);
        }

        lastDisplayedView = displayedView;
    }
}
