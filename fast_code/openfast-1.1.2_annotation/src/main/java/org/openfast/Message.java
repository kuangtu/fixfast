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

import org.openfast.template.MessageTemplate;

public class Message extends GroupValue {
    private static final long serialVersionUID = 1L;
    private final MessageTemplate template;

    public Message(MessageTemplate template, FieldValue[] fieldValues) {
        super(template, fieldValues);
        this.template = template;
    }
    public Message(MessageTemplate template) {
        this(template, initializeFieldValues(template.getFieldCount()));
    }
    private static FieldValue[] initializeFieldValues(int fieldCount) {
        FieldValue[] fields = new FieldValue[fieldCount];
        return fields;
    }
    @Override
    public boolean equals(Object obj) {
        if ((obj == null) || !(obj instanceof Message)) {
            return false;
        }
        return equals((Message) obj);
    }
    public boolean equals(Message message) {
        if (this.getFieldCount() != message.getFieldCount())
            return false;
        for (int i = 1; i < message.getFieldCount(); i++)
            if (message.getValue(i) == null) {
                if (this.getValue(i) == null) {
                    continue;
                } else {
                    return false;
                }
            } else if (!message.getValue(i).equals(this.getValue(i))) {
                return false;
            }
        return true;
    }
    @Override
    public int hashCode() {
        return super.hashCode() + template.hashCode();
    }
    @Override
    public int getFieldCount() {
        return values.length;
    }
    public MessageTemplate getTemplate() {
        return template;
    }
    @Override
    public FieldValue copy() {
        FieldValue[] copies = new FieldValue[values.length];
        for (int i = 0; i < copies.length; i++) {
            copies[i] = values[i].copy();
        }
        return new Message(template, copies);
    }
}
