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
package org.openfast.codec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openfast.Context;
import org.openfast.Message;
import org.openfast.template.MessageTemplate;
import org.openfast.template.TemplateRegisteredListener;

public class FastEncoder implements Coder {
    private Context context;
    private List listeners = Collections.EMPTY_LIST;

    public FastEncoder(Context context) {
        this.context = context;
    }
    /**
     * WARNING: Not thread-safe.
     * 
     * @param message
     * @return the fast encoding of the message
     */
    public byte[] encode(Message message) {
        MessageTemplate template = message.getTemplate();
        context.newMessage(template);
        return template.encode(message, context);
    }
    public void reset() {
        context.reset();
    }
    public void registerTemplate(int templateId, MessageTemplate template) {
        context.registerTemplate(templateId, template);
    }
    public void addTemplateRegisteredListener(TemplateRegisteredListener templateRegisteredListener) {
        if (listeners.isEmpty()) {
            listeners = new ArrayList();
        }
        listeners.add(templateRegisteredListener);
    }
}
