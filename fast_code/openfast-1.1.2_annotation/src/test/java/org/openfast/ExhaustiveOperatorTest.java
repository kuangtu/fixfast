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

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import junit.framework.TestCase;

import org.openfast.codec.FastDecoder;
import org.openfast.codec.FastEncoder;
import org.openfast.template.Field;
import org.openfast.template.MessageTemplate;
import org.openfast.template.Scalar;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.Type;
import org.openfast.test.TestUtil;


/**
 * The goal of this test is to make sure all cases of all operators are covered
 *
 * @author Jacob Northey
 */
public class ExhaustiveOperatorTest extends TestCase {
    private Context decodingContext;
    private Context encodingContext;
    private FastEncoder encoder;
    private PipedInputStream in;
    private PipedOutputStream out;
    private FastDecoder decoder;

    public void setUp() throws Exception {
        out = new PipedOutputStream();
        in = new PipedInputStream(out);
        encodingContext = new Context();
        decodingContext = new Context();
        encoder = new FastEncoder(encodingContext);
        decoder = new FastDecoder(decodingContext, in);
    }

    public void testEmptyOperatorWithOptionalField() throws Exception {
        Scalar field = new Scalar("", Type.U32, Operator.NONE,
                ScalarValue.UNDEFINED, true);
        MessageTemplate template = registerTemplate(field);

        Message message = new Message(template);
        message.setInteger(1, 126);

        //                 --PMAP-- --TID--- ---#1---
        String encoding = "11000000 11110001 11111111";

        encodeAndAssertEquals(encoding, message);

        GroupValue readMessage = decoder.readMessage();

        assertEquals(message, readMessage);
    }

    public void testEmptyOperatorWithOptionalFieldOnNullValue()
        throws Exception {
        Scalar field = new Scalar("", Type.U32, Operator.NONE,
                ScalarValue.UNDEFINED, true);
        MessageTemplate template = registerTemplate(field);

        // NOTE: The field is not set.
        Message message = new Message(template);

        //                 --PMAP-- --TID--- ---#1---
        String encoding = "11000000 11110001 10000000";

        encodeAndAssertEquals(encoding, message);

        GroupValue readMessage = decoder.readMessage();

        assertEquals(message, readMessage);
    }

    public void testEmptyOperatorWithSequenceOfMessages()
        throws IOException {
        Scalar field = new Scalar("", Type.U32, Operator.NONE,
                ScalarValue.UNDEFINED, true);
        MessageTemplate template = registerTemplate(field);

        // NOTE: The field is not set.
        Message msg1 = new Message(template);

        Message msg2 = new Message(template);
        msg2.setInteger(1, 15);

        //                 --PMAP-- --TID--- ---#1---
        String encoding = "11000000 11110001 10000000";
        byte[] encodedMessage;
        encodeAndAssertEquals(encoding, msg1);

        //          --PMAP-- ---#1---
        encoding = "10000000 10010000";
        encodedMessage = encoder.encode(msg2);
        TestUtil.assertBitVectorEquals(encoding, encodedMessage);
        out.write(encodedMessage);

        GroupValue readMessage = decoder.readMessage();
        assertEquals(msg1, readMessage);
        readMessage = decoder.readMessage();
        assertEquals(msg2, readMessage);
    }

    public void testEmptyOperatorWithMandatoryField() throws IOException {
        Scalar field = new Scalar("", Type.U32, Operator.NONE,
                ScalarValue.UNDEFINED, false);
        MessageTemplate template = registerTemplate(field);

        // NOTE: The field is not set.
        Message msg1 = new Message(template);
        msg1.setInteger(1, 0);

        Message msg2 = new Message(template);
        msg2.setInteger(1, 16);

        //                 --PMAP-- --TID--- ---#1---
        String encoding = "11000000 11110001 10000000";
        byte[] encodedMessage;
        encodeAndAssertEquals(encoding, msg1);

        //          --PMAP-- ---#1---
        encoding = "10000000 10010000";
        encodedMessage = encoder.encode(msg2);
        TestUtil.assertBitVectorEquals(encoding, encodedMessage);
        out.write(encodedMessage);

        GroupValue readMessage = decoder.readMessage();
        assertEquals(msg1, readMessage);
        readMessage = decoder.readMessage();
        assertEquals(msg2, readMessage);
    }

    public void testConstantOperatorWithOptionalField()
        throws IOException {
        Scalar field = new Scalar("", Type.U32, Operator.CONSTANT, new IntegerValue(16), true);
        MessageTemplate template = registerTemplate(field);

        Message msg1 = new Message(template);

        Message msg2 = new Message(template);
        msg2.setInteger(1, 16);

        //                     --PMAP-- --TID---
        encodeAndAssertEquals("11000000 11110001", msg1);

        //                     --PMAP--
        encodeAndAssertEquals("10100000", msg2);

        readMessageAndAssertEquals(msg1);
        readMessageAndAssertEquals(msg2);
    }

    public void testConstantOperatorWithMandatoryField()
        throws IOException {
        Scalar field = new Scalar("", Type.U32, Operator.CONSTANT, new IntegerValue(16), false);
        MessageTemplate template = registerTemplate(field); 

        // NOTE: The field is not set.
        Message msg1 = new Message(template);
//        msg1.setInteger(1, 16);

        Message msg2 = new Message(template);
        msg2.setInteger(1, 16);

        //                 --PMAP-- --TID---
        encodeAndAssertEquals("11000000 11110001", msg1);

        //                     --PMAP--
        encodeAndAssertEquals("10000000", msg2);

        GroupValue readMessage = decoder.readMessage();
        assertEquals(msg1, readMessage);
        readMessage = decoder.readMessage();
        assertEquals(msg2, readMessage);
    }

    public void testDefaultOperatorWithOptionalField() {
        Scalar field = new Scalar("", Type.U32, Operator.DEFAULT,
                new IntegerValue(16), true);
        MessageTemplate template = registerTemplate(field);

        // NOTE: The field is not set.
        Message msg1 = new Message(template);

        Message msg2 = new Message(template);
        msg2.setInteger(1, 16);

        Message msg3 = new Message(template);
        msg3.setInteger(1, 20);

        //                     --PMAP-- --TID--- ---#1---
        encodeAndAssertEquals("11100000 11110001 10000000", msg1);

        //                     --PMAP--
        encodeAndAssertEquals("10000000", msg2);

        //                     --PMAP-- ---#1---
        encodeAndAssertEquals("10100000 10010101", msg3);

        readMessageAndAssertEquals(msg1);
        readMessageAndAssertEquals(msg2);
        readMessageAndAssertEquals(msg3);
    }

    public void testDefaultOperatorWithMandatoryField() {
        Scalar field = new Scalar("", Type.U32, Operator.DEFAULT,
                new IntegerValue(16), false);
        MessageTemplate template = registerTemplate(field);

        Message msg1 = new Message(template);
        msg1.setInteger(1, 16);

        Message msg2 = new Message(template);
        msg2.setInteger(1, 20);

        //                     --PMAP-- --TID---
        encodeAndAssertEquals("11000000 11110001", msg1);

        //                     --PMAP-- ---#1---
        encodeAndAssertEquals("10100000 10010100", msg2);

        readMessageAndAssertEquals(msg1);
        readMessageAndAssertEquals(msg2);
    }

    public void testCopyOperatorWithOptionalField() {
        Scalar field = new Scalar("", Type.U32, Operator.COPY,
                new IntegerValue(16), true);
        MessageTemplate template = registerTemplate(field);

        Message msg1 = new Message(template);
        msg1.setInteger(1, 16);

        // NOTE: The field is not set.
        Message msg2 = new Message(template);

        Message msg3 = new Message(template);
        msg3.setInteger(1, 20);

        Message msg4 = new Message(template);
        msg4.setInteger(1, 20);

        //                     --PMAP-- --TID---
        encodeAndAssertEquals("11000000 11110001", msg1);

        //                     --PMAP-- ---#1---
        encodeAndAssertEquals("10100000 10000000", msg2);

        //                     --PMAP-- ---#1---
        encodeAndAssertEquals("10100000 10010101", msg3);

        //                     --PMAP--
        encodeAndAssertEquals("10000000", msg4);

        readMessageAndAssertEquals(msg1);
        readMessageAndAssertEquals(msg2);
        readMessageAndAssertEquals(msg3);
        readMessageAndAssertEquals(msg4);
    }

    public void testCopyOperatorWithMandatoryField() {
        Scalar field = new Scalar("", Type.U32, Operator.COPY,
                new IntegerValue(16), false);
        MessageTemplate template = registerTemplate(field);

        Message msg1 = new Message(template);
        msg1.setInteger(1, 16);

        Message msg2 = new Message(template);
        msg2.setInteger(1, 20);

        Message msg3 = new Message(template);
        msg3.setInteger(1, 20);

        //                     --PMAP-- --TID---
        encodeAndAssertEquals("11000000 11110001", msg1);

        //                     --PMAP-- ---#1---
        encodeAndAssertEquals("10100000 10010100", msg2);

        //                     --PMAP--
        encodeAndAssertEquals("10000000", msg3);

        readMessageAndAssertEquals(msg1);
        readMessageAndAssertEquals(msg2);
        readMessageAndAssertEquals(msg3);
    }

    public void testIncrementOperatorWithOptionalField() {
        Scalar field = new Scalar("", Type.U32,
                Operator.INCREMENT, new IntegerValue(16), true);
        MessageTemplate template = registerTemplate(field);

        Message msg1 = new Message(template);
        msg1.setInteger(1, 16);

        Message msg2 = new Message(template);
        msg2.setInteger(1, 17);

        // NOTE: The field is not set.		
        Message msg3 = new Message(template);

        Message msg4 = new Message(template);
        msg4.setInteger(1, 20);

        //                     --PMAP-- --TID---
        encodeAndAssertEquals("11000000 11110001", msg1);

        //                     --PMAP--
        encodeAndAssertEquals("10000000", msg2);

        //                     --PMAP-- ---#1---
        encodeAndAssertEquals("10100000 10000000", msg3);

        //                     --PMAP-- ---#1---
        encodeAndAssertEquals("10100000 10010101", msg4);

        readMessageAndAssertEquals(msg1);
        readMessageAndAssertEquals(msg2);
        readMessageAndAssertEquals(msg3);
        readMessageAndAssertEquals(msg4);
    }

    public void testIncrementOperatorWithMandatoryField() {
        Scalar field = new Scalar("", Type.U32,
                Operator.INCREMENT, new IntegerValue(16), false);
        MessageTemplate template = registerTemplate(field);

        Message msg1 = new Message(template);
        msg1.setInteger(1, 16);

        Message msg2 = new Message(template);
        msg2.setInteger(1, 17);

        // NOTE: The field is not set.		
        Message msg3 = new Message(template);
        msg3.setInteger(1, 20);

        //                     --PMAP-- --TID---
        encodeAndAssertEquals("11000000 11110001", msg1);

        //                     --PMAP-- ---#1---
        encodeAndAssertEquals("10000000", msg2);

        //                     --PMAP--
        encodeAndAssertEquals("10100000 10010100", msg3);

        readMessageAndAssertEquals(msg1);
        readMessageAndAssertEquals(msg2);
        readMessageAndAssertEquals(msg3);
    }

    public void testDeltaOperatorWithOptionalField() {
        Scalar field = new Scalar("", Type.U32, Operator.DELTA,
                new IntegerValue(16), true);
        MessageTemplate template = registerTemplate(field);

        Message msg1 = new Message(template);
        msg1.setInteger(1, 16);

        Message msg2 = new Message(template);
        msg2.setInteger(1, 17);

        // NOTE: The field is not set.		
        Message msg3 = new Message(template);

        Message msg4 = new Message(template);
        msg4.setInteger(1, 20);

        //                     --PMAP-- --TID--- ---#1---
        encodeAndAssertEquals("11000000 11110001 10000001", msg1);

        //                     --PMAP-- ---#1---
        encodeAndAssertEquals("10000000 10000010", msg2);

        //                     --PMAP-- ---#1---
        encodeAndAssertEquals("10000000 10000000", msg3);

        //                     --PMAP-- ---#1---
        encodeAndAssertEquals("10000000 10000100", msg4);

        readMessageAndAssertEquals(msg1);
        readMessageAndAssertEquals(msg2);
        readMessageAndAssertEquals(msg3);
        readMessageAndAssertEquals(msg4);
    }

    public void testDeltaOperatorWithMandatoryField() {
        Scalar field = new Scalar("", Type.U32,
                Operator.INCREMENT, new IntegerValue(16), false);
        MessageTemplate template = registerTemplate(field);

        Message msg1 = new Message(template);
        msg1.setInteger(1, 16);

        Message msg2 = new Message(template);
        msg2.setInteger(1, 17);

        // NOTE: The field is not set.		
        Message msg3 = new Message(template);
        msg3.setInteger(1, 20);

        //                     --PMAP-- --TID---
        encodeAndAssertEquals("11000000 11110001", msg1);

        //                     --PMAP-- ---#1---
        encodeAndAssertEquals("10000000", msg2);

        //                     --PMAP--
        encodeAndAssertEquals("10100000 10010100", msg3);

        readMessageAndAssertEquals(msg1);
        readMessageAndAssertEquals(msg2);
        readMessageAndAssertEquals(msg3);
    }

    public void testTailOperatorWithOptionalField() {
        Scalar field = new Scalar("", Type.STRING, Operator.TAIL,
                new StringValue("abc"), true);
        MessageTemplate template = registerTemplate(field);

        Message msg1 = new Message(template);
        msg1.setString(1, "abc");

        Message msg2 = new Message(template);
        msg2.setString(1, "abd");

        // NOTE: The field is not set.		
        Message msg3 = new Message(template);

        Message msg4 = new Message(template);
        msg4.setString(1, "dbef");

        //                     --PMAP-- --TID---
        encodeAndAssertEquals("11000000 11110001", msg1);

        //                     --PMAP-- ---#1---
        encodeAndAssertEquals("10100000 11100100", msg2);

        //                     --PMAP-- ---#1---
        encodeAndAssertEquals("10100000 10000000", msg3);

        //                     --PMAP-- -----------------#1----------------
        encodeAndAssertEquals("10100000 01100100 01100010 01100101 11100110",
            msg4);

        readMessageAndAssertEquals(msg1);
        readMessageAndAssertEquals(msg2);
        readMessageAndAssertEquals(msg3);
        readMessageAndAssertEquals(msg4);
    }

    public void testTailOperatorWithMandatoryField() {
        Scalar field = new Scalar("", Type.STRING, Operator.TAIL,
                new StringValue("abc"), false);
        MessageTemplate template = registerTemplate(field);

        Message msg1 = new Message(template);
        msg1.setString(1, "abc");

        Message msg2 = new Message(template);
        msg2.setString(1, "abd");

        // NOTE: The field is not set.		
        Message msg3 = new Message(template);
        msg3.setString(1, "abc");

        Message msg4 = new Message(template);
        msg4.setString(1, "dbef");

        //                     --PMAP-- --TID---
        encodeAndAssertEquals("11000000 11110001", msg1);

        //                     --PMAP-- ---#1---
        encodeAndAssertEquals("10100000 11100100", msg2);

        //                     --PMAP-- ---#1---
        encodeAndAssertEquals("10100000 11100011", msg3);

        //                     --PMAP-- -----------------#1----------------
        encodeAndAssertEquals("10100000 01100100 01100010 01100101 11100110",
            msg4);

        readMessageAndAssertEquals(msg1);
        readMessageAndAssertEquals(msg2);
        readMessageAndAssertEquals(msg3);
        readMessageAndAssertEquals(msg4);
    }

    private MessageTemplate registerTemplate(Scalar field) {
        MessageTemplate messageTemplate = new MessageTemplate("", new Field[] { field });
        encodingContext.registerTemplate(113, messageTemplate);
        decodingContext.registerTemplate(113, messageTemplate);

        return messageTemplate;
    }

    private void readMessageAndAssertEquals(GroupValue msg1) {
        GroupValue readMessage = decoder.readMessage();
        assertEquals(msg1, readMessage);
    }

    private void encodeAndAssertEquals(String encoding, Message msg1) {
        byte[] encodedMessage = encoder.encode(msg1);
        TestUtil.assertBitVectorEquals(encoding, encodedMessage);

        try {
            out.write(encodedMessage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
