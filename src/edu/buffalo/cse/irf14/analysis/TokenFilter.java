
/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

/**
 * The abstract class that you must extend when implementing your 
 * TokenFilter rule implementations.
 * Apart from the inherited Analyzer methods, we would use the 
 * inherited constructor (as defined here) to test your code.
 * @author nikhillo
 *
 */
public abstract class TokenFilter implements Analyzer {


	private TokenStream tokenStream = null;
	/**
	 * Default constructor, creates an instance over the given
	 * TokenStream
	 * @param stream : The given TokenStream instance
	 */
	public TokenFilter(TokenStream stream) {
		//TODO : YOU MUST IMPLEMENT THIS METHOD
		tokenStream = stream;

	}

	@Override
	public boolean increment() throws TokenizerException{
		try {
			if( tokenStream.hasNext() && tokenStream.next()!=null ){
				return true;
			} else {	
				return false;
			}
		} catch(Exception exception) {
			throw new TokenizerException();
		}
	}
	public TokenStream getStream() {
		try {
			tokenStream.reset();
			while( this.increment() ){
				applyFilter();
			}
			tokenStream.reset();
		} catch (TokenizerException e) {
			//TODO : code to be added.
		}
		return tokenStream;
	}

	public TokenStream getCurrentStream() {
		return tokenStream;
	}

	public TokenFilter getNextFilter() throws TokenizerException{
		return null;
	}

	public abstract void applyFilter() throws TokenizerException;

}
