package spacegame.util;

import java.awt.geom.Point2D;
import java.lang.Math;

/**
 * An extension to the relatively impotent java.awt.geom.Point2D.Double,
 * Vector2D allows mathematical manipulation of 2-component vectors.
 * 
 */
public class Vector2D extends Point2D.Double {

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.geom.Point2D.Double#Point2D.Double()
     */
    public Vector2D() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.geom.Point2D.Double#Point2D.Double()
     */
    public Vector2D(double x, double y) {
        super(x, y);
    }

    /**
     * Copy constructor
     */
    public Vector2D(Vector2D v) {
        x = v.x;
        y = v.y;
    }

    /**
     * @return the radius (length, modulus) of the vector in polar coordinates
     */
    public double getR() {
        return Math.sqrt(x * x + y * y);
    }

    /**
     * @return the angle (argument) of the vector in polar coordinates in the
     *         range [-pi/2, pi/2]
     */
    public double getTheta() {
        return Math.atan2(y, x);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.geom.Point2D.Double#setLocation(double, double)
     */
    public void set(double x, double y) {
        super.setLocation(x, y);
    }

    /**
     * Sets the vector given polar arguments.
     * 
     * @param r
     *            The new radius
     * @param t
     *            The new angle, in radians
     */
    public void setPolar(double r, double t) {
        super.setLocation(r * Math.cos(t), r * Math.sin(t));
    }

    /** Sets the vector's radius, preserving its angle. */
    public void setR(double r) {
        double t = getTheta();
        setPolar(r, t);
    }

    /** Sets the vector's angle, preserving its radius. */
    public void setTheta(double t) {
        double r = getR();
        setPolar(r, t);
    }

    /** The sum of the vector and rhs */
    public Vector2D plus(Vector2D rhs) {
        return new Vector2D(x + rhs.x, y + rhs.y);
    }

    /** The difference of the vector and rhs: this - rhs */
    public Vector2D minus(Vector2D rhs) {
        return new Vector2D(x - rhs.x, y - rhs.y);
    }

    public boolean equals(Vector2D rhs) {
        return x == rhs.x && y == rhs.y;
    }

    /** Product of the vector and scalar */
    public Vector2D scalarMult(double scalar) {
        return new Vector2D(scalar * x, scalar * y);
    }
    
    /** Multiplies and sets this vector */
    public void scalarMultVoid(double scalar) {
        //return new Vector2D(scalar * x, scalar * y);
        this.x = this.x * scalar;
        this.y = this.y * scalar;
    }
    
    /** Divide a vector by a scalar */
    public Vector2D scalarDiv(double scalar){
    	return new Vector2D(x / scalar, y / scalar);
    }

    /** Dot product of the vector and rhs */
    public double dotProduct(Vector2D rhs) {
        return x * rhs.x + y * rhs.y;
    }

    /**
     * Since Vector2D works only in the x-y plane, (u x v) points directly along
     * the z axis. This function returns the value on the z axis that (u x v)
     * reaches.
     * 
     * @return signed magnitude of (this x rhs)
     */
    public double crossProduct(Vector2D rhs) {
        return x * rhs.y - y * rhs.x;
    }

    /** Product of components of the vector: compenentProduct( <x y>) = x*y. */
    public double componentProduct() {
        return x * y;
    }

    /** Componentwise product: <this.x*rhs.x, this.y*rhs.y> */
    public Vector2D componentwiseProduct(Vector2D rhs) {
        return new Vector2D(x * rhs.x, y * rhs.y);
    }

    /**
     * An alias for getR()
     * @return the length of this
     */
    public double length() {
        return getR();
    }
    
    /** Returns a perpendicular vector to this one. 
     * obtained by transposing the Cartesian components and taking the minus sign of one
     **/
    public Vector2D perp(){
    	return new Vector2D(y, x*(-1));
    }

    /**
     * Returns a new vector with the same direction as the vector but with
     * length 1, except in the case of zero vectors, which return a copy of
     * themselves.
     */
    public Vector2D unitVector() {
        if (getR() != 0) {
            return new Vector2D(x / getR(), y / getR());
        }
        return new Vector2D(0,0);
    }
    
    /**
     * changes this one
     */
    public void unitVectorVoid() {
        if (getR() != 0) {
            x = x / getR();
            y = y / getR();
        }
        x = 0;
        y = 0;
    }

    /** Polar version of the vector, with radius in x and angle in y */
    public Vector2D toPolar() {
        return new Vector2D(Math.sqrt(x * x + y * y), Math.atan2(y, x));
    }

    /** Rectangular version of the vector, assuming radius in x and angle in y*/
    public Vector2D toRect() {
        return new Vector2D(x * Math.cos(y), x * Math.sin(y));
    }

    /** @return Standard string representation of a vector: "<x, y>" */
    public String toString() {
        return "<" + x + ", " + y + ">";
    }
    
    /** truncates the vector to a limit. */
    public void truncate(double max){
    	double i;
    	double length = length();
    	if(length > .0001){
    		double test = 0;
    	}

    		i = max / length();
        	i = i < 1.0 ? i : 1.0;
        	scalarMultVoid(i);
    	
    }
    
    public Vector2D rotate(double radians)
    {
        double x = this.x * Math.cos(radians) - this.y * Math.sin(radians);
        double y = this.x * Math.sin(radians) + this.y * Math.cos(radians);
        this.x = x;
        this.y = y;
        return this;
    }
    
    public Vector2D rotate_static(double radians)
    {
        double x = this.x * Math.cos(radians) - this.y * Math.sin(radians);
        double y = this.x * Math.sin(radians) + this.y * Math.cos(radians);
        //this.x = x;
        //this.y = y;
        return new Vector2D(x, y);
    }
}