package me.ericandjacob.data;

import me.ericandjacob.util.CryptoUtils;

public class Account {

  private String site;
  private char[] password;
  private String username;

  protected Account(String username, String site, char[] password) {
    this.site = site;
    this.password = password;
    this.username = username;
  }

  public String getSite() {
    return this.site;
  }

  public char[] getPassword() {
    return this.password;
  }

  public String getUsername() {
    return this.username;
  }

  public void setSite(String site) {
    this.site = site;
  }

  public void setPassword(char[] password, boolean saveOld) {
    if (!saveOld) {
      CryptoUtils.clearCharArray(this.password);
    }
    this.password = password;
  }

  public void setUsername(String timeStamp) {
    this.username = timeStamp;
  }

  @Override
  public String toString() {
    // TODO Auto-generated method stub
    return new StringBuilder().append("Account[site=").append(this.site).append(",username=").append(this.username).append(",password=]").append(this.password.toString()).toString();
  }
  
}
