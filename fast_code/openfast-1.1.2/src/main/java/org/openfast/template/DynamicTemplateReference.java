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
import org.openfast.Message;
import org.openfast.QName;
import org.openfast.codec.FastDecoder;

public class DynamicTemplateReference extends Field {
    private static final long serialVersionUID = 1L;
    public static final DynamicTemplateReference INSTANCE = new DynamicTemplateReference();

    public DynamicTemplateReference() {
        super(QName.NULL, false);
    }

    /**
     * @return Returns null
     */
    public FieldValue createValue(String value) {
        return null;
    }

    /**
     * @param in
     * @param template
     * @param context
     * @param present
     * @return the next message in the decoder
     */
    public FieldValue decode(InputStream in, Group template, Context context, BitVectorReader pmapReader) {
        return new FastDecoder(context, in).readMessage();
    }

    /**
     * @param value
     * @param template
     * @param context
     * @param presenceMapBuilder
     * @return the encoding of the message given its template
     */
    public byte[] encode(FieldValue value, Group template, Context context, BitVectorBuilder presenceMapBuilder) {
        Message message = (Message) value;
        return message.getTemplate().encode(message, context);
    }

    /**
     * @return Returns null
     */
    public String getTypeName() {
        return null;
    }

    /**
     * @return Returns null
     */
    public Class getValueType() {
        return null;
    }

    /**
     * @return Returns false
     */
    public boolean isPresenceMapBitSet(byte[] encoding, FieldValue fieldValue) {
        return false;
    }

    /**
     * @return Returns false
     */
    public boolean usesPresenceMapBit() {
        return false;
    }

    public boolean equals(Object obj) {
        return obj != null && obj.getClass().equals(this.getClass());
    }
    
    public String toString() {
        return "DynamicTemplateRef";
    }
}
