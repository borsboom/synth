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
import zrs.ui.*;

class EnvelopeProperties extends PropertiesDialog {
  EnvelopeBox pa;

  EnvelopePanel envelopePanel;

  EnvelopeProperties (Frame f, EnvelopeBox parent) {
    super (f, "Envelope Generator Properties", parent);
    this.pa = parent;

    add ("Center", envelopePanel = new EnvelopePanel ((Envelope)pa.envelope.clone()));
  }

  public void apply() {
    pa.envelope = (Envelope) envelopePanel.getEnvelope().clone();
    pa.repaint();
  }
}
