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
import java.io.*;
import java.util.*;
import zrs.ui.*;
import zrs.misc.*;

public class WaveWindow extends Frame 
       implements MyFileDialogObserver, ClipboardObserver, WaveCanvasObserver,
                  PropertiesObserver, WindowRegistryAble {

  public static int CLOSE = 1;

  static int untitledCounter = 0;

  static SingleAudioPlayer singleAudioPlayer = new SingleAudioPlayer();

  WaveWindowObserver observer = null;

  WaveCanvas waveCanvas;
  String name;
  File file = null;

  MenuItem saveMenuItem;
  MenuItem saveAsMenuItem;
  MenuItem propertiesMenuItem;
  MenuItem cutMenuItem;
  MenuItem copyMenuItem;
  MenuItem pasteMenuItem;
  MenuItem clearMenuItem;
  MenuItem viewSelectionMenuItem;
  MenuItem amplifyMenuItem;
  MenuItem reverseMenuItem;
  MenuItem spectrumMenuItem;

  Button closeButton;
  Button cutButton;
  Button copyButton;
  Button pasteButton;
  Button clearButton;
  Button viewAllButton;
  Button viewSelectionButton;
  Button viewOneToOneButton;
  Button zoomInButton;
  Button zoomOutButton;
  Button playButton;
  Button loopButton;
  Button stopButton;

  Scrollbar scrollbar;

  WavePropertiesDialog propertiesDialog;

  boolean opening;
  
  public WaveWindow () {
    initGUI();
    saveMenuItem.disable();
    saveAsMenuItem.disable();
    untitledCounter++;
    name = "Untitled " + untitledCounter;
    setTitle();
    scrollbar.disable();
    playButton.disable();
    loopButton.disable();
    amplifyMenuItem.disable();
    reverseMenuItem.disable();
    spectrumMenuItem.disable();
    propertiesMenuItem.disable();
    if (!WaveCanvas.clipboard.isEmpty()) {
      pasteButton.enable();
      pasteMenuItem.enable();
    }
    pack();
  }

  public void setObserver (WaveWindowObserver observer) {
    this.observer = observer;
  }

  public WaveWindow (Wave wave, String name) {
    initGUI();
    saveMenuItem.disable();
    saveAsMenuItem.enable();
    untitledCounter++;
    if (name == null)
      this.name = "Untitled " + untitledCounter;
    else
      this.name = name;
    setTitle();
    waveCanvas.setWave (wave);
    pack();
    updateScrollbar();
  }

  public WaveWindow (Wave wave) {
    this (wave, null);
  }

  public WaveWindow (File file) throws IOException, WaveFormatException {
    initGUI();
    saveMenuItem.enable();
    saveAsMenuItem.enable();
    FileInputStream fis = null;
    try {
      fis = new FileInputStream (file);
      load (fis, file, file.getName());
    }
    finally {
      if (fis != null) fis.close();
    }
    this.file = file;
    pack();
    updateScrollbar();
  }

  void initGUI() {
    MenuBar menuBar = new MenuBar();
    Menu m = new Menu("File");
    m.add ("New");
    m.add ("Open ...");
    m.add ("Load ...");
    m.add (saveMenuItem = new MenuItem ("Save"));
    m.add (saveAsMenuItem = new MenuItem ("Save As ..."));
    m.addSeparator();
    m.add ("Close");
    menuBar.add (m);

    m = new Menu ("Edit");
    m.add ("Select All");
    m.add ("Select View");
    m.add ("Select None");
    m.addSeparator();
    m.add (cutMenuItem = new MenuItem ("Cut")).disable();
    m.add (copyMenuItem = new MenuItem ("Copy")).disable();
    m.add (pasteMenuItem = new MenuItem ("Paste")).disable();
    m.add (clearMenuItem = new MenuItem ("Clear")).disable();
    m.addSeparator();
    m.add (amplifyMenuItem = new MenuItem ("Amplify"));
    m.add (reverseMenuItem = new MenuItem ("Reverse"));
    menuBar.add (m);

    m = new Menu ("View");
    m.add ("Zoom In");
    m.add ("Zoom Out");
    m.addSeparator();
    m.add ("View All");
    m.add (viewSelectionMenuItem = new MenuItem ("View Selection")).disable();
    m.add ("1-1");
    m.addSeparator();
    m.add (spectrumMenuItem = new MenuItem ("Spectrum"));
    m.addSeparator();
    m.add (propertiesMenuItem = new MenuItem ("Properties ..."));
    menuBar.add (m);

    setMenuBar (menuBar);

    ButtonBar bar = new ButtonBar();
    bar.add (closeButton = new Button ("Close"));
    bar.addSeparator();
    bar.add (playButton = new Button ("Play"));
    bar.add (loopButton = new Button ("Loop"));
    bar.add (stopButton = new Button ("Stop"));
    bar.addSeparator();
    bar.add (cutButton = new Button ("Cut")).disable();
    bar.add (copyButton = new Button ("Copy")).disable();
    bar.add (pasteButton = new Button ("Paste")).disable();
    bar.add (clearButton = new Button ("Clear")).disable();
    bar.addSeparator();
    bar.add (zoomInButton = new Button ("Z.In"));
    bar.add (zoomOutButton = new Button ("Z.Out"));
    bar.add (viewAllButton = new Button ("All"));
    bar.add (viewSelectionButton = new Button ("Sel")).disable();
    bar.add (viewOneToOneButton = new Button ("1-1"));

    waveCanvas = new WaveCanvas();
    waveCanvas.allowSelection (true);
    waveCanvas.setObserver (this);

    scrollbar = new Scrollbar (Scrollbar.HORIZONTAL);

    add ("North", bar);
    add ("Center", waveCanvas);
    add ("South", scrollbar);

    WaveCanvas.clipboard.addObserver (this);
  }

  public void show() {
    super.show();
    WindowRegistry.add (this);
  }

  void setTitle() {
    setTitle ("Wave - " + name);
  }

  public String getWindowIdentifier() {
    return "Wave - " + name;
  }

  public void load (InputStream is, File file, String name)
                                   throws IOException, WaveFormatException 
  {
    WindowRegistry.saveCursors();
    WindowRegistry.setAllCursors (Frame.WAIT_CURSOR);
    try { waveCanvas.load (is); }
    finally { WindowRegistry.restoreCursors(); }
    this.name = name;
    this.file = file;
    setTitle();
    hideProperties();
    if (file == null)
      saveMenuItem.disable();
    else
      saveMenuItem.enable();
    saveAsMenuItem.enable();
    playButton.enable();
    loopButton.enable();
    amplifyMenuItem.enable();
    reverseMenuItem.enable();
    spectrumMenuItem.enable();
    propertiesMenuItem.enable();
  }

  public String getName() {
    return name;
  }

  public File getFile() {
    return file;
  }

  public Wave getWave() {
    return waveCanvas.getWave();
  }

  void quit() {
    hide();
    dispose();
    if (observer != null)
      observer.waveWindowEvent (this, CLOSE);
    WindowRegistry.remove (this);
  }

  public void clipboardEvent (Clipboard clipboard, int action) {
    if (action == Clipboard.PUT) {
      if (waveCanvas.getWave() == null || waveCanvas.getSelection() != null) {
        pasteButton.enable();
	pasteMenuItem.enable();
      }
    }
  }

  public void waveCanvasEvent (WaveCanvas wc, int action) {
    if (action == WaveCanvas.SELECT) {
      cutButton.enable();
      cutMenuItem.enable();
      copyButton.enable();
      copyMenuItem.enable();
      if (!WaveCanvas.clipboard.isEmpty()) {
        pasteButton.enable();
        pasteMenuItem.enable();
      }
      clearButton.enable();
      clearMenuItem.enable();
      viewSelectionButton.enable();
      viewSelectionMenuItem.enable();
    }
    else if (action == WaveCanvas.UNSELECT) {
      cutButton.disable();
      cutMenuItem.disable();
      copyButton.disable();
      copyMenuItem.disable();
      pasteButton.disable();
      pasteMenuItem.disable();
      clearButton.disable();
      clearMenuItem.disable();
      viewSelectionButton.disable();
      viewSelectionMenuItem.disable();
    }
    else if (action == WaveCanvas.NULL) {
      pasteButton.enable();
      pasteMenuItem.enable();
      playButton.disable();
      loopButton.disable();
      amplifyMenuItem.disable();
      reverseMenuItem.disable();
      spectrumMenuItem.disable();
      propertiesMenuItem.disable();
    }
    else if (action == WaveCanvas.UNNULL) {
      playButton.enable();
      loopButton.enable();
      amplifyMenuItem.enable();
      reverseMenuItem.enable();
      spectrumMenuItem.enable();
      propertiesMenuItem.enable();
    }
  }

  public void hideProperties() {
    if (propertiesDialog != null) {
      propertiesDialog.hide();
      propertiesDialog.dispose();
      propertiesDialog = null;
    }
  }

  void updateScrollbar() {
    if (waveCanvas.getWave() == null)
      scrollbar.disable();
    else {
      int[] view = waveCanvas.getView();
      int visible = view[1] - view[0] + 1;
      int minimum = 0;
      int maximum = waveCanvas.getWave().length() - (view[1] - view[0] + 1);
      int pageIncrement = (view[1] - view[0] + 1) * 3 / 4;
      int lineIncrement = (view[1] - view[0] + 1) / 16;
      scrollbar.setValues (view[0], visible, minimum, maximum);
      scrollbar.setPageIncrement (pageIncrement<1?1:pageIncrement);
      scrollbar.setLineIncrement (lineIncrement<1?1:lineIncrement);
      scrollbar.enable();
    }
  }

  public boolean handleEvent (Event event) {
    if (event.id == Event.WINDOW_DESTROY)
      quit();
    else if (event.target == scrollbar) {
      int[] view = waveCanvas.getView();
      int l = view[1] - view[0];
      boolean flag = true;

      // Bloody Win32 AWT.  Hurry up with that rewrite, Sun!
      // This also doesn't work with Netscape under Win32... neither does
      if (event.id == Event.SCROLL_LINE_DOWN) view[0] += l / 16;
      else if (event.id == Event.SCROLL_LINE_UP) view[0] -= l / 16;
      else if (event.id == Event.SCROLL_PAGE_DOWN) view[0] += l * 3 / 4;
      else if (event.id == Event.SCROLL_PAGE_UP) view[0] -= l * 3 / 4;
      else if (event.id == Event.SCROLL_ABSOLUTE) 
        view[0] = ((Integer)event.arg).intValue();
      else flag = false;
      
      if (flag) {
        view[1] = view[0] + l;
        waveCanvas.setView (view[0], view[1]);
        updateScrollbar();
      }
    }
    else return super.handleEvent (event);
    return true;
  }

  public void fileDialogAction (MyFileDialog dia, int action) {
    if (action == MyFileDialog.OKAY) {
      if (dia.getMode() == MyFileDialog.LOAD) {
        setCursor (WAIT_CURSOR);
	try {
          if (opening) {
	    if (dia.isURL()) {
	      Wave w = new Wave (dia.getURL().openStream());
	      new WaveWindow (w, dia.getURL().toString()).show();
	    }
	    else
	      new WaveWindow (dia.getF()).show();
	  } else {
	    hideProperties();
	    InputStream is = null;
	    try {
	      is = dia.getInputStream();
	      load (is, dia.isURL()?null:dia.getF(),
	            dia.isURL()?dia.getURL().toString():dia.getF().getName());
	    } finally {
	      if (is != null) is.close();
	    }
	    updateScrollbar();
	  }
	}
	catch (Exception e) { new ExceptionDialog (this, e).show(); }
        setCursor (DEFAULT_CURSOR);
      }
      else {
        WindowRegistry.saveCursors();
	WindowRegistry.setAllCursors (WAIT_CURSOR);
        FileOutputStream fos = null;
        try {
          fos = new FileOutputStream (dia.getF());
          waveCanvas.getWave().write (fos);
	  file = dia.getF();
	  name = file.getName();
	  setTitle();
	  saveMenuItem.enable();
	}
	catch (Exception e) { new ExceptionDialog (this, e).show(); }
	finally {
	  try {
	    if (fos != null) fos.close();
	  } catch (IOException e) {}
	}
	WindowRegistry.restoreCursors();
      }
    }
  }

  public boolean action (Event event, Object arg) {
    if (event.target == closeButton || arg.equals ("Close"))
      quit();
    else if (arg.equals ("New"))
      new WaveWindow().show();
    else if (arg.equals ("Open ...")) {
      MyFileDialog f = new MyFileDialog (this, this, "Open Wave", MyFileDialog.LOAD);
      f.setAllowURL (true);
      f.show();
      opening = true;
    }
    else if (arg.equals ("Load ...")) {
      MyFileDialog f = new MyFileDialog (this, this, "Load Wave", MyFileDialog.LOAD);
      f.setAllowURL (true);
      f.show();
      opening = false;
    } 
    else if (arg.equals ("Save")) {
      WindowRegistry.saveCursors();
      WindowRegistry.setAllCursors (WAIT_CURSOR);
      FileOutputStream fos = null;
      try {
        file.canWrite();
        fos = new FileOutputStream (file);
        waveCanvas.getWave().write (fos);
      }
      catch (Exception e) { new ExceptionDialog (this, e).show(); }
      finally {
	try {
          if (fos != null) fos.close();
	} catch (IOException e) {}
      }
      WindowRegistry.restoreCursors();
    }
    else if (arg.equals ("Save As ...")) {
      MyFileDialog dia = 
                new MyFileDialog (this, this, "Save Wave", MyFileDialog.SAVE);
      if (file != null)
        dia.setFile (file.getPath());
      dia.show();
    } 
    else if (event.target == playButton)
      singleAudioPlayer.play (waveCanvas.getWave());
    else if (event.target == loopButton)
      singleAudioPlayer.loop (waveCanvas.getWave());
    else if (event.target == stopButton)
      singleAudioPlayer.stop();
    else if (arg.equals ("Select All"))
      waveCanvas.selectAll();
    else if (arg.equals ("Select None"))
      waveCanvas.selectNone();
    else if (arg.equals ("Select View"))
      waveCanvas.selectView();
    else if (event.target == zoomInButton || arg.equals ("Z.In")) {
      waveCanvas.zoomIn();
      updateScrollbar();
    }
    else if (event.target == zoomOutButton || arg.equals ("Z.Out")) {
      waveCanvas.zoomOut();
      updateScrollbar();
    }
    else if (event.target == viewAllButton || arg.equals ("All")) {
      waveCanvas.viewAll();
      updateScrollbar();
    }
    else if (event.target == viewSelectionButton || arg.equals ("Sel")) {
      waveCanvas.viewSelection();
      updateScrollbar();
    }
    else if (event.target == viewOneToOneButton || arg.equals ("1-1")) {
      waveCanvas.viewOneToOne();
      updateScrollbar();
    }
    else if (arg.equals ("Properties ...")) {
      if (waveCanvas.wave != null) {
        if (propertiesDialog == null)
          propertiesDialog = new WavePropertiesDialog (this);
        propertiesDialog.show();
      }
    }
    else if (event.target == clearButton || arg.equals ("Clear"))
      waveCanvas.clearSelection();
    else if (event.target == cutButton || arg.equals ("Cut")) {
      waveCanvas.cutSelection();
      updateScrollbar();
    }
    else if (event.target == copyButton || arg.equals ("Copy"))
      waveCanvas.copySelection();
    else if (event.target == pasteButton || arg.equals ("Paste")) {
      waveCanvas.pasteBeforeSelection();
      updateScrollbar();
    }
    else if (arg.equals ("Amplify"))
      new AmplifyDialog (this).show();
    else if (arg.equals ("Reverse"))
      waveCanvas.reverse();
    else if (arg.equals ("Spectrum")) {
      new SpectrumWindow (waveCanvas.getSelectedWave(), name).show();
    }
    else 
      return super.action (event, arg);
    return true;
  }

  public static void main (String[] args) throws IOException, WaveFormatException {
    WindowRegistry.setExitWhenFinished (true);
    if (args != null && args.length > 0) {
      for (int i = 0; i < args.length; i++)
        new WaveWindow (new File (args[i])).show();
    }
    else
      new WaveWindow().show();
  }

}
