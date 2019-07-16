package org.openfast.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.DataOutputStream;
import org.openfast.Message;
import org.openfast.MessageBlockWriter;

/** 
 * Write the CME preamble.  The sub-channel identifier (the fifth byte) is always (int)0.
 */
public class CmeMessageBlockWriter implements MessageBlockWriter {
	byte[] data = new byte[CmeConstants.PREAMBLE_LEN];

	/** Note: the CME Channel Sub ID (the fifth byte of the preamble) will always be zero. */
	public void writeBlockLength(OutputStream out, Message message, byte[] encodedMessage) throws IOException {
		out.write(writeBlockLength(data, message.getLong("MsgSeqNum"), 0));
	}
	
	/** Write seqNum and subId to data and return data. */
	final static byte[] writeBlockLength(byte[] data, final long seqNum, final int subId) {
		data[0] = (byte)((seqNum & 0xFF000000L) >> 24);
		data[1] = (byte)((seqNum & 0x00FF0000L) >> 16);
		data[2] = (byte)((seqNum & 0x0000FF00L) >> 8);
		data[3] = (byte)(seqNum & 0x000000FFL);
		data[4] = (byte)subId;
		return data;
	}

}
