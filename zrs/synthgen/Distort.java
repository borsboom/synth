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

public class Distort extends Generator {
  Generator in;
  
  public Distort (Synthesizer parent) {
    super (parent);
  }

  public Distort (Synthesizer parent, Generator in) {
    this (parent);
    setParameters (in);
  }

  public void setParameters (Generator in) {
    this.in = in;
  }

  double nextValue() throws SynthesizerException {
    double x = in.getValue();
    return (Math.exp (x) - Math.exp (-x)) / (Math.exp (x) + Math.exp (-x));
  }

}
