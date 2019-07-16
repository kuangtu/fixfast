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
import java.util.Collections;
import java.util.List;
import org.openfast.QName;

public abstract class AbstractTemplateRegistry implements TemplateRegistry {
    private List listeners = Collections.EMPTY_LIST;

    public MessageTemplate get(String name) {
        return get(new QName(name, ""));
    }

    public int getId(String name) {
        return getId(new QName(name, ""));
    }

    public boolean isDefined(String name) {
        return isDefined(new QName(name, ""));
    }

    public boolean isRegistered(String name) {
        return isRegistered(new QName(name, ""));
    }

    public void register(int templateId, String name) {
        register(templateId, new QName(name, ""));
    }

    public void remove(String name) {
        remove(new QName(name, ""));
    }

    protected void notifyTemplateRegistered(MessageTemplate template, int id) {
        for (int i = 0; i < listeners.size(); i++)
            ((TemplateRegisteredListener) listeners.get(i)).templateRegistered(template, id);
    }

    public void addTemplateRegisteredListener(TemplateRegisteredListener templateRegisteredListener) {
        if (this.listeners == Collections.EMPTY_LIST)
            this.listeners = new ArrayList(3);
        this.listeners.add(templateRegisteredListener);
    }

    public void removeTemplateRegisteredListener(TemplateRegisteredListener templateRegisteredListener) {
        this.listeners.remove(templateRegisteredListener);
    }
}
