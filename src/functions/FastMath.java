package functions;

public class FastMath {
	private static final float RAD = (float) Math.PI / 180.0F;
	
	   private static final int SIN_BITS = 12;
	   private static final int SIN_MASK = ~(-1 << SIN_BITS);
	   private static final int SIN_COUNT = SIN_MASK + 1;
	
	   private static final float radFull = (float) (Math.PI * 2.0);
	   private static final float degFull = (float) (360.0);
	   private static final float radToIndex = SIN_COUNT / radFull;
	   private static final float degToIndex = SIN_COUNT / degFull;
	
	   private static final float[] sin = new float[SIN_COUNT];
	   private static final float[] cos = new float[SIN_COUNT];

	   static
	   {
	      for (int i = 0; i < SIN_COUNT; i++)
	      {
	         sin[i] = (float) Math.sin((float) (i + 0.5f) / SIN_COUNT * radFull);
	         cos[i] = (float) Math.cos((float) (i + 0.5f) / SIN_COUNT * radFull);
	      }
	   }



	   /**
	    * SIN / COS (RAD)
	    */

	   public static final float sin(float rad) {
	      return sin[(int) (rad * radToIndex) & SIN_MASK];
	   }

	   public static final float cos(float rad) {
	      return cos[(int) (rad * radToIndex) & SIN_MASK];
	   }
	   
	   public static final float sind(float deg) {
		   return sin(deg*0.01745328f);
	   }
	   public static final float cosd(float deg) {
		   return cos(deg*0.01745328f);
	   }



	   /**
	    * SIN / COS (DEG)
	    */

	   public static final float sinDeg(float deg)
	   {
	      return sin[(int) (deg * degToIndex) & SIN_MASK];
	   }

	   public static final float cosDeg(float deg)
	   {
	      return cos[(int) (deg * degToIndex) & SIN_MASK];
	   }



	   /**
	    * SIN / COS (DEG - STRICT)
	    */

	   public static final float sinDegStrict(float deg)
	   {
	      return (float) Math.sin(deg * RAD);
	   }

	   public static final float cosDegStrict(float deg)
	   {
	      return (float) Math.cos(deg * RAD);
	   }
		
	
	   /**
	* ATAN2
	*/
		
	   private static final int           SIZE                 = 1024;
	    private static final float        STRETCH            = 3.14159f;
	    // Output will swing from -STRETCH to STRETCH (default: Math.PI)
	    // Useful to change to 1 if you would normally do "atan2(y, x) / Math.PI"

	    // Inverse of SIZE
	    private static final int        EZIS            = -SIZE;
	    private static final float[]    ATAN2_TABLE_PPY    = new float[SIZE + 1];
	    private static final float[]    ATAN2_TABLE_PPX    = new float[SIZE + 1];
	    private static final float[]    ATAN2_TABLE_PNY    = new float[SIZE + 1];
	    private static final float[]    ATAN2_TABLE_PNX    = new float[SIZE + 1];
	    private static final float[]    ATAN2_TABLE_NPY    = new float[SIZE + 1];
	    private static final float[]    ATAN2_TABLE_NPX    = new float[SIZE + 1];
	    private static final float[]    ATAN2_TABLE_NNY    = new float[SIZE + 1];
	    private static final float[]    ATAN2_TABLE_NNX    = new float[SIZE + 1];

	    static
	    {
	        for (int i = 0; i <= SIZE; i++)
	        {
	            float f = (float)i / SIZE;
	            ATAN2_TABLE_PPY[i] = (float)(StrictMath.atan(f) * STRETCH / StrictMath.PI);
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

	    public static final float atan2(float y, float x)
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


	public static float abs(float x) {
		return (x >= 0) ? x : -x;
	}

	public static float calcAngleDiff(float a1, float a2) {
		return 180 - abs(abs(a1 - a2) - 180);
	}
	public static float calcAngleSubt(float a1, float a2) {
		int s = (a1 >= a2) ? ((a1 - a2 > 180) ? -1 : 1) : ((a2 - a1 > 180) ? 1 : -1);
		return s*(180 - abs(abs(a1 - a2) - 180));
	}

	public static float testAtan2(float y, float x) {
	    if (y >= 0)
	        return (x >= 0 ? y/(x+y) : 1-x/(-x+y)); 
	    else
	        return (x < 0 ? 2-y/(-x-y) : 3+x/(x-y)); 
	}
}
