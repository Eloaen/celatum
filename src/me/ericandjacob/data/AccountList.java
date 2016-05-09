package me.ericandjacob.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class AccountList {

  private static final Pattern spacePattern = Pattern.compile("(.+)");
  public ArrayList<Account> accounts;

  public AccountList() {
    this.accounts = new ArrayList<Account>();
  }

  public Account addAccount(String username, String site, char[] password) {
    Account account = new Account(username, site, password);
    this.accounts.add(account);
    return account;
  }

  public void removeAccount(Account account) {
    account.setPassword(null, false);
    account.setSite(null);
    account.setUsername(null);
    this.accounts.remove(account);
  }

  public List<Account> getAccountsAsList() {
    return Collections.unmodifiableList(this.accounts);
  }

  public List<Account> getAccountsCopy() {
    final ArrayList<Account> result = new ArrayList<Account>(this.accounts.size());
    this.accounts.stream().forEach(a -> result.add(a));
    return result;
  }

  public int getLength() {
    return this.accounts.size();
  }

  // TODO Improve search
  public Optional<Account> searchForFirst(String site) {
    site = site.toLowerCase().trim();
    for (Account account : this.accounts) {
      String accSite = account.getSite().toLowerCase().trim();
      if (accSite.contains(site) || site.contains(accSite)) {
        return Optional.of(account);
      }
    }
    return Optional.empty();
  }

  public ArrayList<Integer> searchFor(String site) {
    String[] strings = spacePattern.split(site.toLowerCase());
    ArrayList<Integer> resultAccounts = new ArrayList<Integer>();
    for (int i = 0; i < this.accounts.size(); ++i) {
      Stream.Builder<String> builder = Stream.builder();
      Stream<String> stream = builder.add(this.accounts.get(i).getSite().toLowerCase()).add(this.accounts.get(i).getUsername().toLowerCase()).build();
      for (String str : strings) {
        if (stream.anyMatch(s -> s.startsWith(str))) {
          resultAccounts.add(i);
          break;
        }
      }
    }
    return resultAccounts;
  }

  public Optional<Account> searchForExactNoCase(String site) {
    for (Account account : this.accounts) {
      if (account.getSite().toLowerCase().equals(site.toLowerCase())) {
        return Optional.of(account);
      }
    }
    return Optional.empty();
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder().append("AccountList[accounts=");
    for (Account account : this.accounts)
      builder.append(account).append(",");
    return builder.append("]").toString();
  }

}
