/*
 * Copyright: Copyright 2010 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 */

package de.topicmapslab.sesame.sail.tmapi;

import java.util.Iterator;
import java.util.Set;

import org.openrdf.model.Statement;
import org.openrdf.model.vocabulary.RDFS;
import org.tmapi.core.Locator;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;

import de.topicmapslab.sesame.sail.tmapi.utils.TmapiStatementFactory;
import de.topicmapslab.sesame.sail.tmapi.utils.TmapiStatementIterator;

/**
 * @author Arnim Bleier
 *
 */
public class MaianaSeeAlsoHandler implements SailTmapiPlugin {
	
	TopicMap tm;
	private Set<Statement> statements;
	private TmapiStatementFactory statementFactory;
	private TmapiStatementIterator<?> tmapiStatementIteratior;
	
	public final String DATASLOT = "t/";
	public final String SI = "si:";
	public final String SL = "sl:";
	public final String II = "ii:";

	public MaianaSeeAlsoHandler(){
		
	}

	@Override
	public void evaluate(Locator subj, Locator pred, Locator obj, TopicMap tm,
			TmapiStatementIterator<?> tmapiStatementIteratior) {
		
		this.tm = tm;
		this.statements = tmapiStatementIteratior.getStatements();
		this.statementFactory = tmapiStatementIteratior.getStatementFactory();
		this.tmapiStatementIteratior = tmapiStatementIteratior;
		
		Topic sTopic = tmapiStatementIteratior.getTopic(subj, tm);
		ObjectTopic objectTopic = new ObjectTopic(this, obj);

		if (	(pred != null && !RDFS.SEEALSO.toString().equals(pred.toExternalForm()))
				
				|| (subj != null && sTopic == null )
				
				|| (obj != null && !objectTopic.exists())
						) {

			
			// Q has no match in this tm
		} else {

			if (sTopic == null
					&& (pred != null && pred.toExternalForm().equals(RDFS.SEEALSO.stringValue()))
					&& obj == null)
				createSameAsListxPx();
			else if (sTopic != null
					&& obj == null 
							)
				createSameAsListSPX(sTopic);
			else if (sTopic == null
					&& objectTopic.exists()) {
				createSameAsListXPO(obj);
			} else if (sTopic != null
					&& objectTopic.exists())
				createTypeSameAsSPO(sTopic, objectTopic);
			
			else if (subj == null
					&& obj == null
					&& pred == null)
				createSameAsListxPx();
			else 
				System.out.println("YOu schould never read This form " + getClass());
		}
		
	}
	
	

	private void createSameAsListxPx() {
		Iterator<Topic> topicsIterator = tm.getTopics().iterator();
		while (topicsIterator.hasNext()) {
			createSameAsListSPX(topicsIterator.next());
		}
	}

	private void createSameAsListSPX(Topic sTopic) {
		statements.add(statementFactory.create(sTopic, RDFS.SEEALSO, tm
				.getLocator().toExternalForm()
				+ DATASLOT
				+ getBestLocatorString(sTopic)));
	}

	private void createSameAsListXPO(Locator obj) {
		String s = obj.toExternalForm();		
		int i = s.lastIndexOf(tm.getLocator().toExternalForm());

		Topic t = tmapiStatementIteratior.getTopic(tm.createLocator(s.substring(i)), tm);
		if (t != null)
			statements.add(statementFactory.create(t, RDFS.SEEALSO, s));
	}

	private void createTypeSameAsSPO(Topic sTopic, ObjectTopic oTopic) {
		statements.add(statementFactory.create(sTopic, RDFS.SEEALSO, oTopic.getURL()));
	}
	
	public String getBestLocatorString(Topic t) {
		Set<Locator> l;
		l = t.getSubjectLocators();
		if (!l.isEmpty())
			return SL + l.iterator().next().toExternalForm();
		l = t.getSubjectIdentifiers();
		if (!l.isEmpty())
			return SI + l.iterator().next().toExternalForm();
		l = t.getItemIdentifiers();
		return II + l.iterator().next();
	}



}
