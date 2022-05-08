package com.pantherbotics.swervesim.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@SuppressWarnings("unused")
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

	/**
	 * Draw an arrow line between two points.
	 * @param g the graphics component.
	 * @param x1 x-position of first point.
	 * @param y1 y-position of first point.
	 * @param x2 x-position of second point.
	 * @param y2 y-position of second point.
	 * @param w the width of the arrow head.
	 * @param h the height of the arrow head.
	 */
	public static void drawArrow(Graphics2D g, int x1, int y1, int x2, int y2, int w, int h) {
		Point fromPt = new Point(x1, y1);
		Point toPt = new Point(x2, y2);

		int length = (int) fromPt.distance(toPt);
		int wid = (int) ((BasicStroke) g.getStroke()).getLineWidth();

		Polygon arrowPolygon = new Polygon();
		arrowPolygon.addPoint(-length, (int)Math.ceil(wid/2D));
		arrowPolygon.addPoint(0, (int)Math.ceil(wid/2D));
		arrowPolygon.addPoint(0,3*(int)Math.ceil(w/2D));
		arrowPolygon.addPoint(h,0);
		arrowPolygon.addPoint(0,-3*(int)Math.floor(w/2D));
		arrowPolygon.addPoint(0, -(int)Math.floor(wid/2D));
		arrowPolygon.addPoint(-length, -(int)Math.floor(wid/2D));

		double rotate = Math.atan2(toPt.y - fromPt.y, toPt.x - fromPt.x);

		AffineTransform transform = new AffineTransform();
		transform.translate(toPt.x, toPt.y);
		double ptDistance = fromPt.distance(toPt);
		double scale = ptDistance / (length+h); // 12 because it's the length of the arrow polygon.
		transform.scale(scale, scale);
		transform.rotate(rotate);

		g.fill(transform.createTransformedShape(arrowPolygon));
	}

	/**
	 * Draw an arrow line between two points.
	 * @param g the graphics component.
	 * @param x1 x-position of first point.
	 * @param y1 y-position of first point.
	 * @param speed the speed of the wheel vector.
	 * @param hX the heading X value from current heading angle.
	 * @param hY the heading Y value from current heading angle.
	 * @param scale the vector scale for the arrow.
	 * @param w the width of the arrow head.
	 * @param h the height of the arrow head.
	 */
	public static void drawArrow(Graphics2D g, int x1, int y1, double speed, double hX, double hY, int scale, int w, int h) {
		drawArrow(g, x1, y1,
				x1 + (int)Math.floor(hX * speed * scale),
				y1 - (int)Math.ceil(hY * speed * scale),
				w, h);
	}

	/**
	 * converts a BufferedImage to a byte[]
	 * @param image the BufferedImage to convert
	 * @param format the format to convert to Ex: JPEG, JPEG2000, RAW, TIFF, BMP, PNG
	 * @return the byte[] representation of the image
	 */
	public static byte[] toByteArray(BufferedImage image, String format) throws IOException {
		ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
		ImageIO.write(image, format, byteArrayOut);
		return byteArrayOut.toByteArray();
	}
}
