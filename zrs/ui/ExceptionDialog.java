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

package zrs.ui;

import java.awt.*;
import java.io.*;

public class ExceptionDialog extends ErrorDialog {

  public ExceptionDialog (Frame parent, Throwable e) {
    super (parent, e.toString(), "Exception");
    try {
      PipedInputStream pis = new PipedInputStream();
      new ExceptionDialogThread (e, new PipedOutputStream (pis)).start();
      DataInputStream dis = new DataInputStream (pis);
      String s = "";
      StringBuffer details = new StringBuffer (1024);
      try {
	for (;;) {
	  s = dis.readLine();
	  if (s == null) break;
	  details.append (s);
	  details.append ('\n');
	}
      } catch (EOFException e2) {}
      if (details.length() < 1)
        e.printStackTrace();
      else
        setDetails (details.toString());
      pis.close();
    }
    catch (Exception e2) { e.printStackTrace(); }
  }

}

class ExceptionDialogThread extends Thread {

  Throwable exception;
  PipedOutputStream out;

  ExceptionDialogThread (Throwable exception, PipedOutputStream out) {
    this.exception = exception;
    this.out = out;
  }

  public void run() {
    PrintStream ps = new PrintStream (out);
    exception.printStackTrace (ps);
    ps.close();
    try { out.close(); }
    catch (IOException e) { e.printStackTrace(); }
  }
  
}
