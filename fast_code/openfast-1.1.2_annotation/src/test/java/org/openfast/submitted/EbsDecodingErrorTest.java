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
package org.openfast.submitted;

import java.io.ByteArrayInputStream;

import org.openfast.ByteUtil;
import org.openfast.Context;
import org.openfast.Message;
import org.openfast.codec.FastDecoder;
import org.openfast.template.loader.XMLMessageTemplateLoader;
import org.openfast.test.OpenFastTestCase;

public class EbsDecodingErrorTest extends OpenFastTestCase {
    private Context context;

    protected void setUp() throws Exception {
        XMLMessageTemplateLoader loader = new XMLMessageTemplateLoader();
        loader.setLoadTemplateIdFromAuxId(true);
        loader.load(resource("EBS/templates.xml"));
        context = new Context();
        context.setTemplateRegistry(loader.getTemplateRegistry());
    }

    public void testDecodingScenario1() {
        byte[] encoded = ByteUtil.convertHexStringToByteArray("C0 81 01 F9 C0 F8 FE 87 1C 50 54 FC BF 06 6B C1 "
                + "06 6B C2 82 32 7B BA 81 B2 B2 81 FD 06 62 A0 D0 B8");
        FastDecoder decoder = new FastDecoder(context, new ByteArrayInputStream(encoded));

        Message versionInformationMessage = decoder.readMessage();
        assertEquals(249, versionInformationMessage.getInt("versNo"));

        Message resetMessage = decoder.readMessage();
        assertEquals(context.getTemplateRegistry().get("ResetMessage"), resetMessage.getTemplate());
        decoder.reset();
        Message orderBookDeltaInformationMessage = decoder.readMessage();
        assertEquals(1, orderBookDeltaInformationMessage.getSequence("entries").getLength());
    }

    public void testDecodingScenario2() {
        byte[] encoded = ByteUtil.convertHexStringToByteArray("C0 81 01 F9 C0 F8 FE 87 12 61 54 C8 C0 07 77 "
                + "F2 07 77 F3 82 33 22 C1 82 B2 B2 82 FD 06 31 E3 03 BE B1 B2 B2 81 FD 06 31 E3 03 BE B7");
        FastDecoder decoder = new FastDecoder(context, new ByteArrayInputStream(encoded));

        Message versionInformationMessage = decoder.readMessage();
        assertEquals(249, versionInformationMessage.getInt("versNo"));

        Message resetMessage = decoder.readMessage();
        assertEquals(context.getTemplateRegistry().get("ResetMessage"), resetMessage.getTemplate());

        decoder.reset();
        Message orderBookDeltaInformationMessage = decoder.readMessage();
        assertEquals(2, orderBookDeltaInformationMessage.getSequence("entries").getLength());
    }

    public void testDecodingScenario3() {
        byte[] encoded = ByteUtil.convertHexStringToByteArray("C0 81 01 F9 C0 F8 FE 87 14 63 7F 98 BF 0C 03 "
                + "8F 0C 03 90 82 33 0E FD 82 B2 B1 84 FD 06 5F BD 02 DD B1 B2 B1 85 FD 06 5F C2 04 C1 B1");
        FastDecoder decoder = new FastDecoder(context, new ByteArrayInputStream(encoded));

        Message versionInformationMessage = decoder.readMessage();
        assertEquals(249, versionInformationMessage.getInt("versNo"));

        Message resetMessage = decoder.readMessage();
        assertEquals(context.getTemplateRegistry().get("ResetMessage"), resetMessage.getTemplate());

        decoder.reset();
        Message orderBookDeltaInformationMessage = decoder.readMessage();
        assertEquals(2, orderBookDeltaInformationMessage.getSequence("entries").getLength());
    }
}
