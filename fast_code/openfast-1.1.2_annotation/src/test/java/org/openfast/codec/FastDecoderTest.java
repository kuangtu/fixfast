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

package org.openfast.codec;

import java.io.InputStream;

import junit.framework.TestCase;

import org.openfast.ByteUtil;
import org.openfast.Context;
import org.openfast.GroupValue;
import org.openfast.IntegerValue;
import org.openfast.Message;
import org.openfast.ScalarValue;
import org.openfast.StringValue;
import org.openfast.template.Field;
import org.openfast.template.MessageTemplate;
import org.openfast.template.Scalar;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.Type;

public class FastDecoderTest extends TestCase {
    public void testDecodeEmptyMessage() {
        MessageTemplate messageTemplate = new MessageTemplate("", new Field[] {});
        InputStream in = ByteUtil.createByteStream("11000000 11110001");
        Context context = new Context();
        context.registerTemplate(113, messageTemplate);

        Message message = new FastDecoder(context, in).readMessage();
        assertEquals(113, message.getInt(0));
    }

    public void testDecodeSequentialEmptyMessages() {
        MessageTemplate messageTemplate = new MessageTemplate("", new Field[] {});
        InputStream in = ByteUtil.createByteStream("11000000 11110001 10000000");
        Context context = new Context();
        context.registerTemplate(113, messageTemplate);

        FastDecoder decoder = new FastDecoder(context, in);
        GroupValue message = decoder.readMessage();
        GroupValue message2 = decoder.readMessage();
        assertEquals(113, message.getInt(0));
        assertEquals(113, message2.getInt(0));
    }

    public void testDecodeSimpleMessage() {
        MessageTemplate template = new MessageTemplate("", new Field[] { new Scalar("1", Type.U32, Operator.COPY,
                ScalarValue.UNDEFINED, false) });
        InputStream in = ByteUtil.createByteStream("11100000 11110001 10000001");
        Context context = new Context();
        context.registerTemplate(113, template);

        Message message = new Message(template);
        message.setInteger(1, 1);

        FastDecoder decoder = new FastDecoder(context, in);
        GroupValue readMessage = decoder.readMessage();
        assertEquals(message, readMessage);
        assertEquals(readMessage, message);
    }

    public void testDecodeMessageWithAllFieldTypes() {
        // --PMAP-- --TID--- ---#1--- -------#2--------
        // ------------#3------------ ---#4--- ------------#5------------
        // ---#6---
        String msgstr = "11111111 11110001 11001000 10000001 11111111 11111101 00001001 10110001 11111111 01100001 01100010 11100011 10000010";
        InputStream in = ByteUtil.createByteStream(msgstr);

        MessageTemplate template = new MessageTemplate("", new Field[] {
                new Scalar("1", Type.ASCII, Operator.COPY, ScalarValue.UNDEFINED, false),
                new Scalar("2", Type.BYTE_VECTOR, Operator.COPY, ScalarValue.UNDEFINED, false),
                new Scalar("3", Type.DECIMAL, Operator.COPY, ScalarValue.UNDEFINED, false),
                new Scalar("4", Type.I32, Operator.COPY, ScalarValue.UNDEFINED, false),
                new Scalar("5", Type.ASCII, Operator.COPY, ScalarValue.UNDEFINED, false),
                new Scalar("6", Type.U32, Operator.COPY, ScalarValue.UNDEFINED, false), });
        Context context = new Context();
        context.registerTemplate(113, template);

        GroupValue message = new Message(template);
        message.setString(1, "H");
        message.setByteVector(2, new byte[] { (byte) 0xFF });
        message.setDecimal(3, 1.201);
        message.setInteger(4, -1);
        message.setString(5, "abc");
        message.setInteger(6, 2);
        assertEquals(message, new FastDecoder(context, in).readMessage());
    }

    public void testDecodeMessageWithSignedIntegerFieldTypesAndAllOperators() {
        MessageTemplate template = new MessageTemplate("", new Field[] {
                new Scalar("1", Type.I32, Operator.COPY, ScalarValue.UNDEFINED, false),
                new Scalar("2", Type.I32, Operator.DELTA, ScalarValue.UNDEFINED, false),
                new Scalar("3", Type.I32, Operator.INCREMENT, new IntegerValue(10), false),
                new Scalar("4", Type.I32, Operator.INCREMENT, ScalarValue.UNDEFINED, false),
                new Scalar("5", Type.I32, Operator.CONSTANT, new IntegerValue(1), false), /*
                                                                                           * NON
                                                                                           * -
                                                                                           * TRANSFERRABLE
                                                                                           */
                new Scalar("6", Type.I32, Operator.DEFAULT, new IntegerValue(2), false) });

        GroupValue message = new Message(template);
        message.setInteger(1, 109);
        message.setInteger(2, 29470);
        message.setInteger(3, 10);
        message.setInteger(4, 3);
        message.setInteger(5, 1);
        message.setInteger(6, 2);

        // --PMAP-- --TID--- --------#1------- ------------#2------------
        // ---#4---
        String msg1 = "11101000 11110001 00000000 11101101 00000001 01100110 10011110 10000011";

        // --PMAP-- ---#2--- ---#6---
        String msg2 = "10000100 11111111 10000011";

        // --PMAP-- --------#1------- --------#2------- ---#4--- ---#6---
        String msg3 = "10101100 00000000 11100000 00001000 10000111 10000001 10000011";

        InputStream in = ByteUtil.createByteStream(msg1 + ' ' + msg2 + ' ' + msg3);
        Context context = new Context();
        context.registerTemplate(113, template);

        FastDecoder decoder = new FastDecoder(context, in);

        Message readMessage = decoder.readMessage();
        assertEquals(message, readMessage);

        message.setInteger(2, 29469);
        message.setInteger(3, 11);
        message.setInteger(4, 4);
        message.setInteger(6, 3);

        readMessage = decoder.readMessage();
        assertEquals(message, readMessage);

        message.setInteger(1, 96);
        message.setInteger(2, 30500);
        message.setInteger(3, 12);
        message.setInteger(4, 1);

        readMessage = decoder.readMessage();
        assertEquals(message, readMessage);
    }

    public void testDecodeMessageWithUnsignedIntegerFieldTypesAndAllOperators() {
        MessageTemplate template = new MessageTemplate("", new Field[] {
                new Scalar("1", Type.U32, Operator.COPY, ScalarValue.UNDEFINED, false),
                new Scalar("2", Type.U32, Operator.DELTA, ScalarValue.UNDEFINED, false),
                new Scalar("3", Type.U32, Operator.INCREMENT, new IntegerValue(10), false),
                new Scalar("4", Type.U32, Operator.INCREMENT, ScalarValue.UNDEFINED, false),
                new Scalar("5", Type.U32, Operator.CONSTANT, new IntegerValue(1), false), /*
                                                                                           * NON
                                                                                           * -
                                                                                           * TRANSFERRABLE
                                                                                           */
                new Scalar("6", Type.U32, Operator.DEFAULT, new IntegerValue(2), false) });

        GroupValue message = new Message(template);
        message.setInteger(1, 109);
        message.setInteger(2, 29470);
        message.setInteger(3, 10);
        message.setInteger(4, 3);
        message.setInteger(5, 1);
        message.setInteger(6, 2);

        // --PMAP-- --TID--- ---#1--- ------------#2------------ ---#4---
        String msg1 = "11101000 11110001 11101101 00000001 01100110 10011110 10000011";

        // --PMAP-- ---#2--- ---#6---
        String msg2 = "10000100 11111111 10000011";

        // --PMAP-- ---#1--- --------#2------- ---#4--- ---#6---
        String msg3 = "10101100 11100000 00001000 10000111 10000001 10000011";

        InputStream in = ByteUtil.createByteStream(msg1 + ' ' + msg2 + ' ' + msg3);
        Context context = new Context();
        context.registerTemplate(113, template);

        FastDecoder decoder = new FastDecoder(context, in);

        Message readMessage = decoder.readMessage();
        assertEquals(message, readMessage);

        message.setInteger(2, 29469);
        message.setInteger(3, 11);
        message.setInteger(4, 4);
        message.setInteger(6, 3);

        readMessage = decoder.readMessage();
        assertEquals(message, readMessage);

        message.setInteger(1, 96);
        message.setInteger(2, 30500);
        message.setInteger(3, 12);
        message.setInteger(4, 1);

        readMessage = decoder.readMessage();
        assertEquals(message, readMessage);
    }

    public void testDecodeMessageWithStringFieldTypesAndAllOperators() {
        MessageTemplate template = new MessageTemplate("", new Field[] {
                new Scalar("1", Type.ASCII, Operator.COPY, ScalarValue.UNDEFINED, false),
                new Scalar("2", Type.ASCII, Operator.DELTA, ScalarValue.UNDEFINED, false),
                new Scalar("3", Type.ASCII, Operator.CONSTANT, new StringValue("e"), false), /*
                                                                                              * NON
                                                                                              * -
                                                                                              * TRANSFERRABLE
                                                                                              */
                new Scalar("4", Type.ASCII, Operator.DEFAULT, new StringValue("long"), false) });

        Message message = new Message(template);
        message.setString(1, "on");
        message.setString(2, "DCB32");
        message.setString(3, "e");
        message.setString(4, "long");

        // --PMAP-- --TID--- --------#1-------
        // ---------------------#2---------------------
        String msg1 = "11100000 11110001 01101111 11101110 10000000 01000100 01000011 01000010 00110011 10110010";

        // --PMAP-- ------------#2------------
        // ---------------------#4---------------------
        String msg2 = "10010000 10000010 00110001 10110110 01110011 01101000 01101111 01110010 11110100";

        InputStream in = ByteUtil.createByteStream(msg1 + ' ' + msg2);
        Context context = new Context();
        context.registerTemplate(113, template);

        FastDecoder decoder = new FastDecoder(context, in);

        Message readMessage = decoder.readMessage();
        assertEquals(message, readMessage);

        message.setString(2, "DCB16");
        message.setString(4, "short");

        readMessage = decoder.readMessage();
        assertEquals(message, readMessage);
    }
}
