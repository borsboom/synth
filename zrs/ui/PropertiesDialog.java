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

public abstract class PropertiesDialog extends Dialog {
  
  protected Frame pa;

  PropertiesObserver observer;

  Button okayButton;
  Button cancelButton;
  Button applyButton;

  public PropertiesDialog (Frame f, String title, PropertiesObserver parent) {
    super (f, title, false);
    this.pa = f;
    observer = parent;
    Panel p = new Panel();
    p.setLayout (new FlowLayout (FlowLayout.RIGHT));
    p.add (okayButton = new Button ("Okay"));
    p.add (cancelButton = new Button ("Cancel"));
    p.add (applyButton = new Button ("Apply"));
    add ("South", p);
  }

  public void show() {
    pack();
    pack();
    pack();
    super.show();
  }

  public abstract void apply();

  public boolean handleEvent (Event event) {
    if (event.id == Event.WINDOW_DESTROY ||
        (event.id == Event.KEY_PRESS && event.key == 0x1b) ||
        event.id == Event.ACTION_EVENT && event.target == cancelButton)
    {
      observer.hideProperties();
    }
    else if (event.id == Event.ACTION_EVENT && event.target == okayButton) {
      try {
        apply(); 
        observer.hideProperties();
      }
      catch (Exception e) { new ExceptionDialog (pa, e).show(); }
    }
    else if (event.id == Event.ACTION_EVENT && event.target == applyButton) {
      try { apply(); }
      catch (Exception e) { new ExceptionDialog (pa, e).show(); }
    }
    else return super.handleEvent (event);
    return true;
  }

}
