/* (C) Copyright 1993 by Steven Trainoff.  Permission is granted to make
* any use of this code subject to the condition that all copies contain
* this notice and an indication of what has been changed.
*/
/* Translated to Java by Emanuel Borsboom <manny@zerius.victoria.bc.ca>
 * Removed multi-dimensional stuff (because I didn't need it)
 */

package fft;

public class FFT {

  /* This routine performs a complex fft.  It takes two arrays holding
   * the real and imaginary parts of the the complex numbers.  It performs
   * the fft and returns the result in the original arrays.  It destroys
   * the orginal data in the process.  Note the array returned is NOT
   * normalized.  Each element must be divided by n to get dimensionally
   * correct results.  This routine takes optional arrays for the sines, cosine
   * and bitreversal.  If any of these pointers are null, all of the arrays are 
   * regenerated.
   */

  public static void fft(double[][] x, FFTArrays a)
  {
	int n = x.length;
	int nu = ilog2(n);      /* Number of data points */
	int dual_space = n;	/* Spacing between dual nodes */
	int nu1 = nu;		/* = nu-1 right shift needed when finding p */
	int k;			/* Iteration of factor array */
	int i; 			/* Number of dual node pairs considered */
	int j; 			/* Index into factor array */
	
	/* For each iteration of factor matrix */
	for (k = 0; k < nu; k++) {
		/* Initialize */
  	        dual_space /= 2;     /* Fewer elements in each set of duals */
		nu1--;			/* nu1 = nu - 1 */

		/* For each set of duals */
		for(j = 0; j < n; j += dual_space) {
			/* For each dual node pair */
			for (i = 0; i < dual_space; i++, j++) {
				double treal, timag;		/* Temp of w**p */
				int p = a.rev[j >> nu1];
				
				treal = x[j+dual_space][0]*a.c[p] + x[j+dual_space][1]*a.s[p];
				timag = x[j+dual_space][1]*a.c[p] - x[j+dual_space][0]*a.s[p];

				x[j+dual_space][0] = x[j][0] - treal;
				x[j+dual_space][1] = x[j][1] - timag;

				x[j][0] += treal;
				x[j][1] += timag;
			}
		}
	}

	/* We are done with the transform, now unscamble results */
	for (j = 0; j < n; j++) {
		if ((i = a.rev[j]) > j) {
			double treal, timag;

			/* Swap */
			treal = x[j][0];
			timag = x[j][1];

			x[j][0] = x[i][0];
			x[j][1] = x[i][1];

			x[i][0] = treal;
			x[i][1] = timag;
		}
	}
  }

  public static void fft(double[][] x) {
    fft (x, new FFTArrays (x.length));
  }

  /* invfft performs an inverse fft */
  public static void invfft(double[][] x, FFTArrays a)
  {
	int i;
	
	/* Negate the sin array to do the inverse transform */
	for (i = 0; i < x.length; i++)
		a.s[i] = -a.s[i];
	
	fft(x, a);
	
		/* Put the sin array back */
        for (i = 0; i < x.length; i++)
		a.s[i] = - a.s[i];
  }

  public static void invfft (double[][] x) {
    fft (x, new FFTArrays (x.length));
  }

 /* This routine normalized the elements of a 1d fft by dividing by the number of elements
  * so that fft, inversefft, normalizefft is an identity
  */
  public static void normalize(double[][] x)
  {
    int i;
    
    for (i = 0; i < x.length; i++) {
      x[i][0] /= x.length;
      x[i][1] /= x.length;
    }
  }
 
  /* This routine takes an array of real numbers and performs a fft.  It
   * returns the magnitude of the fft in the original array.  This routine
   * uses an order n/2 complex ft and disentangles the results.  This is
   * much more efficient than using an order n complex fft with the
   * imaginary component set to zero.  We return the mean in data[0]
   * and the Nyquist frequency in data[n/w].  The rest of data is
   * left untouched.  The results are normalized.
   */

  public static void realfftmag(double[] data)
  {
	int n = data.length;
	double x[][];			/* Temp array used perform fft */
	int i;

	x = new double [n/2][2];
	
	/* Load data into temp array
	 * even terms end up in x[n][0] odd terms in x[n][1]
	 */
	for (i = 0; i < n; i++) 
	  x[i/2][i%2] = data[i];
	
	fft(x);

	/* Load results into output array */

	/* i = 0 needs to be treated separately */
	data[0] = (x[0][0] + x[0][1])/n;

	for (i = 1; i < n/2; i++) {
		double xr, xi;
		double  arg, ti, tr;
		double c, s;		/* Cosine and sin */

		arg = 2 * Math.PI * i / n;
		c = Math.cos(arg);/* These are different c,s than used in fft */
		s = Math.sin(arg);

		ti = (x[i][1] + x[n/2-i][1]) / 2;
		tr = (x[i][0] - x[n/2-i][0]) / 2;

		xr = (x[i][0] + x[n/2-i][0])/2 + c * ti - s * tr;
		xi = (x[i][1] - x[n/2-i][1])/2 - s * ti - c * tr;

		xr /= n/2;
		xi /= n/2;
		
		data[i] = Math.sqrt(xr*xr + xi*xi);
	}
	
	/* Nyquist frequency is returned in data[0] */
	data[n/2] = (x[0][0] - x[0][1])/n;
  }

  /* Computes a^b where a and b are integers */
  public static int ipow(int a, int b)
  {
    int i;
    int sum = 1;
    
    for (i = 0; i < b; i++)	
      sum *= a;
    return (sum);
  }

  /* Computes log2(n).  Returns -1 if n == 0*/
  public static int ilog2(int n)
  {
    int i;
    
    for (i = -1; n != 0; i++, n>>=1)
      ;
    return(i);
  }

}

