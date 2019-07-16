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


package org.openfast.impl;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import junit.framework.TestCase;
import org.openfast.Message;
import org.openfast.ScalarValue;
import org.openfast.template.Field;
import org.openfast.template.MessageTemplate;
import org.openfast.template.Scalar;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.Type;

public class CmeTcpReplayMessageBlockReaderWriterTest extends TestCase {
    final static byte[] encodedMessageWithHeader = {
        (byte)0x91,                                                 // "Length Indicator" (17)
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x00, // "Preamble" (seqNo=1, subId=0)
        (byte)0xc0, (byte)0x83, (byte)0x81, (byte)0x23,             // FAST message...
        (byte)0x5c, (byte)0x5e, (byte)0x0d, (byte)0x5f,             // FAST message...
        (byte)0x03, (byte)0x60, (byte)0xc4, (byte)0x82              // FAST message.
    };

    final byte[] encodedMessageSansHeader = {
        (byte)0xc0, (byte)0x83, (byte)0x81, (byte)0x23,
        (byte)0x5c, (byte)0x5e, (byte)0x0d, (byte)0x5f,
        (byte)0x03, (byte)0x60, (byte)0xc4, (byte)0x82
    };

    final static MessageTemplate msgTemplate = new MessageTemplate("",
        new Field[] {
            new Scalar("MsgSeqNum", Type.U32, Operator.COPY, ScalarValue.UNDEFINED, false)
        }
    );

    public void testEncodeTotalLen() {
        byte[] encoded = CmeTcpReplayMessageBlockWriter.encodeTotalLen(encodedMessageSansHeader.length, CmeConstants.PREAMBLE_LEN);
        assertEquals(encodedMessageWithHeader[0], encoded[0]);
    }

    public void testRead() {
        CmeTcpReplayMessageBlockReader reader = new CmeTcpReplayMessageBlockReader();
        assertTrue(reader.readBlock(new ByteArrayInputStream(encodedMessageWithHeader)));
		assertEquals(17, reader.getLastLengthIndicator());
		assertEquals(1, reader.getLastSeqNum());
		assertEquals(0, reader.getLastSubId());
    }

    public void testWriteThenRead() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        Message msg = new Message(msgTemplate);
        msg.setLong("MsgSeqNum", 1L);
       
        CmeTcpReplayMessageBlockWriter writer = new CmeTcpReplayMessageBlockWriter();
        writer.writeBlockLength(out, msg, encodedMessageSansHeader);

        CmeTcpReplayMessageBlockReader reader = new CmeTcpReplayMessageBlockReader();
        assertTrue(reader.readBlock(new ByteArrayInputStream(out.toByteArray())));
        assertEquals(encodedMessageSansHeader.length + CmeConstants.PREAMBLE_LEN , reader.getLastLengthIndicator());
        assertEquals(msg.getLong("MsgSeqNum"), reader.getLastSeqNum());
        assertEquals(0, reader.getLastSubId());
    }
}
