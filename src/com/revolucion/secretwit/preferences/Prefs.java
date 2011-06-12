/*******************************************************************************
 * Copyright (c) 2011 Željko Zirikoviæ.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.revolucion.secretwit.preferences;

import java.util.prefs.Preferences;

public class Prefs {

	private Preferences preferences;

	private Prefs() {
		preferences = Preferences.userRoot();
	}

	private static class Singleton {
		private static final Prefs INSTANCE = new Prefs();
	}

	public static Prefs getInstance() {
		return Singleton.INSTANCE;
	}

	public String get(String key) {
		return get(key, null);
	}

	public String get(String key, String defaultValue) {
		if (key == null)
			return null;

		return preferences.get(key, defaultValue);
	}

	public void set(String key, String value) {
		if (key != null)
			preferences.put(key, value);
	}
}
