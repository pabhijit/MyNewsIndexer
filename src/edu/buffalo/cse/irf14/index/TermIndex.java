package edu.buffalo.cse.irf14.index;

import java.util.HashMap;

public class TermIndex {

	private HashMap<Integer, HashMap<Integer,Integer>> termPostings = new HashMap<Integer, HashMap<Integer,Integer>>();

	public void addToPostings( int termId, int docId ) {

		if( termPostings.containsKey( termId ) ) {
			HashMap<Integer,Integer> tempPostings = termPostings.get(termId);
			if( tempPostings.containsKey( docId ) ) {
				tempPostings.put( docId, tempPostings.get( docId ) + 1 );
			} else {
				tempPostings.put( docId, 1 );
			}
		} else {
			HashMap<Integer,Integer> tempPostings = new HashMap<Integer, Integer>();
			tempPostings.put( docId, 1 );
			termPostings.put( termId, tempPostings );
		}
	}
	
	public void add( int termId, int docId, int docFreq ) {

			HashMap<Integer,Integer> tempPostings = null;
			if( termPostings.containsKey(termId) ){
				tempPostings = termPostings.get(termId);
				tempPostings.put(docId, docFreq);
			} else {
				tempPostings = new HashMap<Integer, Integer>();
			tempPostings.put( docId, docFreq );
			termPostings.put( termId, tempPostings );
			}
	}

	/**
	 * @return the termPostings
	 */
	public HashMap<Integer, HashMap<Integer, Integer>> getTermPostings() {
		return termPostings;
	}

	/**
	 * @param termPostings the termPostings to set
	 */
	public void setTermPostings(
			HashMap<Integer, HashMap<Integer, Integer>> termPostings) {
		this.termPostings = termPostings;
	}

}
