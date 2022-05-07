package com.pantherbotics.swervesim.util;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImagePanel extends JPanel {

	Image image;

	public void setImage(byte[] bytes) {
		//image = Toolkit.getDefaultToolkit().createImage(bytes);
		this.image = new ImageIcon(bytes).getImage();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), null);
	}
}
