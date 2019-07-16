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
package org.openfast.session;

import java.util.HashMap;
import java.util.Map;
import org.openfast.Context;
import org.openfast.Dictionary;
import org.openfast.FieldValue;
import org.openfast.GroupValue;
import org.openfast.Message;
import org.openfast.MessageHandler;
import org.openfast.QName;
import org.openfast.ScalarValue;
import org.openfast.codec.Coder;
import org.openfast.error.ErrorCode;
import org.openfast.session.template.exchange.AbstractFieldInstructionConverter;
import org.openfast.session.template.exchange.ComposedDecimalConverter;
import org.openfast.session.template.exchange.ConversionContext;
import org.openfast.session.template.exchange.DynamicTemplateReferenceConverter;
import org.openfast.session.template.exchange.GroupConverter;
import org.openfast.session.template.exchange.ScalarConverter;
import org.openfast.session.template.exchange.SequenceConverter;
import org.openfast.session.template.exchange.StaticTemplateReferenceConverter;
import org.openfast.session.template.exchange.VariableLengthInstructionConverter;
import org.openfast.template.BasicTemplateRegistry;
import org.openfast.template.DynamicTemplateReference;
import org.openfast.template.Field;
import org.openfast.template.Group;
import org.openfast.template.MessageTemplate;
import org.openfast.template.Scalar;
import org.openfast.template.Sequence;
import org.openfast.template.StaticTemplateReference;
import org.openfast.template.TemplateRegistry;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.Type;

public class SessionControlProtocol_1_1 extends AbstractSessionControlProtocol {
    public static final String NAMESPACE = "http://www.fixprotocol.org/ns/fast/scp/1.1";
    private static final QName RESET_PROPERTY = new QName("reset", NAMESPACE);
    private static final Map/* <MessageTemplate, SessionMessageHandler> */messageHandlers = new HashMap();
    private final ConversionContext initialContext = createInitialContext();

    protected SessionControlProtocol_1_1() {
        messageHandlers.put(FAST_ALERT_TEMPLATE, ALERT_HANDLER);
        messageHandlers.put(TEMPLATE_DEFINITION, new SessionMessageHandler() {
            public void handleMessage(Session session, Message message) {
                MessageTemplate template = createTemplateFromMessage(message, session.in.getTemplateRegistry());
                session.addDynamicTemplateDefinition(template);
                if (message.isDefined("TemplateId"))
                    session.registerDynamicTemplate(template.getQName(), message.getInt("TemplateId"));
            }
        });
        messageHandlers.put(TEMPLATE_DECLARATION, new SessionMessageHandler() {
            public void handleMessage(Session session, Message message) {
                session.registerDynamicTemplate(getQName(message), message.getInt("TemplateId"));
            }
        });
    }
    public static ConversionContext createInitialContext() {
        ConversionContext context = new ConversionContext();
        context.addFieldInstructionConverter(new ScalarConverter());
        context.addFieldInstructionConverter(new SequenceConverter());
        context.addFieldInstructionConverter(new GroupConverter());
        context.addFieldInstructionConverter(new DynamicTemplateReferenceConverter());
        context.addFieldInstructionConverter(new StaticTemplateReferenceConverter());
        context.addFieldInstructionConverter(new ComposedDecimalConverter());
        context.addFieldInstructionConverter(new VariableLengthInstructionConverter());
        return context;
    }
    protected QName getQName(Message message) {
        String name = message.getString("Name");
        String ns = message.getString("Ns");
        return new QName(name, ns);
    }
    public void configureSession(Session session) {
        registerSessionTemplates(session.in.getTemplateRegistry());
        registerSessionTemplates(session.out.getTemplateRegistry());
        session.in.addMessageHandler(FAST_RESET_TEMPLATE, RESET_HANDLER);
        session.out.addMessageHandler(FAST_RESET_TEMPLATE, RESET_HANDLER);
    }
    public void registerSessionTemplates(TemplateRegistry registry) {
        registry.registerAll(TEMPLATE_REGISTRY);
    }
    public Session connect(String senderName, Connection connection, TemplateRegistry inboundRegistry,
            TemplateRegistry outboundRegistry, MessageListener messageListener, SessionListener sessionListener) {
        Session session = new Session(connection, this, inboundRegistry, outboundRegistry);
        configureSession(session);
        session.out.writeMessage(createHelloMessage(senderName));
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {}
        Message message = session.in.readMessage();
        session.setMessageHandler(messageListener);
        String serverName = message.getString(1);
        String vendorId = message.isDefined(2) ? message.getString(2) : "unknown";
        session.setClient(new BasicClient(serverName, vendorId));
        return session;
    }
    public void onError(Session session, ErrorCode code, String message) {
        session.out.writeMessage(createFastAlertMessage(code));
    }
    public Session onNewConnection(String serverName, Connection connection) {
        Session session = new Session(connection, this, TemplateRegistry.NULL, TemplateRegistry.NULL);
        Message message = session.in.readMessage();
        String clientName = message.getString(1);
        String vendorId = message.isDefined(2) ? message.getString(2) : "unknown";
        session.setClient(new BasicClient(clientName, vendorId));
        session.out.writeMessage(createHelloMessage(serverName));
        return session;
    }
    public Message createHelloMessage(String senderName) {
        Message message = new Message(FAST_HELLO_TEMPLATE);
        message.setString(1, senderName);
        message.setString(2, SessionConstants.VENDOR_ID);
        return message;
    }
    public static Message createFastAlertMessage(ErrorCode code) {
        Message alert = new Message(FAST_ALERT_TEMPLATE);
        alert.setInteger(1, code.getSeverity().getCode());
        alert.setInteger(2, code.getCode());
        alert.setString(4, code.getDescription());
        return alert;
    }
    public void handleMessage(Session session, Message message) {
        if (!messageHandlers.containsKey(message.getTemplate()))
            return;
        ((SessionMessageHandler) messageHandlers.get(message.getTemplate())).handleMessage(session, message);
    }
    public boolean isProtocolMessage(Message message) {
        if (message == null)
            return false;
        return messageHandlers.containsKey(message.getTemplate());
    }
    public boolean supportsTemplateExchange() {
        return true;
    }
    public Message createTemplateDeclarationMessage(MessageTemplate messageTemplate, int templateId) {
        Message declaration = new Message(TEMPLATE_DECLARATION);
        AbstractFieldInstructionConverter.setName(messageTemplate, declaration);
        declaration.setInteger("TemplateId", templateId);
        return declaration;
    }
    public Message createTemplateDefinitionMessage(MessageTemplate messageTemplate) {
        Message templateDefinition = GroupConverter.convert(messageTemplate, new Message(TEMPLATE_DEFINITION), initialContext);
        int reset = messageTemplate.hasAttribute(RESET_PROPERTY) ? 1 : 0;
        templateDefinition.setInteger("Reset", reset);
        return templateDefinition;
    }

    public MessageTemplate createTemplateFromMessage(Message templateDef, TemplateRegistry registry) {
        String name = templateDef.getString("Name");
        String namespace = "";
        if (templateDef.isDefined("Ns"))
            namespace = templateDef.getString("Ns");
        Field[] fields = GroupConverter.parseFieldInstructions(templateDef, registry, initialContext);
        MessageTemplate group = new MessageTemplate(new QName(name, namespace), fields);
        if (templateDef.isDefined("TypeRef")) {
            GroupValue typeRef = templateDef.getGroup("TypeRef");
            String typeRefName = typeRef.getString("Name");
            String typeRefNs = ""; // context.getNamespace();
            if (typeRef.isDefined("Ns"))
                typeRefNs = typeRef.getString("Ns");
            group.setTypeReference(new QName(typeRefName, typeRefNs));
        }
        if (templateDef.isDefined("AuxId")) {
            group.setId(templateDef.getString("AuxId"));
        }
        return group;
    }

    public static final int FAST_RESET_TEMPLATE_ID = 120;
    public static final int FAST_HELLO_TEMPLATE_ID = 16002;
    public static final int FAST_ALERT_TEMPLATE_ID = 16003;
    public static final int TEMPLATE_DECL_ID = 16010;
    public static final int TEMPLATE_DEF_ID = 16011;
    public static final int INT32_INSTR_ID = 16012;
    public static final int UINT32_INSTR_ID = 16013;
    public static final int INT64_INSTR_ID = 16014;
    public static final int UINT64_INSTR_ID = 16015;
    public static final int DECIMAL_INSTR_ID = 16016;
    public static final int COMP_DECIMAL_INSTR_ID = 16017;
    public static final int ASCII_INSTR_ID = 16018;
    public static final int UNICODE_INSTR_ID = 16019;
    public static final int BYTE_VECTOR_INSTR_ID = 16020;
    public static final int STAT_TEMP_REF_INSTR_ID = 16021;
    public static final int DYN_TEMP_REF_INSTR_ID = 16022;
    public static final int SEQUENCE_INSTR_ID = 16023;
    public static final int GROUP_INSTR_ID = 16024;
    public static final int CONSTANT_OP_ID = 16025;
    public static final int DEFAULT_OP_ID = 16026;
    public static final int COPY_OP_ID = 16027;
    public static final int INCREMENT_OP_ID = 16028;
    public static final int DELTA_OP_ID = 16029;
    public static final int TAIL_OP_ID = 16030;
    public static final int FOREIGN_INSTR_ID = 16031;
    public static final int ELEMENT_ID = 16032;
    public static final int TEXT_ID = 16033;
    public final static MessageTemplate FAST_ALERT_TEMPLATE = new MessageTemplate("Alert", new Field[] {
            new Scalar("Severity", Type.U32, Operator.NONE, ScalarValue.UNDEFINED, false),
            new Scalar("Code", Type.U32, Operator.NONE, ScalarValue.UNDEFINED, false),
            new Scalar("Value", Type.U32, Operator.NONE, ScalarValue.UNDEFINED, true),
            new Scalar("Description", Type.ASCII, Operator.NONE, ScalarValue.UNDEFINED, false), });
    public final static MessageTemplate FAST_HELLO_TEMPLATE = new MessageTemplate("Hello", new Field[] {
            new Scalar("SenderName", Type.ASCII, Operator.NONE, ScalarValue.UNDEFINED, false),
            new Scalar("VendorId", Type.ASCII, Operator.NONE, ScalarValue.UNDEFINED, true) });
    public static final Message RESET = new Message(FAST_RESET_TEMPLATE) {
        private static final long serialVersionUID = 1L;

        public void setFieldValue(int fieldIndex, FieldValue value) {
            throw new IllegalStateException("Cannot set values on a fast reserved message.");
        }
    };
    static {
        FAST_RESET_TEMPLATE.setAttribute(RESET_PROPERTY, "yes");
    }
    /**
     * ************************ MESSAGE HANDLERS
     * *********************************************
     */
    private static final MessageHandler RESET_HANDLER = new MessageHandler() {
        public void handleMessage(Message readMessage, Context context, Coder coder) {
            if (readMessage.getTemplate().hasAttribute(RESET_PROPERTY))
                coder.reset();
        }
    };
    private static final SessionMessageHandler ALERT_HANDLER = new SessionMessageHandler() {
        public void handleMessage(Session session, Message message) {
            ErrorCode alertCode = ErrorCode.getAlertCode(message);
            if (alertCode.equals(SessionConstants.CLOSE)) {
                session.close(alertCode);
            } else {
                session.getErrorHandler().error(alertCode, message.getString(4));
            }
        }
    };
    /**
     * ************************* MESSAGE TEMPLATES
     * ******************************************
     */
    private static final MessageTemplate ATTRIBUTE = new MessageTemplate(new QName("Attribute", NAMESPACE), new Field[] {
            dict("Ns", Type.UNICODE, true, "template"), unicode("Name"), unicode("Value") });
    private static final MessageTemplate ELEMENT = new MessageTemplate(new QName("Element", NAMESPACE), new Field[] {
            dict("Ns", Type.UNICODE, true, "template"), unicode("Name"),
            new Sequence(qualify("Attributes"), new Field[] { new StaticTemplateReference(ATTRIBUTE) }, false),
            new Sequence(qualify("Content"), new Field[] { DynamicTemplateReference.INSTANCE }, false) });
    private static final MessageTemplate OTHER = new MessageTemplate(new QName("Other", NAMESPACE), new Field[] { new Group(
            qualify("Other"), new Field[] {
                    new Sequence(qualify("ForeignAttributes"), new Field[] { new StaticTemplateReference(ATTRIBUTE) }, true),
                    new Sequence(qualify("ForeignElements"), new Field[] { new StaticTemplateReference(ELEMENT) }, true) }, true) });
    private static final MessageTemplate TEMPLATE_NAME = new MessageTemplate(new QName("TemplateName", NAMESPACE), new Field[] {
            dict("Ns", Type.UNICODE, false, Dictionary.TEMPLATE),
            new Scalar(qualify("Name"), Type.UNICODE, Operator.NONE, null, false) });
    private static final MessageTemplate NS_NAME = new MessageTemplate(new QName("NsName", NAMESPACE), new Field[] {
            new Scalar(qualify("Ns"), Type.UNICODE, Operator.COPY, null, false),
            new Scalar(qualify("Name"), Type.UNICODE, Operator.NONE, null, false) });
    private static final MessageTemplate NS_NAME_WITH_AUX_ID = new MessageTemplate(
            new QName("NsNameWithAuxId", NAMESPACE),
            new Field[] { new StaticTemplateReference(NS_NAME), new Scalar(qualify("AuxId"), Type.UNICODE, Operator.NONE, null, true) });
    private static final MessageTemplate FIELD_BASE = new MessageTemplate(new QName("PrimFieldBase", NAMESPACE), new Field[] {
            new StaticTemplateReference(NS_NAME_WITH_AUX_ID), new Scalar(qualify("Optional"), Type.U32, Operator.NONE, null, false),
            new StaticTemplateReference(OTHER) });
    private static final MessageTemplate PRIM_FIELD_BASE = new MessageTemplate(new QName("PrimFieldBase", NAMESPACE), new Field[] {
            new StaticTemplateReference(FIELD_BASE),
            new Group(qualify("Operator"), new Field[] { DynamicTemplateReference.INSTANCE }, true) });
    private static final MessageTemplate LENGTH_PREAMBLE = new MessageTemplate(new QName("LengthPreamble", NAMESPACE), new Field[] {
            new StaticTemplateReference(NS_NAME_WITH_AUX_ID), new StaticTemplateReference(OTHER) });
    private static final MessageTemplate PRIM_FIELD_BASE_WITH_LENGTH = new MessageTemplate(new QName("PrimFieldBaseWithLength",
            NAMESPACE), new Field[] { new StaticTemplateReference(PRIM_FIELD_BASE),
            new Group(qualify("Length"), new Field[] { new StaticTemplateReference(LENGTH_PREAMBLE) }, true) });
    public static final MessageTemplate INT32_INSTR = new MessageTemplate(new QName("Int32Instr", NAMESPACE), new Field[] {
            new StaticTemplateReference(PRIM_FIELD_BASE), new Scalar(qualify("InitialValue"), Type.I32, Operator.NONE, null, true) });
    public static final MessageTemplate UINT32_INSTR = new MessageTemplate(new QName("UInt32Instr", NAMESPACE), new Field[] {
            new StaticTemplateReference(PRIM_FIELD_BASE), new Scalar(qualify("InitialValue"), Type.U32, Operator.NONE, null, true) });
    public static final MessageTemplate INT64_INSTR = new MessageTemplate(new QName("Int64Instr", NAMESPACE), new Field[] {
            new StaticTemplateReference(PRIM_FIELD_BASE), new Scalar(qualify("InitialValue"), Type.I64, Operator.NONE, null, true) });
    public static final MessageTemplate UINT64_INSTR = new MessageTemplate(new QName("UInt64Instr", NAMESPACE), new Field[] {
            new StaticTemplateReference(PRIM_FIELD_BASE), new Scalar(qualify("InitialValue"), Type.U64, Operator.NONE, null, true) });
    public static final MessageTemplate DECIMAL_INSTR = new MessageTemplate(new QName("DecimalInstr", NAMESPACE),
            new Field[] { new StaticTemplateReference(PRIM_FIELD_BASE),
                    new Scalar(qualify("InitialValue"), Type.DECIMAL, Operator.NONE, null, true) });
    public static final MessageTemplate UNICODE_INSTR = new MessageTemplate(new QName("UnicodeInstr", NAMESPACE), new Field[] {
            new StaticTemplateReference(PRIM_FIELD_BASE_WITH_LENGTH),
            new Scalar(qualify("InitialValue"), Type.UNICODE, Operator.NONE, null, true) });
    public static final MessageTemplate ASCII_INSTR = new MessageTemplate(new QName("AsciiInstr", NAMESPACE), new Field[] {
            new StaticTemplateReference(PRIM_FIELD_BASE), new Scalar(qualify("InitialValue"), Type.ASCII, Operator.NONE, null, true) });
    public static final MessageTemplate BYTE_VECTOR_INSTR = new MessageTemplate(new QName("ByteVectorInstr", NAMESPACE), new Field[] {
            new StaticTemplateReference(PRIM_FIELD_BASE_WITH_LENGTH),
            new Scalar(qualify("InitialValue"), Type.BYTE_VECTOR, Operator.NONE, null, true) });
    public static final MessageTemplate TYPE_REF = new MessageTemplate(new QName("TypeRef", NAMESPACE), new Field[] { new Group(
            qualify("TypeRef"), new Field[] { new StaticTemplateReference(NS_NAME), new StaticTemplateReference(OTHER) }, true) });
    public static final MessageTemplate TEMPLATE_DECLARATION = new MessageTemplate(new QName("TemplateDecl", NAMESPACE), new Field[] {
            new StaticTemplateReference(TEMPLATE_NAME), u32("TemplateId") });
    public static final MessageTemplate TEMPLATE_DEFINITION = new MessageTemplate(new QName("TemplateDef", NAMESPACE), new Field[] {
            new StaticTemplateReference(TEMPLATE_NAME), unicodeopt("AuxId"), u32opt("TemplateId"),
            new StaticTemplateReference(TYPE_REF), u32("Reset"), new StaticTemplateReference(OTHER),
            new Sequence(qualify("Instructions"), new Field[] { DynamicTemplateReference.INSTANCE }, false) });
    public static final MessageTemplate OP_BASE = new MessageTemplate(new QName("OpBase", NAMESPACE), new Field[] {
            unicodeopt("Dictionary"), new Group(qualify("Key"), new Field[] { new StaticTemplateReference(NS_NAME) }, true),
            new StaticTemplateReference(OTHER) });
    public static final MessageTemplate CONSTANT_OP = new MessageTemplate(new QName("ConstantOp", NAMESPACE),
            new Field[] { new StaticTemplateReference(OTHER) });
    public static final MessageTemplate DEFAULT_OP = new MessageTemplate(new QName("DefaultOp", NAMESPACE),
            new Field[] { new StaticTemplateReference(OTHER) });
    public static final MessageTemplate COPY_OP = new MessageTemplate(new QName("CopyOp", NAMESPACE),
            new Field[] { new StaticTemplateReference(OP_BASE) });
    public static final MessageTemplate INCREMENT_OP = new MessageTemplate(new QName("IncrementOp", NAMESPACE),
            new Field[] { new StaticTemplateReference(OP_BASE) });
    public static final MessageTemplate DELTA_OP = new MessageTemplate(new QName("DeltaOp", NAMESPACE),
            new Field[] { new StaticTemplateReference(OP_BASE) });
    public static final MessageTemplate TAIL_OP = new MessageTemplate(new QName("TailOp", NAMESPACE),
            new Field[] { new StaticTemplateReference(OP_BASE) });
    public static final MessageTemplate GROUP_INSTR = new MessageTemplate(new QName("GroupInstr", NAMESPACE), new Field[] {
            new StaticTemplateReference(FIELD_BASE), new StaticTemplateReference(TYPE_REF),
            new Sequence(qualify("Instructions"), new Field[] { DynamicTemplateReference.INSTANCE }, false) });
    public static final MessageTemplate SEQUENCE_INSTR = new MessageTemplate(new QName("SequenceInstr", NAMESPACE), new Field[] {
            new StaticTemplateReference(FIELD_BASE),
            new StaticTemplateReference(TYPE_REF),
            new Group(qualify("Length"), new Field[] {
                    new Group(qualify("Name"), new Field[] { new StaticTemplateReference(NS_NAME_WITH_AUX_ID) }, true),
                    new Group(qualify("Operator"), new Field[] { DynamicTemplateReference.INSTANCE }, true),
                    new Scalar(qualify("InitialValue"), Type.U32, Operator.NONE, null, true), new StaticTemplateReference(OTHER), },
                    true), new Sequence(qualify("Instructions"), new Field[] { DynamicTemplateReference.INSTANCE }, false) });
    public static final MessageTemplate STAT_TEMP_REF_INSTR = new MessageTemplate(new QName("StaticTemplateRefInstr", NAMESPACE),
            new Field[] { new StaticTemplateReference(TEMPLATE_NAME), new StaticTemplateReference(OTHER) });
    public static final MessageTemplate DYN_TEMP_REF_INSTR = new MessageTemplate(new QName("DynamicTemplateRefInstr", NAMESPACE),
            new Field[] { new StaticTemplateReference(OTHER) });
    public static final MessageTemplate FOREIGN_INSTR = new MessageTemplate(qualify("ForeignInstr"),
            new Field[] { new StaticTemplateReference(ELEMENT) });
    public static final MessageTemplate TEXT = new MessageTemplate(qualify("Text"), new Field[] { new Scalar(qualify("Value"),
            Type.UNICODE, Operator.NONE, ScalarValue.UNDEFINED, false) });
    public static final MessageTemplate COMP_DECIMAL_INSTR = new MessageTemplate(qualify("CompositeDecimalInstr"), new Field[] {
            new StaticTemplateReference(FIELD_BASE),
            new Group(qualify("Exponent"), new Field[] {
                    new Group(qualify("Operator"), new Field[] { DynamicTemplateReference.INSTANCE }, false),
                    new Scalar(qualify("InitialValue"), Type.I32, Operator.NONE, ScalarValue.UNDEFINED, true),
                    new StaticTemplateReference(OTHER) }, true),
            new Group(qualify("Mantissa"), new Field[] {
                    new Group(qualify("Operator"), new Field[] { DynamicTemplateReference.INSTANCE }, false),
                    new Scalar(qualify("InitialValue"), Type.I32, Operator.NONE, ScalarValue.UNDEFINED, true),
                    new StaticTemplateReference(OTHER) }, true) });
    public static final Message DYN_TEMP_REF_MESSAGE = new Message(DYN_TEMP_REF_INSTR);

    private static Field u32(String name) {
        return new Scalar(qualify(name), Type.U32, Operator.NONE, null, false);
    }
    private static Field dict(String name, Type type, boolean optional, String dictionary) {
        Scalar scalar = new Scalar(qualify(name), type, Operator.COPY, null, optional);
        scalar.setDictionary(dictionary);
        return scalar;
    }
    private static QName qualify(String name) {
        return new QName(name, NAMESPACE);
    }
    private static Field unicodeopt(String name) {
        return new Scalar(qualify(name), Type.UNICODE, Operator.NONE, null, true);
    }
    private static Field unicode(String name) {
        return new Scalar(qualify(name), Type.UNICODE, Operator.NONE, null, false);
    }
    private static Field u32opt(String name) {
        return new Scalar(qualify(name), Type.U32, Operator.NONE, null, true);
    }

    private static final TemplateRegistry TEMPLATE_REGISTRY = new BasicTemplateRegistry();
    static {
        TEMPLATE_REGISTRY.register(FAST_HELLO_TEMPLATE_ID, FAST_HELLO_TEMPLATE);
        TEMPLATE_REGISTRY.register(FAST_ALERT_TEMPLATE_ID, FAST_ALERT_TEMPLATE);
        TEMPLATE_REGISTRY.register(FAST_RESET_TEMPLATE_ID, FAST_RESET_TEMPLATE);
        TEMPLATE_REGISTRY.register(TEMPLATE_DECL_ID, TEMPLATE_DECLARATION);
        TEMPLATE_REGISTRY.register(TEMPLATE_DEF_ID, TEMPLATE_DEFINITION);
        TEMPLATE_REGISTRY.register(INT32_INSTR_ID, INT32_INSTR);
        TEMPLATE_REGISTRY.register(UINT32_INSTR_ID, UINT32_INSTR);
        TEMPLATE_REGISTRY.register(INT64_INSTR_ID, INT64_INSTR);
        TEMPLATE_REGISTRY.register(UINT64_INSTR_ID, UINT64_INSTR);
        TEMPLATE_REGISTRY.register(DECIMAL_INSTR_ID, DECIMAL_INSTR);
        TEMPLATE_REGISTRY.register(COMP_DECIMAL_INSTR_ID, COMP_DECIMAL_INSTR);
        TEMPLATE_REGISTRY.register(ASCII_INSTR_ID, ASCII_INSTR);
        TEMPLATE_REGISTRY.register(UNICODE_INSTR_ID, UNICODE_INSTR);
        TEMPLATE_REGISTRY.register(BYTE_VECTOR_INSTR_ID, BYTE_VECTOR_INSTR);
        TEMPLATE_REGISTRY.register(STAT_TEMP_REF_INSTR_ID, STAT_TEMP_REF_INSTR);
        TEMPLATE_REGISTRY.register(DYN_TEMP_REF_INSTR_ID, DYN_TEMP_REF_INSTR);
        TEMPLATE_REGISTRY.register(SEQUENCE_INSTR_ID, SEQUENCE_INSTR);
        TEMPLATE_REGISTRY.register(GROUP_INSTR_ID, GROUP_INSTR);
        TEMPLATE_REGISTRY.register(CONSTANT_OP_ID, CONSTANT_OP);
        TEMPLATE_REGISTRY.register(DEFAULT_OP_ID, DEFAULT_OP);
        TEMPLATE_REGISTRY.register(COPY_OP_ID, COPY_OP);
        TEMPLATE_REGISTRY.register(INCREMENT_OP_ID, INCREMENT_OP);
        TEMPLATE_REGISTRY.register(DELTA_OP_ID, DELTA_OP);
        TEMPLATE_REGISTRY.register(TAIL_OP_ID, TAIL_OP);
        TEMPLATE_REGISTRY.register(FOREIGN_INSTR_ID, FOREIGN_INSTR);
        TEMPLATE_REGISTRY.register(ELEMENT_ID, ELEMENT);
        TEMPLATE_REGISTRY.register(TEXT_ID, TEXT);
        MessageTemplate[] templates = TEMPLATE_REGISTRY.getTemplates();
        for (int i = 0; i < templates.length; i++) {
            setNamespace(templates[i]);
        }
    }

    private static void setNamespace(Group group) {
        group.setChildNamespace(NAMESPACE);
        Field[] fields = group.getFields();
        for (int i = 0; i < fields.length; i++) {
            if (fields[i] instanceof Group) {
                setNamespace((Group) fields[i]);
            }
        }
    }
    public Message getCloseMessage() {
        return CLOSE;
    }

    private static final Message CLOSE = createFastAlertMessage(SessionConstants.CLOSE);
}
