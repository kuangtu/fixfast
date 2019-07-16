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

package chap5;

import org.openfast.*;
import org.openfast.template.type.codec.*;
import junit.framework.TestCase;

public class TypeInstructionTest extends TestCase {

	public void testInteger() {
		ScalarValue value = new IntegerValue(1000);
		byte[] encoding = TypeCodec.NULLABLE_INTEGER.encode(value);
		String res = ByteUtil.convertByteArrayToBitString(encoding);
		assertEquals("00000111 11101001", res);
	}

	public void testNULLInteger() {
		ScalarValue value = ScalarValue.NULL;
		byte[] encoding = TypeCodec.NULLABLE_INTEGER.encode(value);
		String res = ByteUtil.convertByteArrayToBitString(encoding);
		assertEquals("10000000", res);
	}

	public void testNULLAscii() {
		ScalarValue value = ScalarValue.NULL;
		byte[] encoding = TypeCodec.NULLABLE_ASCII.encode(value);
		String res = ByteUtil.convertByteArrayToBitString(encoding);
		assertEquals("10000000", res);
	}

	public void testEmptyAscii() {
		ScalarValue value = new StringValue("");
		byte[] encoding = TypeCodec.NULLABLE_ASCII.encode(value);
		String res = ByteUtil.convertByteArrayToBitString(encoding);
		assertEquals("00000000 10000000", res);
	}

	public void testZeroAscii() {
		ScalarValue value = new StringValue("\0");
		byte[] encoding = TypeCodec.NULLABLE_ASCII.encode(value);
		String res = ByteUtil.convertByteArrayToBitString(encoding);
		assertEquals("00000000 00000000 10000000", res);
	}

	public void testVector() {
		ScalarValue value = new ByteVectorValue("abc".getBytes());
		byte[] encoding = TypeCodec.NULLABLE_BYTE_VECTOR_TYPE.encode(value);
		String res = ByteUtil.convertByteArrayToBitString(encoding);
		assertEquals("10000100 01100001 01100010 01100011", res);
	}

	public void testEmptyVector() {
		ScalarValue value = new ByteVectorValue("".getBytes());
		byte[] encoding = TypeCodec.NULLABLE_BYTE_VECTOR_TYPE.encode(value);
		String res = ByteUtil.convertByteArrayToBitString(encoding);
		assertEquals("10000001", res);
	}

	public void testNULLVector() {
		ScalarValue value = ScalarValue.NULL;
		byte[] encoding = TypeCodec.NULLABLE_BYTE_VECTOR_TYPE.encode(value);
		String res = ByteUtil.convertByteArrayToBitString(encoding);
		assertEquals("10000000", res);
	}
	public void testUnicodeStrEncodeDecode() {
		 ScalarValue value = new StringValue("交易所");
		        byte[] encoding = TypeCodec.NULLABLE_UNICODE.encode(value);
		        String res = ByteUtil.convertByteArrayToBitString(encoding);
		        assertEquals("10001010 11100100 10111010 10100100 11100110 10011000 "
		                + "10010011 11100110 10001001 10000000", res);
		    }


	public void testDecimal() {
		ScalarValue value = new DecimalValue(1024000);
		byte[] encoding = TypeCodec.NULLABLE_SF_SCALED_NUMBER.encode(value);
		String res = ByteUtil.convertByteArrayToBitString(encoding);
		assertEquals("10000100 00001000 10000000", res);
	}

	public void testNULLDecimal() {
		ScalarValue value = ScalarValue.NULL;
		byte[] encoding = TypeCodec.NULLABLE_SF_SCALED_NUMBER.encode(value);
		String res = ByteUtil.convertByteArrayToBitString(encoding);
		assertEquals("10000000", res);
	}

}
