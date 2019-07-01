package org.openfast.scenario;

import java.io.InputStream;

import org.openfast.Message;
import org.openfast.MessageInputStream;
import org.openfast.template.loader.XMLMessageTemplateLoader;
import org.openfast.test.OpenFastTestCase;

public class CmeTemplateTest extends OpenFastTestCase {
	public void testDeltas() throws Exception {
		InputStream templateSource = resource("CME/templates.xml");
		XMLMessageTemplateLoader templateLoader = new XMLMessageTemplateLoader();
		templateLoader.setLoadTemplateIdFromAuxId(true);
		templateLoader.load(templateSource);
		
		InputStream is = resource("CME/messages.fast");
		MessageInputStream mis = new MessageInputStream(is);
		mis.setTemplateRegistry(templateLoader.getTemplateRegistry());
		Message md = mis.readMessage();
		assertEquals(-5025.0, md.getSequence("MDEntries").get(0).getDouble("NetChgPrevDay"), .1);
	}
}
