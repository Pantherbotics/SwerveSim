package com.pantherbotics.swervesim.util;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public class G2DUtils {

	/**
	 * Draws a rectangle with a given rotation
	 * @param g the Graphics2D object to draw with
	 * @param x the x coordinate of the CENTER of the rectangle
	 * @param y the y coordinate of the CENTER of the rectangle
	 * @param width the width of the rectangle
	 * @param height the height of the rectangle
	 * @param rotation the rotation of the rectangle, in degrees (positive clockwise)
	 */
	public static void drawRect(Graphics2D g, int x, int y, int width, int height, double rotation) {
		double theta = Math.toRadians(rotation);

		// create rect centred on the point we want to rotate it about
		Rectangle2D rect = new Rectangle2D.Double(-width/2D, -height/2D, width, height);

		AffineTransform transform = new AffineTransform();
		transform.translate(x, y);
		transform.rotate(theta);

		// it's been while, you might have to perform the rotation and translate in the
		// opposite order

		Shape rotatedRect = transform.createTransformedShape(rect);

		g.draw(rotatedRect);
	}
}
