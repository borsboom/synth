/*
 * Copyright (C) 1996 Emanuel Borsboom <manny@zerius.victoria.bc.ca>
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package zrs.ui;

import java.awt.*;

class StatusCanvas extends Canvas {
  
  static final Color shadeColor = Color.darkGray;
  static final Color barColor = Color.red;
  static final Color textColor = Color.black;
  static final Color bgColor = Color.white;
  static final Font font = new Font ("Helvetica", Font.PLAIN, 12);
  static FontMetrics fm = null;

  Dimension prefSize;

  int percent = 0;

  StatusCanvas (int width, int height) {
    setBackground (bgColor);
    prefSize = new Dimension (width, height);
  }

  StatusCanvas() {
    this (128, 24);
  }

  void setPercent (int percent) {
    if (percent != this.percent) {
      this.percent = percent;
      repaint();
    }
  }

  public Dimension minimumSize() {
    return prefSize;
  }

  public Dimension preferredSize() {
    return prefSize;
  }

  public void paint (Graphics g) {

    if (fm == null)
      fm = g.getFontMetrics (font);

    int width = size().width;
    int height = size().height;

    g.setColor (shadeColor);
    g.drawLine (0, 0, width-1, 0);
    g.drawLine (0, 0, 0, height-1);

    g.setColor (barColor);
    g.fillRect (1, 1, (width-2) * percent / 100 + 1, height-2);

    g.setColor (textColor);
    g.setFont (font);
    String s = Integer.toString (percent) + "%";
    g.drawString (s, width/2 - fm.stringWidth (s)/2,
                     height/2 - fm.getHeight()/2 + fm.getAscent());
  }

}

public class StatusDialog extends Dialog {
  
  Cancelable whatToCancel;

  StatusCanvas statusCanvas;
  Button cancelButton;

  public StatusDialog (Frame parent, String title, boolean modal,
                       String message, Cancelable whatToCancel) 
  {
    super (parent, title, modal);

    this.whatToCancel = whatToCancel;

    GridBagLayout gbl = new GridBagLayout();
    setLayout (gbl);
    GridBagConstraints c = new GridBagConstraints();

    c.gridx = c.gridy = 0;

    if (message != null) {
      c.insets = new Insets (4, 4, 4, 4);
      Label l = new Label (message, Label.CENTER);
      gbl.setConstraints (l, c);
      add (l);
      c.gridy++;
    }

    c.insets = new Insets (3, 3, 3, 3);
    c.weighty = c.weightx = 1.0;
    c.fill = GridBagConstraints.BOTH;
    statusCanvas = new StatusCanvas();
    gbl.setConstraints (statusCanvas, c);
    add (statusCanvas);
    c.gridy++;
    
    if (whatToCancel != null) {
      c.fill = GridBagConstraints.NONE;
      c.weightx = c.weighty = 0.0;
      cancelButton = new Button ("Cancel");
      gbl.setConstraints (cancelButton, c);
      add (cancelButton);
    }

    pack();
    pack();
    pack();
  }

  public void setPercent (int percent) {
    statusCanvas.setPercent (percent);
  }

  public boolean action (Event event, Object arg) {
    if (event.target == cancelButton) {
      whatToCancel.cancel();
      return true;
    }
    else return super.action (event, arg);
  }

}
