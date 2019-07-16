package org.openfast.session;

import org.openfast.Message;
import org.openfast.SequenceValue;
import org.openfast.template.BasicTemplateRegistry;
import org.openfast.template.MessageTemplate;
import org.openfast.template.TemplateRegistry;
import org.openfast.test.ObjectMother;
import org.openfast.test.OpenFastTestCase;

public class SessionControlProtocol_1_1Test extends OpenFastTestCase {
    private SessionControlProtocol_1_1 SCP_1_1;

    protected void setUp() throws Exception {
        SCP_1_1 = (SessionControlProtocol_1_1) SessionConstants.SCP_1_1;
    }

    public void testSimpleCreateTemplateDefinitionMessage() {
        Message templateDef = SCP_1_1.createTemplateDefinitionMessage(ObjectMother.quoteTemplate());
        assertEquals("Quote", templateDef.getString("Name"));
        SequenceValue instructions = templateDef.getSequence("Instructions");
        assertEquals("bid", instructions.get(0).getGroup(0).getString("Name"));
        assertEquals("ask", templateDef.getSequence("Instructions").get(1).getGroup(0).getString("Name"));
    }

    public void testSimpleCreateTemplateFromMessage() {
        Message templateDef = SCP_1_1.createTemplateDefinitionMessage(ObjectMother.quoteTemplate());
        MessageTemplate template = SCP_1_1.createTemplateFromMessage(templateDef, TemplateRegistry.NULL);
        assertEquals(ObjectMother.quoteTemplate(), template);
    }

    public void testComplexCreateTemplateDefinitionMessage() {
        Message templateDef = SCP_1_1.createTemplateDefinitionMessage(ObjectMother.allocationInstruction());
        assertEquals("AllocInstrctn", templateDef.getString("Name"));
    }

    public void testComplexCreateTemplateFromMessage() {
        Message templateDef = SCP_1_1.createTemplateDefinitionMessage(ObjectMother.allocationInstruction());
        MessageTemplate template = SCP_1_1.createTemplateFromMessage(templateDef, TemplateRegistry.NULL);
        assertEquals(ObjectMother.allocationInstruction(), template);
    }

    public void testCreateTemplateDeclarationMessage() {
        Message templateDecl = SCP_1_1.createTemplateDeclarationMessage(ObjectMother.quoteTemplate(), 104);
        assertEquals("Quote", templateDecl.getString("Name"));
        assertEquals(104, templateDecl.getInt("TemplateId"));
    }

    public void testTemplateRef() {
        Message templateDef = SCP_1_1.createTemplateDefinitionMessage(ObjectMother.batchTemplate());
        assertEquals("Header", templateDef.getSequence("Instructions").get(0).getGroup(0).getString("Name"));
        assertEquals(SessionControlProtocol_1_1.DYN_TEMP_REF_MESSAGE, templateDef.getSequence("Instructions").get(1).getGroup(0)
                .getSequence("Instructions").get(0).getGroup(0));
        BasicTemplateRegistry registry = new BasicTemplateRegistry();
        registry.register(24, ObjectMother.headerTemplate());
        assertEquals(ObjectMother.batchTemplate(), SCP_1_1.createTemplateFromMessage(templateDef, registry));
    }
}
