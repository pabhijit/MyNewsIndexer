package edu.buffalo.cse.irf14.index;

import java.util.SortedMap;
import java.util.TreeMap;

public class Dictionary {

	private SortedMap<String, Integer> dictionary;
	private int valueId = 1;

	public Dictionary() {
		dictionary = new TreeMap<String, Integer>();
	}

	public void addToDictionary(String term){
		if(!dictionary.containsKey(term)) {
			dictionary.put(term, valueId++);
		}
	}

	public void add(String term, Integer value){
		dictionary.put(term, value);
	}

	public Integer get(String key) {	
		return dictionary.get(key);
	}

	/**
	 * @return the dictionary
	 */
	public SortedMap<String, Integer> getDictionary() {
		return dictionary;
	}

	/**
	 * @param dictionary the dictionary to set
	 */
	public void setDictionary(SortedMap<String, Integer> dictionary) {
		this.dictionary = dictionary;
	}
}
