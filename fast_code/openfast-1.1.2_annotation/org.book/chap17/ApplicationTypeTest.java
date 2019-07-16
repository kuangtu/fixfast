package chap17;

import org.openfast.template.*;

import org.openfast.template.type.*;
import org.openfast.template.type.codec.TypeCodec;
import org.openfast.template.operator.*;

import java.io.InputStream;

import org.openfast.*;
import org.openfast.codec.FastDecoder;
import org.openfast.codec.FastEncoder;
import org.openfast.template.*;
import org.openfast.template.loader.*;
import bookUtil.BookUtilTest;
import bookUtil.BookUtilTest.*;
import org.openfast.test.*;

import junit.framework.TestCase;

public class ApplicationTypeTest extends OpenFastTestCase {


    public void testCopyEncode() {
        Scalar scalar = new Scalar("a", Type.U32, Operator.COPY,
                ScalarValue.UNDEFINED, false);
        Context context = new Context();
        byte[] encoding = scalar.encode(new IntegerValue(1), new Group("",
                new Field[] { scalar }, false), context,
                new BitVectorBuilder(1));
        // 整数1压缩之后,编码为:0x81=10000001
        TestUtil.assertBitVectorEquals("10000001", encoding);

        // 对于整数1,再次进行压缩,因为前值为1,不用进行传输,
        // 所以encoding得到字节数组为new byte[0] 数据长度为0.
        encoding = scalar.encode(new IntegerValue(1), new Group("",
                new Field[] { scalar }, false), context,
                new BitVectorBuilder(1));

        assertEquals("", ByteUtil.convertByteArrayToBitString(encoding));

    }

    public void testDeltaEncode() {
    	
        Context context = new Context();
        	
       	Scalar scalar = new Scalar("a", Type.U32, Operator.DELTA,  new IntegerValue(10), false);
            byte[] encoding = scalar.encode(new IntegerValue(12), new Group("", new Field[] { scalar }, false), context,
                    new BitVectorBuilder(1));
            //字段初始值为10，字段值为12，差值为2
            TestUtil.assertBitVectorEquals("10000010", encoding);
            
           //字段值为13，前值为12，差值为1
            encoding = scalar.encode(new IntegerValue(13), new Group("", new Field[] { scalar }, false), context,
                    new BitVectorBuilder(1));
            TestUtil.assertBitVectorEquals("10000001", encoding);
           
       }

    public void testGroupEncode() {
        Context context = new Context();
        MessageTemplate template = new MessageTemplate("", new Field[] {});
        
        // firstName基本类型字段操作符为COPY,PMAP中占位
        Scalar firstName = new Scalar("First Name", Type.U32, Operator.COPY,
                new IntegerValue(10), true);
        // lastName基本类型字段操作符为NONE,PMAP中不占位
        Scalar lastName = new Scalar("Last Name", Type.U32, Operator.NONE,
                new IntegerValue(5), false);
        // 基于基本类型创建group对象
        Group theGroup = new Group("guy", new Field[] { firstName, lastName },
                false);
        // 对于group进行压缩

        // 字段firstName值为1,字段lastName值为2
        byte[] actual = theGroup.encode(new GroupValue(theGroup,
                new FieldValue[] { new IntegerValue(1), new IntegerValue(2) }),
                template, context);
        // 压缩之后,该group包含了PMAP,实体值的第一位bit为1, firstName字段压缩值存在于数据流中
        // 字段lastName在PMAP中不占位
        String expected = "11000000 10000010 10000010";

        TestUtil.assertBitVectorEquals(expected, actual);

    }

    
    public void testGroupWithoutPresenceMap() {
        MessageTemplate template = template("<template>" + "  <group name=\"priceGroup\" presence=\"optional\">"
                + "    <decimal name=\"price\"><delta/></decimal>" + "  </group>" + "</template>");
        Context encodingContext = new Context();
        Context decodingContext = new Context();
       //注册消息模板id为1
        encodingContext.registerTemplate(1, template);
        decodingContext.registerTemplate(1, template);

        String encodedBits = "11100000 10000001 11111110 10111111";

        FastDecoder decoder = new FastDecoder(decodingContext, bitStream(encodedBits));
        Message message = decoder.readMessage();
        assertEquals(0.63, message.getGroup("priceGroup").getDouble("price"), 0.01);

        byte[] encoding = template.encode(message, encodingContext);
        assertEquals(encodedBits, encoding);
    }

    public void testGroupNULL() {
        MessageTemplate template = template("<template>" + "  <group name=\"priceGroup\" presence=\"optional\">"
                + "    <decimal name=\"price\"><delta/></decimal>" + "  </group>" + "</template>");
        Context encodingContext = new Context();
        Context decodingContext = new Context();
        //注册消息模板id为1
        encodingContext.registerTemplate(1, template);
        decodingContext.registerTemplate(1, template);
        
        String encodedBits = "11000000 10000001";
        
        //模板中group字段值为空
        Message message = new Message(template);
        message.setFieldValue(1, null);

        byte[] encoding = template.encode(message, encodingContext);
        assertEquals(encodedBits, encoding);    
        
    }


}