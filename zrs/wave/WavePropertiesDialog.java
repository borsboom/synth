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

package zrs.wave;

import java.awt.*;
import zrs.ui.*;

class WavePropertiesDialog extends PropertiesDialog {

  WaveWindow waveWindow;

  TextField samplingRateField;
  TextField maxAmplitudeField;

  WavePropertiesDialog (WaveWindow parent) {
    super (parent, "Wave Properties", parent);
    waveWindow = parent;

    Panel p = new Panel();

    GridBagLayout gbl = new GridBagLayout();
    p.setLayout (gbl);
    GridBagConstraints c = new GridBagConstraints();

    Label l = new Label ("Sampling Rate");
    c.gridx = c.gridy = 0;
    c.anchor = GridBagConstraints.EAST;
    c.insets = new Insets (2, 2, 2, 2);
    gbl.setConstraints (l, c);
    p.add (l);
    l = new Label ("Max Amplitude");
    c.gridy++;
    gbl.setConstraints (l, c);
    p.add (l);
    samplingRateField = new TextField (
      Integer.toString (waveWindow.waveCanvas.getWave().getSamplingRate()), 7);
    c.gridx++;
    c.gridy = 0;
    c.anchor = GridBagConstraints.WEST;
    gbl.setConstraints (samplingRateField, c);
    p.add (samplingRateField);
    maxAmplitudeField = new TextField (
      Double.toString (waveWindow.waveCanvas.getWave().getMaxAmplitude()), 7);
    c.gridy++;
    gbl.setConstraints (maxAmplitudeField, c);
    p.add (maxAmplitudeField);

    add ("Center", p);
  }

  public void apply() {
    waveWindow.waveCanvas.getWave().setSamplingRate (
                  Integer.valueOf (samplingRateField.getText()).intValue());
    waveWindow.waveCanvas.getWave().setMaxAmplitude (
                 Double.valueOf (maxAmplitudeField.getText()).doubleValue());
    waveWindow.waveCanvas.repaint();
  }

}
