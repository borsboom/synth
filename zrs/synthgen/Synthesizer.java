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

import java.util.Vector;
import java.util.Enumeration;

public class Synthesizer extends Generator {

  int samplingRate;
  int sampleNo;
  double currentTime;
  double timeIncrement;

  public double maxValue;

  Vector generators;
  Generator outputGenerator;

  public Synthesizer (int samplingRate) {
    super (null);
    init (samplingRate);
  }

  public Synthesizer (Synthesizer parent) {
    super (parent);
    init (parent.samplingRate);
  }

  private void init (int samplingRate) {
    this.samplingRate = samplingRate;
    timeIncrement = 1 / (double)samplingRate;
    maxValue = 0.0;
    sampleNo = -1;
    generators = new Vector ();
  }

  public Generator add (Generator gen) {
    generators.addElement (gen);
    return gen;
  }

  public void setOutput (Generator outputGenerator) {
    this.outputGenerator = outputGenerator;
  }

  public double nextValue() throws SynthesizerException {
    sampleNo++;
    currentTime = (double)sampleNo * timeIncrement;

    Enumeration e = generators.elements();
    while (e.hasMoreElements())
      ((Generator)e.nextElement()).recalculate();

    double v = outputGenerator.getValue();

    if (Math.abs (v) > maxValue)
      maxValue = Math.abs(v);

    return v;
  }

}
