package org.jboss.pressgang.ccms.ui.client.local.mvp.component.category;

import org.jboss.errai.bus.client.api.Message;
import org.jboss.pressgang.ccms.rest.v1.collections.RESTCategoryCollectionV1;
import org.jboss.pressgang.ccms.rest.v1.collections.items.RESTCategoryCollectionItemV1;
import org.jboss.pressgang.ccms.rest.v1.collections.items.RESTTagCollectionItemV1;
import org.jboss.pressgang.ccms.ui.client.local.mvp.component.base.ComponentBase;
import org.jboss.pressgang.ccms.ui.client.local.mvp.component.base.filteredresults.BaseFilteredResultsComponent;
import org.jboss.pressgang.ccms.ui.client.local.mvp.presenter.category.CategoryFilteredResultsPresenter;
import org.jboss.pressgang.ccms.ui.client.local.mvp.presenter.category.CategoryFilteredResultsPresenter.Display;
import org.jboss.pressgang.ccms.ui.client.local.mvp.presenter.category.CategoryFilteredResultsPresenter.LogicCompnent;
import org.jboss.pressgang.ccms.ui.client.local.mvp.view.base.BaseTemplateViewInterface;
import org.jboss.pressgang.ccms.ui.client.local.resources.strings.PressGangCCMSUI;
import org.jboss.pressgang.ccms.ui.client.local.restcalls.RESTCalls;
import org.jboss.pressgang.ccms.ui.client.local.restcalls.RESTCalls.RESTCallback;
import org.jboss.pressgang.ccms.ui.client.local.ui.ProviderUpdateData;
import org.jboss.pressgang.ccms.ui.client.local.utilities.EnhancedAsyncDataProvider;

import com.google.gwt.user.client.Window;
import com.google.gwt.view.client.HasData;

public class CategoryFilteredResultsComponent extends
        BaseFilteredResultsComponent<CategoryFilteredResultsPresenter.Display, RESTCategoryCollectionItemV1> implements
        LogicCompnent {

    @Override
    public void bind(final String queryString, final CategoryFilteredResultsPresenter.Display display,
            final BaseTemplateViewInterface waitDisplay) {
        super.bind(display, waitDisplay);

        display.setProvider(generateListProvider(queryString, display, waitDisplay));
    }

    /**
     * @return A provider to be used for the category display list
     */
    @Override
    protected EnhancedAsyncDataProvider<RESTCategoryCollectionItemV1> generateListProvider(final String queryString, final Display display,
            final BaseTemplateViewInterface waitDisplay) {
        final EnhancedAsyncDataProvider<RESTCategoryCollectionItemV1> provider = new EnhancedAsyncDataProvider<RESTCategoryCollectionItemV1>() {
            @Override
            protected void onRangeChanged(final HasData<RESTCategoryCollectionItemV1> list) {

                final RESTCallback<RESTCategoryCollectionV1> callback = new RESTCallback<RESTCategoryCollectionV1>() {
                    @Override
                    public void begin() {
                        resetProvider();
                        display.addWaitOperation();
                    }

                    @Override
                    public void generalException(final Exception ex) {
                        Window.alert(PressGangCCMSUI.INSTANCE.ConnectionError());
                        display.removeWaitOperation();
                    }

                    @Override
                    public void success(final RESTCategoryCollectionV1 retValue) {
                        try {
                            getProviderData().setItems(retValue.getItems());
                            displayAsynchronousList(getProviderData().getItems(), retValue.getSize(), getProviderData()
                                    .getStartRow());
                        } finally {
                            display.removeWaitOperation();
                        }
                    }

                    @Override
                    public void failed(final Message message, final Throwable throwable) {
                        display.removeWaitOperation();
                        Window.alert(PressGangCCMSUI.INSTANCE.ConnectionError());
                    }
                };

                getProviderData().setStartRow(list.getVisibleRange().getStart());
                final int length = list.getVisibleRange().getLength();
                final int end = getProviderData().getStartRow() + length;

                RESTCalls.getCategoriesFromQuery(callback, queryString, getProviderData().getStartRow(), end);
            }
        };
        return provider;
    }

    @Override
    public String getQuery() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void displayQueryElements(String queryString) {
        // TODO Auto-generated method stub
        
    }
}
