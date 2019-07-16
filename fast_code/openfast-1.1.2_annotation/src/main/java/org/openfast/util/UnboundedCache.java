package org.openfast.util;

import java.util.HashMap;
import java.util.Map;
import org.openfast.FieldValue;

public class UnboundedCache implements Cache {
    private int nextIndex = 1;
    private final Map indexToValueMap = new HashMap();
    private final Map valueToIndexMap = new HashMap();

    public int getIndex(FieldValue value) {
        return ((Integer)valueToIndexMap.get(value)).intValue();
    }

    public int store(FieldValue value) {
        Integer next = new Integer(nextIndex);
        indexToValueMap.put(next, value);
        valueToIndexMap.put(value, next);
        nextIndex++;
        return next.intValue();
    }

    public void store(int index, FieldValue value) {
        Integer indexVal = new Integer(index);
        indexToValueMap.put(indexVal, value);
        valueToIndexMap.put(value, indexVal);
    }

    public boolean containsValue(FieldValue value) {
        return valueToIndexMap.containsKey(value);
    }

    public FieldValue lookup(int index) {
        return (FieldValue) indexToValueMap.get(new Integer(index));
    }
}
