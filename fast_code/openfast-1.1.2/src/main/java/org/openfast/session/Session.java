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
package org.openfast.session;

import java.io.IOException;
import java.net.SocketException;

import org.openfast.Context;
import org.openfast.Message;
import org.openfast.MessageInputStream;
import org.openfast.MessageOutputStream;
import org.openfast.OpenFastContext;
import org.openfast.QName;
import org.openfast.error.ErrorCode;
import org.openfast.error.ErrorHandler;
import org.openfast.error.FastConstants;
import org.openfast.error.FastException;
import org.openfast.template.MessageTemplate;
import org.openfast.template.TemplateRegistry;

public class Session implements ErrorHandler {
    public final MessageInputStream in;
    public final MessageOutputStream out;
    private final SessionProtocol protocol;
    private final Connection connection;
    private Client client;
    private MessageListener messageListener;
    private boolean listening;
    private Thread listeningThread;
    private ErrorHandler errorHandler = ErrorHandler.DEFAULT;
    private SessionListener sessionListener = SessionListener.NULL;
    private OpenFastContext context;

    public Session(Connection connection, SessionProtocol protocol, TemplateRegistry inboundRegistry,
            TemplateRegistry outboundRegistry) {
        this.context = new BasicOpenFastContext();
        Context inContext = new Context(context);
        inContext.getTemplateRegistry().registerAll(inboundRegistry);

        Context outContext = new Context(context);
        outContext.getTemplateRegistry().registerAll(outboundRegistry);
        inContext.setErrorHandler(this);

        this.connection = connection;
        this.protocol = protocol;
        try {
            this.in = new MessageInputStream(connection.getInputStream(), inContext);
            this.out = new MessageOutputStream(connection.getOutputStream(), outContext);
        } catch (IOException e) {
            errorHandler.error(null, "Error occurred in connection.", e);
            throw new IllegalStateException(e);
        }

        protocol.configureSession(this);
    }

    // INITIATOR
    public void close() throws FastConnectionException {
        listening = false;
        out.writeMessage(protocol.getCloseMessage());
        in.close();
        out.close();
    }

    // RESPONDER
    public void close(ErrorCode alertCode) {
        listening = false;
        in.close();
        out.close();
        sessionListener.onClose();
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Client getClient() {
        return client;
    }

    public void error(ErrorCode code, String message) {
        if (code.equals(FastConstants.D9_TEMPLATE_NOT_REGISTERED)) {
            code = SessionConstants.TEMPLATE_NOT_SUPPORTED;
            message = "Template Not Supported: " + message;
        }
        protocol.onError(this, code, message);
        errorHandler.error(code, message);
    }

    public void error(ErrorCode code, String message, Throwable t) {
        protocol.onError(this, code, message);
        errorHandler.error(code, message, t);
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        if (errorHandler == null) {
            this.errorHandler = ErrorHandler.NULL;
        }
        this.errorHandler = errorHandler;
    }

    public OpenFastContext getContext() {
        return context;
    }

    public void reset() {
        out.reset();
        in.reset();
        out.writeMessage(protocol.getResetMessage());
    }

    public Connection getConnection() {
        return connection;
    }

    public void setMessageHandler(MessageListener messageListener) {
        this.messageListener = messageListener;
        setListening(true);
    }

    private void listenForMessages() {
        if (listeningThread == null) {
            Runnable messageReader = new Runnable() {
                public void run() {
                    while (listening) {
                        try {
                            Message message = in.readMessage();

                            if (message == null) {
                                notifySessionClosed();
                                break;
                            }
                            if (protocol.isProtocolMessage(message)) {
                                protocol.handleMessage(Session.this, message);
                            } else if (messageListener != null) {
                                messageListener.onMessage(Session.this, message);
                            } else {
                                throw new IllegalStateException(
                                        "Received non-protocol message without a message listener.");
                            }
                        } catch (Exception e) {
                            Throwable cause = e.getCause();
                            if (cause != null && cause.getClass().equals(SocketException.class)) {
                                notifySessionClosed();
                                errorHandler.error(FastConstants.IO_ERROR, cause.getMessage(), cause);
                            } else if (e instanceof FastException) {
                                FastException fastException = ((FastException) e);
                                errorHandler.error(fastException.getCode(), fastException.getMessage(), e);
                            } else {
                                errorHandler.error(FastConstants.GENERAL_ERROR, e.getMessage(), e);
                            }
                        }
                    }
                }

                private void notifySessionClosed() {
                    listening = false;
                    if (sessionListener != null) {
                        sessionListener.onClose();
                    }
                }
            };
            listeningThread = new Thread(messageReader, "FAST Session Message Reader");
        }
        if (listeningThread.isAlive())
            return;
        listeningThread.start();
    }

    public void setListening(boolean listening) {
        this.listening = listening;
        if (listening)
            listenForMessages();
    }

    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    public void sendTemplates(TemplateRegistry registry) {
        if (!protocol.supportsTemplateExchange()) {
            throw new UnsupportedOperationException("The procotol " + protocol + " does not support template exchange.");
        }
        MessageTemplate[] templates = registry.getTemplates();
        for (int i = 0; i < templates.length; i++) {
            MessageTemplate template = templates[i];
            out.writeMessage(protocol.createTemplateDefinitionMessage(template));
            out.writeMessage(protocol.createTemplateDeclarationMessage(template, registry.getId(template)));
            if (!out.getTemplateRegistry().isRegistered(template))
                out.registerTemplate(registry.getId(template), template);
        }
    }

    public void addDynamicTemplateDefinition(MessageTemplate template) {
        in.getTemplateRegistry().define(template);
        out.getTemplateRegistry().define(template);
    }

    public void registerDynamicTemplate(QName templateName, int id) {
        if (!in.getTemplateRegistry().isDefined(templateName))
            throw new IllegalStateException("Template " + templateName + " has not been defined.");
        in.getTemplateRegistry().register(id, templateName);
        if (!out.getTemplateRegistry().isDefined(templateName))
            throw new IllegalStateException("Template " + templateName + " has not been defined.");
        out.getTemplateRegistry().register(id, templateName);
    }

    public void setSessionListener(SessionListener sessionListener) {
        this.sessionListener = sessionListener;
    }
}
