package org.openfast.examples.producer;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.openfast.FieldValue;
import org.openfast.GroupValue;
import org.openfast.Message;
import org.openfast.QName;
import org.openfast.SequenceValue;
import org.openfast.template.BasicTemplateRegistry;
import org.openfast.template.Field;
import org.openfast.template.Group;
import org.openfast.template.MessageTemplate;
import org.openfast.template.Sequence;
import org.openfast.template.TemplateRegistry;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;

public class XmlCompressedMessageConverter {
    private static final QName INSTANCE_NAME = new QName("instanceName", "http://www.lasalletech.com/fast/ext/1.0");
    protected TemplateRegistry templateRegistry = new BasicTemplateRegistry();

    private final HierarchicalStreamDriver driver;

    public XmlCompressedMessageConverter() {
        this(new XppDriver());
    }

    public XmlCompressedMessageConverter(HierarchicalStreamDriver driver) {
        if (driver == null)
            throw new NullPointerException();
        this.driver = driver;
    }

    public List parse(InputStream in) {
        HierarchicalStreamReader reader = driver.createReader(in);
        return unmarshal(reader);
    }

    public void serialize(List messages, OutputStream out) {
        HierarchicalStreamWriter writer = driver.createWriter(out);
        writer.startNode("messages");
        for (Iterator iter = messages.iterator(); iter.hasNext();) {
            Message message = (Message) iter.next();
            marshal(message, writer, null);
        }
        writer.endNode();
        writer.close();
    }

    public void marshal(Object obj, HierarchicalStreamWriter writer, MarshallingContext context) {
        Message message = (Message) obj;
        writer.startNode(message.getGroup().getName());
        writeGroup(writer, message);
        writer.endNode();
    }

    private void writeGroup(HierarchicalStreamWriter writer, GroupValue groupValue) {
        Field[] fields = groupValue.getGroup().getFields();
        for (int i=0; i<fields.length; i++) {
            Field field = fields[i];
            if (!groupValue.isDefined(field.getName()))
                continue;
            if (field.getName().equals("templateId"))
                continue;
            writer.startNode(field.getName());
            if (field instanceof Group) {
                writeGroup(writer, groupValue.getGroup(field.getName()));
            } else if (field instanceof Sequence) {
                String instanceName = field.getName();
                if (field.hasAttribute(INSTANCE_NAME))
                    instanceName = field.getAttribute(INSTANCE_NAME);
                SequenceValue sequenceValue = groupValue.getSequence(field.getName());
                GroupValue[] seqValues = sequenceValue.getValues();
                for (int j=0; j<seqValues.length; j++) {
                    writer.startNode(instanceName);
                    writeGroup(writer, seqValues[j]);
                    writer.endNode();
                }
            } else {
                writer.setValue(groupValue.getString(field.getName()));
            }
            writer.endNode();
        }
    }

    public List unmarshal(HierarchicalStreamReader reader) {
        if (reader.getNodeName().equals("messages")) {
            List messages = new ArrayList();
            while (reader.hasMoreChildren()) {
                reader.moveDown();
                if (!templateRegistry.isRegistered(reader.getNodeName()))
                    throw new IllegalArgumentException("The template named " + reader.getNodeName() + " is not defined.");
                MessageTemplate template = templateRegistry.get(reader.getNodeName());
                Message message = (Message) template.createValue(null);
                parseGroup(reader, template, message);
                messages.add(message);
                reader.moveUp();
            }
            return messages;
        }
        return null;
    }

    private void parseGroup(HierarchicalStreamReader reader, Group group, GroupValue groupValue) {
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            Field field = group.getField(reader.getNodeName());
            if(field != null)
            {
                FieldValue value = null;
                if (field instanceof Group) {
                    Group currentGroup = (Group) field;
                    value = currentGroup.createValue(null);
                    parseGroup(reader, currentGroup, (GroupValue) value);
                } else if (field instanceof Sequence) {
                    Sequence currentSequence = (Sequence) field;
                    value = currentSequence.createValue(null);
                    parseSequence(reader, currentSequence, (SequenceValue) value);
                } else {
                    value = field.createValue(reader.getValue());
                }
                groupValue.setFieldValue(field, value);
            }
            else {
                System.err.println("Warning: skipping unexpected field '" + reader.getNodeName() + "'");
            }
            reader.moveUp();
        }
    }

    private void parseSequence(HierarchicalStreamReader reader, Sequence sequence, SequenceValue value) {
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            GroupValue current = (GroupValue) sequence.getGroup().createValue(null);
            parseGroup(reader, sequence.getGroup(), current);
            value.add(current);
            reader.moveUp();
        }
    }

    public boolean canConvert(Class clazz) {
        return clazz.equals(Message.class);
    }

    protected TemplateRegistry getTemplateRegistry() {
        return templateRegistry;
    }

    public void setTemplateRegistry(TemplateRegistry templateRegistry) {
        this.templateRegistry = templateRegistry;
    }
}
