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
import java.io.*;
import zrs.synthgen.*;
import zrs.ui.PropertiesDialog;

class ConstantBox extends GeneratorBox {
  static final String IDENT = "Cons";
  
  static Color fgColor = Color.black;
  static Color bgColor = Color.lightGray;
  static Color hiColor = new Color (210, 210, 210);
  static Color shadeColor = Color.darkGray;
  static Color selectedFgColor = Color.white;
  static Color selectedBgColor = Color.blue;
  static Color selectedHiColor = new Color (128, 128, 255);
  static Color selectedShadeColor = new Color (0, 0, 128);
  static int borderSize = 5;

  double value = 0;

  ConstantBox (SynthCanvas pa) {
    super (pa, IDENT);
  }

  void newGenerator (Synthesizer synth) {
    synth.add (generator = new Constant (synth, value));
  }

  void write (DataOutputStream out) throws IOException {
    super.write (out);
    out.writeDouble (value);
  }

  void read (DataInputStream in) throws IOException, FileFormatException {
    super.read (in);
    value = in.readDouble();
  }

  void calcRects() {
    if (insideSize != null) {
      wholeRect.reshape (topLeft.x, topLeft.y,
                         insideSize.width + borderSize * 2,
		         insideSize.height + borderSize * 2);
      insideRect.reshape (topLeft.x + borderSize, topLeft.y + borderSize,
                          insideSize.width, insideSize.height);
    }
  }

  PropertiesDialog getProperties (Frame f) {
    return new ConstantProperties (f, this);
  }

  void draw (Graphics g) {
    if (!knowSize) {
      if (labelFm == null) {
        labelFm = g.getFontMetrics (labelFont);
        topBorderSize = labelFm.getHeight() + 2;
      }
      insideSize = new Dimension (
                 labelFm.stringWidth (Double.toString(value)),
                                          labelFm.getHeight());
      calcRects();
    }

    g.setColor (selected ? selectedBgColor : bgColor);
    g.fillRect (wholeRect.x, wholeRect.y, wholeRect.width, wholeRect.height);

    g.setColor (selected ? selectedShadeColor : shadeColor);
    g.drawLine (wholeRect.x, wholeRect.y + wholeRect.height - 1,
                wholeRect.x + wholeRect.width - 1,
		wholeRect.y + wholeRect.height - 1);
    g.drawLine (wholeRect.x + wholeRect.width - 1, wholeRect.y,
                wholeRect.x + wholeRect.width - 1,
		wholeRect.y + wholeRect.height - 1);

    g.setColor (selected ? selectedHiColor : hiColor);
    g.drawLine (wholeRect.x, wholeRect.y,
                wholeRect.x + wholeRect.width-1, wholeRect.y);
    g.drawLine (wholeRect.x, wholeRect.y,
                wholeRect.x, wholeRect.y + wholeRect.height - 1);

    g.setColor (selected ? selectedFgColor : fgColor);
    g.setFont (labelFont);
    g.drawString (Double.toString(value), insideRect.x,
                  insideRect.y + labelFm.getAscent());
  }

}
