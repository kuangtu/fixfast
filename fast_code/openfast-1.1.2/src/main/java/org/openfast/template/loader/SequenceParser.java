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

import org.openfast.Global;
import org.openfast.QName;
import org.openfast.ScalarValue;
import org.openfast.template.Field;
import org.openfast.template.Scalar;
import org.openfast.template.Sequence;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.Type;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class SequenceParser extends AbstractFieldParser {
    private ScalarParser sequenceLengthParser = new ScalarParser("length") {
        protected Type getType(Element fieldNode, ParsingContext context) {
            return Type.U32;
        }

        protected QName getName(Element fieldNode, ParsingContext context) {
            if (context.getName() == null)
                return Global.createImplicitName(context.getParent().getName());
            return context.getName();
        }
    };

    public SequenceParser() {
        super("sequence");
    }

    protected Field parse(Element sequenceElement, boolean optional, ParsingContext context) {
        Sequence sequence = new Sequence(context.getName(), parseSequenceLengthField(context.getName(), sequenceElement, optional,
                context), GroupParser.parseFields(sequenceElement, context), optional);
        GroupParser.parseMore(sequenceElement, sequence.getGroup(), context);
        return sequence;
    }

    /**
     * 
     * @param name
     * @param sequence
     *            The dom element object
     * @param sequenceName
     *            Name of the sequence to which this lenght field belongs
     * @param optional
     *            Determines if the Scalar is required or not for the data
     * @return Returns null if there are no elements by the tag length,
     *         otherwise
     */
    private Scalar parseSequenceLengthField(QName name, Element sequence, boolean optional, ParsingContext parent) {
        NodeList lengthElements = sequence.getElementsByTagName("length");
        if (lengthElements.getLength() == 0) {
            Scalar implicitLength = new Scalar(Global.createImplicitName(name), Type.U32, Operator.NONE, ScalarValue.UNDEFINED,
                    optional);
            implicitLength.setDictionary(parent.getDictionary());
            return implicitLength;
        }
        Element length = (Element) lengthElements.item(0);
        ParsingContext context = new ParsingContext(length, parent);
        return (Scalar) sequenceLengthParser.parse(length, optional, context);
    }
}
