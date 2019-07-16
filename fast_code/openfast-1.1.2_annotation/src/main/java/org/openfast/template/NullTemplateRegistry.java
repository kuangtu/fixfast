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

import java.util.Collections;
import java.util.Iterator;
import org.openfast.QName;

final class NullTemplateRegistry implements TemplateRegistry {
    public void addTemplateRegisteredListener(TemplateRegisteredListener templateRegisteredListener) {}

    public MessageTemplate get(int templateId) {
        return null;
    }

    public MessageTemplate get(String templateName) {
        return null;
    }

    public int getTemplateId(String templateName) {
        return 0;
    }

    public int getTemplateId(MessageTemplate template) {
        return 0;
    }

    public MessageTemplate[] getTemplates() {
        return new MessageTemplate[] {};
    }

    public boolean isRegistered(String templateName) {
        return false;
    }

    public boolean isRegistered(int templateId) {
        return false;
    }

    public boolean isRegistered(MessageTemplate template) {
        return false;
    }

    public void register(int templateId, MessageTemplate template) {}

    public void remove(String name) {}

    public void remove(MessageTemplate template) {}

    public void remove(int id) {}

    public void add(MessageTemplate template) {}

    public void define(MessageTemplate template) {}

    public MessageTemplate getTemplate(String name) {
        return null;
    }

    public MessageTemplate getTemplate(QName name) {
        return null;
    }

    public MessageTemplate getTemplate(int id) {
        return null;
    }

    public boolean hasTemplate(String name) {
        return false;
    }

    public boolean hasTemplate(QName name) {
        return false;
    }

    public boolean hasTemplate(int id) {
        return false;
    }

    public boolean isDefined(MessageTemplate template) {
        return false;
    }

    public MessageTemplate[] toArray() {
        return null;
    }

    public MessageTemplate get(QName name) {
        return null;
    }

    public int getId(String name) {
        return 0;
    }

    public int getId(MessageTemplate template) {
        return 0;
    }

    public boolean isDefined(QName name) {
        return false;
    }

    public boolean isDefined(String name) {
        return false;
    }

    public void register(int templateId, QName name) {}

    public void register(int templateId, String name) {}

    public void removeTemplateRegisteredListener(TemplateRegisteredListener templateRegisteredListener) {}

    public int getId(QName name) {
        return 0;
    }

    public boolean isRegistered(QName name) {
        return false;
    }

    public void remove(QName name) {}

    public void registerAll(TemplateRegistry registry) {}

    public Iterator nameIterator() {
        return Collections.EMPTY_LIST.iterator();
    }

    public Iterator iterator() {
        return null;
    }
}