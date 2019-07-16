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
package org.openfast.template.loader;

import org.openfast.QName;
import org.openfast.template.Field;
import org.openfast.template.Scalar;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.Type;
import org.openfast.util.Util;
import org.w3c.dom.Element;

public class ScalarParser extends AbstractFieldParser {
    public ScalarParser(String[] nodeNames) {
        super(nodeNames);
    }

    public ScalarParser(String nodeName) {
        super(nodeName);
    }

    public ScalarParser() {
        super(new String[] {});
    }

    public boolean canParse(Element element, ParsingContext context) {
        return context.getTypeMap().containsKey(getTypeName(element));
    }
    
    public boolean canSerialize(Field field) {
        return field instanceof Scalar;
    }
    
    public Field parse(Element fieldNode, boolean optional, ParsingContext context) {
        Operator operator = Operator.NONE;
        String defaultValue = null;
        String key = null;
        String ns = "";
        Element operatorElement = getOperatorElement(fieldNode);
        if (operatorElement != null) {
            if (operatorElement.hasAttribute("value"))
                defaultValue = operatorElement.getAttribute("value");
            operator = Operator.getOperator(operatorElement.getNodeName());
            if (operatorElement.hasAttribute("key"))
                key = operatorElement.getAttribute("key");
            if (operatorElement.hasAttribute("ns"))
                ns = operatorElement.getAttribute("ns");
            if (operatorElement.hasAttribute("dictionary"))
                context.setDictionary(operatorElement.getAttribute("dictionary"));
        }
        Type type = getType(fieldNode, context);
        Scalar scalar = new Scalar(getName(fieldNode, context), type, operator, type.getValue(defaultValue), optional);
        if (fieldNode.hasAttribute("id"))
            scalar.setId(fieldNode.getAttribute("id"));
        if (key != null)
            scalar.setKey(new QName(key, ns));
        scalar.setDictionary(context.getDictionary());
        parseExternalAttributes(fieldNode, scalar);
        return scalar;
    }

    protected QName getName(Element fieldNode, ParsingContext context) {
        return context.getName();
    }

    protected Type getType(Element fieldNode, ParsingContext context) {
        String typeName = getTypeName(fieldNode);
        if (!context.getTypeMap().containsKey(typeName)) {
            context.getErrorHandler().error(
                    XMLMessageTemplateLoader.INVALID_TYPE,
                    "The type " + typeName + " is not defined.  Possible types: "
                            + Util.collectionToString(context.getTypeMap().keySet(), ", "));
        }
        return (Type) context.getTypeMap().get(typeName);
    }

    protected String getTypeName(Element fieldNode) {
        return fieldNode.getNodeName();
    }

    protected Element getOperatorElement(Element fieldNode) {
        return getElement(fieldNode, 1);
    }
}
