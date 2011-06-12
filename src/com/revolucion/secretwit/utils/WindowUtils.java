/*******************************************************************************
 * Copyright (c) 2011 Željko Zirikoviæ.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.revolucion.secretwit.utils;

import java.awt.Component;
import java.awt.Window;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class WindowUtils {

	private WindowUtils() {}

	public static boolean isParentWindowFocused(Component component) {
		Window window = SwingUtilities.getWindowAncestor(component);
		return window != null && window.isFocused();
	}

	public static JFrame getFrameForComponent(Component component) {
		return (JFrame) JOptionPane.getFrameForComponent(component);
	}
}
