/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

import edu.buffalo.cse.irf14.analysis.analyzers.AuthorAnalyzer;
import edu.buffalo.cse.irf14.analysis.analyzers.ContentAnalyzer;
import edu.buffalo.cse.irf14.analysis.analyzers.DateAnalyzer;
import edu.buffalo.cse.irf14.analysis.analyzers.PlaceAnalyzer;
import edu.buffalo.cse.irf14.analysis.analyzers.TitleAnalyzer;
import edu.buffalo.cse.irf14.document.FieldNames;

/**
 * @author nikhillo
 * This factory class is responsible for instantiating "chained" {@link Analyzer} instances
 */
public class AnalyzerFactory {

	private static AnalyzerFactory analyzerFactory;
	private static TitleAnalyzer titleAnalyzer;
	private static ContentAnalyzer contentAnalyzer;
	private static PlaceAnalyzer placeAnalyzer;
	private static DateAnalyzer dateAnalyzer;
	private static AuthorAnalyzer authorAnalyzer;

	/**
	 * Static method to return an instance of the factory class.
	 * Usually factory classes are defined as singletons, i.e. 
	 * only one instance of the class exists at any instance.
	 * This is usually achieved by defining a private static instance
	 * that is initialized by the "private" constructor.
	 * On the method being called, you return the static instance.
	 * This allows you to reuse expensive objects that you may create
	 * during instantiation
	 * @return An instance of the factory
	 */
	public static AnalyzerFactory getInstance() {
		//TODO: YOU NEED TO IMPLEMENT THIS METHOD
		if ( null == analyzerFactory){
			analyzerFactory = new AnalyzerFactory();
		}	
		return analyzerFactory;
	}

	/**
	 * Returns a fully constructed and chained {@link Analyzer} instance
	 * for a given {@link FieldNames} field
	 * Note again that the singleton factory instance allows you to reuse
	 * {@link TokenFilter} instances if need be
	 * @param name: The {@link FieldNames} for which the {@link Analyzer}
	 * is requested
	 * @param TokenStream : Stream for which the Analyzer is requested
	 * @return The built {@link Analyzer} instance for an indexable {@link FieldNames}
	 * null otherwise
	 */
	public Analyzer getAnalyzerForField(FieldNames name, TokenStream stream) {
		//TODO : YOU NEED TO IMPLEMENT THIS METHOD
		TokenFilterFactory tokenFilterFactory = TokenFilterFactory.getInstance();

		if( null != name && null != stream ) {
			if( name.equals( FieldNames.CONTENT ) ) {
				contentAnalyzer = new ContentAnalyzer(tokenFilterFactory.getFilterByType(TokenFilterType.ACCENT, stream),
						tokenFilterFactory.getFilterByType(TokenFilterType.STOPWORD, stream),
						tokenFilterFactory.getFilterByType(TokenFilterType.STEMMER, stream),
						tokenFilterFactory.getFilterByType(TokenFilterType.CAPITALIZATION, stream),
						tokenFilterFactory.getFilterByType(TokenFilterType.SYMBOL, stream),
						tokenFilterFactory.getFilterByType(TokenFilterType.SPECIALCHARS, stream),
						tokenFilterFactory.getFilterByType(TokenFilterType.DATE, stream),
						tokenFilterFactory.getFilterByType(TokenFilterType.NUMERIC, stream));
				return contentAnalyzer;
			} 
			else if( name.equals( FieldNames.TITLE ) ) {
				//if( null == titleAnalyzer ) {
				titleAnalyzer = new TitleAnalyzer(tokenFilterFactory.getFilterByType(TokenFilterType.ACCENT, stream),
						tokenFilterFactory.getFilterByType(TokenFilterType.STEMMER, stream),
						tokenFilterFactory.getFilterByType(TokenFilterType.CAPITALIZATION, stream),			
						tokenFilterFactory.getFilterByType(TokenFilterType.SYMBOL, stream),
						tokenFilterFactory.getFilterByType(TokenFilterType.SPECIALCHARS, stream),
						tokenFilterFactory.getFilterByType(TokenFilterType.DATE, stream),
						tokenFilterFactory.getFilterByType(TokenFilterType.NUMERIC, stream));
				//}
				return titleAnalyzer;
			}
			else if( name.equals( FieldNames.AUTHOR ) ) {
				authorAnalyzer = new AuthorAnalyzer(tokenFilterFactory.getFilterByType(TokenFilterType.ACCENT, stream));

				return authorAnalyzer;
			}
			else if( name.equals( FieldNames.AUTHORORG ) ) {
				authorAnalyzer = new AuthorAnalyzer(tokenFilterFactory.getFilterByType(TokenFilterType.ACCENT, stream));

				return authorAnalyzer;
			}
			else if( name.equals( FieldNames.PLACE ) ) {
				placeAnalyzer = new PlaceAnalyzer(tokenFilterFactory.getFilterByType(TokenFilterType.ACCENT, stream));

				return placeAnalyzer;
			}
			else if( name.equals( FieldNames.NEWSDATE ) ) {
				dateAnalyzer = new DateAnalyzer(tokenFilterFactory.getFilterByType(TokenFilterType.DATE, stream));
				return dateAnalyzer;
			}
		}
		return null;
	}
}
