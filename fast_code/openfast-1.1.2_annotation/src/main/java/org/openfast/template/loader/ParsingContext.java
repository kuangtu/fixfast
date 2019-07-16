/*
The contents of this file are subject to the Mozilla Public License
Version 1.1 (the "License"); you may not use this file except in
compliance with the License. You may obtain a copy of the License at
http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS"
basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
License for the specific language governing rights and limitations
under the License.

The Original Code is OpenFAST.

The Initial Developer of the Original Code is The LaSalle Technology
Group, LLC.  Portions created by The LaSalle Technology Group, LLC
are Copyright (C) The LaSalle Technology Group, LLC. All Rights Reserved.

Contributor(s): Jacob Northey <jacob@lasalletech.com>
                Craig Otis <cotis@lasalletech.com>
 */
package org.openfast.template.loader;

import java.util.List;
import java.util.Map;
import org.openfast.QName;
import org.openfast.error.ErrorHandler;
import org.openfast.template.TemplateRegistry;
import org.w3c.dom.Element;

public class ParsingContext {
    static final ParsingContext NULL = new ParsingContext();
    static {
        NULL.setDictionary("global");
        NULL.setNamespace("");
        NULL.setTemplateNamespace("");
    }
    private final ParsingContext parent;
    private String templateNamespace = null;
    private String namespace = null;
    private String dictionary = null;
    private ErrorHandler errorHandler;
    private TemplateRegistry templateRegistry;
    private Map typeMap;
    private List fieldParsers;
    private QName name;

    public ParsingContext() {
        this(NULL);
    }

    public ParsingContext(ParsingContext parent) {
        this.parent = parent;
    }

    public ParsingContext(Element node, ParsingContext parent) {
        this.parent = parent;
        if (node.hasAttribute("templateNs"))
            setTemplateNamespace(node.getAttribute("templateNs"));
        if (node.hasAttribute("ns"))
            setNamespace(node.getAttribute("ns"));
        if (node.hasAttribute("dictionary"))
            setDictionary(node.getAttribute("dictionary"));
        if (node.hasAttribute("name"))
            setName(new QName(node.getAttribute("name"), getNamespace()));
    }

    private void setName(QName name) {
        this.name = name;
    }

    public void setTemplateNamespace(String templateNS) {
        this.templateNamespace = templateNS;
    }

    public String getTemplateNamespace() {
        if (templateNamespace == null)
            return parent.getTemplateNamespace();
        return templateNamespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getNamespace() {
        if (namespace == null)
            return parent.getNamespace();
        return namespace;
    }

    public void setDictionary(String dictionary) {
        this.dictionary = dictionary;
    }

    public String getDictionary() {
        if (dictionary == null)
            return parent.getDictionary();
        return dictionary;
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public ErrorHandler getErrorHandler() {
        if (errorHandler == null)
            return parent.getErrorHandler();
        return errorHandler;
    }

    public TemplateRegistry getTemplateRegistry() {
        if (templateRegistry == null)
            return parent.getTemplateRegistry();
        return templateRegistry;
    }

    public void setTemplateRegistry(TemplateRegistry templateRegistry) {
        this.templateRegistry = templateRegistry;
    }

    public void setTypeMap(Map typeMap) {
        this.typeMap = typeMap;
    }

    public Map getTypeMap() {
        if (typeMap == null)
            return parent.getTypeMap();
        return typeMap;
    }

    public List getFieldParsers() {
        if (fieldParsers == null)
            return parent.getFieldParsers();
        return fieldParsers;
    }

    public void setFieldParsers(List list) {
        this.fieldParsers = list;
    }

    public FieldParser getFieldParser(Element element) {
        List parsers = getFieldParsers();
        for (int i = parsers.size() - 1; i >= 0; i--) {
            FieldParser fieldParser = ((FieldParser) parsers.get(i));
            if (fieldParser.canParse(element, this))
                return fieldParser;
        }
        return null;
    }

    public ParsingContext getParent() {
        return parent;
    }

    public QName getName() {
        return name;
    }

    public void addFieldParser(FieldParser parser) {
        getFieldParsers().add(parser);
    }
}
