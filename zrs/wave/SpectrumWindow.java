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
import zrs.misc.*;

public class SpectrumWindow extends Frame implements WindowRegistryAble {

  public SpectrumWindow (Wave wave, String name) {
    setTitle ("Spectrum - " + name);
    setBackground (Color.black);
    SpectrumCanvas spectrumCanvas = new SpectrumCanvas (wave);
    setLayout (new BorderLayout());
    add ("Center", spectrumCanvas);
    pack();
    WindowRegistry.add (this);
  }

  public String getWindowIdentifier() {
    return getTitle();
  }

  public boolean handleEvent (Event event) {
    if (event.id == Event.WINDOW_DESTROY) {
      hide();
      dispose();
      WindowRegistry.remove (this);
    }
    else return super.handleEvent (event);
    return true;
  }

}
