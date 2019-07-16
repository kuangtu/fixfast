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

import java.util.Iterator;

import org.openfast.QName;

public interface TemplateRegistry {
    TemplateRegistry NULL = new NullTemplateRegistry();

    void registerAll(TemplateRegistry registry);
    void register(int id, MessageTemplate template);
    void register(int id, String name);
    void register(int id, QName name);
    void define(MessageTemplate template);
    void remove(String name);
    void remove(QName name);
    void remove(MessageTemplate template);
    void remove(int id);
    MessageTemplate get(int id);
    MessageTemplate get(String name);
    MessageTemplate get(QName name);
    MessageTemplate[] getTemplates();
    int getId(String name);
    int getId(QName name);
    int getId(MessageTemplate template);
    boolean isRegistered(String name);
    boolean isRegistered(QName name);
    boolean isRegistered(int id);
    boolean isRegistered(MessageTemplate template);
    boolean isDefined(String name);
    boolean isDefined(QName name);
    void addTemplateRegisteredListener(TemplateRegisteredListener templateRegisteredListener);
    void removeTemplateRegisteredListener(TemplateRegisteredListener templateRegisteredListener);
    /**
     * Iterator over the names of each template (defined or registered) in this
     * registry
     * 
     * @return an iterator over the qualified names each item is of type QName
     */
    Iterator/* <QName> */nameIterator();
    /**
     * Iterator over the set of templates (defined or registered) in this
     * registry
     * 
     * @return an iterator over the set of templates each item is an instance of
     *         MessageTemplate
     */
    Iterator/* <MessageTemplate> */iterator();
}
