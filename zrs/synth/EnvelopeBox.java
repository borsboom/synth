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
import java.util.*;
import zrs.synthgen.*;
import zrs.ui.PropertiesDialog;

class EnvelopeBox extends GeneratorBox {
  static final String IDENT = "Env ";

  Envelope envelope = new Envelope();

  EnvelopeBox (SynthCanvas pa) {
    super (pa, IDENT);
    setLabel ("Envelope");
    reserveSpace = new Dimension (48, 24);
    reserveWhere = RESERVE_EAST;
    addInput (new Input (this, "peak"));
    envelope.addPoint (new EnvelopePoint (0, 0));
    envelope.addPoint (new EnvelopePoint (1, 0.20));
    envelope.addPoint (new EnvelopePoint (0.666, 0.4));
    envelope.addPoint (new EnvelopePoint (0.666, 0.8));
    envelope.addPoint (new EnvelopePoint (0, 1));
  }

  void newGenerator (Synthesizer synth) {
    synth.add (generator = new EnvelopeGenerator (synth));
  }

  void connectGenerator (Synthesizer synth) throws SynthIfException {
    double[] points = new double [envelope.size()];
    double[] times = new double [envelope.size() - 1];

    EnvelopePoint p = envelope.pointAt (0);
    points[0] = p.value;
    double lastTime = p.time;
    double thisTime;
    for (int i = 1; i < envelope.size(); i++) {
      p = envelope.pointAt (i);
      points[i] = p.value;
      thisTime = p.time;
      times[i-1] = thisTime - lastTime;
      lastTime = thisTime;
    }

    ((EnvelopeGenerator)generator).setParameters (
      ((Input)inputs.elementAt (0)).getGenerator(),
      points, times, -1, -1
    );
  }

  PropertiesDialog getProperties (Frame f) {
    return new EnvelopeProperties (f, this);
  }

  void write (DataOutputStream out) throws IOException {
    super.write (out);
    out.writeInt (envelope.size());
    Enumeration e = envelope.elements();
    while (e.hasMoreElements()) {
      EnvelopePoint p = (EnvelopePoint) e.nextElement();
      out.writeDouble (p.value);
      out.writeDouble (p.time);
    }
  }

  void read (DataInputStream in) throws IOException, FileFormatException {
    super.read (in);
    envelope = new Envelope();
    int n = in.readInt();
    for (int i = 0; i < n; i++) {
      double v = in.readDouble();
      double t = in.readDouble();
      envelope.addPoint (new EnvelopePoint (v, t));
    }
  }

  void draw (Graphics g) {
    super.draw (g);

    int x = insideRect.x + reservePosition.x;
    int y = insideRect.y + reservePosition.y;
    int w = reserveSpace.width;
    int h = reserveSpace.height;

    g.setColor (Color.black);
    g.fillRect (x, y, w, h);
    g.setColor (Color.darkGray);
    g.drawLine (x, y, x+w-1, y);
    g.drawLine (x, y, x, y+h-1);
    g.setColor (Color.white);
    g.drawLine (x, y+h-1, x+w-1, y+h-1);
    g.drawLine (x+w-1, y, x+w-1, y+h-1);
    EnvelopeCanvas.drawEnvelope (g, envelope, x+1, y+1, w-2, h-2,
                         -1, -1, -1, false, null);
 }
}
