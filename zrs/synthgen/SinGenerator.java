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

public class SinGenerator extends Generator {
  Generator frequency;
  Generator phase;
  Generator amplitude;
  double curPos = 0.0;

  public SinGenerator (Synthesizer parent) {
    super (parent);
  }

  public SinGenerator (Synthesizer parent, Generator frequency,
                       Generator phase, Generator amplitude)
  {
    this (parent);
    setParameters (frequency, phase, amplitude);
  }

  public void setParameters (Generator frequency, Generator phase,
                             Generator amplitude) 
  {
    this.frequency = frequency;
    this.phase = phase;
    this.amplitude = amplitude;
  }

  double nextValue() throws SynthesizerException {
    double v = Math.sin (curPos + phase.getValue() * Math.PI / 180.0) 
               * amplitude.getValue();
    curPos += frequency.getValue() * parent.timeIncrement * 2*Math.PI;
    curPos %= 2 * Math.PI;
    if (curPos < 0) curPos += 2 * Math.PI;
    return v;
  }
}
