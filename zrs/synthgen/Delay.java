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

public class Delay extends Generator {
  Generator input;
  Generator seconds;
  double maxSeconds;

  double[] values;
  int curPos = 0;

  public Delay (Synthesizer parent) {
    super (parent);
  }

  public Delay (Synthesizer parent, Generator input, Generator seconds,
                double maxSeconds) 
  {
    this (parent);
    setParameters (input, seconds, maxSeconds);
  }

  public void setParameters (Generator input, Generator seconds,
                             double maxSeconds) 
  {
    this.input = input;
    this.seconds = seconds;
    this.maxSeconds = maxSeconds;

    values = new double [(int)Math.ceil (maxSeconds * parent.samplingRate) + 1];
    for (int i = 0; i < values.length; i++)
      values [i] = 0.0;
  }

  double nextValue() throws SynthesizerException {
    values [curPos] = input.getValue();
    int n = (int) (seconds.getValue() * parent.samplingRate);
    if (n < 0)
      throw new SynthesizerException ("Negative Delay");
    if (n >= values.length)
      throw new SynthesizerException ("Delay too large.  Increase maximum.");
    double v = values [(curPos + n) % values.length];
    if (--curPos < 0) curPos += values.length;
    return v;
  }
}
