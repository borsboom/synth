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

import zrs.synthgen.*;
import zrs.wave.*;
import zrs.ui.*;
import zrs.misc.*;

class SynthesizeThread extends Thread implements Cancelable, Timeable {

  SynthWindow pa;

  Synthesizer synth;
  int samplingRate;
  int sampleCount;
  int currentSample;

  StatusDialog statusDialog;
  TimerThread timerThread;

  SynthesizeThread (SynthWindow parent, Synthesizer synth,
                    int samplingRate, int sampleCount) 
  {
    this.pa = parent;
    this.synth = synth;
    this.samplingRate = samplingRate;
    this.sampleCount = sampleCount;
    setPriority (Thread.NORM_PRIORITY - 1);
    statusDialog = new StatusDialog (parent, "Synthesize...", false,
                                     "Generating Wave...", this);
  }

  public void cancel() {
    statusDialog.hide();
    statusDialog.dispose();
    timerThread.stop();
    stop();
  }

  public void tick (TimerThread t) {
    statusDialog.setPercent (currentSample * 100 / sampleCount);
  }

  public void run() {

    timerThread = new TimerThread (this, 250);
    statusDialog.show();

    double[] wave = new double [sampleCount];
    currentSample = 0;

    timerThread.start();

    try {
      for (; currentSample < sampleCount; currentSample++)
        wave[currentSample] = synth.nextValue();
      new WaveWindow (new Wave (wave, samplingRate, synth.maxValue>1.0?synth.maxValue:1.0), "Output from " + pa.name).show();
    }
    catch (Exception e) { new ExceptionDialog (pa, e).show(); }
    finally {
      statusDialog.hide();
      statusDialog.dispose();
      timerThread.stop();
    }

  }
}
