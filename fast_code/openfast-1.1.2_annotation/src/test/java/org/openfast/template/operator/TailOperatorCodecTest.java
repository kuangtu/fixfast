package org.openfast.template.operator;

import org.openfast.ByteUtil;
import org.openfast.ByteVectorValue;
import org.openfast.ScalarValue;
import org.openfast.StringValue;
import org.openfast.error.FastConstants;
import org.openfast.error.FastException;
import org.openfast.template.Scalar;
import org.openfast.template.type.Type;
import org.openfast.test.OpenFastTestCase;

public class TailOperatorCodecTest extends OpenFastTestCase {
    private static final OperatorCodec TAIL_CODEC = Operator.TAIL.getCodec(Type.ASCII);
    private static final Scalar OPT_NO_DEFAULT = new Scalar("noDefault", Type.ASCII, Operator.TAIL, ScalarValue.UNDEFINED, true);
    private static final Scalar OPT_DEFAULT = new Scalar("noDefault", Type.ASCII, Operator.TAIL, new StringValue("abc"), true);
    private static final Scalar MAND_NO_DEFAULT = new Scalar("noDefault", Type.ASCII, Operator.TAIL, ScalarValue.UNDEFINED, false);
    private static final Scalar MAND_DEFAULT = new Scalar("noDefault", Type.ASCII, Operator.TAIL, new StringValue("abc"), false);

    public void testGetValueToEncodeAllCasesForOptionalNoInitialValue() {
        // VALUE PREVIOUS
        assertEquals(null, TAIL_CODEC.getValueToEncode(null, UNDEF, OPT_NO_DEFAULT));
        assertEquals(string("abcd"), TAIL_CODEC.getValueToEncode(string("abcd"), UNDEF, OPT_NO_DEFAULT));
        assertEquals(string("e"), TAIL_CODEC.getValueToEncode(string("abce"), string("abcd"), OPT_NO_DEFAULT));
        assertEquals(null, TAIL_CODEC.getValueToEncode(string("abce"), string("abce"), OPT_NO_DEFAULT));
        assertEquals(string("abcef"), TAIL_CODEC.getValueToEncode(string("abcef"), string("abce"), OPT_NO_DEFAULT));
        assertEquals(NULL, TAIL_CODEC.getValueToEncode(null, string("abcef"), OPT_NO_DEFAULT));
        assertEquals(null, TAIL_CODEC.getValueToEncode(null, null, OPT_NO_DEFAULT));
        assertEquals(string("z"), TAIL_CODEC.getValueToEncode(string("z"), null, OPT_NO_DEFAULT));
    }

    public void testGetValueToEncodeAllCasesForMandatoryNoInitialValue() {
        // VALUE PREVIOUS
        assertEquals(string("abcd"), TAIL_CODEC.getValueToEncode(string("abcd"), UNDEF, MAND_NO_DEFAULT));
        assertEquals(string("e"), TAIL_CODEC.getValueToEncode(string("abce"), string("abcd"), MAND_NO_DEFAULT));
        assertEquals(string("abcef"), TAIL_CODEC.getValueToEncode(string("abcef"), string("abce"), MAND_NO_DEFAULT));
        assertEquals(string("z"), TAIL_CODEC.getValueToEncode(string("z"), null, MAND_NO_DEFAULT));
    }

    public void testGetValueToEncodeAllCasesForOptionalDefaultABC() {
        // VALUE PREVIOUS
        assertEquals(null, TAIL_CODEC.getValueToEncode(string("abc"), UNDEF, OPT_DEFAULT));
        assertEquals(NULL, TAIL_CODEC.getValueToEncode(null, UNDEF, OPT_DEFAULT));
        assertEquals(string("abcd"), TAIL_CODEC.getValueToEncode(string("abcd"), null, OPT_DEFAULT));
        assertEquals(string("e"), TAIL_CODEC.getValueToEncode(string("abce"), string("abcd"), OPT_DEFAULT));
        assertEquals(null, TAIL_CODEC.getValueToEncode(string("abce"), string("abce"), OPT_DEFAULT));
        assertEquals(string("abcef"), TAIL_CODEC.getValueToEncode(string("abcef"), string("abce"), OPT_DEFAULT));
        assertEquals(NULL, TAIL_CODEC.getValueToEncode(null, string("abcef"), OPT_DEFAULT));
        assertEquals(null, TAIL_CODEC.getValueToEncode(null, null, OPT_DEFAULT));
        assertEquals(string("z"), TAIL_CODEC.getValueToEncode(string("z"), null, OPT_DEFAULT));
    }

    public void testGetValueToEncodeAllCasesForMandatoryDefaultABC() {
        // VALUE PREVIOUS
        assertEquals(null, TAIL_CODEC.getValueToEncode(string("abc"), UNDEF, MAND_DEFAULT));
        assertEquals(string("d"), TAIL_CODEC.getValueToEncode(string("abd"), UNDEF, MAND_DEFAULT));
        assertEquals(string("e"), TAIL_CODEC.getValueToEncode(string("abce"), string("abcd"), MAND_DEFAULT));
        assertEquals(string("abcef"), TAIL_CODEC.getValueToEncode(string("abcef"), string("abce"), MAND_DEFAULT));
        assertEquals(string("z"), TAIL_CODEC.getValueToEncode(string("z"), null, MAND_DEFAULT));
    }

    public void testDecodeEmptyForOptionalNoDefault() {
        assertEquals(null, TAIL_CODEC.decodeEmptyValue(UNDEF, OPT_NO_DEFAULT));
        assertEquals(null, TAIL_CODEC.decodeEmptyValue(null, OPT_NO_DEFAULT));
        assertEquals(string("abcd"), TAIL_CODEC.decodeEmptyValue(string("abcd"), OPT_NO_DEFAULT));
    }

    public void testDecodeEmptyForMandatoryNoDefaultThrowsException() {
        try {
            TAIL_CODEC.decodeEmptyValue(UNDEF, MAND_NO_DEFAULT);
            fail();
        } catch (FastException e) {
            assertEquals(FastConstants.D6_MNDTRY_FIELD_NOT_PRESENT, e.getCode());
        }
        try {
            TAIL_CODEC.decodeEmptyValue(null, MAND_NO_DEFAULT);
            fail();
        } catch (FastException e) {
            assertEquals(FastConstants.D6_MNDTRY_FIELD_NOT_PRESENT, e.getCode());
        }
    }

    public void testDecodeEmptyForMandatoryNoDefault() {
        assertEquals(string("a"), TAIL_CODEC.decodeEmptyValue(string("a"), MAND_NO_DEFAULT));
    }

    public void testDecodeEmptyForOptionalDefaultABC() {
        assertEquals(string("abc"), TAIL_CODEC.decodeEmptyValue(UNDEF, OPT_DEFAULT));
        assertEquals(null, TAIL_CODEC.decodeEmptyValue(null, OPT_DEFAULT));
        assertEquals(string("abcd"), TAIL_CODEC.decodeEmptyValue(string("abcd"), OPT_DEFAULT));
    }

    public void testDecodeEmptyForMandatoryDefaultABC() {
        assertEquals(string("abc"), TAIL_CODEC.decodeEmptyValue(UNDEF, MAND_DEFAULT));
    }

    public void testDecodeForOptionalNoDefault() {
        assertEquals(string("abc"), TAIL_CODEC.decodeValue(string("abc"), UNDEF, OPT_NO_DEFAULT));
        assertEquals(string("abd"), TAIL_CODEC.decodeValue(string("d"), string("abc"), OPT_NO_DEFAULT));
        assertEquals(string("abcd"), TAIL_CODEC.decodeValue(string("abcd"), string("abc"), OPT_NO_DEFAULT));
        assertEquals(null, TAIL_CODEC.decodeValue(null, string("abc"), OPT_NO_DEFAULT));
    }

    public void testUnencodableValue() {
        try {
            TAIL_CODEC.getValueToEncode(new StringValue("a"), new StringValue("abce"), OPT_NO_DEFAULT);
            fail();
        } catch (FastException e) {
            assertEquals(FastConstants.D3_CANT_ENCODE_VALUE, e.getCode());
        }
    }

    public void testGetValueToEncodeForByteVector() throws Exception {
        Scalar byteVectorField = new Scalar("bv", Type.BYTE_VECTOR, Operator.TAIL, ScalarValue.UNDEFINED, true);
        ScalarValue priorValue = new ByteVectorValue(ByteUtil.convertBitStringToFastByteArray("10001000 01001000 10101010 11111111"));
        ScalarValue value = new ByteVectorValue(ByteUtil.convertBitStringToFastByteArray("10001000 01001000 10101010 01010101"));
        ScalarValue expected = new ByteVectorValue(ByteUtil.convertBitStringToFastByteArray("01010101"));
        assertEquals(expected, OperatorCodec.TAIL.getValueToEncode(value, priorValue, byteVectorField));
    }

    public void testGetValueToEncodeForUnicodeString() throws Exception {
        Scalar byteVectorField = new Scalar("str", Type.UNICODE, Operator.TAIL, ScalarValue.UNDEFINED, true);
        ScalarValue priorValue = new StringValue("abcde");
        ScalarValue value = new StringValue("abcce");
        ScalarValue expected = new StringValue("ce");
        assertEquals(expected, OperatorCodec.TAIL.getValueToEncode(value, priorValue, byteVectorField));
    }

    public void testGetValueToEncodeForAsciiString() throws Exception {
        Scalar byteVectorField = new Scalar("str", Type.ASCII, Operator.TAIL, ScalarValue.UNDEFINED, true);
        ScalarValue priorValue = new StringValue("abcde");
        ScalarValue value = new StringValue("abcce");
        ScalarValue expected = new StringValue("ce");
        assertEquals(expected, OperatorCodec.TAIL.getValueToEncode(value, priorValue, byteVectorField));
    }

    public void testGetValueToEncodeAsciiStringTooLong() {
        Scalar byteVectorField = new Scalar("str", Type.ASCII, Operator.TAIL, ScalarValue.UNDEFINED, true);
        ScalarValue priorValue = new StringValue("abcde");
        ScalarValue value = new StringValue("dbcdef");
        assertEquals(value, OperatorCodec.TAIL.getValueToEncode(value, priorValue, byteVectorField));
    }

    public void testGetValueToEncodeAsciiStringLengthMismatch() {
        Scalar byteVectorField = new Scalar("str", Type.ASCII, Operator.TAIL, ScalarValue.UNDEFINED, true);
        ScalarValue priorValue = new StringValue("abcde");
        ScalarValue value = new StringValue("abcdef");
        assertEquals(value, OperatorCodec.TAIL.getValueToEncode(value, priorValue, byteVectorField));
    }

    public void testGetValueToEncodeAsciiStringSameValue() {
        Scalar byteVectorField = new Scalar("str", Type.ASCII, Operator.TAIL, ScalarValue.UNDEFINED, true);
        ScalarValue priorValue = new StringValue("abcde");
        ScalarValue value = new StringValue("abcde");
        assertEquals(null, OperatorCodec.TAIL.getValueToEncode(value, priorValue, byteVectorField));
    }
}
