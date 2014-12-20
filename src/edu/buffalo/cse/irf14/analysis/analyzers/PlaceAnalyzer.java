/**
 * 
 */
package edu.buffalo.cse.irf14.analysis.analyzers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.buffalo.cse.irf14.analysis.Analyzer;
import edu.buffalo.cse.irf14.analysis.TokenFilter;
import edu.buffalo.cse.irf14.analysis.TokenStream;
import edu.buffalo.cse.irf14.analysis.TokenizerException;

/**
 * @author Abhijit
 *
 */
public class PlaceAnalyzer implements Analyzer {

	private List<TokenFilter> filters = null;
	private Iterator<TokenFilter> itFilters = null;
	private TokenStream stream = null;

	public PlaceAnalyzer(TokenFilter...tokenFilters) {

		if( tokenFilters != null ) {
			filters = new ArrayList<TokenFilter>();
			for(TokenFilter filter : tokenFilters){
				filters.add(filter);
			}
		}
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.irf14.analysis.Analyzer#increment()
	 */
	@Override
	public boolean increment() throws TokenizerException {

		if( itFilters == null ) {
			itFilters = filters.iterator();
		}
		if(itFilters.hasNext()) {
			stream = itFilters.next().getStream();
			return true;
		} else {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.irf14.analysis.Analyzer#getStream()
	 */
	@Override
	public TokenStream getStream() {
		// TODO Auto-generated method stub
		return stream;
	}

}
