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

public class EnvelopeGenerator extends Generator {
  double[] points;
  double[] times;
  int loopStart, loopEnd;
  int currentPoint = 0;
  double pointStartTime = 0.0;
  boolean endReached = false;

  Generator peakAmplitude;

  public EnvelopeGenerator (Synthesizer parent) {
    super (parent);
  }

  public EnvelopeGenerator (Synthesizer parent, Generator peakAmplitude,
            double[] points, double[] times, int loopStart, int loopEnd) 
  {
    this (parent);
    setParameters (peakAmplitude, points, times, loopStart, loopEnd);
  }

  public void setParameters (Generator peakAmplitude, double[] points,
                                 double[] times, int loopStart, int loopEnd)
  {
    this.peakAmplitude = peakAmplitude;
    this.points = points;
    this.times = times;
    this.loopStart = loopStart;
    this.loopEnd = loopEnd;
  }

  double nextValue() throws SynthesizerException {

    if (!endReached && parent.currentTime - pointStartTime >= times[currentPoint]) {
      currentPoint++;
      if (loopEnd > 0 && currentPoint >= loopEnd)
        currentPoint = loopStart;
      if (currentPoint >= points.length-1)
        endReached = true;
      pointStartTime = parent.currentTime;
    }

    if (endReached)
      return points[points.length-1] * peakAmplitude.getValue();

    return ( (parent.currentTime - pointStartTime) /
             times[currentPoint] * 
             (points[currentPoint+1] - points[currentPoint]) +
             points[currentPoint] ) * peakAmplitude.getValue();

  }
}
