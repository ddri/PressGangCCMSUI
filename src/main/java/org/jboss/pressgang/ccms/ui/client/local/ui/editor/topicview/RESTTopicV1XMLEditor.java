package org.jboss.pressgang.ccms.ui.client.local.ui.editor.topicview;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.user.client.ui.SimplePanel;
import edu.ycp.cs.dh.acegwt.client.ace.AceEditor;
import edu.ycp.cs.dh.acegwt.client.ace.AceEditorMode;
import edu.ycp.cs.dh.acegwt.client.ace.AceEditorTheme;
import org.jboss.pressgang.ccms.rest.v1.entities.base.RESTBaseTopicV1;
import org.jboss.pressgang.ccms.ui.client.local.constants.CSSConstants;

public class RESTTopicV1XMLEditor extends SimplePanel implements Editor<RESTBaseTopicV1<?, ?, ?>> {
    public final AceEditor xml = new AceEditor(true);

    public RESTTopicV1XMLEditor(final boolean readOnly) {
        this.addStyleName(CSSConstants.TOPIC_XML_VIEW_ACE_PANEL);
        xml.addStyleName(CSSConstants.TOPIC_XML_VIEW_XML_FIELD);

        xml.setReadOnly(readOnly);
        xml.setMode(AceEditorMode.XML);
        xml.setTheme(AceEditorTheme.ECLIPSE);

        this.setWidget(xml);
    }
}
