package me.ericandjacob.gui;

import java.awt.Dimension;

import javax.swing.JPanel;

public class Screen {

  private JPanel panel;
  private Dimension size;
  
  public Screen(JPanel panel, Dimension size) {
    this.panel = panel;
    this.size = size;
  }
  
  public JPanel getPanel() {
    return panel;
  }
  
  public Dimension getSize() {
    return size;
  }
  
}
