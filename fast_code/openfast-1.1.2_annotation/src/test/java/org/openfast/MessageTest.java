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


package org.openfast;

import junit.framework.TestCase;

import org.openfast.template.Field;
import org.openfast.template.MessageTemplate;
import org.openfast.template.Scalar;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.Type;


public class MessageTest extends TestCase {
    /*
     * Test method for 'org.openfast.Message.equals(Object)'
     */
    public void testEquals() {
        MessageTemplate template = new MessageTemplate("",
                new Field[] {
                    new Scalar("1", Type.U32, Operator.COPY, ScalarValue.UNDEFINED, false)
                });
        GroupValue message = new Message(template);
        message.setInteger(1, 1);

        GroupValue other = new Message(template);
        other.setInteger(1, 1);

        assertEquals(message, other);
    }

    public void testNotEquals() {
        MessageTemplate template = new MessageTemplate("",
                new Field[] {
                    new Scalar("1", Type.U32, Operator.COPY, ScalarValue.UNDEFINED, false)
                });
        Message message = new Message(template);
        message.setInteger(1, 2);

        Message other = new Message(template);
        assertFalse(message.equals(other));
        assertFalse(other.equals(message));
        other.setInteger(1, 1);

        assertFalse(message.equals(other));
        assertFalse(other.equals(message));
    }
}
