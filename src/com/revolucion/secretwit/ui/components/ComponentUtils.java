/*******************************************************************************
 * Copyright (c) 2011 Željko Zirikoviæ.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.revolucion.secretwit.ui.components;

import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class ComponentUtils {

	private ComponentUtils() {}

	public static JButton createSmallButton() {
		JButton button = new JButton();
		button.setBorderPainted(false);
		button.setBorder(new EmptyBorder(0, 1, 0, 1));
		button.setContentAreaFilled(false);
		button.setFocusPainted(false);
		button.setRolloverEnabled(true);
		button.setHorizontalAlignment(SwingConstants.CENTER);

		return button;
	}
}
