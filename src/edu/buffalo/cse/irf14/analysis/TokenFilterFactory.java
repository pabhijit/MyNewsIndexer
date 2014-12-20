/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

import edu.buffalo.cse.irf14.analysis.TokenFilters.AccentFilter;
import edu.buffalo.cse.irf14.analysis.TokenFilters.CapitalizationFilter;
import edu.buffalo.cse.irf14.analysis.TokenFilters.DateFilter;
import edu.buffalo.cse.irf14.analysis.TokenFilters.NumericFilter;
import edu.buffalo.cse.irf14.analysis.TokenFilters.SpecialCharsFilter;
import edu.buffalo.cse.irf14.analysis.TokenFilters.Stemmer;
import edu.buffalo.cse.irf14.analysis.TokenFilters.StopWordRemover;
import edu.buffalo.cse.irf14.analysis.TokenFilters.SymbolFilter;


/**
 * Factory class for instantiating a given TokenFilter
 * @author nikhillo
 *
 */
public class TokenFilterFactory {

	private static TokenFilterFactory filterFactory;

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
	public static TokenFilterFactory getInstance() {
		//TODO : YOU MUST IMPLEMENT THIS METHOD
		if ( null == filterFactory){
			filterFactory = new TokenFilterFactory();
		}		
		return filterFactory;
	}

	/**
	 * Returns a fully constructed {@link TokenFilter} instance
	 * for a given {@link TokenFilterType} type
	 * @param type: The {@link TokenFilterType} for which the {@link TokenFilter}
	 * is requested
	 * @param stream: The TokenStream instance to be wrapped
	 * @return The built {@link TokenFilter} instance
	 */
	public TokenFilter getFilterByType(TokenFilterType type, TokenStream stream) {
		//TODO : YOU MUST IMPLEMENT THIS METHOD
		try{
			if(null != type && null != stream){
				if( type.equals(TokenFilterType.SYMBOL) ){
					return new SymbolFilter(stream);
				}
				if( type.equals(TokenFilterType.DATE) ){
					return new DateFilter(stream);
				}
				if( type.equals(TokenFilterType.NUMERIC) ){
					return new NumericFilter(stream);
				}
				if( type.equals(TokenFilterType.CAPITALIZATION) ){
					return new CapitalizationFilter(stream);
				}
				if( type.equals(TokenFilterType.STOPWORD) ){
					return new StopWordRemover(stream);
				}
				if( type.equals(TokenFilterType.STEMMER) ){
					return new Stemmer(stream);
				}
				if( type.equals(TokenFilterType.ACCENT) ){
					return new AccentFilter(stream);
				}
				if( type.equals(TokenFilterType.SPECIALCHARS) ){
					return new SpecialCharsFilter(stream);
				}
			}
		} catch ( Exception exception ){
			return null;
		}
		return null;
	}
}
