package falcon.fix;

import static falcon.fix.MessageTypes.*;

import java.nio.ByteBuffer;

/**
 * On-wire protocol parsing and formatting.
 */
public class Protocol {

  /*
   * Maximum message header size in bytes.
   */
  public static final int MAX_HEADER_SIZE = 64;

  /*
   * Maximum message body size in bytes.
   */
  public static final int MAX_BODY_SIZE = 4096;

  /**
   * Format field to on-wire format.
   */
  public static void format(ByteBuffer buf, int tag, byte[] value) {
    writeInt(buf, tag);
    buf.put((byte) '=');
    buf.put(value);
    buf.put((byte) 0x01);
  }

  /**
   * Format field to on-wire format.
   */
  public static void formatInt(ByteBuffer buf, int tag, int value) {
    writeInt(buf, tag);
    buf.put((byte) '=');
    writeInt(buf, value);
    buf.put((byte) 0x01);
  }

  public static void formatString(ByteBuffer buf, int tag, String value) {
    writeInt(buf, tag);
    buf.put((byte) '=');
    for (int i = 0; i < value.length(); i++) {
      buf.put((byte) value.charAt(i));
    }
    buf.put((byte) 0x01);
  }

  public static void formatCheckSum(ByteBuffer buf, int tag, int value) {
    writeInt(buf, tag);
    buf.put((byte) '=');
    if (value < 10) {
      buf.put((byte) '0');
    }
    if (value < 100) {
      buf.put((byte) '0');
    }
    writeInt(buf, value);
    buf.put((byte) 0x01);
  }

  public static void writeInt(ByteBuffer buf, int n) {
    if (n < 0) {
      buf.put((byte) '-');
    }
    n = Math.abs(n);
    int start = buf.position();
    do {
      buf.put((byte)('0' + n % 10));
      n /= 10;
    } while (n > 0);
    int end = buf.position();
    int i = start;
    int j = end - 1;
    while (i < j) {
      byte tmp = buf.get(i);
      buf.put(i, buf.get(j));
      buf.put(j, tmp);
      i++; j--;
    }
  }

  public static void match(ByteBuffer buf, int tag) throws ParseException {
    matchTag(buf, tag);
    while (buf.get() != (byte)0x01)
      ;;
  }

  public static int matchInt(ByteBuffer buf, int tag) throws ParseException {
    matchTag(buf, tag);
    return parseInt(buf, (byte)0x01);
  }

  public static int parseInt(ByteBuffer buf, byte delimiter) {
	//如果以'-'开始,则认为是负数
    int sign = 1;
    if (buf.get(buf.position()) == (byte)'-') {
      buf.get();
      sign = -1;
    }
    int result = 0;
    //读取byte buffer知道碰到delimiter
    //此时buf的位置指向了delimiter之后的一个字节
    for (;;) {
      byte ch = buf.get();
      if (ch == delimiter) {
        break;
      }
      //最先读取的是整数的高位,需要每次*10移位
      result *= 10;
      //如果byte中存放的字母'1',ascii码为49,减去'0',得到整数1
      result += (byte)ch - (byte)'0';
    }
    //返回整数
    return sign * result;
  }

  public static ByteString parseString(ByteBuffer buf, byte delimiter) {
	//开始处理时buffer的位置
    int start = buf.position();
    for (;;) {
      byte ch = buf.get();
      //直到delimiter位置
      //此时buffer中的position为delimiter之后的位置
      if (ch == delimiter) {
        break;
      }
    }
    //此时end指向了delimiter
    int end = buf.position() - 1;
    //将position指向了start开始位置
    buf.position(start);
    //end - start为delimiter之前byte字符的数目length
    //读取从start开始length长度字节数组,保存在ByteString对象
    return ByteString.of(buf, end-start);
  }

  public static MessageType matchMsgType(ByteBuffer buf) throws ParseException {
    matchTag(buf, Tags.MsgType);
    MessageType result = null;
    switch (buf.get()) {
    case (byte)'0': result = Heartbeat;       break;
    case (byte)'1': result = TestRequest;     break;
    case (byte)'2': result = ResendRequest;   break;
    case (byte)'3': result = Reject;          break;
    case (byte)'4': result = SequenceReset;   break;
    case (byte)'5': result = Logout;          break;
    case (byte)'8': result = ExecutionReport; break;
    case (byte)'A': result = Logon;           break;
    case (byte)'D': result = NewOrderSingle;  break;
    case (byte)0x01:
      throw new ParseFailedException("Invalid MsgType (35)");
    default:
      throw new ParseFailedException("Tag specified without a value");
    }
    if (buf.get() != (byte)0x01) {
      throw new ParseFailedException("Invalid MsgType (35)");
    }
    return result;
  }

  public static void matchTag(ByteBuffer buf, int tag) throws ParseException {
	//从buffer中读取整数,直到'='
    int actual = parseInt(buf, (byte)'=');
    //如果与给定的tag不同,异常
    if (actual != tag) {
      throw new ParseFailedException("Required tag missing");
    }
  }
}
