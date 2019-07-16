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
package org.openfast.test;

import org.openfast.DecimalValue;
import org.openfast.FieldValue;
import org.openfast.GroupValue;
import org.openfast.IntegerValue;
import org.openfast.Message;
import org.openfast.ScalarValue;
import org.openfast.SequenceValue;
import org.openfast.StringValue;
import org.openfast.template.DynamicTemplateReference;
import org.openfast.template.Field;
import org.openfast.template.Group;
import org.openfast.template.MessageTemplate;
import org.openfast.template.Scalar;
import org.openfast.template.Sequence;
import org.openfast.template.StaticTemplateReference;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.Type;

public class ObjectMother {
    private static MessageTemplate quoteTemplate;
    private static MessageTemplate allocationInstruction;
    private static Group instrument;
    private static Sequence allocations;
    private static MessageTemplate batchTemplate;
    private static MessageTemplate headerTemplate;
    public static final int QUOTE_TEMPLATE_ID = 10;
    public static final int ALLOC_INSTRCTN_TEMPLATE_ID = 25;

    public static MessageTemplate quoteTemplate() {
        if (quoteTemplate == null) {
            quoteTemplate = new MessageTemplate("Quote", new Field[] {
                    new Scalar("bid", Type.DECIMAL, Operator.DELTA, ScalarValue.UNDEFINED, false),
                    new Scalar("ask", Type.DECIMAL, Operator.DELTA, ScalarValue.UNDEFINED, false) });
        }
        return quoteTemplate;
    }

    public static MessageTemplate batchTemplate() {
        if (batchTemplate == null) {
            batchTemplate = new MessageTemplate("Batch", new Field[] { new StaticTemplateReference(headerTemplate()),
                    new Sequence("Batch", new Field[] { DynamicTemplateReference.INSTANCE }, false) });
        }
        return batchTemplate;
    }

    public static MessageTemplate headerTemplate() {
        if (headerTemplate == null) {
            headerTemplate = new MessageTemplate("Header", new Field[] { new Scalar("Sent", Type.U32, Operator.DELTA,
                    ScalarValue.UNDEFINED, false) });
        }
        return headerTemplate;
    }

    public static Message quote(double bid, double ask) {
        Message quote = new Message(quoteTemplate());
        quote.setDecimal(1, bid);
        quote.setDecimal(2, ask);
        return quote;
    }

    public static Message newAllocInstrctn(String id, int side, double quantity, double averagePrice, GroupValue instrument,
            SequenceValue allocations) {
        Message allocInstrctn = new Message(allocationInstruction());
        allocInstrctn.setFieldValue(1, allocations);
        allocInstrctn.setFieldValue(2, instrument);
        allocInstrctn.setFieldValue(3, new StringValue(id));
        allocInstrctn.setFieldValue(4, new IntegerValue(side));
        allocInstrctn.setFieldValue(5, new DecimalValue(quantity));
        allocInstrctn.setFieldValue(6, new DecimalValue(averagePrice));
        return allocInstrctn;
    }

    public static MessageTemplate allocationInstruction() {
        if (allocationInstruction == null) {
            allocationInstruction = new MessageTemplate("AllocInstrctn", new Field[] { allocations(), instrument(),
                    new Scalar("ID", Type.ASCII, Operator.DELTA, ScalarValue.UNDEFINED, false),
                    new Scalar("Side", Type.U32, Operator.COPY, ScalarValue.UNDEFINED, false),
                    new Scalar("Quantity", Type.DECIMAL, Operator.DELTA, ScalarValue.UNDEFINED, false),
                    new Scalar("Average Price", Type.DECIMAL, Operator.DELTA, ScalarValue.UNDEFINED, false) });
        }
        return allocationInstruction;
    }

    public static Sequence allocations() {
        if (allocations == null) {
            allocations = new Sequence("Allocations", new Field[] {
                    new Scalar("Account", Type.ASCII, Operator.COPY, ScalarValue.UNDEFINED, false),
                    new Scalar("Price", Type.DECIMAL, Operator.DELTA, ScalarValue.UNDEFINED, false),
                    new Scalar("Quantity", Type.DECIMAL, Operator.DELTA, ScalarValue.UNDEFINED, false),
                    new Scalar("Average Price", Type.DECIMAL, Operator.DELTA, ScalarValue.UNDEFINED, false) }, false);
        }
        return allocations;
    }

    private static Group instrument() {
        if (instrument == null) {
            instrument = new Group("Instrmt", new Field[] {
                    new Scalar("Symbol", Type.ASCII, Operator.COPY, ScalarValue.UNDEFINED, false),
                    new Scalar("MMY", Type.ASCII, Operator.DELTA, ScalarValue.UNDEFINED, false), }, false);
        }
        return instrument;
    }

    public static GroupValue newInstrument(String symbol, String mmy) {
        return new GroupValue(instrument(), new FieldValue[] { new StringValue(symbol), new StringValue(mmy) });
    }

    public static GroupValue newAllocation(String account, double price, double quantity) {
        StringValue acct = account != null ? new StringValue(account) : null;
        return new GroupValue(allocations().getGroup(), new FieldValue[] { acct, new DecimalValue(price), new DecimalValue(quantity),
                new DecimalValue(0.0) });
    }

    public static Message basicAllocationInstruction() {
        return newAllocInstrctn("abcd1234", 2, 25.0, 102.0, basicInstrument(), basicAllocations());
    }

    private static SequenceValue basicAllocations() {
        SequenceValue value = new SequenceValue(allocationInstruction().getSequence("Allocations"));
        value.add(newAllocation("general", 101.0, 15.0));
        value.add(newAllocation("specific", 103.0, 10.0));
        return value;
    }

    private static GroupValue basicInstrument() {
        return newInstrument("IBM", "200301");
    }
}
