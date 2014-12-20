/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * @author nikhillo
 * Class that represents a stream of Tokens. All {@link Analyzer} and
 * {@link TokenFilter} instances operate on this to implement their
 * behavior
 */
public class TokenStream implements Iterator<Token>{


	private List<Token> tokenList = new ArrayList<Token>();
	private ListIterator<Token> tokenIterator = null;
	private int iteratorIndex = 0;
	private boolean isRemoved = false;
	private Token currentVal = null;

	/**
	 * Method that checks if there is any Token left in the stream
	 * with regards to the current pointer.
	 * DOES NOT ADVANCE THE POINTER
	 * @return true if at least one Token exists, false otherwise
	 */
	@Override
	public boolean hasNext() {
		// TODO YOU MUST IMPLEMENT THIS

		if ( null == tokenIterator ) { 
			if ( tokenList.size() != 0 ) {
				tokenIterator = tokenList.listIterator();
			} else {
				return false;
			}
		}
		if( !tokenIterator.hasNext() ) {
			currentVal = null;
		}
		return tokenIterator.hasNext();
	}

	/**
	 * Method to return the next Token in the stream. If a previous
	 * hasNext() call returned true, this method must return a non-null
	 * Token.
	 * If for any reason, it is called at the end of the stream, when all
	 * tokens have already been iterated, return null
	 */
	@Override
	public Token next() {
		// TODO YOU MUST IMPLEMENT THIS
		try{
			if ( null == tokenIterator ) {
				if ( tokenList.size() != 0 ) {
					tokenIterator = tokenList.listIterator();
				} else {
					return null;
				}
			}
			iteratorIndex = iteratorIndex + 1;
			currentVal = tokenIterator.next();
			isRemoved = false;
			return currentVal;
		} catch( NoSuchElementException exception ) {
			currentVal = null;
			return null;
		}		
	}

	/**
	 * Method to remove the current Token from the stream.
	 * Note that "current" token refers to the Token just returned
	 * by the next method. 
	 * Must thus be NO-OP when at the beginning of the stream or at the end
	 */
	@Override
	public void remove() {
		// TODO YOU MUST IMPLEMENT THIS
		try{
			if ( null != tokenIterator ) {
				if ( iteratorIndex >= 1 && currentVal != null ) {
					tokenIterator.remove();	
					currentVal = null;
					isRemoved = true;
				}
			}
		}catch( Exception exception ) {
			exception.printStackTrace();
		}	
	}

	/**
	 * Method to reset the stream to bring the iterator back to the beginning
	 * of the stream. Unless the stream has no tokens, hasNext() after calling
	 * reset() must always return true.
	 */
	public void reset() {
		//TODO : YOU MUST IMPLEMENT THIS
		//if ( tokenList.size() != 0 ) {
		tokenIterator = tokenList.listIterator();
		isRemoved = false;
		iteratorIndex = 0;
		currentVal = null;
		//}
	}

	/**
	 * Method to append the given TokenStream to the end of the current stream
	 * The append must always occur at the end irrespective of where the iterator
	 * currently stands. After appending, the iterator position must be unchanged
	 * Of course this means if the iterator was at the end of the stream and a 
	 * new stream was appended, the iterator hasn't moved but that is no longer
	 * the end of the stream.
	 * @param stream : The stream to be appended
	 */
	public void append(TokenStream stream) {
		//TODO : YOU MUST IMPLEMENT THIS
		if ( null != stream && null != stream.getTokenList() ) {
			tokenIterator = null;
			this.tokenList.addAll(stream.getTokenList());
			tokenIterator = this.tokenList.listIterator();
			for ( int iCount=0 ; iCount < iteratorIndex ; iCount++ ) {
				tokenIterator.next();
			}
		}
	}

	/**
	 * Method to get the current Token from the stream without iteration.
	 * The only difference between this method and {@link TokenStream#next()} is that
	 * the latter moves the stream forward, this one does not.
	 * Calling this method multiple times would not alter the return value of {@link TokenStream#hasNext()}
	 * @return The current {@link Token} if one exists, null if end of stream
	 * has been reached or the current Token was removed
	 */
	public Token getCurrent() {
		//TODO: YOU MUST IMPLEMENT THIS
		if(iteratorIndex == 0) {
			return null;
		} else {
			return currentVal; 
		}
	}

	public Token previous() { 
		if( iteratorIndex == 0 || iteratorIndex == 1 ) {
			return null;
		} else {
			if( isRemoved ) {
				currentVal = tokenIterator.previous();
				iteratorIndex = iteratorIndex - 1;
				isRemoved = false;
			}
			else if( tokenIterator.previous()!=null){
				if(tokenIterator.nextIndex() != 0 ) {
					currentVal = tokenIterator.previous();	
					iteratorIndex = iteratorIndex - 1;
					tokenIterator.next();
				} else {
					tokenIterator.next();
				}
			} else {
				return null;
			}		
			return currentVal;
		}
	}
	
	public int size(){
		if( tokenList != null ) {
			return tokenList.size();
		} else {
			return 0;
		}
	}

	public List<Token> getTokenList() {
		return tokenList;
	}

	public void setTokenList(List<Token> tokenList) {
		this.tokenList = tokenList;
	}

	public int getIteratorIndex() {
		return iteratorIndex;
	}


}
