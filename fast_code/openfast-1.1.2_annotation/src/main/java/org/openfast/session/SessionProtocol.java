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

import org.openfast.Message;
import org.openfast.error.ErrorCode;
import org.openfast.template.MessageTemplate;
import org.openfast.template.TemplateRegistry;

public interface SessionProtocol {
    public void configureSession(Session session);

    public Session connect(String senderName, Connection connection, TemplateRegistry inboundRegistry,
            TemplateRegistry outboundRegistry, MessageListener messageListener, SessionListener sessionListener);

    public Session onNewConnection(String serverName, Connection connection);

    public void onError(Session session, ErrorCode code, String message);

    public Message getResetMessage();

    public boolean isProtocolMessage(Message message);

    public void handleMessage(Session session, Message message);

    // Template Exchange
    public boolean supportsTemplateExchange();

    public Message createTemplateDefinitionMessage(MessageTemplate messageTemplate);

    public Message createTemplateDeclarationMessage(MessageTemplate messageTemplate, int templateId);

    public Message getCloseMessage();
    
    void registerSessionTemplates(TemplateRegistry registry);
    
    MessageTemplate createTemplateFromMessage(Message templateDef, TemplateRegistry registry);
}
