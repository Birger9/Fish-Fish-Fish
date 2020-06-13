package util;

import java.util.Collection;

/**
 * The Point2D class hold a pair of values, and supports various operators such as addition and multiplication
 */
public class Point2D {

    private double x, y;

    public Point2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point2D(Point2D other) {
        x = other.x;
        y = other.y;
    }

    public Point2D() {
    }

    /**
     * inverted method inverts a Point2D x- and y-value.
     * @param Nothing.
     * @return Point2D new inverted Point2D object
     */
    public Point2D inverted(){
        return new Point2D(-x, -y);
    }

    /**
     * sum method calculates the sum of two point2D objects
     * @param other The other point to add.
     * @return Point2D new Point2D with one Point2D x-value added with the other Point2D x-value.
     * Same with the y-value
     */
    public static Point2D sum(Point2D a, Point2D b) {
        return new Point2D(
        	a.x + b.x,
		a.y + b.y
	);
    }

    /**
     * difference method that calculates the difference of two Point2D objects.
     * @param Point2D two Point2D objects.
     * @return Point2D new Point2D with one Point2D x-value subtracted with the other Point2D x-value.
     * Same with the y-value
     */
    public static Point2D difference(Point2D a, Point2D b) {
	return new Point2D(
		a.x - b.x,
		a.y - b.y
	);
    }

    /**
     * product method that calculates the product of two Point2D objects.
     * @param Point2D two Point2D objects.
     * @return Point2D new Point2D with one Point2D x-value multiplied with the other Point2D x-value.
     * Same with the y-value
     */
    public static Point2D product(Point2D a, Point2D b) {
	return new Point2D(
		a.x * b.x,
		a.y * b.y
	);
    }

    /**
     * product method that calculates the product of a Point2D object with a value(scalar)
     * @param Point2D two Point2D objects.
     * @return Point2D new Point2D with one Point2D x-value multiplied with scalar value.
     * Same with the y-value
     */
    public static Point2D product(Point2D point, double scalar) {
        return new Point2D(
		point.x * scalar,
		point.y * scalar
	);
    }

    /**
     * distance method that calculates the distance between two Point2D objects.
     * @param a The first point object
     * @param b The other point object
     * @return Double, the distance to Point2D b
     */
    public static double distance(Point2D a, Point2D b) {
        double dx = a.x - b.x;
        double dy = a.y - b.y;
        return Math.sqrt(dx*dx + dy*dy);
    }

    /**
     * distanceTo method that calculates the distance between two Point2D objects.
     * @param other The other point.
     * @return Double, the distance to Point2D b
     */
    public double distanceTo(Point2D other) {
	double dx = x - other.x;
	double dy = y - other.y;
	return Math.sqrt(dx*dx + dy*dy);
    }

    /**
     * xyComponents method calculates the x and y components given the hypotenuse and an angle.
     * @param value The hypotenuse.
     * @param angle The angle in radians.
     * @return Point2D, the point containing the components (x, y)
     */
    public static Point2D xyComponents(double value, double angle) {
    	return new Point2D(
    		Math.cos(angle) * value, // x component
		Math.sin(angle) * value  // y component
	);
    }

    /**
     * listAverage method calculates the average point in a collection of Point2D.
     * @param list The collection of points.
     * @return Point2D, the point containing the average values
     */
    public static Point2D listAverage(Collection<Point2D> list){
	Point2D sum = new Point2D(0, 0);
	for(Point2D sample : list){
	    sum.addX(sample.x);
	    sum.addY(sample.y);
	}
	return new Point2D(
		sum.x / list.size(),
		sum.y / list.size()
	);
    }

    /**
     * clamp method constrains the values in a point between two points.
     * @param point The point to clamp.
     * @param min The minimum value.
     * @param max The maximum value.
     * @return Point2D, the point containing the clamped value
     */
    public static Point2D clamp(Point2D point, Point2D min, Point2D max) {
        point = Point2D.clampMin(point, min);
	point = Point2D.clampMax(point, max);
	return point;
    }

    /**
     * clampMin method constrains the minimum values of a point.
     * @param point The point to clamp.
     * @param min The minimum value.
     * @return Point2D, the point containing the clamped value
     */
    public static Point2D clampMin(Point2D point, Point2D min) {
        return new Point2D (Math.max(point.x, min.x),
			    Math.max(point.y, min.y)
	    );
    }

    /**
     * clampMax method constrains the maximum values of a point.
     * @param point The point to clamp.
     * @param max The maximum value.
     * @return Point2D, the point containing the clamped value.
     */
    public static Point2D clampMax(Point2D point, Point2D max) {
	return new Point2D (Math.min(point.x, max.x),
			    Math.min(point.y, max.y)
	);
    }

    @Override
    public String toString() {
	return x + ", " + y;
    }

    /**
     * add method adds the values of this point with another point.
     * @param other The point to add.
     * @return Nothing
     */
    public void add(Point2D other) {
        x += other.x;
        y += other.y;
    }

    public void addX(double increment) {
        x += increment;
    }

    public void addY(double increment) {
	y += increment;
    }

    public double getX() {
	return x;
    }

    public void setX(final double x) {
	this.x = x;
    }

    public double getY() {
	return y;
    }

    public void setY(final double y) {
	this.y = y;
    }
}
