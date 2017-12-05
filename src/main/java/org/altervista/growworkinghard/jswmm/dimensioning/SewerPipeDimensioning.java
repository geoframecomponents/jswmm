/*
 * GNU GPL v3 License
 *
 * Copyright 2015 AboutHydrology (Riccardo Rigon)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.blogspot.geoframe.sewerPipeDimensioning;

import it.blogspot.geoframe.utils.GEOconstants;
import it.blogspot.geoframe.utils.GEOgeometry;
import it.blogspot.geoframe.hydroGeoEntities.line.*;

/**
 * @mainpage On-line Documentation
 * 
 * @section Description Description
 * 			This component compute the pipe diameter of
 *          a sewer with the respect of minimum slope and minimum excavation.
 *          The class take as input a <strong>Pipe</strong> object and, after
 *          the computation, return an object of the same type with some values
 *          changed and some evaluated.
 * 
 * @subsection Units Units
 * 			   The units used in the component are:
 *             <ul>
 *             <li>discharge \f$[l/s]\f$
 *             <li>diameter \f$[m]\f$
 *             <li>fill angle \f$[rad]\f$
 *             <li>hydraulic radius \f$[m]\f$
 *             <li>slope \f$[-]\f$
 *             <li>Gauckler Strickler \f$[\frac{m^{1/3}}{s}]\f$
 *             </ul>
 * 
 * @section Implementation Implementation
 *          <ol>
 *          <li>The component evaluate the first attempt values of diameter and
 *          minimum slope due to shear stress fixed in prior to guarantee the
 *          auto-cleaning at the base of the pipe.
 * 
 *          <li>It evaluate the slope of the pipe with the hypothesis that the
 *          end point elevation is equal to the minimum excavation given.
 * 
 *          <li>With a comparison is evaluated if the slope computed in the last
 *          step is greater of the minimum.
 * 
 *          <ol type="A">
 *          <li>If this check is true the component compute the diameter with
 *          the slope due to set elevation end point and the minimum slope with
 *          that diameter.
 *          <ol type="a">
 *          <li>If the slope of the pipe with this diameter in greater than the
 *          minimum the method returns the pipe object filled with minimum
 *          excavation, diameter and related velocity.
 * 
 *          <li>If this check fail, so the slope should be equal to the minimum
 *          the method returns a pipe object with a new evaluated end elevation
 *          point, same diameter and related velocity.
 *          </ol>
 * 
 *          <li>Else set the diameter equal to the diameter evaluated at the
 *          first step.
 * 
 *          </ol>
 *          </ol>
 * 
 * @author ftt01, dallatorre.daniele@gmail.com
 * @author
 * @version 0.1
 * @date June 13, 2016
 * @copyright GNU Public License v3 GWH-2b4
 */
public class SewerPipeDimensioning {

	private static double gaucklerStricklerCoefficient;
	private static double fillCoefficient;
	private static double fillAngle;
	private static double discharge;
	private double diameter;
	private double minSlope;
	private double pipeSlope;
	private double elevationEndPoint;

	private Pipe pipe;

	/**
	 * @brief Default constructor.
	 */
	public SewerPipeDimensioning() {
	}

	/**
	 * @brief Setter of the class fields.
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
	 * @brief Setter of the pipe class fields.
	 */
	private void setFields() {
		gaucklerStricklerCoefficient = pipe.getGaucklerStricklerCoefficient();
		fillCoefficient = pipe.getFillCoefficient();
		discharge = pipe.getDischarge();
	}

	/**
	 * @brief Setter for the first attempt values.
	 */
	private void setFirstAttemptValues() {
		fillAngle = computeFillAngle();
		elevationEndPoint = pipe.getEndPoint().getTerrainElevation()
				- GEOconstants.MINIMUMEXCAVATION;
		pipeSlope = computePipeSlope();
		diameter = computeFixedDiameter();
		minSlope = computeMinSlope();
	}

	/**
	 * @brief Computation of <strong>fillAngle</strong>.
	 * 
	 *        Computation of <strong>fillAngle</strong> from \f[
	 *        G=\frac{1-cos(\theta/2)}{2} \f] where \f$ G \f$ is the fill
	 *        coefficient, \f$ \theta \f$ is the fill angle related to the fill
	 *        coefficient.
	 */
	private double computeFillAngle() {
		return 2 * Math.acos(1 - 2 * fillCoefficient);
	}

	/**
	 * @brief Computation of minimum slope.
	 * 
	 *        Evaluation of minimum slope due to fixed shear stress at the base
	 *        of the pipe with the relation \f[ i_{min}=\frac{\tau}{\gamma*R_h}
	 *        \f] where the \f$\tau\f$ is the shear stress, \f$\gamma\f$ is the
	 *        specific weight of water and \f$R_h\f$ the hydraulic radius.
	 * 
	 * @todo Build a method to use commercial pipe dimensions.
	 */
	private double computeMinSlope() {
		double hydraulicRadius = computeHydraulicRadius(diameter);

		return GEOconstants.SHEARSTRESS
				/ (GEOconstants.WSPECIFICWEIGHT * hydraulicRadius);
	}

	/**
	 * @brief Computation of diameter due to auto-cleaning.
	 * 
	 *        The diameter is related to minimum slope and is evaluated by the
	 *        relation \f[ D = {\left[ \frac{4^{^{13}/_6} Q}{\theta
	 *        {(1-\frac{sin(\theta)}{\theta})}^{^7/_6} K_s \sqrt{^\tau/_\gamma}}
	 *        \right]}^{^6/_{13}} \f]
	 */
	private double computeFixedDiameter() {
		final double pow1 = 13.0 / 6;
		double numerator = Math.pow(4, pow1);
		final double pow2 = 7.0 / 6;
		double denominator = fillAngle
				* Math.pow(1 - Math.sin(fillAngle) / fillAngle, pow2)
				* gaucklerStricklerCoefficient
				* Math.pow(GEOconstants.SHEARSTRESS
						/ GEOconstants.WSPECIFICWEIGHT, 0.5);
		final double pow3 = 6.0 / 13;

		return Math.pow(numerator / denominator, pow3);
	}

	/**
	 * @brief Computation of the hydraulic radius.
	 * 
	 *        Computation of the hydraulic radius from \f[ R_h =
	 *        D\frac{1-sin(\theta)/\theta)}{4} \f] where the \f$ \theta \f$ is
	 *        the fill angle.
	 * 
	 * @param [in] diameter Diameter of the pipe.
	 */
	private double computeHydraulicRadius(double diameter) {
		return diameter / 4 * (1 - Math.sin(fillAngle) / fillAngle);
	}

	/**
	 * @brief Computation of <strong>pipeSlope</strong>
	 * 
	 *        Evaluation of the slope of the pipe with the end elevation point
	 *        set by class field <strong>elevationEndPoint</strong>.
	 */
	private double computePipeSlope() {
		return GEOgeometry.computeSlope(pipe.getStartPoint().getX(), pipe
				.getStartPoint().getY(), pipe.getStartPoint().getElevation(),
				pipe.getEndPoint().getX(), pipe.getEndPoint().getY(),
				elevationEndPoint);
	}

	/**
	 * @brief Evaluation of diameter related to fixed slope.
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
	 * @brief Evaluation of elevation of end point due to a defined slope.
	 *
	 *        Computation of the elevation of the end point through the Pitagora
	 *        formula and use of <strong>GEOgeometry</strong> tool.
	 *
	 * @param [in] slope The slope of the pipe for that is necessary to evaluate
	 *        the end point elevation.
	 */
	private double computeElevationEndPoint(double slope) {
		return pipe.getStartPoint().getElevation()
				- slope
				* GEOgeometry.horizontalProjection(pipe.getStartPoint().getX(),
						pipe.getStartPoint().getY(), pipe.getEndPoint().getX(),
						pipe.getEndPoint().getY());
	}

	/**
	 * @brief Evaluation of velocity.
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
	 * @brief Main function of the class.
	 *
	 *        Call <strong>setPipe</strong> to setting all the fields and
	 *        implement the minimum slope check. Then it return the
	 *        <strong>pipe</strong> object with new value of diameter, end point
	 *        elevation and velocity.
	 * 
	 * @param [in] pipe Object passed to the class necessary to fill all the
	 *        fields.
	 * @param [out] pipe Object passed to the class necessary to return all the
	 *        evaluated fields of the entity.
	 */
	public Pipe run(final Pipe pipe) {
		setPipe(pipe);

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

		return this.pipe;
	}
}
