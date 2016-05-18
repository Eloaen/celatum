package me.ericandjacob;

import java.io.File;
import java.io.IOException;

import me.ericandjacob.data.User;
import me.ericandjacob.file.DataFile;
import me.ericandjacob.gui.Gui;
import me.ericandjacob.gui.PasswordManagerGui;

public class Main {

  public static DataFile dataFile = new DataFile(Main.getDataFilePath());
  private static Gui gui;

  public static void main(String[] args) throws IOException {
    Main.dataFile.loadData();
    boolean debug = false;
    if (debug) {
      Main.dataFile.saveData();
      File file = (args.length > 1 && args[0].equals("file")) ? new File(args[1]) : File.createTempFile("TempFile", "TempFile");
      Main.dataFile = new DataFile(file.getAbsolutePath());
      Main.dataFile.loadData();
      Main.gui = new Gui();
      Main.gui.getBase().setVisible(false);
      if (args.length > 1 && args[0].equals("file")) {
        Main.gui.getBase().setVisible(true);
        return;
      }
      file.deleteOnExit();

      User u = Main.dataFile.getUserList().registerNewUser();
      u.setUsername("TestAccount");
      u.getAccounts().addAccount("Platform", "Username",
          new char[] {'p', 'a', 's', 's', 'w', 'o', 'r', 'd'});
      new PasswordManagerGui(u, "TestAccount", new char[] {'p', 'a', 's', 's', 'w', 'o', 'r', 'd'});
      return;
    }
    Main.gui = new Gui();
  }

  private static String getDataFilePath() {
    String appdata = System.getenv("appdata");
    if (appdata != null) {
      return appdata + "\\EricAndJacob\\PasswordManager.dat";
    }
    return "C:\\EricAndJacobPasswordManager.dat";
  }

  public static Gui getGui() {
    return Main.gui;
  }

}
