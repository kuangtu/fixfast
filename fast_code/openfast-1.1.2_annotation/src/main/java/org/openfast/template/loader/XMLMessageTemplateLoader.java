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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.openfast.error.ErrorCode;
import org.openfast.error.ErrorHandler;
import org.openfast.error.FastAlertSeverity;
import org.openfast.error.FastConstants;
import org.openfast.error.FastException;
import org.openfast.template.BasicTemplateRegistry;
import org.openfast.template.MessageTemplate;
import org.openfast.template.TemplateRegistry;
import org.openfast.template.type.Type;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XMLMessageTemplateLoader implements MessageTemplateLoader {
    static final String TEMPLATE_DEFINITION_NS = "http://www.fixprotocol.org/ns/fast/td/1.1";

    static final ErrorCode IO_ERROR = new ErrorCode(FastConstants.STATIC, -1, "IOERROR", "IO Error", FastAlertSeverity.ERROR);

    static final ErrorCode XML_PARSING_ERROR = new ErrorCode(FastConstants.STATIC, -1, "XMLPARSEERR", "XML Parsing Error",
            FastAlertSeverity.ERROR);

    static final ErrorCode INVALID_TYPE = new ErrorCode(FastConstants.STATIC, -1, "INVALIDTYPE", "Invalid Type",
            FastAlertSeverity.ERROR);

    // IMMUTABLE
    private final boolean namespaceAwareness;

    private final ParsingContext initialContext;

    private boolean loadTemplateIdFromAuxId;

    public XMLMessageTemplateLoader() {
        this(false);
    }

    public XMLMessageTemplateLoader(boolean namespaceAwareness) {
        this.namespaceAwareness = namespaceAwareness;
        this.initialContext = createInitialContext();
    }

    public static ParsingContext createInitialContext() {
        ParsingContext initialContext = new ParsingContext();
        initialContext.setErrorHandler(ErrorHandler.DEFAULT);
        initialContext.setTemplateRegistry(new BasicTemplateRegistry());
        initialContext.setTypeMap(Type.getRegisteredTypeMap());
        initialContext.setFieldParsers(new ArrayList());
        initialContext.addFieldParser(new ScalarParser());
        initialContext.addFieldParser(new GroupParser());
        initialContext.addFieldParser(new SequenceParser());
        initialContext.addFieldParser(new ComposedDecimalParser());
        initialContext.addFieldParser(new StringParser());
        initialContext.addFieldParser(new ByteVectorParser());
        initialContext.addFieldParser(new TemplateRefParser());
        return initialContext;
    }

    public void addFieldParser(FieldParser fieldParser) {
        initialContext.getFieldParsers().add(fieldParser);
    }

    /**
     * Parses the XML stream and creates an array of the elements
     * 
     * @param source
     *            The inputStream object to load
     */
    public MessageTemplate[] load(InputStream source) {
        Document document = parseXml(source);

        if (document == null) {
            return new MessageTemplate[] {};
        }

        Element root = document.getDocumentElement();

        TemplateParser templateParser = new TemplateParser(loadTemplateIdFromAuxId);

        if (root.getNodeName().equals("template")) {
            return new MessageTemplate[] { (MessageTemplate) templateParser.parse(root, initialContext) };
        } else if (root.getNodeName().equals("templates")) {
            ParsingContext context = new ParsingContext(root, initialContext);

            NodeList templateTags = root.getElementsByTagName("template");
            MessageTemplate[] templates = new MessageTemplate[templateTags.getLength()];
            int templatesToLoad = templates.length;
            int previousNumberOfTemplatesLeft = templates.length;
            while (templatesToLoad > 0) {
                for (int i = 0; i < templateTags.getLength(); i++) {
                    if (templates[i] == null) {
                        Element templateTag = (Element) templateTags.item(i);
                        MessageTemplate template = (MessageTemplate) templateParser.parse(templateTag, context);
                        if (template != null) {
                            templates[i] = template;
                            templatesToLoad--;
                        }
                    }
                }
                if (previousNumberOfTemplatesLeft == templatesToLoad) {
                    throw new FastException("Unresolved static template references exist.", FastConstants.PARSE_ERROR);
                }
                previousNumberOfTemplatesLeft = templatesToLoad;
            }
            return templates;
        } else {
            initialContext.getErrorHandler().error(FastConstants.S1_INVALID_XML,
                    "Invalid root node " + root.getNodeName() + ", \"template\" or \"templates\" expected.");
            return new MessageTemplate[] {};
        }
    }

    /**
     * Parse an XML file from an inputStream, returns a DOM org.w3c.dom.Document
     * object.
     * 
     * @param templateStream
     *            The inputStream to be parsed
     * @return Returns a DOM org.w3c.dom.Document object, returns null if there
     *         are exceptions caught
     */
    private Document parseXml(InputStream templateStream) {
        org.xml.sax.ErrorHandler errorHandler = new org.xml.sax.ErrorHandler() {
            public void error(SAXParseException exception) throws SAXException {
                initialContext.getErrorHandler().error(XML_PARSING_ERROR, "ERROR: " + exception.getMessage(), exception);
            }

            public void fatalError(SAXParseException exception) throws SAXException {
                initialContext.getErrorHandler().error(XML_PARSING_ERROR, "FATAL: " + exception.getMessage(), exception);
            }

            public void warning(SAXParseException exception) throws SAXException {
                initialContext.getErrorHandler().error(XML_PARSING_ERROR, "WARNING: " + exception.getMessage(), exception);
            }
        };
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setIgnoringElementContentWhitespace(true);
            dbf.setNamespaceAware(namespaceAwareness);
            DocumentBuilder builder = dbf.newDocumentBuilder();

            builder.setErrorHandler(errorHandler);
            InputSource inputSource = new InputSource(templateStream);
            Document document = builder.parse(inputSource);
            return document;
        } catch (IOException e) {
            initialContext.getErrorHandler().error(IO_ERROR, "Error occurred while trying to read xml template: " + e.getMessage(), e);
        } catch (Exception e) {
            initialContext.getErrorHandler().error(XML_PARSING_ERROR, "Error occurred while parsing xml template: " + e.getMessage(),
                    e);
        }

        return null;
    }

    /**
     * Sets the errorHandler object to a method
     * 
     * @param errorHandler
     *            The errorHandler that is being set
     */
    public void setErrorHandler(ErrorHandler errorHandler) {
        initialContext.setErrorHandler(errorHandler);
    }

    public void setTemplateRegistry(TemplateRegistry templateRegistry) {
        initialContext.setTemplateRegistry(templateRegistry);
    }

    public TemplateRegistry getTemplateRegistry() {
        return initialContext.getTemplateRegistry();
    }

    public void setTypeMap(Map typeMap) {
        initialContext.setTypeMap(typeMap);
    }

    public void setLoadTemplateIdFromAuxId(boolean loadTempalteIdFromAuxId) {
        this.loadTemplateIdFromAuxId = loadTempalteIdFromAuxId;
    }
}
