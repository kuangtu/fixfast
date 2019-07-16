package org.openfast;

import java.io.ByteArrayOutputStream;

import junit.framework.TestCase;

import org.openfast.error.FastConstants;
import org.openfast.error.FastException;
import org.openfast.test.IOExceptionOnCloseStream;
import org.openfast.test.IOExceptionThrowingStream;
import org.openfast.test.ObjectMother;

public class MessageOutputStreamTest extends TestCase {

	public void testWriteMessageMessage() {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		MessageOutputStream out = new MessageOutputStream(byteOut);
		try {
			out.writeMessage(new Message(ObjectMother.allocationInstruction()));
			fail();
		} catch (FastException e) {
			assertEquals(FastConstants.D9_TEMPLATE_NOT_REGISTERED, e.getCode());
		}
	}

	public void testIOErrorOnWrite() {
		MessageOutputStream out = new MessageOutputStream(new IOExceptionThrowingStream());
		out.registerTemplate(ObjectMother.ALLOC_INSTRCTN_TEMPLATE_ID, ObjectMother.allocationInstruction());
		Message message = ObjectMother.basicAllocationInstruction();
		try {
			out.writeMessage(message);
			fail();
		} catch (FastException e) {
			assertEquals(FastConstants.IO_ERROR, e.getCode());
		}
	}
	
	public void testIOErrorOnClose() {
		MessageOutputStream out = new MessageOutputStream(new IOExceptionOnCloseStream());
		out.registerTemplate(ObjectMother.ALLOC_INSTRCTN_TEMPLATE_ID, ObjectMother.allocationInstruction());
		Message message = ObjectMother.basicAllocationInstruction();
		try {
			out.writeMessage(message);
			out.close();
			fail();
		} catch (FastException e) {
			assertEquals(FastConstants.IO_ERROR, e.getCode());
		}
	}
}
