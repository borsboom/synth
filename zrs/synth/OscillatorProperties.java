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
import zrs.wave.*;
import zrs.ui.*;
import zrs.misc.WindowRegistry;

class OscillatorProperties
      extends PropertiesDialog
      implements MyFileDialogObserver, WaveWindowObserver
{

  OscillatorBox pa;
  Frame paFrame;

  CheckboxGroup cbg;
  Checkbox sinBox;
  Checkbox triangleBox;
  Checkbox squareBox;
  Checkbox sawtoothBox;
  Checkbox waveBox;
  Checkbox interpolateBox;

  WaveCanvas waveCanvas;
  Button loadButton;
  Button updateButton;
  Button editButton;

  WaveWindow waveWindow = null;

  void addComponent (Component a, Panel p, GridBagLayout g,
                     GridBagConstraints c) {
    g.setConstraints (a, c);
    p.add (a);
  }

  OscillatorProperties (Frame f, OscillatorBox parent) {
    super (f, "Oscillator Properties", parent);
    this.pa = parent;
    paFrame = f;

    Panel p2 = new Panel();
    GridBagLayout gbl2 = new GridBagLayout();
    p2.setLayout (gbl2);

    GridBagLayout gbl = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    Panel p = new Panel();
    p.setLayout (gbl);
    c.gridx = c.gridy = 0;
    c.insets = new Insets (2, 2, 2, 2);
    c.anchor = GridBagConstraints.CENTER;
    addComponent (new Label ("Wave Type"), p, gbl, c);
    cbg = new CheckboxGroup();
    c.gridy++;
    c.anchor = GridBagConstraints.WEST;
    c.insets.bottom = 0;
    c.insets.top = 0;
    addComponent (sinBox = new Checkbox ("Sin", cbg, pa.type==pa.SIN), p,gbl,c);
    c.gridy++;
    addComponent (triangleBox = new Checkbox ("Triangle", cbg, pa.type==pa.TRIANGLE), p,gbl,c);
    c.gridy++;
    addComponent (squareBox = new Checkbox ("Square", cbg, pa.type==pa.SQUARE), p,gbl,c);
    c.gridy++;
    addComponent (sawtoothBox = new Checkbox ("Sawtooth", cbg, pa.type==pa.SAWTOOTH), p,gbl,c);
    c.gridy++;
    c.insets.bottom = 2;
    addComponent (waveBox = new Checkbox ("Custom", cbg, pa.type==pa.WAVE), p,gbl,c);

    c.gridx = c.gridy = 0;
    c.insets = new Insets (2, 2, 2, 2);
    gbl2.setConstraints (p, c);
    p2.add (p);

    gbl = new GridBagLayout();
    c = new GridBagConstraints();
    p = new Panel();
    p.setLayout (gbl);
    c.gridx = 0;
    c.gridy = 0;
    c.insets = new Insets (2, 2, 2, 2);
    c.anchor = GridBagConstraints.CENTER;
    addComponent (new Label ("Custom Wave"), p, gbl, c);
    c.gridy++;
    c.insets.top = 0;
    c.weightx = c.weighty = 1.0;
    c.fill = GridBagConstraints.BOTH;
    addComponent (waveCanvas = new WaveCanvas ((pa.wave==null)?null:(Wave)pa.wave.clone(), new Dimension(96,38)), p,gbl,c);
    c.gridy++;
    c.insets.bottom = 0;
    c.weightx = c.weighty = 0.0;
    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.NORTHWEST;
    addComponent (interpolateBox = new Checkbox ("Interpolate"), p, gbl, c);
    interpolateBox.setState (pa.interpolate);
    c.gridy++;
    c.anchor = GridBagConstraints.CENTER;
    addComponent (loadButton = new Button ("Load ..."), p, gbl, c);
    c.gridy++;
    addComponent (updateButton = new Button ("Update"), p, gbl, c);
    updateButton.disable();
    c.gridy++;
    c.insets.bottom = 2;
    addComponent (editButton = new Button ("Edit ..."), p, gbl, c);

    c.gridx = 1;
    c.gridy = 0;
    c.insets = new Insets (2, 2, 2, 2);
    c.weightx = c.weighty = 1.0;
    c.fill = GridBagConstraints.BOTH;
    gbl2.setConstraints (p, c);
    p2.add (p);

    add ("Center", p2);
  }

  public void waveWindowEvent (WaveWindow waveWindow, int action) {
    if (action == WaveWindow.CLOSE)
      updateButton.disable();
  }

  public void apply() {
    Checkbox cur = cbg.getCurrent();
    pa.wave=(waveCanvas.getWave()==null)?null:(Wave)waveCanvas.getWave().clone();
    pa.interpolate = interpolateBox.getState();
    if (cur == sinBox)
      pa.type = pa.SIN;
    else if (cur == squareBox)
      pa.type = pa.SQUARE;
    else if (cur == triangleBox)
      pa.type = pa.TRIANGLE;
    else if (cur == sawtoothBox)
      pa.type = pa.SAWTOOTH;
    else if (cur == waveBox) {
      pa.type = pa.WAVE;
    }
    pa.repaint();
  }

  public void fileDialogAction (MyFileDialog dia, int action) {
    if (action == MyFileDialog.OKAY) {
      WindowRegistry.saveCursors();
      WindowRegistry.setAllCursors (Frame.WAIT_CURSOR);
      InputStream in = null;
      try {
	in = dia.getInputStream();;
	waveCanvas.setWave (new Wave (in));
      }
      catch (Exception e) { new ExceptionDialog (paFrame, e).show(); }
      finally {
	try {
          if (in != null) in.close();
	} catch (IOException e) {}
        WindowRegistry.restoreCursors();
      }
    }
  }

  public boolean action (Event event, Object arg) {
    if (event.target == loadButton) {
      MyFileDialog f = new MyFileDialog (this, paFrame, "Load Wave", MyFileDialog.LOAD);
      f.setAllowURL (true);
      f.show();
    }
    else if (event.target == updateButton) {
      if (waveWindow != null)
        waveCanvas.setWave ((waveWindow.getWave()==null)?null:(Wave)waveWindow.getWave().clone());
    }
    else if (event.target == editButton) {
      waveWindow = new WaveWindow ((waveCanvas.getWave()==null)?null:(Wave)waveCanvas.getWave().clone(), "Wave from Oscillator in " + pa.pa.pa.name);
      waveWindow.setObserver (this);
      waveWindow.show();
      updateButton.enable();
    }
    else return super.action (event, arg);
    return true;
  }

}
