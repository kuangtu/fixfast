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
import org.openfast.util.Util;
import org.w3c.dom.Element;

public class VariableLengthScalarParser extends ScalarParser {
    public VariableLengthScalarParser(String nodeName) {
        super(nodeName);
    }

    public Field parse(Element fieldNode, boolean optional, ParsingContext context) {
        Scalar scalar = (Scalar) super.parse(fieldNode, optional, context);
        Element element = getElement(fieldNode, 1);
        if (element != null && element.getNodeName().equals("length")) {
            String lengthName = element.getAttribute("name");
            String lengthNamespace = context.getNamespace();
            String lengthId = null;
            if (element.hasAttribute("ns"))
                lengthNamespace = element.getAttribute("ns");
            if (element.hasAttribute("id"))
                lengthId = element.getAttribute("id");
            scalar.addNode(Util.createLength(new QName(lengthName, lengthNamespace), lengthId));
        }
        return scalar;
    }

    protected Element getOperatorElement(Element fieldNode) {
        Element operatorElement = super.getOperatorElement(fieldNode);
        if (operatorElement != null && operatorElement.getNodeName().equals("length"))
            return getElement(fieldNode, 2);
        return operatorElement;
    }
}
