/*
 * Copyright (c) 2011-2012, Peter Abeles. All Rights Reserved.
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

package boofcv.factory.geo.d3.epipolar;

import boofcv.abst.geo.epipolar.*;
import boofcv.alg.geo.AssociatedPair;
import boofcv.alg.geo.epipolar.EpipolarResiduals;
import boofcv.alg.geo.epipolar.h.HomographyLinear4;
import boofcv.alg.geo.epipolar.h.ResidualsHomographySampson;
import boofcv.alg.geo.epipolar.h.ResidualsHomographyTransfer;
import boofcv.alg.geo.epipolar.pose.PnPLepetitEPnP;
import boofcv.numerics.fitting.modelset.ModelGenerator;
import org.ejml.data.DenseMatrix64F;

/**
 * @author Peter Abeles
 */
public class FactoryEpipolar {

	/**
	 * Returns an algorithm for estimating a homography matrix given a set of
	 * {@link AssociatedPair}.
	 *
	 * @return Fundamental algorithm.
	 */
	public static EpipolarMatrixEstimator computeHomography( boolean normalize ) {
		HomographyLinear4 alg = new HomographyLinear4(normalize);
		return new WrapHomographyLinear(alg);
	}

	/**
	 * Creates a non-linear optimizer for refining estimates of homography matrices.
	 *
	 * @param tol Tolerance for convergence.  Try 1e-8
	 * @param maxIterations Maximum number of iterations it will perform.  Try 100 or more.
	 * @return Refinement
	 */
	public static RefineEpipolarMatrix refineHomography( double tol , int maxIterations  ,
														 EpipolarError type ) {
		EpipolarResiduals func;
		switch( type ) {
			case SIMPLE:
				func = new ResidualsHomographyTransfer();
				break;

			case SAMPSON:
				func = new ResidualsHomographySampson();
				break;

			default:
				throw new IllegalArgumentException("Type not supported: "+type);
		}

		return new LeastSquaresHomography(tol,maxIterations,func);
	}

	/**
	 * Returns an algorithm for estimating a fundamental/essential matrix given a set of 
	 * {@link AssociatedPair}.
	 * 
	 * @param isFundamental True if input observations are in pixels, false if they are normalized.
	 * @param minPoints Selects which algorithms to use.  Only 7 and 8 supported.
	 * @return Fundamental algorithm.
	 */
	public static EpipolarMatrixEstimator computeFundamental( boolean isFundamental , int minPoints ) {
		return new WrapFundamentalLinear(isFundamental,minPoints);
	}

	/**
	 * Creates a robust model generator for use with {@link boofcv.numerics.fitting.modelset.ModelMatcher},
	 *
	 * @param fundamentalAlg The algorithm which is being wrapped.
	 * @return ModelGenerator
	 */
	public static ModelGenerator<DenseMatrix64F,AssociatedPair>
	robustModel( EpipolarMatrixEstimator fundamentalAlg ) {
		return new FundamentalModelGenerator(fundamentalAlg);
	}

	/**
	 * Creates a non-linear optimizer for refining estimates of fundamental or essential matrices.
	 *
	 * @see boofcv.alg.geo.epipolar.f.ResidualsFundamentalSampson
	 * @see boofcv.alg.geo.epipolar.f.ResidualsFundamentalSimple
	 *
	 * @param tol Tolerance for convergence.  Try 1e-8
	 * @param maxIterations Maximum number of iterations it will perform.  Try 100 or more.
	 * @return Refinement
	 */
	public static RefineEpipolarMatrix refineFundamental( double tol , int maxIterations ,
														  EpipolarError type ) {
		switch( type ) {
			case SAMPSON:
				return new LeastSquaresFundamental(tol,maxIterations,true);
			case SIMPLE:
				return new LeastSquaresFundamental(tol,maxIterations,false);
		}

		throw new IllegalArgumentException("Type not supported: "+type);
	}

	/**
	 * Returns a solution to the PnP problem for 4 or more points using EPnP. Fast and fairly
	 * accurate algorithm.  Can handle general and planar scenario automatically.
	 *
	 * @see PnPLepetitEPnP
	 *
	 * @param numIterations If more then zero then non-linear optimization is done.  More is not always better.  Try 10
	 * @param magicNumber Affects how the problem is linearized.  See comments in {@link PnPLepetitEPnP}.  Try 0.1
	 * @return  PerspectiveNPoint
	 */
	public static PerspectiveNPoint pnpEfficientPnP( int numIterations , double magicNumber ) {
		PnPLepetitEPnP alg = new PnPLepetitEPnP(magicNumber);
		alg.setNumIterations(numIterations);
		return new WrapPnPLepetitEPnP(alg);
	}

}