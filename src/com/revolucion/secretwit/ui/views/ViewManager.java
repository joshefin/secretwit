/*******************************************************************************
 * Copyright (c) 2011 Željko Zirikoviæ.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.revolucion.secretwit.ui.views;

public class ViewManager {

	private ViewPane viewPane;

	private ViewManager() {
		viewPane = new ViewPane();
	}

	private static class Singleton {
		private static final ViewManager INSTANCE = new ViewManager();
	}

	public static ViewManager getInstance() {
		return Singleton.INSTANCE;
	}

	public ViewPane getViewPane() {
		return viewPane;
	}

	public void showTimelineView() {
		viewPane.showTimelineView();
	}

	public void showSignupView() {
		viewPane.showSignupView();
	}

	public void addViewChangeListener(ViewChangeListener listener) {
		viewPane.setViewChangeListener(listener);
	}

}
