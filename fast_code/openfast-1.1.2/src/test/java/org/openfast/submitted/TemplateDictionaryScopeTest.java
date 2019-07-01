package org.openfast.submitted;

import java.io.InputStream;
import org.openfast.Context;
import org.openfast.Dictionary;
import org.openfast.Message;
import org.openfast.ScalarValue;
import org.openfast.codec.FastDecoder;
import org.openfast.codec.FastEncoder;
import org.openfast.template.Field;
import org.openfast.template.MessageTemplate;
import org.openfast.template.Scalar;
import org.openfast.template.StaticTemplateReference;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.Type;
import org.openfast.test.OpenFastTestCase;

public class TemplateDictionaryScopeTest extends OpenFastTestCase {
    private MessageTemplate nameTemplate;
    private MessageTemplate startTemplate;
    private MessageTemplate definitionTemplate;
    private Context context;

    protected void setUp() throws Exception {
        Scalar ns = new Scalar("Ns", Type.STRING, Operator.COPY, ScalarValue.UNDEFINED, false);
        ns.setDictionary(Dictionary.TEMPLATE);
        Scalar name = new Scalar("Name", Type.STRING, Operator.COPY, ScalarValue.UNDEFINED, false);
        name.setDictionary(Dictionary.TEMPLATE);
        nameTemplate = new MessageTemplate("Name", new Field[] {
                ns,
                name
        });
        startTemplate = new MessageTemplate("Start", new Field[]{
                new StaticTemplateReference(nameTemplate)
        });
        definitionTemplate = new MessageTemplate("Definition", new Field[]{
            new StaticTemplateReference(nameTemplate)
        });

        context = new Context();
        context.getTemplateRegistry().register(1, nameTemplate);
        context.getTemplateRegistry().register(2, startTemplate);
        context.getTemplateRegistry().register(3, definitionTemplate);
    }
    
    public void testEncodeNestedStaticTemplateReference() {
        FastEncoder encoder = new FastEncoder(context);
        Message startMsg = new Message(startTemplate);
        startMsg.setString("Ns", "myNS");
        startMsg.setString("Name", "A");
        
        assertEquals("11110000 10000010 01101101 01111001 01001110 11010011 11000001", encoder.encode(startMsg));
        
        Message defMsg = new Message(definitionTemplate);
        defMsg.setString("Ns", "myNS");
        defMsg.setString("Name", "B");
        
        assertEquals("11010000 10000011 11000010", encoder.encode(defMsg));
    }
    
    public void testDecodeNestedStaticTemplateReference() {
        InputStream byteIn = bitStream("11110000 10000010 01101101 01111001 01001110 11010011 11000001 11010000 10000011 11000010");
        FastDecoder decoder = new FastDecoder(context, byteIn);
        
        Message startMsg = new Message(startTemplate);
        startMsg.setString("Ns", "myNS");
        startMsg.setString("Name", "A");
        
        assertEquals(startMsg, decoder.readMessage());
        
        Message defMsg = new Message(definitionTemplate);
        defMsg.setString("Ns", "myNS");
        defMsg.setString("Name", "B");
        
        assertEquals(defMsg, decoder.readMessage());
    }
}
