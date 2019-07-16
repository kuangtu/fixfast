package org.openfast.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.DataOutputStream;
import org.openfast.IntegerValue;
import org.openfast.Message;
import org.openfast.MessageBlockWriter;
import org.openfast.template.type.codec.TypeCodec;

/** 
 * Write the CME "length indicator" and "preamble" headers.
 * The sub-channel identifier (the fifth byte) is always (int)0.
 */
public class CmeTcpReplayMessageBlockWriter implements MessageBlockWriter {
    CmeMessageBlockWriter preambleWriter = new CmeMessageBlockWriter();

    public void writeBlockLength(OutputStream out, Message message, byte[] encodedMessage) throws IOException {
        out.write(encodeTotalLen(encodedMessage.length, CmeConstants.PREAMBLE_LEN));
        preambleWriter.writeBlockLength(out, message, encodedMessage);
    }

    final static byte[] encodeTotalLen(int encodedMessageLen, int preambleLen) {
        byte [] result = TypeCodec.UINT.encodeValue(new IntegerValue(preambleLen + encodedMessageLen));
        final int endIndex = result.length - 1;
        result[endIndex] = (byte)(result[endIndex] | (byte)0x80); // set stop bit
        return result;
    }
}
