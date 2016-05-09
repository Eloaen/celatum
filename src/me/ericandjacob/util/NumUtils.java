package me.ericandjacob.util;

public class NumUtils {

  public static byte[] intToByteArray(int a) {
    return new byte[] {(byte) (a >>> 24), (byte) (a >>> 16), (byte) (a >>> 8), (byte) a};
  }
  
}
