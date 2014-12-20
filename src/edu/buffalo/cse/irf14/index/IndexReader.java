/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author nikhillo
 * Class that emulates reading data back from a written index
 */
public class IndexReader {

	/*Dictionaries*/
	private static Dictionary documents = new Dictionary(); 
	private static Dictionary categories = new Dictionary();
	private static Dictionary terms = new Dictionary();
	private static Dictionary authors = new Dictionary();
	private static Dictionary places = new Dictionary();

	/*Indexes*/
	private static Index authorIndex = null;
	private static Index categoryIndex = null;
	private static Index placeIndex = null;
	private static TermIndex termIndex = null;

	/*Files*/
	private static final String AUTHOR_FILENAME = "AuthorIndex.dat";
	private static final String TERM_FILENAME = "TermIndex.dat";
	private static final String PLACE_FILENAME = "PlaceIndex.dat";
	private static final String CATEGORY_FILENAME = "CategoryIndex.dat";
	private static final String AUTHOR_DICT_FILENAME = "AuthorDictionary.dat";
	private static final String TERM_DICT_FILENAME = "TermDictionary.dat";
	private static final String PLACE_DICT_FILENAME = "PlaceDictionary.dat";
	private static final String CATEGORY_DICT_FILENAME = "CategoryDictionary.dat";
	private static final String DOCUMENT_DICT_FILENAME = "DocumentDictionary.dat";

	private static boolean isDocDictLoaded = false;
	private BufferedReader bReader = null;
	private FileReader fin = null;
	private int totalKeyTerms; 
	private static int totalValueTerms;
	/**
	 * Default constructor
	 * @param indexDir : The root directory from which the index is to be read.
	 * This will be exactly the same directory as passed on IndexWriter. In case 
	 * you make subdirectories etc., you will have to handle it accordingly.
	 * @param type The {@link IndexType} to read from
	 */
	public IndexReader(String indexDir, IndexType type) {
		//TODO
		String fileName = null;
		if( !isDocDictLoaded ) {
			fileName = indexDir.concat(File.separator + DOCUMENT_DICT_FILENAME);
			documents = loadDictionary(fileName);
			totalValueTerms = documents.getDictionary().size();
			isDocDictLoaded = true;
		}
		//if(type.equals(IndexType.AUTHOR)){
		fileName = indexDir.concat(File.separator + AUTHOR_FILENAME);
		authorIndex = readFromDisk(fileName);
		fileName = indexDir.concat(File.separator + AUTHOR_DICT_FILENAME);
		authors = loadDictionary(fileName);
		//} else if(type.equals(IndexType.PLACE)){
		fileName = indexDir.concat(File.separator + PLACE_FILENAME);
		placeIndex = readFromDisk(fileName);
		fileName = indexDir.concat(File.separator + PLACE_DICT_FILENAME);
		places = loadDictionary(fileName);
		//} else if(type.equals(IndexType.CATEGORY)){
		fileName = indexDir.concat(File.separator + CATEGORY_FILENAME);
		categoryIndex = readFromDisk(fileName);
		fileName = indexDir.concat(File.separator + CATEGORY_DICT_FILENAME);
		categories = loadDictionary(fileName);
		//} else{
		fileName = indexDir.concat(File.separator + TERM_FILENAME);
		readTermsFromDisk(fileName);
		fileName = indexDir.concat(File.separator + TERM_DICT_FILENAME);
		terms = loadDictionary(fileName);
		//}
	}

	/**
	 * Get total number of terms from the "key" dictionary associated with this 
	 * index. A postings list is always created against the "key" dictionary
	 * @return The total number of terms
	 */
	public int getTotalKeyTerms() {
		//TODO : YOU MUST IMPLEMENT THIS
		return totalKeyTerms;
	}

	/**
	 * Get total number of terms from the "value" dictionary associated with this 
	 * index. A postings list is always created with the "value" dictionary
	 * @return The total number of terms
	 */
	public int getTotalValueTerms() {
		//TODO: YOU MUST IMPLEMENT THIS
		return totalValueTerms;
	}

	/**
	 * Method to get the postings for a given term. You can assume that
	 * the raw string that is used to query would be passed through the same
	 * Analyzer as the original field would have been.
	 * @param term : The "analyzed" term to get postings for
	 * @return A Map containing the corresponding fileid as the key and the 
	 * number of occurrences as values if the given term was found, null otherwise.
	 */
	public Map<String, Integer> getPostings(String term) {
		//TODO:YOU MUST IMPLEMENT THIS

		Map<String, Integer> rv = null;
		try {

			if( null != term ) {
				Integer termId = terms.get(term);
				if( termId !=null && termId > 0) {	
					Map<Integer, Integer> postings = termIndex.getTermPostings().get(termId);
					if( postings != null && postings.size() > 0 ) {	
						rv = new HashMap<String, Integer>();
						for ( Entry<Integer,Integer> entry : postings.entrySet()) {
							rv.put(getKeyByValue(documents,entry.getKey()), entry.getValue());
						}
					}
				} 
			} 
		}catch(Exception exception) {
			exception.printStackTrace();
		} finally {
			return rv;
		}
	}

	/**
	 * Method to get the top k terms from the index in terms of the total number
	 * of occurrences.
	 * @param k : The number of terms to fetch
	 * @return : An ordered list of results. Must be <=k fr valid k values
	 * null for invalid k values
	 */
	public List<String> getTopK(int k) {
		//TODO YOU MUST IMPLEMENT THIS
		TreeMap<Integer, List<String>> termMap = new TreeMap<Integer, List<String>>();
		List<String> rv = new ArrayList<String>();
		int iCount = 0;
		try{
			if( termIndex != null && k > 0 ) {
				for ( Entry<Integer, HashMap<Integer, Integer>> entry : termIndex.getTermPostings().entrySet() ) {
					List<String> listTemp = null;
					Integer numOccur = 0;
					for ( Integer docFreq : entry.getValue().values() ) {
						numOccur = numOccur + docFreq;
					}
					if(termMap.containsKey(numOccur)){
						listTemp = termMap.get(numOccur);
					} else {
						listTemp = new ArrayList<String>();
					}
					listTemp.add(getKeyByValue(terms,entry.getKey()));
					termMap.put(numOccur, listTemp);
				}
				for ( Entry<Integer, List<String>> entry : termMap.descendingMap().entrySet() ) {
					if(iCount < k) {
						rv.addAll(entry.getValue());
						iCount = iCount + entry.getValue().size();
					} else {
						break;
					}
				}
				//Collections.sort(rv);
				return rv;
			} else {
				return null;
			}
		}catch(Exception e) {
			return null;
		}
	}

	/**
	 * Method to implement a simple boolean AND query on the given index
	 * @param terms The ordered set of terms to AND, similar to getPostings()
	 * the terms would be passed through the necessary Analyzer.
	 * @return A Map (if all terms are found) containing FileId as the key 
	 * and number of occurrences as the value, the number of occurrences 
	 * would be the sum of occurrences for each participating term. return null
	 * if the given term list returns no results
	 * BONUS ONLY
	 */
	public Map<String, Integer> query(String...terms) {
		//TODO : BONUS ONLY

		Map<String, Integer> rv = new HashMap<String, Integer>();
		int loopCount = 0;
		if( terms != null ) {
			try{
				for( String term: terms ) {
					if(term !=null) {
						Integer termId = this.terms.get(term);
						if( termId !=null && termId > 0) {	
							Map<Integer, Integer> postings = termIndex.getTermPostings().get(termId);
							if( postings != null && postings.size() > 0 ) {	

								for ( Entry<Integer,Integer> entry : postings.entrySet()) {
									if(loopCount == 0) {
										rv.put(getKeyByValue(documents,entry.getKey()), entry.getValue());
									} else {
										if(rv.containsKey(getKeyByValue(documents,entry.getKey()))){
											Integer value = rv.get(getKeyByValue(documents,entry.getKey()))+entry.getValue();
											rv.put(getKeyByValue(documents,entry.getKey()), value);
										} else {
											rv.remove(getKeyByValue(documents,entry.getKey()));
										}
									}
								}
								if(loopCount != 0){
									List<String> mapRemove = new ArrayList<String>();
									for (String str:rv.keySet()) {
										if(!postings.containsKey(documents.get(str))){
											mapRemove.add(str);
										}
									}
									for(String str:mapRemove) {
										rv.remove(str);
									}
								}
								loopCount = loopCount + 1;
							}
						} 

					} else {
						rv = null;
						break;
					}
				}
				if(rv.isEmpty()){
					return null;
				}
				return rv;
			}catch(Exception e){
				e.printStackTrace();
				return null;
			}
		} else
			return null;
	}

	public void readTermsFromDisk( String fileName ) {

		String line = null;
		totalKeyTerms = 0;
		termIndex = new TermIndex();
		try {
			fin = new FileReader( fileName );
			bReader = new BufferedReader(fin);
			while((line=bReader.readLine()) != null){
				totalKeyTerms = totalKeyTerms + 1;
				String lineTemp[] = line.split("#");
				String postings[] = lineTemp[2].split(":");
				for( int iCount=0 ; iCount<postings.length ; iCount+=2 ) {
					termIndex.add(Integer.parseInt(lineTemp[0]), Integer.parseInt(postings[iCount]),
							Integer.parseInt(postings[iCount+1]));
				}
			}
			bReader.close();
			fin.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {

		}	
	}

	@SuppressWarnings("finally")
	public Index readFromDisk( String fileName ) {

		Index index = new Index();
		String line = null;
		totalKeyTerms = 0;
		try {
			fin = new FileReader( fileName );
			bReader = new BufferedReader(fin);
			while((line=bReader.readLine()) != null){
				totalKeyTerms = totalKeyTerms + 1;
				String lineTemp[] = line.split("#");
				String docIds[] = lineTemp[1].split(":");
				for( int iCount=0 ; iCount<docIds.length ; iCount++ ) {
					index.addToIndex(Integer.parseInt(lineTemp[0]), Integer.parseInt(docIds[iCount]));
				}
			}
			bReader.close();
			fin.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			return index;
		}	
	}

	public Dictionary loadDictionary( String fileName ) {

		String line = null;
		Dictionary dictionary = new Dictionary();
		try {
			fin = new FileReader( fileName );
			bReader = new BufferedReader(fin);
			while((line=bReader.readLine()) != null){
				String lineTemp[] = line.split(":");
				dictionary.add(lineTemp[0], Integer.parseInt(lineTemp[1]));
			}
			bReader.close();
			fin.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			return dictionary;
		}	
	}

	public Set<String> getIndexPostings(String index, IndexType indexType) {

		Set<String> retVal = new HashSet<String>();
		List<Integer> temp = null;
		try{
			if( indexType.equals( IndexType.AUTHOR ) ) {
				if( authorIndex.getIndexes().containsKey( authors.get(index)  ) ) {
					temp = authorIndex.getIndexes().get( authors.get(index) );
				} else {
					return null;
				}
			} else if( indexType.equals( IndexType.PLACE ) ){
				if( placeIndex.getIndexes().containsKey( places.get(index)  ) ) {
					temp = placeIndex.getIndexes().get( places.get(index) );
				} else {
					return null;
				}

			} else if(indexType.equals(IndexType.CATEGORY)){
				if( categoryIndex.getIndexes().containsKey( categories.get(index)  ) ) {
					temp = categoryIndex.getIndexes().get( categories.get(index) );
				} else {
					return null;
				}
			} 
			if( temp != null && temp.size() != 0 ) {
				for( Integer docValue : temp ) {
					retVal.add( getKeyByValue( documents, docValue ) );
				}
				return retVal;
			}
			return null;
		} catch( Exception exception ){
			return null;
		}
	}

	public List<String> getWildCardTerms(String term) {

		List<String> rv = null;
		try {
			if( null != term ) {
				term = term.replaceAll("\\*", ".*");
				term = term.replaceAll("\\?", ".");
				rv = new ArrayList<String>();
				for( String indexedTerm : terms.getDictionary().keySet() ) {	
					if(indexedTerm.matches(term)) {
						rv.add(indexedTerm);
					}
				}
			}
		}catch(Exception exception) {
			exception.printStackTrace();
		} finally {
			return rv;
		}
	}

	public static String getKeyByValue(Dictionary dict,Integer value) {

		for (Entry<String, Integer> entry : dict.getDictionary().entrySet()) {
			if (value.equals(entry.getValue())) {
				return entry.getKey();
			}
		}
		return null;
	}
}
