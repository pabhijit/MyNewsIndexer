package edu.buffalo.cse.irf14.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import edu.buffalo.cse.irf14.SearchRunner;
import edu.buffalo.cse.irf14.analysis.Analyzer;
import edu.buffalo.cse.irf14.analysis.AnalyzerFactory;
import edu.buffalo.cse.irf14.analysis.TokenStream;
import edu.buffalo.cse.irf14.analysis.Tokenizer;
import edu.buffalo.cse.irf14.analysis.TokenizerException;
import edu.buffalo.cse.irf14.document.FieldNames;
import edu.buffalo.cse.irf14.index.IndexReader;
import edu.buffalo.cse.irf14.index.IndexType;

public class IndexSearcher 
{

	private static Stack<Set<String>> termStack = new Stack<Set<String>>();
	private static List<QueryTermIndex> resultList = new ArrayList<QueryTermIndex>();
	private static Set<String> resultDocs = new HashSet<String>();
	private static Map<String,Integer> postings;
	private static Set<String> postingsIndex;
	public static double totalNumberOfDocs;
	private static int isNot=0;
	private static IndexReader reader = new IndexReader(SearchRunner.indexDir, null);

	public static List<QueryTermIndex> searchIndex(String query) 
	{
		try
		{
			totalNumberOfDocs = reader.getTotalValueTerms();
			query = CommonUtil.convertToPostfix(query);
			String terms[] = query.split( " " );
			for( String iterStr : terms ) 
			{
				if( iterStr.contains( "Term:" ) ||  iterStr.contains( "term:" ) ) 
				{
					String tempTerm = iterStr.replace("Term:", "").replace("term:", "");
					if(tempTerm.contains("<"))
					{
						isNot=1;
						tempTerm = tempTerm.replace("<", "").replace(">", "");	
					}
					else
					{
						if(isNot==1) {
							isNot++;
						}
					}
					Map<String,Integer> tempPostings = new HashMap<String, Integer>();
					postings = reader.getPostings( tempTerm );
					if((tempPostings = reader.getPostings(tempTerm.toUpperCase())) != null ) {
						postings.putAll( tempPostings );
					}
					if((tempPostings = reader.getPostings(tempTerm.toLowerCase())) != null ) {
						postings.putAll( tempPostings );
					}
					if((tempPostings = reader.getPostings(tempTerm.replace(tempTerm.substring(0, 1), tempTerm.substring(0, 1).toUpperCase()))) != null ) {
						postings.putAll( tempPostings );
					}
					tempTerm = getAnalyzedTerm( tempTerm );
					if((tempPostings = reader.getPostings( tempTerm )) != null ) {
						postings.putAll( tempPostings );
					}
					if( postings != null ) 
					{
						QueryTermIndex term = new QueryTermIndex( iterStr, postings );
						resultList.add(term);
						termStack.push(postings.keySet());
					}
				} 
				else if( iterStr.contains("Category:") || iterStr.contains("Place:") || iterStr.contains("Author:") ||
						iterStr.contains("category:") || iterStr.contains("place:") || iterStr.contains("author:") ) 
				{
					String tempTerm = null;
					if(iterStr.contains("<")) 
					{
						isNot=1;
						iterStr=iterStr.replace("<", "");
						iterStr=iterStr.replace(">", "");
					}
					else
					{
						if(isNot==1)
							isNot++;
					}

					if( iterStr.contains("Category:") ||  iterStr.contains("category:") ){
						tempTerm = iterStr.replace("Category:", "").replace("category:", "");
						postingsIndex=reader.getIndexPostings(tempTerm, IndexType.CATEGORY );
					} else if( iterStr.contains("Place:") || iterStr.contains("place:") ) {
						tempTerm = iterStr.replace("Place:", "").replace("place:", "");
						postingsIndex=reader.getIndexPostings(tempTerm, IndexType.PLACE );
					} else if( iterStr.contains("Author:")){
						tempTerm = iterStr.replace("Author:", "").replace("Author:", "");
						postingsIndex=reader.getIndexPostings(tempTerm, IndexType.AUTHOR );
					}
					if(postingsIndex!=null)
					{
						Map<String, Integer> tempMap = new HashMap<String, Integer>();
						for( String str: postingsIndex ) {
							tempMap.put(str, 1);
						}
						QueryTermIndex term = new QueryTermIndex( iterStr, tempMap );
						resultList.add(term);
						termStack.push(postingsIndex);
					}
				}
				else if( CommonUtil.isOperator(iterStr) )
				{
					performOp(iterStr);
				}
			}
			if( !termStack.isEmpty() ) {
				resultDocs.addAll( termStack.pop() );
			}
			for( QueryTermIndex tempIndex : resultList )
			{
				Set<String> docList = tempIndex.getPostings().keySet();
				for(String doc : docList)
				{
					if( !resultDocs.contains(doc) ) 
					{
						tempIndex.getPostings().keySet().remove(doc);
					}
				}
			}
			return resultList;
		}
		catch( Exception e ) 
		{
			return resultList;
		}
	}

	private static void performOp(String iterStr) 
	{
		int iCount = 0;
		List<Set<String>> passTerm = new ArrayList<Set<String>>();
		try
		{
			while(!termStack.isEmpty() && iCount < 2) 
			{
				passTerm.add(termStack.pop());
				iCount = iCount + 1;
			}
			if(iterStr.equals("AND")) 
			{
				performAnd(passTerm);
			}
			else
			{
				performOr(passTerm);
			}
		}
		catch( Exception e )
		{

		}	
	}

	public static void performAnd(List<Set<String>> termList)
	{

		if(termList.size() > 1) 
		{
			Set<String> termA = termList.get(0);
			Set<String> termB = termList.get(1);

			if(isNot==0 || isNot>2)
			{
				Set<String> notInA = new HashSet<String>(termB);
				notInA.removeAll(termA);
				termB.removeAll(notInA);
				termStack.push(termB);
				//resultDocs.addAll( termB );
				if(isNot>2)
					isNot=isNot-2;
			}
			else
			{
				if(isNot==1)
				{
					Set<String> notTerm=new HashSet<String>(termA);
					termB.removeAll(notTerm);
					termStack.push(termB);
				}
				else
				{
					Set<String> notTerm=new HashSet<String>(termB);
					termA.removeAll(notTerm);
					termStack.push(termA);
				}
				isNot=0;
			}
		}

	}

	public static void performOr(List<Set<String>> termList)
	{
		if(termList.size() > 1) 
		{
			Set<String> termA = termList.get(0);
			Set<String> termB = termList.get(1);

			if(isNot==0 || isNot>2)
			{
				Set<String> notInA = new HashSet<String>(termB);
				notInA.addAll(termA);
				termStack.push(notInA);
				//resultDocs.addAll(notInA);
				if(isNot>2)
					isNot=isNot-2;
			}
			else
			{
				if(isNot==1)
				{
					Set<String> notTerm=new HashSet<String>(termA);
					termB.removeAll(notTerm);
					termStack.push(termB);
				}
				else
				{
					Set<String> notTerm=new HashSet<String>(termB);
					termA.removeAll(notTerm);
					termStack.push(termA);
				}
				isNot=0;
			}

		}
	}



	//Source: IndexerTest class
	private static String getAnalyzedTerm(String string) 
	{
		Tokenizer tknizer = new Tokenizer();
		AnalyzerFactory fact = AnalyzerFactory.getInstance();
		try 
		{
			if(string != null && string.length() != 0) {
				TokenStream stream = tknizer.consume(string);
				Analyzer analyzer = fact.getAnalyzerForField(FieldNames.CONTENT, stream);

				while (analyzer.increment()) 
				{

				}
				stream = analyzer.getStream();
				stream.reset();
				return stream.next().toString();
			}
		} 
		catch (TokenizerException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static List<String> getWildCardTerms(String term) {
		return reader.getWildCardTerms(term);
	}
}
