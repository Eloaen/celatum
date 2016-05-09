package me.ericandjacob.util;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

public class CryptoUtils {

  public static byte[] generateSalt(int length) {
    Random random = new SecureRandom();
    byte[] salt = new byte[length];
    random.nextBytes(salt);
    return salt;
  }

  public static boolean isEqual(byte[] b1, byte[] b2) {
    if (b1.length != b2.length) {
      return false;
    }
    for (int i = 0; i < b1.length; ++i) {
      if ((b1[i] ^ b2[i]) != 0) {
        return false;
      }
    }
    return true;
  }

  public static void clearCharArray(char[] arr) {
    for (int i = 0; i < arr.length; ++i) {
      arr[i] = 0x0;
    }
  }

  public static void clearByteArray(byte[] arr) {
    for (int i = 0; i < arr.length; ++i) {
      arr[i] = 0x0;
    }
  }

  // http://stackoverflow.com/questions/5513144/converting-char-to-byte#9670279
  public static byte[] toBytes(char[] chars) {
    CharBuffer charBuffer = CharBuffer.wrap(chars);
    ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode(charBuffer);
    byte[] bytes =
        Arrays.copyOfRange(byteBuffer.array(), byteBuffer.position(), byteBuffer.limit());
    Arrays.fill(charBuffer.array(), '\u0000'); // clear sensitive data
    Arrays.fill(byteBuffer.array(), (byte) 0); // clear sensitive data
    return bytes;
  }

  public static char[] toChars(byte[] bytes) {
    ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
    CharBuffer charBuffer = StandardCharsets.UTF_8.decode(byteBuffer);
    char[] chars =
        Arrays.copyOfRange(charBuffer.array(), charBuffer.position(), charBuffer.limit());
    Arrays.fill(byteBuffer.array(), (byte) 0);
    Arrays.fill(charBuffer.array(), '\u0000');
    return chars;
  }

  public static byte[] generateHash(String algo, byte[]... bytes) throws NoSuchAlgorithmException {
    MessageDigest digest = MessageDigest.getInstance(algo);
    for (int i = 0; i < bytes.length; ++i) {
      digest.update(bytes[i]);
    }
    return digest.digest();
  }

}
