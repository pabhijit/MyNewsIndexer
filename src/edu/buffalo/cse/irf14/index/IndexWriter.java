/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import edu.buffalo.cse.irf14.analysis.Analyzer;
import edu.buffalo.cse.irf14.analysis.AnalyzerFactory;
import edu.buffalo.cse.irf14.analysis.TokenStream;
import edu.buffalo.cse.irf14.analysis.Tokenizer;
import edu.buffalo.cse.irf14.analysis.TokenizerException;
import edu.buffalo.cse.irf14.document.Document;
import edu.buffalo.cse.irf14.document.FieldNames;

/**
 * @author nikhillo
 * Class responsible for writing indexes to disk
 */
public class IndexWriter {

	private String indexDir;

	/*Dictionaries*/
	private Dictionary documents = new Dictionary(); 
	private Dictionary categories = new Dictionary();
	private Dictionary terms = new Dictionary();
	private Dictionary authors = new Dictionary();
	private Dictionary places = new Dictionary();

	/*Indexes*/
	private Index authorIndex = new Index();
	private Index categoryIndex = new Index();
	private Index placeIndex = new Index();
	private TermIndex termIndex = new TermIndex();

	/**
	 * Default constructor
	 * @param indexDir : The root directory to be sued for indexing
	 */
	public IndexWriter(String indexDir) {
		//TODO : YOU MUST IMPLEMENT THIS
		this.indexDir = indexDir;
	}

	/**
	 * Method to add the given Document to the index
	 * This method should take care of reading the filed values, passing
	 * them through corresponding analyzers and then indexing the results
	 * for each indexable field within the document. 
	 * @param d : The Document to be added
	 * @throws IndexerException : In case any error occurs
	 */
	public void addDocument(Document d) throws IndexerException {
		//TODO : YOU MUST IMPLEMENT THIS

		AnalyzerFactory analyzerFactory = AnalyzerFactory.getInstance();
		Tokenizer tokenizer = new Tokenizer();
		//TokenStream mainStream = new TokenStream();
		TokenStream streamTemp = null;
		Analyzer analyzer = null;
		String strField[] = null;
		String strFileId[] = null;
		String strTemp = null;
		try {
			if( (strFileId = d.getField( FieldNames.FILEID )) != null ){
				documents.addToDictionary( strFileId[0] );
			}
			if( (strField = d.getField( FieldNames.CATEGORY )) != null ) {
				categories.addToDictionary(strField[0]);
				categoryIndex.addToIndex(categories.get(strField[0]), documents.get(strFileId[0]));
			}
			if( (strField = d.getField( FieldNames.PLACE )) != null ) {
				if(!strField[0].isEmpty()){
					Tokenizer tk = new Tokenizer(",");
					streamTemp = tk.consume( strField[0] );
					if( streamTemp!= null){
						analyzer = analyzerFactory.getAnalyzerForField( FieldNames.PLACE , streamTemp );
						while( analyzer.increment() ){

						}
						streamTemp = analyzer.getStream();
						while (streamTemp.hasNext()) {
							strTemp = streamTemp.next().getTermText();
							places.addToDictionary( strTemp );
							placeIndex.addToIndex(places.get( strTemp ), documents.get( strFileId[0] ) );
						}
					}
				}
			}
			if( (strField = d.getField( FieldNames.NEWSDATE )) != null ){
				if(!strField[0].isEmpty()){
					streamTemp = tokenizer.consume( strField[0] );
					analyzer = analyzerFactory.getAnalyzerForField( FieldNames.NEWSDATE , streamTemp );
					while( analyzer.increment() ){

					}
					streamTemp = analyzer.getStream();
					while( streamTemp.hasNext() ) {
						strTemp = streamTemp.next().getTermText();
						terms.addToDictionary(strTemp);                      //adding to term dictionary
						termIndex.addToPostings(terms.get(strTemp), documents.get(strFileId[0]));  //adding to term Index
					}
				}
			}
			if( (strField = d.getField( FieldNames.AUTHOR )) != null ) {
				if(!strField[0].isEmpty()){
					streamTemp = tokenizer.consume( strField[0] );
					analyzer = analyzerFactory.getAnalyzerForField( FieldNames.AUTHOR , streamTemp );
					while( analyzer.increment() ){

					}
					streamTemp = analyzer.getStream();
					while( streamTemp.hasNext() ) {
						strTemp = streamTemp.next().getTermText();
						authors.addToDictionary( strTemp );
						authorIndex.addToIndex(authors.get( strTemp ), documents.get( strFileId[0] ) );
					}
				}
			} 
			if( (strField = d.getField( FieldNames.AUTHORORG )) != null ) {
				if(!strField[0].isEmpty()){
					streamTemp = tokenizer.consume( strField[0] );
					analyzer = analyzerFactory.getAnalyzerForField( FieldNames.AUTHORORG , streamTemp);
					while( analyzer.increment() ){

					}
					streamTemp = analyzer.getStream();
					while( streamTemp.hasNext() ) {
						strTemp = streamTemp.next().getTermText();
						authors.addToDictionary(strTemp);
						authorIndex.addToIndex(authors.get(strTemp), documents.get(strFileId[0]));
					}
				}
			} 
			if( (strField = d.getField( FieldNames.TITLE )) != null ) {
				streamTemp = tokenizer.consume( strField[0] );
				analyzer = analyzerFactory.getAnalyzerForField( FieldNames.TITLE , streamTemp);
				while( analyzer.increment() ){

				}
				streamTemp = analyzer.getStream();
				while( streamTemp.hasNext() ) {
					strTemp = streamTemp.next().getTermText();
					terms.addToDictionary(strTemp);                      //adding to term dictionary
					termIndex.addToPostings(terms.get(strTemp), documents.get(strFileId[0]));  //adding to term Index
				}
			} 
			if( (strField = d.getField( FieldNames.CONTENT )) != null ) {
				streamTemp = tokenizer.consume( strField[0] );
				analyzer = analyzerFactory.getAnalyzerForField( FieldNames.CONTENT , streamTemp);
				while( analyzer.increment() ){

				}
				streamTemp = analyzer.getStream();
				while( streamTemp.hasNext() ) {
					strTemp = streamTemp.next().getTermText();
					terms.addToDictionary(strTemp);                      //adding to term dictionary
					termIndex.addToPostings(terms.get(strTemp), documents.get(strFileId[0]));  //adding to term Index
				}
			} 

		}catch (TokenizerException e) {
			System.err.println("Tokenizer Exception has occured");
		}

	}

	/*Files*/
	private final String AUTHOR_FILENAME = "AuthorIndex.dat";
	private final String TERM_FILENAME = "TermIndex.dat";
	private final String PLACE_FILENAME = "PlaceIndex.dat";
	private final String CATEGORY_FILENAME = "CategoryIndex.dat";
	private final String AUTHOR_DICT_FILENAME = "AuthorDictionary.dat";
	private final String TERM_DICT_FILENAME = "TermDictionary.dat";
	private final String PLACE_DICT_FILENAME = "PlaceDictionary.dat";
	private final String CATEGORY_DICT_FILENAME = "CategoryDictionary.dat";
	private final String DOCUMENT_DICT_FILENAME = "DocumentDictionary.dat";

	public void writeIndexToDisk(IndexType indexType) {

		String fileName = null;
		if(indexType.equals((IndexType.AUTHOR))){
			fileName = indexDir.concat(File.separator + AUTHOR_FILENAME);
		} else if(indexType.equals(IndexType.PLACE)){
			fileName = indexDir.concat(File.separator + PLACE_FILENAME);
		} else if(indexType.equals(IndexType.CATEGORY)){
			fileName = indexDir.concat(File.separator + CATEGORY_FILENAME);
		} else{
			fileName = indexDir.concat(File.separator + TERM_FILENAME);
		}

		try {
			FileOutputStream fout = new FileOutputStream(fileName);
			BufferedWriter bWriter = new BufferedWriter(new OutputStreamWriter(fout));

			if( fileName.contains("Term")) {
				for ( Entry<Integer, HashMap<Integer, Integer>> entry : termIndex.getTermPostings().entrySet() ) {
					bWriter.write(entry.getKey() + "#");
					HashMap<Integer, Integer> postings = (HashMap<Integer, Integer>) entry.getValue();
					bWriter.write(postings.size() + "#");
					int iCount = 0;
					for( Entry<Integer, Integer> docIds: postings.entrySet()) {
						bWriter.write(docIds.getKey() + ":" + docIds.getValue());
						iCount = iCount + 1;
						if( iCount < postings.size() ) {
							bWriter.write(":");
						} else {
							bWriter.write("\n");
						}
					}
				}
			} else {
				Index indexTemp = null;
				if(fileName.contains("Author")) {
					indexTemp = authorIndex;
				} else if(fileName.contains("Category")) {
					indexTemp = categoryIndex;
				} else {
					indexTemp = placeIndex;
				}

				for ( Entry<Integer, List<Integer>> entry : indexTemp.getIndexes().entrySet() ) {
					bWriter.write(entry.getKey() + "#" );
					List<Integer> docIds = entry.getValue();
					int iCount = 0;
					for( Integer docId: docIds) {
						bWriter.write( String.valueOf( docId ) );
						iCount = iCount + 1;
						if( iCount < docIds.size() ) {
							bWriter.write(":");
						} else {
							bWriter.write("\n");
						}
					}
				}
			}
			bWriter.close();
			fout.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException ioe) {
			// TODO Auto-generated catch block
			ioe.printStackTrace();
		} finally{

		}

	}

	public void writeDictionaryToDisk(String fieldType) {

		String fileName = null;
		Dictionary tempDictionary = new Dictionary();
		if(fieldType.equalsIgnoreCase(String.valueOf(FieldNames.AUTHOR))){
			fileName = indexDir.concat(File.separator + AUTHOR_DICT_FILENAME);
			tempDictionary = authors;
		} else if(fieldType.equalsIgnoreCase(String.valueOf(FieldNames.PLACE))){
			fileName = indexDir.concat(File.separator + PLACE_DICT_FILENAME);
			tempDictionary = places;
		} else if(fieldType.equalsIgnoreCase(String.valueOf(FieldNames.CATEGORY))){
			fileName = indexDir.concat(File.separator + CATEGORY_DICT_FILENAME);
			tempDictionary = categories;
		} else if(fieldType.equalsIgnoreCase(String.valueOf(FieldNames.FILEID))){
			fileName = indexDir.concat(File.separator + DOCUMENT_DICT_FILENAME);
			tempDictionary = documents;
		}else{
			fileName = indexDir.concat(File.separator + TERM_DICT_FILENAME);
			tempDictionary = terms;
		}

		try {
			FileOutputStream fout = new FileOutputStream(fileName);
			BufferedWriter bWriter = new BufferedWriter(new OutputStreamWriter(fout));

			for ( Entry<String, Integer> entry : tempDictionary.getDictionary().entrySet() ) {
				bWriter.write(entry.getKey().trim() +":"+entry.getValue());
				bWriter.write("\n");
			}

			bWriter.close();
			fout.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally{

		}

	}

	/**
	 * Method that indicates that all open resources must be closed
	 * and cleaned and that the entire indexing operation has been completed.
	 * @throws IndexerException : In case any error occurs
	 */
	public void close() throws IndexerException {
		//TODO
		try {
			String dictionaries[] = {"AUTHOR","TERM","PLACE","CATEGORY","FILEID"};

			for(String s : dictionaries) {
				writeDictionaryToDisk(s);
			}
			for(IndexType s : IndexType.values()) {
				writeIndexToDisk(s);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
