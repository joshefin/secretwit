/*******************************************************************************
 * Copyright (c) 2011 Željko Zirikoviæ.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.revolucion.secretwit.ui.components;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicProgressBarUI;

public class MinimalisticProgressBarUI extends BasicProgressBarUI {

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);

		progressBar.setOpaque(false);
		progressBar.setBorderPainted(false);
		progressBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		
		UIManager.put("ProgressBar.repaintInterval", 30);
	}
	
}
