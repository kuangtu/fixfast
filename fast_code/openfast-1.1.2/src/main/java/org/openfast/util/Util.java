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
package org.openfast.util;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;
import org.openfast.ByteUtil;
import org.openfast.ByteVectorValue;
import org.openfast.Global;
import org.openfast.IntegerValue;
import org.openfast.Node;
import org.openfast.QName;
import org.openfast.ScalarValue;
import org.openfast.SimpleNode;
import org.openfast.error.FastConstants;
import org.openfast.template.ComposedScalar;
import org.openfast.template.Scalar;
import org.openfast.template.TwinValue;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.DecimalConverter;
import org.openfast.template.type.Type;

public class Util {
    private static final TwinValue NO_DIFF = new TwinValue(new IntegerValue(0), ByteVectorValue.EMPTY_BYTES);

    public static boolean isBiggerThanInt(long value) {
        return (value > Integer.MAX_VALUE) || (value < Integer.MIN_VALUE);
    }

    public static ScalarValue getDifference(byte[] newValue, byte[] priorValue) {
        if ((priorValue == null) || (priorValue.length == 0)) {
            return new TwinValue(new IntegerValue(0), new ByteVectorValue(newValue));
        }
        if (Arrays.equals(priorValue, newValue)) {
            return NO_DIFF;
        }
        int appendIndex = 0;
        while ((appendIndex < priorValue.length) && (appendIndex < newValue.length)
                && (newValue[appendIndex] == priorValue[appendIndex]))
            appendIndex++;
        int prependIndex = 1;
        while ((prependIndex <= newValue.length) && (prependIndex <= priorValue.length)
                && (newValue[newValue.length - prependIndex] == priorValue[priorValue.length - prependIndex]))
            prependIndex++;
//        String prepend = newValue.substring(0, value.length() - prependIndex + 1);
        int prependLength = newValue.length - prependIndex + 1;
        int appendLength = newValue.length - appendIndex;
        if (prependLength < appendLength) {
            return new TwinValue(new IntegerValue(prependIndex - priorValue.length - 2), new ByteVectorValue(newValue, 0, prependLength));
        }
        return new TwinValue(new IntegerValue(priorValue.length - appendIndex), new ByteVectorValue(newValue, appendIndex, appendLength));
    }

    public static byte[] applyDifference(ScalarValue baseValue, TwinValue diffValue) {
        int subtraction = ((IntegerValue) diffValue.first).value;
        byte[] base = baseValue.getBytes();
        byte[] diff = diffValue.second.getBytes();
        if (subtraction < 0) {
            subtraction = (-1 * subtraction) - 1;
            return ByteUtil.combine(diff, 0, diff.length, base, subtraction, base.length);
        }
        return ByteUtil.combine(base, 0, base.length - subtraction, diff, 0, diff.length);
    }

    public static String collectionToString(Collection set) {
        return collectionToString(set, ",");
    }

    public static String collectionToString(Collection set, String sep) {
        StringBuffer buffer = new StringBuffer();
        Iterator iter = set.iterator();
        buffer.append("{");
        while (iter.hasNext()) {
            buffer.append(iter.next()).append(sep);
        }
        buffer.deleteCharAt(buffer.length() - sep.length());
        buffer.append("}");
        return buffer.toString();
    }

    public static int millisecondsSinceMidnight(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        cal.setTime(date);
        return cal.get(Calendar.HOUR_OF_DAY) * 3600000 + cal.get(Calendar.MINUTE) * 60000 + cal.get(Calendar.SECOND) * 1000
                + cal.get(Calendar.MILLISECOND);
    }

    /**
     * 
     * @param year
     *            the year yyyy
     * @param month
     *            1-based index of month
     * @param day
     *            day of month
     * @return the corresponding date
     */
    public static Date date(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        cal.set(year - 1900, month - 1, day);
        return cal.getTime();
    }

    public static int dateToInt(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        cal.setTime(date);
        return cal.get(Calendar.YEAR) * 10000 + (cal.get(Calendar.MONTH) + 1) * 100 + cal.get(Calendar.DATE);
    }

    public static int timeToInt(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        cal.setTime(date);
        return cal.get(Calendar.HOUR_OF_DAY) * 10000000 + cal.get(Calendar.MINUTE) * 100000 + cal.get(Calendar.SECOND) * 1000
                + cal.get(Calendar.MILLISECOND);
    }

    public static int timestampToInt(Date date) {
        return dateToInt(date) * 1000000000 + timeToInt(date);
    }

    public static Date toTimestamp(long value) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        int year = (int) (value / 10000000000000L);
        value %= 10000000000000L;
        int month = (int) (value / 100000000000L);
        value %= 100000000000L;
        int day = (int) (value / 1000000000);
        value %= 1000000000;
        int hour = (int) (value / 10000000);
        value %= 10000000;
        int min = (int) (value / 100000);
        value %= 100000;
        int sec = (int) (value / 1000);
        int ms = (int) (value % 1000);
        cal.set(year, month - 1, day, hour, min, sec);
        cal.set(Calendar.MILLISECOND, ms);
        return cal.getTime();
    }

    public static ComposedScalar composedDecimal(QName name, Operator exponentOp, ScalarValue exponentVal, Operator mantissaOp,
            ScalarValue mantissaVal, boolean optional) {
        Scalar exponentScalar = new Scalar(Global.createImplicitName(name), Type.I32, exponentOp, exponentVal, optional);
        Scalar mantissaScalar = new Scalar(Global.createImplicitName(name), Type.I64, mantissaOp, mantissaVal, false);
        return new ComposedScalar(name, Type.DECIMAL, new Scalar[] { exponentScalar, mantissaScalar }, optional,
                new DecimalConverter());
    }

    public static int toInt(String attribute) {
        try {
            return Integer.parseInt(attribute);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static Node createLength(QName name, String id) {
        SimpleNode lengthNode = new SimpleNode(FastConstants.LENGTH_FIELD);
        lengthNode.setAttribute(FastConstants.LENGTH_NAME_ATTR, name.getName());
        lengthNode.setAttribute(FastConstants.LENGTH_NS_ATTR, name.getNamespace());
        if (id != null)
            lengthNode.setAttribute(FastConstants.LENGTH_ID_ATTR, id);
        return lengthNode;
    }
}
