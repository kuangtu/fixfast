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

import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import org.openfast.DateValue;
import org.openfast.IntegerValue;
import org.openfast.ScalarValue;
import org.openfast.util.Util;

public class MillisecondsSinceMidnight extends TypeCodec {
    private static final long serialVersionUID = 1L;

    public ScalarValue decode(InputStream in) {
        int millisecondsSinceMidnight = TypeCodec.INTEGER.decode(in).toInt();
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        int hour = millisecondsSinceMidnight / 3600000;
        millisecondsSinceMidnight -= hour * 3600000;
        cal.set(Calendar.HOUR_OF_DAY, hour);
        int minute = millisecondsSinceMidnight / 60000;
        millisecondsSinceMidnight -= minute * 60000;
        cal.set(Calendar.MINUTE, minute);
        int second = millisecondsSinceMidnight / 1000;
        millisecondsSinceMidnight -= second * 1000;
        cal.set(Calendar.SECOND, second);
        int millisecond = millisecondsSinceMidnight;
        cal.set(Calendar.MILLISECOND, millisecond);
        return new DateValue(cal.getTime());
    }

    public byte[] encodeValue(ScalarValue value) {
        Date date = ((DateValue) value).value;
        int millisecondsSinceMidnight = Util.millisecondsSinceMidnight(date);
        return TypeCodec.INTEGER.encodeValue(new IntegerValue(millisecondsSinceMidnight));
    }

    public boolean equals(Object obj) {
        return obj != null && obj.getClass() == getClass();
    }
}
