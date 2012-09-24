package org.jboss.pressgang.ccms.ui.client.local.mvp.presenter.topic;

import javax.enterprise.context.Dependent;

import org.jboss.pressgang.ccms.ui.client.local.mvp.presenter.base.TemplatePresenter;
import org.jboss.pressgang.ccms.ui.client.local.mvp.view.topic.TopicViewInterface;

import com.google.gwt.user.client.ui.HasWidgets;

@Dependent
public class TopicRenderedPresenter extends TemplatePresenter {
    public static final String HISTORY_TOKEN = "TopicRenderedView";
    
    private String topicId;

    public interface Display extends TopicViewInterface {

    }

    @Override
    public void go(final HasWidgets container) {
        // TODO Auto-generated method stub

    }

    @Override
    public void parseToken(final String historyToken) {
        // TODO Auto-generated method stub
    }
}