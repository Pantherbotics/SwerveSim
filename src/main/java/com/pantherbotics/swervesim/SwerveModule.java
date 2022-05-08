package com.pantherbotics.swervesim;

import com.pantherbotics.swervesim.util.MathUtils;

import java.awt.*;
import java.text.DecimalFormat;

import static com.pantherbotics.swervesim.util.G2DUtils.drawArrow;

public class SwerveModule {
	public final int id, w, h;
	public double swerveRot = 0;

	private final DecimalFormat df = new DecimalFormat("#0.000");
	private final DecimalFormat df2 = new DecimalFormat("#000.000");

	/**
	 * @param id the module's ID
	 * @param w the width of the image (in pixels)
	 * @param h the height of the image (in pixels)
	 */
	public SwerveModule(int id, int w, int h) {
		this.id = id;
		this.w = w;
		this.h = h;
	}

	/**
	 * Update the stored value for the rotation of the swerve.
	 * @param swerveRot the new rotation value
	 */
	public void setSwerveRot(double swerveRot) {
		this.swerveRot = swerveRot;
	}


	/**
	 * Draws the wheel vector given wheel data and screen data.
	 * @param g the Graphics2D object to draw with
	 * @param angle the angle of the wheel, in degrees
	 * @param speed the speed of the wheel [-1, 1]
	 * @param font the font size currently in use
	 * @param startFactor the factor [0, 1] to start the vector at, when scaling towards the center
	 * @param width the width of the swerve box (in pixels)
	 * @param height the height of the swerve box (in pixels)
	 * @param vecScale the scalar for the wheel vector
	 */
	public void draw(Graphics2D g, double angle, double speed, int font, double startFactor, double width, double height, int vecScale) {
		//Draw the Wheel Vector
		g.setColor(Color.BLUE);

		int x = (int) Math.floor(getSwerveCornerX() * startFactor * width/2);
		int y = (int) -Math.floor(getSwerveCornerY() * startFactor * height/2);

		drawArrow(g, w/2 + x, h/2 + y, speed, angle + swerveRot, vecScale, 5, 8);


		g.setColor(Color.WHITE);

		double textScale = 1.75;
		g.drawString(String.valueOf(id), (int) ((x*textScale) + w/2), (int) ((y*textScale) + h/2));

		//Write module information above/below the wheel
		String w1i1 = id + " Speed: " + df.format(speed);
		String w1i2 = "  Angle: " + df2.format(angle);
		g.drawString(w1i1, w-(g.getFontMetrics().stringWidth(w1i2)), h/2 - 2*font + 2*id*font - font + 10*(id-1));
		g.drawString(w1i2, w-(g.getFontMetrics().stringWidth(w1i2)), h/2 - 2*font + 2*id*font + 10*(id-1));
	}

	private double getSwerveCornerX() {
		if (id == 1) {
			return MathUtils.getHeadingX(-45 + swerveRot);
		}else if (id == 2) {
			return MathUtils.getHeadingX(45 + swerveRot);
		}else if (id == 3) {
			return MathUtils.getHeadingX(135 + swerveRot);
		}else if (id == 4) {
			return MathUtils.getHeadingX(-135 + swerveRot);
		}
		return 0;
	}

	private double getSwerveCornerY() {
		if (id == 1) {
			return MathUtils.getHeadingY(-45 + swerveRot);
		}else if (id == 2) {
			return MathUtils.getHeadingY(45 + swerveRot);
		}else if (id == 3) {
			return MathUtils.getHeadingY(135 + swerveRot);
		}else if (id == 4) {
			return MathUtils.getHeadingY(-135 + swerveRot);
		}
		return 0;
	}
}
