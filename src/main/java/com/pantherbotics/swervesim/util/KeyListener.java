package com.pantherbotics.swervesim.util;

import java.awt.*;
import java.awt.event.KeyEvent;

public class KeyListener {
	private volatile boolean pressed = false;
	public boolean isPressed() {
		synchronized (KeyListener.class) {
			return pressed;
		}
	}

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
