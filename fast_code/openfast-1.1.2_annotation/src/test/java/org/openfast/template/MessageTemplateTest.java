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


package org.openfast.template;

import org.openfast.Message;
import org.openfast.codec.FastDecoder;
import org.openfast.codec.FastEncoder;
import org.openfast.test.OpenFastTestCase;


public class MessageTemplateTest extends OpenFastTestCase {
	public void testMessageTemplateWithNoFieldsThatUsePresenceMapStillEncodesPresenceMap() {
		MessageTemplate template = template(
				"<template>" +
				"  <string name=\"string\"/>" +
				"  <decimal name=\"decimal\"><delta/></decimal>" +
				"</template>");
		String encoding = "11000000 10000001 11100001 10000000 10000001";
		
		FastDecoder decoder = decoder(encoding, template);
		FastEncoder encoder = encoder(template);
		
		Message message = decoder.readMessage();
		assertEquals("a", message.getString("string"));
		assertEquals(1.0, message.getDouble("decimal"), 0.1);
		
		assertEquals(encoding, encoder.encode(message));
	}
}
