package org.openfast;

import java.util.List;
import java.util.Map;

public interface Node {
    List getNodes();
    List getChildren(QName name);
    String getAttribute(QName name);
    
    QName getNodeName();
    Map getAttributes();
    boolean hasAttribute(QName name);
    boolean hasChild(QName name);
    
    void addNode(Node node);
    void setAttribute(QName name, String value);
}
