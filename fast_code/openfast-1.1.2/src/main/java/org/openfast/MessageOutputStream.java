/*
The contents of this file are subject to the Mozilla Public License
Version 1.1 (the "License"); you may not use this file except in
compliance with the License. You may obtain a copy of the License at
http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS"
basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
License for the specific language governing rights and limitations
under the License.

The Original Code is OpenFAST.

The Initial Developer of the Original Code is The LaSalle Technology
Group, LLC.  Portions created by The LaSalle Technology Group, LLC
are Copyright (C) The LaSalle Technology Group, LLC. All Rights Reserved.

Contributor(s): Jacob Northey <jacob@lasalletech.com>
                Craig Otis <cotis@lasalletech.com>
 */
package org.openfast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openfast.codec.FastEncoder;
import org.openfast.error.FastConstants;
import org.openfast.logging.FastMessageLogger.Direction;
import org.openfast.template.MessageTemplate;
import org.openfast.template.TemplateRegistry;

public class MessageOutputStream implements MessageStream {
    private final OutputStream out;
    private final FastEncoder encoder;
    private final Context context;
    private List handlers = Collections.EMPTY_LIST;
    private Map templateHandlers = Collections.EMPTY_MAP;
    private MessageBlockWriter blockWriter = MessageBlockWriter.NULL;

    public MessageOutputStream(OutputStream outputStream) {
        this(outputStream, new Context());
    }

    public MessageOutputStream(OutputStream outputStream, Context context) {
        this.out = outputStream;
        this.encoder = new FastEncoder(context);
        this.context = context;
    }

    public void writeMessage(Message message) {
        writeMessage(message, false);
    }

    public void writeMessage(Message message, boolean flush) {
        try {
            byte[] data = encodeMessage(message);
            if ((data == null) || (data.length == 0)) {
                return;
            }
            blockWriter.writeBlockLength(out, message, data);
            out.write(data);
            if (flush)
                out.flush();
            getContext().getLogger().log(message, data, Direction.OUTBOUND);
        } catch (IOException e) {
            Global.handleError(FastConstants.IO_ERROR, "An IO error occurred while writing message " + message, e);
        }
    }

    private byte[] encodeMessage(Message message) {
        if (context.isTraceEnabled())
            context.startTrace();
        if (!handlers.isEmpty()) {
            for (int i = 0; i < handlers.size(); i++) {
                ((MessageHandler) handlers.get(i)).handleMessage(message, context, encoder);
            }
        }
        if (templateHandlers.containsKey(message.getTemplate())) {
            ((MessageHandler) templateHandlers.get(message.getTemplate())).handleMessage(message, context, encoder);
        }
        return encoder.encode(message);
    }

    public void reset() {
        encoder.reset();
    }

    public void registerTemplate(int templateId, MessageTemplate template) {
        encoder.registerTemplate(templateId, template);
    }

    public void close() {
        try {
            out.close();
        } catch (IOException e) {
            Global.handleError(FastConstants.IO_ERROR, "An error occurred while closing output stream.", e);
        }
    }

    public OutputStream getUnderlyingStream() {
        return out;
    }

    public void addMessageHandler(MessageTemplate template, MessageHandler handler) {
        if (templateHandlers == Collections.EMPTY_MAP) {
            templateHandlers = new HashMap();
        }
        templateHandlers.put(template, handler);
    }

    public void addMessageHandler(MessageHandler handler) {
        if (handlers == Collections.EMPTY_LIST) {
            handlers = new ArrayList(4);
        }
        handlers.add(handler);
    }

    public void setTemplateRegistry(TemplateRegistry registry) {
        context.setTemplateRegistry(registry);
    }

    /**
     * Specify a block writer implementation that is used to prefix messages
     * with a block size
     * 
     * @param blockWriter
     */
    public void setBlockWriter(MessageBlockWriter blockWriter) {
        this.blockWriter = blockWriter;
    }

    public TemplateRegistry getTemplateRegistry() {
        return context.getTemplateRegistry();
    }

    public Context getContext() {
        return context;
    }
}
