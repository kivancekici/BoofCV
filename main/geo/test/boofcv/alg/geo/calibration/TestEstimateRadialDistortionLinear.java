package boofcv.alg.geo.calibration;

import boofcv.alg.calibration.CalibrationGridConfig;
import boofcv.alg.calibration.EstimateRadialDistortionLinear;
import georegression.geometry.GeometryMath_F64;
import georegression.struct.point.Point2D_F64;
import org.ejml.data.DenseMatrix64F;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;

/**
 * @author Peter Abeles
 */
public class TestEstimateRadialDistortionLinear {

	Random rand = new Random(34);

	/**
	 * Given perfect observations and a random scenario see if it can compute the distortion parameters
	 */
	@Test
	public void perfect() {

		double distort[] = new double[]{0.01,-0.002};
		DenseMatrix64F K = GenericCalibrationGrid.createStandardCalibration();
		List<DenseMatrix64F> homographies = GenericCalibrationGrid.createHomographies(K, 2, rand);

		CalibrationGridConfig config = GenericCalibrationGrid.createStandardConfig();

		List<List<Point2D_F64>> observations = new ArrayList<List<Point2D_F64>>();

		for( DenseMatrix64F H : homographies ) {
			// in calibrated image coordinates
			List<Point2D_F64> pixels = GenericCalibrationGrid.observations(H, config);
			// apply distortion
			for( Point2D_F64 p : pixels ) {
				distort(p, distort);
			}
			// put into pixel coordinates
			for( Point2D_F64 p : pixels ) {
				GeometryMath_F64.mult(K,p,p);
			}

			observations.add( pixels );
		}

		EstimateRadialDistortionLinear alg = new EstimateRadialDistortionLinear(config,distort.length);

		alg.process(K,homographies,observations);

		double found[] = alg.getParameters();

		for( int i = 0; i < distort.length; i++ )
			assertEquals(distort[i],found[i],1e-6);
	}

	/**
	 * Applies distortion to the provided pixel in calibrated coordinates
	 */
	private static void distort( Point2D_F64 p , double coef[] ) {
		double r = p.x*p.x + p.y*p.y;

		double m = 0;
		for( int i = 0; i < coef.length; i++ ) {
			m += coef[i]*Math.pow(r,i+1);
		}
		p.x += p.x*m;
		p.y += p.y*m;
	}
}
