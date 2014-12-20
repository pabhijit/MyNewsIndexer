/**
 * 
 */
package edu.buffalo.cse.irf14.document;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * @author nikhillo
 * Class that parses a given file into a Document
 */
public class Parser {
	
	private static Pattern patternHypen = Pattern.compile("(?i)(.*)( - )(.*)");
	private static Pattern patternAuthor =Pattern.compile("(?i)(.*<author>)(.*)(</author>.*)");
	private static Pattern patternComma = Pattern.compile("(?i)(.*)(,)(.*)");

	/**
	 * Static method to parse the given file into the Document object
	 * @param filename : The fully qualified filename to be parsed
	 * @return The parsed and fully loaded Document object
	 * @throws ParserException In case any error occurs during parsing
	 */
	public static Document parse(String filename) throws ParserException {
		// TODO YOU MUST IMPLEMENT THIS

		String line = null;
		Document document = null;
		int lCounter = 0;
		StringBuilder content = new StringBuilder();
		//TODO: To be put in constants file
		
		try {
			if( null != filename && !filename.trim().isEmpty() ) {
				document = new Document();

				String filenameSplit[] = filename.split( Pattern.quote( String.valueOf( File.separatorChar ) ) ); //TODO: Not sure
				document.setField( FieldNames.FILEID, filenameSplit[filenameSplit.length-1] );   
				document.setField( FieldNames.CATEGORY, filenameSplit[filenameSplit.length-2] );

				File file = new File( filename );
				BufferedReader reader =  new BufferedReader( new FileReader( file ) );

				try{
					while ( ( line = reader.readLine() ) != null ) {
						if ( !line.trim().isEmpty() ) {
							if ( lCounter == 0 ) {
								document.setField(FieldNames.TITLE, line.trim());
								lCounter = lCounter + 1;
							} else if ( lCounter == 1 ) {
								if ( patternAuthor.matcher(line).matches() ) {	
									String authorDetails = patternAuthor.matcher(line).replaceAll("$2").replaceAll("[Bb][yY] ", "").trim();
									if ( authorDetails.contains( "," ) ) {
										document.setField( FieldNames.AUTHOR, patternComma.matcher(authorDetails).replaceAll("$1").trim()  );
										document.setField( FieldNames.AUTHORORG, patternComma.matcher(authorDetails).replaceAll("$3").trim()  );
									} else {
										document.setField( FieldNames.AUTHOR, authorDetails );
									}			
								}
								else if ( line.matches( ".*,.*-.*" ) ) {
									if ( patternHypen.matcher(line).matches() ) {
										String placeNDate = patternHypen.matcher(line).replaceAll("$1").trim();
										content.append( patternHypen.matcher(line).replaceAll("$3").trim() );
										content.append(" ");
										document.setField( FieldNames.PLACE, patternComma.matcher(placeNDate).replaceAll("$1").trim() );
										document.setField (FieldNames.NEWSDATE, patternComma.matcher(placeNDate).replaceAll("$3").trim() );
									}
								}
								else {
									lCounter+=1;
									content.append(line);
									content.append(" ");
								}
							} else if ( lCounter == 2 ) {
								content.append(line);
							}

						}

					}
					if( null != content && content.toString().length() !=0 ) {
						document.setField(FieldNames.CONTENT, content.toString());
					}
				} finally {
					reader.close();
				}
			} else {
				throw new ParserException();
			}
		} catch ( IOException iOException ) {
			throw new ParserException();
		} catch ( Exception Exception ) {
			throw new ParserException();
		} 

		return document;
	}

}
