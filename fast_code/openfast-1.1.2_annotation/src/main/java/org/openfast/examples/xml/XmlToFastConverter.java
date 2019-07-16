package org.openfast.examples.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.openfast.Context;
import org.openfast.GroupValue;
import org.openfast.Message;
import org.openfast.MessageOutputStream;
import org.openfast.SequenceValue;
import org.openfast.extensions.MapFieldParser;
import org.openfast.template.MessageTemplate;
import org.openfast.template.Sequence;
import org.openfast.template.TemplateRegistry;
import org.openfast.template.loader.XMLMessageTemplateLoader;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.xml.XppDriver;

public class XmlToFastConverter {
    private final TemplateRegistry templateRegistry;
    private final MessageTemplate elementTemplate;
    private final int nodeNameIdx;
    private final int attributesIdx;
    private final int childrenIdx;
    private final int valueIdx;
    private final int attributeNameIdx;
    private final int attributeValueIdx;
    private final Sequence childrenSequence;
    private final Sequence attributesSequence;

    public XmlToFastConverter() {
        XMLMessageTemplateLoader xmlTemplateLoader = new XMLMessageTemplateLoader();
        xmlTemplateLoader.setLoadTemplateIdFromAuxId(true);
        xmlTemplateLoader.addFieldParser(new MapFieldParser());
        xmlTemplateLoader.load(this.getClass().getResourceAsStream("xmlOverFastTemplates.xml"));
        this.templateRegistry = xmlTemplateLoader.getTemplateRegistry();
        this.elementTemplate = templateRegistry.get("element");
        this.nodeNameIdx = elementTemplate.getFieldIndex("name");
        this.attributesIdx = elementTemplate.getFieldIndex("attributes");
        this.childrenSequence = elementTemplate.getSequence("children");
        this.attributesSequence = elementTemplate.getSequence("attributes");
        this.attributeNameIdx = attributesSequence.getGroup().getFieldIndex("name");
        this.attributeValueIdx = attributesSequence.getGroup().getFieldIndex("value");
        this.childrenIdx = elementTemplate.getFieldIndex("children");
        this.valueIdx = elementTemplate.getFieldIndex("value");
    }
    
    public void convert(InputStream in, OutputStream out) {
        try {
            HierarchicalStreamReader reader = new XppDriver().createReader(in);
            Context context = new Context();
            context.setTemplateRegistry(templateRegistry);
            MessageOutputStream messageOut = new MessageOutputStream(out, context);
            Message message = convertElementToMessage(reader);
            messageOut.writeMessage(message);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
            }
            try {
                out.close();
            } catch (IOException e) {
            }
        }
    }

    private Message convertElementToMessage(HierarchicalStreamReader reader) {
        Message message = new Message(elementTemplate);
        message.setString(nodeNameIdx, reader.getNodeName());
        SequenceValue attributes = new SequenceValue(attributesSequence);
        for (int i=0; i<reader.getAttributeCount(); i++) {
            GroupValue attribute = new GroupValue(this.attributesSequence.getGroup());
            attribute.setString(attributeNameIdx, reader.getAttributeName(i));
            attribute.setString(attributeValueIdx, reader.getAttribute(i));
            attributes.add(attribute);
        }
        message.setFieldValue(attributesIdx, attributes);
        if (reader.getValue() != null && !"".equals(reader.getValue()))
            message.setString(valueIdx, reader.getValue());
        SequenceValue children = new SequenceValue(childrenSequence);
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            GroupValue childValue = new GroupValue(childrenSequence.getGroup());
            Message child = convertElementToMessage(reader);
            childValue.setFieldValue(0, child);
            children.add(childValue);
            reader.moveUp();
        }
        message.setFieldValue(childrenIdx, children);
        return message;
    }
}
