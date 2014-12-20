package edu.buffalo.cse.irf14;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.regex.Matcher;

import edu.buffalo.cse.irf14.query.IndexSearcher;
import edu.buffalo.cse.irf14.query.Query;
import edu.buffalo.cse.irf14.query.QueryConstants;
import edu.buffalo.cse.irf14.query.QueryParser;
import edu.buffalo.cse.irf14.query.QueryTermIndex;
import edu.buffalo.cse.irf14.query.Scorer;

/**
 * Main class to run the searcher.
 * As before implement all TODO methods unless marked for bonus
 * @author nikhillo
 *
 */
public class SearchRunner {

	public enum ScoringModel {TFIDF, OKAPI};
	public static String defaultOperator = QueryConstants.QUERY_OR;
	public static String indexDir = null;
	private String corpusDir = null;
	private PrintStream stream = null;
	private long querytime = 0;
	private String wildCardTerm;
	Map<String, List<String>> wildCardMap = new HashMap<String, List<String>>();

	/**
	 * Default (and only public) constuctor
	 * @param indexDir : The directory where the index resides
	 * @param corpusDir : Directory where the (flattened) corpus resides
	 * @param mode : Mode, one of Q or E
	 * @param stream: Stream to write output to
	 */
	public SearchRunner(String indexDir, String corpusDir,
			char mode, PrintStream stream) {
		//TODO: IMPLEMENT THIS METHOD

		this.indexDir = indexDir;
		this.corpusDir = corpusDir;

		try{
			File file = null;

			if( mode == 'Q' ) {
				PrintStream streamOut = new PrintStream(System.out);
				this.stream = streamOut;
				while (true) {
					System.out.println("Please provide your query and press enter..");
					Scanner scanner = new Scanner(System.in);
					String userQuery = scanner.nextLine();
					if( userQuery != null && !userQuery.isEmpty() ){
						if( userQuery.length() < 10 ) {
							query( userQuery,ScoringModel.TFIDF );
						} else {
							query( userQuery,ScoringModel.OKAPI );
						}
					} 
				}
			} else if( mode == 'E') {
				this.stream = stream;
				System.out.println("Please provide file path and press enter..");
				Scanner scanner = new Scanner(System.in);
				String pathName = scanner.nextLine();
				if( pathName != null && !pathName.isEmpty() ) {
					file = new File(pathName);
					query(file);
				}
			} else {
				System.err.println("invalid mode!!");
			}
		} catch( Exception exception ){

		}
	}

	/**
	 * Method to execute given query in the Q mode
	 * @param userQuery : Query to be parsed and executed
	 * @param model : Scoring Model to use for ranking results
	 */
	public void query(String userQuery, ScoringModel model) {
		//TODO: IMPLEMENT THIS METHOD
		Map<String,TreeMap<String,Double>> output = new HashMap<String, TreeMap<String,Double>>();
		Scorer scorer = new Scorer();
		try {
			Long startTime = System.currentTimeMillis();
			if( wildcardSupported() ) {
				Matcher matcher = QueryConstants.WILD_CARD_PATTERN.matcher(userQuery);
				String tmp = null;
				while (matcher.find()) {
					tmp = matcher.group();
					wildCardTerm = tmp;
					StringBuilder builderWC = null;
					Map<String, List<String>> wildCardTerms = getQueryTerms();
					//replace the wild card terms with the ones in the map
					for(Entry<String, List<String>> entry : wildCardTerms.entrySet()){
						builderWC = new StringBuilder();
						for( String str : entry.getValue() ) {
							builderWC.append(str + " ");
						}
					}
					userQuery=userQuery.replace(tmp, builderWC.toString().trim() );
				}
			}
			Query processedQuery = QueryParser.parse(userQuery, defaultOperator);
			if( processedQuery!= null ) {
				List<QueryTermIndex> termList = IndexSearcher.searchIndex( String.valueOf( processedQuery ) );
				TreeMap<String, Double> resultList = null;
				resultList = scorer.evaluateScore(termList, processedQuery, model); 

				if( null != resultList && resultList.size() != 0 ) {
					output.put(userQuery, resultList);
				}
				querytime = System.currentTimeMillis() - startTime;
				processOutputStream(output,'Q');
			} else {
				System.out.println("Invalid query or query could not be parsed");
			}
		} catch( Exception e ) {

		}
	}

	/**
	 * Method to execute queries in E mode
	 * @param queryFile : The file from which queries are to be read and executed
	 */
	public void query(File queryFile) {
		//TODO: IMPLEMENT THIS METHOD

		Map<String,TreeMap<String,Double>> output = new HashMap<String, TreeMap<String,Double>>();
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = null;
		String line = null;
		int numQueries = 0;
		String queries[] = null;
		int firstLine = 0;
		int queryIndex = 0;
		try {
			reader = new BufferedReader(new FileReader(queryFile));
			while( ( line = reader.readLine() ) != null ){
				if( !line.trim().isEmpty() ){
					if( firstLine == 0 ) {
						numQueries = Integer.valueOf( line.trim().split("=")[1] );
						queries = new String[numQueries];
						firstLine = 1;
					} else {
						if (!line.trim().endsWith("}")) {
							builder.append(line.trim());
						} else {
							builder.append(line.trim());
							queries[queryIndex] = builder.toString();
							builder = new StringBuilder();
							queryIndex = queryIndex + 1;
						}
					}
				}
			}
			reader.close();

			if( wildcardSupported() ) {
				for( int i=0; i < numQueries; i++ ){
					Matcher matcher = QueryConstants.WILD_CARD_PATTERN.matcher(queries[i]);
					String tmp = null;
					while (matcher.find()) {
						tmp = matcher.group();
						wildCardTerm = tmp;
						StringBuilder builderWC = null;
						Map<String, List<String>> wildCardTerms = getQueryTerms();
						//replace the wild card terms with the ones in the map
						for(Entry<String, List<String>> entry : wildCardTerms.entrySet()){
							builderWC = new StringBuilder();
							for( String str : entry.getValue() ) {
								builderWC.append(str + " ");
							}
						}
						queries[i]=queries[i].replace(tmp, builderWC.toString().trim() );
					}
				}
			}

			Scorer scorer = new Scorer();
			for(String query : queries) {
				Query processedQuery = QueryParser.parse(query, defaultOperator);
				List<QueryTermIndex> termList = IndexSearcher.searchIndex( String.valueOf(processedQuery) );
				TreeMap<String, Double> resultList = null;
				if( termList!= null && termList.size() < 10 ){
					resultList = scorer.evaluateScore(termList, processedQuery, ScoringModel.TFIDF); 
				} else {
					resultList = scorer.evaluateScore(termList, processedQuery, ScoringModel.OKAPI);
				}
				if( null != resultList && resultList.size() != 0 ) {
					output.put(query, resultList);
				}
			}
			processOutputStream(output, 'E');
		}catch ( FileNotFoundException fnfe ) {
			System.out.println("Invalid File Path");
		}
		catch ( Exception e ) {
			System.out.println("Invalid Query");
		}


	}

	/**
	 * General cleanup method
	 */
	public void close() {
		//TODO : IMPLEMENT THIS METHOD
		stream.close();
	}

	/**
	 * Method to indicate if wildcard queries are supported
	 * @return true if supported, false otherwise
	 */
	public static boolean wildcardSupported() {
		//TODO: CHANGE THIS TO TRUE ONLY IF WILDCARD BONUS ATTEMPTED
		return true;
	}

	/**
	 * Method to get substituted query terms for a given term with wildcards
	 * @return A Map containing the original query term as key and list of
	 * possible expansions as values if exist, null otherwise
	 */
	public Map<String, List<String>> getQueryTerms() {
		//TODO:IMPLEMENT THIS METHOD IFF WILDCARD BONUS ATTEMPTED
		Map<String, List<String>> rv = new HashMap<String, List<String>>();
		try{
			rv.put( wildCardTerm, IndexSearcher.getWildCardTerms( wildCardTerm ) );
			return rv;
		} catch(Exception e){
			return null;
		}
	}

	/**
	 * Method to indicate if speel correct queries are supported
	 * @return true if supported, false otherwise
	 */
	public static boolean spellCorrectSupported() {
		//TODO: CHANGE THIS TO TRUE ONLY IF SPELLCHECK BONUS ATTEMPTED
		return false;
	}

	/**
	 * Method to get ordered "full query" substitutions for a given misspelt query
	 * @return : Ordered list of full corrections (null if none present) for the given query
	 */
	public List<String> getCorrections() {
		//TODO: IMPLEMENT THIS METHOD IFF SPELLCHECK EXECUTED
		return null;
	}

	public void processOutputStream(Map<String,TreeMap<String,Double>> output, char mode) {

		int k = 0; //for max number of results.
		List<String> relevantDocs = new ArrayList<String>();
		try {
			if(mode=='E'){
				stream.append("numResults="+output.size()+"\n");
				for( Entry<String,TreeMap<String,Double>> entry : output.entrySet() ) {
					stream.append(entry.getKey().split(":")[0] + ":{");
					int iCount = 1;
					List<Entry<String,Double>> sortedList = sortByValue(entry.getValue());
					for( Entry<String,Double> entry1 : sortedList ){
						if( k < 10 ) {
							stream.append( entry1.getKey()+"#"+entry1.getValue() );
							if( iCount < entry.getValue().size() ){
								stream.append(", ");
								iCount++;
							}
							relevantDocs.add( entry1.getKey() );
							k = k + 1;
						} else {
							break;
						}
					}
					stream.append("}"+"\n");
				}
			} else if(mode=='Q'){
				for( Entry<String,TreeMap<String,Double>> entry : output.entrySet() ) {
					stream.append("Query: "+entry.getKey()+"\n");
					stream.append("Query Time: "+querytime+" ms\n");

					int rank = 1;
					List<Entry<String,Double>> sortedList = sortByValue(entry.getValue());
					for( Entry<String,Double> entry1 : sortedList ){
						if( k < 10 ) {
							stream.append( "Rank: " + String.valueOf(rank)+"\nDocument:"+entry1.getKey() +"\n" );
							stream.append( "Relevancy: "+entry1.getValue() +"\n" );
							relevantDocs.add( entry1.getKey() );
							k = k + 1;
						} else {
							break;
						}
						rank = rank +1;
					}
				}

			}
			while( relevantDocs.size() < 10 ){
				relevantDocs.add("0000005");
			}

			stream.append("\n\nThe snippets are as follows:\n");
			for ( String str : relevantDocs ) {
				String fileName = corpusDir + File.separator + str;
				String line = null;
				File file = new File( fileName );
				BufferedReader reader = null;
				try {
					reader =  new BufferedReader( new FileReader( file ) );
				} catch(FileNotFoundException fnfe){
					continue;
				}
				int lines = 0;
				while ( ( line = reader.readLine() ) != null ) {
					if ( !line.trim().isEmpty() ) {
						if( lines < 3){
							stream.append(line+"\n");
							lines = lines + 1;
						} else {
							stream.append("...Read more\n\n");
							break;
						}
					}
				}
				reader.close();
			}
			stream.flush();
		} catch(Exception e){

		}
	}

	static <K,V extends Comparable<? super V>> 
	List<Entry<String,Double>> sortByValue(Map<String,Double> map) {

		List<Entry<String,Double>> sortedEntries = new ArrayList<Entry<String,Double>>(map.entrySet());

		Collections.sort(sortedEntries, 
				new Comparator<Entry<String,Double>>() {
			@Override
			public int compare(Entry<String,Double> e1, Entry<String,Double> e2) {
				return e2.getValue().compareTo(e1.getValue());
			}
		}
				);

		return sortedEntries;
	}
}