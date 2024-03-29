/*
 * Copyright: Copyright 2010 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 */

package de.topicmapslab.sesame.sail.tmapi;

import java.util.Iterator;
import java.util.Set;

import org.openrdf.model.Statement;
import org.openrdf.model.vocabulary.OWL;
import org.tmapi.core.Locator;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;

import de.topicmapslab.sesame.sail.tmapi.utils.TmapiStatementFactory;
import de.topicmapslab.sesame.sail.tmapi.utils.TmapiStatementIterator;

/**
 * @author Arnim Bleier
 *
 */
public class MultiLocatorHandler implements SailTmapiPlugin {
	
	private TopicMap tm;
	private Set<Statement> statements;
	private TmapiStatementFactory statementFactory;

	public MultiLocatorHandler(){
		
	}

	@Override
	public void evaluate(Locator subj, Locator pred, Locator obj, TopicMap tm,
			TmapiStatementIterator<?> other) {
		this.tm = tm;
		this.statements = other.getStatements();
		this.statementFactory = other.getStatementFactory();
		

		Topic sTopic = null, pTopic = null, oTopic = null;	
		sTopic = other.getTopic(subj, tm);
		pTopic = other.getTopic(pred, tm);
		oTopic = other.getTopic(obj, tm);
		

		
		if (sTopic == null && subj != null || pred != null && (pTopic == null && !OWL.SAMEAS.toString().equals(pred.toExternalForm()) )
				 || oTopic == null && obj != null) {


				// Q has no match in this tm
			} else {
				if (sTopic == null && oTopic == null && pTopic == null && pred != null && pred.toExternalForm().equals(OWL.SAMEAS.stringValue()))
					createSameAsListxPx();
				else if (sTopic != null && oTopic == null && pred != null && pred.toExternalForm().equals(OWL.SAMEAS.stringValue()))
					createSameAsListSPX(sTopic);
				else if (sTopic == null && oTopic != null)
					createSameAsListXPO(oTopic);
				else if (sTopic != null && oTopic != null)
					createTypeSameAsSPO(sTopic, oTopic);
				else if (sTopic == null && oTopic == null &&  pred == null )
					createSameAsListxPx();
				
				
				else if (sTopic != null && oTopic == null && pred == null)
					createSameAsListSPX(sTopic);
			}
		
	}
	

	

	
	private void createSameAsListxPx(){
		
		Iterator<Topic> topicsIterator = tm.getTopics().iterator();
		while (topicsIterator.hasNext()) {		
			createSameAsListSPX(topicsIterator.next());
		}
	}
	
	private void createSameAsListSPX(Topic sTopic){
		Iterator<Locator> otherLotatorsiterator = getOtherLocaters(sTopic).iterator(); 
		while (otherLotatorsiterator.hasNext()) {
			statements.add(statementFactory.create(sTopic, OWL.SAMEAS, otherLotatorsiterator.next()));
		}
	}
	
	private void createSameAsListXPO(Topic oTopic){
		createSameAsListSPX(oTopic);
	}
	
	private void createTypeSameAsSPO(Topic sTopic, Topic oTopic){
//		System.out.println(statementFactory.getBestLocator(sTopic) + "mit " + statementFactory.getBestLocator(oTopic));
	}
	
	
	private Set<Locator> getOtherLocaters(Topic t){
		Set<Locator> resutl = statementFactory.getAllLocators(t);
		resutl.remove(statementFactory.getBestLocator(t));
		return resutl;
	}
	



}
