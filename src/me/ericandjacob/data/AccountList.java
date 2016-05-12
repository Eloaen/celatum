package me.ericandjacob.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class AccountList {

  private static final Pattern spacePattern = Pattern.compile("\\s+");
  public ArrayList<Account> accounts;

  public AccountList() {
    this.accounts = new ArrayList<Account>();
  }

  /**
   * Creates an account with the provided username, site, and password.
   * 
   * @param username Username of account
   * @param site Site account is used for
   * @param password Password for account
   * @return the added account
   */
  public Account addAccount(String username, String site, char[] password) {
    Account account = new Account(username, site, password);
    this.accounts.add(account);
    return account;
  }

  /**
   * Removes Account <b>account</b>
   * 
   * @param account account to remove
   */
  public void removeAccount(Account account) {
    account.setPassword(null, false);
    account.setSite(null);
    account.setUsername(null);
    this.accounts.remove(account);
  }

  /**
   * Gets a unmodifiable list representing the accounts stored in this {@link AccountList}.
   * 
   * @return List representing accounts
   */
  public List<Account> getAccountsAsList() {
    return Collections.unmodifiableList(this.accounts);
  }

  /**
   * Gets a copied list of accounts in this {@link AccountList}.
   * 
   * @return {@link ArrayList} containing accounts.
   */
  public List<Account> getAccountsCopy() {
    final ArrayList<Account> result = new ArrayList<Account>(this.accounts.size());
    this.accounts.stream().forEach(a -> result.add(a));
    return result;
  }

  public int getLength() {
    return this.accounts.size();
  }

  /**
   * Searches through all accounts' usernames and sites and attempts to match it to any space-split
   * term in parameter site.<br/>
   * If matched, it will sort it based on the number of space-split terms in site and username that
   * can match to any space-split term in parameter site.<br/>
   * The amount of times site is matched is weighted more than the amount of times username is
   * matched.
   * 
   * @param site
   * @return indices of the accounts that were matched
   */
  public ArrayList<Integer> searchFor(String site) {
    String[] strings = spacePattern.split(site.toLowerCase());
    ArrayList<Integer> resultAccounts = new ArrayList<Integer>();
    // Search for any matches
    for (int i = 0; i < this.accounts.size(); ++i) {
      Account acc = this.accounts.get(i);
      for (int j = 0; j < strings.length; ++j) {
        String aSite = acc.getSite().toLowerCase();
        String user = acc.getUsername().toLowerCase();
        if (aSite.startsWith(strings[j]) || user.startsWith(strings[j])) {
          // Match found; Count how many terms are matched in site and user
          int siteTerms = 0;
          int userTerms = 0;
          String[] siteSplit = spacePattern.split(aSite);
          String[] userSplit = spacePattern.split(user);
          for (int k = 0; k < strings.length; ++k) {
            for (int l = 0; l < siteSplit.length; ++l) {
              if (siteSplit[l].startsWith(strings[k]))
                ++siteTerms;
            }
            for (int l = 0; l < userSplit.length; ++l) {
              if (userSplit[l].startsWith(strings[k]))
                ++userTerms;
            }
          }
          resultAccounts.add(i);
          resultAccounts.add(siteTerms);
          resultAccounts.add(userTerms);
          break;
        }
      }
    }

    int temp;
    // Sort according to amount of search parameters that matched any word in the site
    int indicies = resultAccounts.size() - 3;
    for (int i = 0; i <= indicies; i += 3) {
      int siteTerms = resultAccounts.get(i + 1);
      for (int j = indicies; j >= i; j -= 3) {
        if (resultAccounts.get(j + 1) > siteTerms) {
          for (int k = 0; k < 3; ++k) {
            temp = resultAccounts.get(i + k);
            resultAccounts.set(i + k, resultAccounts.get(j + k));
            resultAccounts.set(j + k, temp);
          }
        }
      }
    }

    // Sort groups formed by previous sort by amount of search parameters that matched any word in
    // the username
    for (int i = 0; i < indicies; i += 3) {
      int userTerms = resultAccounts.get(i + 2);
      for (int j = i + 3; j <= indicies; j += 3) {
        if (resultAccounts.get(i + 1) == resultAccounts.get(j + 1)
            && userTerms < resultAccounts.get(j + 2)) {
          for (int k = 0; k < 3; ++k) {
            temp = resultAccounts.get(i + k);
            resultAccounts.set(i + k, resultAccounts.get(j + k));
            resultAccounts.set(j + k, temp);
          }
        }
      }
    }

    // Remove siteTerms and userTerms
    for (int i = 0; i < resultAccounts.size(); ++i) {
      resultAccounts.remove(i + 1);
      resultAccounts.remove(i + 1);
    }

    return resultAccounts;
  }

  /**
   * Return an account whose site exactly matches the given site, ignoring case.
   * 
   * @param site
   * @return the account, if found
   */
  public Optional<Account> searchForExactIgnoreCase(String site) {
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
      builder.append(account)
          .append(account == this.accounts.get(this.accounts.size() - 1) ? "" : ",");
    return builder.append("]").toString();
  }

}
