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

class EnvelopeCanvas extends Canvas {

  static final Color bgColor = Color.black;
  static final Color gridColor = Color.gray;
  static final Color pointColor = Color.blue;
  static final Color selectedPointColor = Color.red;
  static final Color envColor = Color.green;
  static final Color axisColor = new Color (0, 128, 0);
  static final Color coordColor = Color.white;
  static final int pointSize = 5;
  static final Dimension minSize = new Dimension (64, 32);
  static final Font coordFont = new Font ("Helvetica", Font.PLAIN, 10);
  static FontMetrics coordFm = null;

  Dimension prefSize = new Dimension (256, 64);

  EnvelopePanel pa;

  EnvelopeCanvas (EnvelopePanel parent) {
    this.pa = parent;
    setBackground (bgColor);
  }

  EnvelopeCanvas (EnvelopePanel parent, int width, int height) {
    this (parent);
    this.prefSize = new Dimension (width, height);
  }

  public Dimension minimumSize() {
    return minSize;
  }

  public Dimension preferredSize() {
    return prefSize;
  }

  static Point envelopeToPoint (EnvelopePoint p, double minV, double maxV, double maxT, int w, int h) {
    return new Point (
      (int)(p.time * (w / maxT)),
      (int)((h-1) * (1 - (p.value - minV) / (maxV - minV)))
    );
  }

  static EnvelopePoint pointToEnvelope (Point p, double minV, double maxV, double maxT, int w, int h) {
    return new EnvelopePoint (
      maxV - p.y * (maxV - minV) / (double)(h-1),
      p.x * maxT / w
    );
  }

  public boolean mouseDown (Event event, int x, int y) {
    Enumeration e = pa.envelope.elements();
    int w = size().width;
    int h = size().height;
    EnvelopePoint found = null;
    while (e.hasMoreElements()) {
      EnvelopePoint ep = (EnvelopePoint) e.nextElement();
      Point p = envelopeToPoint (ep, pa.minValue, pa.maxValue, pa.maxTime, w,h);
      if (x >= p.x-pointSize/2 && x <= p.x+pointSize/2 &&
          y >= p.y-pointSize/2 && y <= p.y+pointSize/2) {
	found = ep;
	break;
      }
    }
    if (found != null)
      pa.selectedPoint = found;
    else {
      pa.selectedPoint = pointToEnvelope (
                new Point (x, y), pa.minValue, pa.maxValue, pa.maxTime, w, h);
      pa.envelope.addPoint (pa.selectedPoint);
    }
    pa.deleteButton.enable();
    repaint();

    return true;
  }

  public boolean mouseDrag (Event event, int x, int y) {
    if (pa.selectedPoint != null) {
      int w = size().width;
      int h = size().height;
      if (x < 0) x = 0; if (x >= w) x = w - 1;
      if (y < 0) y = 0; if (y >= h) y = h - 1;
      EnvelopePoint p = pointToEnvelope (
                 new Point (x, y), pa.minValue, pa.maxValue, pa.maxTime, w, h);
      pa.envelope.movePoint (pa.selectedPoint, p);
      repaint();
    }
    return true;
  }

  public static void drawEnvelope (Graphics g, Envelope env,
                                   int x, int y, int w, int h,
				   double minV, double maxV, double maxT,
                                   boolean drawPoints, EnvelopePoint sel)
  {

    if (env != null) {

      Enumeration e;

      if (maxT < 0) {
        maxV = 1.0;
        minV = 0.0;
        maxT = 1.0;

        e = env.elements();
        while (e.hasMoreElements()) {
          EnvelopePoint p = (EnvelopePoint) e.nextElement();
          if (p.value > maxV) maxV = p.value;
          if (p.value < minV) minV = p.value;
          if (p.time > maxT) maxT = p.time;
        }
      }

    g.setColor (axisColor);
    g.drawLine (x, 
      y + envelopeToPoint (new EnvelopePoint (0, 0), minV, maxV, maxT, w, h).y,
      x + w - 1,
      y + envelopeToPoint (new EnvelopePoint (0, 0), minV, maxV, maxT, w, h).y);

      e = env.elements();

      EnvelopePoint lastp = (EnvelopePoint) e.nextElement();
      Point last = envelopeToPoint (lastp, minV, maxV, maxT, w, h);
      
      while (e.hasMoreElements()) {

        EnvelopePoint ep = (EnvelopePoint) e.nextElement();
        Point p = envelopeToPoint (ep, minV, maxV, maxT, w, h);

        g.setColor (envColor);
        g.drawLine (x+last.x, y+last.y, x+p.x, y+p.y);
	if (drawPoints) {
          g.setColor ((sel==lastp)?selectedPointColor:pointColor);
          g.fillRect (x + last.x - pointSize/2,
	              y + last.y - pointSize/2,
		      pointSize, pointSize);
        }

        last = p;
        lastp = ep;
      }
      if (drawPoints) {
        g.setColor ((sel==lastp) ? selectedPointColor : pointColor);
        g.fillRect (x + last.x - pointSize/2,
	            y + last.y - pointSize/2,
		    pointSize, pointSize);
      }
    }

  }


  public void paint (Graphics g) {

    if (coordFm == null) 
      coordFm = g.getFontMetrics (coordFont);

    int w = size().width;
    int h = size().height;

    g.setColor (Color.darkGray);
    g.drawLine (0, 0, w-1, 0);
    g.drawLine (0, 0, 0, h-1);
    g.setColor (Color.white);
    g.drawLine (w-1, 0, w-1, h-1);
    g.drawLine (0, h-1, w-1, h-1);

    drawEnvelope (g, pa.envelope, 1, 1, w-2, h-2,
                  pa.minValue, pa.maxValue, pa.maxTime,
		  true, pa.selectedPoint);

    if (pa.envelope != null && pa.selectedPoint != null) {
      g.setFont (coordFont);
      g.setColor (coordColor);
      String s = Double.toString (pa.selectedPoint.value) + ", " +
                 Double.toString (pa.selectedPoint.time);
      g.drawString (s, size().width - coordFm.stringWidth (s) - 1,
                  h - coordFm.getHeight() + coordFm.getAscent());
    }
  }
}

class EnvelopePanel extends Panel {

  EnvelopeCanvas envelopeCanvas;
  Button deleteButton;
  Button updateButton;
  Button fitButton;
  TextField timeField;
  TextField minField;
  TextField maxField;

  Envelope envelope;
  EnvelopePoint selectedPoint;

  double maxValue;
  double minValue;
  double maxTime;

  EnvelopePanel (Envelope envelope) {
    this.envelope = envelope;
    envelopeCanvas = new EnvelopeCanvas (this);
    initGUI();
  }

  EnvelopePanel (Envelope envelope, int width, int height) {
    this.envelope = envelope;
    envelopeCanvas = new EnvelopeCanvas (this, width, height);
    initGUI();
  }

  void fit() {
    maxValue = 1.0;
    minValue = 0.0;
    maxTime = 1.0;

    Enumeration e = envelope.elements();
    while (e.hasMoreElements()) {
      EnvelopePoint p = (EnvelopePoint) e.nextElement();
      if (p.value > maxValue) maxValue = p.value;
      if (p.value < minValue) minValue = p.value;
      if (p.time > maxTime) maxTime = p.time;
    }

    minField.setText (new Double (minValue).toString());
    maxField.setText (new Double (maxValue).toString());
    timeField.setText (new Double (maxTime).toString());

  }

  void initGUI () {

    Label minLabel = new Label ("Minimum:");
    minField = new TextField (6);
    Label maxLabel = new Label ("Maximum:");
    maxField = new TextField (6);
    Label timeLabel = new Label ("Time:");
    timeField = new TextField (6);

    fit();

    deleteButton = new Button ("Delete");
    deleteButton.disable();
    updateButton = new Button ("Update");
    fitButton = new Button ("Fit");

    GridBagLayout gbl = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    setLayout (gbl);

    c.gridx = c.gridy = 0;
    c.gridwidth = 3;
    c.fill = GridBagConstraints.BOTH;
    c.weightx = c.weighty = 1.0;
    c.insets = new Insets (4, 4, 3, 4);
    gbl.setConstraints (envelopeCanvas, c);
    add (envelopeCanvas);

    c.weightx = c.weighty = 0;
    c.gridwidth = 1;
    c.gridx = 0;
    c.gridy = 1;
    c.insets.top = 0;
    c.insets.right = 3;
    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.EAST;
    gbl.setConstraints (maxLabel, c);
    add (maxLabel);
    c.gridy++;
    gbl.setConstraints (minLabel, c);
    add (minLabel);
    c.gridy++;
    gbl.setConstraints (timeLabel, c);
    add (timeLabel);

    c.gridx++;
    c.gridy = 1;
    c.anchor = GridBagConstraints.WEST;
    c.insets.left = 0;
    gbl.setConstraints (maxField, c);
    add (maxField);
    c.gridy++;
    gbl.setConstraints (minField, c);
    add (minField);
    c.gridy++;
    gbl.setConstraints (timeField, c);
    add (timeField);

    c.gridx++;
    c.gridy = 1;
    c.anchor = GridBagConstraints.CENTER;
    c.insets.left = 4;
    gbl.setConstraints (deleteButton, c);
    add (deleteButton);
    c.gridy++;
    gbl.setConstraints (updateButton, c);
    add (updateButton);
    c.gridy++;
    gbl.setConstraints (fitButton, c);
    add (fitButton);

  }

  Envelope getEnvelope() {
    return envelope;
  }

  public boolean action (Event event, Object arg) {
    if (event.target == deleteButton) {
      envelope.removePoint (selectedPoint);
      selectedPoint = null;
      deleteButton.disable();
      envelopeCanvas.repaint();
    } 
    else if (event.target == updateButton) {
      maxValue = new Double (maxField.getText()).doubleValue();
      minValue = new Double (minField.getText()).doubleValue();
      maxTime = new Double (timeField.getText()).doubleValue();
      envelopeCanvas.repaint();
    }
    else if (event.target == fitButton) {
      fit();
      envelopeCanvas.repaint();
    }
    else return super.action (event, arg);
    return true;
  }

}
