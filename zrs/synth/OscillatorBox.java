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
import zrs.wave.*;
import zrs.ui.PropertiesDialog;

class OscillatorBox extends GeneratorBox {
  static final String IDENT = "Osc ";

  static final int SIN = 0;
  static final int TRIANGLE = 1;
  static final int SQUARE = 2;
  static final int SAWTOOTH = 3;
  static final int WAVE = 4;

  static final Wave sinWave =
        new Wave (StaticWaves.SIN_WAVE, StaticWaves.SIN_WAVE.length);
  static final Wave triangleWave =
        new Wave (StaticWaves.TRIANGLE_WAVE, StaticWaves.TRIANGLE_WAVE.length);
  static final Wave squareWave =
        new Wave (StaticWaves.SQUARE_WAVE, StaticWaves.SQUARE_WAVE.length);
  static final Wave sawtoothWave =
        new Wave (StaticWaves.SAWTOOTH_WAVE, StaticWaves.SAWTOOTH_WAVE.length);

  int type = SIN;
  Wave wave = null;
  boolean interpolate = true;

  OscillatorBox (SynthCanvas pa) {
    super(pa, IDENT);
    setLabel ("Oscillator");
    reserveSpace = new Dimension (66, 34);
    reserveWhere = RESERVE_SOUTH;
    addInput (new Input (this, "freq"));
    addInput (new Input (this, "phase", true, false, true));
    addInput (new Input (this, "amp"));
  }

  void write (DataOutputStream out) throws IOException {
    super.write (out);
    out.writeInt (type);
    if (type == WAVE) {
      out.writeBoolean (interpolate);
      out.writeBoolean (wave != null);
      if (wave != null)
        wave.write (out);
    }
  }

  void read (DataInputStream in) throws IOException, FileFormatException {
    super.read (in);
    type = in.readInt();
    if (type == WAVE) {
      try {
        interpolate = in.readBoolean();
	boolean f = in.readBoolean();
	if (f) wave = new Wave (in);
	else wave = null;
      }
      catch (WaveFormatException e) {
        throw new FileFormatException ("Wave format error");
      }
    }
  }

  void newGenerator (Synthesizer synth) {
    if (type == SIN)
      synth.add (generator = new SinGenerator (synth));
    else if (type == TRIANGLE)
      synth.add (generator = new TriangleGenerator (synth));
    else if (type == SQUARE)
      synth.add (generator = new SquareGenerator (synth));
    else if (type == SAWTOOTH)
      synth.add (generator = new SawtoothGenerator (synth));
    else if (type == WAVE)
      synth.add (generator = new WaveGenerator (synth));
  }

  void connectGenerator (Synthesizer synth)
    throws SynthIfException
  {
    Generator freq = ((Input)inputs.elementAt (0)).getGenerator();
    Generator phase = ((Input)inputs.elementAt (1)).getGenerator();
    if (phase == null)
      synth.add (phase = new Constant (synth, 0));
    Generator amp = ((Input)inputs.elementAt (2)).getGenerator();
    if (type == SIN)
      ((SinGenerator)generator).setParameters (freq, phase, amp);
    if (type == TRIANGLE)
      ((TriangleGenerator)generator).setParameters (freq, phase, amp);
    if (type == SQUARE)
      ((SquareGenerator)generator).setParameters (freq, phase, amp);
    if (type == SAWTOOTH)
      ((SawtoothGenerator)generator).setParameters (freq, phase, amp);
    if (type == WAVE) {
      if (wave == null)
        throw new SynthIfException ("Empty wave in oscillator");
      ((WaveGenerator)generator).setParameters (wave.getWave(), freq, phase, amp, interpolate);
    }
  }

  PropertiesDialog getProperties (Frame f) {
    return new OscillatorProperties (f, this);
  }

  void draw (Graphics g) {
    super.draw (g);

    int x = insideRect.x + reservePosition.x;
    int y = insideRect.y + reservePosition.y;
    int w = reserveSpace.width;
    int h = reserveSpace.height;

    Wave wave;
    if (type == SIN) wave = sinWave;
    else if (type == TRIANGLE) wave = triangleWave;
    else if (type == SQUARE) wave = squareWave;
    else if (type == SAWTOOTH) wave = sawtoothWave;
    else wave = this.wave;

    g.setColor (Color.black);
    g.fillRect (x, y, w, h);
    g.setColor (Color.darkGray);
    g.drawLine (x, y, x+w-1, y);
    g.drawLine (x, y, x, y+h-1);
    g.setColor (Color.white);
    g.drawLine (x, y+h-1, x+w-1, y+h-1);
    g.drawLine (x+w-1, y, x+w-1, y+h-1);
    WaveCanvas.drawWave (g, wave, x+1, y+1, w-2, h-2,
			 0, (wave==null)?0:wave.length()-1, false, 0, 0);
  }

}
