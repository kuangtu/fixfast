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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.openfast.QName;
import org.openfast.util.IntegerMap;
import org.openfast.util.SimpleIntegerMap;

public class BasicTemplateRegistry extends AbstractTemplateRegistry {
    private final Map nameMap = new HashMap();
    private final IntegerMap idMap = new SimpleIntegerMap();
    private final Map templateMap = new HashMap();
    private final List templates = new ArrayList();

    public void register(int id, MessageTemplate template) {
        define(template);
        Integer tid = new Integer(id);
        idMap.put(id, template);
        templateMap.put(template, tid);
        notifyTemplateRegistered(template, id);
    }
    public void register(int id, QName name) {
        if (!nameMap.containsKey(name))
            throw new IllegalArgumentException("The template named " + name + " is not defined.");
        Integer tid = new Integer(id);
        MessageTemplate template = (MessageTemplate) nameMap.get(name);
        templateMap.put(template, tid);
        idMap.put(id, template);
        notifyTemplateRegistered(template, id);
    }
    public void define(MessageTemplate template) {
        if (!templates.contains(template)) {
            nameMap.put(template.getQName(), template);
            templates.add(template);
        }
    }
    public int getId(QName name) {
        Object template = nameMap.get(name);
        if (template == null || !templateMap.containsKey(template))
            return -1;
        return ((Integer) templateMap.get(template)).intValue();
    }
    public MessageTemplate get(int templateId) {
        return (MessageTemplate) idMap.get(templateId);
    }
    public MessageTemplate get(QName name) {
        return (MessageTemplate) nameMap.get(name);
    }
    public int getId(MessageTemplate template) {
        if (!isRegistered(template))
            return -1;
        return ((Integer) templateMap.get(template)).intValue();
    }
    public boolean isRegistered(QName name) {
        return nameMap.containsKey(name);
    }
    public boolean isRegistered(int templateId) {
        return idMap.containsKey(templateId);
    }
    public boolean isRegistered(MessageTemplate template) {
        return templateMap.containsKey(template);
    }
    public boolean isDefined(QName name) {
        return nameMap.containsKey(name);
    }
    public MessageTemplate[] getTemplates() {
        return (MessageTemplate[]) templates.toArray(new MessageTemplate[templates.size()]);
    }
    public void remove(QName name) {
        MessageTemplate template = (MessageTemplate) nameMap.remove(name);
        Object id = templateMap.remove(template);
        idMap.remove(((Integer) id).intValue());
        templates.remove(template);
    }
    public void remove(MessageTemplate template) {
        Object id = templateMap.remove(template);
        nameMap.remove(template.getName());
        idMap.remove(((Integer)id).intValue());
    }
    public void remove(int id) {
        MessageTemplate template = (MessageTemplate) idMap.remove(id);
        templateMap.remove(template);
        nameMap.remove(template.getName());
    }
    public void registerAll(TemplateRegistry registry) {
        if (registry == null) return;
        MessageTemplate[] templates = registry.getTemplates();
        if (templates == null) return;
        for (int i = 0; i < templates.length; i++) {
            register(registry.getId(templates[i]), templates[i]);
        }
    }
    public Iterator nameIterator() {
        return nameMap.keySet().iterator();
    }
    public Iterator iterator() {
        return templates.iterator();
    }
}
