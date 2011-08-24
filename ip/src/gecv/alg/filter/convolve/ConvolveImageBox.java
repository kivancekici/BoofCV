/*
 * Copyright 2011 Peter Abeles
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package gecv.alg.filter.convolve;

import gecv.alg.InputSanityCheck;
import gecv.alg.filter.convolve.border.ConvolveJustBorder_General;
import gecv.alg.filter.convolve.noborder.ImplConvolveBox;
import gecv.core.image.border.ImageBorderValue;
import gecv.factory.filter.kernel.FactoryKernel;
import gecv.struct.convolve.Kernel1D_F32;
import gecv.struct.convolve.Kernel1D_I32;
import gecv.struct.image.*;

/**
 * Convolves a kernel which is composed entirely of 1's across an image.  This special kernel can be highly optimized
 * and has a computational complexity independent of the kernel size. 
 *
 * @author Peter Abeles
 */
public class ConvolveImageBox {

	/**
	 * Performs a horizontal 1D convolution of a box kernel across the image
	 *
	 * @param input	 The original image. Not modified.
	 * @param output	 Where the resulting image is written to. Modified.
	 * @param radius Kernel size.
	 * @param includeBorder Should the vertical border of the image be processed?
	 */
	public static void horizontal(ImageFloat32 input, ImageFloat32 output, int radius, boolean includeBorder) {
		InputSanityCheck.checkSameShape(input , output);

		Kernel1D_F32 kernel = FactoryKernel.table1D_F32(radius,false);
		ConvolveJustBorder_General.horizontal(kernel,ImageBorderValue.wrap(input,0),output,radius);
		ImplConvolveBox.horizontal(input, output, radius, includeBorder);
	}

	/**
	 * Performs a horizontal 1D convolution of a box kernel across the image
	 *
	 * @param input	 The original image. Not modified.
	 * @param output	 Where the resulting image is written to. Modified.
	 * @param radius Kernel size.
	 * @param includeBorder Should the vertical border of the image be processed?
	 */
	public static void horizontal(ImageUInt8 input, ImageInt16 output, int radius, boolean includeBorder) {
		InputSanityCheck.checkSameShape(input , output);

		Kernel1D_I32 kernel = FactoryKernel.table1D_I32(radius);
		ConvolveJustBorder_General.horizontal(kernel,ImageBorderValue.wrap(input,0),output,radius);
		ImplConvolveBox.horizontal(input, output, radius, includeBorder);
	}

	/**
	 * Performs a horizontal 1D convolution of a box kernel across the image
	 *
	 * @param input	 The original image. Not modified.
	 * @param output	 Where the resulting image is written to. Modified.
	 * @param radius Kernel size.
	 * @param includeBorder Should the vertical border of the image be processed?
	 */
	public static void horizontal(ImageUInt8 input, ImageSInt32 output, int radius, boolean includeBorder) {
		InputSanityCheck.checkSameShape(input , output);

		Kernel1D_I32 kernel = FactoryKernel.table1D_I32(radius);
		ConvolveJustBorder_General.horizontal(kernel,ImageBorderValue.wrap(input,0),output,radius);
		ImplConvolveBox.horizontal(input, output, radius, includeBorder);
	}

	/**
	 * Performs a horizontal 1D convolution of a box kernel across the image
	 *
	 * @param input	 The original image. Not modified.
	 * @param output	 Where the resulting image is written to. Modified.
	 * @param radius Kernel size.
	 * @param includeBorder Should the vertical border of the image be processed?
	 */
	public static void horizontal(ImageSInt16 input, ImageInt16 output, int radius, boolean includeBorder) {
		InputSanityCheck.checkSameShape(input , output);

		Kernel1D_I32 kernel = FactoryKernel.table1D_I32(radius);
		ConvolveJustBorder_General.horizontal(kernel,ImageBorderValue.wrap(input,0),output,radius);
		ImplConvolveBox.horizontal(input, output, radius, includeBorder);
	}

	/**
	 * Performs a vertical 1D convolution of a box kernel across the image
	 *
	 * @param input	 The original image. Not modified.
	 * @param output	 Where the resulting image is written to. Modified.
	 * @param radius Kernel size.
	 * @param includeBorder Should the horizontal border of the image be processed?
	 */
	public static void vertical(ImageFloat32 input, ImageFloat32 output, int radius, boolean includeBorder) {
		InputSanityCheck.checkSameShape(input , output );

		Kernel1D_F32 kernel = FactoryKernel.table1D_F32(radius,false);
		ConvolveJustBorder_General.vertical(kernel,ImageBorderValue.wrap(input,0),output,radius);
		ImplConvolveBox.vertical(input, output, radius, includeBorder);
	}

	/**
	 * Performs a vertical 1D convolution of a box kernel across the image
	 *
	 * @param input	 The original image. Not modified.
	 * @param output	 Where the resulting image is written to. Modified.
	 * @param radius Kernel size.
	 * @param includeBorder Should the horizontal border of the image be processed?
	 */
	public static void vertical(ImageUInt8 input, ImageInt16 output, int radius, boolean includeBorder) {
		InputSanityCheck.checkSameShape(input , output);

		Kernel1D_I32 kernel = FactoryKernel.table1D_I32(radius);
		ConvolveJustBorder_General.vertical(kernel,ImageBorderValue.wrap(input,0),output,radius);
		ImplConvolveBox.vertical(input, output, radius, includeBorder);
	}

	/**
	 * Performs a vertical 1D convolution of a box kernel across the image
	 *
	 * @param input	 The original image. Not modified.
	 * @param output	 Where the resulting image is written to. Modified.
	 * @param radius Kernel size.
	 * @param includeBorder Should the horizontal border of the image be processed?
	 */
	public static void vertical(ImageUInt8 input, ImageSInt32 output, int radius, boolean includeBorder) {
		InputSanityCheck.checkSameShape(input , output);

		Kernel1D_I32 kernel = FactoryKernel.table1D_I32(radius);
		ConvolveJustBorder_General.vertical(kernel,ImageBorderValue.wrap(input,0),output,radius);
		ImplConvolveBox.vertical(input, output, radius, includeBorder);
	}

	/**
	 * Performs a vertical 1D convolution of a box kernel across the image
	 *
	 * @param input	 The original image. Not modified.
	 * @param output	 Where the resulting image is written to. Modified.
	 * @param radius Kernel size.
	 * @param includeBorder Should the horizontal border of the image be processed?
	 */
	public static void vertical(ImageSInt16 input, ImageInt16 output, int radius, boolean includeBorder) {
		InputSanityCheck.checkSameShape(input , output);

		Kernel1D_I32 kernel = FactoryKernel.table1D_I32(radius);
		ConvolveJustBorder_General.vertical(kernel,ImageBorderValue.wrap(input,0),output,radius);
		ImplConvolveBox.vertical(input, output, radius, includeBorder);
	}
}