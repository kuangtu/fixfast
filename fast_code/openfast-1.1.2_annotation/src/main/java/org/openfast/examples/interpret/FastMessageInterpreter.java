/*
 * Contributed to OpenFAST by Object Computing, Inc.
 */
package org.openfast.examples.interpret;

import org.openfast.FieldValue;
import org.openfast.GroupValue;
import org.openfast.Message;
import org.openfast.SequenceValue;
import org.openfast.examples.util.FastMessageConsumer;
import org.openfast.template.Field;
import org.openfast.template.Group;
import org.openfast.template.MessageTemplate;
import org.openfast.template.Sequence;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Accept incoming, decoded FAST messages and display them on an outputStream.
 * The field names, tags (ID's) and structure of the messages come from the
 * template used to decode the message.
 */
public class FastMessageInterpreter implements FastMessageConsumer {
    private final PrintStream outputStream;
    private final String tab = "  ";
    private String indent = "";
    private long recordCount = 0L;

    /**
     * Construct the consumer given an output stream to receive the results.
     * @param outputStream where to write the results.
     */
    public FastMessageInterpreter(OutputStream outputStream) {
        this.outputStream = new PrintStream(outputStream);
    }

    /**
     * Accept each incoming message.
     * Formats the message for display and writes to the output stream
     * given to the constructor of this object.
     *
     * @param message A decoded FAST message
     */
    public void accept(Message message) {
//        outputStream.println(message.toString());
        recordCount += 1;
        StringBuffer line = new StringBuffer(indent + "Record#" + recordCount + " ");
        formatMessage(message, line);
        outputStream.println(line);
    }

    /**
     * Display additional information related to the message.
     * @param message the annotation.
     */
    public void annotate(String message) {
        outputStream.println(message);
    }

    private void formatMessage(Message message, StringBuffer line) {
        MessageTemplate template = message.getTemplate();
        line.append("Message template: ").append(template.getName()).append("->");
        boolean needNewLine = false;
        int count = message.getFieldCount();
        for(int i = 0; i < count; ++i){
            if( message.isDefined(i)){ // skip missing optional fields
                Field field = template.getField(i);
                if(field instanceof Sequence){
                    formatSequence(field, message.getSequence(i), line);
                    needNewLine = true;
                }
                else{
                    String fieldName = field.getName();
                    String fieldId = field.getId();
                    String fieldValue = message.getValue(i).toString();
                    if(needNewLine){
                        line.append("\n").append(indent);
                        needNewLine = false;
                    }
                    /* 1.5
                    line.append(String.format("%s{%s}=%s ",
                        fieldName,
                        fieldId,
                        fieldValue)
                    );
                    */
                    line.append(fieldName).
                        append('{').append(fieldId).append("}=").
                        append(fieldValue);
                }
            }
        }
    }

    private void formatSequence(Field sequenceField, SequenceValue sequenceValue, StringBuffer line){
        // get the (template) definition of this sequence
        Sequence sequence = sequenceValue.getSequence();
        Group group = sequence.getGroup();
        int fieldCount = group.getFieldCount();
        String sequenceName = sequence.getName();

        // Get the entries in this sequence
        GroupValue[] entries = sequenceValue.getValues();

        String oldIndent = indent;
        indent = indent + tab;
/*1.5
        line.append(String.format("\n%s%s{%s} Sequence[%d]:",
            indent, sequenceName, sequenceField.getId(), entries.length));
*/
        line.append("\n").append(indent).append(sequenceName).
                append('{').append(sequenceField.getId()).append("} Sequence[").
                append(entries.length).append("]:");
        indent = indent + tab;

        boolean needNewLine = false;
        for (int i = 0; i < entries.length; i++) {
            GroupValue entry = entries[i];
            line.append("\n").append(indent);
            // for each entry, iterate through the possible fields
            for (int nField = 0; nField < fieldCount; ++nField) {
                if (entry.isDefined(nField)) { // skip optional+missing fields
                    Field field = sequence.getField(nField);        // definition
                    if (field instanceof Sequence) {
                        formatSequence(field, entry.getSequence(nField), line);
                        needNewLine = true;
                    } else {
                        if (needNewLine) {
                            line.append("\n").append(indent);
                        }
                        String fieldName = field.getName();             // name
                        String fieldId = field.getId();                 // id (aka TAG)
                        FieldValue fieldValue = entry.getValue(nField); // value
                        /*1.5
                        line.append(String.format("%s{%s}=%s ",
                                fieldName,
                                fieldId,
                                fieldValue.toString()));
                        */                                
                        line.append(fieldName).append('{').
                                append(fieldId).append("}=").
                                append(fieldValue.toString()).append("|");
                    }
                }
            }
        }
        indent = oldIndent;
    }
}
