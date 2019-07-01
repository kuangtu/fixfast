package org.openfast.template.loader;

import org.openfast.error.FastConstants;
import org.openfast.error.FastException;
import org.openfast.template.DynamicTemplateReference;
import org.openfast.template.Field;
import org.openfast.template.MessageTemplate;
import org.openfast.template.StaticTemplateReference;
import org.openfast.test.OpenFastTestCase;
import org.w3c.dom.Element;

public class TemplateRefParserTest extends OpenFastTestCase {
    private TemplateRefParser parser;
    private ParsingContext context;

    protected void setUp() throws Exception {
        parser = new TemplateRefParser();
        context = XMLMessageTemplateLoader.createInitialContext();
    }

    public void testParseDynamic() throws Exception {
        Element dynTempRefDef = document("<templateRef/>").getDocumentElement();
        assertEquals(DynamicTemplateReference.INSTANCE, parser.parse(dynTempRefDef, context));
    }

    public void testParseStatic() throws Exception {
        Element statTempRefDef = document("<templateRef name=\"base\"/>").getDocumentElement();
        MessageTemplate base = new MessageTemplate("base", new Field[] {});
        context.getTemplateRegistry().define(base);
        StaticTemplateReference statTempRef = (StaticTemplateReference) parser.parse(statTempRefDef, context);
        assertEquals(base, statTempRef.getTemplate());
    }

    public void testParseStaticWithUndefinedTemplate() throws Exception {
        Element statTempRefDef = document("<templateRef name=\"base\"/>").getDocumentElement();
        try {
            parser.parse(statTempRefDef, context);
        } catch (UnresolvedStaticTemplateReferenceException e) {
        }
    }
}
