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

public class ErrorDialog extends Dialog {

  String details;

  Button okayButton;
  Button detailsButton;

  ErrorDetailsDialog detailsDialog = null;

  public ErrorDialog (Frame parent, String errorMsg, String title) 
  {
    super (parent, title, true);
    this.details = details;

    GridBagLayout gbl = new GridBagLayout();
    setLayout (gbl);
    GridBagConstraints c = new GridBagConstraints();

    c.gridx = c.gridy = 0;
    c.weightx = c.weighty = 1.0;
    c.insets = new Insets (6, 6, 6, 6);
    c.gridwidth = 2;
    Label l = new Label (errorMsg, Label.CENTER);
    gbl.setConstraints (l, c);
    add (l);

    c.gridy++;
    c.gridwidth = 1;
    c.insets = new Insets (0, 2, 2, 2);
    c.weighty = 0.0;
    c.anchor = GridBagConstraints.EAST;
    detailsButton = new Button ("Details");
    gbl.setConstraints (detailsButton, c);
    detailsButton.hide();
    add (detailsButton);
    c.weightx = 0;
    c.gridx++;
    okayButton = new Button ("Acknowledge");
    gbl.setConstraints (okayButton, c);
    add (okayButton);

    pack();
    pack();
    pack();
  }

  public ErrorDialog (Frame parent, String errorMsg) {
    this (parent, errorMsg, "Error");
  }

  public void setDetails (String details) {
    this.details = details;
    detailsButton.show();
  }

  public boolean handleEvent (Event event) {
    if (event.id == Event.WINDOW_DESTROY ||
        (event.id == Event.KEY_PRESS && event.key == 0x1b) ||
	(event.id == Event.ACTION_EVENT && event.target == okayButton))
    {
      if (detailsDialog != null) {
        detailsDialog.hide();
	detailsDialog.dispose();
      }
      hide();
      dispose();
      return true;
    }
    else if (event.id == Event.ACTION_EVENT && event.target == detailsButton) {
      if (detailsDialog == null)
        detailsDialog = new ErrorDetailsDialog ((Frame) getParent(), details);
      detailsDialog.show();
      return true;
    }
    else return super.handleEvent (event);
  }

}

class ErrorDetailsDialog extends Dialog {
  
  Button okayButton;

  ErrorDetailsDialog (Frame parent, String details) {
    super (parent, "Error Details", true);

    setLayout (new BorderLayout());

    TextArea t = new TextArea (details, 16, 64);
    t.setFont (new Font ("Courier", Font.PLAIN, 12));
    t.setEditable (false);
    add ("Center", t);

    Panel p = new Panel();
    p.setLayout (new FlowLayout (FlowLayout.RIGHT, 2, 2));
    p.add (okayButton = new Button ("Okay"));

    add ("South", p);
    pack();
    pack();
    pack();
  }

  public boolean handleEvent (Event event) {
    if (event.id == Event.WINDOW_DESTROY ||
        (event.id == Event.ACTION_EVENT && event.target == okayButton) ||
	(event.id == Event.KEY_PRESS && event.key == 0x1b))
    {
      hide();
      return true;
    }
    else return super.handleEvent (event);
  }

}
