package me.ericandjacob.gui;

import javax.swing.JButton;

@SuppressWarnings("serial")
public class Button extends JButton {

  public Button(String title, int width, int height, boolean avaliable) {
    super(title);
    this.setSize(width, height);
    this.setEnabled(avaliable);
  }

}
