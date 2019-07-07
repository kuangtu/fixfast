package chap11;

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

public class DictTest extends OpenFastTestCase {

	public void testGlobalDict() {
		String templateXml = "<templates>" 
				+ "<template>" 
				+ " "
				+ " <uInt32 name=\"price1\"><delta value = \"10\" dictionary = \"global\" key=\"price\"/></uInt32>"
				+ "  <string name=\"security1\"><copy dictionary = \"global\" key=\"security1\"/></string>"
				+ "</template>"
				+ "<template>"
				+ " "
				+ " <uInt32 name=\"price2\"><delta value = \"5\" dictionary = \"global\" key=\"price\"/></uInt32>"
				+ "  <string name=\"security2\"><copy dictionary = \"global\" key=\"security2\"/></string>"
				+ "</template>"
				+ "</templates>";

		MessageTemplateLoader loader = new XMLMessageTemplateLoader();
		MessageTemplate[] templates = loader.load(stream(templateXml));
		MessageTemplate t0 = templates[0];
		MessageTemplate t1 = templates[1];

		Context context = new Context();
		context.registerTemplate(10, t0);
		context.registerTemplate(11, t1);

		FastEncoder encoder = new FastEncoder(context);
		assertEquals(ScalarValue.UNDEFINED, context.lookup("global", t0, new QName("price")));

		Message message1 = new Message(t0);
		message1.setInteger(1, 10);
		message1.setString(2, "600000");
		encoder.encode(message1);
		assertEquals(10, context.lookup("global", t0, new QName("price")).toInt());

		Message message2 = new Message(t1);
		message2.setInteger(1, 15);
		message2.setString(2, "600001");
		encoder.encode(message2);
		assertEquals(15, context.lookup("global", t0, new QName("price")).toInt());
	}

	public void testTemplateTempDict() {
		String templateXml = "<templates>"
				+ "<template>"
				+ " "
				+ " <uInt32 name=\"price1\"><copy dictionary = \"global\" key=\"price\"/></uInt32>"
				+ "  <string name=\"security1\"><copy dictionary = \"global\" key=\"security\"/></string>"
				+ "</template>" 
				+ "<template>"
				+ " "
				+ " <uInt32 name=\"price2\"><copy dictionary = \"template\" key=\"price\"/></uInt32>"
				+ "  <string name=\"security2\"><copy dictionary = \"global\" key=\"security\"/></string>"
				+ "</template>"
				+ "</templates>";

		MessageTemplateLoader loader = new XMLMessageTemplateLoader();
		MessageTemplate[] templates = loader.load(stream(templateXml));

		MessageTemplate t0 = templates[0];
		MessageTemplate t1 = templates[1];

		Context context = new Context();
		context.registerTemplate(10, t0);
		context.registerTemplate(11, t1);

		FastEncoder encoder = new FastEncoder(context);

		assertEquals(ScalarValue.UNDEFINED, context.lookup("global", t0, new QName("price")));
		assertEquals(ScalarValue.UNDEFINED, context.lookup("template", t0, new QName("price")));

		Message message1 = new Message(t0);
		message1.setInteger(1, 10);
		message1.setString(2, "600000");
		encoder.encode(message1);
		assertEquals(10, context.lookup("global", t0, new QName("price")).toInt());

		Message message2 = new Message(t1);
		message2.setInteger(1, 15);
		message2.setString(2, "600001");
		encoder.encode(message2);
		assertEquals(10, context.lookup("global", t0, new QName("price")).toInt());
		assertEquals(15, context.lookup("template", t1, new QName("price")).toInt());
	}

	public void testDictRest() {
		byte[] encoding = null;

		MessageTemplate template = new MessageTemplate("",
				new Field[] { new Scalar("copy", Type.U32, Operator.COPY, new IntegerValue(1), false) });

		Context context = new Context();
		FastEncoder encoder = new FastEncoder(context);
		context.registerTemplate(10, template);

		Message message = new Message(template);
		ScalarValue value = context.lookup("global", template, new QName("copy"));
		assertEquals(ScalarValue.UNDEFINED, value);
		message.setFieldValue(1, new IntegerValue(1));

		encoding = encoder.encode(message);
		value = context.lookup("global", template, new QName("copy"));
		assertEquals(new IntegerValue(1), value);
		// ---PMAP-- ---ID---
		assertEquals("11000000 10001010", ByteUtil.convertByteArrayToBitString(encoding));

		message.setFieldValue(1, new IntegerValue(2));
		encoding = encoder.encode(message);
		value = context.lookup("global", template, new QName("copy"));
		assertEquals(new IntegerValue(2), value);
		// ---PMAP-- ---F1---
		assertEquals("10100000 10000010", ByteUtil.convertByteArrayToBitString(encoding));

		// 对字典进行重置
		context.reset();
		value = context.lookup("global", template, new QName("copy"));
		assertEquals(ScalarValue.UNDEFINED, value);
		message.setFieldValue(1, new IntegerValue(2));
		encoding = encoder.encode(message);
		value = context.lookup("global", template, new QName("copy"));
		assertEquals(new IntegerValue(2), value);
		// ---PMAP-- ---TID-- ---F1---
		assertEquals("11100000 10001010 10000010", ByteUtil.convertByteArrayToBitString(encoding));

	}

}