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
import zrs.synthgen.*;

class AdderBox extends GeneratorBox {
  static final String IDENT = "Add ";

  static Color fgColor = Color.black;
  static Color bgColor = Color.lightGray;
  static Color hiColor = new Color (210, 210, 210);
  static Color shadeColor = Color.darkGray;
  static Color selectedFgColor = Color.white;
  static Color selectedBgColor = Color.blue;
  static Color selectedHiColor = new Color (128, 128, 255);
  static Color selectedShadeColor = new Color (0, 0, 128);
  static int radius = Input.radius;
  static int diameter = radius * 2;
  static int borderSize = 5;
  static int wholeRadius = radius + borderSize;
  static int wholeDiameter = wholeRadius * 2;
  static int plusSize = radius - 3;

  Point centerPoint = new Point (0, 0);

  AdderBox (SynthCanvas pa) {
    super(pa, IDENT);
    Input in = new Input (this, null, false, true, false);
    addInput (in);
  }

  void newGenerator (Synthesizer synth) {
    synth.add (generator = new Adder (synth));
  }

  void connectGenerator (Synthesizer synth) 
    throws SynthIfException
  {
    Generator[] generators = ((Input) inputs.firstElement()).getGenerators();
    if (generators.length < 2)
      throw new SynthIfException ("Adder: must have at least two inputs");
    Generator lastGen = generators[0];
    for (int i = 1; i < generators.length-1; i++) {
      lastGen = new Adder (synth, lastGen, generators[i]);
      synth.add (lastGen);
    }

    ((Adder)generator).setParameters (
      lastGen, generators [generators.length - 1]
    );
  }

  void calcRects() {
    wholeRect.reshape (topLeft.x, topLeft.y, wholeDiameter, wholeDiameter);
    insideRect.reshape (topLeft.x + borderSize, topLeft.y + borderSize,
                        diameter, diameter);
    centerPoint.x = topLeft.x + wholeDiameter / 2;
    centerPoint.y = topLeft.y + wholeDiameter / 2;
  }

  boolean inside (int x, int y) {
    int a = centerPoint.x - x;
    int b = centerPoint.y - y;
    return (int) Math.sqrt (a * a + b * b) <= wholeRadius;
  }

  boolean inBorder (int x, int y) {
    int a = centerPoint.x - x;
    int b = centerPoint.y - y;
    return inside (x, y) && !((int)Math.sqrt (a * a + b * b) < radius);
  }

  Point getNearestEdge (Point p) {
    double theta = Math.atan ((p.y-centerPoint.y) / (double)(p.x - centerPoint.x));
    if (p.x >= centerPoint.x) {
      return new Point ((int)(wholeRadius * Math.cos (theta)) + centerPoint.x,
                      (int)(wholeRadius * Math.sin (theta)) + centerPoint.y);
    } else
      return new Point (centerPoint.x - (int)(wholeRadius * Math.cos (theta)),
                        centerPoint.y - (int)(wholeRadius * Math.sin (theta)));
  }

  void draw (Graphics g) {
    if (!knowSize) {
      knowSize = true;
      calcRects();
      ((Input) inputs.firstElement()).move (0, 0);
    }
    g.setColor (selected ? selectedBgColor : bgColor);
    g.fillOval (topLeft.x, topLeft.y, wholeDiameter, wholeDiameter);
    g.setColor (selected ? selectedHiColor : hiColor);
    g.drawArc (topLeft.x, topLeft.y, wholeDiameter, wholeDiameter, 45, 180);
    g.setColor (selected ? selectedShadeColor : shadeColor);
    g.drawArc (topLeft.x, topLeft.y, wholeDiameter, wholeDiameter, 225, 180);
    g.setColor (selected ? selectedFgColor : fgColor);
    g.drawLine (centerPoint.x, centerPoint.y - plusSize,
                centerPoint.x, centerPoint.y + plusSize + 1);
    g.drawLine (centerPoint.x+1, centerPoint.y - plusSize,
                centerPoint.x+1, centerPoint.y + plusSize + 1);
    g.drawLine (centerPoint.x - plusSize, centerPoint.y,
                centerPoint.x + plusSize + 1, centerPoint.y);
    g.drawLine (centerPoint.x - plusSize, centerPoint.y+1,
                centerPoint.x + plusSize + 1, centerPoint.y+1);
  }

}

