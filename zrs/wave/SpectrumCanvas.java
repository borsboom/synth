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
import fft.*;

public class SpectrumCanvas extends Canvas {

  double[] data;
  double maxMag;
  static Dimension prefSize = new Dimension (256, 128);

  public SpectrumCanvas (Wave wave) {
    if (wave.length() < 4)
      data = null;
    else {
      double[] d = wave.getWave();
      data = new double [FFT.ipow (2, FFT.ilog2 (d.length))];
      for (int i = 0; i < data.length; i++)
        data[i] = d[i];
      FFT.realfftmag (data);
      maxMag = 0.0;
      for (int i = 1; i <= data.length/2; i++)
        if (data[i] > maxMag) maxMag = data[i];
    }
    setBackground (Color.black);
  }

  public Dimension minimumSize() { 
    return prefSize;
  }

  public Dimension preferredSize() {
    return prefSize;
  }

  public void paint (Graphics g) {

    if (data == null) return;

    int w = size().width;
    int h = size().height;

    double yScale = h / maxMag;
    double xInc = data.length / (double)(w * 2);
    double xPos = xInc + 1;

    int xp;
    int oxp = 1;

    g.setColor (Color.green);

    for (int x = 0; x < w; x++) {
      xp = (int) xPos;
      double m = 0;
      for (int i = oxp; i < xp; i++)
        if (data[i] > m) m = data[i];
      int y = (int)(m * yScale);
      g.drawLine (x, h - 1, x, h - y - 1);
      xPos += xInc;
      oxp = xp;
    }
  }

  public boolean handleEvent (Event event) {
    if (event.id == Event.MOUSE_DOWN)
      repaint();
    else return super.handleEvent (event);
    return true;
  }

}

