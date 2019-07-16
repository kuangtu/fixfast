package chap9;

import org.openfast.template.*;

import org.openfast.template.type.*;
import org.openfast.template.operator.*;
import org.openfast.*;
import org.openfast.ByteUtil;
import org.openfast.codec.FastEncoder;
import org.openfast.template.*;
import org.openfast.template.loader.*;
import bookUtil.BookUtilTest;
import bookUtil.BookUtilTest.*;
import org.openfast.test.*;

import junit.framework.TestCase;

public class TemplatesTest extends OpenFastTestCase {
	
	public void testTemplateDef() {
		MessageTemplate messageTemplate = new MessageTemplate("template",
				new Field[] { new Scalar("1", Type.U32, Operator.NONE, new IntegerValue(1), false),
						new Scalar("2", Type.U32, Operator.DEFAULT, new IntegerValue(1), false),
						new Scalar("3", Type.U32, Operator.COPY, new IntegerValue(1), true) });
		assertEquals(messageTemplate.getFieldCount(), 4);
		assertEquals(messageTemplate.getField(1).getName(), "1");
	}

	public void testLoadMessageTemplate() {
		String templateXml = "<templates>"
				+ "  <template name=\"t1\">" 
				+ "    <string name=\"securityid\"/>"
				+ "    <string name=\"exchange\"/>" + "  "
				+ "  </template>" 
				+ "  <template name=\"t2\">"
				+ "    <string name=\"securityid\"/>" 
				+ "    <uInt32 name=\"quantity\"/>"
				+ "    <decimal name=\"price\" />" 
				+ "  </template>" + 
				"</templates>";
		MessageTemplate[] templates = new XMLMessageTemplateLoader().load(stream(templateXml));

		// 验证模板t1的字段数目
		assertEquals(3, templates[0].getFieldCount());
		// 验证模板t2的字段数目
		assertEquals(4, templates[1].getFieldCount());
		// 验证模板t2的各字段类型
		assertScalarField(templates[1], 0, Type.U32, "templateId", Operator.COPY);
		assertScalarField(templates[1], 1, Type.ASCII, "securityid", Operator.NONE);
		assertScalarField(templates[1], 2, Type.U32, "quantity", Operator.NONE);
		assertScalarField(templates[1], 3, Type.DECIMAL, "price", Operator.NONE);

	}

	public void testStaticTemplateReference() {
		String templateXml = "<templates>" 
				+ "  <template name=\"t1\">" 
				+ "    <string name=\"securityid\"/>"
				+ "    <string name=\"exchange\"/>" 
				+ "  </template>" 
				+ "  <template name=\"t2\">"
				+ "    <uInt32 name=\"quantity\"/>" 
				+ "    <templateRef name=\"t1\"/>"
				+ "    <decimal name=\"price\" />" 
				+ "  </template>" 
				+ "</templates>";
		MessageTemplate[] templates = new XMLMessageTemplateLoader().load(stream(templateXml));

		// 验证模板t1的字段数目
		assertEquals(3, templates[0].getFieldCount());
		// 验证模板t2的字段数目
		assertEquals(5, templates[1].getFieldCount());
		// 验证模板t2的各字段类型
		assertScalarField(templates[1], 0, Type.U32, "templateId", Operator.COPY);
		assertScalarField(templates[1], 1, Type.U32, "quantity", Operator.NONE);
		// 字段2和3,引用了模板1
		assertScalarField(templates[1], 2, Type.ASCII, "securityid", Operator.NONE);
		assertScalarField(templates[1], 3, Type.ASCII, "exchange", Operator.NONE);
		assertScalarField(templates[1], 4, Type.DECIMAL, "price", Operator.NONE);
	}

	public void testDynamicTemplateReferenceField() {
		String templateXml = "<templates>" 
				+ "  <template name=\"t1\">" 
				+ "    <string name=\"securityid\"/>"
				+ "    <string name=\"exchange\"/>" 
				+ "  </template>"
				+ "  <template name=\"t2\">"
				+ "    <uInt32 name=\"quantity\"/>" 
				+ "    <templateRef/>" 
				+ "    <decimal name=\"price\"/>"
				+ "  </template>"
				+ "	</templates>";

		MessageTemplateLoader loader = new XMLMessageTemplateLoader();
		MessageTemplate[] templates = loader.load(stream(templateXml));

		assertEquals(4, templates[1].getFieldCount());
		assertScalarField(templates[1], 1, Type.U32, "quantity", Operator.NONE);
		assertScalarField(templates[1], 3, Type.DECIMAL, "price", Operator.NONE);
		// t2的第二个字段是动态模版引用
		assertTrue(templates[1].getField(2) instanceof DynamicTemplateReference);
	}

	public void testDynamicTemplateReference() throws Exception {
		String templateXml = "<templates>" 
				+ "  <template name=\"t1\">" 
				+ "    <string name=\"securityid\"/>"
				+ "    <string name=\"exchange\"/>"
				+ "  </template>" 
				+ "  <template name=\"t2\">"
				+ "    <uInt32 name=\"quantity\"/>" 
				+ "    <templateRef/>" 
				+ "    <decimal name=\"price\"/>"
				+ "  </template>"
				+ "	</templates>";
		MessageTemplateLoader loader = new XMLMessageTemplateLoader();
		MessageTemplate[] templates = loader.load(stream(templateXml));

		MessageTemplate template1 = templates[0];
		MessageTemplate template2 = templates[1];

		// 按照t2创建消息,其中的第一个字段值为15,
		// 第三个字段值为102.0
		Message msg2 = new Message(template2);
		msg2.setInteger(1, 15);
		msg2.setDecimal(3, 102.0);

		// 按照t1创建消息,其中第一个字段值为"IBM",
		// 第二个字段值为"NASDAQ"
		Message msg1 = new Message(template1);
		msg1.setString(1, "IBM");
		msg1.setString(2, "NYSE");
		// t2消息的第二个字段是基于第一个模板的消息
		msg2.setFieldValue(2, msg1);

		Context context = new Context();
		context.registerTemplate(1, template1);
		context.registerTemplate(2, template2);

		FastEncoder encoder = new FastEncoder(context);

		assertEquals("11000000 10000010 10001111 11000000 10000001 01001001 01000010 11001101 "
				+ "01001110 01011001 01010011 11000101 10000000 00000000 11100110", encoder.encode(msg2));
	}

}