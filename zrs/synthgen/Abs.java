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

public class Abs extends Generator {
  Generator gen1;
  
  public Abs (Synthesizer parent) {
    super (parent);
  }

  public Abs (Synthesizer parent, Generator gen1) {
    this (parent);
    setParameters (gen1);
  }

  public void setParameters (Generator gen1) {
    this.gen1 = gen1;
  }

  double nextValue() throws SynthesizerException {
    double temp = gen1.getValue();

    if( temp >= 0 )
      return temp;
    return -temp;
  }

}
