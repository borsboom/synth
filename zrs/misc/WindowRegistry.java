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

import java.util.*;

public class WindowRegistry {
  public static Vector windows = new Vector();
  static boolean exitWhenFinished = false;
  static int[] cursors = null;
  static boolean cursorsValid = false;

  public static void add (WindowRegistryAble window) {
    windows.addElement (window);
  }

  public static void remove (WindowRegistryAble window) {
    windows.removeElement (window);
    if (exitWhenFinished && windows.isEmpty())
      System.exit (0);
    cursorsValid = false;
  }

  public static void setExitWhenFinished (boolean exit) {
    exitWhenFinished = exit;
  }

  public static boolean getExitWhenFinished() {
    return exitWhenFinished;
  }

  public static Enumeration elements() {
    return windows.elements();
  }

  public static void saveCursors() {
    cursors = new int [windows.size()];
    Enumeration e = elements();
    int i = 0;
    while (e.hasMoreElements())
      cursors [i++] = ((WindowRegistryAble) e.nextElement()).getCursorType();
    cursorsValid = true;
  }

  public static void restoreCursors() {
    if (cursorsValid) {
      Enumeration e = elements();
      for (int i = 0; i < cursors.length; i++) 
        ((WindowRegistryAble) e.nextElement()).setCursor (cursors[i]);
    }
  }

  public static void setAllCursors (int cursorType) {
    Enumeration e = elements();
    while (e.hasMoreElements()) {
      ((WindowRegistryAble) e.nextElement()).setCursor (cursorType);
    }
  }

}
