package com.pantherbotics.swervesim;

import javax.swing.*;
import java.awt.*;

public class ImagePanel extends JPanel {
	private Image image;

	public void setImage(byte[] bytes) {
		this.image = new ImageIcon(bytes).getImage();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), null);
	}
}
