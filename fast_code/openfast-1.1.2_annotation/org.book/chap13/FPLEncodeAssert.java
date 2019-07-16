package chap13;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.SynchronousQueue;

import org.openfast.ByteUtil;
import org.openfast.Context;
import org.openfast.DecimalValue;
import org.openfast.FieldValue;
import org.openfast.IntegerValue;
import org.openfast.Message;
import org.openfast.MessageInputStream;
import org.openfast.MessageOutputStream;
import org.openfast.SequenceValue;
import org.openfast.StringValue;
import org.openfast.codec.FastEncoder;
import org.openfast.template.MessageTemplate;
import org.openfast.template.Sequence;
import org.openfast.template.loader.MessageTemplateLoader;
import org.openfast.template.loader.XMLMessageTemplateLoader;
import org.openfast.test.OpenFastTestCase;
import org.openfast.util.RecordingInputStream;

public class FPLEncodeAssert extends OpenFastTestCase {
	
    public void testAssertSendingTimeValue() throws Exception {
    	
        InputStream templateSource = resource("FPL/FASTTestTemplate.xml");
        MessageTemplateLoader templateLoader = new XMLMessageTemplateLoader();
        MessageTemplate[] templates = templateLoader.load(templateSource);

        InputStream is = resource("FPL/messages.fast");
        MessageInputStream mis = new MessageInputStream(new RecordingInputStream(is));
        mis.registerTemplate(35, templates[0]);
        Message msg = mis.readMessage();
        //<SendingTime>20061101132712123</SendingTime>
        //在data.xml文件中，字段数据为20061101132712123
        assertEquals(msg.getLong(5), "20061101132712123");
        msg = mis.readMessage();
        //<SendingTime>20061101132712187</SendingTime>
        //在data.xml文件中，字段数据为20061101132712187
        assertEquals(msg.getLong(5), "20061101132712187");
        msg = mis.readMessage();
        assertEquals(msg.getLong(5), "20061101132712204");
        assertEquals(templates[0], msg.getTemplate());
       
    }
}
