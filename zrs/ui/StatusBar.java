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
import zrs.misc.TimerThread;
import zrs.misc.Timeable;

public class StatusBar extends Canvas implements Timeable {

  private static final Color textColor = Color.black;
  private static final Color emphasizedColor = new Color (128, 0, 0);

  private Dimension minSize = new Dimension (32, 1);
  private boolean emphasized;
  private String text;
  private Window pa;
  private static final Font font = new Font ("Helvetica", Font.PLAIN, 12);
  private FontMetrics fm;
  private TimerThread timer = null;

  public StatusBar (Window pa) {
    setBackground (Color.lightGray);
    this.pa = pa;
  }

  public StatusBar (Window pa, String text) {
    this (pa);
    this.text = text;
  }

  public Dimension preferredSize() {
    return minimumSize();
  }

  public Dimension minimumSize() {
    return minSize;
  }

  public void setText (String text) {
    if (timer != null) {
      timer.stop();
      timer = null;
    }
    emphasized = false;
    this.text = text;
    repaint();
  }

  public void setEmphasizedText (String text) {
    if (timer != null) {
      timer.stop();
      timer = null;
    }
    emphasized = true;
    this.text = text;
    repaint();
  }

  public void setText (String text, int time) {
    setText (text);
    timer = new TimerThread (this, time, 1);
    timer.start();
  }

  public void setEmphasizedText (String text, int time) {
    setEmphasizedText (text);
    timer = new TimerThread (this, time, 1);
    timer.start();
  }

  public void clearText() {
    text = null;
    repaint();
  }

  public void tick (TimerThread t) {
    timer = null;
    clearText();
  }

  public void paint (Graphics g) {
    if (fm == null) {
      fm = g.getFontMetrics (font);
      minSize.height = fm.getHeight() + 1;
      pa.pack();
      repaint();
    } else {
      g.setPaintMode();
      g.setColor (Color.gray);
      g.drawLine (0, 0, size().width-1, 0);
      if (text != null) {
        g.setColor (emphasized ? emphasizedColor : textColor);
        g.setFont (font);
        g.drawString (text, 0, fm.getAscent() + 1);
      }
    }
  }

}
