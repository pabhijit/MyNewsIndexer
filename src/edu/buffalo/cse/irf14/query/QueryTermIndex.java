package edu.buffalo.cse.irf14.query;

import java.util.HashMap;
import java.util.Map;

public class QueryTermIndex {

	private String term;
	private Map<String, Integer> postings = new HashMap<String, Integer>();
	private int docFreq;
	
	public QueryTermIndex( String term,Map<String, Integer> postings) {
		this.term = term;
		this.postings = postings;
		for(int occur: postings.values()) {
			this.docFreq = this.docFreq + occur;
		}
	}
	
	/**
	 * @return the term
	 */
	public String getTerm() {
		return term;
	}
	/**
	 * @return the postings
	 */
	public Map<String, Integer> getPostings() {
		return postings;
	}
	/**
	 * @return the docFreq
	 */
	public int getDocFreq() {
		return docFreq;
	}
}
