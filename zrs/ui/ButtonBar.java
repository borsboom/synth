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

public class ButtonBar extends Panel {

  static final int separatorSpace = 5;

  boolean separated;

  GridBagLayout gbl;
  int previousSpace;
  Component previousComponent = null;

  Panel panel;

  public ButtonBar() {
    setBackground (Color.lightGray);
    setLayout (new FlowLayout (FlowLayout.LEFT, 0, 0));
    panel = new Panel();
    gbl = new GridBagLayout();
    panel.setLayout (gbl);
    super.add (panel);
  }

  public Component add (Component component) {
    GridBagConstraints c = new GridBagConstraints();
    if (separated) {
      c.insets.left = 4;
      separated = false;
    }
    gbl.setConstraints (component, c);
    panel.add (component);
    return component;
  }

  public void addSeparator() {
    separated = true;
  }

}
