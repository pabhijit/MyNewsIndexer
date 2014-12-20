package edu.buffalo.cse.irf14.query;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import edu.buffalo.cse.irf14.index.IndexType;

/**
 * Class that represents a parsed query
 * @author nikhillo
 *
 */
public class Query {

	private String queryString;
	private Map<String, Double> queryTerms = new HashMap<String, Double>();

	public Query(String queryString) {
		this.queryString = queryString;
	}

	/**
	 * Method to convert given parsed query into string
	 */
	public String toString()
	{
		//TODO: YOU MUST IMPLEMENT THIS
		StringBuilder queryBuilder = null;
		String index="Term";
		int checkNot = 0;
		int checkDefault = 0;
		int checkColenQuote=0;
		int indexOfColen=0;
		int i=0;
		try
		{
			queryString.replace(": ", ":");
			if(queryString.contains("AND") || queryString.contains("NOT"))
			{
				String[] st=queryString.split(" ");
				for(i=0;i<st.length;i++)
				{
					if(st[i].startsWith("("))
					{
						do{i++;}while(!st[i].endsWith(")"));
					}
					else
					{
						if(i!=st.length-1)
						{
							if(st[i].contains(":\""))
							{
								do
								{i++;}while(!st[i].contains("\""));
								i++;
							}
							if(!st[i].contains("(") && !st[i].equals("AND") && !st[i].equals("OR") && !st[i].equals("NOT") && !st[i+1].equals("AND") && !st[i+1].equals("OR") && !st[i+1].equals("NOT"))
							{
								st[i]="("+st[i];
								do
								{
									i++;
									if(i==st.length-1)
										break;
								}
								while(!st[i].equals("AND") && !st[i].equals("OR") && !st[i].equals("NOT") && !st[i+1].equals("AND") && !st[i+1].equals("OR") && !st[i+1].equals("NOT") && !st[i+1].contains("(") && i!=st.length-1);
								st[i]=st[i]+")";
							}
						}
					}
				}
				queryString="";
				for(i=0;i<st.length;i++)
				queryString+=st[i]+" ";
			}
			
			queryBuilder = new StringBuilder();
			queryBuilder.append( "{ " );
			for( String str : queryString.split( " " ) )
			{
				if( checkDefault == 1 && !( CommonUtil.isOperator( str ) || str.equals(")")))
				{
					queryBuilder.append("OR ");
					checkDefault = 0;
				}
				if( str.startsWith( "\"" ) )
				{
					//str = str.replace("\"", "");
					queryBuilder.append( index+":" + str + " " );
					index="Term";
				}
				else if( str.endsWith( "\"" ) )
				{
					//str = str.replace("\"", "");
					queryBuilder.append( str + " " );
					checkDefault = 1;
					checkColenQuote=0;
				}
				else if( str.equals( QueryConstants.QUERY_AND ) || str.equals( QueryConstants.QUERY_OR ) )
				{
					queryBuilder.append( str + " " );
					checkDefault = 0;
				}
				else if( str.equals( QueryConstants.QUERY_NOT ) )
				{
					queryBuilder.append( "AND <" );
					checkDefault = 0;
					checkNot = 1;
				}
				else if( str.startsWith( "(" ) )
				{
					queryBuilder.append("[ Term:"+str.substring(1)+" ");
					checkDefault = 1;
				}
				else if( str.endsWith( ")" ) ) 
				{
					if(checkNot==1)
					{
						queryBuilder.append(index+":"+str.substring(0, str.length()-1)+"> ] " );
						checkNot=0;
						index="Term";	// defaulting index again to Term
					}
					else
					{
						queryBuilder.append(index+":"+str.substring(0, str.length()-1)+" ] " );
						index="Term";	// defaulting index again to Term
					}
					checkDefault = 1;
				}
				else if( str.contains( ":" ) &&( str.split( ":" )[0].equalsIgnoreCase( IndexType.AUTHOR.toString() )||
						str.split( ":" )[0].equalsIgnoreCase( IndexType.PLACE.toString() )||
						str.split( ":" )[0].equalsIgnoreCase( IndexType.CATEGORY.toString() ) ||
						str.split( ":" )[0].equalsIgnoreCase( IndexType.TERM.toString()) ) )
				{
					if(str.split(":")[1].startsWith("("))
					{
						index=str.split(":")[0];
						indexOfColen=str.indexOf(":");
						queryBuilder.append("[ "+index+":"+str.substring(indexOfColen+2)+" ");
						checkDefault = 1;
					}
					else
					{
						if( checkNot == 0 )
						{
							queryBuilder.append( str + " " );
							if(!str.contains(":\""))
							checkDefault = 1;
							else
							checkColenQuote=1;
						}
						else
						{
							queryBuilder.append( str + "> " );
							checkNot = 0;
							checkDefault = 1;
						}
					}
				}
				else
				{
					if( checkNot == 0 )
					{
						if(checkColenQuote==1)
						{
							queryBuilder.append(str + " " );
						}
						else
						{
							queryBuilder.append( index+":" + str + " " );
							checkDefault = 1;
						}
					}
					else 
					{
						queryBuilder.append( index+":" + str + "> " );
						checkNot = 0;
						checkDefault = 1;
					}
				}
			}
			queryBuilder.append( "}" );
		}
		catch( Exception exception )
		{
			return null;
		}
		return queryBuilder.toString();
	}

	public void buildQueryTerms( String query ) {
		
		String terms[] = query.split(" ");
		
		for( String term : terms ){
			if( !CommonUtil.isOperator(term) && !term.equals("{") && !term.equals("}") && !term.equals("[") && !term.equals("]") ) {
				if( queryTerms.containsKey( term ) ) {
					queryTerms.put( term, queryTerms.get(term)+1 );
				} else {
					queryTerms.put( term, 1.0);
				}
			}
		}
		double norm = 0.0;
		for( Entry<String,Double> entry : queryTerms.entrySet() ) {
			double tf = 1 + Math.log10( entry.getValue() );
			norm += tf*tf;
			queryTerms.put( entry.getKey(), tf );
		}
		for( Entry<String,Double> entry : queryTerms.entrySet() ) {
			queryTerms.put( entry.getKey(), entry.getValue()/Math.sqrt( norm ) );
		}
	}

	/**
	 * @return the queryString
	 */
	public String getQueryString() {
		return queryString;
	}

	/**
	 * @return the queryTerms
	 */
	public Map<String, Double> getQueryTerms() {
		return queryTerms;
	}
	
	/*public static void main(String[] args)
	{
		String query = "author:(brian OR richard) AND apple orange banana place:(paris OR washington)";
		Query query2 = new Query(query);
		query = query2.toString();
		System.out.println(query);
	}*/
}
