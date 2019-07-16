package org.openfast.util;

import junit.framework.TestCase;

public class KeyTest extends TestCase {

	public void testEqualsObject() {
		Key key = new Key("abc", new Integer(500));
		Key same = new Key("abc", new Integer(500));
		Key diff = new Key("abcd", new Integer(500));
		Key diff2 = new Key("abc", new Integer(501));
		
		assertEquals(same, key);
		assertEquals(key, same);
		assertEquals(key.hashCode(), same.hashCode());
		
		assertFalse(key.equals(diff));
		assertFalse(diff.equals(key));
		assertFalse(diff2.equals(key));
		assertFalse(key.equals(diff2));
		
		assertFalse(key.hashCode() == diff.hashCode());
		assertFalse(key.hashCode() == diff2.hashCode());
	}
	
	public void testNullValues() {
		try {
			new Key(null, new Integer(1));
			fail();
		} catch (NullPointerException e) {
		}
		
		try {
			new Key(new Integer(1), null);
			fail();
		} catch (NullPointerException e) {
		}
	}

}
