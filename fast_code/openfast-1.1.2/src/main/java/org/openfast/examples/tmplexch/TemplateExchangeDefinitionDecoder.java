package org.openfast.examples.tmplexch;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import org.openfast.Context;
import org.openfast.Message;
import org.openfast.codec.FastDecoder;
import org.openfast.session.SessionConstants;
import org.openfast.template.BasicTemplateRegistry;
import org.openfast.template.MessageTemplate;
import org.openfast.template.serializer.XMLMessageTemplateSerializer;

public class TemplateExchangeDefinitionDecoder {
    private InputStream fastIn;
    private OutputStream out;

    public TemplateExchangeDefinitionDecoder(InputStream fastIn, boolean namespaceAware, OutputStream out) {
        this.fastIn = fastIn;
        this.out = out;
    }

    public void start() throws IOException {
        Context context = new Context();
        BasicTemplateRegistry registry = new BasicTemplateRegistry();
        SessionConstants.SCP_1_1.registerSessionTemplates(registry);
        context.setTemplateRegistry(registry);
        FastDecoder decoder = new FastDecoder(context, fastIn);
        Message message = null;
        List templates = new ArrayList();
        do {
            message = decoder.readMessage();
            if (message != null) {
                templates.add(SessionConstants.SCP_1_1.createTemplateFromMessage(message, registry));
            }
        } while (message != null);
        XMLMessageTemplateSerializer serializer = new XMLMessageTemplateSerializer();
        MessageTemplate[] templateArr = (MessageTemplate[]) templates.toArray(new MessageTemplate[templates.size()]);
        serializer.serialize(templateArr, out);
    }
}
