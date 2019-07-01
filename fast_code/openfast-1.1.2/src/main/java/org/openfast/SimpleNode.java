package org.openfast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleNode implements Node {
    private List nodes = Collections.EMPTY_LIST;
    protected Map attributes = Collections.EMPTY_MAP;
    protected final QName name;
    
    public SimpleNode(QName nodeName) {
        this.name = nodeName;
    }

    public void addNode(Node node) {
        if (nodes.isEmpty())
            nodes = new ArrayList(3);
        nodes.add(node);
    }

    public List getNodes() {
        return nodes;
    }

    public String getAttribute(QName name) {
        return (String) attributes.get(name);
    }

    public Map getAttributes() {
        return attributes;
    }

    public List getChildren(QName name) {
        List children = Collections.EMPTY_LIST;
        for (int i=0; i<nodes.size() && name != null; i++) {
            Node child = (Node)nodes.get(i);
            if (name.equals(child.getNodeName())) {
                if (children.isEmpty()) {
                    children = new ArrayList();
                }
                children.add(nodes.get(i));
            }
        }
        return children;
    }

    public QName getNodeName() {
        return name;
    }

    public void setAttribute(QName name, String value) {
        if (attributes.isEmpty()) {
            attributes = new HashMap();
        }
        attributes.put(name, value);
    }

    public boolean hasAttribute(QName name) {
        return attributes.containsKey(name);
    }

    public boolean hasChild(QName name) {
        for (int i=0; i<nodes.size() && name != null; i++) {
            Node child = (Node)nodes.get(i);
            if (name.equals(child.getNodeName())) {
                return true;
            }
        }
        return false;
    }
}
