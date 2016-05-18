package me.ericandjacob.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;


public class VersionChecker {

  private static String version;

  public static boolean checkVersion() throws IOException {

    URL url = new URL("http://xaanit.dx.am/version.html");

    URLConnection con = url.openConnection();
    InputStream is = con.getInputStream();

    BufferedReader br = new BufferedReader(new InputStreamReader(is));

    String line = null;
    String fin = "";
    while ((line = br.readLine()) != null) {
      fin += line;
    }

    VersionChecker.version = fin.replaceAll("[^0-9.]", "");
    return Double.parseDouble(VersionChecker.version) == getVersion();

  }

  public static double getVersion() {
    return 1.4;
  }


  public static String getUpdatedVersion() {
    return version;
  }

}
