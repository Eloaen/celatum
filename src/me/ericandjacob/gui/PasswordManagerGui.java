package me.ericandjacob.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableCellRenderer;

import me.ericandjacob.Main;
import me.ericandjacob.data.User;
import me.ericandjacob.util.CryptoUtils;



public class PasswordManagerGui {

  private User user;
  private String name;
  private char[] password;
  public JFrame base;
  private Screen currentScreen;
  private JTable information2;
  private JTextField search;
  private InformationTableModel model;

  public PasswordManagerGui(User user, String name, char[] password) {
    this.user = user;
    this.name = name;
    this.password = password;

    /* Making Things */


    JPanel addPassword = new JPanel();
    JPanel mainScreen = new JPanel();
    Screen addPasswordObject = new Screen(addPassword, new Dimension(400, 200));
    Screen mainScreenObject = new Screen(mainScreen, new Dimension(340, 400));
    mainScreen.setLayout(null);
    addPassword.setLayout(null);

    search = new JTextField();

    JLabel username = new JLabel("Username: " + this.name);
    JLabel howManyPass = new JLabel(
        "You have [" + Integer.toString(this.user.getAccounts().getLength()) + "] password(s).");
    JLabel searchInfo = new JLabel("Search: ");
    JLabel informationPlatform = new JLabel("Platform: ");
    JLabel informationUsername = new JLabel("Username: ");
    JLabel informationPassword = new JLabel("Password: ");

    Button addPass = new Button("Add", 100, 100, true);
    Button back = new Button("Back", 100, 100, true);
    Button confirmAdd = new Button("Add", 100, 100, true);

    model = new InformationTableModel(this.user.getAccounts());
    TableCellRenderer renderer = new PasswordCellRenderer();
    information2 = new JTable(model) {
      private static final long serialVersionUID = 1L;

      @Override
      public TableCellRenderer getCellRenderer(int row, int column) {
        if (information2.getColumnName(column).equals("Password")) {
          return renderer;
        }
        return super.getCellRenderer(row, column);
      }
    };
    JScrollPane information = new JScrollPane(information2);

    JTextField platformEnter = new JTextField();
    JTextField usernameEnter = new JTextField();
    JPasswordField passwordEnter = new JPasswordField();

    /* Set generic screen things */

    this.base = new JFrame("Celatum");
    this.base.setResizable(false);

    mainScreen.add(username);
    mainScreen.add(howManyPass);
    mainScreen.add(addPass);
    mainScreen.add(search);
    mainScreen.add(information);
    mainScreen.add(searchInfo);


    addPass.setVisible(true);
    addPass.setSize(70, 25);
    addPass.setLocation(260, 1);
    setLabelSize(username);
    setLabelSize(howManyPass);
    username.setLocation(1, 1);
    howManyPass.setLocation(1, 13);

    search.setSize(150, 28);
    search.getDocument().addDocumentListener(documentListener);
    search.setLocation(50, 330);
    setLabelSize(searchInfo);
    searchInfo.setLocation(10, 330);

    information.putClientProperty(JTable.AUTO_RESIZE_ALL_COLUMNS, true);
    information.putClientProperty(JTable.WHEN_FOCUSED, false);
    information.setSize(new Dimension(300, 300));
    information2.setSize(information2.getSize());
    information.setLocation(10, 30);
    information.setVisible(true);

    changeScreenOnButtonPress(addPass, addPasswordObject, platformEnter);
    changeScreenOnButtonPress(back, mainScreenObject);

    // Add Password
    addPassword.add(platformEnter);
    addPassword.add(usernameEnter);
    addPassword.add(passwordEnter);
    addPassword.add(confirmAdd);
    addPassword.add(informationPlatform);
    addPassword.add(informationUsername);
    addPassword.add(informationPassword);
    addPassword.add(back);

    setLabelSize(informationPlatform);
    setLabelSize(informationUsername);
    setLabelSize(informationPassword);


    platformEnter.setSize(new Dimension(115, 20));
    platformEnter.setLocation(150, 10);
    informationPlatform.setLocation(100, 13);

    back.setSize(new Dimension(70, 20));
    back.setLocation(300, 1);

    usernameEnter.setSize(new Dimension(115, 20));
    usernameEnter.setLocation(150, 40);
    informationUsername.setLocation(90, 43);

    passwordEnter.setSize(new Dimension(115, 20));
    passwordEnter.setLocation(150, 70);
    passwordEnter.setEchoChar('\u0000');
    informationPassword.setLocation(90, 73);

    confirmAdd.setSize(new Dimension(70, 20));
    confirmAdd.setLocation(170, 100);

    confirmAdd.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        if (passwordEnter.getPassword().length == 0) {
          JOptionPane.showMessageDialog(null, "You must enter a password.");
          return;
        }

        if (usernameEnter.getText().trim().equals(""))
          usernameEnter.setText("N/A");


        if (platformEnter.getText().trim().equals("")) {
          platformEnter.setText("N/A");
        }

        PasswordManagerGui.this.user.getAccounts().addAccount(usernameEnter.getText(),
            platformEnter.getText(), passwordEnter.getPassword());
        PasswordManagerGui.this.model.updateFilter();
        howManyPass.setText(
            "You have [" + Integer.toString(PasswordManagerGui.this.user.getAccounts().getLength())
                + "] password(s).");
        platformEnter.setText("");
        usernameEnter.setText("");
        passwordEnter.setText("");
        PasswordManagerGui.this.user.encrypt(PasswordManagerGui.this.password);
        boolean success = Main.dataFile.saveData();
        PasswordManagerGui.this.user.decrypt(PasswordManagerGui.this.password);
        PasswordManagerGui.this.model.setAccountList(PasswordManagerGui.this.user.getAccounts());
        executeSearch(PasswordManagerGui.this.search.getText());
        setScreen(mainScreenObject);
        if (!success) {
          System.out.println("Could not save passwords.");
          return;
        }
      }
    });


    /* More required generic screen things */
    this.setScreen(mainScreenObject);
    this.base.setLocationRelativeTo(null);
    this.base.setVisible(true);
    // this.base.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.base.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        synchronized (Main.dataFile) {
          PasswordManagerGui.this.user.encrypt(PasswordManagerGui.this.password);
          CryptoUtils.clearCharArray(PasswordManagerGui.this.password);
          PasswordManagerGui.this.base.dispose();
          Main.getGui().getBase().setVisible(true);
        }
      }
    });

  }

  DocumentListener documentListener = new DocumentListener() {
    public void changedUpdate(DocumentEvent documentEvent) {}

    public void insertUpdate(DocumentEvent documentEvent) {
      executeSearch(PasswordManagerGui.this.search.getText());
    }

    public void removeUpdate(DocumentEvent documentEvent) {
      executeSearch(PasswordManagerGui.this.search.getText());
    }
  };

  public void executeSearch(String text) {
    if (text.trim().equals("")) {
      PasswordManagerGui.this.model.updateFilter();
    } else {
      PasswordManagerGui.this.model.updateFilter(text);
    }
  }

  private void setScreen(Screen screen) {
    if (this.currentScreen != null)
      this.base.remove(this.currentScreen.getPanel());
    this.currentScreen = screen;
    this.base.add(screen.getPanel());
    this.base.setSize(0, 0);
    this.base.setSize(screen.getSize());
  }

  private void changeScreenOnButtonPress(Button btn, Screen scrn) {
    btn.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent event) {
        setScreen(scrn);
      }

    });
  }

  private void changeScreenOnButtonPress(Button btn, Screen scrn, JComponent toFocus) {
    btn.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent event) {
        setScreen(scrn);
        toFocus.requestFocusInWindow();
      }

    });
  }
  
  public void setLabelSize(JLabel label) {
    label.setSize(label.getPreferredSize());
  }


}
