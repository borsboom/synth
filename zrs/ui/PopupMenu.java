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
import java.util.Vector;

public class PopupMenu extends Window {

  static final Font font = new Font ("Helvetica", Font.PLAIN, 12);
  static final Font titleFont = new Font ("Helvetica", Font.BOLD, 12);
  static final Color borderColor = Color.black;
  static final Color bgColor = new Color (230, 230, 230);
  static final Color selectedBgColor = Color.black;
  static final Color fgColor = Color.black;
  static final Color selectedFgColor = Color.white;
  static final Color titleColor = Color.black;
  static final Color dividerColor = Color.gray;
  static final int TITLE_ABOVE_SPACE = 2;
  static final int TITLE_BELOW_SPACE = 3;
  static final int ITEM_ABOVE_SPACE = 2;
  static final int ITEM_BELOW_SPACE = 2;
  static final int DIVIDER_HEIGHT = 1;
  static final int MATTE_SIZE = 3;

  String title;
  String[] items;
  int selected = -1;

  FontMetrics fm = null;
  FontMetrics titleFm = null;
  Dimension minSize = new Dimension (0, 0);

  Frame pa;

  public PopupMenu (Frame parent, String title, String[] items, int x, int y) {
    super (parent);
    this.pa = parent;
    this.title = title;
    this.items = items;
    setBackground (bgColor);
    move (x, y);
  }

  public void paint (Graphics g) {
    if (fm == null) {
      fm = g.getFontMetrics (font);
      titleFm = g.getFontMetrics (titleFont);

      minSize.width = titleFm.stringWidth (title);
      minSize.height = 1 + TITLE_ABOVE_SPACE + titleFm.getHeight() + TITLE_BELOW_SPACE;

      for (int i = 0; i < items.length; i++) {
	minSize.height += ITEM_ABOVE_SPACE;
        if (items[i].equals ("-")) {
	  minSize.height += DIVIDER_HEIGHT;
	} else {
	  minSize.height += fm.getHeight();
	  if (fm.stringWidth (items[i]) > minSize.width)
	    minSize.width = fm.stringWidth (items[i]);
	}
	minSize.height += ITEM_BELOW_SPACE;
      }
      minSize.width += (MATTE_SIZE+1) * 2;
      minSize.height++;
      resize (minSize);
      repaint();

    } else {
      g.setColor (borderColor);
      g.drawLine (0, 0, 0, size().height-1);
      g.drawLine (0, 0, size().width-1, 0);
      g.drawLine (size().width-1, 0, size().width-1, size().height-1);
      g.drawLine (0, size().height-1, size().width-1, size().height-1);

      int y = 1 + TITLE_ABOVE_SPACE;

      g.setFont (titleFont);
      g.setColor (titleColor);
      g.drawString (title, size().width/2 - titleFm.stringWidth(title)/2,
                    y + titleFm.getAscent());
      y += titleFm.getHeight() + TITLE_BELOW_SPACE;

      g.setFont (font);
      for (int i = 0; i < items.length; i++) {
        if (items[i].equals ("-")) {
	  g.setColor (dividerColor);
	  g.drawLine (1, y + ITEM_ABOVE_SPACE, size().width-2, y + ITEM_ABOVE_SPACE);
	  y += DIVIDER_HEIGHT;
	}
	else {
	  if (selected == i) {
            g.setColor (selectedBgColor);
            g.fillRect (1, y, size().width-2, fm.getHeight() + ITEM_ABOVE_SPACE + ITEM_BELOW_SPACE);
            g.setColor (selectedFgColor);
	  } else
	    g.setColor (fgColor);
	  g.drawString (items[i], 1 + MATTE_SIZE, y + ITEM_ABOVE_SPACE + fm.getAscent());
	  y += fm.getHeight();
	}
	y += ITEM_ABOVE_SPACE + ITEM_BELOW_SPACE;
      }
    }
  }

  int mouseIn (int x, int y) {
    int c = 1 + TITLE_ABOVE_SPACE + titleFm.getHeight() + TITLE_BELOW_SPACE;
    int nc;
    if (fm == null || x < 0 || y < c ||
	x >= size().width || y >= size().height)
    {
      return -1;
    }

    int i;
    for (i = 0; i < items.length; i++) {
      if (items[i].equals ("-"))
        nc = c + ITEM_ABOVE_SPACE + DIVIDER_HEIGHT + ITEM_BELOW_SPACE;
      else
        nc = c + ITEM_ABOVE_SPACE + fm.getHeight() + ITEM_BELOW_SPACE;
      if (y >= c && y < nc)
        break;
      c = nc;
    }
    
    if (i < items.length)
      return i;
    else
      return -1;
  }

  public boolean mouseEnter (Event event, int x, int y) {
    int i = selected;
    selected = mouseIn (x, y);
    if (selected != i) repaint();
    return true;
  }

  public boolean mouseExit (Event event, int x, int y) {
    if (selected != -1) {
      selected = -1;
      repaint();
    }
    return true;
  }

  public boolean mouseMove (Event event, int x, int y) {
    int i = selected;
    selected = mouseIn (x, y);
    if (selected != i) repaint();
    return true;
  }

  public boolean mouseUp (Event event, int x, int y) {
    dispose();
    return true;
  }
}
