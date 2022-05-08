package com.pantherbotics.swervesim.util;

import java.awt.*;
import java.awt.event.KeyEvent;

public class KeyListener {
	private volatile boolean pressed = false;

	/**
	 * @return true if the key is pressed
	 */
	public boolean isPressed() {
		synchronized (KeyListener.class) {
			return pressed;
		}
	}

	/**
	 * Registers the key listener
	 * Utilize isPressed() to check if the key is pressed
	 * @param keyCode the key code(s) to listen for
	 */
	public KeyListener(int... keyCode) {
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(ke -> {
			synchronized (KeyListener.class) {
				switch (ke.getID()) {
					case KeyEvent.KEY_PRESSED:
						for (int key : keyCode) {
							if (ke.getKeyCode() == key) {
								pressed = true;
							}
						}
						break;

					case KeyEvent.KEY_RELEASED:
						for (int key : keyCode) {
							if (ke.getKeyCode() == key) {
								pressed = false;
							}
						}
						break;
				}
				return false;
			}
		});
	}
}
