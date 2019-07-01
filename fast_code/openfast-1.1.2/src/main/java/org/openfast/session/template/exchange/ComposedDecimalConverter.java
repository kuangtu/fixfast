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

import org.openfast.GroupValue;
import org.openfast.IntegerValue;
import org.openfast.Message;
import org.openfast.QName;
import org.openfast.ScalarValue;
import org.openfast.session.SessionControlProtocol_1_1;
import org.openfast.template.ComposedScalar;
import org.openfast.template.Field;
import org.openfast.template.Group;
import org.openfast.template.LongValue;
import org.openfast.template.Scalar;
import org.openfast.template.TemplateRegistry;
import org.openfast.template.operator.Operator;
import org.openfast.util.Util;

public class ComposedDecimalConverter extends AbstractFieldInstructionConverter {
    public Field convert(GroupValue fieldDef, TemplateRegistry templateRegistry, ConversionContext context) {
        QName name = new QName(fieldDef.getString("Name"), fieldDef.getString("Ns"));
        boolean optional = fieldDef.getBool("Optional");
        Operator exponentOperator = Operator.NONE;
        ScalarValue exponentDefaultValue = ScalarValue.UNDEFINED;
        Operator mantissaOperator = Operator.NONE;
        ScalarValue mantissaDefaultValue = ScalarValue.UNDEFINED;
        if (fieldDef.isDefined("Exponent")) {
            GroupValue exponentDef = fieldDef.getGroup("Exponent");
            GroupValue exponentOperatorDef = exponentDef.getGroup("Operator").getGroup(0);
            exponentOperator = getOperator(exponentOperatorDef.getGroup());
            if (exponentDef.isDefined("InitialValue"))
                exponentDefaultValue = new IntegerValue(exponentDef.getInt("InitialValue"));
        }
        if (fieldDef.isDefined("Mantissa")) {
            GroupValue mantissaDef = fieldDef.getGroup("Mantissa");
            GroupValue mantissaOperatorDef = mantissaDef.getGroup("Operator").getGroup(0);
            mantissaOperator = getOperator(mantissaOperatorDef.getGroup());
            if (mantissaDef.isDefined("InitialValue"))
                mantissaDefaultValue = new LongValue(mantissaDef.getInt("InitialValue"));
        }
        ComposedScalar composedDecimal = Util.composedDecimal(name, exponentOperator, exponentDefaultValue, mantissaOperator, mantissaDefaultValue, optional);
        if (fieldDef.isDefined("AuxId")) {
            composedDecimal.setId(fieldDef.getString("AuxId"));
        }
        return composedDecimal;
    }

    public GroupValue convert(Field field, ConversionContext context) {
        ComposedScalar composedScalar = (ComposedScalar) field;
        Message message = new Message(SessionControlProtocol_1_1.COMP_DECIMAL_INSTR);
        setNameAndId(field, message);
        message.setInteger("Optional", field.isOptional() ? 1 : 0);
        GroupValue exponentDef = createComponent(composedScalar.getFields()[0], "Exponent");
        GroupValue mantissaDef = createComponent(composedScalar.getFields()[1], "Mantissa");
        if (exponentDef != null)
            message.setFieldValue("Exponent", exponentDef);
        if (mantissaDef != null)
            message.setFieldValue("Mantissa", mantissaDef);
        return message;
    }

    private GroupValue createComponent(Scalar component, String componentName) {
        Group componentGroup = SessionControlProtocol_1_1.COMP_DECIMAL_INSTR.getGroup(componentName);
        GroupValue componentDef = new GroupValue(componentGroup);
        GroupValue componentOperatorDef = createOperator(component);
        if (componentOperatorDef == null)
            return null;
        GroupValue componentOperatorGroup = new GroupValue(componentGroup.getGroup("Operator"));
        componentDef.setFieldValue("Operator", componentOperatorGroup);
        componentOperatorGroup.setFieldValue(0, componentOperatorDef);
        if (!component.getDefaultValue().isUndefined())
            componentDef.setInteger("InitialValue", component.getDefaultValue().toInt());
        return componentDef;
    }

    public boolean shouldConvert(Field field) {
        return field.getClass().equals(ComposedScalar.class);
    }

    public Group[] getTemplateExchangeTemplates() {
        return new Group[] { SessionControlProtocol_1_1.COMP_DECIMAL_INSTR };
    }
}
