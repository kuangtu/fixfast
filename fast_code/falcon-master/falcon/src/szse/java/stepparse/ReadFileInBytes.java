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
		for (int i = start; i < end; i++) {
			tmp = buf.get(i) & 0xFF;
			result += tmp;
		}

		return result;
	}

	public static byte[] GetStepMsg(ByteBuffer buffer) throws ParseException, IOException {

		// convert write mode to read mode
		byte[] stepbytes = null;
		buffer.flip();

		while (buffer.remaining() > 0) {
			int startoffset = buffer.position();
			byte ch = 0x0;
			// check the STEP mesage header
			// TODO should check full "FIXT.1.1" text
			if (buffer.get() == (byte) '8' && buffer.get() == (byte) '=' && buffer.get() == (byte) 'F') {
				// 0x01作为STEP消息之间的分隔字符
				while (buffer.get() != (byte) 0x01)
					;
				// 得到消息体长度
				int bodyLen = Protocol.matchInt(buffer, BodyLength);
				// msg type field after body length
				int msgTypeOffset = buffer.position();
				// get the checksum field's offset
				int checksumOffset = msgTypeOffset + bodyLen;
				buffer.position(checksumOffset);
				// compare the checksum
				long sum = sumlong(buffer, startoffset, checksumOffset);
				long checksumActual = sum % 256;
				int checksumExpected = Protocol.matchInt(buffer, CheckSum);
				if (checksumExpected != checksumActual) {
					throw new RuntimeException(
							String.format("Invalid checksum: expected %d, got: %d", checksumExpected, checksumActual));
				}
				// endOffset point to STEP message's next byte
				int endOffset = buffer.position();

				// from startoffset to endoffset-1 is a integrity step message
				buffer.position(startoffset);
				int stepslen = endOffset - startoffset;
				stepbytes = new byte[stepslen];
				int i = 0;
				while (startoffset < endOffset) {
					stepbytes[i++] = buffer.get();
					startoffset++;
				}
				// write the step messages to one file
				FileOutputStream fos = new FileOutputStream(new File("data/step.dat"), true);
				fos.write(stepbytes);
				fos.close();
			}
		}

		return stepbytes;

	}

	public static void main(String args[]) throws ParseException, IOException {
		// open the example raw file
		File origFile = new File("data\\step_mdgw_msgs.log");
		FileInputStream origStream = new FileInputStream(origFile);
		ByteBuffer buffer = ByteBuffer.allocate((int) origFile.length());
		byte[] readbuf = new byte[(int) origFile.length()];
		int readnum = 0;

		// read the example raw file, and put read buf into byte buffer
		// if readnum return -1, the end of file reached

		while (true) {
			try {
				readnum = origStream.read(readbuf);
				// put the read bytes into buffer

				// 到达文件末尾
				if (readnum == -1) {
					break;
				}
				buffer.put(readbuf, 0, readnum);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// parse the step message
		byte[] res = GetStepMsg(buffer);
	}

}
