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
import java.io.*;
import zrs.synthgen.*;
import zrs.ui.*;

abstract class GeneratorBox implements PropertiesObserver {
  
  static int borderSize = 1;
  static int inputSpace = 4;
  static int topBorderSize;

  static Font labelFont = new Font ("Helvetica", Font.PLAIN, 12);
  static FontMetrics labelFm;
  String label;

  static Color color = Color.lightGray;
  static Color hiColor = new Color (196, 196, 196);
  static Color shadeColor = Color.darkGray;
  static Color labelColor = Color.black;
  static Color labelBgColor = new Color (150, 150, 150);
  static Color labelHiColor = Color.white;
  static Color labelHiBgColor = Color.blue;

  SynthCanvas pa;
  String identifier;

  Point topLeft;		// actual position and dimensions of box
  Dimension insideSize;
  Rectangle wholeRect = new Rectangle(); // calculated based on previous two
  Rectangle insideRect = new Rectangle();  // and origin
  boolean knowSize = false;

  Vector inputs = new Vector();

  Generator generator = null;
  PropertiesDialog propertiesDialog;

  boolean selected = false;

  // space to reserve below inputs
  Dimension reserveSpace = null;
  Point reservePosition;           // calculated during first call to draw()
  static final int RESERVE_SOUTH = 0;
  static final int RESERVE_EAST = 1;
  int reserveWhere = RESERVE_EAST;

  GeneratorBox (SynthCanvas pa, String identifier) {
    this.pa = pa;
    this.identifier = identifier;
  }

  void newGenerator (Synthesizer synth) { };

  void connectGenerator (Synthesizer synth) 
    throws SynthIfException
  { };

  void write (DataOutputStream out) throws IOException {
    out.writeBytes (identifier);
    out.writeInt (topLeft.x);
    out.writeInt (topLeft.y);
    out.writeBoolean (selected);
  }

  void read (DataInputStream in) throws IOException, FileFormatException {
    int x, y;
    x = in.readInt();
    y = in.readInt();
    topLeft = new Point (x, y);
    selected = in.readBoolean();
  }

  void addInput (Input i) {
    inputs.addElement (i);
  }

  void setLabel (String label) {
    this.label = label;
  }

  // Calculates rectangles based on topLeft and insideSize
  void calcRects() {
    if (insideSize == null) return;
    int w = insideSize.width;
    if (labelFm != null && label != null) {
      if (w < labelFm.stringWidth (label) + 3)
        w = labelFm.stringWidth (label) + 3;
    }

    wholeRect.reshape (topLeft.x, topLeft.y, w + 2 * borderSize,
		       insideSize.height + borderSize + topBorderSize);
    insideRect.reshape (topLeft.x + borderSize,
                        topLeft.y + topBorderSize,
			w, insideSize.height);
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

  void move (int x, int y) {
    topLeft = new Point (x, y);
    calcRects();
  }

  void translate (int dx, int dy) {
    topLeft.translate (dx, dy);
    calcRects();
  }

  boolean inBorder (int x, int y) {
    return wholeRect.inside (x, y) && !insideRect.inside (x, y);
  }

  boolean inside (int x, int y) {
    return wholeRect.inside (x, y);
  }

  Input inInput (int x, int y) {
    for (int i = 0; i < inputs.size(); i++) {
      if (((Input)inputs.elementAt(i)).inside (x - insideRect.x, y - insideRect.y))
        return (Input)inputs.elementAt(i);
    }
    return null;
  }

  Point getPosition() {
    return topLeft;
  }

  Dimension getSize() {
    return new Dimension (wholeRect.width, wholeRect.height);
  }

  Rectangle getRect() {
    return new Rectangle (wholeRect.x, wholeRect.y,
                          wholeRect.width, wholeRect.height);
  }

  Point getNearestEdge (Point dp) {
    Rectangle sr = wholeRect;
    Point sp = getCenter();
    int dx = dp.x - sp.x;
    int dy = dp.y - sp.y;
    double a = Math.abs (dy / (double) dx);
    double b = Math.abs (sr.height / (double) sr.width);
    if (a < b) {
      sp.x += sr.width / 2 * ((dx < 0) ? -1 : 1);
      sp.y += sr.width / 2 * a * ((dy < 0) ? -1 : 1);
    } else {
      sp.y += sr.height / 2 * ((dy < 0) ? -1 : 1);
      sp.x += sr.height / 2 / a * ((dx < 0) ? -1 : 1);
    }
    return sp;
  }

  Point getCenter() {
    return new Point (wholeRect.x + wholeRect.width/2,
                      wholeRect.y + wholeRect.height/2);
  }

  PropertiesDialog getProperties (Frame f) {
    return null; 
  }

  void showProperties (Frame f) {
    if (propertiesDialog == null)
      propertiesDialog = getProperties (f);
    if (propertiesDialog != null) 
      propertiesDialog.show();
  }

  public void hideProperties() {
    if (propertiesDialog != null) {
      propertiesDialog.hide();
      propertiesDialog.dispose();
      propertiesDialog = null;
    }
  }

  void dispose() {
    if (propertiesDialog != null)
      hideProperties();
  }

  void repaint() {
    pa.repaint();
  }

  void draw (Graphics g) {
    if (!knowSize) {
      Dimension d;
      Input e;
      int maxHeight = -1;

      if (labelFm == null) {
        labelFm = g.getFontMetrics (labelFont);
        topBorderSize = labelFm.getHeight() + 2;
      }

      insideSize = new Dimension (inputSpace, inputSpace * 2);

      for (int i = 0; i < inputs.size(); i++) {
        d = (e = (Input)inputs.elementAt(i)).calcSize(g);
	e.move (insideSize.width, inputSpace);
	insideSize.width += d.width + inputSpace;
	if (d.height > maxHeight) maxHeight = d.height;
      }
      insideSize.height += maxHeight;

      if (reserveSpace != null) {
        if (reserveWhere == RESERVE_SOUTH) {
	  if (insideSize.width < reserveSpace.width + inputSpace*2) 
	    insideSize.width = reserveSpace.width + inputSpace*2;
	  reservePosition = new Point (
	         insideSize.width/2 - reserveSpace.width/2, insideSize.height);
          insideSize.height += inputSpace + reserveSpace.height;
	} else {
	  if (insideSize.height < reserveSpace.height + inputSpace*2)
	    insideSize.height = reserveSpace.height + inputSpace*2;
	  reservePosition = new Point (
                insideSize.width, insideSize.height/2 - reserveSpace.height/2);
	  insideSize.width += reserveSpace.width + inputSpace;
	}
      }

      calcRects();
    }

    Rectangle wr = wholeRect;
    Rectangle ir = insideRect;

    g.setColor (selected ? labelHiBgColor : labelBgColor);
    g.fillRect (wr.x, wr.y, wr.width, topBorderSize);
    g.setColor (color);
    g.fillRect (wr.x, wr.y + topBorderSize, wr.width, wr.height-topBorderSize);
    g.setColor (shadeColor);
    g.drawLine (wr.x + wr.width - 1, wr.y, wr.x + wr.width - 1,
		wr.y + wr.height - 1);
    g.drawLine (wr.x, wr.y + wr.height - 1, wr.x + wr.width - 1,
		wr.y + wr.height - 1);
    g.setColor (Color.black);
    g.drawLine (wr.x, wr.y + topBorderSize - 1, wr.x + wr.width - 1,
		wr.y + topBorderSize - 1);
    g.setColor (hiColor);
    g.drawLine (wr.x, wr.y, wr.x + wr.width - 1, wr.y);
    g.drawLine (wr.x, wr.y, wr.x, wr.y + wr.height - 1);

    g.setColor (selected ? labelHiColor: labelColor);
    g.setFont (labelFont);
    g.drawString (label, wr.x + wr.width/2 - labelFm.stringWidth(label)/2,
                         wr.y+labelFm.getAscent()+2);

    for (int i = 0; i < inputs.size(); i++) {
      ((Input)inputs.elementAt(i)).draw (g);
    }
  }

}
