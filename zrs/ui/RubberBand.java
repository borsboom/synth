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

public abstract class RubberBand {

  public int startX;
  public int startY;

  int oldX;
  int oldY;

  boolean drawn = false;

  public RubberBand () {
  }

  public RubberBand (int startX, int startY) {
    this.oldX = this.startX = startX;
    this.oldY = this.startY = startY;
  }

  public Point getStart() {
    return new Point (startX, startY);
  }

  public void move (Graphics g, int x, int y) {
    erase (g);
    draw (g, x, y);
  }

  public void erase (Graphics g) {
    if (drawn) {
      g.setColor (Color.white);
      g.setXORMode (Color.black);
      invert (g, oldX, oldY);
      drawn = false;
    }
  }

  public void draw (Graphics g, int x, int y) {
    if (!drawn) {
      g.setColor (Color.white);
      g.setXORMode (Color.black);
      invert (g, oldX = x, oldY = y);
      drawn = true;
    }
  }

  abstract void invert (Graphics g, int x, int y);
}
