package org.openfast.util;

import org.openfast.FieldValue;

public interface Cache {

    boolean containsValue(FieldValue value);

    int getIndex(FieldValue value);

    int store(FieldValue value);

    void store(int index, FieldValue value);

    FieldValue lookup(int index);
}
