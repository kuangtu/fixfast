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


package org.openfast;

import junit.framework.TestCase;

import org.openfast.template.Field;
import org.openfast.template.Group;
import org.openfast.template.MessageTemplate;
import org.openfast.template.Scalar;
import org.openfast.template.Sequence;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.Type;

import org.openfast.test.ObjectMother;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;


public class EncodeDecodeTest extends TestCase {
    public void testComplexMessage() throws Exception {
        MessageTemplate template = new MessageTemplate("Company",
            new Field[] {
                new Scalar("Name", Type.STRING, Operator.NONE, ScalarValue.UNDEFINED, false),
                new Scalar("Id", Type.U32, Operator.INCREMENT, ScalarValue.UNDEFINED, false),
                new Sequence("Employees",
                    new Field[] {
                        new Scalar("First Name", Type.STRING, Operator.COPY, ScalarValue.UNDEFINED, false),
                        new Scalar("Last Name", Type.STRING, Operator.COPY, ScalarValue.UNDEFINED, false),
                        new Scalar("Age", Type.U32, Operator.DELTA, ScalarValue.UNDEFINED, false)
                    }, false),
                new Group("Tax Information",
                    new Field[] {
                        new Scalar("EIN", Type.STRING, Operator.NONE, ScalarValue.UNDEFINED, false)
                    }, false)
            });
        Message aaaInsurance = new Message(template);
        aaaInsurance.setFieldValue(1, new StringValue("AAA Insurance"));
        aaaInsurance.setFieldValue(2, new IntegerValue(5));

        SequenceValue employees = new SequenceValue(template.getSequence(
                    "Employees"));
        employees.add(new FieldValue[] {
                new StringValue("John"), new StringValue("Doe"),
                new IntegerValue(45)
            });
        employees.add(new FieldValue[] {
                new StringValue("Jane"), new StringValue("Doe"),
                new IntegerValue(48)
            });
        aaaInsurance.setFieldValue(3, employees);
        aaaInsurance.setFieldValue(4,
            new GroupValue(template.getGroup("Tax Information"),
                new FieldValue[] { new StringValue("99-99999999") }));

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        MessageOutputStream out = new MessageOutputStream(outStream);
        out.registerTemplate(1, template);
        out.writeMessage(aaaInsurance);

        Message abcBuilding = new Message(template);
        abcBuilding.setFieldValue(1, new StringValue("ABC Building"));
        abcBuilding.setFieldValue(2, new IntegerValue(6));
        employees = new SequenceValue(template.getSequence("Employees"));
        employees.add(new FieldValue[] {
                new StringValue("Bob"), new StringValue("Builder"),
                new IntegerValue(3)
            });
        employees.add(new FieldValue[] {
                new StringValue("Joe"), new StringValue("Rock"),
                new IntegerValue(59)
            });
        abcBuilding.setFieldValue(3, employees);
        abcBuilding.setFieldValue(4,
            new GroupValue(template.getGroup("Tax Information"),
                new FieldValue[] { new StringValue("99-99999999") }));
        out.writeMessage(abcBuilding);

        MessageInputStream in = new MessageInputStream(new ByteArrayInputStream(
                    outStream.toByteArray()));
        in.registerTemplate(1, template);

        GroupValue message = in.readMessage();
        assertEquals(aaaInsurance, message);

        message = in.readMessage();
        assertEquals(abcBuilding, message);
    }

    public void testMultipleMessages() {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        MessageOutputStream out = new MessageOutputStream(outStream);
        out.registerTemplate(ObjectMother.ALLOC_INSTRCTN_TEMPLATE_ID,
            ObjectMother.allocationInstruction());

        SequenceValue allocations = new SequenceValue(ObjectMother.allocationInstruction()
                                                                  .getSequence("Allocations"));
        allocations.add(ObjectMother.newAllocation("fortyFiveFund", 22.5, 75.0));
        allocations.add(ObjectMother.newAllocation("fortyFund", 24.6, 25.0));

        Message ai1 = ObjectMother.newAllocInstrctn("ltg0001", 1, 100.0, 23.4,
                ObjectMother.newInstrument("CTYA", "200910"), allocations);

        allocations = new SequenceValue(ObjectMother.allocationInstruction()
                                                    .getSequence("Allocations"));
        allocations.add(ObjectMother.newAllocation("fortyFiveFund", 22.5, 75.0));
        allocations.add(ObjectMother.newAllocation("fortyFund", 24.6, 25.0));

        Message ai2 = ObjectMother.newAllocInstrctn("ltg0001", 1, 100.0, 23.4,
                ObjectMother.newInstrument("CTYA", "200910"), allocations);

        allocations = new SequenceValue(ObjectMother.allocationInstruction()
                                                    .getSequence("Allocations"));
        allocations.add(ObjectMother.newAllocation("fortyFiveFund", 22.5, 75.0));
        allocations.add(ObjectMother.newAllocation("fortyFund", 24.6, 25.0));

        Message ai3 = ObjectMother.newAllocInstrctn("ltg0001", 1, 100.0, 23.4,
                ObjectMother.newInstrument("CTYA", "200910"), allocations);

        out.writeMessage(ai1);
        out.writeMessage(ai2);
        out.writeMessage(ai3);

        byte[] bytes = outStream.toByteArray();
        MessageInputStream in = new MessageInputStream(new ByteArrayInputStream(
                    bytes));
        in.registerTemplate(ObjectMother.ALLOC_INSTRCTN_TEMPLATE_ID,
            ObjectMother.allocationInstruction());

        Message message = in.readMessage();
        assertEquals(ai1, message);
        message = in.readMessage();
        assertEquals(ai2, message);
        assertEquals(ai3, in.readMessage());
    }
}
