package org.openfast.session.template.exchange;

import org.openfast.Context;
import org.openfast.IntegerValue;
import org.openfast.Message;
import org.openfast.ScalarValue;
import org.openfast.codec.FastEncoder;
import org.openfast.session.SessionConstants;
import org.openfast.session.SessionControlProtocol_1_1;
import org.openfast.template.Field;
import org.openfast.template.Scalar;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.Type;
import org.openfast.test.OpenFastTestCase;

public class TemplateDefinitionTest extends OpenFastTestCase {
    private ScalarConverter converter;
    private ConversionContext conversionContext;
    private Context context;
    private FastEncoder encoder;

    protected void setUp() throws Exception {
        converter = new ScalarConverter();
        conversionContext = SessionControlProtocol_1_1.createInitialContext();
        context = new Context();
        SessionConstants.SCP_1_1.registerSessionTemplates(context.getTemplateRegistry());
        encoder = new FastEncoder(context);
    }

    public void testEncodeOperatorCodec() {
        Field field = new Scalar("const10", Type.I32, Operator.CONSTANT, new IntegerValue(10), false);
        Message msg = (Message) converter.convert(field, conversionContext);
        byte[] encoded = encoder.encode(msg);
        //   --PMAP-- ---TEMPLATE ID--- ---NS--- -----NAME--------------------------
        String expected = 
            "11101000 01111101 10001100 10000000 10000111 01100011 01101111 01101110 " +
        //   ----------------------------------- ---ID--- --OPT--- --PMAP-- --TID---
            "01110011 01110100 00110001 00110000 10000000 10000000 11000000 01111101 " +
        //   -------- -DEFAULT
            "10011001 10001011";
        assertEquals(expected, encoded);
    }
    
    public void testEncodeId() {
        Field field = new Scalar("a", Type.U32, Operator.NONE, ScalarValue.UNDEFINED, false);
        field.setId("1");
        Message msg = (Message) converter.convert(field, conversionContext);
        byte[] encoded = encoder.encode(msg);
        String expected = 
        //   --PMAP-- ---TEMPLATE ID--- ---NS--- -------NAME------ --------ID------- --OPT--- DFT VAL-
            "11100000 01111101 10001101 10000000 10000001 01100001 10000010 00110001 10000000 10000000";
        assertEquals(expected, encoded);
        
        assertEquals(field, converter.convert(msg, context.getTemplateRegistry(), conversionContext));
    }
}
