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
import zrs.ui.BoxStretchRubberBand;
import zrs.misc.Clipboard;
import sun.audio.*;

public class WaveCanvas extends Canvas {

  static final int SELECT = 1;
  static final int UNSELECT = 2;
  static final int NULL = 3;
  static final int UNNULL = 4;

  static final Color bgColor = Color.black;
  static final Color waveColor = Color.green;
  static final Color axisColor = new Color (0, 128, 0);
  static final Color selectedBgColor = new Color (0, 0, 128);
  static final int dragThreshhold = 1;
  static final int wholeHeight = 16;

  static Clipboard clipboard = new Clipboard();

  boolean dragged;
  Point dragStart;

  boolean showWhole;

  Wave wave = null;
  int viewStart = 0;
  int viewEnd = -1;
  boolean selected = false;
  boolean selectionAllowed = false;
  int selectStart;
  int selectEnd;
  boolean selectingFlag = false;
  BoxStretchRubberBand rubberBand;

  Dimension minSize = new Dimension (512, 256);

  WaveCanvasObserver observer = null;

  public WaveCanvas() {
    setBackground (bgColor);
  }

  public WaveCanvas (Dimension size) {
    this();
    minSize = size;
  }

  public WaveCanvas (Wave wave) {
    this();
    this.wave = wave;
    if (wave != null)
      viewEnd = wave.length();
    else
      viewEnd = -1;
  }

  public WaveCanvas (Wave wave, Dimension size) {
    this (wave);
    minSize = size;
  }

  public void setObserver (WaveCanvasObserver observer) {
    this.observer = observer;
  }

  public Dimension minimumSize() {
    return minSize;
  }

  public Dimension preferredSize() {
    return minimumSize();
  }

  public void allowSelection (boolean selectionAllowed) {
    this.selectionAllowed = selectionAllowed;
  }

  public int[] getSelection() {
    if (selected) {
      int[] r = { selectStart, selectEnd };
      return r;
    }
    else
      return null;
  }

  public void select (int start, int end) {
    if (!selected || start != selectStart || end != selectEnd) {
      if (selectEnd < selectStart) {
        selectNone();
      } else {
        if (selectStart >= wave.length()) selectStart = wave.length() - 1;
        if (selectEnd >= wave.length()) selectEnd = wave.length() - 1;
        if (selectStart < 0) selectStart = 0;
        if (selectEnd < 0) selectEnd = 0;
        selectStart = start;
        selectEnd = end;
        selected = true;
        if (observer != null) observer.waveCanvasEvent (this, SELECT);
      }
    }
  }

  public void selectNone() {
    selected = false;
    if (observer != null) observer.waveCanvasEvent (this, UNSELECT);
    repaint();
  }

  public void selectAll() {
    select (0, wave.length() - 1);
    repaint();
  }

  public void selectView() {
    select (viewStart, viewEnd);
    repaint();
  }

  public int[] getView () {
    int[] r = { viewStart, viewEnd };
    return r;
  }

  public void setView (int start, int end) {
    if (wave != null) {
      if ((viewStart = start) < 0) viewStart = 0;
      if (viewStart >= wave.length()) viewStart = wave.length() - 1;
      if ((viewEnd = end) >= wave.length()) viewEnd = wave.length() - 1;
      if (viewEnd < viewStart) viewEnd = viewStart;
    }
    repaint();
  }

  public void zoomIn() {
    int l = viewEnd - viewStart + 1;
    setView (viewStart + l/4, viewEnd - l/4);
  }

  public void zoomOut() {
    int l = viewEnd - viewStart + 1;
    setView (viewStart - l/2, viewEnd + l/2);
  }

  public void viewAll() {
    setView (0, wave.length() - 1);
  }

  public void viewSelection() {
    if (selected)
      setView (selectStart, selectEnd);
  }

  public void viewOneToOne() {
    int start = viewStart;
    int end = viewStart + size().width - 1;
    if (wave != null && end >= wave.length()) {
      end = wave.length();
      start = viewEnd - (size().width - 1);
      if (start < 0) start = 0;
    }
    setView (start, end);
  }

  public void clearSelection() {
    if (selected) {
      double[] w = wave.getWave();
      for (int i = selectStart; i <= selectEnd; i++)
        w[i] = 0;
      wave.setWave (w);
      repaint();
    }
  }

  public void reverse() {
    int i, e;
    if (selected) {
      i = selectStart;
      e = selectEnd;
    } else {
      i = 0;
      e = wave.length() - 1;
    }
    double[] w = wave.getWave();
    double t;
    for (int j = e; i < j; i++, j--) {
      t = w[i];
      w[i] = w[j];
      w[j] = t;
    }
    wave.setWave (w);
    repaint();
  }

  public Wave getSelectedWave() {
    if (selected)
      return wave.getSection (selectStart, selectEnd);
    else 
      return wave;
  }

  public void amplify (double factor) {
    int i, e;
    if (selected) {
      i = selectStart;
      e = selectEnd;
    } else {
      i = 0;
      e = wave.length() - 1;
    }
    double[] w = wave.getWave();
    for (; i <= e; i++)
      w[i] *= factor;
    wave.setWave (w);
    repaint();
  }

  public void copySelection() {
    if (selected) {
      double[] s = wave.getWave();
      double[] d = new double [selectEnd - selectStart + 1];
      int j = 0;
      for (int i = selectStart; i <= selectEnd; i++, j++)
        d[j] = s[i];
      clipboard.putObject (new Wave (d, wave.getSamplingRate(), wave.getMaxAmplitude()));
    }
  }

  public void cutSelection() {
    if (selected) {
      copySelection();
      if (wave.length() - (selectEnd - selectStart + 1) <= 0) {
        wave = null;
	selectNone();
	observer.waveCanvasEvent (this, NULL);
      } else {
        double[] s = wave.getWave();
        double[] d = new double [wave.length() - (selectEnd - selectStart + 1)];
        int i, j;
        for (i = 0; i < selectStart; i++)
          d[i] = s[i];
        for (j = selectEnd + 1; j < s.length; j++, i++)
          d[i] = s[j];
        wave.setWave (d);

        int start, end;
        if (selectStart >= wave.length()) start = wave.length() - 1;
        else start = selectStart;
        select (start, start);
        i = viewEnd - viewStart + 1;
        if (viewStart >= wave.length())
          viewStart = wave.length() - 1;
        viewEnd = viewStart + i;
        if (viewEnd >= wave.length())
          viewEnd = wave.length() - 1;
        repaint();
      }
    }
  }

  public void pasteBeforeSelection() {
    if (clipboard.isEmpty())
      return;

    if (wave == null) {
      wave = (Wave) ((Wave) clipboard.getObject()).clone();
      viewStart = 0;
      viewEnd = wave.length() - 1;
      observer.waveCanvasEvent (this, UNNULL);
      selectNone();
      repaint();
    }
    else if (selected) {
      double[] s = wave.getWave();
      double[] c = ((Wave) clipboard.getObject()).getWave();
      double[] d = new double [wave.length() + c.length];
      int start, end;
      int i, j, k;
      for (i = 0; i < selectStart; i++)
        d[i] = s[i];
      start = i;
      for (j = 0; j < c.length; i++, j++)
        d[i] = c[j];
      end = i-1;
      for (j = start; j < s.length; i++, j++)
        d[i] = s[j];
      wave.setWave (d);
      select (start, end);
      repaint();
    }
  }

  public Wave getWave() {
    return wave;
  }

  public void setWave (Wave wave) {
    this.wave = wave;
    viewStart = 0;
    if (wave == null)
      viewEnd = -1;
    else
      viewEnd = wave.length() - 1;
    selectNone();
    repaint();
  }

  public void load (InputStream is) throws IOException, WaveFormatException {
    wave = new Wave (is);
    viewAll();
    selectNone();
    observer.waveCanvasEvent (this, UNNULL);
    repaint();
  }

  static int coordToSampleNo (int x, int w, int l, int vs, int ve) {
    int s = x * (ve - vs + 1) / w + vs;
    if (s < 0) s = 0;
    else if (s >= l) s = l - 1;
    return s;
  }

  int coordToSampleNo (int x) {
    return coordToSampleNo (x, size().width, wave.length(), viewStart, viewEnd);
  }

  static int sampleNoToCoord (int sampleNo, int w, int l, int vs, int ve) {
    return (sampleNo - vs) * w / (ve - vs + 1);
  }

  int sampleNoToCoord (int sampleNo) {
    return sampleNoToCoord (sampleNo, size().width, wave.length(),
                                      viewStart, viewEnd);
  }

  public boolean mouseDown (Event event, int x, int y) {
    dragStart = new Point (x, y);
    dragged = false;

    if (selectionAllowed && wave != null) {
      if ((event.modifiers & Event.META_MASK) != 0 ||
          (event.modifiers & Event.ALT_MASK) != 0 ||
	  (event.modifiers & Event.CTRL_MASK) != 0)
      {
	selectNone();
	repaint();
      }
      else if (event.clickCount == 2) {
	select (viewStart, viewEnd);
        repaint();
      }
      else if (event.clickCount == 3) {
	selectAll();
        repaint();
      }
      else {
        rubberBand = new BoxStretchRubberBand (x, 1, size().height-2);
        selectingFlag = true;
      }
    }
    else return super.mouseDown (event, x, y);
    return true;
  }

  public boolean mouseDrag (Event event, int x, int y) {

    boolean firstDrag = false;

    if (!dragged) {
      if (x > dragStart.x+dragThreshhold || x < dragStart.x-dragThreshhold ||
          y > dragStart.y+dragThreshhold || y < dragStart.y-dragThreshhold)
      {
        dragged = true;
	firstDrag = true;
      }
    }
    if (!dragged)
      return true;

    if (selectingFlag) {
      rubberBand.move (getGraphics(), x, y);
    }
    else return super.mouseDrag (event, x, y);
    return true;
  }

  public boolean mouseUp (Event event, int x, int y) {

    boolean dragged = this.dragged;
    this.dragged = false;

    if (selectingFlag) {
      rubberBand.erase (getGraphics());
      int a = coordToSampleNo (rubberBand.getStart().x);
      int b = coordToSampleNo (x);
      if (dragged) {
        if (rubberBand.getStart().x < x) select (a, b);
        else select (b, a);
      }
      else
        select (a, a);
      repaint();
      selectingFlag = false;
      rubberBand = null;
    }

    else return super.mouseUp (event, x, y);
    return true;
  }

  private static final int[] keyFrequencies = {
    0, 0, 8476, 9514, 0, 11314, 12699, 14254, 0, 0,	/* 0-9 */
    0, 0, 0, 0, 0, 0, 0,				/* :-@ */
    0, 5993, 5040, 4757, 10079, 0,			/* A-F */
    5657, 6350, 16000, 7127, 0, 0,			/* G-L */
    7551, 6727, 0, 0, 8000, 10679,			/* M-R */
    4238, 11986, 15102, 5339, 8980, 4490,		/* S-X */
    13454, 4000						/* Y-Z */
  };

  public boolean keyDown (Event event, int key) {
    if (wave == null) return false;
    if (key >= 'a' && key <= 'z') key += 'A' - 'a';
    if (key < '0' || key > 'Z')
      return false;
    int f = keyFrequencies [key-'0'];
    if (f == 0) return false;
    byte[] s = wave.getUlaw();
    byte[] d = new byte [s.length * 8000 / f];
    double inc = f / 8000.0;
    double pos = 0;
    for (int i = 0; i < d.length; i++) {
      d[i] = s[(int)pos];
      pos += inc;
    }
    AudioPlayer.player.start (new AudioDataStream (new AudioData (d)));
    return true;
  }

  public static void drawWave (Graphics g, Wave wave,
                        int x, int y, int width, int height,
			int viewStart, int viewEnd,
			boolean selected,
                        int selectStart, int selectEnd)
  {
    double[] w;

    if (wave != null && (w = wave.getWave()) != null) {
      double samplePosition = viewStart;
      double sampleIncrement = (viewEnd - viewStart + 1) / (double) width;
      double amplitudeRatio = -(height/2-1) / wave.getMaxAmplitude();

      if (selected) {
        int x1 = sampleNoToCoord (selectStart, width,
	                          wave.length(), viewStart, viewEnd);
        if (x1 < width) {
          if (x1 < 0) x1 = 0;
	  int x2 = sampleNoToCoord (selectEnd, width, wave.length(), 
	                                       viewStart, viewEnd);
	  if (x2 >= width) x2 = width - 1;
          g.setColor (selectedBgColor);
          g.fillRect (x + x1, y, x2 - x1 + 1, height - 1);
        }
      }

      g.setColor (axisColor);
      g.drawLine (x, y+height/2, x+width-1, y+height/2);

      g.setColor (waveColor);

      double sum;

      int oldV = (int)(w[(int)samplePosition] * amplitudeRatio) +
                       height/2;
      int cx = x;
      for (int i = 1; i < width; i++, cx++) {
        samplePosition += sampleIncrement;
        int v = (int)(w[(int)samplePosition] * amplitudeRatio) +
	              height/2;
        g.drawLine (cx, oldV + y, cx+1, v + y);
	oldV = v;
      }
    }
    else {
      g.setColor (axisColor);
      g.drawLine (x, y+height/2, x+width-1, y+height/2);
    }


  }

  public void paint (Graphics g) {
    int w = size().width;
    int h = size().height;
    g.setColor (Color.darkGray);
    g.drawLine (0, 0, w-1, 0);
    g.drawLine (0, 0, 0, h-1);
    g.setColor (Color.white);
    g.drawLine (w-1, 0, w-1, h-1);
    g.drawLine (0, h-1, w-1, h-1);
    drawWave (g, wave, 1, 1, w-2, h-2,
              viewStart, viewEnd, selected, selectStart, selectEnd);
  }

}
