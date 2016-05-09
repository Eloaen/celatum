package me.ericandjacob.data;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import me.ericandjacob.util.StreamUtils;

public class UserList {
  
  private ArrayList<User> users;

  public UserList() {
    this.users = new ArrayList<User>();
  }
  
  public User registerNewUser() {
    User user = new User(this);
    this.users.add(user);
    return user;
  }
  
  public User loadUserFromStream(InputStream is) throws IOException {
    int userLength = StreamUtils.readInt(is);
    User user = new User(this, is, userLength);
    this.users.add(user);
    return user;
  }
  
  public boolean hasUsername(String username) {
    return getUser(username).isPresent();
  }
  
  public Optional<User> getUser(String username) {
    for (int i = 0; i < this.users.size(); ++i) {
      if (this.users.get(i).checkUsername(username)) {
        return Optional.of(this.users.get(i));
      }
    }
    return Optional.empty();
  }
  
  public List<User> getUserList() {
    return Collections.unmodifiableList(this.users);
  }
  
}
