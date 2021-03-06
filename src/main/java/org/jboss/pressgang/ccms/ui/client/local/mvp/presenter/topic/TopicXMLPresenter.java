package org.jboss.pressgang.ccms.ui.client.local.mvp.presenter.topic;

import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import edu.ycp.cs.dh.acegwt.client.ace.AceEditor;
import org.jboss.pressgang.ccms.rest.v1.entities.base.RESTBaseTopicV1;
import org.jboss.pressgang.ccms.ui.client.local.constants.ServiceConstants;
import org.jboss.pressgang.ccms.ui.client.local.mvp.presenter.base.BaseTemplatePresenter;
import org.jboss.pressgang.ccms.ui.client.local.mvp.view.base.BasePopulatedEditorViewInterface;
import org.jboss.pressgang.ccms.ui.client.local.mvp.view.base.BaseTemplateViewInterface;
import org.jboss.pressgang.ccms.ui.client.local.ui.editor.topicview.RESTTopicV1XMLEditor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

import static org.jboss.pressgang.ccms.ui.client.local.utilities.GWTUtilities.clearContainerAndAddTopLevelPanel;
import static org.jboss.pressgang.ccms.ui.client.local.utilities.GWTUtilities.removeHistoryToken;

public class TopicXMLPresenter extends BaseTemplatePresenter {

    // Empty interface declaration, similar to UiBinder
    public interface TopicXMLPresenterDriver extends SimpleBeanEditorDriver<RESTBaseTopicV1<?, ?, ?>, RESTTopicV1XMLEditor> {
    }

    public interface Display extends BaseTemplateViewInterface, BasePopulatedEditorViewInterface<RESTBaseTopicV1<?, ?, ?>, RESTBaseTopicV1<?, ?, ?>, RESTTopicV1XMLEditor> {

        interface PlainTextXMLDialog {
            PushButton getOK();

            PushButton getCancel();

            void setText(final String text);

            String getText();

            DialogBox getDialogBox();
        }

        /**
         * The interface that defines the tag selection dialog box
         *
         * @author Matthew Casperson
         */
        interface XmlTagsDialog {
            PushButton getOK();

            PushButton getCancel();

            PushButton getMoreInfo();

            ListBox getOptions();

            DialogBox getDialogBox();

            void setSuggestions(final List<String> suggestions);
        }

        interface XmlTemplatesDialog {
            PushButton getOK();

            PushButton getCancel();

            ListBox getOptions();

            DialogBox getDialogBox();

            void setSuggestions(final Map<String, String> suggestions);
        }

        interface CSPTopicDetailsDialog {
            PushButton getOK();

            PushButton getCancel();

            TextBox getIds();

            DialogBox getDialogBox();
        }

        XmlTagsDialog getXmlTagsDialog();

        CSPTopicDetailsDialog getCSPTopicDetailsDialog();

        XmlTemplatesDialog getXmlTemplatesDialog();

        PlainTextXMLDialog getPlainTextXMLDialog();

        ToggleButton getLineWrap();

        ToggleButton getShowInvisibles();

        AceEditor getEditor();
    }

    public static final String HISTORY_TOKEN = "TopicXMLView";

    @Nullable
    private Integer topicId;

    @Inject
    private Display display;

    @NotNull
    public Display getDisplay() {
        return display;
    }

    @Override
    public void parseToken(@NotNull final String searchToken) {
        try {
            topicId = Integer.parseInt(removeHistoryToken(searchToken, HISTORY_TOKEN));
        } catch (@NotNull final NumberFormatException ex) {
            topicId = null;
        }

    }

    @Override
    public void go(@NotNull final HasWidgets container) {
        clearContainerAndAddTopLevelPanel(container, display);
        bindExtended(ServiceConstants.TOPIC_XML_EDIT_TOPIC, HISTORY_TOKEN);
    }

    public void bindExtended(final int helpTopicId, @NotNull final String pageId) {
        super.bind(helpTopicId, pageId, display);
        bindAceEditorButtons();
    }

    /**
     * Bind the button clicks for the ACE editor buttons
     */
    private void bindAceEditorButtons() {
        display.getLineWrap().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(@NotNull final ClickEvent event) {
                display.getEditor().setUseWrapMode(!display.getEditor().getUserWrapMode());
                display.getLineWrap().setDown(display.getEditor().getUserWrapMode());
            }
        });

        display.getShowInvisibles().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(@NotNull final ClickEvent event) {
                display.getEditor().setShowInvisibles(!display.getEditor().getShowInvisibles());
                display.getShowInvisibles().setDown(display.getEditor().getShowInvisibles());
            }
        });
    }

}
