package org.jboss.pressgang.ccms.ui.client.local.mvp.view.contentspec;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.user.client.ui.TextArea;
import edu.ycp.cs.dh.acegwt.client.ace.AceEditor;
import org.jboss.pressgang.ccms.rest.v1.entities.contentspec.RESTContentSpecV1;
import org.jboss.pressgang.ccms.ui.client.local.mvp.presenter.contentspec.ContentSpecErrorPresenter;
import org.jboss.pressgang.ccms.ui.client.local.mvp.presenter.contentspec.ContentSpecPresenter;
import org.jboss.pressgang.ccms.ui.client.local.mvp.view.base.BaseTemplateView;
import org.jboss.pressgang.ccms.ui.client.local.resources.strings.PressGangCCMSUI;
import org.jboss.pressgang.ccms.ui.client.local.ui.editor.contentspec.RESTContentSpecV1TextEditor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.google.common.base.Preconditions.checkArgument;

/**
* The view that displays the text content of a content spec
 */
public class ContentSpecErrorView extends BaseTemplateView implements ContentSpecErrorPresenter.Display {

    private final TextArea errors = new TextArea();

    public ContentSpecErrorView() {
        super(PressGangCCMSUI.INSTANCE.PressGangCCMS(), PressGangCCMSUI.INSTANCE.ContentSpecTextEdit());
        errors.setReadOnly(true);
        this.getPanel().setWidget(errors);
    }

    public void display(@Nullable final RESTContentSpecV1 contentSpec) {
        errors.setText("");

        if (contentSpec != null && contentSpec.getErrors() != null) {
            errors.setText(contentSpec.getErrors());
        }
    }
}
