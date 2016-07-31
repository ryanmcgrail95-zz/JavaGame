package resource.image;

import time.Stopwatch;

public interface Img {
	public static Stopwatch s = new Stopwatch(), k = new Stopwatch();
	
	public static enum AlphaType{NORMAL, BG_ALPHA, GRAYSCALE_MASK};
	public static enum FilterType{NONE, HQX_2X, HQX_3X, HQX_4X, XBRZ_2X, XBRZ_3X, XBRZ_4X, XBRZ_5X};
	
	public static AlphaType[] alphaTypeList = AlphaType.values();
	public static FilterType[] filterTypeList = FilterType.values();
}
