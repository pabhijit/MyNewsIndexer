/**
 * 
 */
package edu.buffalo.cse.irf14;

import java.io.File;
import java.io.PrintStream;
import java.util.Calendar;

import edu.buffalo.cse.irf14.document.Document;
import edu.buffalo.cse.irf14.document.Parser;
import edu.buffalo.cse.irf14.document.ParserException;
import edu.buffalo.cse.irf14.index.IndexWriter;
import edu.buffalo.cse.irf14.index.IndexerException;

/**
 * @author nikhillo
 *
 */
public class Runner {

	
	private static final String BASE_USER_DIR = 
			System.getProperty("user.dir") + File.separatorChar + "training" ;
	private static final String INDEX_USER_DIR = 
			System.getProperty("user.dir");
	/**
	 * 
	 */
	public Runner() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Long start = System.currentTimeMillis();
		String ipDir = BASE_USER_DIR;
		String indexDir = INDEX_USER_DIR;
		//more? idk!
		
		File ipDirectory = new File(ipDir);
		String[] catDirectories = ipDirectory.list();
		
		String[] files;
		File dir;
		int i=0;
		Document d = null;
		IndexWriter writer = new IndexWriter(indexDir);
		
		try {
			for (String cat : catDirectories) {
				
				dir = new File(ipDir+ File.separator+ cat);
				files = dir.list();
				
				if (files == null)
					continue;
				
				for (String f : files) {
					try {
						i++;
						d = Parser.parse(dir.getAbsolutePath() + File.separator +f);
						writer.addDocument(d);
					} catch (ParserException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
					
				}
				
			}
			writer.close();
			System.out.println("Time taken to write the corpus = "+(System.currentTimeMillis()-start)/1000 + " seconds");
			
			PrintStream stream = new PrintStream(System.out);
			SearchRunner runner = new SearchRunner(indexDir, ipDir, 'Q', stream);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
