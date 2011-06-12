/*******************************************************************************
 * Copyright (c) 2011 Zeljko Zirikovic.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GPL which 
 * accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.revolucion.secretwit.ui.timeline;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Window;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.ResponseList;
import twitter4j.Status;

import com.revolucion.secretwit.cache.ThumbnailCache;
import com.revolucion.secretwit.cache.ThumbnailCacheListener;
import com.revolucion.secretwit.twitter.TwitterClient;
import com.revolucion.secretwit.ui.HeaderPane;
import com.revolucion.secretwit.ui.components.MinimalisticScrollBarUI;
import com.revolucion.secretwit.utils.Colors;

public class TimelinePane extends JPanel {

	private static final long serialVersionUID = -2052989703862994444L;

	private final Logger logger = LoggerFactory.getLogger(TimelinePane.class);

	private JList listTimeline;
	private JScrollPane scrollPaneTimeline;
	private TimelineListModel<Status> listTimelineModel;

	private TimelinePane() {
		setLayout(new BorderLayout());
		setOpaque(false);

		init();

		// loadTweets();
	}
	
	private static class Singleton {
		private static final TimelinePane INSTANCE = new TimelinePane();
	}

	public static TimelinePane getInstance() {
		return Singleton.INSTANCE;
	}
	
	public void addStatus(Status status) {
		listTimelineModel.add(status);
	}

	public void reload() {
		new SwingWorker<ResponseList<Status>, Void>() {
			@Override
			protected ResponseList<Status> doInBackground() throws Exception {
				logger.debug("Reloading statuses.");
				return TwitterClient.getInstance().getTimeline();
			}

			@Override
			protected void done() {
				try {
					listTimelineModel.clear();
					
					final ResponseList<Status> statuses = get();
					if (statuses != null && !statuses.isEmpty()) {
						logger.debug("Loaded {} tweets.", statuses.size());
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								listTimelineModel.addAll(statuses);
							}
						});
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.execute();
	}

	private void init() {
		listTimelineModel = new TimelineListModel<Status>(100);

		listTimeline = new TimelineList(listTimelineModel);
		listTimeline.setUI(new TimelineListUI());
		// listTimeline.setOpaque(false);
		listTimeline.setBackground(Color.white);
		listTimeline.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
		listTimeline.setLayoutOrientation(JList.VERTICAL);
		listTimeline.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listTimeline.setVisibleRowCount(6);
		listTimeline.setFixedCellWidth(500);
		listTimeline.setFixedCellHeight(90);
		listTimeline.setSelectionBackground(Colors.TIMELINE_SELECTION);
		listTimeline.setSelectionForeground(Colors.TIMELINE_MESSAGE_FONT);
		listTimeline.setForeground(Colors.TIMELINE_MESSAGE_FONT);
		
		ThumbnailCache cache = new ThumbnailCache();
		cache.setThumbnailCacheListener(new ThumbnailCacheListener() {
			@Override
			public void thumbnailLoaded(URL identifier) {
				// TODO mnogo bi bolje bilo kad bi se radio repaint(Rect bounds) samo jedne celije
				listTimeline.repaint();
			}
		});
		
		listTimeline.setCellRenderer(new TweetCellRenderer(cache));

		scrollPaneTimeline = new JScrollPane(listTimeline);
		scrollPaneTimeline.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		scrollPaneTimeline.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPaneTimeline.setOpaque(false);
		// scrollPaneTimeline.setBackground(Color.white);
		// scrollPaneTimeline.getViewport().setOpaque(false);
		scrollPaneTimeline.getViewport().setBackground(Color.white);
		scrollPaneTimeline.getVerticalScrollBar().setUI(new MinimalisticScrollBarUI(Colors.SCROLLBAR_TRACK, Colors.SCROLLBAR_THUMB, Colors.SCROLLBAR_ARROW));
		
		add(scrollPaneTimeline, BorderLayout.CENTER);
	}

	public void loadTweets() {
		int requestPeriod = (3600 / TwitterClient.getInstance().getHourlyRateLimit() + 20) * 1000;
		logger.info("Request period: {} ms", requestPeriod);
		
		Timer tweetLoaderTimer = new Timer("TweetsLoader", true);
		tweetLoaderTimer.scheduleAtFixedRate(new TweetLoader(), 300, requestPeriod);
	}
	
	private class TweetLoader extends TimerTask {
		private ResponseList<Status> latestStatuses = null;

		@Override
		public void run() {
			Window parentWindow = SwingUtilities.windowForComponent(TimelinePane.this);
			boolean isWindowActive = (parentWindow != null) ? parentWindow.isActive() : true;
			boolean isScrolling = scrollPaneTimeline.getVerticalScrollBar().getValueIsAdjusting();
			
			logger.debug("Tweet loader started.");

			if (isWindowActive && !isScrolling && isShowing()) {
				logger.debug("Reloading statuses.");
				
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						HeaderPane.getInstance().setProgressBarVisible(true);
					}
				});
				
				final ResponseList<Status> statuses = TwitterClient.getInstance().getTimeline();
				if (statuses != null && !statuses.isEmpty()) {
					boolean reload = true;
					// Check if there are new statuses
					if (latestStatuses != null)
						reload = !statuses.equals(latestStatuses);
					
					if (reload) {
						latestStatuses = statuses;
						logger.debug("Loaded {} tweets.", statuses.size());
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								listTimelineModel.addAll(statuses);
							}
						});
					}
					else
						logger.debug("Nothing new.");
				}
				else
					logger.warn("Empty timeline.");
				
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						HeaderPane.getInstance().setProgressBarVisible(false);
					}
				});
			}
		}
		
	}

}
