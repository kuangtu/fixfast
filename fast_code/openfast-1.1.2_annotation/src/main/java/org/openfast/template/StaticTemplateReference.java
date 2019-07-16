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
package org.openfast.template;

import java.io.InputStream;
import org.openfast.BitVectorBuilder;
import org.openfast.BitVectorReader;
import org.openfast.Context;
import org.openfast.FieldValue;

public class StaticTemplateReference extends Field {
    private static final long serialVersionUID = 1L;
    private MessageTemplate template;

    protected void setTemplate(MessageTemplate template) {
        this.template = template;
    }

    public StaticTemplateReference(MessageTemplate template) {
        super(template.getQName(), false);
        this.template = template;
    }
    
    public StaticTemplateReference() {
        super(null, false);
        this.template = null;
    }

    public FieldValue createValue(String value) {
        return null;
    }

    public FieldValue decode(InputStream in, Group template, Context context, BitVectorReader pmapReader) {
        return null;
    }

    public byte[] encode(FieldValue value, Group template, Context context, BitVectorBuilder presenceMapBuilder) {
        return null;
    }

    public String getTypeName() {
        return null;
    }

    public Class getValueType() {
        return null;
    }

    public boolean isPresenceMapBitSet(byte[] encoding, FieldValue fieldValue) {
        return false;
    }

    public boolean usesPresenceMapBit() {
        return false;
    }

    public MessageTemplate getTemplate() {
        return template;
    }

    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        StaticTemplateReference other = (StaticTemplateReference) obj;
        return template.equals(other.template);
    }
}
