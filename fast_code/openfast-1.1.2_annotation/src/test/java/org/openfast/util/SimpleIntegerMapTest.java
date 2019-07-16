package org.openfast.util;

import junit.framework.TestCase;

public class SimpleIntegerMapTest extends TestCase {
    public void testPut() {
        IntegerMap map = new SimpleIntegerMap();
        Object o = new Object();
        map.put(0, o);
        assertEquals(o, map.get(0));
    }
    
    public void testPutShortMiss() {
        IntegerMap map = new SimpleIntegerMap();
        Object o = new Object();
        map.put(5, o);
        Object o2 = new Object();
        map.put(1, o2);
        assertEquals(o, map.get(5));
        assertEquals(o2, map.get(1));
    }
    
    public void testPutLongMiss() {
        IntegerMap map = new SimpleIntegerMap();
        Object o = new Object();
        map.put(5, o);
        Object o2 = new Object();
        map.put(100, o2);
        assertEquals(o, map.get(5));
        assertEquals(o2, map.get(100));
    }
}
