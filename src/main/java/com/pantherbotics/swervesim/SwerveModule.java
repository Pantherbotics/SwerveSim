package com.pantherbotics.swervesim;

import com.pantherbotics.swervesim.util.G2DUtils;

import java.awt.*;
import java.text.DecimalFormat;

import static com.pantherbotics.swervesim.util.MathUtils.getHeadingX;
import static com.pantherbotics.swervesim.util.MathUtils.getHeadingY;

public class SwerveModule {
	public final int id, x, y;

	/**
	 * @param x X pixel position of the module
	 * @param y Y pixel position of the module
	 */
	public SwerveModule(int id, int x, int y) {
		this.id = id;
		this.x = x;
		this.y = y;
	}

	private final DecimalFormat df = new DecimalFormat("#0.000");
	private final DecimalFormat df2 = new DecimalFormat("#000.000");

	/**
	 * Draws the wheel vector given wheel data and screen data.
	 * @param g the Graphics2D object to draw with
	 * @param angle the angle of the wheel, in degrees
	 * @param speed the speed of the wheel [-1, 1]
	 * @param font the font size currently in use
	 * @param spacing the spacing between the wheel and the edge of the swerve
	 * @param vecScale the scalar for the wheel vector
	 */
	public void draw(Graphics2D g, double angle, double speed, int font, int spacing, int vecScale) {
		//Draw the Wheel Vector
		g.setColor(Color.BLUE);
		G2DUtils.drawArrow(g, x, y, speed, getHeadingX(angle), getHeadingY(angle), vecScale, 5, 8);

		g.setColor(Color.WHITE);

		//Write module information above/below the wheel
		String w1i1 = "Angle: " + df2.format(angle);
		String w1i2 = "Speed:   " + df.format(speed);
		if (id == 1 || id == 2) {
			g.drawString(w1i1, x-(g.getFontMetrics().stringWidth(w1i1)/2), y - font - spacing);
			g.drawString(w1i2, x-(g.getFontMetrics().stringWidth(w1i1)/2), y - 2*font - spacing);
		}else {
			g.drawString(w1i1, x-(g.getFontMetrics().stringWidth(w1i1)/2), y + 2*font + 10 + spacing);
			g.drawString(w1i2, x-(g.getFontMetrics().stringWidth(w1i1)/2), y + font + 10 + spacing);
		}
	}
}
