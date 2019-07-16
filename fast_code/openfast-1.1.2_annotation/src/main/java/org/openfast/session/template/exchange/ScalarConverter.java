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
package org.openfast.session.template.exchange;

import java.util.HashMap;
import java.util.Map;
import org.openfast.FieldValue;
import org.openfast.GroupValue;
import org.openfast.Message;
import org.openfast.QName;
import org.openfast.ScalarValue;
import org.openfast.session.SessionControlProtocol_1_1;
import org.openfast.template.Field;
import org.openfast.template.Group;
import org.openfast.template.MessageTemplate;
import org.openfast.template.Scalar;
import org.openfast.template.TemplateRegistry;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.Type;

public class ScalarConverter extends AbstractFieldInstructionConverter {
    private final Map/* <Type, MessageTemplate> */TYPE_TEMPLATE_MAP = new HashMap();
    private final Map/* <Type, MessageTemplate> */TEMPLATE_TYPE_MAP = new HashMap();

    public ScalarConverter() {
        TYPE_TEMPLATE_MAP.put(Type.I32, SessionControlProtocol_1_1.INT32_INSTR);
        TYPE_TEMPLATE_MAP.put(Type.U32, SessionControlProtocol_1_1.UINT32_INSTR);
        TYPE_TEMPLATE_MAP.put(Type.I64, SessionControlProtocol_1_1.INT64_INSTR);
        TYPE_TEMPLATE_MAP.put(Type.U64, SessionControlProtocol_1_1.UINT64_INSTR);
        TYPE_TEMPLATE_MAP.put(Type.DECIMAL, SessionControlProtocol_1_1.DECIMAL_INSTR);
        TYPE_TEMPLATE_MAP.put(Type.UNICODE, SessionControlProtocol_1_1.UNICODE_INSTR);
        TYPE_TEMPLATE_MAP.put(Type.ASCII, SessionControlProtocol_1_1.ASCII_INSTR);
        TYPE_TEMPLATE_MAP.put(Type.STRING, SessionControlProtocol_1_1.ASCII_INSTR);
        TYPE_TEMPLATE_MAP.put(Type.BYTE_VECTOR, SessionControlProtocol_1_1.BYTE_VECTOR_INSTR);
        TEMPLATE_TYPE_MAP.put(SessionControlProtocol_1_1.INT32_INSTR, Type.I32);
        TEMPLATE_TYPE_MAP.put(SessionControlProtocol_1_1.UINT32_INSTR, Type.U32);
        TEMPLATE_TYPE_MAP.put(SessionControlProtocol_1_1.INT64_INSTR, Type.I64);
        TEMPLATE_TYPE_MAP.put(SessionControlProtocol_1_1.UINT64_INSTR, Type.U64);
        TEMPLATE_TYPE_MAP.put(SessionControlProtocol_1_1.DECIMAL_INSTR, Type.DECIMAL);
        TEMPLATE_TYPE_MAP.put(SessionControlProtocol_1_1.UNICODE_INSTR, Type.UNICODE);
        TEMPLATE_TYPE_MAP.put(SessionControlProtocol_1_1.ASCII_INSTR, Type.ASCII);
        TEMPLATE_TYPE_MAP.put(SessionControlProtocol_1_1.BYTE_VECTOR_INSTR, Type.BYTE_VECTOR);
    }

    public Field convert(GroupValue fieldDef, TemplateRegistry templateRegistry, ConversionContext context) {
        Type type = (Type) TEMPLATE_TYPE_MAP.get(fieldDef.getGroup());
        boolean optional = fieldDef.getBool("Optional");
        ScalarValue initialValue = ScalarValue.UNDEFINED;
        if (fieldDef.isDefined("InitialValue"))
            initialValue = (ScalarValue) fieldDef.getValue("InitialValue");
        Scalar scalar = null;
        String name = fieldDef.getString("Name");
        String namespace = "";
        if (fieldDef.isDefined("Ns"))
            namespace = fieldDef.getString("Ns");
        QName qname = new QName(name, namespace);
        if (fieldDef.isDefined("Operator")) {
            GroupValue operatorGroup = fieldDef.getGroup("Operator").getGroup(0);
            Operator operator = getOperator(operatorGroup.getGroup());
            scalar = new Scalar(qname, type, operator, initialValue, optional);
            if (operatorGroup.isDefined("Dictionary"))
                scalar.setDictionary(operatorGroup.getString("Dictionary"));
            if (operatorGroup.isDefined("Key")) {
                String keyName = operatorGroup.getGroup("Key").getString("Name");
                String ns = operatorGroup.getGroup("Key").getString("Ns");
                scalar.setKey(new QName(keyName, ns));
            }
        } else {
            scalar = new Scalar(qname, type, Operator.NONE, initialValue, optional);
        }
        if (fieldDef.isDefined("AuxId")) {
            scalar.setId(fieldDef.getString("AuxId"));
        }
        return scalar;
    }

    public GroupValue convert(Field field, ConversionContext context) {
        Scalar scalar = (Scalar) field;
        MessageTemplate scalarTemplate = (MessageTemplate) TYPE_TEMPLATE_MAP.get(scalar.getType());
        Message scalarMsg = new Message(scalarTemplate);
        setNameAndId(scalar, scalarMsg);
        scalarMsg.setInteger("Optional", scalar.isOptional() ? 1 : 0);
        if (!scalar.getOperator().equals(Operator.NONE))
            scalarMsg.setFieldValue("Operator", new GroupValue(scalarTemplate.getGroup("Operator"),
                    new FieldValue[] { createOperator(scalar) }));
        if (!scalar.getDefaultValue().isUndefined())
            scalarMsg.setFieldValue("InitialValue", scalar.getDefaultValue());
        return scalarMsg;
    }

    public Group[] getTemplateExchangeTemplates() {
        return (Group[]) TEMPLATE_TYPE_MAP.keySet().toArray(new Group[TEMPLATE_TYPE_MAP.size()]);
    }

    public boolean shouldConvert(Field field) {
        return field.getClass().equals(Scalar.class);
    }
}
