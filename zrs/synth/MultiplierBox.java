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

class MultiplierBox extends GeneratorBox {
  static final String IDENT = "Mult";

  MultiplierBox (SynthCanvas pa) {
    super(pa, IDENT);
    setLabel ("Multiplier");
    addInput (new Input (this, "a"));
    addInput (new Input (this, "b"));
  }

  void newGenerator (Synthesizer synth) {
    synth.add (generator = new Multiplier (synth));
  }

  void connectGenerator (Synthesizer synth) 
    throws SynthIfException
  {
    ((Multiplier)generator).setParameters (
      ((Input)inputs.elementAt (0)).getGenerator(),
      ((Input)inputs.elementAt (1)).getGenerator()
    );
  }
}


