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

import gfx.RGBA;

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
		int[] bg, col = new int[4];
		int[] outPixels = new int[width * height];

		bg = RGBA.convertInt2RGBA(inPixels[0]);
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				RGBA.convertInt2RGBA(inPixels[index], col);

				r = col[0];
				g = col[1];
				b = col[2];
				a = col[3];
				
				if(r == bg[0] && g == bg[1] && b == bg[2])
					a = 0;
				
				outPixels[index++] = RGBA.convertRGBA2Int(r, g, b, a); //(a << 24) | (r << 16) | (g << 8) | b;
			}
		}
		return outPixels;
	}

	public String toString() {
		return "Edges/Detect Edges";
	}
}
