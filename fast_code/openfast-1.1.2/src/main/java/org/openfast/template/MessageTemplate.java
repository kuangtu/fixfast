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
import org.openfast.BitVectorReader;
import org.openfast.Context;
import org.openfast.FieldValue;
import org.openfast.IntegerValue;
import org.openfast.Message;
import org.openfast.QName;
import org.openfast.ScalarValue;
import org.openfast.error.FastConstants;
import org.openfast.error.FastException;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.Type;

public class MessageTemplate extends Group implements FieldSet {
    private static final long serialVersionUID = 1L;

    public MessageTemplate(QName name, Field[] fields) {
        super(name, addTemplateIdField(fields), false);
        updateTemplateReference(fields);
        this.fields[0].setMessageTemplate(this);
    }

    private void updateTemplateReference(Field[] fields) {
        for (int i=0; i<fields.length; i++) {
            fields[i].setMessageTemplate(this);
        }
    }

    public boolean usesPresenceMap() {
        return true;
    }

    public MessageTemplate(String name, Field[] fields) {
        this(new QName(name), fields);
    }

    /**
     * Take an existing field array and add TemplateID information to it
     * 
     * @param fields
     *            The field array that needs the TemplateID added to it
     * @return Returns a new array with the passed field information and
     *         TemplateID
     */
    private static Field[] addTemplateIdField(Field[] fields) {
        Field[] newFields = new Field[fields.length + 1];
        newFields[0] = new Scalar("templateId", Type.U32, Operator.COPY, ScalarValue.UNDEFINED, false);
        System.arraycopy(fields, 0, newFields, 1, fields.length);
        return newFields;
    }

    /**
     * @param index
     *            The index to find the field
     * @return Returns the index of the field object
     */
    public Field getField(int index) {
        return fields[index];
    }

    /**
     * @return Returns the length of the fields as an int
     */
    public int getFieldCount() {
        return fields.length;
    }

    /**
     * Uses the superclasses encode method to encode the byte array - see
     * Group.java
     * 
     * @param message
     *            The GroupValue object to be encoded
     * @param context
     *            The previous object to keep the data in sync
     * @return Returns a byte array of the encoded message
     */
    public byte[] encode(Message message, Context context) {
        if (!context.getTemplateRegistry().isRegistered(message.getTemplate()))
            throw new FastException("Cannot encode message: The template " + message.getTemplate() + " has not been registered.",
                    FastConstants.D9_TEMPLATE_NOT_REGISTERED);
        message.setInteger(0, context.getTemplateId(message.getTemplate()));
        return super.encode(message, this, context);
    }

    /**
     * Decodes the inputStream and creates a new message that contains this
     * information
     * 
     * @param in
     *            The inputStream to be decoded
     * @param templateId
     *            The templateID of the message
     * @param presenceMapReader
     *            The BitVector map of the Message
     * @param context
     *            The previous object to keep the data in sync
     * @return Returns a new message object with the newly decoded fieldValue
     */
    public Message decode(InputStream in, int templateId, BitVectorReader presenceMapReader, Context context) {
        try {
            if (context.isTraceEnabled())
                context.getDecodeTrace().groupStart(this);
            FieldValue[] fieldValues = super.decodeFieldValues(in, this, presenceMapReader, context);
            fieldValues[0] = new IntegerValue(templateId);
            Message message = new Message(this, fieldValues);
            if (context.isTraceEnabled())
                context.getDecodeTrace().groupEnd();
            return message;
        } catch (FastException e) {
            throw new FastException("An error occurred while decoding " + this, e.getCode(), e);
        }
    }

    /**
     * @return Returns the class of the message
     */
    public Class getValueType() {
        return Message.class;
    }

    public String toString() {
        return name.getName();
    }

    /**
     * @return Creates a new Message object with the specified FieldValue and
     *         the passed string value
     */
    public FieldValue createValue(String value) {
        return new Message(this);
    }

    /**
     * @return Returns the field array
     */
    public Field[] getFields() {
        return fields;
    }

    /**
     * Returns a field array of the current stored fields
     * 
     * @return Returns a field array
     */
    public Field[] getTemplateFields() {
        Field[] f = new Field[fields.length - 1];
        System.arraycopy(fields, 1, f, 0, fields.length - 1);
        return f;
    }

    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null || !(obj instanceof MessageTemplate))
            return false;
        return equals((MessageTemplate) obj);
    }

    private boolean equals(MessageTemplate other) {
        if (!name.equals(other.name))
            return false;
        if (fields.length != other.fields.length)
            return false;
        for (int i = 0; i < fields.length; i++) {
            if (!fields[i].equals(other.fields[i]))
                return false;
        }
        return true;
    }

    public int hashCode() {
        int hashCode = (name != null) ? name.hashCode() : 0;
        for (int i = 0; i < fields.length; i++)
            hashCode += fields[i].hashCode();
        return hashCode;
    }
}
