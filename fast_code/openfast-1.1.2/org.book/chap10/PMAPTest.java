package chap10;

import org.openfast.template.*;

import org.openfast.template.type.*;
import org.openfast.template.operator.*;
import org.openfast.*;
import org.openfast.codec.FastEncoder;
import org.openfast.template.*;
import org.openfast.template.loader.*;
import bookUtil.BookUtilTest;
import bookUtil.BookUtilTest.*;
import org.openfast.test.*;

import junit.framework.TestCase;

public class PMAPTest extends OpenFastTestCase {
	public void testNonePmapMand() {
		Scalar field = new Scalar("none", Type.U32, Operator.NONE, new IntegerValue(1), false);
		MessageTemplate messageTemplate = new MessageTemplate("", new Field[] { field, });
		BitVectorBuilder pmap = new BitVectorBuilder(1);
		Context context = new Context();
		field.encode(new IntegerValue(10), messageTemplate, context, pmap);
		assertEquals("10000000", ByteUtil.convertByteArrayToBitString(pmap.getBitVector().getBytes()));
		assertEquals(0, pmap.getIndex());
	}

	public void testConstantPmapMand() {
		Scalar field = new Scalar("constant", Type.U32, Operator.CONSTANT, new IntegerValue(0), false);
		MessageTemplate messageTemplate = new MessageTemplate("", new Field[] { field, });
		BitVectorBuilder pmap = new BitVectorBuilder(7);
		Context context = new Context();
		field.encode(new IntegerValue(0), messageTemplate, context, pmap);
		assertEquals("10000000", ByteUtil.convertByteArrayToBitString(pmap.getBitVector().getTruncatedBytes()));
		assertEquals(0, pmap.getIndex());
	}

	public void testConstantPmapOpt() {
		Scalar field = new Scalar("constant", Type.U32, Operator.CONSTANT, new IntegerValue(0), true);
		MessageTemplate messageTemplate = new MessageTemplate("", new Field[] { field, });
		BitVectorBuilder pmap = new BitVectorBuilder(1);
		Context context = new Context();
		field.encode(new IntegerValue(0), messageTemplate, context, pmap);
		assertEquals("11000000", ByteUtil.convertByteArrayToBitString(pmap.getBitVector().getTruncatedBytes()));
		assertEquals(1, pmap.getIndex());
	}

	public void testConstantPmapOptNull() {
		Scalar field = new Scalar("constant", Type.U32, Operator.CONSTANT, new IntegerValue(0), true);
		MessageTemplate messageTemplate = new MessageTemplate("", new Field[] { field, });
		BitVectorBuilder pmap = new BitVectorBuilder(1);
		Context context = new Context();
		field.encode(null, messageTemplate, context, pmap);
		assertEquals("10000000", ByteUtil.convertByteArrayToBitString(pmap.getBitVector().getBytes()));
		assertEquals(1, pmap.getIndex());
	}

	public void testDefaultMand() {
		Scalar field = new Scalar("default", Type.U32, Operator.DEFAULT, new IntegerValue(0), false);
		MessageTemplate messageTemplate = new MessageTemplate("", new Field[] { field, });
		BitVectorBuilder pmap = new BitVectorBuilder(7);
		Context context = new Context();
		field.encode(new IntegerValue(11), messageTemplate, context, pmap);
		assertEquals("11000000", ByteUtil.convertByteArrayToBitString(pmap.getBitVector().getBytes()));
		assertEquals(1, pmap.getIndex());
	}

	public void testDefaultOpt() {
		Scalar field = new Scalar("default", Type.U32, Operator.DEFAULT, new IntegerValue(0), true);
		MessageTemplate messageTemplate = new MessageTemplate("", new Field[] { field, });
		BitVectorBuilder pmap = new BitVectorBuilder(7);
		Context context = new Context();
		field.encode(new IntegerValue(0), messageTemplate, context, pmap);
		assertEquals("10000000", ByteUtil.convertByteArrayToBitString(pmap.getBitVector().getBytes()));
		assertEquals(1, pmap.getIndex());
	}

	public void testCopyMand() {
		Scalar field = new Scalar("copy", Type.U32, Operator.COPY, new IntegerValue(0), false);
		MessageTemplate messageTemplate = new MessageTemplate("", new Field[] { field, });
		BitVectorBuilder pmap = new BitVectorBuilder(7);
		Context context = new Context();
		field.encode(new IntegerValue(10), messageTemplate, context, pmap);
		assertEquals("11000000", ByteUtil.convertByteArrayToBitString(pmap.getBitVector().getBytes()));
		assertEquals(1, pmap.getIndex());
	}

	public void testCopyPmapOpt() {
		Scalar field = new Scalar("copy", Type.U32, Operator.COPY, new IntegerValue(0), true);
		MessageTemplate messageTemplate = new MessageTemplate("", new Field[] { field, });
		BitVectorBuilder pmap = new BitVectorBuilder(7);
		Context context = new Context();
		field.encode(new IntegerValue(0), messageTemplate, context, pmap);
		assertEquals("10000000", ByteUtil.convertByteArrayToBitString(pmap.getBitVector().getBytes()));
		assertEquals(1, pmap.getIndex());
	}

	public void testCopyPmapOptNULL() {
		Scalar field = new Scalar("copy", Type.U32, Operator.COPY, new IntegerValue(0), true);
		MessageTemplate messageTemplate = new MessageTemplate("", new Field[] { field, });
		BitVectorBuilder pmap = new BitVectorBuilder(1);
		Context context = new Context();
		field.encode(null, messageTemplate, context, pmap);
		assertEquals("11000000", ByteUtil.convertByteArrayToBitString(pmap.getBitVector().getBytes()));
		assertEquals(1, pmap.getIndex());
	}

	public void testTailPmapMand() {
		Scalar field = new Scalar("tail", Type.ASCII, Operator.TAIL, new StringValue("ab"), false);
		MessageTemplate messageTemplate = new MessageTemplate("", new Field[] { field, });
		BitVectorBuilder pmap = new BitVectorBuilder(1);
		Context context = new Context();
		field.encode(new StringValue("abc"), messageTemplate, context, pmap);
		assertEquals("11000000", ByteUtil.convertByteArrayToBitString(pmap.getBitVector().getBytes()));
		assertEquals(1, pmap.getIndex());
	}

	public void testTailPmapOpt() {
		Scalar field = new Scalar("tail", Type.ASCII, Operator.TAIL, new StringValue("ab"), true);
		MessageTemplate messageTemplate = new MessageTemplate("", new Field[] { field, });

		BitVectorBuilder pmap = new BitVectorBuilder(1);
		Context context = new Context();
		field.encode(new StringValue("ab"), messageTemplate, context, pmap);
		assertEquals("10000000", ByteUtil.convertByteArrayToBitString(pmap.getBitVector().getBytes()));
		assertEquals(1, pmap.getIndex());
	}

	public void testDeltaPmapMand() {
		Scalar field = new Scalar("delta", Type.U32, Operator.DELTA, new IntegerValue(10), false);
		MessageTemplate messageTemplate = new MessageTemplate("", new Field[] { field, });
		BitVectorBuilder pmap = new BitVectorBuilder(7);
		Context context = new Context();
		field.encode(new IntegerValue(5), messageTemplate, context, pmap);
		assertEquals("10000000", ByteUtil.convertByteArrayToBitString(pmap.getBitVector().getBytes()));
		assertEquals(0, pmap.getIndex());
	}

	public void testDeltaNULLOpt() {
		Scalar field = new Scalar("delta", Type.U32, Operator.DELTA, new IntegerValue(10), true);
		MessageTemplate messageTemplate = new MessageTemplate("", new Field[] { field, });
		BitVectorBuilder pmap = new BitVectorBuilder(7);
		Context context = new Context();
		field.encode(null, messageTemplate, context, pmap);
		assertEquals("10000000", ByteUtil.convertByteArrayToBitString(pmap.getBitVector().getBytes()));
		assertEquals(0, pmap.getIndex());
	}

	public void testGroupWithPmap() {
		Scalar scalar1 = new Scalar("field1", Type.U32, Operator.COPY, ScalarValue.UNDEFINED, true);
		Scalar scalar2 = new Scalar("field2", Type.U32, Operator.NONE, ScalarValue.UNDEFINED, false);
		Scalar scalar3 = new Scalar("field3", Type.U32, Operator.DEFAULT, new IntegerValue(2), false);
		Group group1 = new Group("group1", new Field[] { scalar1, scalar2, scalar3 }, false);
		MessageTemplate template = new MessageTemplate("", new Field[] { group1 });
		Context context = new Context();
		byte[] encoding = group1.encode(
				new GroupValue(group1,
						new FieldValue[] { new IntegerValue(1), new IntegerValue(2), new IntegerValue(3) }),
				template, context);
		String res = ByteUtil.convertByteArrayToBitString(encoding);

		assertEquals("11100000 10000010 10000010 10000011", res);

	}

	public void testGroupNULL() {
		Scalar scalar1 = new Scalar("field1", Type.U32, Operator.COPY, ScalarValue.UNDEFINED, true);
		Scalar scalar2 = new Scalar("field2", Type.U32, Operator.COPY, ScalarValue.UNDEFINED, true);
		Scalar scalar3 = new Scalar("field3", Type.U32, Operator.DEFAULT, new IntegerValue(2), true);

		Group group1 = new Group("group1", new Field[] { scalar1, scalar2, scalar3 }, true);
		MessageTemplate template = new MessageTemplate("", new Field[] { group1 });
		Context context = new Context();
		byte[] encoding = group1.encode(new GroupValue(group1, new FieldValue[] { null, null, null }), template,
				context);
		String res = ByteUtil.convertByteArrayToBitString(encoding);

		assertEquals("10010000 10000000", res);
	}

	public void testGroupWithoutPMAP() {
		Scalar scalar1 = new Scalar("field1", Type.U32, Operator.NONE, ScalarValue.UNDEFINED, false);
		Scalar scalar2 = new Scalar("field2", Type.U32, Operator.NONE, ScalarValue.UNDEFINED, false);
		Scalar scalar3 = new Scalar("field3", Type.U32, Operator.DELTA, new IntegerValue(2), false);

		Group group1 = new Group("group1", new Field[] { scalar1, scalar2, scalar3 }, false);
		MessageTemplate template = new MessageTemplate("", new Field[] { group1 });
		Context context = new Context();
		byte[] encoding = group1.encode(
				new GroupValue(group1,
						new FieldValue[] { new IntegerValue(1), new IntegerValue(2), new IntegerValue(3) }),
				template, context);

		String res = ByteUtil.convertByteArrayToBitString(encoding);

		// 按照FAST协议规定,三个字段在PMAP中不占位,因此编码后得到
		// 的为各字段的数据
		assertEquals("10000001 10000010 10000001", res);
	}

	public void testSequenceMand() {
		Scalar field1 = new Scalar("field1", Type.U32, Operator.COPY, ScalarValue.UNDEFINED, true);
		Scalar field2 = new Scalar("field2", Type.U32, Operator.NONE, ScalarValue.UNDEFINED, false);
		Scalar field3 = new Scalar("field3", Type.U32, Operator.DEFAULT, new IntegerValue(2), false);

		Sequence sequence1 = new Sequence("seq1", new Field[] { field1, field2, field3 }, false);

		SequenceValue sequenceValue = new SequenceValue(sequence1);

		sequenceValue.add(new FieldValue[] { new IntegerValue(1), new IntegerValue(2), new IntegerValue(3) });
		sequenceValue.add(new FieldValue[] { new IntegerValue(1), new IntegerValue(4), new IntegerValue(2) });
		MessageTemplate template = new MessageTemplate("", new Field[] { sequence1 });
		Context context = new Context();
		byte[] encoding = sequence1.encode(sequenceValue, template, context, new BitVectorBuilder(7));
		String res = ByteUtil.convertByteArrayToBitString(encoding);
		
		assertEquals("10000010 11100000 10000010 10000010 10000011 10000000 10000100", res);
	}	

	public void testSequenceNULL() {
		Scalar scalar1 = new Scalar("1", Type.U32, Operator.COPY, ScalarValue.UNDEFINED, true);
		Scalar scalar2 = new Scalar("2", Type.U32, Operator.NONE, ScalarValue.UNDEFINED, false);
		Scalar scalar3 = new Scalar("3", Type.U32, Operator.DEFAULT, new IntegerValue(2), false);

		Sequence sequence1 = new Sequence("seq1", new Field[] { scalar1, scalar2, scalar3 }, true);
		MessageTemplate template = new MessageTemplate("", new Field[] { sequence1 });
		Context context = new Context();
		byte[] encoding = sequence1.encode(null, template, context, new BitVectorBuilder(1));
		String res = ByteUtil.convertByteArrayToBitString(encoding);

		assertEquals("10000000", res);
	}

}