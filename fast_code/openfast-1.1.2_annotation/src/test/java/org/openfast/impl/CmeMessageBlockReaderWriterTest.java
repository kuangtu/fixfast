/*
The contents of this file are subject to the Mozilla Public License
Version 1.1 (the "License"); you may not use this file except in
compliance with the License. You may obtain a copy of the License at
http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS"
basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
License for the specific language governing rights and limitations
under the License.

The Original Code is OpenFAST.

The Initial Developer of the Original Code is The LaSalle Technology
Group, LLC.  Portions created by The LaSalle Technology Group, LLC
are Copyright (C) The LaSalle Technology Group, LLC. All Rights Reserved.

Contributor(s): Jacob Northey <jacob@lasalletech.com>
                Craig Otis <cotis@lasalletech.com>
*/


package org.openfast.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import junit.framework.TestCase;

public class CmeMessageBlockReaderWriterTest extends TestCase {
	CmeMessageBlockReader reader;
	byte[] data;
	
	void writeAndThenRead(final long seqNum, final int subId) {
		CmeMessageBlockWriter.writeBlockLength(data, seqNum, subId);
		reader.readBlock(data);
	}

	public void setUp() {
		reader = new CmeMessageBlockReader();
		data = new byte[CmeConstants.PREAMBLE_LEN];
	}

	public void testReadWrite() {
		long seqNum;
		int subId;
		
		seqNum = 1294967294L;
		subId = 13;
		writeAndThenRead(seqNum, subId);
		assertEquals(seqNum, reader.getLastSeqNum());
		assertEquals(subId, reader.getLastSubId());

		seqNum = 2147483648L;
		subId = 111;
		writeAndThenRead(seqNum, subId);
		assertEquals(seqNum, reader.getLastSeqNum());
		assertEquals(subId, reader.getLastSubId());

		seqNum = 1073741824L;
		subId = 42;
		writeAndThenRead(seqNum, subId);
		assertEquals(seqNum, reader.getLastSeqNum());
		assertEquals(subId, reader.getLastSubId());
    }

	public void testReadWriteZeroes() {
		writeAndThenRead(0, 0);
		assertEquals(0, reader.getLastSeqNum());
		assertEquals(0, reader.getLastSubId());
    }
	
	public void testReadWriteMaxValidValues() {
		final long seqNum = CmeConstants.PREAMBLE_SEQ_NUM_MAX;
		final int subId = CmeConstants.PREAMBLE_SUB_ID_MAX;
		writeAndThenRead(seqNum, subId);
		assertTrue(reader.getLastSeqNum() == seqNum);
		assertTrue(reader.getLastSubId() == subId);
    }
	
	public void testReadWriteOverflow() {
		final long seqNum = CmeConstants.PREAMBLE_SEQ_NUM_MAX + 1;
		final int subId = CmeConstants.PREAMBLE_SUB_ID_MAX + 1;
		writeAndThenRead(seqNum, subId);
		assertTrue(reader.getLastSeqNum() != seqNum);
		assertTrue(reader.getLastSubId() != subId);
    }
}

