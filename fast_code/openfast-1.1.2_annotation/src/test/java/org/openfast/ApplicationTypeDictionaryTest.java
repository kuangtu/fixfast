package org.openfast;

import org.openfast.test.ObjectMother;
import org.openfast.test.OpenFastTestCase;

public class ApplicationTypeDictionaryTest extends OpenFastTestCase {

	public void testLookup() {
		ObjectMother.allocationInstruction().setTypeReference(new QName("AllocationInstruction"));
		ObjectMother.allocations().setTypeReference(new QName("Allocation"));
		
		Context context = new Context();
		
		context.store("type", ObjectMother.allocationInstruction(), new QName("ID"), string("1234"));
		
		assertEquals(string("1234"), context.lookup("type", ObjectMother.allocationInstruction(), new QName("ID")));
		assertEquals(ScalarValue.UNDEFINED, context.lookup("type", ObjectMother.allocations().getGroup(), new QName("ID")));
	}

}
