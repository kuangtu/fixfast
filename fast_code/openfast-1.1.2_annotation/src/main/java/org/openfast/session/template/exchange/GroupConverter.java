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

import org.openfast.FieldValue;
import org.openfast.GroupValue;
import org.openfast.Message;
import org.openfast.QName;
import org.openfast.SequenceValue;
import org.openfast.error.FastConstants;
import org.openfast.session.SessionControlProtocol_1_1;
import org.openfast.template.Field;
import org.openfast.template.Group;
import org.openfast.template.MessageTemplate;
import org.openfast.template.TemplateRegistry;

public class GroupConverter extends AbstractFieldInstructionConverter {
    public Field convert(GroupValue fieldDef, TemplateRegistry templateRegistry, ConversionContext context) {
        String name = fieldDef.getString("Name");
        String namespace = "";
        if (fieldDef.isDefined("Ns"))
            namespace = fieldDef.getString("Ns");
        Field[] fields = parseFieldInstructions(fieldDef, templateRegistry, context);
        boolean optional = fieldDef.getBool("Optional");
        Group group = new Group(new QName(name, namespace), fields, optional);
        if (fieldDef.isDefined("TypeRef")) {
            GroupValue typeRef = fieldDef.getGroup("TypeRef");
            String typeRefName = typeRef.getString("Name");
            String typeRefNs = ""; // context.getNamespace();
            if (typeRef.isDefined("Ns"))
                typeRefNs = typeRef.getString("Ns");
            group.setTypeReference(new QName(typeRefName, typeRefNs));
        }
        if (fieldDef.isDefined("AuxId")) {
            group.setId(fieldDef.getString("AuxId"));
        }
        return group;
    }

    public GroupValue convert(Field field, ConversionContext context) {
        Group group = (Group) field;
        Message groupMsg = convert(group, new Message(SessionControlProtocol_1_1.GROUP_INSTR), context);
        groupMsg.setBool("Optional", field.isOptional());
        return groupMsg;
    }

    public boolean shouldConvert(Field field) {
        return field.getClass().equals(Group.class);
    }

    public Group[] getTemplateExchangeTemplates() {
        return new Group[] { SessionControlProtocol_1_1.GROUP_INSTR };
    }

    public static Message convert(Group group, Message groupMsg, ConversionContext context) {
        setNameAndId(group, groupMsg);
        if (group.getTypeReference() != null && !FastConstants.ANY_TYPE.equals(group.getTypeReference())) {
            GroupValue typeRef = new GroupValue((Group) SessionControlProtocol_1_1.TYPE_REF.getField(new QName("TypeRef", SessionControlProtocol_1_1.NAMESPACE)));
            setName(typeRef, group.getTypeReference());
            groupMsg.setFieldValue("TypeRef", typeRef);
        }
        SequenceValue instructions = new SequenceValue(SessionControlProtocol_1_1.TEMPLATE_DEFINITION.getSequence("Instructions"));
        int i = group instanceof MessageTemplate ? 1 : 0;
        Field[] fields = group.getFieldDefinitions();
        for (; i < fields.length; i++) {
            Field field = fields[i];
            FieldInstructionConverter converter = context.getConverter(field);
            if (converter == null)
                throw new IllegalStateException("No converter found for type " + field.getClass());
            FieldValue value = converter.convert(field, context);
            instructions.add(new FieldValue[] { value });
        }
        groupMsg.setFieldValue("Instructions", instructions);
        return groupMsg;
    }

    public static Field[] parseFieldInstructions(GroupValue groupDef, TemplateRegistry registry, ConversionContext context) {
        SequenceValue instructions = groupDef.getSequence("Instructions");
        Field[] fields = new Field[instructions.getLength()];
        for (int i = 0; i < fields.length; i++) {
            GroupValue fieldDef = instructions.get(i).getGroup(0);
            FieldInstructionConverter converter = context.getConverter(fieldDef.getGroup());
            if (converter == null)
                throw new IllegalStateException("Encountered unknown group " + fieldDef.getGroup()
                        + "while processing field instructions " + groupDef.getGroup());
            fields[i] = converter.convert(fieldDef, registry, context);
        }
        return fields;
    }
}
