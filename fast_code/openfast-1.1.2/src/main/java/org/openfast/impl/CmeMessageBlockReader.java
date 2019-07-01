package org.openfast.impl;

import java.io.IOException;
import java.io.InputStream;
import org.openfast.Message;
import org.openfast.MessageBlockReader;

public class CmeMessageBlockReader implements MessageBlockReader {
	byte[] data = new byte[CmeConstants.PREAMBLE_LEN];
	int[] convertedBytes = new int[CmeConstants.PREAMBLE_LEN];
	long lastSeqNum;
	int lastSubId;

	public void messageRead(InputStream in, Message message) {
	}

	public boolean readBlock(InputStream in) {
		try {
			if(in.read(data) == -1)
				return false;
			readBlock(data);
			return true;
		}
		catch(final IOException e) {
			return false;
		}
	}
	
	void readBlock(byte[] data) {
		convertedBytes[0] = (0x000000FF & ((int)data[0]));
		convertedBytes[1] = (0x000000FF & ((int)data[1]));
		convertedBytes[2] = (0x000000FF & ((int)data[2]));
		convertedBytes[3] = (0x000000FF & ((int)data[3]));
		lastSeqNum = ((long)(convertedBytes[0] << 24
					| convertedBytes[1] << 16
					| convertedBytes[2] << 8
					| convertedBytes[3])) & 0xFFFFFFFFL;

		lastSubId = data[4];
	}

	public long getLastSeqNum() {
		return lastSeqNum;
	}

	public long getLastSubId() {
		return lastSubId;
	}

	@Override
	public String toString() {
		return "(" + lastSeqNum + "|" + lastSubId + ")";
	}
}
