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

package zrs.wave;

import java.awt.*;
import zrs.ui.ExceptionDialog;

class AmplifyDialog extends Dialog {

  static double factor = 1.0;

  WaveWindow pa;

  Button okayButton;
  Button cancelButton;
  TextField factorField;

  AmplifyDialog (WaveWindow parent) {
    super (parent, "Amplify", true);
    this.pa = parent;

    Panel p = new Panel();
    p.setLayout (new FlowLayout (FlowLayout.CENTER, 3, 3));
    p.add (new Label ("Amplification Factor"));
    factorField = new TextField (Double.toString (factor), 6);
    p.add (factorField);
    add ("Center", p);

    p = new Panel();
    p.setLayout (new FlowLayout (FlowLayout.RIGHT, 1, 1));
    p.add (okayButton = new Button ("Okay"));
    p.add (cancelButton = new Button ("Cancel"));
    add ("South", p);

    pack();
    pack();
    pack();
  }

  public boolean handleEvent (Event event) {
    if ((event.id == Event.WINDOW_DESTROY) ||
        (event.id == Event.KEY_PRESS && event.key == 0x1b) ||
	(event.id == Event.ACTION_EVENT && event.target == cancelButton)) 
    {
      hide();
      dispose();
    }
    else if (event.id == Event.ACTION_EVENT && event.target == okayButton) {
      hide();
      dispose();
      try {
        pa.waveCanvas.amplify (
	                Double.valueOf (factorField.getText()).doubleValue());
      }
      catch (Exception e) { new ExceptionDialog (pa, e).show(); }
    }
    else return super.handleEvent (event);
    return true;
  }
}
