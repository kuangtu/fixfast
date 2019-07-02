package chap7;

import org.openfast.template.*;

import org.openfast.template.type.*;
import org.openfast.template.operator.*;
import org.openfast.*;
import org.openfast.ByteUtil;

import junit.framework.TestCase;

public class GetValueToEncodeTest extends TestCase {
	
	
    public void testNoneEncodeMand() {
        Scalar field = new Scalar("none", Type.U32, Operator.NONE,
                ScalarValue.UNDEFINED, false);

        OperatorCodec none = Operator.NONE.getCodec(Type.U32);

        assertEquals(new IntegerValue(1), none.getValueToEncode(
                new IntegerValue(1), new IntegerValue(2), field));

        assertEquals(new IntegerValue(1), none.getValueToEncode(
                new IntegerValue(1), ScalarValue.NULL, field));

        assertEquals(new IntegerValue(1), none.getValueToEncode(
                new IntegerValue(1), ScalarValue.UNDEFINED, field));
    }
    
    public void testNoneOptNULL() {
        Scalar field = new Scalar("none", Type.U32, Operator.NONE,
                new IntegerValue(1), true);
        OperatorCodec none = Operator.NONE.getCodec(Type.U32);
        assertEquals(ScalarValue.NULL,
                none.getValueToEncode(null, new IntegerValue(1), field));
    }
    
    public void testConstantMand() {
        Scalar field = new Scalar("constant", Type.U32, Operator.CONSTANT,
                new IntegerValue(1), false);
        BitVectorBuilder presenceMapBuilder = new BitVectorBuilder(1);
        OperatorCodec constant = Operator.CONSTANT.getCodec(Type.U32);
        assertEquals(null, constant.getValueToEncode(new IntegerValue(1),
                new IntegerValue(2), field, presenceMapBuilder));
        assertEquals("10000000", ByteUtil.convertByteArrayToBitString(presenceMapBuilder.getBitVector().getBytes()));
        
    }
    
    public void testConstantOpt() {
        Scalar field = new Scalar("constant", Type.U32, Operator.CONSTANT,
                new IntegerValue(1), true);
        BitVectorBuilder presenceMapBuilder = new BitVectorBuilder(7);
        OperatorCodec constant = Operator.CONSTANT.getCodec(Type.U32);
        assertEquals(null, constant.getValueToEncode(new IntegerValue(1),
                new IntegerValue(2), field, presenceMapBuilder));
        assertEquals("11000000", ByteUtil.convertByteArrayToBitString(presenceMapBuilder.getBitVector().getBytes()));
        assertEquals(1, presenceMapBuilder.getIndex());
    }
    
    public void testConstantOptNULL() {
        Scalar field = new Scalar("constant", Type.U32, Operator.CONSTANT,
                new IntegerValue(1), true);
        BitVectorBuilder presenceMapBuilder = new BitVectorBuilder(7);
        OperatorCodec constant = Operator.CONSTANT.getCodec(Type.U32);
        assertEquals(null, constant.getValueToEncode(null, new IntegerValue(2),
                field, presenceMapBuilder));
        assertEquals("10000000", ByteUtil.convertByteArrayToBitString(presenceMapBuilder.getBitVector().getBytes()));
        assertEquals(1, presenceMapBuilder.getIndex());
    }
    
	public void testDefaultMand()
	{
		Scalar field = new Scalar("default", Type.U32, Operator.DEFAULT, new IntegerValue(1), false);
		OperatorCodec def = Operator.DEFAULT.getCodec(Type.U32);
		assertEquals(new IntegerValue(4), def.getValueToEncode(new IntegerValue(4), new IntegerValue(2), field));		
	}






}
