package me.ericandjacob.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Optional;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import me.ericandjacob.Main;
import me.ericandjacob.data.User;
import me.ericandjacob.util.CryptoUtils;
import me.ericandjacob.util.GenUtil;
import me.ericandjacob.util.VersionChecker;


public class Gui {

  private JFrame base;
  private Screen currentScreen;
  private String version = "       ";

  public Gui() throws IOException {


    try {
      checkVersion();
    } catch (IOException e1) {
      e1.printStackTrace();
    }


    /* Ui Manager */
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
        | UnsupportedLookAndFeelException e) {
      e.printStackTrace();
    }

    /* Making Objects */
    // JPanels & Screens
    JPanel mainScreen = new JPanel();
    JPanel registerScreen = new JPanel();
    JPanel loginScreen = new JPanel();
    Screen mainScreenObject = new Screen(mainScreen, new Dimension(500, 100));
    Screen registerScreenObject = new Screen(registerScreen, new Dimension(400, 190));
    Screen loginScreenObject = new Screen(loginScreen, new Dimension(400, 190));

    // Buttons
    // Main screen
    Button register = new Button("Register", 154, 92, true);
    Button login = new Button("Login", 154, 92, true);
    Button back = new Button("Back", 154, 92, true);
    Button back2 = new Button("Back", 154, 92, true);

    // Register
    Button registerCommit = new Button("Finish", 154, 92, true);

    // Login
    Button loginCommit = new Button("Login", 154, 92, true);


    // Labels
    // Main Screen
    JLabel programName = new JLabel("Celatum", JLabel.CENTER);
    JLabel debugInfo = new JLabel(this.version, JLabel.CENTER);

    // Register
    JLabel enterUsername = new JLabel("Please enter in a username.");
    JLabel enterPassword = new JLabel("Please enter in a secure password.");

    // Login
    JLabel instructionsForLoggingInPartOne = new JLabel("Please login");
    JLabel showWhichIsUsername = new JLabel("Username: ");
    JLabel showWhichIsPassword = new JLabel("Password: ");


    // Text fields
    // Register
    JTextField registerUsername = new JTextField("");
    JPasswordField registerPassword = new JPasswordField("");

    // Login
    JTextField loginUsername = new JTextField("");
    JPasswordField loginPassword = new JPasswordField("");


    /* Changing labels and such */
    programName.setFont(new Font("Times New Roman", Font.PLAIN, 17));

    register.setToolTipText("Make a new account");
    login.setToolTipText("Login to your account");


    /* Set generic screen things */
    this.base = new JFrame("Celatum");
    this.base.setSize(500, 100);
    this.base.setLocationRelativeTo(null);
    this.base.setResizable(false);


    /* Setting up the main title screen */

    mainScreen.add(register);
    mainScreen.add(programName);
    mainScreen.add(login);
    mainScreen.add(debugInfo);
    mainScreen.setLayout(null);
    
    register.setLocation(1, 1);
    register.setSize(new Dimension(100, 30));
    
    programName.setLocation(220, 10);
    programName.setSize(programName.getPreferredSize());
    
    login.setLocation(390, 1);
    login.setSize(new Dimension(100, 30));
    
    if(VersionChecker.checkVersion())
    debugInfo.setLocation(200, 40);
    else
      debugInfo.setLocation(100, 40);
    debugInfo.setSize(debugInfo.getPreferredSize());

    this.setScreen(mainScreenObject);

    /* Setting up the register screen */

    registerScreen.add(enterUsername);
    registerScreen.add(enterPassword);
    registerScreen.add(registerUsername);
    registerScreen.add(registerPassword);
    registerScreen.add(registerCommit);
    registerScreen.add(back2);
    registerScreen.setLayout(null);

    enterUsername.setSize(150, 10);
    enterUsername.setLocation(115, 5);

    enterPassword.setSize(190, 10);
    enterPassword.setLocation(105, 60);

    registerUsername.setSize(150, 30);
    registerUsername.setLocation(115, 20);
    registerUsername.setEditable(true);

    registerPassword.setSize(150, 30);
    registerPassword.setLocation(115, 80);
    registerPassword.setEditable(true);

    back2.setSize(80, 20);
    back2.setLocation(1, 1);
    back2.setVisible(true);

    registerCommit.setSize(154, 32);
    registerCommit.setLocation(115, 120);
    registerCommit.setVisible(true);


    registerCommit.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent event) {
        String username = registerUsername.getText();
        char[] password = registerPassword.getPassword();
        boolean isEmpty = true;
        for (int i = 0; i < password.length; ++i) {
          if (password[i] != 0x20 && password[i] != 0xA && password[i] != 0x9
              && password[i] != 0xC) {
            isEmpty = false;
            break;
          }
        }

        
        if (isEmpty) {
          GenUtil.logError("User tried to enter in a password of null");
          JOptionPane.showMessageDialog(null, "Please enter in a password!");
        } else if (username.trim().equals("")) {
          GenUtil.logError("User tried to enter in a username of null");
          JOptionPane.showMessageDialog(null, "Please enter in a username!");
        } else if (GenUtil.charArrayEqual(GenUtil.charArrayToLowerCase(password), password)) {
          GenUtil.logError("User tried to enter in an all lowercase password.");
          JOptionPane.showMessageDialog(null,
              "Please let your password have at least one uppercase letter.");
        } else if (password.length < 6) {
          GenUtil.logError(
              "User password was " + password.length + ", program was expexting 6 or more.");
          JOptionPane.showMessageDialog(null, "Your password must be at least 6 characters..");
        } else {
          // TODO Maby we should add a confirm password field.
          if (Main.dataFile.getUserList().hasUsername(username)) {
            JOptionPane.showMessageDialog(null, "This username is taken.");
          } else {
            if (Wordcounter(username) > 1) {
              JOptionPane.showMessageDialog(null, "Your username can not contain spaces.");
              username = username.replace(" ", "");
            }
            User user = Main.dataFile.getUserList().registerNewUser();
            user.setUsername(username);
            user.encrypt(password);
            CryptoUtils.clearCharArray(password);
            registerUsername.setText(null);
            registerPassword.setText(null);
            setScreen(loginScreenObject);
            loginUsername.requestFocusInWindow();
          }

        }
      }


    });


    /* Setting up the login screen */


    loginScreen.add(instructionsForLoggingInPartOne);
    loginScreen.add(loginUsername);
    loginScreen.add(loginPassword);
    loginScreen.add(loginCommit);
    loginScreen.add(showWhichIsPassword);
    loginScreen.add(showWhichIsUsername);
    loginScreen.add(back);
    loginScreen.setLayout(null);

    instructionsForLoggingInPartOne.setSize(instructionsForLoggingInPartOne.getPreferredSize());
    instructionsForLoggingInPartOne.setLocation(140, 5);
    instructionsForLoggingInPartOne.setVisible(true);

    loginUsername.setSize(100, 50);
    loginUsername.setLocation(135, 30);
    loginUsername.setVisible(true);

    showWhichIsUsername.setSize(showWhichIsUsername.getPreferredSize());
    showWhichIsUsername.setLocation(50, 40);
    showWhichIsUsername.setVisible(true);

    loginPassword.setSize(100, 50);
    loginPassword.setLocation(135, 90);
    loginPassword.setVisible(true);

    back.setSize(80, 20);
    back.setLocation(310, 1);
    back.setVisible(true);

    showWhichIsPassword.setSize(showWhichIsPassword.getPreferredSize());
    showWhichIsPassword.setLocation(50, 95);
    showWhichIsPassword.setVisible(true);

    loginCommit.setSize(90, 32);
    loginCommit.setLocation(265, 55);
    loginCommit.setVisible(true);


    loginCommit.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        Optional<User> userOpt = Main.dataFile.getUserList().getUser(loginUsername.getText());
        if (!userOpt.isPresent()) {
          JOptionPane.showMessageDialog(null, "Username not found.");
          return;
        }
        User user = userOpt.get();
        Optional<Exception> exception = user.decrypt(loginPassword.getPassword());
        if (exception.isPresent()) {
          exception.get().printStackTrace();
          JOptionPane.showMessageDialog(null, "Wrong password.");
          return;
        }
        new PasswordManagerGui(user, loginUsername.getText(), loginPassword.getPassword());
        loginUsername.setText(null);
        loginPassword.setText(null);
        Gui.this.base.setVisible(false);
        Gui.this.setScreen(mainScreenObject);
        login.requestFocusInWindow();
      }
    });

    /* Checking button presses */

    changeScreenOnButtonPress(register, registerScreenObject, registerUsername);
    changeScreenOnButtonPress(login, loginScreenObject, loginUsername);
    changeScreenOnButtonPress(back, mainScreenObject, login);
    changeScreenOnButtonPress(back2, mainScreenObject, login);
    clearTextOnButtonPress(back, new JTextField[] {loginUsername, loginPassword});
    clearTextOnButtonPress(back2, new JTextField[] {registerUsername, registerPassword});


    /* More required generic screen things */

    this.base.setFocusTraversalPolicy(new FocusTraversalPolicy() {

      @Override
      public Component getComponentAfter(Container aContainer, Component aComponent) {
        if (Gui.this.currentScreen == mainScreenObject) {
          if (aComponent == login)
            return register;
          return login;
        } else if (Gui.this.currentScreen == loginScreenObject) {
          if (aComponent == loginUsername)
            return loginPassword;
          if (aComponent == loginPassword)
            return loginCommit;
          return loginUsername;
        } else if (Gui.this.currentScreen == registerScreenObject) {
          if (aComponent == registerUsername)
            return registerPassword;
          if (aComponent == registerPassword)
            return registerCommit;
          return registerUsername;
        }
        return null;
      }

      @Override
      public Component getComponentBefore(Container aContainer, Component aComponent) {
        if (Gui.this.currentScreen == mainScreenObject) {
          if (aComponent == login)
            return register;
          return login;
        } else if (Gui.this.currentScreen == loginScreenObject) {
          if (aComponent == loginPassword)
            return loginUsername;
          if (aComponent == loginCommit)
            return loginPassword;
          if (aComponent == loginUsername)
            return loginCommit;
          return loginUsername;
        } else if (Gui.this.currentScreen == registerScreenObject) {
          if (aComponent == registerCommit)
            return registerPassword;
          if (aComponent == registerPassword)
            return registerUsername;
          if (aComponent == registerUsername)
            return registerCommit;
          return registerUsername;
        }
        return null;
      }

      @Override
      public Component getDefaultComponent(Container aContainer) {
        if (Gui.this.currentScreen == mainScreenObject) {
          return login;
        } else if (Gui.this.currentScreen == loginScreenObject) {
          return loginUsername;
        } else if (Gui.this.currentScreen == registerScreenObject) {
          return registerUsername;
        }
        return null;
      }

      @Override
      public Component getFirstComponent(Container aContainer) {
        if (Gui.this.currentScreen == mainScreenObject) {
          return login;
        } else if (Gui.this.currentScreen == loginScreenObject) {
          return loginUsername;
        } else if (Gui.this.currentScreen == registerScreenObject) {
          return registerUsername;
        }
        return null;
      }

      @Override
      public Component getLastComponent(Container aContainer) {
        if (Gui.this.currentScreen == mainScreenObject) {
          return register;
        } else if (Gui.this.currentScreen == loginScreenObject) {
          return loginCommit;
        } else if (Gui.this.currentScreen == registerScreenObject) {
          return registerCommit;
        }
        return null;
      }
    });

    this.base.setVisible(true);
    // this.base.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.base.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        GenUtil.logDebug("Saving dataFile");
        boolean success = Main.dataFile.saveData();
        if (!success) {
          System.out.println("Could not save.");
          return;
        }
        GenUtil.logDebug("Finished saving dataFile");
        System.exit(0);
      }
    });
  }

  private void setScreen(Screen screen) {
    if (this.currentScreen != null)
      this.base.remove(this.currentScreen.getPanel());

    this.currentScreen = screen;
    this.base.add(screen.getPanel());
    this.base.setSize(0, 0);
    this.base.setSize(screen.getSize());

  }

  @SuppressWarnings("unused")
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
  
  public JFrame getBase() {
    return this.base;
  }

  public static int Wordcounter(String str) {
    return str.trim().split("\\s+").length;
  }


  public void clearTextOnButtonPress(Button btn, JTextField[] field) {
    btn.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent event) {
        for (int i = 0; i < field.length; ++i)
          field[i].setText(null);
      }

    });
  }

  public void checkVersion() throws IOException {
    if (!VersionChecker.checkVersion())
      outdatedVersion();
    else
      getVersion();
  }

  public void getVersion() {
    this.version += "Release " + VersionChecker.getVersion();
  }

  public void outdatedVersion() {
        this.version += "Release " + VersionChecker.getVersion() + " (Outdated Client, please update to release "
        + VersionChecker.getUpdatedVersion() + ")";
  }

}

