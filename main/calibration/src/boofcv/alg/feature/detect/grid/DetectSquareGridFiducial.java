/*
 * Copyright (c) 2011-2015, Peter Abeles. All Rights Reserved.
 *
 * This file is part of BoofCV (http://boofcv.org).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package boofcv.alg.feature.detect.grid;

import boofcv.abst.filter.binary.InputToBinary;
import boofcv.alg.feature.detect.squares.*;
import boofcv.alg.shapes.polygon.BinaryPolygonConvexDetector;
import boofcv.struct.image.ImageSingleBand;
import boofcv.struct.image.ImageUInt8;
import georegression.struct.point.Point2D_F64;
import georegression.struct.shapes.Polygon2D_F64;
import org.ddogleg.struct.FastQueue;

import java.util.ArrayList;
import java.util.List;

/**
 * Detect a square grid calibration target and returns the corner points of each square.  This calibration grid is
 * specified by a set of squares which are organized in a grid pattern.  All squares are the same size.  The entire
 * grid must be visible.  Space between the squares is specified as a ratio of the square size. The grid will be
 * oriented so that returned points are in counter clockwise (CCW) ordering, which appears to be CW in the image.
 *
 * There is also always at least two solutions to the ordering.  For sake of consistency it will select
 * the orientation where index 0 is the closest to the origin.
 *
 * @author Peter Abeles
 */
// TODO tell the polygon detector that there should be no inner contour
public class DetectSquareGridFiducial<T extends ImageSingleBand> {

	// dimension of square grid.  This only refers to black squares and not the white space
	int numCols;
	int numRows;

	// converts input image into a binary image
	InputToBinary<T> inputToBinary;

	// detector for squares
	BinaryPolygonConvexDetector<T> detectorSquare;

	// Converts detected squares into a graph and into grids
	SquaresIntoClusters s2c;
	ClustersIntoGrids c2g;

	// output results.  Grid of calibration points in row-major order
	List<Point2D_F64> calibrationPoints = new ArrayList<Point2D_F64>();
	int calibRows;
	int calibCols;

	SquareGridTools tools = new SquareGridTools();

	// storage for binary image
	ImageUInt8 binary = new ImageUInt8(1,1);

	/**
	 * COnfigures the detector
	 *
	 * @param numRows Number of black squares in the grid rows
	 * @param numCols Number of black squares in the grid columns
	 * @param spaceToSquareRatio Ratio of spacing between the squares and the squares width
	 * @param inputToBinary Converts input image into a binary image
	 * @param detectorSquare Detects the squares in the image.  Must be configured to detect squares
	 */
	public DetectSquareGridFiducial(int numRows, int numCols, double spaceToSquareRatio,
									InputToBinary<T> inputToBinary ,
									BinaryPolygonConvexDetector<T> detectorSquare) {
		this.numCols = numCols;
		this.numRows = numRows;
		this.inputToBinary = inputToBinary;
		this.detectorSquare = detectorSquare;

		s2c = new SquaresIntoClusters(spaceToSquareRatio,6);
		c2g = new ClustersIntoGrids(numCols*numRows);
	}

	/**
	 * Process the image and detect the calibration target
	 *
	 * @param image Input image
	 * @return true if a calibration target was found and false if not
	 */
	public boolean process( T image ) {
		binary.reshape(image.width,image.height);

		inputToBinary.process(image,binary);
		detectorSquare.process(image, binary);

		FastQueue<Polygon2D_F64> found = detectorSquare.getFound();

		List<List<SquareNode>> clusters = s2c.process(found.toList());
		c2g.process(clusters);
		List<SquareGrid> grids = c2g.getGrids();

		SquareGrid match = null;
		double matchSize = 0;
		for( SquareGrid g : grids ) {
			if (g.columns != numCols || g.rows != numRows) {
				if( g.columns == numRows && g.rows == numCols ) {
					tools.transpose(g);
				} else {
					continue;
				}
			}

			double size = tools.computeSize(g);
			if( size > matchSize ) {
				matchSize = size;
				match = g;
			}
		}

		if( match != null ) {
			if( tools.checkFlip(match) ) {
				tools.flipRows(match);
			}
			tools.putIntoCanonical(match);
			if( !tools.orderSquareCorners(match) )
				return false;

			extractCalibrationPoints(match);
			return true;
		}
		return false;
	}

	List<Point2D_F64> row0 = new ArrayList<Point2D_F64>();
	List<Point2D_F64> row1 = new ArrayList<Point2D_F64>();
	/**
	 * Extracts the calibration points from the corners of a fully ordered grid
	 */
	void extractCalibrationPoints(SquareGrid grid) {
		calibrationPoints.clear();
		for (int row = 0; row < grid.rows; row++) {

			row0.clear();
			row1.clear();
			for (int col = 0; col < grid.columns; col++) {
				Polygon2D_F64 square = grid.get(row,col).corners;

				row0.add(square.get(0));
				row0.add(square.get(1));
				row1.add(square.get(3));
				row1.add(square.get(2));

			}
			calibrationPoints.addAll(row0);
			calibrationPoints.addAll(row1);
		}

		calibCols = grid.columns*2;
		calibRows = grid.rows*2;
	}

	public List<Point2D_F64> getCalibrationPoints() {
		return calibrationPoints;
	}

	public int getCalibrationRows() {
		return calibRows;
	}

	public int getCalibrationCols() {
		return calibCols;
	}
}