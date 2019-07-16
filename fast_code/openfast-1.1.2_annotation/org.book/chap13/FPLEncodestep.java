package chap13;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.openfast.ByteUtil;
import org.openfast.Context;
import org.openfast.DecimalValue;
import org.openfast.FieldValue;
import org.openfast.IntegerValue;
import org.openfast.Message;
import org.openfast.MessageOutputStream;
import org.openfast.SequenceValue;
import org.openfast.StringValue;
import org.openfast.codec.FastEncoder;
import org.openfast.template.MessageTemplate;
import org.openfast.template.Sequence;
import org.openfast.template.loader.XMLMessageTemplateLoader;
import org.openfast.test.OpenFastTestCase;

public class FPLEncodestep extends OpenFastTestCase {
	
	public void testFLPShowTest() throws IOException {
		   File templatesxml = new File("src/test/resources/FPL/FASTTestTemplate.xml");
	    	InputStream   templateSource = new FileInputStream(templatesxml);
	    	
        XMLMessageTemplateLoader templateLoader = new XMLMessageTemplateLoader();
        templateLoader.setLoadTemplateIdFromAuxId(true);
        MessageTemplate[] templates = templateLoader.load(templateSource);
        
        Context context = new Context();
        context.setTemplateRegistry(templateLoader.getTemplateRegistry());
        
        FastEncoder encoder = new FastEncoder(context);
        
        
        //out put byte array
        ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
        MessageOutputStream msgOut = new MessageOutputStream(outByteStream);
        
        OutputStream outStream = null;   
        try {
        	outStream = new FileOutputStream("src/test/resources/FPL//entries.fast");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        
        assertEquals(1,templates.length);
        
        MessageTemplate fpl = templates[0];
        
        msgOut.registerTemplate(Integer.parseInt(fpl.getId()), fpl);
        
        //just one template
        assertEquals(7, fpl.getFieldCount());
        

        Message msg1 = new Message(fpl);
        msg1.setInteger(4, 123456701);
        msg1.setLong(5, 20061101132712123L);
        msg1.getSequence(6);
        
        Sequence mds = fpl.getSequence("MDEntries");
        SequenceValue mktdata1 = new SequenceValue(mds);
       

        mktdata1.add(new FieldValue[] {

        		new IntegerValue(0),

        		new StringValue("0"),

        		new IntegerValue(1),

        		new StringValue("GEZ6"),

        		new DecimalValue(94.325),

        		new IntegerValue(50),

        		new IntegerValue(5),

        		new StringValue("K"),

        		null
            });

        mktdata1.add(new FieldValue[] {

        		new IntegerValue(0),

        		new StringValue("0"),

        		new IntegerValue(2),

        		new StringValue("GEZ6"),

        		new DecimalValue(94.300),

        		new IntegerValue(75),

        		new IntegerValue(7),

        		null,

        		null
            });

        
   
          
        
        mktdata1.add(new FieldValue[] {

        		new IntegerValue(0),

        		new StringValue("0"),

        		new IntegerValue(3),

        		new StringValue("GEZ6"),

        		new DecimalValue(94.275),

        		new IntegerValue(20),

        		new IntegerValue(4),

        		null,

        		null
            });

      
        msg1.setFieldValue(6, mktdata1);
        
    	msgOut.writeMessage(msg1);
    	
    	
    	
    	
    
        msg1.setInteger(4, 123456702);

        msg1.setLong(5, 20061101132712187L);
    	
        msg1.getSequence(6);
         mds = fpl.getSequence("MDEntries");
        mktdata1 = new SequenceValue(mds);
        

      //set the msg1
      mktdata1.add(new FieldValue[] {

      		new IntegerValue(0),

      		new StringValue("1"),

      		new IntegerValue(1),

      		new StringValue("GEZ6"),

      		new DecimalValue(94.350),

      		new IntegerValue(145),

      		new IntegerValue(14),

      		null,

      		null
          });
        
 
      
      //set the msg1
      mktdata1.add(new FieldValue[] {

      		new IntegerValue(0),

      		new StringValue("1"),

      		new IntegerValue(2),

      		new StringValue("GEZ6"),

      		new DecimalValue(94.375),

      		new IntegerValue(120),

      		new IntegerValue(9),

      		null,

      		null
          });

      
      //set the msg1
      mktdata1.add(new FieldValue[] {

      		new IntegerValue(0),

      		new StringValue("1"),

      		new IntegerValue(3),

      		new StringValue("GEZ6"),

      		new DecimalValue(94.400),

      		new IntegerValue(87),

      		new IntegerValue(4),

      		null,

      		null
          });
  
      
      msg1.setFieldValue(6, mktdata1);
  	msgOut.writeMessage(msg1);
  	
  	
  msg1.setInteger(4, 123456703);
  
  msg1.setLong(5, 20061101132712204L);
	
  msg1.getSequence(6);
   mds = fpl.getSequence("MDEntries");
  mktdata1 = new SequenceValue(mds);
  

  //set the msg1
  mktdata1.add(new FieldValue[] {

  		new IntegerValue(0),

  		new StringValue("2"),

  		null,

  		new StringValue("GEZ6"),

  		new DecimalValue(94.325),

  		new IntegerValue(5),

  		null,

  		null,

  		new StringValue("X")
      });

  
  //set the msg1
  mktdata1.add(new FieldValue[] {

  		new IntegerValue(0),

  		new StringValue("2"),

  		null,

  		new StringValue("GEZ6"),

  		new DecimalValue(94.300),

  		new IntegerValue(11),

  		null,

  		null,

  		null
      });

  
  

//set the msg1
  mktdata1.add(new FieldValue[] {

  		new IntegerValue(0),

  		new StringValue("0"),

  		new IntegerValue(1),

  		new StringValue("GEZ6"),

  		new DecimalValue(94.275),

  		new IntegerValue(50),

  		new IntegerValue(5),

  		null,

  		null
      });

  
//set the msg1
  mktdata1.add(new FieldValue[] {

  		new IntegerValue(0),

  		new StringValue("0"),

  		new IntegerValue(2),

  		new StringValue("GEZ6"),

  		new DecimalValue(94.250),

  		new IntegerValue(75),
 
  		new IntegerValue(7),

  		null,

  		null
      });
  
 
//set the msg1
  mktdata1.add(new FieldValue[] {

  		new IntegerValue(1),

  		new StringValue("0"),

  		new IntegerValue(3),

  		new StringValue("GEZ6"),

  		new DecimalValue(94.225),

  		new IntegerValue(20),

  		new IntegerValue(4),

  		null,

  		null
      });
 

  
//set the msg1
  mktdata1.add(new FieldValue[] {

  		new IntegerValue(0),

  		new StringValue("1"),

  		new IntegerValue(1),

  		new StringValue("GEZ6"),

  		new DecimalValue(94.35),

  		new IntegerValue(150),

  		new IntegerValue(15),

  		null,

  		null
      });
  
  
  msg1.setFieldValue(6, mktdata1);
	msgOut.writeMessage(msg1);
  
      
    	//write to 
    	msgOut.close();
    	try {
			outByteStream.writeTo(outStream);
			outByteStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
	}

}
