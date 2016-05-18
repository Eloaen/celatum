package me.ericandjacob.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import me.ericandjacob.data.User;
import me.ericandjacob.data.UserList;
import me.ericandjacob.util.StreamUtils;

public class DataFile {

  private File location;
  private UserList users;

  public DataFile(String location) {
    this.location = new File(location);
    System.out.println(location);
  }

  /**
   * Attempts to load the DataFile into memory
   * 
   * @return success
   */
  public boolean loadData() {
    try {
      Optional<String> err = checkForAndCreateFile();
      if (err.isPresent()) {
        System.out.println(err.get());
        return false;
      }
      if (this.users != null) {
        return false;
      }
      if (this.location.length() < 4) {
        this.users = new UserList();
        this.saveData();
      }
      FileInputStream fis = new FileInputStream(this.location);
      this.users = new UserList();
      int amountUsers = StreamUtils.readInt(fis);
      for (int i = 0; i < amountUsers; ++i) {
        this.users.loadUserFromStream(fis);
      }
      fis.close();
      return false;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean saveData() {
    try {
      Optional<String> err = checkForAndCreateFile();
      if (err.isPresent()) {
        System.out.println(err.get());
        return false;
      }
      if (this.users == null) {
        return false;
      }
      FileOutputStream fos = new FileOutputStream(this.location);
      List<User> userList = this.users.getUserList();
      StreamUtils.writeInt(fos, userList.size());
      for (int i = 0; i < userList.size(); ++i) {
        if (userList.get(i).isDecrypted()) {
          fos.close();
          this.location.delete();
          throw new IllegalArgumentException("All users in the UserList must be encrypted.");
        }
        userList.get(i).writeUserToStream(fos);
      }
      fos.close();
      return true;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }

  public UserList getUserList() {
    return this.users;
  }

  private Optional<String> checkForAndCreateFile() throws IOException {
    if (this.location.isFile()) {
      return Optional.empty();
    }
    if (this.location.isDirectory()) {
      return Optional.of("location must not be a directory");
    }
    File parent = this.location.getParentFile();
    for (int i = 0; i < 10 || parent.mkdirs(); ++i);
    if (!parent.exists()) {
      return Optional.of("unable to create directory");
    }
    if (!this.location.createNewFile()) {
      return Optional.of("unable to create file");
    }
    return Optional.empty();
  }

}
