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
package org.openfast.template.type;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.openfast.DateValue;
import org.openfast.ScalarValue;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.codec.TypeCodec;

public class DateType extends Type {
    private static final long serialVersionUID = 1L;
    private TypeCodec dateCodec;
    private DateFormat dateFormatter;

    public DateType(String dateFormat, TypeCodec dateCodec) {
        this(new SimpleDateFormat(dateFormat), dateCodec);
    }
    public DateType(DateFormat dateFormat, TypeCodec dateCodec) {
        super("date");
        this.dateFormatter = dateFormat;
        this.dateCodec = dateCodec;
    }
    public ScalarValue getDefaultValue() {
        return new DateValue(new Date(0));
    }
    public boolean isValueOf(ScalarValue previousValue) {
        return previousValue instanceof DateValue;
    }
    public TypeCodec getCodec(Operator operator, boolean optional) {
        return dateCodec;
    }
    public ScalarValue getValue(String value) {
        if (value == null)
            return ScalarValue.UNDEFINED;
        try {
            return new DateValue(dateFormatter.parse(value));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    public String serialize(ScalarValue value) {
        return dateFormatter.format(((DateValue) value).value);
    }
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dateCodec == null) ? 0 : dateCodec.hashCode());
        result = prime * result + ((dateFormatter == null) ? 0 : dateFormatter.hashCode());
        return result;
    }
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final DateType other = (DateType) obj;
        if (dateCodec == null) {
            if (other.dateCodec != null)
                return false;
        } else if (!dateCodec.equals(other.dateCodec))
            return false;
        if (dateFormatter == null) {
            if (other.dateFormatter != null)
                return false;
        } else if (!dateFormatter.equals(other.dateFormatter))
            return false;
        return true;
    }
}
