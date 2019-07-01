package org.openfast;

import java.io.IOException;
import java.io.OutputStream;

public interface MessageBlockWriter {

    MessageBlockWriter NULL = new MessageBlockWriter() {
        public void writeBlockLength(OutputStream out, Message message, byte[] encodedMessage) throws IOException {
		}
	};

    void writeBlockLength(OutputStream out, Message message, byte[] encodedMessage) throws IOException;
}
