package org.openfast;

import org.openfast.error.ErrorHandler;
import org.openfast.logging.FastMessageLogger;
import org.openfast.template.MessageTemplate;
import org.openfast.template.TemplateRegistry;

public interface OpenFastContext {
    public int getTemplateId(MessageTemplate template);

    public MessageTemplate getTemplate(int templateId);

    public void registerTemplate(int templateId, MessageTemplate template);

    public void setErrorHandler(ErrorHandler errorHandler);

    public TemplateRegistry getTemplateRegistry();

    public void setTemplateRegistry(TemplateRegistry registry);
    
    public FastMessageLogger getLogger();
    
    public void setLogger(FastMessageLogger logger);
}
