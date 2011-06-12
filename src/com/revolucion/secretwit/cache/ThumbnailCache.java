/*******************************************************************************
 * Copyright (c) 2011 Zeljko Zirikovic.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GPL which 
 * accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.revolucion.secretwit.cache;

import java.awt.image.BufferedImage;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import com.revolucion.secretwit.utils.ImageUtils;
import com.thebuzzmedia.imgscalr.Scalr;

/**
 * ThumbnailCache - A cache the for image thumbnails that provides chaching and background fetching. Thumbnails are
 * fetched by a IThumbnailFetcher which needs the KEY to know how to get the thumbnail.
 * 
 * @author c0d3R
 * 
 */
public class ThumbnailCache {
	
	public static final int THUMBNAIL_SIZE = 48;

	/**
	 * Empty cover image that is displayed while images is not loaded.
	 */
	private BufferedImage emptyImage = null;

	/**
	 * Map that represents the cache for thumbnails.
	 */
	private ConcurrentHashMap<URL, SoftReference<BufferedImage>> thumbnails;

	/**
	 * The maximum number of item to keep in the request queue.
	 */
	private int maxQueueSize = 100;

	/**
	 * List of waiting requests for the asyncronus fetching thread; new requests are added to the start of the list and
	 * requests are processed from the start of the list.
	 */
	private LinkedList<URL> requestQueue = new LinkedList<URL>();
	
	/**
	 * Listener that is fired whan a thumbnail is loaded.
	 */
	private ThumbnailCacheListener thumbnailCacheListener;

	/**
	 * Currently running Fetcher Thread used for reading and loading images.
	 */
	private FetcherThread fetcherThread;

	public ThumbnailCache() {
		loadEmptyImage();

		thumbnails = new ConcurrentHashMap<URL, SoftReference<BufferedImage>>();

		// Start fetcher thread
		fetcherThread = new FetcherThread();
		new Thread(fetcherThread).start();
	}

	private void loadEmptyImage() {
		emptyImage = ImageUtils.getLocalImage("empty_avatar.gif");
	}

	/**
	 * Trazi thumbnail iz kesa. Ako se zeljeni thumbnail ne nalazi u kesu, vraca se <code>emptyImage</code>, a pokrece
	 * se zahtev za ucitavanje trazenog thumbnaila. Sledeci put kad se pozove ova metoda za isti thumbnail on ce biti
	 * odmah na raspolaganju.
	 * 
	 * @param coverFileName
	 * @return Thumbnail of the image or <code>emptyImage</code>
	 */
	public BufferedImage requestThumbnail(URL imageUrl) {
		// Ako je null vrati empty cover
		if (imageUrl == null)
			return emptyImage;

		// Ako je thumbnail u kesu, vrati ga odmah
		SoftReference<BufferedImage> reference = thumbnails.get(imageUrl);
		if (reference != null) {
			BufferedImage image = reference.get();
			// Posto je soft reference, garbage collector moze ocistiti pa ce value biti null ...
			if (image != null)
				return image;
			else
				thumbnails.remove(imageUrl);
		}

		// ... ako nije, pokreni ucitavanje
		loadThumbnail(imageUrl);

		return emptyImage;
	}

	/**
	 * Zahtev za ucitavanje cover-a.
	 * 
	 * @param coverFileName
	 */
	private void loadThumbnail(URL imageUrl) {
		// Request image
		synchronized (requestQueue) {
			// Add to queue
			requestQueue.addFirst(imageUrl);
			// If queue is too big then remove some items
			while (requestQueue.size() > maxQueueSize)
				requestQueue.removeLast();
			// Notify thread if its waiting on the queue
			requestQueue.notify();
		}
	}

	/**
	 * Fetch and return a thumbnail image and store it in the cache, using the provided fetcher.
	 */
	private BufferedImage fetchAndCacheImage(URL identifier) {
		// Check if in cache already
		SoftReference<BufferedImage> imageRef = thumbnails.get(identifier);
		if (imageRef != null) {
			BufferedImage img = imageRef.get();
			if (img != null)
				return img;
		}

		try {
			BufferedImage image = ImageUtils.getImage(identifier);
			if (image != null) {
				if (image.getWidth() > THUMBNAIL_SIZE || image.getHeight() > THUMBNAIL_SIZE)
					image = Scalr.resize(image, THUMBNAIL_SIZE);
					
				thumbnails.put(identifier, new SoftReference<BufferedImage>(image));
			}

			return image;
		}
		catch (Exception e) {
			return emptyImage;
		}
	}

	/**
	 * Remove a thumnail fetch request from the queue and cancel the fetching if its in progress.
	 * 
	 * @param identifier
	 *            Unique identifier for the thumbnail
	 */
	public void unrequestThumbnail(URL identifier) {
		// Remove from to process queue
		synchronized (requestQueue) {
			while (requestQueue.remove(identifier));
		}
		// Cancel if currently background fetching
		fetcherThread.cancel(identifier);
	}
	
	/**
	 * Removes specified thumbnail from the cache so the next time it's requested it'll be loaded again.
	 * 
	 * @param identifier
	 */
	public void reloadThumbnail(URL identifier) {
		unrequestThumbnail(identifier);
		thumbnails.remove(identifier);
	}

	/**
	 * Release any resources used by the cache, threads and memory. This class is useless after this has been called.
	 */
	public void clear() {
		fetcherThread.dispose();
		thumbnails.clear();
	}

	private final class FetcherThread implements Runnable {
		private AtomicBoolean keepRuning = new AtomicBoolean(true);
		private URL currentImageKey = null;

		public FetcherThread() {}

		public void run() {
			while (keepRuning.get()) {
				// Fetch image key from queue
				synchronized (requestQueue) {
					currentImageKey = requestQueue.poll();
				}
				// Process key if there is one
				if (currentImageKey != null) {
					if (!thumbnails.containsKey(currentImageKey)) {
						// Fetch image
						BufferedImage image = fetchAndCacheImage(currentImageKey);
						// Fire available event
						if (image != null && thumbnailCacheListener != null) {
							thumbnailCacheListener.thumbnailLoaded(currentImageKey);
						}
					}
				}
				else {
					// No jobs in que so wait
					synchronized (requestQueue) {
						try {
							requestQueue.wait();
						}
						catch (InterruptedException e) {}
					}
				}
				// Done job
				currentImageKey = null;
			}
		}

		/**
		 * Cancel the current job if its is fetching specified identifier.
		 */
		public void cancel(URL identifier) {
			if (currentImageKey != null && currentImageKey.equals(identifier)) {
				// localFetcher.cancel();
			}
		}

		/**
		 * Cancel the current job a stop fetcher thread.
		 */
		public void dispose() {
			// This will break from main thread loop
			keepRuning.set(false);
			// localFetcher.cancel();
			// Notify in case the fetching thread is waiting
			synchronized (requestQueue) {
				requestQueue.notifyAll();
			}
		}
	}
	
	public ThumbnailCacheListener getThumbnailCacheListener() {
		return thumbnailCacheListener;
	}

	public void setThumbnailCacheListener(ThumbnailCacheListener thumbnailCacheListener) {
		this.thumbnailCacheListener = thumbnailCacheListener;
	}
}
