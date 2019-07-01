package org.openfast.impl;

import java.io.IOException;
import java.io.InputStream;
import org.openfast.Message;
import org.openfast.MessageBlockReader;
import org.openfast.template.type.codec.TypeCodec;

public class CmeTcpReplayMessageBlockReader implements MessageBlockReader {
	CmeMessageBlockReader preambleReader = new CmeMessageBlockReader();
    long lengthIndicator = 0;

	public void messageRead(InputStream in, Message message) {
	}

	public boolean readBlock(InputStream in) {
        lengthIndicator = TypeCodec.UINT.decode(in).toLong();
        return preambleReader.readBlock(in);
	}

    public long getLastLengthIndicator() {
        return lengthIndicator;
    }
	
	public long getLastSeqNum() {
		return preambleReader.getLastSeqNum();
	}

	public long getLastSubId() {
		return preambleReader.getLastSubId();
	}

	@Override
	public String toString() {
		return "(" + getLastLengthIndicator() + "|" + getLastSeqNum() + "|" + getLastSubId() + ")";
	}
}

