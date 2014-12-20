/**
 * 
 */
package edu.buffalo.cse.irf14.query;



/**
 * @author nikhillo
 * Static parser that converts raw text to Query objects
 */
public class QueryParser {

	/**
	 * MEthod to parse the given user query into a Query object
	 * @param userQuery : The query to parse
	 * @param defaultOperator : The default operator to use, one amongst (AND|OR)
	 * @return Query object if successfully parsed, null otherwise
	 */
	public static Query parse(String userQuery, String defaultOperator) {
		//TODO: YOU MUST IMPLEMENT THIS METHOD

		String actualQuery = null;
		int checkBracket = 0;
		int checkQuote = 0;
		Query rv = null;

		try {
			if( !(defaultOperator.equals( QueryConstants.QUERY_AND ) || defaultOperator.equals( QueryConstants.QUERY_OR ) ) ) {
				throw new QueryParserException();
			}
			if( QueryConstants.QUERY_PATTERN.matcher(userQuery).matches() ) {
				actualQuery = QueryConstants.QUERY_PATTERN.matcher(userQuery).replaceAll("$3");

				//check the number of opening and closing brackets & double quotes.
				for( String str : actualQuery.split( " " ) ) {
					if( str.startsWith( "(" ) ) {
						checkBracket = checkBracket + 1;
					} else if( str.endsWith( ")" ) ) {
						checkBracket = checkBracket - 1;
					} else if( ( str.startsWith( "\"" ) && !str.endsWith( "\"" ) )
							|| (!str.startsWith( "\"" ) && str.endsWith( "\"" ) ) ) {
						checkQuote = checkQuote + 1;
					}
				}
				if( checkBracket == 0 && checkQuote % 2 == 0 ){
					rv = new Query(actualQuery);
					rv.buildQueryTerms( String.valueOf(rv) );
				} else {
					throw new QueryParserException();
				}
			} else {
				rv = new Query(userQuery);
				rv.buildQueryTerms( String.valueOf(rv) );
			}
		} catch( Exception exception ) {
			return null;
		}
		return rv;
	}
}
