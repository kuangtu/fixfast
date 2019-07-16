package org.openfast.examples.tmplexch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import org.openfast.Context;
import org.openfast.Message;
import org.openfast.codec.FastEncoder;
import org.openfast.session.SessionConstants;
import org.openfast.template.BasicTemplateRegistry;
import org.openfast.template.MessageTemplate;
import org.openfast.template.TemplateRegistry;
import org.openfast.template.loader.XMLMessageTemplateLoader;

public class TemplateExchangeDefinitionEncoder {
    private final TemplateRegistry templateRegistry;
    private final OutputStream out;

    public TemplateExchangeDefinitionEncoder(File templatesFile, boolean namespaceAware) {
        this(templatesFile, namespaceAware, System.out);
    }
    public TemplateExchangeDefinitionEncoder(File templatesFile, boolean namespaceAware, OutputStream out) {
        XMLMessageTemplateLoader loader = new XMLMessageTemplateLoader(namespaceAware);
        loader.setLoadTemplateIdFromAuxId(true);
        try {
            loader.load(new FileInputStream(templatesFile));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        this.templateRegistry = loader.getTemplateRegistry();
        this.out = out;
    }

    public void start() throws IOException {
        Context context = new Context();
        BasicTemplateRegistry registry = new BasicTemplateRegistry();
        SessionConstants.SCP_1_1.registerSessionTemplates(registry);
        registry.registerAll(templateRegistry);
        context.setTemplateRegistry(registry);
        FastEncoder encoder = new FastEncoder(context);
        MessageTemplate[] templates = templateRegistry.getTemplates();
        for (int i=0; i<templates.length; i++) {
            Message message = SessionConstants.SCP_1_1.createTemplateDefinitionMessage(templates[i]);
            out.write(encoder.encode(message));
        }
    }
}
