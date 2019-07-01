package org.openfast.examples;

import java.io.OutputStream;
import java.io.IOException;
import org.openfast.Message;
import org.openfast.MessageBlockWriter;
import org.openfast.examples.OpenFastExample.Variant;
import org.openfast.impl.*;

public class MessageBlockWriterFactory {
	final Variant variant;
	final int offset;
    final boolean isMulticast;
		
	public MessageBlockWriterFactory() {
		this(Variant.DEFAULT, 0, true);
	}

	public MessageBlockWriterFactory(final Variant variant, final int offset, boolean isMulticast) {
		this.variant = variant;
		this.offset = offset;
        this.isMulticast = isMulticast;
	}

	public MessageBlockWriter create() {
        if(Variant.CME == variant)
        {
            if(this.isMulticast)
                return new CmeMessageBlockWriter();
            else
                return new CmeTcpReplayMessageBlockWriter();
        }
        return createDefault();
	}

	MessageBlockWriter createDefault() {
		if(offset <= 0)
			return MessageBlockWriter.NULL;

		return new MessageBlockWriter() {
			final byte[] PREAMBLE = new byte[offset];
			public void writeBlockLength(OutputStream out, Message message, byte[] data) throws IOException {
				try {
					out.write(PREAMBLE);
				}
				catch(final IOException e) {
				}
			}
		};
	}
}
