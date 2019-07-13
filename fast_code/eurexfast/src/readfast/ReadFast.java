package readfast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.openfast.Message;
import org.openfast.MessageInputStream;
import org.openfast.template.loader.MessageTemplateLoader;
import org.openfast.template.loader.XMLMessageTemplateLoader;

public class ReadFast {
	
	
	
	public static void main(String args[]) throws IOException
	{
        
    	InputStream templateSource = new FileInputStream("data/RDDFastTemplates-1.1.xml");
		XMLMessageTemplateLoader templateLoader = new XMLMessageTemplateLoader();
		templateLoader.setLoadTemplateIdFromAuxId(true);
		templateLoader.load(templateSource);
		
		System.out.println("the tamplate count is" + templateLoader.getTemplateRegistry().getTemplates().length);
			
		InputStream is = new FileInputStream("data/fastdata.bin");
		MessageInputStream mis = new MessageInputStream(is);
		mis.setTemplateRegistry(templateLoader.getTemplateRegistry());
		
		Message md = null;
		int iCount = 0;
		//打开debug追踪开关，显示字段信息
		mis.getContext().setTraceEnabled(true);
		
		while((md = mis.readMessage()) != null)
		{
			System.out.println("the mesage template id is: " + md.getTemplate().getId());
		}
			
	 
		

	}
	
	
}
