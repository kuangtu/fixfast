package org.openfast.test;

import java.io.IOException;
import java.io.OutputStream;

public class IOExceptionOnCloseStream extends OutputStream {

	public void write(int b) throws IOException {
	}
	
	public void close() throws IOException {
		throw new IOException();
	}

}
