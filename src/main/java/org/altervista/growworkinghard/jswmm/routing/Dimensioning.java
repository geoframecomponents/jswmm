/*
 * This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.altervista.growworkinghard.jswmm.routing;

import it.blogspot.geoframe.hydroGeoEntities.line.Pipe;
import it.blogspot.geoframe.utils.GEOconstants;
import it.blogspot.geoframe.utils.GEOgeometry;

public class Dimensioning {

	public Double gaucklerStricklerCoefficient;
	public Double fillCoefficient;
	public Double discharge;

	/*private static double fillAngle;
	private double diameter;
	private double minSlope;
	private double pipeSlope;
	private double elevationEndPoint;
	*/

	public Pipe pipe;

	/**
	 * Setter of the class fields.
	 *
	 *        This method set the fields of the class and the parameters of the
	 *        local <strong>pipe</strong> object calling
	 *        <strong>setFields</strong> method. Furthermore it sets first
	 *        attempt values calling <strong>setFirstAttemptValues</strong>
	 *        method evaluating the remaining field values.
	 * 
	 * @param[in] pipe <strong>Pipe</strong> object that contains all the
	 *            necessary information about the considered HydroGeoEntity.
	 */

	private void setPipe(final Pipe pipe) {
		this.pipe = pipe;
		setFields();
		setFirstAttemptValues();
	}



	/**
	 * Computation of minimum slope.
	 * 
	 *        Evaluation of minimum slope due to fixed shear stress at the base
	 *        of the pipe with the relation \f[ i_{min}=\frac{\tau}{\gamma*R_h}
	 *        \f] where the \f$\tau\f$ is the shear stress, \f$\gamma\f$ is the
	 *        specific weight of water and \f$R_h\f$ the hydraulic radius.
	 * 
	 * @todo Build a method to use commercial pipe dimensions.
	 */
	private double computeMinSlope(Double diameter) {
		double hydraulicRadius = computeHydraulicRadius(diameter);

		return GEOconstants.SHEARSTRESS
				/ (GEOconstants.WSPECIFICWEIGHT * hydraulicRadius);
	}



	/**
	 * Evaluation of diameter related to fixed slope.
	 *
	 *        Computation of the diameter of a pipe with a slope assigned in
	 *        input through the relation \f[ D = \frac{ \Big({ \frac{ \theta\,Q
	 *        }{ K_s\,\sqrt{i_f} } \Big)}^{^3/_8} }{ {\Big(
	 *        1-\frac{sin(\theta)}{\theta} \Big)}^{^5/_8} }\,10^{-^9/_8} \f]
	 *        where \f$ Q \f$ is the discharge, \f$ \theta \f$ the fill angle,
	 *        \f$ K_s \f$ the Gauckler Strickler coefficient and \f$ i_f \f$ the
	 *        fixed slope. The factor \f$ 10^{-^9/_8} \f$ is a unit transform
	 *        coefficient. The value returned is in meters.
	 *
	 * @param[in] slope Slope of the pipe to compute the relative diameter fixed
	 *            the class fields.
	 */

	private double computeDiameter(double slope) {
		final double pow1 = 3.0 / 8;
		double numerator = Math.pow((discharge * fillAngle)
				/ (gaucklerStricklerCoefficient * Math.pow(slope, 0.5)), pow1);
		final double pow2 = 5.0 / 8;
		double denominator = Math
				.pow(1 - Math.sin(fillAngle) / fillAngle, pow2);
		final double pow3 = -9.0 / 8;

		return numerator / denominator * Math.pow(10, pow3);
	}

	/**
	 * Evaluation of velocity.
	 *
	 *        Computation of the velocity of the water in the channel through
	 *        the relation \f[ v = \frac{8\,Q}{D^2\,(\theta - sin(\theta))} \f]
	 *        where \f$ D \f$ is the diameter, \f$ Q \f$ the discharge and \f$
	 *        \theta \f$ the fill angle in the channel.
	 */

	private double computeVelocity() {
		double numerator = discharge * 8;
		double denominator = diameter * diameter
				* (fillAngle - Math.sin(fillAngle));
		return numerator / denominator;
	}

    /**
     * Main function of the class.
     * <p>
     * Call <strong>setPipe</strong> to setting all the fields and
     * implement the minimum slope check. Then it return the
     * <strong>pipe</strong> object with new value of diameter, end point
     * elevation and velocity.
     *
     * @param[in] pipe Object passed to the class necessary to fill all the
     * fields.
     * @param[out] pipe Object passed to the class necessary to return all the
     * evaluated fields of the entity.
     */

    public void evaluateDimension(Double discharge) {

        Double naturalSlope = computeNaturalSlope();
        evaluateDiameter(slope, maxAttemps);
        evaluateDelta(slope);
        setUp1 = terrain(j) - terrain(i) + delta + up2;




		/*setPipe(pipe);

		if (pipeSlope > minSlope) {
			diameter = computeDiameter(pipeSlope);
			minSlope = computeMinSlope();
			if (pipeSlope >= minSlope) {
				this.pipe.buildPipe(elevationEndPoint, diameter,
						fillCoefficient, computeVelocity());
			} else {
				this.pipe.buildPipe(computeElevationEndPoint(minSlope),
						diameter, fillCoefficient, computeVelocity());
			}
		} else {
			this.pipe.buildPipe(computeElevationEndPoint(minSlope), diameter,
					fillCoefficient, computeVelocity());
		}

		return this.pipe;*/
    }


    private Double evaluateDiameter(Double slope) {
	    computeDiameter(slope);
		Double diameter = diameterToCommercial();
		Double minSlope = computeMinSlope(diameter);
		if (slope < minSlope) {
			return evaluateDiameter(minSlope);
		}
		else {
			return diameter;
		}
	}

}