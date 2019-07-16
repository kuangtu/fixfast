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

import org.openfast.BitVectorBuilder;
import org.openfast.Context;
import org.openfast.FieldValue;
import org.openfast.GroupValue;
import org.openfast.IntegerValue;
import org.openfast.Message;
import org.openfast.ScalarValue;
import org.openfast.StringValue;
import org.openfast.codec.FastDecoder;
import org.openfast.codec.FastEncoder;
import org.openfast.error.FastConstants;
import org.openfast.error.FastException;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.Type;
import org.openfast.test.OpenFastTestCase;
import org.openfast.test.TestUtil;

public class ScalarTest extends OpenFastTestCase {
    private Context context;

    public void setUp() {
        context = new Context();
    }

    /*
     * Test method for 'org.openfast.template.Scalar.encode(FieldValue,
     * FieldValue)'
     */
    public void testCopyEncodeWithNoPreviousValue() {
        Scalar scalar = new Scalar("a", Type.U32, Operator.COPY, ScalarValue.UNDEFINED, false);
        byte[] encoding = scalar.encode(new IntegerValue(1), new Group("", new Field[] { scalar }, false), context,
                new BitVectorBuilder(1));
        TestUtil.assertBitVectorEquals("10000001", encoding);
    }

    /*
     * Test method for 'org.openfast.template.Scalar.encode(FieldValue,
     * FieldValue)'
     */
    public void testCopyEncodeWithPreviousValue() {
        Scalar scalar = new Scalar("a", Type.U32, Operator.COPY, ScalarValue.UNDEFINED, false);
        scalar.encode(new IntegerValue(1), new Group("", new Field[] { scalar }, false), context, new BitVectorBuilder(1));
        byte[] encoding = scalar.encode(new IntegerValue(1), new Group("", new Field[] { scalar }, false), context,
                new BitVectorBuilder(1));
        TestUtil.assertBitVectorEquals("", encoding);
    }

    public void testInvalidConstantField() throws Exception {
        try {
            new Scalar("malformed", Type.U32, Operator.CONSTANT, ScalarValue.UNDEFINED, false);
            fail();
        } catch (FastException e) {
            assertEquals(FastConstants.S4_NO_INITIAL_VALUE_FOR_CONST, e.getCode());
            assertEquals(
                    "The field Scalar [name=malformed, operator=constant, type=uInt32, dictionary=global] must have a default value defined.",
                    e.getMessage());
        }
    }

    public void testInvalidDefaultField() throws Exception {
        new Scalar("malformed", Type.U32, Operator.DEFAULT, ScalarValue.UNDEFINED, true); // optional
                                                                                          // okay
        try {
            new Scalar("malformed", Type.U32, Operator.DEFAULT, ScalarValue.UNDEFINED, false); // mandatory
                                                                                               // not
                                                                                               // okay
            fail();
        } catch (FastException e) {
            assertEquals(FastConstants.S5_NO_INITVAL_MNDTRY_DFALT, e.getCode());
            assertEquals(
                    "The field Scalar [name=malformed, operator=default, type=uInt32, dictionary=global] must have a default value defined.",
                    e.getMessage());
        }
    }

    public void testPriorValueTypeConflict() {
        MessageTemplate template = template("<template>" + "  <uInt32 name=\"number\"><copy key=\"value\"/></uInt32>"
                + "  <string name=\"string\"><copy key=\"value\"/></string>" + "</template>");
        Message message = new Message(template);
        message.setInteger(1, 25);
        message.setString(2, "string");
        FastEncoder encoder = encoder(template);
        try {
            byte[] encoding = encoder.encode(message);
            FastDecoder decoder = decoder(template, encoding);
            decoder.readMessage();
            fail();
        } catch (FastException e) {
            assertEquals(FastConstants.D4_INVALID_TYPE, e.getCode());
        }
    }

    public void testUsesPresenceMapBit() throws Exception {
        assertFalse(new Scalar("InitialValue", Type.U32, Operator.NONE, null, true).usesPresenceMapBit());
    }

    public void testNoneOperatorDoesNotModifyDictionary() {
        MessageTemplate template = new MessageTemplate("tmpl", new Field[] {
                new Scalar("Name", Type.STRING, Operator.NONE, ScalarValue.UNDEFINED, false),
                new Group("Group", new Field[] { new Scalar("Name", Type.STRING, Operator.DELTA, ScalarValue.UNDEFINED, false) },
                        false) });
        Message message = new Message(template);
        message.setString("Name", "A");
        message.setFieldValue("Group", new GroupValue(template.getGroup("Group"), new FieldValue[] { new StringValue("AB") }));
        FastEncoder encoder = encoder(template);
        assertEquals("11000000 10000001 11000001 10000000 01000001 11000010", encoder.encode(message));
        assertEquals("10000000 11000001 10000000 10000000", encoder.encode(message));
    }
}
