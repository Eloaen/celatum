package me.ericandjacob.util;

public class GenUtil {

  public static void logError(String str) {
    System.out.println("[ERROR] " + str);
  }

  public static void logDebug(String str) {
    System.out.print("[DEBUG] ");
    System.out.println(str);
  }
  
  public static char[] charArrayToLowerCase(char[] arr) {
    char[] result = new char[arr.length];
    for (int i = 0; i < result.length; ++i) {
      result[i] = arr[i] > 0x40 && arr[i] < 0x5B ? (char) ((int) arr[i] + 0x20) : arr[i];
    }
    return result;
  }

  public static boolean charArrayEqual(char[] arr, char[] arr1) {

    if (arr.length != arr1.length)
      return false;
    for (int i = 0; i < arr.length; ++i) {
      if (arr[i] != arr1[i])
        return false;
    }
    return true;
  }

}
