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

import zrs.synthgen.*;

class AbsBox extends GeneratorBox {
  static final String IDENT = "Abs ";

  AbsBox (SynthCanvas pa) {
    super(pa, IDENT);
    setLabel ("Abs");
    addInput (new Input (this, "in"));
  }

  void newGenerator (Synthesizer synth) {
    synth.add (generator = new Abs (synth));
  }

  void connectGenerator (Synthesizer synth) 
    throws SynthIfException
  {
    ((Abs)generator).setParameters (
      ((Input)inputs.elementAt (0)).getGenerator() );
  }
}


