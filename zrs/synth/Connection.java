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

package zrs.synth;

import java.awt.*;

class Connection {
  static final String IDENT = "conn";

  static final Color color = new Color (0, 0, 196);
  static final Color selectedColor = Color.red;
  static final int selectBoxSize = 6;
  boolean selected;

  GeneratorBox source;
  Input dest;

  Connection (GeneratorBox source, Input dest) {
    this.source = source;
    this.dest = dest;
    selected = false;
  }

  void select() {
    selected = true;
  }

  void unselect() {
    selected = false;
  }

  boolean isSelected() {
    return selected;
  }

  Rectangle getRect() {
    Point[] p = calculatePoints();
    int t;
    if (p[1].x < p[0].x) { t = p[0].x; p[0].x = p[1].x; p[1].x = t; }
    if (p[1].y < p[0].y) { t = p[0].y; p[0].y = p[1].y; p[1].y = t; }
    return new Rectangle (p[0].x, p[0].y,
                          p[1].x - p[0].x + 1, p[1].y - p[0].y + 1);
  }

  Point[] calculatePoints() {
    Point[] p = new Point [2];
    p[1] = dest.getCenter();
    p[0] = source.getNearestEdge (p[1]);
    return p;
  }

  boolean inside (int x, int y) {
    Point[] p = calculatePoints();
    Rectangle r = new Rectangle ((p[0].x + p[1].x) / 2 - selectBoxSize/2,
                                 (p[0].y + p[1].y) / 2 - selectBoxSize/2,
				 selectBoxSize, selectBoxSize);
    return r.inside (x, y);
  }

  void draw (Graphics g) {
    Point[] p = calculatePoints();

    g.setColor (selected ? selectedColor : color);
    g.drawLine (p[0].x, p[0].y, p[1].x, p[1].y);
    g.fillRect ((p[0].x + p[1].x) / 2 - selectBoxSize/2,
                (p[0].y + p[1].y) / 2 - selectBoxSize/2,
		selectBoxSize, selectBoxSize);
  }
}
