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

import java.io.*;
import sun.audio.*;

public class Wave implements Cloneable{

  private double[] wave = null;
  private double maxAmplitude = 1.0;
  private int samplingRate = 0;

  private byte[] ulawBuffer = null;

  public Wave() {
  }

  public Wave (double[] wave, int samplingRate, double maxAmplitude) {
    this.wave = wave;
    this.samplingRate = samplingRate;
    this.maxAmplitude = maxAmplitude;
  }

  public Wave (double[] wave, int samplingRate) {
    this (wave, samplingRate, 1.0);
  }

  public Wave (InputStream i) throws IOException, WaveFormatException {
    read (i);
  }

  public Wave (String filename) throws IOException, WaveFormatException  {
    FileInputStream i = new FileInputStream (filename);
    read (i);
    i.close();
  }

  public double[] getWave() {
    return wave;
  }

  public int getSamplingRate() {
    return samplingRate;
  }

  public double getMaxAmplitude() {
    return maxAmplitude;
  }

  public void setWave (double[] wave) {
    this.wave = wave;
    ulawBuffer = null;
  }

  public void setSamplingRate (int samplingRate) {
    this.samplingRate = samplingRate;
    ulawBuffer = null;
  }

  public void setMaxAmplitude (double maxAmplitude) {
    this.maxAmplitude = maxAmplitude;
    ulawBuffer = null;
  }

  public Object clone() {
    Wave w = new Wave();
    w.wave = new double [wave.length];
    for (int i = 0; i < wave.length; i++)
      w.wave[i] = wave[i];
    w.maxAmplitude = maxAmplitude;
    w.samplingRate = samplingRate;
    return w;
  }

  public Wave getSection (int start, int end) {
    if (start < 0) start = 0;
    if (start >= wave.length) start = wave.length - 1;
    if (end >= wave.length) end = wave.length - 1;
    if (end < 0) end = 0;
    double[] w = new double [end - start + 1];
    int i = 0;
    while (start <= end)
      w[i++] = wave[start++];
    return new Wave (w, samplingRate, maxAmplitude);
  }

  public int length() {
    return wave.length;
  }

  // This code is translated from libst.c out of Sox.  Very little has
  // been changed.

/* libst.c - portable sound tools library
*/

/*
** This routine converts from linear to ulaw.
**
** Craig Reese: IDA/Supercomputing Research Center
** Joe Campbell: Department of Defense
** 29 September 1989
**
** References:
** 1) CCITT Recommendation G.711  (very difficult to follow)
** 2) "A New Digital Technique for Implementation of Any
**     Continuous PCM Companding Law," Villeret, Michel,
**     et al. 1973 IEEE Int. Conf. on Communications, Vol 1,
**     1973, pg. 11.12-11.17
** 3) MIL-STD-188-113,"Interoperability and Performance Standards
**     for Analog-to_Digital Conversion Techniques,"
**     17 February 1987
**
** Input: Signed 16 bit linear sample
** Output: 8 bit ulaw sample
*/

static final boolean ZEROTRAP = true; /* turn on the trap as per the MIL-STD */
static final int BIAS = 0x84;   /* define the add-in bias for 16 bit samples */
static final int CLIP = 32635;

    static final int exp_lut[] = {0,0,1,1,2,2,2,2,3,3,3,3,3,3,3,3,
                               4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,
                               5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,
                               5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,
                               6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,
                               6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,
                               6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,
                               6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,
                               7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,
                               7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,
                               7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,
                               7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,
                               7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,
                               7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,
                               7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,
                               7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7};

static byte
st_linear_to_ulaw( int sample )
    {
    int sign, exponent, mantissa;
    byte ulawbyte;

    /* Get the sample into sign-magnitude. */
    sign = (sample >>> 24) & 0x80;		/* set aside the sign */
    if ( sign != 0 ) sample = -sample;		/* get magnitude */
    if ( sample > CLIP ) sample = CLIP;		/* clip the magnitude */

    /* Convert from 16 bit linear to ulaw. */
    sample = sample + BIAS;
    exponent = exp_lut[( sample >> 7 ) & 0xFF];
    mantissa = ( sample >> ( exponent + 3 ) ) & 0x0F;
    ulawbyte = (byte) (~ ( sign | ( exponent << 4 ) | mantissa ));
if (ZEROTRAP) {
    if ( ulawbyte == 0 ) ulawbyte = 0x02;	/* optional CCITT trap */
}

    return ulawbyte;
    }
  
  // Whew, that was ugly.  We're back to my code now...

  void calculateUlaw() {
    double increment = samplingRate / 8000.0;
    double position = 0.0;
    double r = 32767 / maxAmplitude;
    int n = (int) (wave.length / increment);
    ulawBuffer = new byte [n];
    for (int i = 0; i < n; i++) {
      ulawBuffer[i] = st_linear_to_ulaw ((int)(wave [(int) position] * r));
      position += increment;
    }
  }

  public byte[] getUlaw() {
    if (wave == null)
      return null;
    if (ulawBuffer == null)
      calculateUlaw();
    return ulawBuffer;
  }

  static void writeLong (OutputStream os, int l) throws IOException {
    os.write ((l >>>  0) & 0xff);
    os.write ((l >>>  8) & 0xff);
    os.write ((l >>> 16) & 0xff);
    os.write ((l >>> 24) & 0xff);
  }

  static int readLong (InputStream is) throws IOException {
    int ch1 = is.read();
    int ch2 = is.read();
    int ch3 = is.read();
    int ch4 = is.read();
    return ((ch4 << 24) + (ch3 << 16) + (ch2 << 8) + (ch1 << 0));
  }

  static void writeShort (OutputStream os, short s) throws IOException {
    os.write ((s >>> 0) & 0xff);
    os.write ((s >>> 8) & 0xff);
  }

  static short readShort (InputStream is) throws IOException {
    int ch1 = is.read();
    int ch2 = is.read();
    return (short)((ch2 << 8) + (ch1 << 0));
  }

  static final int RIFF = 0x46464952;
  static final int WAVE = 0x45564157;
  static final int FMT  = 0x20746d66;
  static final int DATA = 0x61746164;

  public void write (OutputStream os) throws IOException {
    writeLong (os, RIFF);
    writeLong (os, 36 + 2*wave.length);
    writeLong (os, WAVE);
    writeLong (os, FMT);
    writeLong (os, 16);
    writeShort (os, (short)1);
    writeShort (os, (short)1);
    writeLong (os, samplingRate);
    writeLong (os, 2*samplingRate);
    writeShort (os, (short)2);
    writeShort (os, (short)16);
    writeLong (os, DATA);
    writeLong (os, 2*wave.length);
    for (int i = 0; i < wave.length; i++) {
      double v = wave[i] / maxAmplitude * 32767;
      if (v <= -32768) v = -32768;
      if (v >= 32767) v = 32767;
      writeShort (os, (short) v);
    }
  }

  public void read (InputStream is) throws IOException, WaveFormatException {
    if (readLong (is) != RIFF) throw new WaveFormatException("Not a WAV file");
    readLong (is);	// file length - 8
    if (readLong (is) != WAVE) throw new WaveFormatException("WAVE");

    if (readLong (is) != FMT) throw new WaveFormatException("FMT");
    if (readLong (is) != 16) throw new WaveFormatException("16");
    if (readShort (is) != 1) throw new WaveFormatException("1");
    int modus = readShort (is);
    if (modus != 1 && modus != 2)
      throw new WaveFormatException ("Modus must be 1 or 2");
    samplingRate = readLong (is);
    readLong (is);	// bytes per second
    int sampleSize = readShort (is);
    if (sampleSize != 1 && sampleSize != 2)
      throw new WaveFormatException ("Sample size must be 1 or 2");
    int bitDepth = readShort (is);
    if (bitDepth != 8 && bitDepth != 16)
      throw new WaveFormatException ("Bit depth must be 8 or 16");
    double factor;

    while (readLong (is) != DATA) {
      int l = readLong (is);
      is.skip (l);
    }

    wave = new double[readLong (is)/sampleSize];
    if (bitDepth == 16 && modus == 2) {
      for (int i = 0; i < wave.length; i++) {
        wave[i] = readShort (is) / 32767.0 * maxAmplitude;
        wave[i] += readShort (is) / 32767.0 * maxAmplitude;
	wave[i] /= 2;
      }
    }
    else if (bitDepth == 16 && modus == 1) {
      for (int i = 0; i < wave.length; i++)
        wave[i] = readShort (is) / 32767.0 * maxAmplitude;
    }
    else if (bitDepth == 8 && modus == 2) {
      for (int i = 0; i < wave.length; i++) {
        wave[i] = (is.read() - 128) / 127.0 * maxAmplitude;
        wave[i] += (is.read() - 128) / 127.0 * maxAmplitude;
        wave[i] /= 2;
      }
    }
    else if (bitDepth == 8 && modus == 1) {
      for (int i = 0; i < wave.length; i++)
        wave[i] = (is.read() - 128) / 127.0 * maxAmplitude;
    }
    ulawBuffer = null;
  }
}
