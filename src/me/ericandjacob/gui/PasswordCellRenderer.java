package me.ericandjacob.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellRenderer;

public class PasswordCellRenderer implements TableCellRenderer {

  private CharTextField field;

  public PasswordCellRenderer() {
    this.field = new CharTextField();
  }

  @Override
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
      boolean hasFocus, int row, int column) {
    if (!(value instanceof char[])) {
      return null;
    }
    field.setChars((char[]) value);
    field.setSelectionColor(Color.BLUE);
    if (isSelected) {
      field.setForeground(table.getSelectionForeground());
      field.setBackground(table.getSelectionBackground());
    } else {
      field.setForeground(table.getForeground());
      field.setBackground(table.getBackground());
    }
    field.setFont(table.getFont());
    field.setBorder(null);
    field.setOpaque(true);
    return field;
  }

  private static class CharTextField extends JTextField {

    private static final long serialVersionUID = 1L;
    Font font;
    char[] chars;

    @Override
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      if (chars != null) {
        Graphics2D graphics = (Graphics2D) g;
        Font pFont = graphics.getFont();
        graphics.setFont(this.getFont());
        graphics.drawChars(chars, 0, chars.length, 2, 10);
        graphics.setFont(pFont);
      }
    }

    public void setChars(char[] chars) {
      this.chars = chars;
    }
  }

}
