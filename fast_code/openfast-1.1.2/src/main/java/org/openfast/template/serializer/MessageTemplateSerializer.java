package org.openfast.template.serializer;

import java.io.OutputStream;
import org.openfast.template.MessageTemplate;

public interface MessageTemplateSerializer {
    public void serialize(MessageTemplate[] templates, OutputStream destination);
}
