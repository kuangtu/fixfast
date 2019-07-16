package org.openfast.acceptance;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.openfast.ByteUtil;
import org.openfast.Context;
import org.openfast.Message;
import org.openfast.codec.FastDecoder;
import org.openfast.codec.FastEncoder;
import org.openfast.template.MessageTemplate;
import org.openfast.template.TemplateRegistry;
import org.openfast.template.loader.XMLMessageTemplateLoader;
import org.openfast.test.OpenFastTestCase;

public class EncodingDecodingAcceptanceTest extends OpenFastTestCase {
    public void testEncoding() throws IOException {
        XMLMessageTemplateLoader loader = new XMLMessageTemplateLoader(true);
        loader.setLoadTemplateIdFromAuxId(true);
        loader.load(getAcceptanceTestTemplates());
        TemplateRegistry registry = loader.getTemplateRegistry();
        MessageTemplate[] templates = registry.getTemplates();
        for (int i=0; i<templates.length; i++) {
            InputStream dataForTemplate = getDataForTemplate(templates[i]);
            if (dataForTemplate != null) {
                encode(registry, templates[i], dataForTemplate);
            }
        }
    }

    private void encode(TemplateRegistry registry, MessageTemplate template, InputStream dataForTemplate) throws IOException {
        Context encodingContext = new Context();
        Context decodingContext = new Context();
        encodingContext.setTemplateRegistry(registry);
        decodingContext.setTemplateRegistry(registry);
        FastEncoder encoder = new FastEncoder(encodingContext);
        BufferedReader reader = new BufferedReader(new InputStreamReader(dataForTemplate));
        String line = reader.readLine(); // Read header
        while ((line = reader.readLine()) != null) {
            String[] entries = line.split(",");
            Message message = createMessage(template, entries);
            byte[] encoded = encoder.encode(message);
            String expected = entries[entries.length-1].trim();
            assertEncoding(expected, encoded);
            assertDecoding(message, decodingContext, expected);
        }
    }
    
    private void assertDecoding(Message message, Context decodingContext, String expected) {
        ByteArrayInputStream byteIn = new ByteArrayInputStream(ByteUtil.convertHexStringToByteArray(expected));
        FastDecoder decoder = new FastDecoder(decodingContext, byteIn);
        assertEquals(message, decoder.readMessage());
    }

    private void assertEncoding(String expected, byte[] encoded) {
        String actual = ByteUtil.convertByteArrayToHexString(encoded);
        assertEquals(expected, actual);
    }

    private Message createMessage(MessageTemplate template, String[] entries) {
        Message message = new Message(template);
        for (int i=0; i<entries.length-1; i++) {
            message.setString(i+1, entries[i].trim());
        }
        return message;
    }

    private InputStream getDataForTemplate(MessageTemplate messageTemplate) {
        return this.getClass().getClassLoader().getResourceAsStream("acceptance/" + messageTemplate.getName() + ".csv");
    }

    private InputStream getAcceptanceTestTemplates() {
        return this.getClass().getClassLoader().getResourceAsStream("acceptance/templates.xml");
    }
}
