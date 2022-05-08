package com.pantherbotics.swervesim;

import com.pantherbotics.swervesim.util.G2DUtils;
import com.pantherbotics.swervesim.util.KeyListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import static com.pantherbotics.swervesim.util.G2DUtils.drawRect;
import static com.pantherbotics.swervesim.util.G2DUtils.toByteArray;
import static com.pantherbotics.swervesim.util.MathUtils.*;

@SuppressWarnings("SameParameterValue")
public class Main {
	//------------------Editable Variables---------------------
	private static final double shift = 0.05; //Shifts X, Y, and steer values by this amount to make it more fluid
	//---------------End of Editable Variables-----------------

	//Odometry and module variables
	private static double odoX = 0, odoY = 0, odoR = 0, lastMs = 0;
	private static SwerveModule wheel1, wheel2, wheel3, wheel4;

	//Define the screen size and scale (can decrease scale for higher fps but lower resolution)
	private static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private static final double screenScale = 0.5;

	//Size of the swerve box (in pixels)
	private static final int width = (int) ((screenSize.getHeight()* screenScale)/1.75);
	private static final int height = (int) ((screenSize.getHeight()* screenScale)/1.75);
	//Scalar for vectors
	private static final int vecScale = (int) ((screenSize.getWidth()* screenScale)/28);
	//From each module corner the wheel will be at this factor from the center to the corner
	private static final double startFactor = 0.9;

	/**
	 * Generate and return the byte array of the simulation image.
	 * @param X the X value of the joystick
	 * @param Y the Y value of the joystick
	 * @param steer the steering value of the joystick
	 * @return the byte array of the simulation image
	 */
	public static byte[] getImageBytes(double X, double Y, double steer) throws IOException {
		//Update the rotation of the swerve
		odoR += steer; odoR = odoR % 360;
		wheel1.setSwerveRot(odoR); wheel2.setSwerveRot(odoR); wheel3.setSwerveRot(odoR); wheel4.setSwerveRot(odoR);

		//Obtain joystick data and define the heading
		double joyHeading = (getHeading(X, Y));
		double heading = joyHeading - odoR;
		double speed = getJoystickSpeed(X, Y);

		//Define the steering vector components and the max vector length
		double xr = steer * Math.cos(Math.toRadians(45)); // /2D normally
		double yr = steer * Math.sin(Math.toRadians(45)); // /2D normally
		double speedMax = Math.sqrt(xr*xr + (1+Math.abs(yr))*(1+Math.abs(yr))); //The largest possible speed from vectors

		//Calculate the vectors for all wheels
		double x = getHeadingX(heading);
		double y = getHeadingY(heading);
		double X1 = x*speed + xr;
		double Y1 = y*speed + yr;
		double X2 = x*speed + xr;
		double Y2 = y*speed - yr;
		double X3 = x*speed - xr;
		double Y3 = y*speed - yr;
		double X4 = x*speed - xr;
		double Y4 = y*speed + yr;
		//From the wheel vectors, calculate the wheel speeds [-1, 1] and angles (deg)
		//We round here for convenience when displaying values, on a real swerve you'd want all the decimals
		double w1S = round(speedMax == 0 ? 0 : Math.sqrt(X1*X1+Y1*Y1)/speedMax, 3);
		double w2S = round(speedMax == 0 ? 0 : Math.sqrt(X2*X2+Y2*Y2)/speedMax, 3);
		double w3S = round(speedMax == 0 ? 0 : Math.sqrt(X3*X3+Y3*Y3)/speedMax, 3);
		double w4S = round(speedMax == 0 ? 0 : Math.sqrt(X4*X4+Y4*Y4)/speedMax, 3);
		double w1A = round(getHeading(X1, Y1), 3);
		double w2A = round(getHeading(X2, Y2), 3);
		double w3A = round(getHeading(X3, Y3), 3);
		double w4A = round(getHeading(X4, Y4), 3);

		//Create the buffered image and set a blank black background
		BufferedImage image = new BufferedImage((int)Math.floor(screenSize.getWidth()* screenScale), (int)Math.floor(screenSize.getHeight()* screenScale), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, image.getWidth(), image.getHeight());

		//Draw the swerve box
		g.setColor(Color.WHITE);
		drawRect(g, image.getWidth()/2, image.getHeight()/2, width, height, odoR);

		//Define the font based on the screen size
		int font = image.getWidth() / 65;
		g.setStroke(new BasicStroke((int)Math.ceil(font/7D)));
		g.setFont(new Font( "Courier New", Font.BOLD, font));

		//Draw the wheel vectors to the image
		wheel1.draw(g, w1A, w1S, font, startFactor, width, height, vecScale);
		wheel2.draw(g, w2A, w2S, font, startFactor, width, height, vecScale);
		wheel3.draw(g, w3A, w3S, font, startFactor, width, height, vecScale);
		wheel4.draw(g, w4A, w4S, font, startFactor, width, height, vecScale);

		//Calculate the odometry vector
		double oX = (X1 + X2 + X3 + X4) * 0.25;
		double oY = (Y1 + Y2 + Y3 + Y4) * 0.25;
		double oHeading = getHeading(oX, oY);
		double oSpeed = Math.sqrt(oX*oX + oY*oY);
		oX = getHeadingX(oHeading+odoR) * oSpeed;
		oY = getHeadingY(oHeading+odoR) * oSpeed;

		//Draw the odometry vector to the image
		int oScale = (int) Math.floor(image.getWidth()/10D);
		g.setColor(Color.CYAN);
		g.setStroke(new BasicStroke((float) (font/4D)));
		G2DUtils.drawArrow(g,
				image.getWidth()/2,
				image.getHeight()/2,
				image.getWidth()/2 + (int)(oX*oScale),
				image.getHeight()/2 - (int)(oY*oScale), font/2, font/2
		);

		//Display Odometry Vector Data
		g.setColor(Color.WHITE);
		String oStr = "Odometry Vector";
		g.drawString(oStr, image.getWidth()/2 - g.getFontMetrics().stringWidth(oStr)/2, image.getHeight()/2 + font + 10);
		String oStrA = "Angle: " + roundStr(getHeading(oX, oY), 3);
		g.drawString(oStrA, image.getWidth()/2 - g.getFontMetrics().stringWidth(oStrA)/2, image.getHeight()/2 + 2*font + 10);

		//Display Joystick Data
		font = image.getHeight()/22;
		g.setFont(new Font( "Courier New", Font.BOLD, font));
		g.drawString("XL (Drive X): " + roundStr(X, 3), 5, font-5);
		g.drawString("YL (Drive Y): " + roundStr(Y, 3), 5, 2*font-5);
		g.drawString("XR (Steer):   " + roundStr(steer, 3), 5, 3*font-5);
		g.drawString("Heading: " + roundStr(heading, 3), 5, image.getHeight()-10);
		//Add rotation to the screen
		g.drawString("Rotation:     " + roundStr(odoR, 1), 5, 4*font-5);

		//Calculate time (in ms) since the last frame
		if (lastMs == 0) {
			lastMs = System.currentTimeMillis();
		}
		double ms = System.currentTimeMillis() - lastMs;
		lastMs = System.currentTimeMillis();

		//Display current Odometry Data
		odoX += oY/1000D * ms;
		odoY += -oX/1000D * ms;
		String odo = "Odometry: " + roundStr(odoX, 3) + ", " + roundStr(odoY, 3);
		g.drawString(odo,  image.getWidth()-g.getFontMetrics().stringWidth(odo)-5, image.getHeight()-10);

		//Display rendering data (ms and fps)
		String delay = "ms: " + (int)Math.floor(ms);
		g.drawString(delay,  image.getWidth()-g.getFontMetrics().stringWidth(delay)-5, font-5);
		String fps = "fps: " + (int)Math.floor(1000D/ms);
		g.drawString(fps,  image.getWidth()-g.getFontMetrics().stringWidth(fps)-5, 2*font-5);

		//Display help text
		g.setColor(Color.CYAN);
		String exit = "Exit: ESC";
		g.drawString(exit,  image.getWidth()/2-g.getFontMetrics().stringWidth(exit)/2, font);

		//Dispose of the graphics to prevent memory leaks and return the image as it's byte array
		g.dispose();
		return toByteArray(image, "PNG");
	}

	private static ImageFrame gui = null;
	/**
	 * Initiates and updates the GUI.
	 * @param x the X value of the joystick
	 * @param y the Y value of the joystick
	 * @param steer the steering value of the joystick
	 */
	public static void run(double x, double y, double steer) {
		//Catch any exceptions throw during the image creation process
		try {
			//Get the current image as a byte array
			byte[] image = getImageBytes(x, y, steer);
			//Create the gui or update the existing one
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

	/**
	 * Starts the simulation, creates the SwerveModule objects, and listens to key events to simulate a joystick.
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		//Calculate the width and height of the image
		int w = (int) (screenSize.getWidth() * screenScale);
		int h = (int) (screenSize.getHeight() * screenScale);

		//Create the SwerveModule objects
		wheel1 = new SwerveModule(1, w, h);
		wheel2 = new SwerveModule(2, w, h);
		wheel3 = new SwerveModule(3, w, h);
		wheel4 = new SwerveModule(4, w, h);

		//Create the joystick component variables
		final double[] x = {0};
		final double[] y = { 0 };
		final double[] steer = { 0 };

		//Start listening to key events
		KeyListener forward = new KeyListener(KeyEvent.VK_W, KeyEvent.VK_UP);
		KeyListener left = new KeyListener(KeyEvent.VK_A, KeyEvent.VK_LEFT);
		KeyListener back = new KeyListener(KeyEvent.VK_S, KeyEvent.VK_DOWN);
		KeyListener right = new KeyListener(KeyEvent.VK_D, KeyEvent.VK_RIGHT);
		KeyListener qKey = new KeyListener(KeyEvent.VK_Q);
		KeyListener eKey = new KeyListener(KeyEvent.VK_E);
		KeyListener escKey = new KeyListener(KeyEvent.VK_ESCAPE);

		//Run the simulation every 16ms (~60fps)
		(new Timer()).schedule(new TimerTask() {
			@Override
			public void run() {
				//Update the joystick values based on key inputs
				if (escKey.isPressed()) { System.exit(0); }

				if (forward.isPressed()) { y[0] = Math.min(1, y[0] + shift); }
				if (back.isPressed()) { y[0] = Math.max(-1, y[0] - shift); }
				if (!forward.isPressed() && !back.isPressed()) { y[0] = approachZero(y[0], shift); }

				if (left.isPressed()) { x[0] = Math.max(-1, x[0] - shift); }
				if (right.isPressed()) { x[0] = Math.min(1, x[0] + shift); }
				if (!left.isPressed() && !right.isPressed()) { x[0] = approachZero(x[0], shift); }

				if (qKey.isPressed()) { steer[0] = Math.max(-1, steer[0] - shift); }
				if (eKey.isPressed()) { steer[0] = Math.min(1, steer[0] + shift); }
				if (!qKey.isPressed() && !eKey.isPressed()) { steer[0] = approachZero(steer[0], shift); }

				//Run the simulation
				Main.run(x[0], y[0], steer[0]);
			}
		}, 1000, 16);
	}
}
