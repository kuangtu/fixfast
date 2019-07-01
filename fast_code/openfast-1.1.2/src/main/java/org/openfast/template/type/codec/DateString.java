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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import org.openfast.DateValue;
import org.openfast.Global;
import org.openfast.ScalarValue;
import org.openfast.StringValue;
import org.openfast.error.FastConstants;

public class DateString extends TypeCodec {
    private static final long serialVersionUID = 1L;
    private final DateFormat formatter;

    public DateString(String format) {
        formatter = new SimpleDateFormat(format);
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    public ScalarValue decode(InputStream in) {
        try {
            return new DateValue(formatter.parse(TypeCodec.ASCII.decode(in).toString()));
        } catch (ParseException e) {
            Global.handleError(FastConstants.PARSE_ERROR, "", e);
            return null;
        }
    }

    public byte[] encodeValue(ScalarValue value) {
        return TypeCodec.ASCII.encode(new StringValue(formatter.format(((DateValue) value).value)));
    }

    public boolean equals(Object obj) {
        return obj != null && obj.getClass() == getClass();
    }
}
