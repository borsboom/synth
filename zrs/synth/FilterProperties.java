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
import java.util.*;
import zrs.ui.*;

class FilterProperties extends PropertiesDialog {
  FilterBox pa;

  TextField aField;
  TextField bField;

  FilterProperties (Frame f, FilterBox parent) {
    super (f, "Filter Properties", parent);
    pa = parent;

    Panel p = new Panel();
    GridBagLayout gbl = new GridBagLayout();
    p.setLayout (gbl);
    GridBagConstraints c = new GridBagConstraints();

    Label l = new Label ("a(i) = ");
    c.insets = new Insets (2, 2, 2, 0);
    gbl.setConstraints (l, c);
    p.add (l);

    aField = new TextField (48);
    if (pa.a != null && pa.a.length > 0) {
      StringBuffer s = new StringBuffer (Double.toString (pa.a[0]));
      for (int i = 1; i < pa.a.length; i++) {
        s.append (", ");
	s.append (Double.toString (pa.a[i]));
      }
      aField.setText (s.toString());
    }
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.weightx = 1;
    c.fill = GridBagConstraints.BOTH;
    c.insets.left = 0;
    c.insets.right = 2;
    gbl.setConstraints (aField, c);
    p.add (aField);

    l = new Label ("b(i) = ");
    c.gridwidth = 1;
    c.weightx = 0;
    c.insets.left = 2;
    c.insets.right = 0;
    c.fill = GridBagConstraints.NONE;
    gbl.setConstraints (l, c);
    p.add (l);

    bField = new TextField (48);
    if (pa.b != null && pa.b.length > 0) {
      StringBuffer s = new StringBuffer (Double.toString (pa.b[0]));
      for (int i = 1; i < pa.b.length; i++) {
        s.append (", ");
	s.append (Double.toString (pa.b[i]));
      }
      bField.setText (s.toString());
    }
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.insets.left = 0;
    c.insets.right = 2;
    c.fill = GridBagConstraints.BOTH;
    gbl.setConstraints (bField, c);
    p.add (bField);

    add ("Center", p);
  }

  public void apply() {
    StringTokenizer st = new StringTokenizer (aField.getText(), ", ");
    if (st.countTokens() < 1)
      pa.a = null;
    else {
      pa.a = new double [st.countTokens()];
      int i = 0;
      while (st.hasMoreTokens())
        pa.a[i++] = Double.valueOf (st.nextToken()).doubleValue();
    }
    st = new StringTokenizer (bField.getText(), ", ");
    if (st.countTokens() < 1)
      pa.b = null;
    else {
      pa.b = new double [st.countTokens()];
      int i = 0;
      while (st.hasMoreTokens())
        pa.b[i++] = Double.valueOf (st.nextToken()).doubleValue();
    }
  }
}
