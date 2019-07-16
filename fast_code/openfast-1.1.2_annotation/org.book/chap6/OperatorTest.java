package chap6;

import org.openfast.template.*;

import org.openfast.template.type.*;
import org.openfast.template.operator.*;
import org.openfast.*;

import junit.framework.TestCase;
public class OperatorTest  extends TestCase {
	
	
	public void testOperatorType() {
	    Scalar scalar = new Scalar("undefined", Type.U32, Operator.COPY, ScalarValue.UNDEFINED, true);
	    
	    Scalar scalar1 = new Scalar("assigned", Type.U32, Operator.COPY, new IntegerValue(10), true);
	    
	    Scalar scalar2 = new Scalar("null", Type.U32, Operator.COPY, ScalarValue.NULL, true);
	    
	}

}
