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

import java.io.InputStream;

import junit.framework.TestCase;

import org.openfast.BitVectorBuilder;
import org.openfast.BitVectorReader;
import org.openfast.ByteUtil;
import org.openfast.Context;
import org.openfast.FieldValue;
import org.openfast.IntegerValue;
import org.openfast.ScalarValue;
import org.openfast.SequenceValue;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.Type;
import org.openfast.test.TestUtil;


public class SequenceTest extends TestCase {
    private Group template;
    private Context context;

    protected void setUp() throws Exception {
        template = new MessageTemplate("", new Field[] {  });
        context = new Context();
    }

    public void testEncode() {
        Scalar firstName = new Scalar("First Name", Type.I32,
                Operator.COPY, ScalarValue.UNDEFINED, false);
        Scalar lastName = new Scalar("Last Name", Type.I32,
                Operator.COPY, ScalarValue.UNDEFINED, false);
        Sequence sequence1 = new Sequence("Contacts",
                new Field[] { firstName, lastName }, false);

        SequenceValue sequenceValue = new SequenceValue(sequence1);
        sequenceValue.add(new FieldValue[] {
                new IntegerValue(1), new IntegerValue(2)
            });
        sequenceValue.add(new FieldValue[] {
                new IntegerValue(3), new IntegerValue(4)
            });

        byte[] actual = sequence1.encode(sequenceValue, template, context, new BitVectorBuilder(1));
        String expected = "10000010 11100000 10000001 10000010 11100000 10000011 10000100";
        TestUtil.assertBitVectorEquals(expected, actual);
    }

    public void testDecode() {
        String actual = "10000010 11100000 10000001 10000010 11100000 10000011 10000100";
        InputStream stream = ByteUtil.createByteStream(actual);

        Scalar firstNumber = new Scalar("First Number", Type.I32,
                Operator.COPY, ScalarValue.UNDEFINED, false);
        Scalar lastNumber = new Scalar("Second Number", Type.I32,
                Operator.COPY, ScalarValue.UNDEFINED, false);
        Sequence sequence1 = new Sequence("Contants",
                new Field[] { firstNumber, lastNumber }, false);

        SequenceValue sequenceValue = new SequenceValue(sequence1);
        sequenceValue.add(new FieldValue[] {
                new IntegerValue(1), new IntegerValue(2)
            });
        sequenceValue.add(new FieldValue[] {
                new IntegerValue(3), new IntegerValue(4)
            });

        FieldValue result = sequence1.decode(stream, template, context, BitVectorReader.INFINITE_TRUE);
        assertEquals(sequenceValue, result);
    }
}
