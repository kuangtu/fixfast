package org.openfast.examples.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import org.openfast.Context;
import org.openfast.GroupValue;
import org.openfast.Message;
import org.openfast.SequenceValue;
import org.openfast.codec.FastDecoder;
import org.openfast.extensions.MapFieldParser;
import org.openfast.template.MessageTemplate;
import org.openfast.template.Sequence;
import org.openfast.template.TemplateRegistry;
import org.openfast.template.loader.XMLMessageTemplateLoader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;

public class FastToXmlConverter {
    private final TemplateRegistry templateRegistry;
    private final MessageTemplate elementTemplate;
    private final int nodeNameIdx;
    private final int attributesIdx;
    private final int childrenIdx;
    private final int valueIdx;
    private final int attributeNameIdx;
    private final int attributeValueIdx;
    
    public FastToXmlConverter() {
        XMLMessageTemplateLoader xmlTemplateLoader = new XMLMessageTemplateLoader();
        xmlTemplateLoader.setLoadTemplateIdFromAuxId(true);
        xmlTemplateLoader.addFieldParser(new MapFieldParser());
        xmlTemplateLoader.load(this.getClass().getResourceAsStream("xmlOverFastTemplates.xml"));
        this.templateRegistry = xmlTemplateLoader.getTemplateRegistry();
        this.elementTemplate = templateRegistry.get("element");
        this.nodeNameIdx = elementTemplate.getFieldIndex("name");
        this.attributesIdx = elementTemplate.getFieldIndex("attributes");
        Sequence attributesSequence = elementTemplate.getSequence("attributes");
        this.attributeNameIdx = attributesSequence.getGroup().getFieldIndex("name");
        this.attributeValueIdx = attributesSequence.getGroup().getFieldIndex("value");
        this.childrenIdx = elementTemplate.getFieldIndex("children");
        this.valueIdx = elementTemplate.getFieldIndex("value");
    }

    public void convert(InputStream in, OutputStream out) {
        try {
            HierarchicalStreamWriter writer = new XppDriver().createWriter(out);
            Context context = new Context();
            context.setTemplateRegistry(templateRegistry);
            FastDecoder decoder = new FastDecoder(context, in);
            Message message = decoder.readMessage();
            writeXml(writer, message);
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

    private void writeXml(HierarchicalStreamWriter writer, GroupValue message) {
        writer.startNode(message.getString(nodeNameIdx));
        if (message.isDefined(attributesIdx)) {
            SequenceValue attributes = message.getSequence(attributesIdx);
            Iterator iter = attributes.iterator();
            while (iter.hasNext()) {
                GroupValue attr = (GroupValue) iter.next();
                writer.addAttribute(attr.getString(attributeNameIdx), attr.getString(attributeValueIdx));
            }
        }
        if (message.isDefined(valueIdx)) {
            writer.setValue(message.getString(valueIdx));
        }
        if (message.isDefined(childrenIdx)) {
            SequenceValue children = message.getSequence(childrenIdx);
            Iterator iter = children.iterator();
            while (iter.hasNext()) {
                GroupValue child = (GroupValue) iter.next();
                writeXml(writer, child.getGroup(0));
            }
        }
        writer.endNode();
    }
}
