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

import java.util.Arrays;
import java.util.List;
import org.openfast.test.OpenFastTestCase;


public class BasicTemplateRegistryTest extends OpenFastTestCase {

    // A defined template should be in the Registry

    public void testDefine() {
        MessageTemplate mt = new MessageTemplate("Logon", new Field[0]);
        TemplateRegistry registry = new BasicTemplateRegistry();
        registry.define(mt);

        assertContains(mt, registry);
        assertEquals(-1, registry.getId("Logon"));
        assertEquals(-1, registry.getId(mt));
        assertEquals(null, registry.get(1000));
        assertEquals(mt, registry.get("Logon"));
    }
    
    // A registerd template should be in the Registry with an ID

    public void testRegister() {
        MessageTemplate mt = new MessageTemplate("Logon", new Field[0]);
        TemplateRegistry registry = new BasicTemplateRegistry();
        registry.register(1000, mt);

        assertContains(mt, registry);
        assertEquals(1000, registry.getId("Logon"));
        assertEquals(1000, registry.getId(mt));
        assertEquals(mt, registry.get(1000));
        assertEquals(mt, registry.get("Logon"));
    }

    private void assertContains(MessageTemplate mt, TemplateRegistry registry) {
        List templates = Arrays.asList(registry.getTemplates());
        assertTrue(templates.contains(mt));
    }

}
