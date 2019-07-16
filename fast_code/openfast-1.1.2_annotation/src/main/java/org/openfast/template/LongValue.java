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

import org.openfast.NumericValue;

public class LongValue extends NumericValue {
    private static final long serialVersionUID = 1L;
    public final long value;

    /**
     * LongValue Constructor
     *
     * @param value
     *            The value of the LongValue as type long
     */
    public LongValue(long value) {
        this.value = value;
    }

    /**
     * Compares a LongValue object with another LongValue object
     *
     * @param obj
     *            the object to compare to
     * @return True if the two objects are the same, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if ((obj == null) || !(obj instanceof NumericValue)) {
            return false;
        }
        return equals((NumericValue) obj);
    }

    /**
     * Compares the value parameter of two LongValues
     *
     * @param otherValue
     *            The LongValue that is to be compared
     * @return Returns true if the two values are the same, false otherwise
     */
    private boolean equals(NumericValue otherValue) {
        return value == otherValue.toLong();
    }

    @Override
    public int hashCode() {
        return (int) value;
    }

    /**
     * Compares a string that is converted to an integer to the value of
     * LogValue
     *
     * @param defaultValue
     *            The string to be convereted to an integer and compared
     * @return Returns true if the string and the value are equal, false
     *         otherwise
     */
    @Override
    public boolean equalsValue(String defaultValue) {
        return Integer.parseInt(defaultValue) == value;
    }

    /**
     * Increment 'value' and create a new LongValue with the new value
     *
     * @return Returns a new LongValue with the value that is one more then
     *         before
     */
    @Override
    public NumericValue increment() {
        return new LongValue(value + 1);
    }

    /**
     * Decrement 'value' and create a new LongValue with the new value
     *
     * @return Returns a new LongValue with the value that is one less then
     *         before
     */
    @Override
    public NumericValue decrement() {
        return new LongValue(value - 1);
    }

    /**
     * @return Returns a string of the value of LongValue
     */
    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public Object toObject() {
        return value;
    }

    /**
     * Subtracts two NumericValues values and creates a new LongValue with the
     * new value
     *
     * @param subend
     *            The NumericValue to be subtracted
     * @return Returns a new LongValue with the value as the difference between
     *         the two NumericValues
     */
    @Override
    public NumericValue subtract(NumericValue subend) {
        return new LongValue(this.value - subend.toLong());
    }

    /**
     *
     * Adds two Numeric Values values and creates a new LongValue with the new
     * value
     *
     * @param addend
     *            The NumericValue to be added
     * @return Returns a new LongValue with the value as he addition between the
     *         two NumericValues
     *
     */
    @Override
    public NumericValue add(NumericValue addend) {
        return new LongValue(this.value + addend.toLong());
    }

    /**
     * @return Returns the value of LongValue as a string
     */
    @Override
    public String serialize() {
        return String.valueOf(value);
    }

    /**
     * Finds if the passed value is the same as the Value of LongValue
     *
     * @param value
     *            The integer to be compared
     * @return Returns true if the integer value passd is the same as the value
     *         of LongValue
     */
    @Override
    public boolean equals(int value) {
        return value == this.value;
    }

    /**
     * @return Returns the value of LongValue as a long
     */
    @Override
    public long toLong() {
        return value;
    }

    /**
     * @return Returns the value of LongValue as an integer
     */
    @Override
    public int toInt() {
        return (int) value;
    }
}
