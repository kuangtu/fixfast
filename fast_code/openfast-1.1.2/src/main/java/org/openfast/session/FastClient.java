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

import org.openfast.template.BasicTemplateRegistry;
import org.openfast.template.TemplateRegistry;

public class FastClient {
    private final String clientName;
    private final Endpoint endpoint;
    private final SessionProtocol sessionProtocol;
    private TemplateRegistry inboundRegistry = new BasicTemplateRegistry();
    private TemplateRegistry outboundRegistry = new BasicTemplateRegistry();
    private MessageListener messageListener = MessageListener.NULL;
    private SessionListener sessionListener = SessionListener.NULL;

    public FastClient(String clientName, SessionProtocol sessionProtocol, Endpoint endpoint) {
        this.clientName = clientName;
        this.sessionProtocol = sessionProtocol;
        this.endpoint = endpoint;
    }
    
    public FastClient(String clientName, SessionProtocol sessionProtocol, Endpoint endpoint, SessionListener sessionListener) {
        this(clientName, sessionProtocol, endpoint);
        this.sessionListener = sessionListener;
    }
    
    public void setInboundTemplateRegistry(TemplateRegistry registry) {
        this.inboundRegistry = registry;
    }
    
    public TemplateRegistry getInboundTemplateRegistry() {
        return this.inboundRegistry;
    }
    
    public void setOutboundTemplateRegistry(TemplateRegistry registry) {
        this.outboundRegistry = registry;
    }
    
    public TemplateRegistry getOutboundTemplateRegistry() {
        return this.outboundRegistry;
    }
    
    public Session connect() throws FastConnectionException {
        Connection connection = endpoint.connect();
        Session session = sessionProtocol.connect(clientName, connection, inboundRegistry, outboundRegistry,
                                                  messageListener, sessionListener);
        return session;
    }

    public void setMessageHandler(MessageListener messageListener) {
        this.messageListener = messageListener;
    }
}
