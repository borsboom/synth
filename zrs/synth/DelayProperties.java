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

import java.awt.*;
import zrs.ui.*;

class DelayProperties extends PropertiesDialog {
  DelayBox pa;

  TextField maxDelayField;

  DelayProperties (Frame f, DelayBox parent) {
    super (f, "Delay Properties", parent);
    this.pa = parent;

    Panel p = new Panel();
    p.setLayout (new FlowLayout (FlowLayout.CENTER, 2, 2));
    p.add (new Label ("Maximum Delay (s)"));
    maxDelayField = new TextField (Double.toString (pa.maxSeconds), 6);
    p.add (maxDelayField);

    add ("Center", p);
  }

  public void apply() {
    pa.maxSeconds = Double.valueOf (maxDelayField.getText()).doubleValue();
  }
}
