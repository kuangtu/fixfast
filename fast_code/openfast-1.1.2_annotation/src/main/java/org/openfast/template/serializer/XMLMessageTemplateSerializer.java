package org.openfast.template.serializer;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.openfast.template.Group;
import org.openfast.template.MessageTemplate;
import org.openfast.template.Scalar;
import org.openfast.template.Sequence;
import org.openfast.util.XmlWriter;

public class XMLMessageTemplateSerializer implements MessageTemplateSerializer {
    private SerializingContext initialContext;
    public XMLMessageTemplateSerializer() {
        this.initialContext = createInitialContext();
    }
    public static SerializingContext createInitialContext() {
        SerializerRegistry registry = new SerializerRegistry();
        registry.addFieldSerializer(new ScalarSerializer());
        registry.addFieldSerializer(new DynamicTemplateReferenceSerializer());
        registry.addFieldSerializer(new StaticTemplateReferenceSerializer());
        registry.addFieldSerializer(new ComposedDecimalSerializer());
        registry.addFieldSerializer(new GroupSerializer());
        registry.addFieldSerializer(new SequenceSerializer());
        registry.addFieldSerializer(new TemplateSerializer());
        registry.addFieldSerializer(new VariableLengthScalarSerializer());
        SerializingContext context = SerializingContext.createInitialContext(registry);
        return context;
    }
    public void serialize(MessageTemplate[] templates, OutputStream destination) {
        XmlWriter writer = new XmlWriter(destination);
        writer.setEnableProcessingInstructions(true);
        SerializingContext context = new SerializingContext(initialContext);
        writer.start("templates");
        String templateNamespace = whichTemplateNamespaceIsUsedMost(templates);
        String childNamespace = whichNamespaceIsUsedMode(templates);
        if (!"".equals(childNamespace))
            writer.addAttribute("ns", childNamespace);
        if (!"".equals(templateNamespace))
            writer.addAttribute("templateNs", templateNamespace);
        writer.addAttribute("xmlns", "http://www.fixprotocol.org/ns/fast/td/1.1");
        context.setTemplateNamespace(templateNamespace);
        context.setNamespace(childNamespace);
        for (int i=0; i<templates.length; i++) {
            context.serialize(writer, templates[i]);
        }
        writer.end();
    }
    
    private String whichNamespaceIsUsedMode(MessageTemplate[] templates) {
        Map namespaces = new HashMap();
        for (int i=0; i<templates.length; i++) {
            tallyNamespaceReferences(templates[i], namespaces);
        }
        
        Iterator iter = namespaces.keySet().iterator();
        int champion = 0;
        String championNs = "";
        while (iter.hasNext()) {
            String contender = (String) iter.next();
            int contenderCount = ((Integer)namespaces.get(contender)).intValue();
            if (contenderCount > champion) {
                champion = contenderCount;
                championNs = contender;
            }
        }
        return championNs;
    }
    private void tallyNamespaceReferences(Group group, Map namespaces) {
        int start = 0;
        if (group instanceof MessageTemplate)
            start = 1;
        for (int i=start; i<group.getFieldCount(); i++) {
            if (group.getField(i) instanceof Scalar) {
                String ns = group.getField(i).getQName().getNamespace();
                if (!namespaces.containsKey(ns)) {
                    namespaces.put(ns, new Integer(1));
                } else {
                    namespaces.put(ns, new Integer(((Integer) namespaces.get(ns)).intValue() + 1));
                }
            } else if (group.getField(i) instanceof Group) {
                tallyNamespaceReferences((Group) group.getField(i), namespaces);
            } else if (group.getField(i) instanceof Sequence) {
                tallyNamespaceReferences(((Sequence)group.getField(i)).getGroup(), namespaces);
            }
        }
    }
    
    private String whichTemplateNamespaceIsUsedMost(MessageTemplate[] templates) {
        Map namespaces = new HashMap();
        for (int i=0; i<templates.length; i++) {
            String ns = templates[i].getQName().getNamespace();
            if (!namespaces.containsKey(ns)) {
                namespaces.put(ns, new Integer(1));
            } else {
                namespaces.put(ns, new Integer(((Integer) namespaces.get(ns)).intValue() + 1));
            }
        }
        
        Iterator iter = namespaces.keySet().iterator();
        int champion = 0;
        String championNs = "";
        while (iter.hasNext()) {
            String contender = (String) iter.next();
            int contenderCount = ((Integer)namespaces.get(contender)).intValue();
            if (contenderCount > champion) {
                champion = contenderCount;
                championNs = contender;
            }
        }
        return championNs;
    }
}
