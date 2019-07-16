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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openfast.BitVector;
import org.openfast.BitVectorBuilder;
import org.openfast.BitVectorReader;
import org.openfast.BitVectorValue;
import org.openfast.Context;
import org.openfast.FieldValue;
import org.openfast.Global;
import org.openfast.GroupValue;
import org.openfast.Node;
import org.openfast.QName;
import org.openfast.error.FastConstants;
import org.openfast.error.FastException;
import org.openfast.template.type.codec.TypeCodec;

public class Group extends Field {
    private static final long serialVersionUID = 1L;
    private QName typeReference = null;
    protected String childNamespace = "";
    protected final Field[] fields;
    protected final Map fieldIndexMap;
    protected final Map fieldIdMap;
    protected final Map fieldNameMap;
    protected final boolean usesPresenceMap;
    protected final StaticTemplateReference[] staticTemplateReferences;
    protected final Field[] fieldDefinitions;
    protected final Map introspectiveFieldMap;

    public Group(String name, Field[] fields, boolean optional) {
        this(new QName(name), fields, optional);
    }

    public Group(QName name, Field[] fields, boolean optional) {
        super(name, optional);
        List expandedFields = new ArrayList();
        List staticTemplateReferences = new ArrayList();
        for (int i = 0; i < fields.length; i++) {
            if (fields[i] instanceof StaticTemplateReference) {
                Field[] referenceFields = ((StaticTemplateReference) fields[i]).getTemplate().getFields();
                for (int j = 1; j < referenceFields.length; j++)
                    expandedFields.add(referenceFields[j]);
                staticTemplateReferences.add(fields[i]);
            } else {
                expandedFields.add(fields[i]);
            }
        }
        this.fields = (Field[]) expandedFields.toArray(new Field[expandedFields.size()]);
        this.fieldDefinitions = fields;
        this.fieldIndexMap = constructFieldIndexMap(this.fields);
        this.fieldNameMap = constructFieldNameMap(this.fields);
        this.fieldIdMap = constructFieldIdMap(this.fields);
        this.introspectiveFieldMap = constructInstrospectiveFields(this.fields);
        this.usesPresenceMap = determinePresenceMapUsage(this.fields);
        this.staticTemplateReferences = (StaticTemplateReference[]) staticTemplateReferences
                .toArray(new StaticTemplateReference[staticTemplateReferences.size()]);
    }

    // BAD ABSTRACTION
    private static Map constructInstrospectiveFields(Field[] fields) {
        Map map = new HashMap();
        for (int i = 0; i < fields.length; i++) {
            if (fields[i] instanceof Scalar) {
                if (fields[i].hasChild(FastConstants.LENGTH_FIELD)) {
                    Node lengthNode = (Node) fields[i].getChildren(FastConstants.LENGTH_FIELD).get(0);
                    map.put(lengthNode.getAttribute(FastConstants.LENGTH_NAME_ATTR), fields[i]);
                }
            }
        }
        if (map.size() == 0)
            return Collections.EMPTY_MAP;
        return map;
    }

    /**
     * Check to see if the passed field array has a Field that has a MapBit
     * present
     * 
     * @param fields
     *            The Field object array to be checked
     * @return Returns true if a Field object has a MapBit present, false
     *         otherwise
     */
    private static boolean determinePresenceMapUsage(Field[] fields) {
        for (int i = 0; i < fields.length; i++)
            if (fields[i].usesPresenceMapBit())
                return true;
        return false;
    }

    /**
     * If your FieldValue already has a BitVector, use this encode method. The
     * MapBuilder index is kept track of and stored through this process. The
     * supplied data is stored to a byte buffer array and returned.
     * 
     * @param value
     *            The value of the FieldValue to be encoded
     * @param template
     *            The Group object to be encoded
     * @param context
     *            The previous object to keep the data in sync
     * @param presenceMapBuilder
     *            The BitVector object that will be used to encode.
     * @return Returns the encoded byte array
     */
    public byte[] encode(FieldValue value, Group template, Context context, BitVectorBuilder presenceMapBuilder) {
        byte[] encoding = encode(value, template, context);
        if (optional) {
            if (encoding.length != 0)
                presenceMapBuilder.set();
            else
                presenceMapBuilder.skip();
        }
        return encoding;
    }

    /**
     * If there is no BitVector, this encoding method will create one. The
     * supplied data is stored to a byte buffer array and returned. The
     * MapBuilder index is kept track of and stored through this process.
     * 
     * @param value
     *            The value of the FieldValue to be encoded
     * @param template
     *            The Group object to be encoded
     * @param context
     *            The previous object to keep the data in sync
     * @return Returns an new byte array if there are no FieldValue to encode,
     *         otherwise returns the buffer to the byte array that the data was
     *         stored to
     */
    public byte[] encode(FieldValue value, Group template, Context context) {
        if (value == null) {
            return new byte[] {};
        }
        GroupValue groupValue = (GroupValue) value;
        if (context.isTraceEnabled()) {
            context.getEncodeTrace().groupStart(this);
        }
        BitVectorBuilder presenceMapBuilder = new BitVectorBuilder(groupValue.getGroup().getMaxPresenceMapSize());
        try {
            byte[][] fieldEncodings = new byte[fields.length][];
            for (int fieldIndex = 0; fieldIndex < fields.length; fieldIndex++) {
                FieldValue fieldValue = groupValue.getValue(fieldIndex);
                Field field = getField(fieldIndex);
                if (!field.isOptional() && fieldValue == null)
                    Global.handleError(FastConstants.GENERAL_ERROR, "Mandatory field " + field + " is null");
                Group fieldTmpl = template;
                if (field.getTemplate() != null)
                    fieldTmpl = field.getTemplate();
                byte[] encoding = field.encode(fieldValue, fieldTmpl, context, presenceMapBuilder);
                fieldEncodings[fieldIndex] = encoding;
            }
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            if (usesPresenceMap()) {
                byte[] pmap = presenceMapBuilder.getBitVector().getTruncatedBytes();
                if (context.isTraceEnabled())
                    context.getEncodeTrace().pmap(pmap);
                buffer.write(pmap);
            }
            for (int i = 0; i < fieldEncodings.length; i++) {
                if (fieldEncodings[i] != null) {
                    buffer.write(fieldEncodings[i]);
                }
            }
            if (context.isTraceEnabled())
                context.getEncodeTrace().groupEnd();
            return buffer.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private int getMaxPresenceMapSize() {
        return fields.length * 2;
    }

    /**
     * 
     * @param in
     *            The InputStream to be decoded
     * @param group
     *            The Group object to be decoded
     * @param context
     *            The previous object to keep the data in sync
     * @param present
     * @return Returns a new GroupValue
     */
    public FieldValue decode(InputStream in, Group group, Context context, BitVectorReader pmapReader) {
        try {
            if (!usesPresenceMapBit() || pmapReader.read()) {
                if (context.isTraceEnabled()) {
                    context.getDecodeTrace().groupStart(this);
                }
                GroupValue groupValue = new GroupValue(this, decodeFieldValues(in, group, context));
                if (context.isTraceEnabled())
                    context.getDecodeTrace().groupEnd();
                return groupValue;
            } else
                return null;
        } catch (FastException e) {
            throw new FastException("Error occurred while decoding " + this, e.getCode(), e);
        }
    }

    /**
     * If there is not a vector map created for the inputStream, a vector map
     * will be created to pass to the public decodeFieldValues method.
     * 
     * @param in
     *            The InputStream to be decoded
     * @param template
     *            The Group object to be decoded
     * @param context
     *            The previous object to keep the data in sync
     * @return Returns the FieldValue array of the decoded field values passed
     *         to it
     * 
     */
    protected FieldValue[] decodeFieldValues(InputStream in, Group template, Context context) {
        if (usesPresenceMap()) {
            BitVector pmap = ((BitVectorValue) TypeCodec.BIT_VECTOR.decode(in)).value;
            if (context.isTraceEnabled())
                context.getDecodeTrace().pmap(pmap.getBytes());
            if (pmap.isOverlong())
                Global.handleError(FastConstants.R7_PMAP_OVERLONG, "The presence map " + pmap + " for the group " + this
                        + " is overlong.");
            return decodeFieldValues(in, template, new BitVectorReader(pmap), context);
        } else {
            return decodeFieldValues(in, template, BitVectorReader.NULL, context);
        }
    }

    /**
     * Goes through the all the field value array, starting with the index
     * passed, checks to see if a map actually created for the field to pass to
     * the decoder - the field index is created as a new Group object and stored
     * to the the FieldValue array. Once all the field values have beed gone
     * through, the method returns.
     * 
     * @param in
     *            The InputStream to be decoded
     * @param template
     *            The Group object
     * @param pmap
     *            The BitVector to be decoded
     * @param context
     *            The previous object to keep the data in sync
     * @param start
     *            The index of the Field to start decoding from
     * @return Returns a FieldValue array of the decoded field values passed to
     *         it.
     * @throws Throws
     *             RuntimeException if there is an problem in the decoding
     * 
     */
    public FieldValue[] decodeFieldValues(InputStream in, Group template, BitVectorReader pmapReader, Context context) {
        FieldValue[] values = new FieldValue[fields.length];
        int start = this instanceof MessageTemplate ? 1 : 0;
        for (int fieldIndex = start; fieldIndex < fields.length; fieldIndex++) {
            Field field = getField(fieldIndex);
            Group fieldTmpl = template;
            if (field.getTemplate() != null)
                fieldTmpl = field.getTemplate();
            values[fieldIndex] = field.decode(in, fieldTmpl, context, pmapReader);
        }
        if (pmapReader.hasMoreBitsSet())
            Global.handleError(FastConstants.R8_PMAP_TOO_MANY_BITS, "The presence map " + pmapReader + " has too many bits for the group " + this);
        return values;
    }

    /**
     * Determine if there is a Map of the passed byte array and fieldValue
     * 
     * @param encoding
     *            The byte array to be checked
     * @param fieldValue
     *            The fieldValue to be checked
     * @return Returns true if there is a PrecenceMapBit of the specified byte
     *         array and field, false otherwise
     */
    public boolean isPresenceMapBitSet(byte[] encoding, FieldValue fieldValue) {
        return encoding.length != 0;
    }

    /**
     * @return Returns the optional boolean of the MapBit
     */
    public boolean usesPresenceMapBit() {
        return optional;
    }

    public boolean usesPresenceMap() {
        return usesPresenceMap;
    }

    /**
     * Find the number of total fields
     * 
     * @return Returns the number of fields
     */
    public int getFieldCount() {
        return fields.length;
    }

    /**
     * Find the field object of the index passed
     * 
     * @param index
     *            The index within the field that is being searched for
     * @return Returns a field object of the specified index
     */
    public Field getField(int index) {
        return fields[index];
    }

    /**
     * @return Returns the class of the GroupValue
     */
    public Class getValueType() {
        return GroupValue.class;
    }

    /**
     * @param value
     *            The value that the fieldValue that is to be created
     * @return Returns a new GroupValue
     */
    public FieldValue createValue(String value) {
        return new GroupValue(this, new FieldValue[fields.length]);
    }

    /**
     * @return Returns the string 'group'
     */
    public String getTypeName() {
        return "group";
    }

    /**
     * Find the field object of the passed field name
     * 
     * @param fieldName
     *            The field name of the field object that is to be returned
     * @return Returns the field object of the passed field name
     */
    public Field getField(String fieldName) {
        return (Field) fieldNameMap.get(new QName(fieldName, childNamespace));
    }

    public Field getField(QName name) {
        return (Field) fieldNameMap.get(name);
    }

    /**
     * Creates a map of the passed field array by the field name and the field
     * index number
     * 
     * @param fields
     *            The name of the field array that is going to be placed into a
     *            new map object
     * @return Returns a map object of the field array passed to it
     */
    private static Map constructFieldNameMap(Field[] fields) {
        Map map = new HashMap();
        for (int i = 0; i < fields.length; i++)
            map.put(fields[i].getQName(), fields[i]);
        return map;
    }

    private static Map constructFieldIdMap(Field[] fields) {
        Map map = new HashMap();
        for (int i = 0; i < fields.length; i++)
            map.put(fields[i].getId(), fields[i]);
        return map;
    }

    /**
     * Creates a map of the passed field array by the field index number,
     * numbered 0 to n
     * 
     * @param fields
     *            The name of the field array that is going to be placed into a
     *            new map object
     * @return Returns a map object of the field array passed to it
     */
    private static Map constructFieldIndexMap(Field[] fields) {
        Map map = new HashMap();
        for (int i = 0; i < fields.length; i++)
            map.put(fields[i], new Integer(i));
        return map;
    }

    /**
     * Find the index of the passed field name as an integer
     * 
     * @param fieldName
     *            The field name that is being searched for
     * @return Returns an integer of the field index of the specified field name
     */
    public int getFieldIndex(String fieldName) {
        return ((Integer) fieldIndexMap.get(getField(fieldName))).intValue();
    }

    public int getFieldIndex(Field field) {
        return ((Integer) fieldIndexMap.get(field)).intValue();
    }

    /**
     * Get the Sequence of the passed fieldName
     * 
     * @param fieldName
     *            The field name that is being searched for
     * @return Returns a sequence object of the specified fieldName
     */
    public Sequence getSequence(String fieldName) {
        return (Sequence) getField(fieldName);
    }

    /**
     * Get the Scalar Value of the passed fieldName
     * 
     * @param fieldName
     *            The field name that is being searched for
     * @return Returns a Scalar value of the specified fieldName
     */
    public Scalar getScalar(String fieldName) {
        return (Scalar) getField(fieldName);
    }

    public Scalar getScalar(int index) {
        return (Scalar) getField(index);
    }

    /**
     * Find the group with the passed fieldName
     * 
     * @param fieldName
     *            The field name that is being searched for
     * @return Returns a Group object of the specified field name
     */
    public Group getGroup(String fieldName) {
        return (Group) getField(fieldName);
    }

    /**
     * Determine if the map has a specified field name.
     * 
     * @param fieldName
     *            The name of the fieldName that is being searched for
     * @return Returns true if there is the field name that was passed in the
     *         Map, false otherwise
     */
    public boolean hasField(String fieldName) {
        return fieldNameMap.containsKey(new QName(fieldName, childNamespace));
    }

    public boolean hasField(QName fieldName) {
        return fieldNameMap.containsKey(fieldName);
    }

    /**
     * 
     * @return Returns an array of Fields
     */
    public Field[] getFields() {
        return fields;
    }

    /**
     * Set the name of the type referenced by this group
     * 
     * @param typeReference
     *            The name of the application type referenced by this goup
     */
    public void setTypeReference(QName typeReference) {
        this.typeReference = typeReference;
    }

    /**
     * 
     * @return Returns the application type referenced by this group
     */
    public QName getTypeReference() {
        return typeReference;
    }

    /**
     * @return Returns true if the type has a reference, false otherwise
     */
    public boolean hasTypeReference() {
        return typeReference != null;
    }

    public String toString() {
        return name.getName();
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Group.hashCode(fields);
        result = prime * result + name.hashCode();
        result = prime * result + ((typeReference == null) ? 0 : typeReference.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        final Group other = (Group) obj;
        if (other.fields.length != fields.length)
            return false;
        if (!other.name.equals(name))
            return false;
        for (int i = 0; i < fields.length; i++)
            if (!fields[i].equals(other.fields[i]))
                return false;
        return true;
    }

    private static int hashCode(Object[] array) {
        final int prime = 31;
        if (array == null)
            return 0;
        int result = 1;
        for (int index = 0; index < array.length; index++) {
            result = prime * result + (array[index] == null ? 0 : array[index].hashCode());
        }
        return result;
    }

    public boolean hasFieldWithId(String id) {
        return fieldIdMap.containsKey(id);
    }

    public Field getFieldById(String id) {
        return (Field) fieldIdMap.get(id);
    }

    public String getChildNamespace() {
        return childNamespace;
    }

    public void setChildNamespace(String childNamespace) {
        this.childNamespace = childNamespace;
    }

    public StaticTemplateReference[] getStaticTemplateReferences() {
        return staticTemplateReferences;
    }

    public StaticTemplateReference getStaticTemplateReference(String name) {
        return getStaticTemplateReference(new QName(name, this.name.getNamespace()));
    }

    public StaticTemplateReference getStaticTemplateReference(QName name) {
        for (int i = 0; i < staticTemplateReferences.length; i++) {
            if (staticTemplateReferences[i].getQName().equals(name))
                return staticTemplateReferences[i];
        }
        return null;
    }

    public Field[] getFieldDefinitions() {
        return fieldDefinitions;
    }

    public boolean hasIntrospectiveField(String fieldName) {
        return introspectiveFieldMap.containsKey(fieldName);
    }

    public Scalar getIntrospectiveField(String fieldName) {
        return (Scalar) introspectiveFieldMap.get(fieldName);
    }
}
