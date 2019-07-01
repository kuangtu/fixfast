package org.openfast.template.loader;

import org.openfast.QName;
import org.openfast.template.Sequence;
import org.openfast.test.OpenFastTestCase;
import org.w3c.dom.Element;

public class SequenceParserTest extends OpenFastTestCase {

	private SequenceParser parser;
	private ParsingContext context;

	public void setUp() {
		parser = new SequenceParser();
		context = ParsingContext.NULL;
	}
	
	public void testInheritDictionary() throws Exception {
		ParsingContext c = new ParsingContext(context);
		c.setDictionary("template");
		Element node = document("<sequence name=\"seq\"></sequence>").getDocumentElement();
		
		assertTrue(parser.canParse(node, c));
		Sequence sequence = (Sequence) parser.parse(node, c);
		assertEquals("template", sequence.getLength().getDictionary());
		
		node = document("<sequence name=\"seq\"><length name=\"explicitLength\"/></sequence>").getDocumentElement();
		sequence = (Sequence) parser.parse(node, c);
		assertEquals("template", sequence.getLength().getDictionary());
	}
	
	public void testInheritance() throws Exception {
		String ns = "http://openfast.org/test";
		String dictionary = "template";
		ParsingContext c = new ParsingContext(context);
		c.setDictionary(dictionary);
		c.setNamespace(ns);
		
		Element node = document("<sequence name=\"seq\"><length name=\"seqLen\"/></sequence>").getDocumentElement();
		
		assertTrue(parser.canParse(node, c));
		Sequence sequence = (Sequence) parser.parse(node, c);
		assertEquals(dictionary, sequence.getLength().getDictionary());
		assertEquals(ns, sequence.getLength().getQName().getNamespace());
		assertEquals(ns, sequence.getQName().getNamespace());
	}
	
	public void testOverride() throws Exception {
		ParsingContext c = new ParsingContext(context);
		c.setDictionary("template");
		c.setNamespace("http://openfast.org/test");
		
		Element node = document("<sequence name=\"seq\" ns=\"http://openfast.org/override\" dictionary=\"type\"><length name=\"seqLen\"/></sequence>").getDocumentElement();
		
		assertTrue(parser.canParse(node, c));
		Sequence sequence = (Sequence) parser.parse(node, c);
		assertEquals("type", sequence.getLength().getDictionary());
		assertEquals("http://openfast.org/override", sequence.getLength().getQName().getNamespace());
		assertEquals("http://openfast.org/override", sequence.getQName().getNamespace());
	}
	
	public void testSequenceWithFields() throws Exception {
		ParsingContext c = new ParsingContext(XMLMessageTemplateLoader.createInitialContext());
		c.setDictionary("template");
		c.setNamespace("http://openfast.org/test");
		
		Element node = document("<sequence name=\"seq\" ns=\"http://openfast.org/override\" dictionary=\"type\">" +
				"<length name=\"seqLen\"/>" +
				"<string name=\"value\"><copy/></string>" +
				"<uInt32 name=\"date\"><delta/></uInt32>" +
				"<typeRef name=\"Seq\" ns=\"org.openfast.override\"/>" +
			"</sequence>").getDocumentElement();
		
		assertTrue(parser.canParse(node, c));
		Sequence sequence = (Sequence) parser.parse(node, c);
		assertEquals("type", sequence.getLength().getDictionary());
		assertEquals("http://openfast.org/override", sequence.getLength().getQName().getNamespace());
		assertEquals("http://openfast.org/override", sequence.getQName().getNamespace());
		assertEquals(new QName("Seq", "org.openfast.override"), sequence.getTypeReference());
	}
}
