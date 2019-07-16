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
package org.openfast;

import org.openfast.template.Sequence;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class SequenceValue implements FieldValue {
    private static final long serialVersionUID = 1L;
    private List elements = Collections.EMPTY_LIST;
    private Sequence sequence;

    public SequenceValue(Sequence sequence) {
        if (sequence == null) {
            throw new NullPointerException();
        }
        this.sequence = sequence;
    }

    public int getLength() {
        return elements.size();
    }

    public Iterator iterator() {
        return elements.iterator();
    }

    public void add(GroupValue value) {
        if (elements == Collections.EMPTY_LIST) {
            elements = new ArrayList();
        }
        elements.add(value);
    }

    public void add(FieldValue[] values) {
        if (elements == Collections.EMPTY_LIST) {
            elements = new ArrayList();
        }
        elements.add(new GroupValue(sequence.getGroup(), values));
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other == null) || !(other instanceof SequenceValue)) {
            return false;
        }
        return equals((SequenceValue) other);
    }

    private boolean equals(SequenceValue other) {
        if (getLength() != other.getLength()) {
            return false;
        }
        for (int i = 0; i < getLength(); i++) {
            if (!elements.get(i).equals(other.elements.get(i))) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        return elements.hashCode() * 37 + sequence.hashCode();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        Iterator iter = elements.iterator();
        builder.append("[ ");
        while (iter.hasNext()) {
            GroupValue value = (GroupValue) iter.next();
            builder.append('[').append(value).append("] ");
        }
        builder.append("]");
        return builder.toString();
    }

    public GroupValue get(int index) {
        return (GroupValue) elements.get(index);
    }

    public Sequence getSequence() {
        return sequence;
    }

    public GroupValue[] getValues() {
        return (GroupValue[]) this.elements.toArray(new GroupValue[elements.size()]);
    }

    public FieldValue copy() {
        SequenceValue value = new SequenceValue(this.sequence);
        for (int i = 0; i < elements.size(); i++) {
            value.add((GroupValue) ((GroupValue) elements.get(i)).copy());
        }
        return value;
    }
}
