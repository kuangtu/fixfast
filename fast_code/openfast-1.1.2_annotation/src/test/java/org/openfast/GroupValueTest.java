package org.openfast;

import junit.framework.TestCase;
import org.openfast.test.ObjectMother;

public class GroupValueTest extends TestCase {
    public void testEquals() {
        GroupValue v = ObjectMother.newAllocation(null, 10.0, 11.0);
        GroupValue v2 = ObjectMother.newAllocation(null, 10.0, 11.0);
        assertEquals(v, v2);
    }
    
    public void testIsDefinedOnInvalidField() {
        GroupValue v = ObjectMother.newAllocation(null, 20.1, 20.2);
        assertFalse(v.isDefined("undefinedField"));
    }
}
