/*******************************************************************************
 * Copyright (c) 2011 Željko Zirikoviæ.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.revolucion.secretwit.utils;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.net.URI;
import java.security.AccessController;
import java.security.PrivilegedAction;

import javax.swing.UIManager;

public class SystemUtils {

	private static boolean isWindowsXP = false;
	private static boolean isWindowsVista = false;
	private static boolean isWindows7 = false;
	private static boolean isMacOS = false;
	private static boolean isWindows = false;

	static {
		final String osName = System.getProperty("os.name", "Windows XP");
		if (osName.contains("Windows")) {
			isWindows = true;
			if (osName.contains("Windows XP"))
				isWindowsXP = true;
			else if (osName.contains("Windows Vista"))
				isWindowsVista = true;
			else if (osName.contains("Windows 7"))
				isWindows7 = true;
		}
		else if (osName.contains("Mac"))
			isMacOS = true;
	}

	public static JavaVersion getJavaVersion() {
		return JavaVersion.parseFromString(System.getProperty("java.version", "1.5"));
		// return Double.parseDouble(System.getProperty("java.version",
		// "1.5").substring(0, 3));
	}

	public static boolean isWindows() {
		return isWindows;
	}

	public static boolean isWindowsXP() {
		return isWindowsXP;
	}

	public static boolean isWindowsVista() {
		return isWindowsVista;
	}

	public static boolean isWindows7() {
		return isWindows7;
	}

	public static boolean isMacOS() {
		return isMacOS;
	}

	public static boolean isGtk() {
		return UIManager.getLookAndFeel().getClass().getName().contains("GTK");
	}

	public static boolean isKDE() {
		String value = "";
		try {
			value = System.getenv("KDE_FULL_SESSION");
		}
		catch (Throwable t) {}

		return "true".equals(value);
	}

	public static boolean isGnome() {
		String desktop = "";

		try {
			desktop = AccessController.doPrivileged(new PrivilegedAction<String>() {
				public String run() {
					return System.getProperty("sun.desktop");
				}
			});
		}
		catch (Throwable t) {}

		return "gnome".equals(desktop);
	}

	public static Dimension getScreenSize() {
		return Toolkit.getDefaultToolkit().getScreenSize();
	}

	public static void openWebSite(String url) {
		if (url != null && !url.isEmpty() && Desktop.isDesktopSupported()) {
			try {
				Desktop.getDesktop().browse(new URI(url));
			}
			catch (Exception e) {}
		}
	}

	public static void openMail(String mailto) {
		if (mailto != null && !mailto.isEmpty() && Desktop.isDesktopSupported()) {
			try {
				Desktop.getDesktop().mail(new URI(mailto));
			}
			catch (Exception e) {}
		}
	}

	public static void openVideo(String path) {
		if (path != null && !path.isEmpty() && Desktop.isDesktopSupported()) {
			File file = new File(path);
			if (file != null && file.exists()) {
				try {
					Desktop.getDesktop().open(file);
				}
				catch (Exception e) {}
			}
		}
	}

	public static class JavaVersion implements Comparable<JavaVersion> {
		private double version;
		private int update;

		public JavaVersion(double version, int update) {
			this.version = version;
			this.update = update;
		}

		public double getVersion() {
			return version;
		}

		public int getUpdate() {
			return update;
		}

		@Override
		public int compareTo(JavaVersion v) {
			int versionCompare = Double.compare(getVersion(), v.getVersion());
			if (versionCompare != 0)
				return versionCompare;

			return Integer.valueOf(getUpdate()).compareTo(v.getUpdate());
		}

		public static JavaVersion parseFromString(String versionString) {
			if (versionString == null || versionString.isEmpty() || !versionString.contains("."))
				return new JavaVersion(1.6, 20);

			double version;
			try {
				version = Double.parseDouble(versionString.substring(0, 3));
			}
			catch (NumberFormatException e) {
				version = 1.6;
			}

			int update = 20;
			int updateIndex = versionString.lastIndexOf('_');
			if (updateIndex != -1) {
				try {
					update = Integer.parseInt(versionString.substring(updateIndex + 1));
				}
				catch (NumberFormatException e) {
					update = 20;
				}
			}

			return new JavaVersion(version, update);
		}

	}

}
