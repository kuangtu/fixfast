package org.openfast.session;

import org.openfast.OpenFastContext;
import org.openfast.error.ErrorHandler;
import org.openfast.logging.FastMessageLogger;
import org.openfast.template.MessageTemplate;
import org.openfast.template.TemplateRegistry;

public class BasicOpenFastContext implements OpenFastContext {

    private FastMessageLogger logger;

    public MessageTemplate getTemplate(int templateId) {
        return null;
    }

    public int getTemplateId(MessageTemplate template) {
        return 0;
    }

    public TemplateRegistry getTemplateRegistry() {
        return null;
    }

    public void registerTemplate(int templateId, MessageTemplate template) {

    }

    public void setErrorHandler(ErrorHandler errorHandler) {

    }

    public void setTemplateRegistry(TemplateRegistry registry) {

    }

    public FastMessageLogger getLogger() {
        return this.logger != null ? this.logger : FastMessageLogger.NULL;
    }

    public void setLogger(FastMessageLogger logger) {
        this.logger = logger;
    }

}
