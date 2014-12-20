package edu.buffalo.cse.irf14.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class QueryDocumentIndex {

	private Map<String, List<Term>> docTermMap = new HashMap<String, List<Term>>();

	public void add( String term, Map<String, Integer> postings, int docFreq ) {

		for( Entry<String, Integer> entry : postings.entrySet() ){
			Term tempTerm = new Term( term, docFreq, entry.getValue() );
			List<Term> terms = null;
			if( docTermMap.containsKey( entry.getKey() ) ) {
				terms = docTermMap.get( entry.getKey() );
			}
			else {
				terms = new ArrayList<Term>();
			}
			terms.add(tempTerm);
			docTermMap.put(entry.getKey(), terms);
		}	
	}

	public void normalizeTfIdfWeight() {
		for( Entry<String, List<Term>> entry : docTermMap.entrySet() ) {
			double norm = 0;
			for( Term term : entry.getValue() ) {
				norm = norm + (term.getWeight()*term.getWeight());
			}
			for( Term term : entry.getValue() ) {
				term.setWeight( term.getWeight()/Math.sqrt( norm ) );
			}
		}
	}
	
	public void computeOkapiScore(){

		double k1 = 1.2, b = 0.75;
		for( Entry<String, List<Term>> entry : docTermMap.entrySet() ) {
			for( Term term : entry.getValue() ) {
				term.setWeight( term.getIdf()*( ( term.getTf()*( k1 + 1 ) )/( term.getTf() + k1*( 1-b+b*( 0.9/1 ) ) ) ) ); //TODO: Need to update D and avg(D)
			}
		}
	
	}

	/**
	 * @return the docTermMap
	 */
	public Map<String, List<Term>> getDocTermMap() {
		return docTermMap;
	}	
}


class Term {

	private String term;
	private double tf;
	private int documentFreq;
	private double idf;
	private double weight;

	public Term(String term,int docFreq, int numOccur ) {
		this.term = term;
		this.documentFreq = docFreq;
		if(numOccur > 0) {
			tf = 1 + Math.log10( numOccur );
		} else {
			tf = 0;
		}
		idf = Math.log10(IndexSearcher.totalNumberOfDocs/documentFreq);   
		weight = tf*idf;
	}

	public String getTerm() {
		return term;
	}
	public double getTf() {
		return tf;
	}
	public int getDocumentFreq() {
		return documentFreq;
	}
	public double getIdf() {
		return idf;
	}
	public double getWeight() {
		return weight;
	}
	public void setWeight(double weight) {
		this.weight = weight;
	}	

}