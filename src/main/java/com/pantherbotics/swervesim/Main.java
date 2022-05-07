package com.pantherbotics.swervesim;

import com.pantherbotics.swervesim.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

public class Main {
	private static final boolean debug = false;

	private static double odoX = 0;
	private static double odoY = 0;
	private static final DecimalFormat df = new DecimalFormat("#0.000");
	private static final DecimalFormat df2 = new DecimalFormat("#000.000");
	public static byte[] getImageBytes(double X, double Y, double steer, double ms) throws IOException {
		double joyHeading = (getHeading(X, Y));
		//while (joyHeading > 360) { joyHeading -= 360; }
		//while (joyHeading < 0) { joyHeading += 360; }
		if (debug) { System.out.println("Heading: " + joyHeading); }

		double speed = (Math.sqrt(X*X + Y*Y)) / 1.41421356; // [-1, 1] of the speed of the left joystick

		double xr = steer * Math.cos(Math.toRadians(45)); // /2D normally
		double yr = steer * Math.sin(Math.toRadians(45)); // /2D normally
		double speedMax = Math.sqrt(xr*xr + (1+Math.abs(yr))*(1+Math.abs(yr))); //The largest possible speed from vectors

		double x = getHeadingX(joyHeading);
		double y = getHeadingY(joyHeading);
		double X1 = x*speed + xr;
		double Y1 = y*speed + yr;
		double X2 = x*speed + xr;
		double Y2 = y*speed - yr;
		double X3 = x*speed - xr;
		double Y3 = y*speed - yr;
		double X4 = x*speed - xr;
		double Y4 = y*speed + yr;
		double w1S = round(speedMax == 0 ? 0 : Math.sqrt(X1*X1+Y1*Y1)/speedMax);
		double w2S = round(speedMax == 0 ? 0 : Math.sqrt(X2*X2+Y2*Y2)/speedMax);
		double w3S = round(speedMax == 0 ? 0 : Math.sqrt(X3*X3+Y3*Y3)/speedMax);
		double w4S = round(speedMax == 0 ? 0 : Math.sqrt(X4*X4+Y4*Y4)/speedMax);
		double w1A = round(getHeading(X1, Y1));
		double w2A = round(getHeading(X2, Y2));
		double w3A = round(getHeading(X3, Y3));
		double w4A = round(getHeading(X4, Y4));

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		BufferedImage image = new BufferedImage((int)screenSize.getWidth()/2, (int)screenSize.getHeight()/2, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, image.getWidth(), image.getHeight());

		int width = (int) (image.getHeight()/1.75); int height = (int) (image.getHeight()/1.75);
		int vecScale = (int) (image.getWidth()/19.25); int spacing = image.getWidth()/18;
		g.setColor(Color.WHITE);
		g.fillRect(image.getWidth()/2 - width/2, image.getHeight()/2 - height/2, width, height);
		g.setColor(Color.BLACK);
		g.fillRect(image.getWidth()/2 - width/2+1, image.getHeight()/2 - height/2+1, width-2, height-2);

		int font = image.getWidth() / 65;
		g.setStroke(new BasicStroke((int)Math.ceil(font/7D)));
		g.setFont(new Font( "Courier New", Font.BOLD, font));

		WheelPos w1 = new WheelPos(image.getWidth()/2 - width/2 + spacing, image.getHeight()/2 - height/2 + spacing);
		WheelPos w2 = new WheelPos(image.getWidth()/2 + width/2 - spacing, image.getHeight()/2 - height/2 + spacing);
		WheelPos w3 = new WheelPos(image.getWidth()/2 + width/2 - spacing, image.getHeight()/2 + height/2 - spacing);
		WheelPos w4 = new WheelPos(image.getWidth()/2 - width/2 + spacing, image.getHeight()/2 + height/2 - spacing);

		//Wheel 1 Vector
		g.setColor(Color.BLUE);
		drawArrow(g, w1.x, w1.y, w1S, w1A, vecScale, 7, 10);
		//drawArrow(g, w1.x, w1.y, w1.x + (int)Math.floor(X1*vecScale), w1.y - (int)Math.floor(Y1*vecScale), 7, 10);
		g.setColor(Color.WHITE);
		String w1i1 = "Angle: " + df2.format(w1A);
		g.drawString(w1i1, w1.x-(g.getFontMetrics().stringWidth(w1i1)/2), w1.y - font - spacing);
		String w1i2 = "Speed:   " + df.format(w1S);
		g.drawString(w1i2, w1.x-(g.getFontMetrics().stringWidth(w1i1)/2), w1.y - 2*font - spacing);


		//Wheel 2 Vector
		g.setColor(Color.BLUE);
		drawArrow(g, w2.x, w2.y, w2S, w2A, vecScale, 7, 10);
		//drawArrow(g, w2.x, w2.y, w2.x + (int)Math.floor(X2*vecScale), w2.y - (int)Math.floor(Y2*vecScale), 7, 10);
		g.setColor(Color.WHITE);
		String w2i1 = "Angle: " + df2.format(w2A);
		g.drawString(w2i1, w2.x-(g.getFontMetrics().stringWidth(w2i1)/2), w2.y - font - spacing);
		String w2i2 = "Speed:   " + df.format(w2S);
		g.drawString(w2i2, w2.x-(g.getFontMetrics().stringWidth(w2i1)/2), w2.y - 2*font - spacing);


		//Wheel 3 Vector
		g.setColor(Color.BLUE);
		drawArrow(g, w3.x, w3.y, w3S, w3A, vecScale, 7, 10);
		//drawArrow(g, w3.x, w3.y, w3.x + (int)Math.floor(X3*vecScale), w3.y - (int)Math.floor(Y3*vecScale), 7, 10);
		g.setColor(Color.WHITE);
		String w3i1 = "Angle: " + df2.format(w3A);
		g.drawString(w3i1, w3.x-(g.getFontMetrics().stringWidth(w3i1)/2), w3.y + 2*font + 10 + spacing);
		String w3i2 = "Speed:   " + df.format(w3S);
		g.drawString(w3i2, w3.x-(g.getFontMetrics().stringWidth(w3i1)/2), w3.y + font + 10 + spacing);


		//Wheel 4 Vector
		g.setColor(Color.BLUE);
		drawArrow(g, w4.x, w4.y, w4S, w4A, vecScale, 7, 10);
		//drawArrow(g, w4.x, w4.y, w4.x + (int)Math.floor(X4*vecScale), w4.y - (int)Math.floor(Y4*vecScale), 7, 10);
		g.setColor(Color.WHITE);
		String w4i1 = "Angle: " + df2.format(w4A);
		g.drawString(w4i1, w4.x-(g.getFontMetrics().stringWidth(w4i1)/2), w4.y + 2*font + 10 + spacing);
		String w4i2 = "Speed:   " + df.format(w4S);
		g.drawString(w4i2, w4.x-(g.getFontMetrics().stringWidth(w4i1)/2), w4.y + font + 10 + spacing);


		double oX = (X1 + X2 + X3 + X4) * 0.25;
		double oY = (Y1 + Y2 + Y3 + Y4) * 0.25;
		//if (oX != 0 && Math.abs(oX) >= Math.abs(oY)) {
		//	oY = oY / Math.abs(oX); oX = oX / Math.abs(oX);
		//}else if (oY != 0 && Math.abs(oY) > Math.abs(oX)) {
		//	oX = oX / Math.abs(oY); oY = oY / Math.abs(oY);
		//}

		if (debug) { System.out.println("oX: " + oX + " oY: " + oY); }
		int oScale = (int) Math.floor(image.getWidth()/8D);
		g.setColor(Color.CYAN);
		g.setStroke(new BasicStroke((float) (font/4D)));
		drawArrow(g,
				image.getWidth()/2,
				image.getHeight()/2,
				image.getWidth()/2 + (int)(oX*oScale),
				image.getHeight()/2 - (int)(oY*oScale), font/2, font/2
		);

		g.setColor(Color.WHITE);
		String oStr = "Odometry Vector";
		g.drawString(oStr, image.getWidth()/2 - g.getFontMetrics().stringWidth(oStr)/2, image.getHeight()/2 + font + 10);
		String oStrA = "Angle: " + roundStr(getHeading(oX, oY));
		g.drawString(oStrA, image.getWidth()/2 - g.getFontMetrics().stringWidth(oStrA)/2, image.getHeight()/2 + 2*font + 10);

		font = image.getHeight()/22;
		g.setFont(new Font( "Courier New", Font.BOLD, font));
		g.drawString("XL (Drive X): " + roundStr(X), 5, font-5);
		g.drawString("YL (Drive Y): " + roundStr(Y), 5, 2*font-5);
		g.drawString("XR (Steer):   " + roundStr(steer), 5, 3*font-5);
		g.drawString("Heading: " + roundStr(joyHeading), 5, image.getHeight()-10);

		odoX += oY/1000D * ms;
		odoY += -oX/1000D * ms;
		String odo = "Odometry: " + roundStr(odoX) + ", " + roundStr(odoY);
		g.drawString(odo,  image.getWidth()-g.getFontMetrics().stringWidth(odo)-5, image.getHeight()-10);

		String delay = "ms: " + (int)Math.floor(ms);
		g.drawString(delay,  image.getWidth()-g.getFontMetrics().stringWidth(delay)-5, font-5);
		String fps = "fps: " + (int)Math.floor(1000D/ms);
		g.drawString(fps,  image.getWidth()-g.getFontMetrics().stringWidth(fps)-5, 2*font-5);

		g.setColor(Color.CYAN);
		String exit = "Exit: ESC";
		g.drawString(exit,  image.getWidth()/2-g.getFontMetrics().stringWidth(exit)/2, font);



		g.dispose();

		return toByteArray(image, "PNG");
		//return image;

		//ImageIO.write(image, "png", new File(imagePath));
		//System.out.println("Image Wrote to File");
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
	 * @param angle the angle of the wheel vector.
	 * @param scale the vector scale for the arrow.
	 * @param w the width of the arrow head.
	 * @param h the height of the arrow head.
	 */
	public static void drawArrow(Graphics2D g, int x1, int y1, double speed, double angle, int scale, int w, int h) {
		//System.out.println("Speed: " + speed + " Angle: " + angle);
		drawArrow(g, x1, y1,
				x1 + (int)Math.floor(getHeadingX(angle) * speed * scale),
				y1 - (int)Math.ceil(getHeadingY(angle) * speed * scale),
				w, h);
	}


	public static String roundStr(double value) {
		BigDecimal bd = new BigDecimal(Double.toString(value));
		bd = bd.setScale(3, RoundingMode.HALF_UP);

		return df.format(bd.doubleValue());
	}

	public static double round(double value) {
		BigDecimal bd = new BigDecimal(Double.toString(value));
		bd = bd.setScale(3, RoundingMode.HALF_UP);

		return bd.doubleValue();
	}

	//I spent like half an hour figuring this out, don't try to figure it out just appreciate the results :)
	//0 Degrees is straight forward, 90 degrees is to the right, 180 degrees is backwards, 270 degrees is to the left
	// Aka clockwise degrees and 0 is straight forward on the joystick :)
	private static double getHeading(double x, double y) {
		if (x == 0 && y == 0) { return 0; }

		double angle = (360 - ((Math.atan2(y, x)*180/Math.PI) + 180)) - 90;
		if (angle < 0) {
			angle = 270 + (90 - Math.abs(angle));
		}
		return angle;
	}

	//Used to re-obtain the X value from an angle using the custom getHeading()
	private static double getHeadingX(double angle) {
		//Ensure values are [0, 360)
		while (angle > 360) { angle -= 360; }
		while (angle < 0) { angle += 360; }

		if (angle >= 0 && angle <= 90) {
			return Math.cos(Math.toRadians(90 - angle));
		}else if (angle >= 90 && angle <= 270) {
			return Math.cos(-Math.toRadians(angle - 90));
		}else if (angle >= 270 && angle <= 360) {
			return -Math.cos(Math.toRadians(270 - angle));
		}
		return 0;
	}
	//Used to re-obtain the Y value from an angle using the custom getHeading()
	private static double getHeadingY(double angle) {
		//Ensure values are [0, 360)
		while (angle > 360) { angle -= 360; }
		while (angle < 0) { angle += 360; }

		if (angle >= 0 && angle <= 90) {
			return Math.sin(Math.toRadians(90 - angle));
		}else if (angle >= 90 && angle <= 270) {
			return Math.sin(-Math.toRadians(angle - 90));
		}else if (angle >= 270 && angle <= 360) {
			return -Math.sin(Math.toRadians(270 - angle));
		}
		return 0;
	}

	private static double lastMs = 0;
	private static ImageFrame gui = null;
	public static void run(double x, double y, double steer) {
		try {
			if (lastMs == 0) {
				lastMs = System.currentTimeMillis(); return;
			}
			double change = System.currentTimeMillis() - lastMs;
			lastMs = System.currentTimeMillis();

			byte[] image = getImageBytes(x, y, steer, change);
			if (gui == null) {
				gui = new ImageFrame(image);
				gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				gui.setExtendedState(JFrame.MAXIMIZED_BOTH);
				gui.setUndecorated(true);
				gui.setVisible(true);
			}else {
				gui.updateImage(image);
			}
		}catch (IOException e) {
			e.printStackTrace();
		}catch (NullPointerException ignored) {}
	}

	private static double approachZero(double value) {
		if (value >= 0) {
			return Math.max(0, value - joyInterval);
		}else if (value < 0) {
			return Math.min(0, value + joyInterval);
		}
		return 0;
	}

	// convert BufferedImage to byte[]
	public static byte[] toByteArray(BufferedImage bi, String format) throws IOException {
		ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
		ImageIO.write(bi, format, byteArrayOut);
		return byteArrayOut.toByteArray();
	}

	private static final double joyInterval = 0.1;
	public static void main(String[] args) {
		final double[] x = {0};
		final double[] y = { 0 };
		final double[] steer = { 0 };

		KeyListener forward = new KeyListener(KeyEvent.VK_W, KeyEvent.VK_UP);
		KeyListener left = new KeyListener(KeyEvent.VK_A, KeyEvent.VK_LEFT);
		KeyListener back = new KeyListener(KeyEvent.VK_S, KeyEvent.VK_DOWN);
		KeyListener right = new KeyListener(KeyEvent.VK_D, KeyEvent.VK_RIGHT);
		KeyListener qKey = new KeyListener(KeyEvent.VK_Q);
		KeyListener eKey = new KeyListener(KeyEvent.VK_E);
		KeyListener escKey = new KeyListener(KeyEvent.VK_ESCAPE);

		(new Timer()).schedule(new TimerTask() {
			@Override
			public void run() {
				if (escKey.isPressed()) { System.exit(0); }

				if (forward.isPressed()) { y[0] = Math.min(1, y[0] + joyInterval); }
				if (back.isPressed()) { y[0] = Math.max(-1, y[0] - joyInterval); }
				if (!forward.isPressed() && !back.isPressed()) { y[0] = approachZero(y[0]); }

				if (left.isPressed()) { x[0] = Math.max(-1, x[0] - joyInterval); }
				if (right.isPressed()) { x[0] = Math.min(1, x[0] + joyInterval); }
				if (!left.isPressed() && !right.isPressed()) { x[0] = approachZero(x[0]); }

				if (qKey.isPressed()) { steer[0] = Math.max(-1, steer[0] - joyInterval); }
				if (eKey.isPressed()) { steer[0] = Math.min(1, steer[0] + joyInterval); }
				if (!qKey.isPressed() && !eKey.isPressed()) { steer[0] = approachZero(steer[0]); }

				Main.run(x[0], y[0], steer[0]);
			}
		}, 1000, 16);
	}
}
