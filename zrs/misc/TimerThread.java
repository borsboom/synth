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

package zrs.misc;

public class TimerThread extends Thread {
  Timeable what;
  int howOften;
  int numberOfTicks;

  public TimerThread (Timeable what, int howOften, int numberOfTicks) {
    this.what = what;
    this.howOften = howOften;
    this.numberOfTicks = numberOfTicks;
    //setPriority (getPriority() + 1);
  }

  public TimerThread (Timeable what, int howOften) {
    this (what, howOften, -1);
  }

  public void run() {
    for (;;) {
      if (numberOfTicks >= 0)
        if (--numberOfTicks < 0)
	  break;

      try {
        sleep (this.howOften);
      }
      catch (InterruptedException e) {}

      what.tick (this);
    }
  }

}
