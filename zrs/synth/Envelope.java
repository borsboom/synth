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

import java.util.*;

class EnvelopePoint {
  double value;
  double time;

  EnvelopePoint (double value, double time) {
    this.value = value;
    this.time = time;
  }
}

class Envelope implements Cloneable {

  Vector points;

  Envelope () {
    points = new Vector();
  }

  public Object clone() {
    Envelope env = new Envelope ();
    env.points = new Vector();
    Enumeration e = points.elements();
    while (e.hasMoreElements()) {
      EnvelopePoint p = (EnvelopePoint) e.nextElement();
      env.points.addElement (new EnvelopePoint (p.value, p.time));
    }
    return env;
  }

  Enumeration elements() {
    return points.elements();
  }

  EnvelopePoint pointAt (int index) {
    return (EnvelopePoint) points.elementAt (index);
  }

  int size() {
    return points.size();
  }

  void removePoint (EnvelopePoint p) {
    if (p != points.firstElement() && p != points.lastElement())
      points.removeElement (p);
  }

  void addPoint (EnvelopePoint p) {
    int i;
    for (i = 0; i < points.size(); i++) {
      EnvelopePoint p2 = (EnvelopePoint) points.elementAt (i);
      if (p2.time > p.time)
        break;
    }
    points.insertElementAt (p, i);
  }
  
  void movePoint (EnvelopePoint p, EnvelopePoint to) {
    boolean noMove = false;
    if (p == points.firstElement())
      noMove = true;
    if (to.time < 0) to.time = 0;
    points.removeElement (p);
    p.value = to.value;
    if (!noMove)
      p.time = to.time;
    addPoint (p);
  }

}

