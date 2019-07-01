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
import org.openfast.template.DynamicTemplateReference;
import org.openfast.template.Field;
import org.openfast.template.StaticTemplateReference;
import org.w3c.dom.Element;

public class TemplateRefParser implements FieldParser {
    public Field parse(Element element, ParsingContext context) {
        if (element.hasAttribute("name")) {
            QName templateName;
            if (element.hasAttribute("templateNs"))
                templateName = new QName(element.getAttribute("name"), element.getAttribute("templateNs"));
            else
                templateName = new QName(element.getAttribute("name"), context.getTemplateNamespace());
            if (context.getTemplateRegistry().isDefined(templateName)) {
                return new StaticTemplateReference(context.getTemplateRegistry().get(templateName));
            } else {
                throw new UnresolvedStaticTemplateReferenceException();
            }
        } else {
            return DynamicTemplateReference.INSTANCE;
        }
    }

    public boolean canParse(Element element, ParsingContext context) {
        return "templateRef".equals(element.getNodeName());
    }
}
