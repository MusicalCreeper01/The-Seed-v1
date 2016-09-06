/*
 * jNoiseLib [https://github.com/andrewgp/jLibNoise]
 * Original code from libnoise [https://github.com/andrewgp/jLibNoise]
 *
 * Copyright (C) 2003, 2004 Jason Bevins
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License (COPYING.txt) for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * The developer's email is jlbezigvins@gmzigail.com (for great email, take
 * off every 'zig'.)
 */
package jLibNoise.noise.model;

import jLibNoise.noise.module.Module;

/**
 * Model that defines the displacement of a line segment.
 * <p/>
 * This model returns an output value from a noise module given the
 * one-dimensional coordinate of an input value located on a line
 * segment, which can be used as displacements.
 * <p/>
 * This class is useful for creating:
 * - roads and rivers
 * - disaffected college students
 * <p/>
 * To generate an output value, pass an input value between 0.0 and 1.0
 * to the GetValue() method.  0.0 represents the start position of the
 * line segment and 1.0 represents the end position of the line segment.
 *
 * @source 'models/line.h/cpp'
 */
public class Line {

    // A flag that specifies whether the value is to be attenuated
    // (moved toward 0.0) as the ends of the line segment are approached.
    private boolean attenuate;
    // A pointer to the noise module used to generate the output values.
    private Module module;
    // @a x coordinate of the start of the line segment.
    private double x0;
    // @a x coordinate of the end of the line segment.
    private double x1;
    // @a y coordinate of the start of the line segment.
    private double y0;
    // @a y coordinate of the end of the line segment.
    private double y1;
    // @a z coordinate of the start of the line segment.
    private double z0;
    // @a z coordinate of the end of the line segment.
    private double z1;

    public Line() {
        attenuate = true;
        x0 = 0.0;
        x1 = 1.0;
        y0 = 0.0;
        y1 = 1.0;
        z0 = 0.0;
        z1 = 1.0;
    }

    /**
     * Constructor
     *
     * @param module The noise module that is used to generate the output values.
     */
    public Line(Module module) {
        this.module = module;
        attenuate = true;
        x0 = 0.0;
        x1 = 1.0;
        y0 = 0.0;
        y1 = 1.0;
        z0 = 0.0;
        z1 = 1.0;
    }

    /**
     * Returns a flag indicating whether the output value is to be
     * attenuated (moved toward 0.0) as the ends of the line segment are
     * approached by the input value.
     *
     * @return true if the value is to be attenuated
     */
    public boolean getAttenuate() {
        return attenuate;
    }

    /**
     * Returns the noise module that is used to generate the output values.
     *
     * @return A reference to the noise module.
     * @pre A noise module was passed to the SetModule() method.
     */
    public Module getModule() {
        return module;
    }


    /**
     * Returns the output value from the noise module given the
     * one-dimensional coordinate of the specified input value located
     * on the line segment.
     * <p/>
     * The output value is generated by the noise module passed to the
     * SetModule() method.  This value may be attenuated (moved toward
     * 0.0) as @a p approaches either end of the line segment; this is
     * the default behavior.
     * <p/>
     * If the value is not to be attenuated, @a p can safely range
     * outside the 0.0 to 1.0 range; the output value will be
     * extrapolated along the line that this segment is part of.
     *
     * @param p The distance along the line segment (ranges from 0.0 to 1.0)
     * @return The output value from the noise module.
     * @pre A noise module was passed to the SetModule() method.
     * @pre The start and end points of the line segment were specified.
     */
    public double getValue(double p) {
        assert (module != null);

        double x = (x1 - x0) * p + x0;
        double y = (y1 - y0) * p + y0;
        double z = (z1 - z0) * p + z0;
        double value = module.getValue(x, y, z);

        if (attenuate) {
            return p * (1.0 - p) * 4 * value;
        } else {
            return value;
        }
    }

    /**
     * Sets a flag indicating that the output value is to be attenuated
     * (moved toward 0.0) as the ends of the line segment are approached.
     *
     * @param attenuate A flag that specifies whether the output value is to be attenuated.
     */
    public void setAttenuate(boolean attenuate) {
        this.attenuate = attenuate;
    }

    /**
     * Sets the position ( @a x, @a y, @a z ) of the end of the line segment to choose values along.
     *
     * @param x x coordinate of the end position.
     * @param y y coordinate of the end position.
     * @param z z coordinate of the end position.
     */
    public void setEndPoint(double x, double y, double z) {
        x1 = x;
        y1 = y;
        z1 = z;
    }

    /**
     * Sets the noise module that is used to generate the output values.
     * <p/>
     * This noise module must exist for the lifetime of this object,
     * until you pass a new noise module to this method.
     *
     * @param module The noise module that is used to generate the output values.
     */
    public void setModule(Module module) {
        this.module = module;
    }

    /**
     * Sets the position ( @a x, @a y, @a z ) of the start of the line segment to choose values along.
     *
     * @param x x coordinate of the start position.
     * @param y y coordinate of the start position.
     * @param z z coordinate of the start position.
     */
    public void setStartPoint(double x, double y, double z) {
        x0 = x;
        y0 = y;
        z0 = z;
    }
}
