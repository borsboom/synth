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

package zrs.synthgen;

public class WaveGenerator extends Generator {
  double[] wave;
  Generator freq;
  Generator phase;
  Generator amp;
  double curPos = 0.0;
  boolean interpolate;

  public WaveGenerator (Synthesizer parent) {
    super (parent);
  }

  public WaveGenerator (Synthesizer parent, double[] wave, Generator freq,
                        Generator phase, Generator amp, boolean interpolate) 
  {
    this (parent);
    setParameters (wave, freq, phase, amp, interpolate);
  }

  public void setParameters (double[] wave, Generator freq, Generator phase,
                             Generator amp, boolean interpolate) 
  {
    this.wave = wave;
    this.freq = freq;
    this.phase = phase;
    this.amp = amp;
    this.interpolate = interpolate;
  }

  double nextValue() throws SynthesizerException {
    int i, j;
    double v;
    double x = (curPos + phase.getValue() * wave.length/360.0) % wave.length;
    if (x < 0) x += wave.length;
    i = (int) x;
    if (interpolate) {
      v = ((wave [(i + 1) % wave.length] - wave [i]) * (x % 1.0)
           + wave [i]) * amp.getValue();
    }
    else
      v = wave [i] * amp.getValue();
    curPos += freq.getValue() * (double)wave.length * parent.timeIncrement;
    curPos %= wave.length;
    if (curPos < 0) curPos += wave.length;
    return v;
  }
}
