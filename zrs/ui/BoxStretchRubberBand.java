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

public class BoxStretchRubberBand extends RubberBand {

  boolean fixHeightFlag = false;
  int fixedHeight;

  public BoxStretchRubberBand (int startX, int startY) {
    super (startX, startY);
  }

  public BoxStretchRubberBand (int startX, int startY, int fixedHeight) {
    this (startX, startY);
    this.fixedHeight = fixedHeight;
    fixHeightFlag = true;
  }

  public BoxStretchRubberBand (Point start) {
    super (start.x, start.y);
  }

  void invert (Graphics g, int x, int y) {
    int left, top, right, bottom;
    if (fixHeightFlag) 
      y = startY + fixedHeight;
    if (x < startX) { left = x; right = startX; }
    else { left = startX; right = x; }
    if (y < startY) { top = y; bottom = startY; }
    else { top = startY; bottom = y; }
    g.drawRect (left, top, right-left+1, bottom-top+1);
  }
}
