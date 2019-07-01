package org.openfast.util;


public interface IntegerMap {

    void put(int key, Object value);

    Object get(int key);

    boolean containsKey(int key);

    Object remove(int key);
}
