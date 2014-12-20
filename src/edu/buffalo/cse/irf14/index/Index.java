package edu.buffalo.cse.irf14.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Index {
	
	private HashMap<Integer, List<Integer>> indexes = new HashMap<Integer, List<Integer>>();

	public void addToIndex(int key, int docId ) {

		if(indexes.containsKey(key)) {
			List<Integer> docIds = indexes.get(key);
			docIds.add(docId);
			indexes.put( key, docIds );
		} else {
			List<Integer> docIds = new ArrayList<Integer>();
			docIds.add(docId);
			indexes.put(key, docIds);
		}
	}

	/**
	 * @return the indexes
	 */
	public HashMap<Integer, List<Integer>> getIndexes() {
		return indexes;
	}

	/**
	 * @param indexes the indexes to set
	 */
	public void setIndexes(HashMap<Integer, List<Integer>> indexes) {
		this.indexes = indexes;
	}

}
