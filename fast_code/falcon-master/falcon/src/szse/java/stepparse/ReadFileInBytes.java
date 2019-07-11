package stepparse;

import static stepparse.StepTags.BodyLength;
import static stepparse.StepTags.CheckSum;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import falcon.fix.ParseException;
import falcon.fix.Protocol;

public class ReadFileInBytes {
	


	  private static int sum(ByteBuffer buf) {
		    return sum(buf, 0, buf.position());
		  }

		  private static int sum(ByteBuffer buf, int start, int end) {
		    int result = 0;
		    for (int i = start; i < end; i++)
		      result += buf.get(i);
		    return result;
	  }
		  
		  private static long sumlong(ByteBuffer buf, int start, int end) {
			    long result = 0;
			    int tmp = 0;
			    for (int i = start; i < end; i++)
			    {
			    	tmp = buf.get(i) & 0xFF;
			    	result += tmp;			    	
			    }

			    return result;
			  }
		  
	public static byte[] GetStepMsg(ByteBuffer buffer) throws ParseException, IOException {
		
		//convert write mode to read mode
		byte[] stepbytes = null;
		buffer.flip();

		
		while(buffer.remaining() > 0) {
			 int startoffset = buffer.position();		 
			 byte ch = buffer.get();
			 if (ch == (byte)'8') {
				 ch = buffer.get();
				 if (ch == '=') {
					 //found the step msg
					 //remove the value  
					 ch = buffer.get();
					 if (ch == 'F') {
						 while (buffer.get() != (byte)0x01)
							  ;;
							int bodyLen = Protocol.matchInt(buffer, BodyLength);
							System.out.println("the bodyLen is: " + bodyLen);
							int msgTypeOffset = buffer.position();
							int checksumOffset = msgTypeOffset + bodyLen;
							buffer.position(checksumOffset);
							long sum = sumlong(buffer, startoffset, checksumOffset) ;
							System.out.println("the raw sum is: "+ sum);
						    long checksumActual = sum % 256;
						    System.out.println("the actual sum is: " + checksumActual);
						    int checksumExpected = Protocol.matchInt(buffer, CheckSum);
						    System.out.println("the check sum is: " + checksumExpected);
						      if (checksumExpected != checksumActual) {
							        throw new RuntimeException(String.format("Invalid checksum: expected %d, got: %d", checksumExpected, checksumActual));
							      }
						    int endOffset = buffer.position();
						    
						    
						    //from startoffset to endoffset-1 is integrity step msg
						    buffer.position(startoffset);
						    
						    int stepslen = endOffset - startoffset;
						    stepbytes =new byte[stepslen];
						    int i = 0;
						    while(startoffset < endOffset) {
						    	
						    	stepbytes[i++] = buffer.get();
						    	
						    	startoffset++;
						    }
							FileOutputStream fos = new FileOutputStream(new File("data\\step.dat"), true);
							fos.write(stepbytes);
							fos.close();
						    
					 }
		
					  
				 }
			 }
			 
		}
		
		return stepbytes;
		
	}
	
	public static void main(String args[]) throws ParseException, IOException {
		//open the example raw file
		File origFile = new File("data\\step_mdgw_msgs.log");
		FileInputStream origStream = new FileInputStream(origFile);
		//byte buffer, 
		//byte[] buffer = new byte[1024 * 100];
		//int buflen = 0;
		System.out.println("the file length is: " + origFile.length());
		ByteBuffer buffer = ByteBuffer.allocate((int)origFile.length());
		
		
		byte[] readbuf = new byte[(int)origFile.length()];
		int readnum = 0;
		//read the example raw file, and put read buf into byte buffer
		//(1)should valid integrity of the STEP message
		
		while(true) {
			try {
				readnum = origStream.read(readbuf);
				//put the read bytes into buffer

				//get the step msg
				
				if (readnum == -1) {
					System.out.println("read finished");
					break;
				}
				buffer.put(readbuf, 0, readnum);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//parse the step message
		byte[] res = GetStepMsg(buffer);
		
		

		
		
		
	}

}
