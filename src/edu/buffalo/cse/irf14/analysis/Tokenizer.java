/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

import java.util.ArrayList;
import java.util.List;

/**
 * @author nikhillo
 * Class that converts a given string into a {@link TokenStream} instance
 */
public class Tokenizer {

	private String delim;

	/**
	 * Default constructor. Assumes tokens are whitespace delimited
	 */	
	public Tokenizer() {
		//TODO : YOU MUST IMPLEMENT THIS METHOD
		setDelim(" ");
	}

	/**
	 * Overloaded constructor. Creates the tokenizer with the given delimiter
	 * @param delim : The delimiter to be used
	 */
	public Tokenizer(String delim) {
		//TODO : YOU MUST IMPLEMENT THIS METHOD
		if( null != delim ){
			setDelim(delim);
		} else {
			setDelim(" ");
		}
	}

	/**
	 * Method to convert the given string into a TokenStream instance.
	 * This must only break it into tokens and initialize the stream.
	 * No other processing must be performed. Also the number of tokens
	 * would be determined by the string and the delimiter.
	 * So if the string were "hello world" with a whitespace delimited
	 * tokenizer, you would get two tokens in the stream. But for the same
	 * text used with lets say "~" as a delimiter would return just one
	 * token in the stream.
	 * @param str : The string to be consumed
	 * @return : The converted TokenStream as defined above
	 * @throws TokenizerException : In case any exception occurs during
	 * tokenization
	 */
	public TokenStream consume(String str) throws TokenizerException {
		//TODO : YOU MUST IMPLEMENT THIS METHOD

		List<Token> lstTokens = new ArrayList<Token>();
		TokenStream tokenStream = new TokenStream();
		Token token = null;
		try {
			if ( null != str && !str.isEmpty() ) {
				str = str.replaceAll("( )+", " ").trim();
				int curr = 0, end;

				if ( str.indexOf( delim ) >- 1 ) {
					while ( ( end = str.trim().indexOf( delim, curr ) ) >= 0 ) {
						token = new Token();
						token.setTermText( str.substring( curr, end ) );
						//token.setTermBuffer(str.substring(curr, end).toCharArray());
						lstTokens.add(token);
						curr = end + 1;
					}
					token = new Token();
					token.setTermText( str.substring( curr, str.length() ) );
					//token.setTermBuffer(str.substring(curr, str.length()).toCharArray());
					lstTokens.add( token );
				} else {
					token = new Token();
					token.setTermText(str);
					//token.setTermBuffer(str.toCharArray());
					lstTokens.add(token);
				}

				tokenStream.setTokenList(lstTokens);
				return tokenStream;

			} else {
				throw new TokenizerException();
			}
		}
		catch(Exception exception){
			throw new TokenizerException();
		}
	}

	/**
	 * @return the delim
	 */
	public String getDelim() {
		return delim;
	}

	/**
	 * @param delim the delim to set
	 */
	public void setDelim(String delim) {
		this.delim = delim;
	}

}
