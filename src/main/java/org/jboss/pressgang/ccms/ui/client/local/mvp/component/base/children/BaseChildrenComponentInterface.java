package org.jboss.pressgang.ccms.ui.client.local.mvp.component.base.children;

import org.jboss.pressgang.ccms.rest.v1.collections.base.RESTBaseCollectionItemV1;
import org.jboss.pressgang.ccms.rest.v1.entities.base.RESTBasePrimaryEntityV1;
import org.jboss.pressgang.ccms.ui.client.local.mvp.component.base.Component;
import org.jboss.pressgang.ccms.ui.client.local.mvp.presenter.base.EditableView;
import org.jboss.pressgang.ccms.ui.client.local.mvp.view.base.children.BaseChildrenViewInterface;
import org.jboss.pressgang.ccms.ui.client.local.ui.ProviderUpdateData;
import org.jboss.pressgang.ccms.ui.client.local.utilities.EnhancedAsyncDataProvider;

/**
 * 
 * @author matthew
 *
 * @param <T> The type of the view
 * @param <U> The type of the possible children
 * @param <V> The type of the existing children
 */
public interface BaseChildrenComponentInterface<T extends BaseChildrenViewInterface<U, V>, U extends RESTBasePrimaryEntityV1, V extends RESTBaseCollectionItemV1>
        extends EditableView, Component<T> {
    
    ProviderUpdateData<V> getPossibleChildrenProviderData();

    void setPossibleChildrenProviderData(final ProviderUpdateData<V> providerData);
    
    void getEntityList();
    
    EnhancedAsyncDataProvider<V> generatePossibleChildrenrovider();
}
