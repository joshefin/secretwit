/*******************************************************************************
 * Copyright (c) 2011 Željko Zirikoviæ.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.revolucion.secretwit.utils;

import java.awt.Font;

import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

public class Fonts {

	/*
	 * Ovako bi trebalo biti ...
	 * 
	 * Windows XP - Tahoma 11 
	 * Windows Vista - Segoe UI 12 
	 * Mac OS X - Lucida
	 * Grande 13 Gnome - DejaVu 10
	 */

	private static final String WINDOWS_XP_FONTNAME = "Tahoma";
	private static final int WINDOWS_XP_FONTSIZE = 11;
	private static final String WINDOWS_VISTA_FONTNAME = "Segoe UI";
	private static final int WINDOWS_VISTA_FONTSIZE = 12;

	public static final Font DEFAULT;

	public static Font HEADER_TITLE;
	public static Font HEADER_PLACE;
	public static Font HEADER_USER;
	
	public static Font MESSAGE;
	public static Font MESSAGE_CHAR_COUNT;
	
	public static Font TIMELINE_DATE;
	public static Font TIMELINE_USERNAME;
	public static Font TIMELINE_REALNAME;
	public static Font TIMELINE_MESSAGE;
	public static Font TIMELINE_SECRET_MESSAGE;
	
	public static Font SIGNUP_NUMBER;
	
	public static Font LOADING_PROGRESS;

	private Fonts() {}

	static {
		if (SystemUtils.isWindowsVista() || SystemUtils.isWindows7())
			DEFAULT = new Font(WINDOWS_VISTA_FONTNAME, Font.PLAIN, WINDOWS_VISTA_FONTSIZE);
		else if (SystemUtils.isWindowsXP())
			DEFAULT = new Font(WINDOWS_XP_FONTNAME, Font.PLAIN, WINDOWS_XP_FONTSIZE);
		else if (SystemUtils.isGnome())
			DEFAULT = new Font("DejaVu Sans", Font.PLAIN, 10);
		else if (SystemUtils.isKDE())
			DEFAULT = new Font("SansSerif", Font.PLAIN, 10);
		else
			DEFAULT = new Font("Dialog", Font.PLAIN, 12);

		initFontSet();

		initDefaultFonts();
	}

	private static void initFontSet() {
		HEADER_TITLE = deriveFromDefault(Font.BOLD, -1);
		HEADER_PLACE = deriveFromDefault(Font.PLAIN, 12);
		HEADER_USER = deriveFromDefault(Font.BOLD, 0);
		MESSAGE = deriveFromDefault(Font.PLAIN, 2);
		MESSAGE_CHAR_COUNT = DEFAULT.deriveFont(140f);
		TIMELINE_DATE = deriveFromDefault(Font.PLAIN, -2);
		TIMELINE_USERNAME = deriveFromDefault(Font.BOLD, 1);
		TIMELINE_REALNAME = deriveFromDefault(Font.PLAIN, 1);
		TIMELINE_MESSAGE = deriveFromDefault(Font.PLAIN, 1);
		TIMELINE_SECRET_MESSAGE = deriveFromDefault(Font.PLAIN, 12);
		SIGNUP_NUMBER = deriveFromDefault(Font.BOLD, 3);
		LOADING_PROGRESS = deriveFromDefault(Font.PLAIN, 4);
	}

	private static Font deriveFromDefault(int style, int size) {
		return DEFAULT.deriveFont(style, (float) (DEFAULT.getSize() + size));
	}

	private static void initDefaultFonts() {
		UIManager.put("Label.font", new FontUIResource(Fonts.DEFAULT));
		UIManager.put("Button.font", new FontUIResource(Fonts.DEFAULT));
		UIManager.put("TextField.font", new FontUIResource(Fonts.DEFAULT));
		UIManager.put("Tree.font", Fonts.DEFAULT);
		UIManager.put("List.font", Fonts.DEFAULT);
		UIManager.put("PasswordField.font", Fonts.DEFAULT);
		UIManager.put("TextArea.font", Fonts.DEFAULT);

		// Object defaultGtkFontName =
		// Toolkit.getDefaultToolkit().getDesktopProperty("gnome.Gtk/FontName");
		// Object defaultWindowsGuiFont =
		// Toolkit.getDefaultToolkit().getDesktopProperty("win.defaultGUI.font");
		// Object defaultWindowsIconFont =
		// Toolkit.getDefaultToolkit().getDesktopProperty("win.icon.font");

		// Enumeration<Object> en = UIManager.getDefaults().keys();
		// while (en.hasMoreElements()) {
		// Object key = en.nextElement();
		// Object value = UIManager.getDefaults().getFont(key);
		//
		// if (value != null) {
		// System.out.println(key.toString() + "\t\t" + value.toString());
		// FontUIResource oldFUIR = (FontUIResource)value;
		// FontUIResource newFUIR = new FontUIResource(WINDOWS_VISTA_FONTNAME,
		// oldFUIR.getStyle(), oldFUIR.getSize());
		// UIManager.put(key, newFUIR);
		// }
		// }
	}
}
