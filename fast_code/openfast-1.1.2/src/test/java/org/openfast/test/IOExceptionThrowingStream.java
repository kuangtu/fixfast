package org.openfast.test;

import java.io.IOException;
import java.io.OutputStream;

public class IOExceptionThrowingStream extends OutputStream {

	public void write(int b) throws IOException {
		throw new IOException();
	}
	
	public void close() throws IOException {
		throw new IOException();
	}

}
