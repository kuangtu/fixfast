package org.openfast;

import junit.framework.TestCase;

public class BitVectorReaderTest extends TestCase {

	public void testRead() {
		BitVectorReader reader = new BitVectorReader(new BitVector(ByteUtil.convertBitStringToFastByteArray("11000000")));
		assertTrue(reader.read());
		assertFalse(reader.read());
		assertFalse(reader.hasMoreBitsSet());
	}

}
