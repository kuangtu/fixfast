package org.openfast.submitted;

import java.io.InputStream;

import org.openfast.Message;
import org.openfast.MessageInputStream;
import org.openfast.template.MessageTemplate;
import org.openfast.template.loader.MessageTemplateLoader;
import org.openfast.template.loader.XMLMessageTemplateLoader;
import org.openfast.test.OpenFastTestCase;
import org.openfast.util.RecordingInputStream;

public class ComposedDecimalNullPointerTest extends OpenFastTestCase {
    public void testNullPointerOnTwinValue() throws Exception {
        InputStream templateSource = resource("FPL/FASTTestTemplate.xml");
        MessageTemplateLoader templateLoader = new XMLMessageTemplateLoader();
        MessageTemplate[] templates = templateLoader.load(templateSource);

        InputStream is = resource("FPL/messages.fast");
        MessageInputStream mis = new MessageInputStream(new RecordingInputStream(is));
        mis.registerTemplate(35, templates[0]);
        Message msg = mis.readMessage();
        msg = mis.readMessage();
        msg = mis.readMessage();
        assertEquals(templates[0], msg.getTemplate());
    }
}
