package functions;

public class FastMath {
	private static final double RAD = (double) Math.PI / 180.0F;
	
	   private static final int SIN_BITS = 12;
	   private static final int SIN_MASK = ~(-1 << SIN_BITS);
	   private static final int SIN_COUNT = SIN_MASK + 1;
	
	   private static final double radFull = (double) (Math.PI * 2.0);
	   private static final double degFull = (double) (360.0);
	   private static final double radToIndex = SIN_COUNT / radFull;
	   private static final double degToIndex = SIN_COUNT / degFull;
	
	   private static final double[] sin = new double[SIN_COUNT];
	   private static final double[] cos = new double[SIN_COUNT];

	   static
	   {
	      for (int i = 0; i < SIN_COUNT; i++)
	      {
	         sin[i] = (double) Math.sin((double) (i + 0.5f) / SIN_COUNT * radFull);
	         cos[i] = (double) Math.cos((double) (i + 0.5f) / SIN_COUNT * radFull);
	      }
	   }



	   /**
	    * SIN / COS (RAD)
	    */

	   public static final double sin(double d) {
	      return sin[(int) (d * radToIndex) & SIN_MASK];
	   }

	   public static final double cos(double rad) {
	      return cos[(int) (rad * radToIndex) & SIN_MASK];
	   }
	   
	   public static final double sind(double deg) {
		   return sin(deg*0.01745328f);
	   }
	   public static final double cosd(double deg) {
		   return cos(deg*0.01745328f);
	   }



	   /**
	    * SIN / COS (DEG)
	    */

	   public static final double sinDeg(double deg)
	   {
	      return sin[(int) (deg * degToIndex) & SIN_MASK];
	   }

	   public static final double cosDeg(double deg)
	   {
	      return cos[(int) (deg * degToIndex) & SIN_MASK];
	   }



	   /**
	    * SIN / COS (DEG - STRICT)
	    */

	   public static final double sinDegStrict(double deg)
	   {
	      return (double) Math.sin(deg * RAD);
	   }

	   public static final double cosDegStrict(double deg)
	   {
	      return (double) Math.cos(deg * RAD);
	   }
		
	
	   /**
	* ATAN2
	*/
		
	   private static final int           SIZE                 = 1024;
	    private static final double        STRETCH            = 3.14159f;
	    // Output will swing from -STRETCH to STRETCH (default: Math.PI)
	    // Useful to change to 1 if you would normally do "atan2(y, x) / Math.PI"

	    // Inverse of SIZE
	    private static final int        EZIS            = -SIZE;
	    private static final double[]    ATAN2_TABLE_PPY    = new double[SIZE + 1];
	    private static final double[]    ATAN2_TABLE_PPX    = new double[SIZE + 1];
	    private static final double[]    ATAN2_TABLE_PNY    = new double[SIZE + 1];
	    private static final double[]    ATAN2_TABLE_PNX    = new double[SIZE + 1];
	    private static final double[]    ATAN2_TABLE_NPY    = new double[SIZE + 1];
	    private static final double[]    ATAN2_TABLE_NPX    = new double[SIZE + 1];
	    private static final double[]    ATAN2_TABLE_NNY    = new double[SIZE + 1];
	    private static final double[]    ATAN2_TABLE_NNX    = new double[SIZE + 1];

	    static
	    {
	        for (int i = 0; i <= SIZE; i++)
	        {
	            double f = (double)i / SIZE;
	            ATAN2_TABLE_PPY[i] = (double)(StrictMath.atan(f) * STRETCH / StrictMath.PI);
	            ATAN2_TABLE_PPX[i] = STRETCH * 0.5f - ATAN2_TABLE_PPY[i];
	            ATAN2_TABLE_PNY[i] = -ATAN2_TABLE_PPY[i];
	            ATAN2_TABLE_PNX[i] = ATAN2_TABLE_PPY[i] - STRETCH * 0.5f;
	            ATAN2_TABLE_NPY[i] = STRETCH - ATAN2_TABLE_PPY[i];
	            ATAN2_TABLE_NPX[i] = ATAN2_TABLE_PPY[i] + STRETCH * 0.5f;
	            ATAN2_TABLE_NNY[i] = ATAN2_TABLE_PPY[i] - STRETCH;
	            ATAN2_TABLE_NNX[i] = -STRETCH * 0.5f - ATAN2_TABLE_PPY[i];
	        }
	    }

	    /**
	     * ATAN2
	     */

	    public static final double atan2(double y, double x)
	    {
	        if (x >= 0)
	        {
	            if (y >= 0)
	            {
	                if (x >= y)
	                    return ATAN2_TABLE_PPY[(int)(SIZE * y / x + 0.5)];
	                else
	                    return ATAN2_TABLE_PPX[(int)(SIZE * x / y + 0.5)];
	            }
	            else
	            {
	                if (x >= -y)
	                    return ATAN2_TABLE_PNY[(int)(EZIS * y / x + 0.5)];
	                else
	                    return ATAN2_TABLE_PNX[(int)(EZIS * x / y + 0.5)];
	            }
	        }
	        else {
	            if (y >= 0) {
	                if (-x >= y)
	                    return ATAN2_TABLE_NPY[(int)(EZIS * y / x + 0.5)];
	                else
	                    return ATAN2_TABLE_NPX[(int)(EZIS * x / y + 0.5)];
	            }
	            else
	            {
	                if (x <= y) // (-x >= -y)
	                    return ATAN2_TABLE_NNY[(int)(SIZE * y / x + 0.5)];
	                else
	                    return ATAN2_TABLE_NNX[(int)(SIZE * x / y + 0.5)];
	            }
	        }
	    }


	public static double abs(double x) {
		return (x >= 0) ? x : -x;
	}

	public static double calcAngleDiff(double a1, double a2) {
		return 180 - abs(abs(a1 - a2) - 180);
	}
	public static double calcAngleSubt(double a1, double a2) {
		int s = (a1 >= a2) ? ((a1 - a2 > 180) ? -1 : 1) : ((a2 - a1 > 180) ? 1 : -1);
		return s*(180 - abs(abs(a1 - a2) - 180));
	}

	public static double testAtan2(double y, double x) {
	    if (y >= 0)
	        return (x >= 0 ? y/(x+y) : 1-x/(-x+y)); 
	    else
	        return (x < 0 ? 2-y/(-x-y) : 3+x/(x-y)); 
	}
}
