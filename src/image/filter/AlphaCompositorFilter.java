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
public class AlphaCompositorFilter {
	
	public AlphaCompositorFilter() {
	}

	public void filter(BufferedImage image, BufferedImage mask) {
		final int width = image.getWidth();
		int[] imgData = new int[width];
		int[] maskData = new int[width];

		for (int y = 0; y < image.getHeight(); y++) {
		    // fetch a line of data from each image
		    image.getRGB(0, y, width, 1, imgData, 0, 1);
		    mask.getRGB(0, y, width, 1, maskData, 0, 1);
		    // apply the mask
		    for (int x = 0; x < width; x++) {
		        int color = imgData[x] & 0x00FFFFFF; // mask away any alpha present
		        int maskColor = (maskData[x] & 0x00FF0000) << 8; // shift red into alpha bits
		        color |= maskColor;
		        imgData[x] = color;
		    }
		    // replace the data
		    image.setRGB(0, y, width, 1, imgData, 0, 1);
		}
	}

	public String toString() {
		return "Alpha Compositor";
	}
}
