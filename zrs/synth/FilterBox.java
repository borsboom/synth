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

class FilterBox extends GeneratorBox {
  static final String IDENT = "Filt";

  double[] a;
  double[] b;

  FilterBox (SynthCanvas pa) {
    super (pa, IDENT);
    setLabel ("Filter");
    addInput (new Input (this, "in"));
  }

  void write (DataOutputStream out) throws IOException {
    super.write (out);
    if (a == null) out.writeInt (0);
    else {
      out.writeInt (a.length);
      for (int i = 0; i < a.length; i++)
        out.writeDouble (a[i]);
    }
    if (b == null) out.writeInt (0);
    else {
      out.writeInt (b.length);
      for (int i = 0; i < b.length; i++)
        out.writeDouble (b[i]);
    }
  }

  void read (DataInputStream in) throws IOException, FileFormatException {
    super.read (in);
    int n = in.readInt();
    if (n < 1) a = null;
    else {
      a = new double [n];
      for (int i = 0; i < a.length; i++)
        a[i] = in.readDouble();
    }
    n = in.readInt();
    if (n < 1) b = null;
    else {
      b = new double [n];
      for (int i = 0; i < b.length; i++)
        b[i] = in.readDouble();
    }
  }

  PropertiesDialog getProperties (Frame f) {
    return new FilterProperties (f, this);
  }

  void newGenerator (Synthesizer synth) {
    synth.add (generator = new Filter (synth));
  }

  void connectGenerator (Synthesizer synth) 
    throws SynthIfException
  {
    ((Filter) generator).setParameters (
      ((Input) inputs.elementAt (0)).getGenerator(), a, b);
  }
}

