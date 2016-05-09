package me.ericandjacob.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamUtils {
  
  public static int readInt(InputStream is) throws IOException {
    int result = 0;
    result |= (is.read() & 0xFF) << 24;
    result |= (is.read() & 0xFF) << 16;
    result |= (is.read() & 0xFF) << 8;
    result |= is.read() & 0xFF;
    return result;
  }
  
  public static void writeInt(OutputStream os, int val) throws IOException {
    os.write(val >>> 24);
    os.write(val >>> 16);
    os.write(val >>> 8);
    os.write(val);
  }
  
  public static byte[] readByteArray(InputStream is, int length) throws IOException {
    byte[] result = new byte[length];
    int index = 0;
    while (index < length) {
      index += is.read(result, index, length - index);
    }
    return result;
  }
  
}
