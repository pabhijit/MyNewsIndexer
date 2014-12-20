package edu.buffalo.cse.irf14.analysis.TokenFilters;

import edu.buffalo.cse.irf14.analysis.TokenFilter;
import edu.buffalo.cse.irf14.analysis.TokenStream;
import edu.buffalo.cse.irf14.analysis.TokenizerException;

public class AccentFilter extends TokenFilter {

	public AccentFilter(TokenStream stream) {
		super(stream);
		// TODO Auto-generated constructor stub

	}
	public void applyFilter() throws TokenizerException {

		try{
			String string=getCurrentStream().getCurrent().getTermText();
			String[][] foreignToEnglish= {{"ÃƒÂ´","o"},{"ÃƒÂ©","e"},{"ÃƒÂ¨","e"},{"ÃƒÂ»","u"},{"ÃƒÂ¼","u"},{"ÃƒÂ«","e"},{"Ã�Â°ÃŒâ‚¬","a"},{"Ã�Â°Ã‘","a"},
					{"â‚¬","p"},{"ÃƒÂ ","a"},{"Ãƒ","a"},{"Ã€","a"},{"Ã�","a"},{"Ã‚","a"},{"Ãƒ","a"},{"Ã„","a"},{"Ã…","a"},{"Ã†","ae"},{"Ã‡","C"},
					{"Ãˆ","E"},{"Ã‰","E"},{"ÃŠ","E"},{"Ã‹","E"},{"ÃŒ","i"},{"Ã�","i"},{"ÃŽ","i"},{"Ã�","i"},{"Ä²","ij"},{"Ã�","d"},{"Â¢",""},
					{"Ã‘","n"},{"Ã’","o"},{"Ã“","o"},{"Ã”","o"},{"Ã•","o"},{"Ã–","o"},{"Ã˜","o"},{"Å’","oe"},{"Ãž","th"},{"Ã™","u"},{"Ãš","u"},
					{"Ã›","u"},{"Ãœ","u"},{"Ã�","y"},{"Å¸","y"},{"Å¸","y"},
					{"Ã ","a"},{"Ã¡","a"},{"Ã¢","a"},{"Ã£","a"},{"Ã¤","a"},{"Ã¥","a"},{"Ã¦","ae"},{"Ã§","c"},{"Ã¨","e"},{"Ã©","e"},{"Ãª","e"},
					{"Ã«","e"},{"Ã¬","i"},{"Ã­","i"},{"Ã®","i"},{"Ã¯","i"},{"Ä³","ij"},{"Ã°","d"},{"Ã±","n"},{"Ã²","o"},{"Ã³","o"},{"Ã´","o"},
					{"Ãµ","o"},{"Ã¶","o"},{"Ã¸","o"},{"Å“","oe"},{"ÃŸ","ss"},{"Ã¾","th"},{"Ã¹","u"},{"Ãº","u"},{"Ã»","u"},{"Ã¼","u"},{"Ã½","y"},
					{"Ã¿","y"},{"ï¬�","fi"},{"ï¬‚","fl"}};
			int iCounter=0;

			for(iCounter=0;iCounter<foreignToEnglish.length;iCounter++)
			{
				string = string.replaceAll(foreignToEnglish[iCounter][0], foreignToEnglish[iCounter][1]);
			}
			getCurrentStream().getCurrent().setTermText(string);
			//getCurrentStream().getCurrent().setTermBuffer(string.toCharArray());

		} catch ( Exception exception ) {

		} 
	}

}
