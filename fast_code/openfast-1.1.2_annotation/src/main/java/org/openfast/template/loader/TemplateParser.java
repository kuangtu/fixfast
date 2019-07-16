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
import org.openfast.template.MessageTemplate;
import org.w3c.dom.Element;

public class TemplateParser extends GroupParser {
    private boolean loadTemplateIdFromAuxId;

    public TemplateParser(boolean loadTemplateIdFromAuxId) {
        this.loadTemplateIdFromAuxId = loadTemplateIdFromAuxId;
    }

    /**
     * Creates a MessageTemplate object from the dom template element
     * 
     * @param context
     * @param templateElement
     *            The dom element object
     * @return Returns a newly created MessageTemplate object
     */
    protected Field parse(final Element templateElement, boolean optional, final ParsingContext context) {
        final QName templateName = getTemplateName(templateElement, context);
        try {
            final Field[] fields = parseFields(templateElement, context);
            return createMessageTemplate(templateElement, context, templateName, fields);
        } catch (UnresolvedStaticTemplateReferenceException e) {
            return null;
        }
    }

    private MessageTemplate createMessageTemplate(Element templateElement, ParsingContext context, QName templateName, Field[] fields) {
        MessageTemplate messageTemplate = new MessageTemplate(templateName, fields);
        parseMore(templateElement, messageTemplate, context);
        if (loadTemplateIdFromAuxId && templateElement.hasAttribute("id")) {
            try {
                int templateId = Integer.parseInt(templateElement.getAttribute("id"));
                context.getTemplateRegistry().register(templateId, messageTemplate);
            } catch (NumberFormatException e) {
                context.getTemplateRegistry().define(messageTemplate);
            }
        } else {
            context.getTemplateRegistry().define(messageTemplate);
        }
        return messageTemplate;
    }

    private QName getTemplateName(Element templateElement, ParsingContext context) {
        return new QName(templateElement.getAttribute("name"), context.getTemplateNamespace());
    }
}
