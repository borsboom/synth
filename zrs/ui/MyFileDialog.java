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
import java.net.*;

public class MyFileDialog extends Dialog {

  public final static int LOAD = 0;
  public final static int SAVE = 1;

  public final static int OKAY = 1;
  public final static int CANCEL = 0;

  Frame pa;

  MyFileDialogObserver observer;
  int mode;
  boolean allowURL = false;

  TextField filenameField;
  Button okayButton;
  Button cancelButton;

  File file = null;
  URL url = null;

  public MyFileDialog (MyFileDialogObserver observer, Frame parent,
                       String title, int mode)
  {
    super (parent, title, false);
    this.pa = parent;
    this.observer = observer;
    this.mode = mode;

    Panel p = new Panel();
    GridBagLayout gbl = new GridBagLayout();
    p.setLayout (gbl);
    GridBagConstraints c = new GridBagConstraints();

    Label l = new Label ("Filename");
    c.insets = new Insets (2, 2, 2, 2);
    gbl.setConstraints (l, c);
    p.add (l);
    if (file == null)
      filenameField = new TextField (24);
    else
      filenameField = new TextField (file.getPath(), 24);
    filenameField.selectAll();
    c.insets.left = 0;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.weightx = 1.0;
    gbl.setConstraints (filenameField, c);
    p.add (filenameField);
    add ("Center", p);

    p = new Panel();
    p.setLayout (new FlowLayout (FlowLayout.RIGHT, 2, 2));
    p.add (okayButton = new Button ("Okay"));
    p.add (cancelButton = new Button ("Cancel"));
    add ("South", p);

    pack();
    pack();
    pack();
  }

  public void setAllowURL (boolean allowURL) {
    this.allowURL = allowURL;
  }

  public void setDirectory (String dir) {
    file = new File (dir, (file==null)?null:file.getName());
    filenameField.setText (file.getPath());
  }

  public void setFile (String filename) {
    file = new File ((file==null)?null:file.getParent(), filename);
    filenameField.setText (file.getPath());
    filenameField.selectAll();
  }

  public boolean isURL() {
    return url != null;
  }

  public URL getURL() {
    return url;
  }

  public File getF() {
    return file;
  }

  public InputStream getInputStream() throws IOException {
    if (url != null) 
      return url.openStream();
    else if (file != null)
      return new FileInputStream (file);
    else
      return null;
  }

  public OutputStream getOutputStream() throws IOException {
    if (url != null) {
      URLConnection c = url.openConnection();
      c.setDoOutput (true);
      return c.getOutputStream();
    }
    else if (file != null)
      return new FileOutputStream (file);
    else 
      return null;
  }

  public String getDirectory() {
    if (file == null) return null;
    else return file.getParent();
  }

  public String getFile() {
    if (file == null) return null;
    else return file.getName();
  }

  public int getMode() {
    return mode;
  }

  public boolean handleEvent (Event event) {
    if (event.id == Event.WINDOW_DESTROY ||
        (event.id == Event.KEY_PRESS && event.key == 0x1b)) 
    {
      file = null;
      url = null;
      hide();
      dispose();
      observer.fileDialogAction (this, CANCEL);
    }
    else return super.handleEvent (event);
    return true;
  }

  public void confirmOverwrite (File file) {
    this.file = file;
    hide();
    dispose();
    observer.fileDialogAction (this, OKAY);
  }

  public boolean action (Event e, Object arg) {
    if (e.target == okayButton) {
      boolean isURL = false;
      boolean okay = true;

      if (filenameField.getText() == "")
        okay = false;
      else if (allowURL) {
	isURL = true;
        try { url = new URL (filenameField.getText()); }
	catch (MalformedURLException ex) { isURL = false; }
      }
      if (!isURL) {
        file = new File (filenameField.getText());
        try {
          if (mode == LOAD) {
            if (!file.exists()) {
              new ErrorDialog (pa, "File does not exist").show();
              okay = false;
	    }
	    else {
	      if (!file.canRead()) {
	        new ErrorDialog (pa, "Cannot read from file").show();
	        okay = false;
	      }
	    } 
          }
          else if (mode == SAVE) {
	    file.canWrite();
	    if (file.exists()) {
	      new ConfirmOverwriteDialog (pa, this, file).show();
	      okay = false;
	    }
	  }
        }
        catch (SecurityException ex) {
          new ErrorDialog (pa, "Security Exception: " + ex.toString()).show();
          okay = false;
        }
      }
      if (okay) {
        hide();
        dispose();
        observer.fileDialogAction (this, OKAY);
      }
    } else if (e.target == cancelButton) {
      file = null;
      url = null;
      hide();
      dispose();
      observer.fileDialogAction (this, CANCEL);
    }
    else return super.action (e, arg);
    return true;
  }
}

class ConfirmOverwriteDialog extends Dialog {
  MyFileDialog pa;
  File file;

  Button yesButton;
  Button noButton;

  ConfirmOverwriteDialog (Frame parent, MyFileDialog pa, File file) {
    super (parent, "Confirm Overwrite", true);
    this.pa = pa;
    this.file = file;

    GridBagLayout gbl = new GridBagLayout();
    setLayout (gbl);
    GridBagConstraints c = new GridBagConstraints();
    
    c.insets = new Insets (6, 6, 6, 6);
    c.gridx = c.gridy = 0;
    c.gridwidth = 2;
    c.weighty = 1.0;
    Label l = new Label ("Overwrite " + file + "?", Label.CENTER);
    gbl.setConstraints (l, c);
    add (l);

    c.insets = new Insets (0, 2, 2, 2);
    c.gridy++;
    c.gridwidth = 1;
    c.weightx = 1.0;
    c.weighty = 0.0;
    c.anchor = GridBagConstraints.EAST;
    yesButton = new Button ("Yes");
    gbl.setConstraints (yesButton, c);
    add (yesButton);
    c.gridx++;
    c.anchor = GridBagConstraints.WEST;
    noButton = new Button ("No");
    gbl.setConstraints (noButton, c);
    add (noButton);

    pack();
    pack();
    pack();
  }

  public boolean handleEvent (Event event) {
    if (event.id == Event.WINDOW_DESTROY ||
        (event.id == Event.ACTION_EVENT && event.target == noButton) ||
	(event.id == Event.KEY_PRESS && event.key == 0x1b)) 
    {
      hide();
      dispose();
      return true;
    }
    else if (event.id == Event.ACTION_EVENT && event.target == yesButton) {
      pa.confirmOverwrite (file);
      hide();
      dispose();
      return true;
    }
    else return super.handleEvent (event);
  }

}
