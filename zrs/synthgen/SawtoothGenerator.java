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

public class SawtoothGenerator extends Generator {
  Generator frequency;
  Generator phase;
  Generator amplitude;
  double curPos = 0.0;

  public SawtoothGenerator (Synthesizer parent) {
    super (parent);
  }

  public SawtoothGenerator (Synthesizer parent, Generator frequency,
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
    double v = ((curPos + phase.getValue()/360.0) % 1.0 * 2.0 - 1.0)
               * amplitude.getValue();
    curPos += frequency.getValue() * parent.timeIncrement;
    curPos %= 1.0;
    if (curPos < 0) curPos += 1.0;
    return v;
  }
}

