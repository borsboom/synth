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

public class Filter extends Generator {
  Generator input;

  double[] a;
  double[] b;

  double[] x;
  double[] y;

  int curx;
  int cury;

  public Filter (Synthesizer parent) {
    super (parent);
  }

  public Filter (Synthesizer parent, Generator input, double[] a, double[] b) 
    throws SynthesizerException
  {
    this (parent);
    setParameters (input, a, b);
  }

  public void setParameters (Generator input, double[] a, double[]b) 
  {
    this.input = input;
    this.a = a;
    this.b = b;

    if (b != null) {
      x = new double [b.length];
      for (int i = 0; i < x.length; i++) x[i] = 0;
    }
    if (a != null) {
      y = new double [a.length];
      for (int i = 0; i < y.length; i++) y[i] = 0;
    }

    curx = cury = 0;
  }

  double nextValue() throws SynthesizerException {
    double v = 0;
    if (b != null) {
      x[curx] = input.getValue();
      for (int i = 0; i < b.length; i++)
        v += b[i] * x [(curx + i) % x.length];
      if (--curx < 0) curx += x.length;
    }
    if (a != null) {
      for (int i = 0; i < a.length; i++)
        v += a[i] * y [(cury + i) % y.length];
      y[cury] = v;
      if (--cury < 0) cury += y.length;
    }
    return v;
  }

}
