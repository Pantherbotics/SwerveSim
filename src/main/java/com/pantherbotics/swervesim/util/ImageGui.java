package com.pantherbotics.swervesim.util;

import com.pantherbotics.swervesim.Main;

import javax.swing.*;
import java.awt.*;

public class ImageGui extends JFrame {
	private ImageIcon image;
	private final JLabel jLabel;
	public ImageGui(byte[] bytes) {
		setLayout(new BorderLayout());

		image = new ImageIcon(bytes);
		jLabel = new JLabel(image);
		add(jLabel, BorderLayout.SOUTH);
	}

	public void updateImage(byte[] bytes) {
		image = new ImageIcon(bytes);
		jLabel.setIcon(image);
	}
}

