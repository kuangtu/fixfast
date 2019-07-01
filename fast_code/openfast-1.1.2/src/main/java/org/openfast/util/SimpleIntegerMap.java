package org.openfast.util;



public class SimpleIntegerMap implements IntegerMap {
    private static final int DEFAULT_INC_SIZE = 32;
    private Object[] table;
    private int firstKey;
    private final int incSize;
    
    public SimpleIntegerMap() {
        this(DEFAULT_INC_SIZE);
    }
    
    public SimpleIntegerMap(int size) {
        this.incSize = size;
    }
    
    public void put(int key, Object object) {
        adjust(key);
        table[key - firstKey] = object;
    }

    private void adjust(int key) {
        if (table == null) {
            table = new Object[incSize];
            firstKey = key;
        } else if (firstKey > key) {
            Object[] originalTable = table;
            int diff = firstKey - key;
            table = new Object[originalTable.length + diff];
            System.arraycopy(originalTable, 0, table, diff, originalTable.length);
            firstKey = key;
        } else if (key >= firstKey + table.length) {
            Object[] originalTable = table;
            int diff = key - (firstKey + table.length);
            table = new Object[originalTable.length + diff + incSize];
            System.arraycopy(originalTable, 0, table, 0, originalTable.length);
        }
    }

    public Object get(int key) {
        if (undefined(key))
            return null;
        return table[key-firstKey];
    }

    private boolean undefined(int key) {
        return table == null || key < firstKey || key >= table.length + firstKey;
    }

    public boolean containsKey(int key) {
        if (undefined(key))
            return false;
        return table[key-firstKey] != null;
    }

    public Object remove(int key) {
        if (undefined(key))
            return null;
        Object removed = table[key-firstKey];
        table[key-firstKey] = null;
        return removed;
    }
}
