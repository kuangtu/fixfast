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
package org.openfast.template;

import org.openfast.error.FastConstants;
import org.openfast.error.FastException;
import org.openfast.template.type.Type;
import org.openfast.test.OpenFastTestCase;

public class TypeTest extends OpenFastTestCase {
    public void testGetType() {
        assertEquals(Type.U32, Type.getType("uInt32"));
        try {
            Type.getType("u32");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(
                    "The type named u32 does not exist.  Existing types are {uInt8,uInt16,uInt32,uInt64,int8,int16,int32,int64,string,ascii,unicode,byteVector,decimal}",
                    e.getMessage());
        }
    }

    public void testIncompatibleDefaultValue() {
        try {
            template("<template>" + "  <decimal><copy value=\"10a\"/></decimal>" + "</template>");
            fail();
        } catch (FastException e) {
            assertEquals(FastConstants.S3_INITIAL_VALUE_INCOMP, e.getCode());
            assertEquals("The value \"10a\" is not compatible with type decimal", e.getMessage());
        }
    }
}
