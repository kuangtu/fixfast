/*
The contents of this file are subject to the Mozilla Public License
Version 1.1 (the "License"); you may not use this file except in
compliance with the License. You may obtain a copy of the License at
http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS"
basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
License for the specific language governing rights and limitations
under the License.

The Original Code is OpenFAST.

The Initial Developer of the Original Code is The LaSalle Technology
Group, LLC.  Portions created by The LaSalle Technology Group, LLC
are Copyright (C) The LaSalle Technology Group, LLC. All Rights Reserved.

Contributor(s): Jacob Northey <jacob@lasalletech.com>
                Craig Otis <cotis@lasalletech.com>
*/


package org.openfast.template.loader;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.openfast.QName;
import org.openfast.ScalarValue;
import org.openfast.error.ErrorHandler;
import org.openfast.error.FastConstants;
import org.openfast.error.FastException;
import org.openfast.template.DynamicTemplateReference;
import org.openfast.template.Field;
import org.openfast.template.MessageTemplate;
import org.openfast.template.Scalar;
import org.openfast.template.Sequence;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.Type;
import org.openfast.template.type.codec.TypeCodec;
import org.openfast.test.OpenFastTestCase;
import org.w3c.dom.Element;


public class XMLMessageTemplateLoaderTest extends OpenFastTestCase {
    public void testLoadTemplateThatContainsDecimalWithTwinOperators() {
        String templateXml = "<templates xmlns=\"http://www.fixprotocol.org/ns/template-definition\"" +
            "	ns=\"http://www.fixprotocol.org/ns/templates/sample\">" +
            "	<template name=\"SampleTemplate\">" +
            "		<decimal name=\"bid\"><mantissa><delta /></mantissa><exponent><copy value=\"-2\" /></exponent></decimal>" +
            "		<decimal name=\"ask\"><mantissa><delta /></mantissa></decimal>" +
            "		<decimal name=\"high\"><exponent><copy/></exponent></decimal>" +
            "		<decimal name=\"low\"><mantissa><delta value=\"10\"/></mantissa><exponent><copy value=\"-2\" /></exponent></decimal>" +
            "		<decimal name=\"open\"><copy /></decimal>" +
            "		<decimal name=\"close\"><copy /></decimal>" + "	</template>" +
            "</templates>";
        MessageTemplateLoader loader = new XMLMessageTemplateLoader();
        MessageTemplate[] templates = loader.load(new ByteArrayInputStream(templateXml.getBytes()));
        MessageTemplate messageTemplate = templates[0];
        assertEquals("SampleTemplate", messageTemplate.getName());
        assertEquals(7, messageTemplate.getFieldCount());
        assertComposedScalarField(messageTemplate, 1, Type.DECIMAL, "bid", Operator.COPY, i(-2), Operator.DELTA, ScalarValue.UNDEFINED);
        assertComposedScalarField(messageTemplate, 2, Type.DECIMAL, "ask", Operator.NONE, ScalarValue.UNDEFINED, Operator.DELTA, ScalarValue.UNDEFINED);
        assertComposedScalarField(messageTemplate, 3, Type.DECIMAL, "high", Operator.COPY, ScalarValue.UNDEFINED, Operator.NONE, ScalarValue.UNDEFINED);
        assertComposedScalarField(messageTemplate, 4, Type.DECIMAL, "low", Operator.COPY, i(-2), Operator.DELTA, i(10));
        assertScalarField(messageTemplate, 5, Type.DECIMAL, "open", Operator.COPY);
        assertScalarField(messageTemplate, 6, Type.DECIMAL, "close", Operator.COPY);
    }

    public void testLoadTemplateThatContainsGroups() {
        String templateXml = "<templates xmlns=\"http://www.fixprotocol.org/ns/template-definition\"" +
            "	ns=\"http://www.fixprotocol.org/ns/templates/sample\">" +
            "	<template name=\"SampleTemplate\">" +
            "		<group name=\"guy\"><string name=\"First Name\"></string><string name=\"Last Name\"></string></group>" +
            "	</template>" + "</templates>";

        MessageTemplateLoader loader = new XMLMessageTemplateLoader();
        MessageTemplate[] templates = loader.load(new ByteArrayInputStream(
                    templateXml.getBytes()));
        MessageTemplate messageTemplate = templates[0];

        assertEquals("SampleTemplate", messageTemplate.getName());
        assertEquals(2, messageTemplate.getFieldCount());

        assertGroup(messageTemplate, 1, "guy");
    }
    
    public void testLoadTemplateWithKey() {
        String templateXml = "<templates xmlns=\"http://www.fixprotocol.org/ns/template-definition\"" +
            "	ns=\"http://www.fixprotocol.org/ns/templates/sample\">" +
            "	<template name=\"SampleTemplate\">" +
            "		<uInt32 name=\"value\"><copy key=\"integer\" /></uInt32>" +
            "	</template>" + "</templates>";

        MessageTemplateLoader loader = new XMLMessageTemplateLoader();
        MessageTemplate[] templates = loader.load(new ByteArrayInputStream(
                    templateXml.getBytes()));
        MessageTemplate messageTemplate = templates[0];

        Scalar scalar = messageTemplate.getScalar("value");
        assertEquals(new QName("integer"), scalar.getKey());
    }

    public void testLoadTemplateWithUnicodeString() {
        String templateXml = "<templates xmlns=\"http://www.fixprotocol.org/ns/template-definition\"" +
            "	ns=\"http://www.fixprotocol.org/ns/templates/sample\">" +
            "	<template name=\"SampleTemplate\">" +
            "		<string name=\"name\" charset=\"unicode\" presence=\"mandatory\"><copy /></string>" +
            "		<string name=\"id\" charset=\"unicode\" presence=\"optional\"><copy /></string>" +
            "		<string name=\"location\" charset=\"ascii\" presence=\"mandatory\"><copy /></string>" +
            "		<string name=\"id2\" charset=\"ascii\" presence=\"optional\"><copy /></string>" +
            "	</template>" + "</templates>";

        MessageTemplateLoader loader = new XMLMessageTemplateLoader();
        MessageTemplate[] templates = loader.load(new ByteArrayInputStream(
                    templateXml.getBytes()));
        MessageTemplate messageTemplate = templates[0];

        Scalar name = messageTemplate.getScalar("name");
        Scalar id = messageTemplate.getScalar("id");
        Scalar location = messageTemplate.getScalar("location");
        Scalar id2 = messageTemplate.getScalar("id2");
        
        assertFalse(name.isOptional());
        assertTrue(id.isOptional());
        assertFalse(location.isOptional());
        assertTrue(id2.isOptional());
        
        assertEquals(TypeCodec.UNICODE, name.getTypeCodec());
        assertEquals(TypeCodec.NULLABLE_UNICODE, id.getTypeCodec());
        assertEquals(TypeCodec.ASCII, location.getTypeCodec());
        assertEquals(TypeCodec.NULLABLE_ASCII, id2.getTypeCodec());
    }
    
    public void testLoadMdIncrementalRefreshTemplate() {
        InputStream templateStream = resource("FPL/mdIncrementalRefreshTemplate.xml");
        MessageTemplateLoader loader = new XMLMessageTemplateLoader();
        MessageTemplate messageTemplate = loader.load(templateStream)[0];

        assertEquals("MDIncrementalRefresh", messageTemplate.getTypeReference().getName());
        assertEquals("MDRefreshSample", messageTemplate.getName());
        assertEquals(10, messageTemplate.getFieldCount());

        /********************************** TEMPLATE FIELDS **********************************/
        int index = 0;
        assertScalarField(messageTemplate, index++, Type.U32, "templateId", Operator.COPY);
        assertScalarField(messageTemplate, index++, Type.ASCII, "8", Operator.CONSTANT);
        assertScalarField(messageTemplate, index++, Type.U32, "9", Operator.CONSTANT);
        assertScalarField(messageTemplate, index++, Type.ASCII, "35", Operator.CONSTANT);
        assertScalarField(messageTemplate, index++, Type.ASCII, "49", Operator.CONSTANT);
        assertScalarField(messageTemplate, index++, Type.U32, "34", Operator.INCREMENT);
        assertScalarField(messageTemplate, index++, Type.ASCII, "52", Operator.DELTA);
        assertScalarField(messageTemplate, index++, Type.U32, "75", Operator.COPY);

        /************************************* SEQUENCE **************************************/
        assertSequence(messageTemplate, index, 17);

        Sequence sequence = (Sequence) messageTemplate.getField(index++);
        assertEquals("MDEntries", sequence.getTypeReference().getName());
        assertSequenceLengthField(sequence, "268", Type.U32, Operator.NONE);

        /********************************** SEQUENCE FIELDS **********************************/
        int seqIndex = 0;
        assertScalarField(sequence, seqIndex++, Type.DECIMAL, "270",
            Operator.DELTA);
        assertScalarField(sequence, seqIndex++, Type.I32, "271",
            Operator.DELTA);
        assertScalarField(sequence, seqIndex++, Type.U32, "273",
            Operator.DELTA);
        assertOptionalScalarField(sequence, seqIndex++, Type.U32,
            "346", Operator.NONE);
        assertScalarField(sequence, seqIndex++, Type.U32, "1023",
            Operator.INCREMENT);
        assertScalarField(sequence, seqIndex++, Type.ASCII, "279",
            Operator.COPY);
        assertScalarField(sequence, seqIndex++, Type.ASCII, "269",
            Operator.COPY);
        assertScalarField(sequence, seqIndex++, Type.ASCII, "107",
            Operator.COPY);
        assertScalarField(sequence, seqIndex++, Type.ASCII, "48",
            Operator.DELTA);
        assertScalarField(sequence, seqIndex++, Type.ASCII, "276",
            Operator.COPY);
        assertScalarField(sequence, seqIndex++, Type.ASCII, "274",
            Operator.COPY);
        assertScalarField(sequence, seqIndex++, Type.DECIMAL, "451",
            Operator.COPY);
        assertScalarField(sequence, seqIndex++, Type.ASCII, "277",
            Operator.DEFAULT);
        assertOptionalScalarField(sequence, seqIndex++, Type.U32,
            "1020", Operator.NONE);
        assertScalarField(sequence, seqIndex++, Type.I32, "537",
            Operator.DEFAULT);
        assertScalarField(sequence, seqIndex++, Type.ASCII, "1024",
            Operator.DEFAULT);
        assertScalarField(sequence, seqIndex++, Type.ASCII, "336",
            Operator.DEFAULT);

        assertScalarField(messageTemplate, index++, Type.ASCII, "10",
            Operator.NONE);
    }
    
    public void testStaticTemplateReference() {
    	String templateXml = "<templates>" +
    			"  <template name=\"t1\">" +
    			"    <string name=\"string\"/>" +
    			"  </template>" +
    			"  <template name=\"t2\">" +
    			"    <uInt32 name=\"quantity\"/>" +
    			"    <templateRef name=\"t1\"/>" +
    			"    <decimal name=\"price\"/>" +
    			"  </template>" +
    			"</templates>";
    	MessageTemplate[] templates = new XMLMessageTemplateLoader().load(stream(templateXml));
    	assertEquals(4, templates[1].getFieldCount());
    	assertScalarField(templates[1], 1, Type.U32, "quantity", Operator.NONE);
    	assertScalarField(templates[1], 2, Type.ASCII, "string", Operator.NONE);
    	assertScalarField(templates[1], 3, Type.DECIMAL, "price", Operator.NONE);
    }
    
    public void testNonExistantTemplateReference() {
    	String template2Xml =
			"<template name=\"t2\">" +
			"  <uInt32 name=\"quantity\"/>" +
			"  <templateRef name=\"t1\"/>" +
			"  <decimal name=\"price\"/>" +
			"</template>";
    	try {
    		new XMLMessageTemplateLoader().load(stream(template2Xml));
    	} catch (FastException e) {
    		assertEquals(FastConstants.D8_TEMPLATE_NOT_EXIST, e.getCode());
    	}
    }
    
    public void testReferencedTemplateInOtherLoader() {
    	String template1Xml =
			"<template name=\"t1\">" +
			"  <string name=\"string\"/>" +
			"</template>";
    	String template2Xml =
			"<template name=\"t2\">" +
			"  <uInt32 name=\"quantity\"/>" +
			"  <templateRef name=\"t1\"/>" +
			"  <decimal name=\"price\"/>" +
			"</template>";

    	MessageTemplateLoader loader1 = new XMLMessageTemplateLoader();
    	MessageTemplateLoader loader2 = new XMLMessageTemplateLoader();
    	loader2.setTemplateRegistry(loader1.getTemplateRegistry());
    	
		loader1.load(stream(template1Xml));
		MessageTemplate[] templates = loader2.load(stream(template2Xml));
    	assertEquals(4, templates[0].getFieldCount());
    	assertScalarField(templates[0], 1, Type.U32, "quantity", Operator.NONE);
    	assertScalarField(templates[0], 2, Type.ASCII, "string", Operator.NONE);
    	assertScalarField(templates[0], 3, Type.DECIMAL, "price", Operator.NONE);
    }
    
    public void testTemplateReferencedFromPreviousLoad() {
    	String template1Xml =
			"<template name=\"t1\">" +
			"  <string name=\"string\"/>" +
			"</template>";
    	String template2Xml =
			"<template name=\"t2\">" +
			"  <uInt32 name=\"quantity\"/>" +
			"  <templateRef name=\"t1\"/>" +
			"  <decimal name=\"price\"/>" +
			"</template>";

    	MessageTemplateLoader loader = new XMLMessageTemplateLoader();
		loader.load(stream(template1Xml));
		MessageTemplate[] templates = loader.load(stream(template2Xml));
		
    	assertEquals(4, templates[0].getFieldCount());
    	assertScalarField(templates[0], 1, Type.U32, "quantity", Operator.NONE);
    	assertScalarField(templates[0], 2, Type.ASCII, "string", Operator.NONE);
    	assertScalarField(templates[0], 3, Type.DECIMAL, "price", Operator.NONE);
    }
    
    public void testDynamicTemplateReference() {
    	String template1Xml =
			"<template name=\"t1\">" +
			"  <string name=\"string\"/>" +
			"</template>";
    	String template2Xml =
			"<template name=\"t2\">" +
			"  <uInt32 name=\"quantity\"/>" +
			"  <templateRef/>" +
			"  <decimal name=\"price\"/>" +
			"</template>";

    	MessageTemplateLoader loader = new XMLMessageTemplateLoader();
		loader.load(stream(template1Xml));
		MessageTemplate[] templates = loader.load(stream(template2Xml));
		
    	assertEquals(4, templates[0].getFieldCount());
    	assertScalarField(templates[0], 1, Type.U32, "quantity", Operator.NONE);
    	assertScalarField(templates[0], 3, Type.DECIMAL, "price", Operator.NONE);
    	assertTrue(templates[0].getField(2) instanceof DynamicTemplateReference);
    }
    
    public void testByteVector() {
    	String templateXml = 
    		"<template name=\"bvt\">" +
    		"  <byteVector name=\"data\">" +
    		"    <length name=\"dataLength\"/>" +
    		"    <tail/>" +
    		"  </byteVector>" +
    		"</template>";
    	MessageTemplateLoader loader = new XMLMessageTemplateLoader();
    	MessageTemplate bvt = loader.load(stream(templateXml))[0];
    	
    	assertScalarField(bvt, 1, Type.BYTE_VECTOR, "data", Operator.TAIL);
    }
    
    public void testNullDocument() {
    	XMLMessageTemplateLoader loader = new XMLMessageTemplateLoader();
    	loader.setErrorHandler(ErrorHandler.NULL);
		assertEquals(0, loader.load(null).length);
    }
    
    public void testCustomFieldParser() {
    	String templateXml = 
    		"<template name=\"custom\">" +
    		"  <array name=\"intArr\" type=\"int\"></array>" +
    		"</template>";
    	XMLMessageTemplateLoader loader = new XMLMessageTemplateLoader();
    	try {
    		loader.load(stream(templateXml));
    	} catch (FastException e) {
    		assertEquals("No parser registered for array", e.getMessage());
    		assertEquals(FastConstants.PARSE_ERROR, e.getCode());
    	}
    	loader.addFieldParser(new FieldParser() {

			public boolean canParse(Element element, ParsingContext context) {
				return element.getNodeName().equals("array");
			}

			public Field parse(Element fieldNode, ParsingContext context) {
				return new Array(new QName(fieldNode.getAttribute("name"), ""), false);
			}});
    	
    	MessageTemplate template = loader.load(stream(templateXml))[0];
    	assertEquals(new Array(new QName("intArr", ""), false), template.getField(1));
    }
}
