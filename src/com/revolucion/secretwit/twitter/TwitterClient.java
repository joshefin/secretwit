/*******************************************************************************
 * Copyright (c) 2011 Zeljko Zirikovic.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GPL which 
 * accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.revolucion.secretwit.twitter;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.ProfileImage;
import twitter4j.RateLimitStatus;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import com.revolucion.secretwit.utils.IOUtils;

public class TwitterClient {

	private final Logger logger = LoggerFactory.getLogger(TwitterClient.class);

	private final String PREFS_KEY = "secretwit/prefs";

	private final String consumerKey = "2ptE0qPL1jNkeyrYobrIQ";
	private final String consumerSecret = "IDbJs9ppEplZQaucPHMQ1NnRBzviaWgZWt3WDxpT24";

	private final String TOKEN_KEY = "secretwit-token";
	private final String TOKENSECRET_KEY = "secretwit-tokensecret";

	private Twitter twitter;
	private RequestToken requestToken;
	//private boolean signedIn = false;
	private User user;

	private TwitterClient() {
		init();
	}

	private static class Singleton {
		private static final TwitterClient INSTANCE = new TwitterClient();
	}

	public static TwitterClient getInstance() {
		return Singleton.INSTANCE;
	}

	private void init() {
		logger.info("Starting Twitter client.");

		TwitterFactory factory = new TwitterFactory();
		twitter = factory.getInstance();

		twitter.setOAuthConsumer(consumerKey, consumerSecret);

		logger.info("OAuth consumer initialized.");

		AccessToken accessToken = loadAccessToken();
		if (accessToken != null) {
			twitter.setOAuthAccessToken(accessToken);
			logger.info("Loaded access token.");
			// signedIn = true;
			try {
				user = twitter.verifyCredentials();
			}
			catch (TwitterException e) {
				logger.error(e.getMessage());
			}
		}
	}

	public int getHourlyRateLimit() {
		int limit = 350;
		try {
			RateLimitStatus rateLimitStatus = twitter.getRateLimitStatus();
			if (rateLimitStatus != null)
				limit = rateLimitStatus.getHourlyLimit();
		}
		catch (TwitterException e) {
			logger.warn("Can't get hourly limit. {}", findExceptionCause(e));
		}

		logger.info("Hourly limit: {}", limit);

		return limit;
	}

	public boolean isSignedIn() {
		//return signedIn;
		return (user != null);
	}

	public String getAuthorizationUrl() {
		try {
			requestToken = twitter.getOAuthRequestToken();

			return requestToken.getAuthorizationURL();
		}
		catch (TwitterException e) {
			logger.error("Can't get authorization url. {}", findExceptionCause(e));
		}

		return null;
	}

	public User authorize(String pin) throws TwitterException {
		AccessToken accessToken = null;
		if (pin != null && !pin.isEmpty()) {
			accessToken = twitter.getOAuthAccessToken(requestToken, pin);
			logger.debug("Requested access token for pin {}.", pin);
		}
		else
			accessToken = twitter.getOAuthAccessToken();

		User user = twitter.verifyCredentials();
		if (user != null) {
			logger.info("Authorized user {}.", user.getName());
			// signedIn = true;
			this.user = user;
			storeAccessToken(accessToken);
		}

		return user;
	}

	public void signOut() {
		// signedIn = false;
		user = null;
		storeAccessToken(null);

		init();
	}

	private AccessToken loadAccessToken() {
		Preferences preferences = Preferences.userRoot().node(PREFS_KEY);
		String token = preferences.get(TOKEN_KEY, null);
		String tokenSecret = preferences.get(TOKENSECRET_KEY, null);

		return (token != null && tokenSecret != null) ? new AccessToken(token, tokenSecret) : null;
	}

	private void storeAccessToken(AccessToken accessToken) {
		Preferences preferences = Preferences.userRoot().node(PREFS_KEY);

		if (accessToken != null) {
			preferences.put(TOKEN_KEY, accessToken.getToken());
			preferences.put(TOKENSECRET_KEY, accessToken.getTokenSecret());

			logger.debug("Stored token: {}", accessToken.getToken());
			logger.debug("Stored token secret: {}", accessToken.getTokenSecret());
		}
		else {
			preferences.remove(TOKEN_KEY);
			preferences.remove(TOKENSECRET_KEY);
		}

		try {
			preferences.flush();
		}
		catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	private ResponseList<Status> getHomeTimeline() {
		try {
			return twitter.getHomeTimeline();
		}
		catch (TwitterException e) {
			logger.error("Can't get home timeline. {}", findExceptionCause(e));
		}

		return null;
	}

	private ResponseList<Status> getPublicTimeline() {
		try {
			return twitter.getPublicTimeline();
		}
		catch (TwitterException e) {
			logger.error("Can't get public timeline. {}", findExceptionCause(e));
		}

		return null;
	}

	public ResponseList<Status> getTimeline() {
		return (isSignedIn()) ? getHomeTimeline() : getPublicTimeline();
	}

	public String getUser() {
		if (!isSignedIn())
			return null;
		
		return user.getScreenName();

		/*
		try {
			return twitter.getScreenName();
		}
		catch (IllegalStateException e) {
			logger.error("Can't get user name. {}", e.getMessage());
		}
		catch (TwitterException e) {
			logger.error("Can't get user name. {}", findExceptionCause(e));
		}

		return null;
		*/
	}

	public String getProfileImage() {
		if (!isSignedIn())
			return null;

		try {
			ProfileImage image = twitter.getProfileImage(twitter.getScreenName(), ProfileImage.NORMAL);
			if (image != null && image.getURL() != null)
				return image.getURL();
		}
		catch (TwitterException e) {
			logger.warn("Can't get profile image. {}", findExceptionCause(e));
		}

		return null;
	}
	
	public boolean updateProfileImage(BufferedImage image) {
		if (image == null)
			return false;
		
		ByteArrayOutputStream out = null;
		InputStream in = null;
		try {
			out = new ByteArrayOutputStream();
			ImageIO.write(image, "png", out);
			byte[] imageBytes = out.toByteArray();
			if (imageBytes != null && imageBytes.length > 2) {
				in = new ByteArrayInputStream(imageBytes);
				twitter.updateProfileImage(in);
				
				logger.info("Updated profile image.");
				
				return true;
			}
		}
		catch (IOException e) {
			logger.error("Can't update profile image. {}", e.getMessage());
		}
		catch (TwitterException e) {
			logger.error("Can't update profile image. {}", findExceptionCause(e));
		}
		finally {
			IOUtils.closeStream(in);
			IOUtils.closeStream(out);
		}
		
		return false;
	}

	public void shutdown() {
		twitter.shutdown();
	}

	public Status updateStatus(String status) {
		try {
			return twitter.updateStatus(status);
		}
		catch (TwitterException e) {
			logger.error("Can't update status. {}", findExceptionCause(e));
		}

		return null;
	}
	
	private String findExceptionCause(TwitterException exception) {
		if (exception.exceededRateLimitation())
			return "Rate limitation exceeded.";

		if (exception.isCausedByNetworkIssue())
			return "Network issue.";

		if (exception.resourceNotFound())
			return "Resource not found.";

		return exception.getMessage() + " " + exception.getExceptionCode();
	}

}
