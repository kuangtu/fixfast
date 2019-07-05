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
package bookUtil;

import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;
import org.openfast.ByteUtil;
import org.openfast.ByteVectorValue;
import org.openfast.IntegerValue;
import org.openfast.ScalarValue;
import org.openfast.StringValue;
import org.openfast.template.TwinValue;
import org.openfast.test.OpenFastTestCase;

public class BookUtilTest extends OpenFastTestCase {
  
     public static TwinValue tv(int sub, String diff) {
        return new TwinValue(new IntegerValue(sub), new ByteVectorValue(diff.getBytes()));
    }
}
