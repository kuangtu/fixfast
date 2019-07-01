package org.openfast.extensions;

import org.openfast.QName;
import org.openfast.template.Field;
import org.openfast.template.loader.FieldParser;
import org.openfast.template.loader.ParsingContext;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class MapFieldParser implements FieldParser {
    public boolean canParse(Element element, ParsingContext context) {
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            String nodeName = children.item(i).getNodeName();
            if (nodeName.equals("map"))
                return true;
        }
        return false;
    }

    public Field parse(Element fieldNode, ParsingContext context) {
        String key = fieldNode.hasAttribute("key") ? fieldNode.getAttribute("key") : null;
        boolean optional = "optional".equals(fieldNode.getAttribute("presence"));
        String name = fieldNode.getAttribute("name");
        QName qname = new QName(name, context.getNamespace());
        return new MapScalar(qname, optional, new QName(key));
    }
}
