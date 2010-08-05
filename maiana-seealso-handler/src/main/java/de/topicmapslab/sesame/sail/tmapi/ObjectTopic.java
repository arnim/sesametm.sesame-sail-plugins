/*
 * Copyright: Copyright 2010 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 */

package de.topicmapslab.sesame.sail.tmapi;

import org.tmapi.core.Locator;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;

/**
 * @author Arnim Bleier
 * 
 */
public class ObjectTopic {



	private String objectString;
	private Boolean exists;
	private boolean beginsWithBaseIRI;
	private String prefix;
	private Topic topic = null;
	private String potentialLocator;
	private MaianaSeeAlsoHandler handler;
	private TopicMap tm;

	/**
	 * 
	 */
	public ObjectTopic(MaianaSeeAlsoHandler maianaSeeAlsoHandler, Locator l) {
		this.handler = maianaSeeAlsoHandler;
		this.tm = handler.tm;
		String baseIRI = tm.getLocator().toExternalForm() + handler.DATASLOT;
		try {
			this.objectString = l.toExternalForm();
			beginsWithBaseIRI = objectString.indexOf(baseIRI) == 0;
			String extendedLocator = objectString.substring(baseIRI.length());
			prefix = extendedLocator.substring(0, 3);
			potentialLocator = extendedLocator.substring(3);
		} catch (NullPointerException e) {
			exists = false;
		} catch (StringIndexOutOfBoundsException e) {
			exists = false;
		}
	}

	/**
	 * @return The URL of this Topic as {@link String}
	 */
	public String getURL() {
		return objectString;
	}

	/**
	 * Tests if this {@link ObjectTopic} is existent in the TopicMap.
	 */
	public boolean exists() {
		if (exists != null)
			return exists;
		else {
			if (!beginsWithBaseIRI)
				exists = false;
			else if (prefix == null
					&& (prefix != handler.SI && prefix != handler.SL && prefix != handler.II))
				exists = false;
			else {
				if (prefix.equals(handler.SI))
					topic = tm.getTopicBySubjectIdentifier(tm
							.createLocator(potentialLocator));
				else if (prefix.equals(handler.SL))
					topic = tm.getTopicBySubjectLocator(tm
							.createLocator(potentialLocator));
				else
					try {
						topic = (Topic) tm.getConstructByItemIdentifier(tm
								.createLocator(potentialLocator));
					} catch (ClassCastException e) {
						// It was not a Topic
					}
				exists = topic != null;
			}
		}
		return exists;
	}
	
	/**
	 * @return Return the {@link Topic}
	 */
	public Topic getTopic(){
		if (!exists())
			return null;
		return topic;
	}

}
