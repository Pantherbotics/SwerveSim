package com.pantherbotics.swervesim.util;

import com.pantherbotics.swervesim.Main;

import javax.swing.*;
import java.awt.*;

public class ImageGui extends JFrame {
	private ImageIcon image;
	private final JLabel jLabel;
	private final JLabel odometer;
	public ImageGui(byte[] bytes) {
		setLayout(new BorderLayout());

		odometer = new JLabel("");
		odometer.setForeground(Color.BLACK);
		odometer.setFont(new Font("SansSerif", Font.BOLD, 32));
		add(odometer, BorderLayout.NORTH);

		image = new ImageIcon(bytes);
		jLabel = new JLabel(image);
		//jLabel.setPreferredSize(new Dimension(image.getIconWidth(), image.getIconHeight()));
		add(jLabel, BorderLayout.SOUTH);
	}

	public void updateImage(byte[] bytes) {
		image = new ImageIcon(bytes);
		jLabel.setIcon(image);
	}

	public void updateOdometer(double x, double y) {
		odometer.setText("X: " + Main.round(x) + " Y: " + Main.round(y));
	}
}
