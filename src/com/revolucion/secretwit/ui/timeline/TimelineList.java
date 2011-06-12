/*******************************************************************************
 * Copyright (c) 2011 Željko Zirikoviæ.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.revolucion.secretwit.ui.timeline;

import java.awt.Rectangle;

import javax.swing.JList;
import javax.swing.ListModel;

/**
 * JList extension with smooth scrolling.
 * 
 * @author c0d3R
 * 
 */
public class TimelineList extends JList {

	private static final long serialVersionUID = 6864402413529498644L;

	public TimelineList() {
		super();
	}

	public TimelineList(ListModel dataModel) {
		super(dataModel);
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 6;
	}

}
