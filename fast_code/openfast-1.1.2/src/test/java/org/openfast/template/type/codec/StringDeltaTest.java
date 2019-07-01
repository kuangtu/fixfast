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


package org.openfast.template.type.codec;

import org.openfast.BitVectorBuilder;
import org.openfast.Context;
import org.openfast.ScalarValue;
import org.openfast.template.Field;
import org.openfast.template.MessageTemplate;
import org.openfast.template.Scalar;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.Type;
import org.openfast.test.OpenFastTestCase;


public class StringDeltaTest extends OpenFastTestCase {
    public void testEncodeValue() {
        assertEncodeDecode(twin(i(1), string("A")), "10000001 11000001", TypeCodec.STRING_DELTA);
    }
    
    public void testNullValue() {
    	Scalar scalar = new Scalar("deltaString", Type.STRING, Operator.DELTA, ScalarValue.UNDEFINED, true);
    	MessageTemplate template = new MessageTemplate("template", new Field[] { scalar });
		BitVectorBuilder bvBuilder = new BitVectorBuilder(7);
		assertEquals("10000000", scalar.encode(null, template, new Context(), bvBuilder));
		
//		assertEquals(null, scalar.decode(bitStream("10000000"), ScalarValue.UNDEFINED));
    }
}
