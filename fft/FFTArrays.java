/* (C) Copyright 1993 by Steven Trainoff.  Permission is granted to make
* any use of this code subject to the condition that all copies contain
* this notice and an indication of what has been changed.
*/
/* Translated to Java by Emanuel Borsboom <manny@zerius.victoria.bc.ca>
 * Removed multi-dimensional stuff (because I didn't need it)
 */

package fft;

public class FFTArrays {
  double[] c;
  double[] s;
  int[] rev;

  /* fft_create_arrays generates the sine and cosine arrays needed in the
   * forward complex fft.  It allocates storaged and return pointers
   * to the initialized arrays 
   */

  public FFTArrays (int n)
  {
    int i;
    int nu = FFT.ilog2(n);

    /* Compute temp array of sins and cosines */
    c = new double [n];
    s = new double [n];
    rev = new int [n];
    
    for (i = 0; i < n; i++) {
      double arg = 2 * Math.PI * i/n;
      c[i] = Math.cos(arg);
      s[i] = Math.sin(arg);
      rev[i] = bitrev(i, nu);
    }
  }

  /* This routine reverses the bits in integer */
  static int bitrev(int k, int nu)
  {
    int i;
    int out = 0;
    
    for (i = 0; i < nu; i++) {
      out <<= 1;
      out |= k & 1;
      k >>>= 1;
    }
    return(out);
  }

}


