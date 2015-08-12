/*
Copyright 2006 Jerry Huxtable

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package image.filter;

import java.awt.*;
import java.awt.image.*;

import com.jhlabs.image.WholeImageFilter;

/**
 * An edge-detection filter.
 */
public class BGEraserFilter extends WholeImageFilter {
	
		public BGEraserFilter() {
	}

	protected int[] filterPixels( int width, int height, int[] inPixels, Rectangle transformedSpace ) {
		int index = 0, r, g, b, a;
		int bR, bG, bB;
		int[] outPixels = new int[width * height];

		bR = outPixels[0] & 0x00ff0000;
		bG = outPixels[0] & 0x0000ff00;
		bB = outPixels[0] & 0x000000ff;
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int rgba = inPixels[index];

				a = rgba & 0xff000000;
				r = rgba & 0x00ff0000;
				g = rgba & 0x0000ff00;
				b = rgba & 0x000000ff;
				
				if(r == bR && g == bG && b == bB)
					a = 0;
				
				outPixels[index++] = (a << 24) | (r << 16) | (g << 8) | b;
			}
		}
		return outPixels;
	}

	public String toString() {
		return "Edges/Detect Edges";
	}
}
