package org.openfast.template.type.codec;

import org.openfast.BitVector;
import org.openfast.BitVectorBuilder;
import org.openfast.BitVectorReader;
import org.openfast.ByteUtil;
import org.openfast.Context;
import org.openfast.IntegerValue;
import org.openfast.QName;
import org.openfast.ScalarValue;
import org.openfast.template.ComposedScalar;
import org.openfast.template.Field;
import org.openfast.template.LongValue;
import org.openfast.template.MessageTemplate;
import org.openfast.template.operator.Operator;
import org.openfast.test.OpenFastTestCase;
import org.openfast.util.Util;

public class ComposedDecimalTest extends OpenFastTestCase {
    QName name = new QName("Value");

    MessageTemplate template = new MessageTemplate("", new Field[] {});

    public void testSimple() {
        String encoding = "11111110 00111001 01000101 10100011";

        ComposedScalar scalar = Util.composedDecimal(name, Operator.COPY, ScalarValue.UNDEFINED, Operator.DELTA,
                ScalarValue.UNDEFINED, true);

        assertEquals(encoding, scalar.encode(d(942755, -2), template, new Context(), new BitVectorBuilder(7)));
        assertEquals(d(942755, -2), scalar.decode(bitStream(encoding), template, new Context(), pmapReader("11000000")));
    }

    private BitVectorReader pmapReader(String bits) {
        return new BitVectorReader(new BitVector(ByteUtil.convertBitStringToFastByteArray(bits)));
    }

    public void testInitialValues() {
        Context context = new Context();
        ComposedScalar scalar = Util.composedDecimal(name, Operator.DEFAULT, i(-3), Operator.DELTA, ScalarValue.UNDEFINED, false);
        assertEquals("00000101 01100000 11110101", scalar.encode(d(94325, -3), template, context, new BitVectorBuilder(7)));
        assertEquals("11100111", scalar.encode(d(94300, -3), template, context, new BitVectorBuilder(7)));
        assertEquals("11100111", scalar.encode(d(94275, -3), template, context, new BitVectorBuilder(7)));
        assertEquals("00000000 11001011", scalar.encode(d(94350, -3), template, context, new BitVectorBuilder(7)));
        assertEquals("10011001", scalar.encode(d(94375, -3), template, context, new BitVectorBuilder(7)));
        assertEquals("10011001", scalar.encode(d(94400, -3), template, context, new BitVectorBuilder(7)));
        assertEquals("01111111 10000011", scalar.encode(d(94275, -3), template, context, new BitVectorBuilder(7)));
        assertEquals("11100111", scalar.encode(d(94250, -3), template, context, new BitVectorBuilder(7)));
        assertEquals("11100111", scalar.encode(d(94225, -3), template, context, new BitVectorBuilder(7)));
        assertEquals("00000000 11111101", scalar.encode(d(94350, -3), template, context, new BitVectorBuilder(7)));

        context = new Context();
        assertEquals(d(94325, -3), scalar.decode(bitStream("00000101 01100000 11110101"), template, context, pmapReader("10100000")));
        assertEquals(d(94300, -3), scalar.decode(bitStream("11100111"), template, context, pmapReader("10100000")));
        assertEquals(d(94275, -3), scalar.decode(bitStream("11100111"), template, context, pmapReader("10100000")));
        assertEquals(d(94350, -3), scalar.decode(bitStream("00000000 11001011"), template, context, pmapReader("10100000")));
        assertEquals(d(94375, -3), scalar.decode(bitStream("10011001"), template, context, pmapReader("10100000")));
        assertEquals(d(94400, -3), scalar.decode(bitStream("10011001"), template, context, pmapReader("10100000")));
        assertEquals(d(94275, -3), scalar.decode(bitStream("01111111 10000011"), template, context, pmapReader("10100000")));
        assertEquals(d(94250, -3), scalar.decode(bitStream("11100111"), template, context, pmapReader("10100000")));
        assertEquals(d(94225, -3), scalar.decode(bitStream("11100111"), template, context, pmapReader("10100000")));
        assertEquals(d(94350, -3), scalar.decode(bitStream("00000000 11111101"), template, context, pmapReader("10100000")));
    }

    public void testCopyExponentDeltaMantissa() {
        ComposedScalar decimal = Util.composedDecimal(name, Operator.COPY, ScalarValue.UNDEFINED, Operator.DELTA, new IntegerValue(1),
                false);
        Context context = new Context();
        BitVectorBuilder pmapBuilder = new BitVectorBuilder(7);
        assertEquals("11111110 00000001 00010110 10101100", decimal.encode(d(19245, -2), template, context, pmapBuilder));
        assertEquals("11000000", pmapBuilder.getBitVector().getBytes());
        pmapBuilder = new BitVectorBuilder(7);
        assertEquals("11111100", decimal.encode(d(19241, -2), template, context, pmapBuilder));
        assertEquals("10000000", pmapBuilder.getBitVector().getBytes());
    }

    public void testCopyExponentDefaultMantissa() {
        ComposedScalar decimal = Util.composedDecimal(name, Operator.COPY, ScalarValue.UNDEFINED, Operator.DEFAULT,
                new LongValue(1), false);
        Context context = new Context();
        BitVectorBuilder pmapBuilder = new BitVectorBuilder(7);
        assertEquals("11111110 00000001 00010110 10101101", decimal.encode(d(19245, -2), template, context, pmapBuilder));
        assertEquals("11100000", pmapBuilder.getBitVector().getBytes());

        pmapBuilder = new BitVectorBuilder(7);
        assertEquals("00000001 00010110 10101001", decimal.encode(d(19241, -2), template, context, pmapBuilder));
        assertEquals("10100000", pmapBuilder.getBitVector().getBytes());

        pmapBuilder = new BitVectorBuilder(7);
        assertEquals("10000000", decimal.encode(d(1, 0), template, context, pmapBuilder));
        assertEquals("11000000", pmapBuilder.getBitVector().getBytes());

        pmapBuilder = new BitVectorBuilder(7);
        assertEquals("", decimal.encode(d(1, 0), template, context, pmapBuilder));
        assertEquals("10000000", pmapBuilder.getBitVector().getBytes());
        assertEquals(2, pmapBuilder.getIndex());
    }

    public void testOptionalDefaultNullExponent() {
        ComposedScalar decimal = Util.composedDecimal(name, Operator.DEFAULT, ScalarValue.UNDEFINED, Operator.DELTA, new IntegerValue(
                12200), true);
        Context context = new Context();
        BitVectorBuilder pmapBuilder = new BitVectorBuilder(7);

        assertEquals("", decimal.encode(null, template, context, pmapBuilder));
        assertEquals("10000000", pmapBuilder.getBitVector().getBytes());
        assertEquals(1, pmapBuilder.getIndex()); // ONLY ONE PMAP BIT SHOULD
                                                    // BE WRITTEN

        pmapBuilder = new BitVectorBuilder(7);
        assertEquals("11111110 10000001", decimal.encode(d(12201, -2), template, context, pmapBuilder));
        assertEquals("11000000", pmapBuilder.getBitVector().getBytes());
        assertEquals(1, pmapBuilder.getIndex());
    }

    public void testOptionalConstantExponent() {
        ComposedScalar decimal = Util.composedDecimal(name, Operator.CONSTANT, new IntegerValue(-2), Operator.DEFAULT,
                new LongValue(100), true);
        Context context = new Context();
        BitVectorBuilder pmapBuilder = new BitVectorBuilder(7);

        assertEquals("", decimal.encode(d(100, -2), template, context, pmapBuilder));
        assertEquals("11000000", pmapBuilder.getBitVector().getBytes());
        assertEquals(2, pmapBuilder.getIndex());
    }

    public void testOptionalDeltaExponentCopyMantissa() {
        ComposedScalar decimal = Util.composedDecimal(name, Operator.DELTA, ScalarValue.UNDEFINED, Operator.COPY,
                ScalarValue.UNDEFINED, true);
        Context context = new Context();
        BitVectorBuilder pmapBuilder = new BitVectorBuilder(7);

        assertEquals("10000000", decimal.encode(null, template, context, pmapBuilder));
        assertEquals("10000000", pmapBuilder.getBitVector().getBytes());
        assertEquals(0, pmapBuilder.getIndex());

        pmapBuilder = new BitVectorBuilder(7);
        assertEquals("10000001 10000001", decimal.encode(d(1, 0), template, context, pmapBuilder));
        assertEquals("11000000", pmapBuilder.getBitVector().getBytes());
        assertEquals(1, pmapBuilder.getIndex());
    }
}
