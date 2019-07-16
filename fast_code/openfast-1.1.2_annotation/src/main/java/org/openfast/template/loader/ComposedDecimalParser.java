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
import org.openfast.ScalarValue;
import org.openfast.template.ComposedScalar;
import org.openfast.template.Field;
import org.openfast.template.Scalar;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.Type;
import org.openfast.util.Util;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ComposedDecimalParser extends AbstractFieldParser {
    public ComposedDecimalParser() {
        super("decimal");
    }

    public boolean canParse(Element element, ParsingContext context) {
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            String nodeName = children.item(i).getNodeName();
            if (nodeName.equals("mantissa") || nodeName.equals("exponent"))
                return true;
        }
        return false;
    }

    protected Field parse(Element fieldNode, boolean optional, ParsingContext context) {
        NodeList fieldChildren = fieldNode.getChildNodes();
        Node mantissaNode = null;
        Node exponentNode = null;
        for (int i = 0; i < fieldChildren.getLength(); i++) {
            if ("mantissa".equals(fieldChildren.item(i).getNodeName())) {
                mantissaNode = fieldChildren.item(i);
            } else if ("exponent".equals(fieldChildren.item(i).getNodeName())) {
                exponentNode = fieldChildren.item(i);
            }
        }
        return createComposedDecimal(fieldNode, context.getName(), optional, mantissaNode, exponentNode, context);
    }

    /**
     * Create a new Scalar object with a new TwinValue and a new TwinOperator
     * with the mantissa and exponent nodes. If there are nodes or child nodes
     * within the passed Nodes, those values are stored as well
     * 
     * @param fieldNode
     *            The dom element object
     * @param name
     *            The name of the create Scalar object
     * @param optional
     *            Determines if the Field is required or not for the data
     * @param mantissaNode
     *            The passed mantissaNode
     * @param exponentNode
     *            The passed exponentNode
     * @return Returns a new Scalar object with the newly create TwinValue
     *         object and TwinOperator object.
     */
    private Field createComposedDecimal(Element fieldNode, QName name, boolean optional, Node mantissaNode, Node exponentNode,
            ParsingContext context) {
        String mantissaOperator = "none";
        String exponentOperator = "none";
        ScalarValue mantissaDefaultValue = ScalarValue.UNDEFINED;
        ScalarValue exponentDefaultValue = ScalarValue.UNDEFINED;
        QName mantissaKey = null;
        QName exponentKey = null;
        String mantissaDictionary = context.getDictionary();
        String exponentDictionary = context.getDictionary();
        String mantissaNamespace = context.getNamespace();
        String exponentNamespace = context.getNamespace();
        if ((mantissaNode != null) && mantissaNode.hasChildNodes()) {
            Element operatorElement = getElement((Element) mantissaNode, 1);
            if (operatorElement != null) {
                mantissaOperator = operatorElement.getNodeName();
                if (operatorElement.hasAttribute("value"))
                    mantissaDefaultValue = Type.I64.getValue(operatorElement.getAttribute("value"));
                if (operatorElement.hasAttribute("ns"))
                    mantissaNamespace = operatorElement.getAttribute("ns");
                if (operatorElement.hasAttribute("key"))
                    mantissaKey = new QName(operatorElement.getAttribute("key"), mantissaNamespace);
                if (operatorElement.hasAttribute("dictionary"))
                    mantissaDictionary = operatorElement.getAttribute("dictionary");
            }
        }
        if ((exponentNode != null) && exponentNode.hasChildNodes()) {
            Element operatorElement = getElement((Element) exponentNode, 1);
            if (operatorElement != null) {
                exponentOperator = operatorElement.getNodeName();
                if (operatorElement.hasAttribute("value"))
                    exponentDefaultValue = Type.I32.getValue(operatorElement.getAttribute("value"));
                if (operatorElement.hasAttribute("ns"))
                    exponentNamespace = operatorElement.getAttribute("ns");
                if (operatorElement.hasAttribute("key"))
                    exponentKey = new QName(operatorElement.getAttribute("key"), exponentNamespace);
                if (operatorElement.hasAttribute("dictionary"))
                    exponentDictionary = operatorElement.getAttribute("dictionary");
            }
        }
        ComposedScalar scalar = Util.composedDecimal(name, Operator.getOperator(exponentOperator), exponentDefaultValue, Operator
                .getOperator(mantissaOperator), mantissaDefaultValue, optional);
        Scalar exponent = scalar.getFields()[0];
        exponent.setDictionary(exponentDictionary);
        if (exponentKey != null)
            exponent.setKey(exponentKey);
        Scalar mantissa = scalar.getFields()[1];
        mantissa.setDictionary(mantissaDictionary);
        if (mantissaKey != null)
            mantissa.setKey(mantissaKey);
        if (fieldNode.hasAttribute("id"))
            scalar.setId(fieldNode.getAttribute("id"));
        return scalar;
    }
}
