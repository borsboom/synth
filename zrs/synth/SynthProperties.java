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

class SynthProperties extends PropertiesDialog {

  SynthCanvas pa;

  TextField samplingRateField;
  TextField timeField;

  void addComponent (Component a, Panel p, GridBagLayout g,
                     GridBagConstraints c) {
    g.setConstraints (a, c);
    p.add (a);
  }

  SynthProperties (Frame f, SynthCanvas parent) {
    super (f, "Synthesizer Properties", parent);
    pa = parent;

    Panel p = new Panel();

    GridBagLayout g = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    p.setLayout (g);

    c.anchor = GridBagConstraints.EAST;
    c.insets = new Insets (3, 3, 3, 3);
    addComponent (new Label ("Sampling Rate"), p, g, c);
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.anchor = GridBagConstraints.WEST;
    addComponent (samplingRateField = new TextField (Integer.toString (pa.samplingRate), 8), p, g, c);
    samplingRateField.requestFocus();

    c.gridwidth = 1;
    c.anchor = GridBagConstraints.EAST;
    addComponent (new Label ("Time (s)"), p, g, c);
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.anchor = GridBagConstraints.WEST;
    addComponent (timeField = new TextField (Double.toString (pa.time), 4), p, g, c);

    add ("Center", p);
  }

  public void apply() {
    pa.samplingRate = new Integer (samplingRateField.getText()).intValue();
    pa.time = new Double (timeField.getText()).doubleValue();
  }

}
