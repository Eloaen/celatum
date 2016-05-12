package me.ericandjacob.gui;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import me.ericandjacob.data.Account;
import me.ericandjacob.data.AccountList;

public class InformationTableModel extends AbstractTableModel {

  private static final long serialVersionUID = 1L;

  private AccountList accountList;
  private ArrayList<Integer> filter;

  public InformationTableModel(AccountList list) {
    this.accountList = list;
    this.updateFilter();
  }

  @Override
  public int getColumnCount() {
    return 3;
  }

  @Override
  public int getRowCount() {
    return filter.size();
  }

  @Override
  public Object getValueAt(int row, int column) {
    Account account = accountList.getAccountsAsList().get(filter.get(row));
    return column == 0 ? account.getSite()
        : column == 1 ? account.getUsername() : account.getPassword();
  }

  @Override
  public String getColumnName(int column) {
    return column == 0 ? "Platform" : column == 1 ? "Username" : "Password";
  }

  public void updateFilter() {
    this.filter = new ArrayList<Integer>();
    for (int i = 0; i < accountList.getLength(); ++i) {
      this.filter.add(i);
    }
    this.fireTableDataChanged();
  }

  public void updateFilter(String filter) {
    this.filter = accountList.searchFor(filter);
    this.fireTableDataChanged();
  }

  public void setAccountList(AccountList accountList) {
    this.accountList = accountList;
    this.updateFilter();
  }
  
}
