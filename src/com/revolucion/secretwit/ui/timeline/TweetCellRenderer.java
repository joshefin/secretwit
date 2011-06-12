/*******************************************************************************
 * Copyright (c) 2011 Željko Zirikoviæ.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.revolucion.secretwit.ui.timeline;

import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.plaf.ListUI;

import net.miginfocom.swing.MigLayout;
import twitter4j.Status;

import com.ocpsoft.pretty.time.PrettyTime;
import com.revolucion.secretwit.cache.ThumbnailCache;
import com.revolucion.secretwit.stego.StegoUtils;
import com.revolucion.secretwit.ui.components.MultilineLabel;
import com.revolucion.secretwit.utils.Colors;
import com.revolucion.secretwit.utils.Fonts;

public class TweetCellRenderer extends JPanel implements ListCellRenderer {
	
	private static final long serialVersionUID = 2918679043499281817L;
	
	private ThumbnailCache cache;

	private JLabel labelIcon;
	private JLabel labelDate;
	private JLabel labelUsername;
	private JLabel labelRealname;
	private MultilineLabel areaMessage;
	
	private JLabel labelSecret;

	public TweetCellRenderer(ThumbnailCache cache) {
		this.cache = cache;
		
		setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 8));
		setLayout(new MigLayout("", "[]10[grow]", "[]4[]2[]"));

		init();
	}

	private void init() {
		labelIcon = new JLabel();

		labelDate = new JLabel();
		labelDate.setFont(Fonts.TIMELINE_DATE);
		labelDate.setForeground(Colors.TIMELINE_DATE_FONT);

		labelUsername = new JLabel();
		labelUsername.setFont(Fonts.TIMELINE_USERNAME);
		labelUsername.setForeground(Colors.TIMELINE_USERNAME_FONT);

		labelRealname = new JLabel();
		labelRealname.setFont(Fonts.TIMELINE_REALNAME);
		labelRealname.setForeground(Colors.TIMELINE_REALNAME_FONT);
		
		areaMessage = new MultilineLabel();
		areaMessage.setFont(Fonts.TIMELINE_MESSAGE);
		areaMessage.setForeground(Colors.TIMELINE_MESSAGE_FONT);
		
		labelSecret = new JLabel();
		labelSecret.setFont(Fonts.TIMELINE_SECRET_MESSAGE);
		labelSecret.setForeground(Colors.TIMELINE_MESSAGE_FONT);
		labelSecret.setHorizontalAlignment(SwingConstants.CENTER);
		labelSecret.setOpaque(true);
		labelSecret.setBackground(new Color(255, 255, 255, 150));
		labelSecret.setVisible(false);

		add(labelIcon, "spany, aligny top");
		add(labelDate, "wrap");
		add(labelUsername, "split 2");
		add(labelRealname, "wrap");
		add(areaMessage, "span, growx");
		
		add(labelSecret, "pos 60 4 container.x2-22 container.y2-8", 0);
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		setComponentOrientation(list.getComponentOrientation());

		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		}
		else {
			// Check for rollover
			Color backgroundColor = list.getBackground();
			ListUI ui = list.getUI();
			if (ui instanceof TimelineListUI) {
				int rolledOverIndex = ((TimelineListUI) ui).getRolledOverIndex();
				if (rolledOverIndex != -1 && rolledOverIndex == index) {
					backgroundColor = Colors.TIMELINE_ROLLOVER;
				}
			}

			setBackground(backgroundColor);
			setForeground(list.getForeground());
		}

		setEnabled(list.isEnabled());
		setFont(list.getFont());

		Status status = (Status) value;
		if (status != null) {
			BufferedImage image = loadImage(status);
			
			labelIcon.setIcon(new ImageIcon(image));
			labelDate.setText(new PrettyTime().format(status.getCreatedAt()) + " via " + parseSource(status));
			labelUsername.setText(status.getUser().getScreenName());
			labelRealname.setText(status.getUser().getName());
			
			String statusText = status.getText();
			areaMessage.setText(statusText);
			
			if (StegoUtils.doesProfileImageHaveWatermark(image)) {
				String decodedStatus = StegoUtils.decodeTweet(statusText);
				if (decodedStatus != null && !decodedStatus.isEmpty()) {
					labelSecret.setText(decodedStatus);
					labelSecret.setVisible(true);
				}
				else
					labelSecret.setVisible(false);
			}
			else
				labelSecret.setVisible(false);
		}

		return this;
	}

	private String parseSource(Status status) {
		if (status == null || status.getSource() == null)
			return "";

		String source = status.getSource();

		int startIndex = source.indexOf('>');
		if (startIndex == -1)
			return source;

		int endIndex = source.indexOf('<', startIndex);
		if (endIndex == -1)
			return source;

		return source.substring(startIndex + 1, endIndex);
	}

	private BufferedImage loadImage(Status status) {
		if (status == null || status.getUser() == null || status.getUser().getProfileImageURL() == null)
			return null;

		URL url = status.getUser().getProfileImageURL();
		
		return cache.requestThumbnail(url);
	}

	@Override
	public void repaint() {}

	@Override
	public void repaint(int x, int y, int width, int height) {}

	@Override
	public void repaint(Rectangle r) {}

	@Override
	protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		if (propertyName == "text" || ((propertyName == "font" || propertyName == "foreground") && oldValue != newValue && getClientProperty(javax.swing.plaf.basic.BasicHTML.propertyKey) != null)) {
			super.firePropertyChange(propertyName, oldValue, newValue);
		}
	}

	@Override
	public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {}

	@Override
	public void firePropertyChange(String propertyName, byte oldValue, byte newValue) {}

	@Override
	public void firePropertyChange(String propertyName, char oldValue, char newValue) {}

	@Override
	public void firePropertyChange(String propertyName, double oldValue, double newValue) {}

	@Override
	public void firePropertyChange(String propertyName, float oldValue, float newValue) {}

	@Override
	public void firePropertyChange(String propertyName, int oldValue, int newValue) {}

	@Override
	public void firePropertyChange(String propertyName, long oldValue, long newValue) {}

	@Override
	public void firePropertyChange(String propertyName, short oldValue, short newValue) {}
}
