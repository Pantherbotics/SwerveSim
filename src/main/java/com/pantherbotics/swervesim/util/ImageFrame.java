package com.pantherbotics.swervesim.util;

import javax.swing.*;
import java.awt.*;

public class ImageFrame extends JFrame {
	private final ImagePanel jLabel;
	public ImageFrame(byte[] bytes) {
		setLayout(new GridLayout());
		jLabel = new ImagePanel();
		jLabel.setImage(bytes);
		add(jLabel);
	}

	public void updateImage(byte[] bytes) {
		jLabel.setImage(bytes);
		jLabel.paintComponent(getGraphics());
	}
}
