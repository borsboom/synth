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

class DelayBox extends GeneratorBox {
  static final String IDENT = "Dely";

  double maxSeconds = 0.10;

  DelayBox (SynthCanvas pa) {
    super (pa, IDENT);
    setLabel ("Delay");
    addInput (new Input (this, "in"));
    addInput (new Input (this, "delay"));
  }

  void write (DataOutputStream out) throws IOException {
    super.write (out);
    out.writeDouble (maxSeconds);
  }

  void read (DataInputStream in) throws IOException, FileFormatException {
    super.read (in);
    maxSeconds = in.readDouble();
  }

  PropertiesDialog getProperties (Frame f) {
    return new DelayProperties (f, this);
  }

  void newGenerator (Synthesizer synth) {
    synth.add (generator = new Delay (synth));
  }

  void connectGenerator (Synthesizer synth) throws SynthIfException {
    ((Delay) generator).setParameters (
      ((Input)inputs.elementAt (0)).getGenerator(),
      ((Input)inputs.elementAt (1)).getGenerator(),
      maxSeconds
    );
  }
}
