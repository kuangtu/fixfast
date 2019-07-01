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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openfast.template.Field;
import org.openfast.template.Group;

public class ConversionContext {
    private Map converterTemplateMap = new HashMap();
    private List converters = new ArrayList();

    public void addFieldInstructionConverter(FieldInstructionConverter converter) {
        Group[] templateExchangeTemplates = converter.getTemplateExchangeTemplates();
        for (int i = 0; i < templateExchangeTemplates.length; i++) {
            converterTemplateMap.put(templateExchangeTemplates[i], converter);
        }
        converters.add(converter);
    }

    public FieldInstructionConverter getConverter(Group group) {
        return (FieldInstructionConverter) converterTemplateMap.get(group);
    }

    public FieldInstructionConverter getConverter(Field field) {
        for (int i = converters.size() - 1; i >= 0; i--) {
            FieldInstructionConverter converter = (FieldInstructionConverter) converters.get(i);
            if (converter.shouldConvert(field))
                return converter;
        }
        throw new IllegalStateException("No valid converter found for the field: " + field);
    }
}
