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
import java.awt.image.*;
import java.io.*;
import java.net.*;
import java.util.*;
import zrs.ui.*;
import zrs.wave.*;
import zrs.misc.*;

public class SynthWindow extends Frame
                         implements MyFileDialogObserver, WindowRegistryAble {

  public static final String VERSION = "0.95";

  public static final String STARTMESSAGE =
    "zrs.synth version " + VERSION + "\n" + 
    "Copyright (C) 1996 Emanuel Borsboom <manny@zerius.victoria.bc.ca>\n" +
    "zrs.synth comes with ABSOLUTELY NO WARRANTY. This is free software,\n" +
    "and you are welcome to redistribute it under certain conditions.\n" +
    "See the About box for details.\n";

  static int untitledCounter = 0;

  MenuBar menuBar;
  SynthCanvas synthCanvas;
  StatusBar statusBar;
  MenuItem saveMenuItem;

  Button openButton;
  Button loadButton;
  Button saveButton;
  Button synthesizeButton;
  Button deleteButton;
  Button propertiesButton;
  Button quitButton;

  AboutDialog aboutDialog = null;

  String name;
  File file = null;
  URL url = null;

  public SynthWindow () {
    initGUI();
    file = null;
    untitledCounter++;
    name = "Untitled " + untitledCounter;
    setTitle();
    saveMenuItem.disable();
    pack();
  }

  public SynthWindow (InputStream is, String name) 
                     throws IOException, SynthIfException, FileFormatException
  {
    initGUI();
    synthCanvas.load (is);
    this.name = name;
    setTitle();
    saveMenuItem.disable();
    pack();
  }

  public SynthWindow (File file) 
                     throws IOException, SynthIfException, FileFormatException
  {
    initGUI();
    FileInputStream fin = null;
    try {
      fin = new FileInputStream (file);
      synthCanvas.load (fin);
    } finally {
      try {
        if (fin != null) fin.close();
      } catch (IOException e) {}
    }
    this.file = file;
    name = file.getName();
    setTitle();
    saveMenuItem.enable();
    pack();
  }

  public SynthWindow (URL url)
                   throws IOException, SynthIfException, FileFormatException {
    initGUI();
    InputStream i = null;
    try {
      i = url.openStream();
      synthCanvas.load (i);
    } finally {
      try {
        if (i != null) i.close();
      } catch (IOException e) {}
    }
    this.url = url;
    name = url.toString();
    setTitle();
    saveMenuItem.enable();
    pack();
  }

  private void initGUI () {
    menuBar = new MenuBar();
    Menu m = new Menu ("File");
    m.add ("New");
    m.add ("New Wave");
    m.add ("Open ...");
    m.add (saveMenuItem = new MenuItem("Save"));
    m.add ("Save As ...");
    m.addSeparator();
    m.add ("Synthesize");
    m.add ("Synthesizer Properties ...");
    m.addSeparator();
    m.add ("Close");
    menuBar.add (m);
    m = new Menu ("Edit");
    m.add ("Select all");
    m.add ("Unselect all");
    m.addSeparator();
    m.add ("Delete");
    m.addSeparator();
    m.add ("Properties ...");
    menuBar.add (m);
    m = new Menu ("Generators", true);
    m.add ("Constant");
    m.add ("Oscillator");
    m.add ("Envelope");
    m.add ("Adder");
    m.add ("Multiplier");
    m.add ("Divider");
    m.add ("Absolute Val.");
    m.add ("Distort");
    m.add ("Delay");
    m.add ("Filter");
    m.add ("Output");
    menuBar.add (m);

    m = new Menu ("Help");
    m.add ("About ...");
    menuBar.add (m);

    setMenuBar (menuBar);

    ButtonBar bar = new ButtonBar();
    bar.add (quitButton = new Button ("Close"));
    bar.addSeparator();
    bar.add (openButton = new Button ("Open"));
    bar.add (saveButton = new Button ("Save"));
    bar.addSeparator();
    bar.add (synthesizeButton = new Button ("Synthesize"));
    bar.addSeparator();
    bar.add (deleteButton = new Button ("Delete"));
    bar.add (propertiesButton = new Button ("Properties"));

    add ("North", bar);
    add ("South", statusBar = new StatusBar (this, "copyright (c) 1996 emanuel borsboom"));
    add ("Center", synthCanvas = new SynthCanvas(this));
  }

  void setTitle() {
    setTitle ("Synthesizer - " + name);
  }

  public String getWindowIdentifier() {
    return "Synthesizer - " + name;
  }

  public void show() {
    super.show();
    WindowRegistry.add (this);
  }

  void quit() {
    hide();
    dispose();
    WindowRegistry.remove (this);
  }

  public boolean handleEvent (Event event) {
    if (event.id == Event.WINDOW_DESTROY)
      quit();
    else return super.handleEvent (event);
    return true;
  }

  public void fileDialogAction (MyFileDialog f, int action) {
    if (action == MyFileDialog.OKAY) {
      if (f.getMode() == MyFileDialog.LOAD) {
        WindowRegistry.saveCursors();
	WindowRegistry.setAllCursors (WAIT_CURSOR);
        try {
          if (f.isURL())
	    new SynthWindow (f.getURL()).show();
          else 
            new SynthWindow (f.getF()).show();
        }
        catch (Exception e) { new ExceptionDialog (this, e).show(); }
	finally {
	  WindowRegistry.restoreCursors();
	}
      }
      else {
        OutputStream fout = null;
	WindowRegistry.saveCursors();
	WindowRegistry.setAllCursors (WAIT_CURSOR);
        try {
	  fout = f.getOutputStream();
          fout = new FileOutputStream (f.getF());
          synthCanvas.save (fout);
	  if (f.isURL()) {
	    url = f.getURL();
	    file = null;
	    name = url.toString();
	  } else {
	    file = f.getF();
	    url = null;
	    name = file.toString();
	  }
	  setTitle();
	  saveMenuItem.enable();
        }
        catch (Exception e) { new ExceptionDialog (this, e).show(); }
	finally {
	  try {
	    if (fout != null) fout.close();
	  } catch (IOException e) {}
	  WindowRegistry.restoreCursors();
	}
      }
    }
  }

  void save() {
    WindowRegistry.saveCursors();
    WindowRegistry.setAllCursors (WAIT_CURSOR);
    OutputStream out = null;
    try {
      if (url != null) {
        URLConnection c = url.openConnection();
        c.setDoOutput (true);
	out = c.getOutputStream();
      } else if (file != null) {
        file.canWrite();
        out = new FileOutputStream (file);
      }
      synthCanvas.save (out);
    }
    catch (Exception e) { new ExceptionDialog (this, e).show(); }
    finally {
      try {
        if (out != null) out.close();
      } catch (IOException e) {}
      WindowRegistry.restoreCursors();
    }
  }

  void saveAs() {
    MyFileDialog f = 
       new MyFileDialog (this, this, "Save As", MyFileDialog.SAVE);
    f.setAllowURL (true);
    if (url != null)
      f.setFile (url.toString());
    else if (file != null)
      f.setFile (file.getPath());
    f.show();
  }

  void open() {
    MyFileDialog dia = new MyFileDialog (this, this, "Open", MyFileDialog.LOAD);
    dia.setAllowURL (true);
    dia.show();
  }

  public boolean action (Event event, Object arg) {
    if (event.target instanceof MenuItem) {
      if (arg.equals ("New"))
        new SynthWindow ().show();
      else if (arg.equals ("New Wave")) 
        new WaveWindow().show();
      else if (arg.equals ("Open ..."))
        open();
      if (arg.equals ("Save"))
        save();
      else if (arg.equals ("Save As ..."))
        saveAs();
      else if (arg.equals ("About ...")) {
        if (aboutDialog == null)
          aboutDialog = new AboutDialog (this);
        aboutDialog.show();
      }
      else if (arg.equals ("Close"))
        quit();
      else return synthCanvas.action (event, arg)?true:super.action(event, arg);
    }
    else if (event.target == quitButton)
      quit();
    else if (event.target == openButton)
      open();
    else if (event.target == saveButton) {
      if (file != null)
        save();
      else
        saveAs();
    }
    else return synthCanvas.action (event, arg)?true:super.action(event, arg);
    return true;
  }

  public static void main (String[] args)
    throws IOException, FileFormatException, SynthIfException
  {
    WindowRegistry.setExitWhenFinished (true);
    System.out.print (STARTMESSAGE);
    if (args != null && args.length > 0) {
      for (int i = 0; i < args.length; i++) {
        new SynthWindow (new File (args[i])).show();
      }
    }
    else new SynthWindow().show();
  }
}
