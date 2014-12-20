package edu.buffalo.cse.irf14.query;

import java.util.Stack;

public class CommonUtil {

	public static boolean isNotNull( String str ) {
		if( str != null ) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isOperator(String str) {
		if( str.equals(QueryConstants.QUERY_AND) || str.equals(QueryConstants.QUERY_OR) || 
				str.equals(QueryConstants.QUERY_NOT) ){
			return true;
		} else {
			return false;
		}
	}

	public static String convertToPostfix(String stringRep)
	{
		StringBuilder postFix = new StringBuilder();
		try{
			Stack<String> stack = new Stack<String>();

			stringRep = stringRep.replace("{", "").replace("}", "").trim();
			
			for(String str:stringRep.split(" "))
			{
				if( isOperator( str ) )
				{
					while( !stack.empty() && checkPrec(stack.peek(), str)) {
						postFix.append( stack.pop() + " " );
					}
					stack.push(str);
				}
				else if( str.equals( "[" ) ) {
					stack.push(str);
				}
				else if ( str.equals( "]" ) ) {
					while( !stack.isEmpty() && !stack.peek().equals("[") ) { 
						postFix.append( stack.pop() + " " );
					}
					if( !stack.isEmpty() && stack.peek().equals( "[" ) ) {
						stack.pop(); 
					}
				} else {
					postFix.append( str + " " );
				}
			}
			while( !stack.empty() ) {
				postFix.append( stack.pop() + " " );
			}
			return postFix.toString();
		}catch( Exception e ){
			return postFix.toString();
		}
	}

	private static boolean checkPrec(String str1, String str2){

		int value1 = 0, value2 = 0;
		if( str1.equals( "OR" ) ) {
			value1 = 1;
		} else if( str1.equals( "AND" ) ) {
			value1 = 2;    
		}
		if( str2.equals( "OR" ) ) {
			value2 = 1;
		} else if( str2.equals( "AND" ) ) {
			value2 = 2;    
		}
		if( value1 < value2 ) {
			return false;
		}
		return true;
	}

}
