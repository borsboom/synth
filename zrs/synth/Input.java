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
import java.util.*;
import zrs.synthgen.*;

class Input {
  static final Color color = Color.gray;
  static final Color hiColor = Color.white;
  static final Color shadeColor = Color.black;
  static final Color labelColor = Color.black;

  static final Font labelFont = new Font ("Helvetica", Font.PLAIN, 10);
  static FontMetrics labelFm;
  static final int radius = 7;
  static final int diameter = radius * 2;
  Point topLeft;
  Dimension size = new Dimension (diameter, diameter);
  int width = -1;
  int height = -1;

  boolean draw = true;
  boolean allowMultipleConnections = false;
  boolean allowEmpty = false;

  String label;

  GeneratorBox pa;

  Input (GeneratorBox parent, String label) {
    this.pa = parent;
    this.label = label;
  }

  Input (GeneratorBox parent, String label, boolean draw, boolean allowMulti, boolean allowEmpty) {
    this.pa = parent;
    this.label = label;
    this.draw = draw;
    this.allowMultipleConnections = allowMulti;
    this.allowEmpty = allowEmpty;
  }

  Generator[] getGenerators() throws SynthIfException {
    Enumeration conE = pa.pa.connections.elements();
    Vector genV = new Vector();
    while (conE.hasMoreElements()) {
      Connection con = (Connection) conE.nextElement();
      if (this == con.dest)
        genV.addElement (con.source.generator);
    }

    if (genV.isEmpty()) {
      if (allowEmpty)
        return null;
      else 
        throw new SynthIfException ("Missing connection.");
    }

    Generator[] generators = new Generator [genV.size()];
    Enumeration genE = genV.elements();
    int i = 0;
    while (genE.hasMoreElements())
      generators [i++] = (Generator) genE.nextElement();
    return generators;
  }

  Generator getGenerator() throws SynthIfException {
    Generator[] generators = getGenerators();
    if (generators == null) return null;
    else return generators [0];
  }

  void move (int x, int y) {
    topLeft = new Point (x, y);
  }

  boolean inside (int x, int y) {
    int cx = topLeft.x + size.width/2;
    int cy = topLeft.y + radius;
    int dist = (int)Math.sqrt((double)(((x-cx)*(x-cx) + (y-cy)*(y-cy))));
    return (dist <= radius);
  }

  Point getCenter() {
    return new Point (topLeft.x + size.width/2 + pa.insideRect.x,
                      topLeft.y + radius + pa.insideRect.y);
  }

  Dimension calcSize (Graphics g) {
    if (labelFm == null) labelFm = g.getFontMetrics (labelFont);

    if (label == null)
      width = height = diameter;
    else {
      width = labelFm.stringWidth (label);
      if (width < diameter) width = diameter;
      height = labelFm.getHeight() + diameter;
    }

    return (size = new Dimension (width, height));
  }

  void draw (Graphics g) {
    if (!draw) return;
    int x = topLeft.x + size.width/2 - radius + pa.insideRect.x;
    int y = topLeft.y + pa.insideRect.y;
    g.setColor (color);
    g.fillOval (x, y, diameter, diameter);
    g.setColor (hiColor);
    g.drawArc (x, y, diameter, diameter, 225, 180);
    g.setColor (shadeColor);
    g.drawArc (x, y, diameter, diameter, 45, 180);
    if (label != null) {
      g.setFont (labelFont);
      g.setColor (labelColor);
      g.drawString (label, topLeft.x + pa.insideRect.x,
                   topLeft.y + pa.insideRect.y + diameter + labelFm.getAscent());
    }
  }
}
