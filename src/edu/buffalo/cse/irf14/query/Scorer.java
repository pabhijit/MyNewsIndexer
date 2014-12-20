package edu.buffalo.cse.irf14.query;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import edu.buffalo.cse.irf14.SearchRunner;

public class Scorer{

	private QueryDocumentIndex queryDocs = new QueryDocumentIndex();

	public TreeMap<String, Double> evaluateScore( List<QueryTermIndex> termsMapping, Query query, SearchRunner.ScoringModel model ) {

		TreeMap<String, Double> retVal = new TreeMap<String, Double>();
		DecimalFormat formatter = new DecimalFormat("0.#####");
		try {
		for ( QueryTermIndex temp : termsMapping ) {
			queryDocs.add(temp.getTerm(), temp.getPostings(), temp.getDocFreq());
		}
		if( model.equals( SearchRunner.ScoringModel.TFIDF ) ) {
			queryDocs.normalizeTfIdfWeight(); 
			for( Entry<String, List<Term>> entry : queryDocs.getDocTermMap().entrySet() ) {
				double docScore = 0.0;
				for( Term term : entry.getValue() ) {
					docScore = docScore + term.getWeight()*query.getQueryTerms().get( term.getTerm() );
				}
				retVal.put( entry.getKey(),Double.valueOf( formatter.format( docScore ) ) );
			}	
		} else {
			queryDocs.computeOkapiScore();
			for( Entry<String, List<Term>> entry : queryDocs.getDocTermMap().entrySet() ) {
				double docScore = 0.0;
				for( Term term : entry.getValue() ) {
					docScore += term.getWeight();
				}
				retVal.put( entry.getKey(),Double.valueOf( formatter.format( docScore ) ) );
			}
		}
		return retVal;
		} catch( Exception e) {
			return null;
		}
	}

}
