package edu.buffalo.cse.irf14.analysis.TokenFilters;

import edu.buffalo.cse.irf14.analysis.TokenFilter;
import edu.buffalo.cse.irf14.analysis.TokenFilterConstants;
import edu.buffalo.cse.irf14.analysis.TokenStream;
import edu.buffalo.cse.irf14.analysis.TokenizerException;

public class SymbolFilter extends TokenFilter {

	public SymbolFilter(TokenStream stream) {
		super(stream);
	}

	@Override
	public void applyFilter() throws TokenizerException {

		String str = getCurrentStream().getCurrent().getTermText();
		try{
			if( null!=str && !str.isEmpty() && TokenFilterConstants.patternCom.matcher(str).matches() ){

				if(str.equals("'em")) {
					str = "them";
				}
				while (str.endsWith(".") || str.endsWith("?") || str.endsWith("!") || str.endsWith(",") || str.endsWith("'") || str.endsWith("\"")
						|| str.endsWith(";")){
					str = str.substring(0, str.length()-1);  
				}
				while ( str.startsWith(".") || str.startsWith(",") || str.startsWith("\"") || str.startsWith(";") ){
					str = str.substring(1, str.length());  
				}

				if( (str.startsWith("(") || str.startsWith("<") ) && (str.endsWith(")") || str.endsWith(">"))) {
					str = str.substring(1, str.length()-1);
				}

				if( TokenFilterConstants.patternHyphen1.matcher(str).matches() || TokenFilterConstants.patternHyphen2.matcher(str).matches()){
					str = str.replaceAll("-"," ");
				} else if( str.matches(".*[\\-]+") ||  str.matches("[\\-]+.*") ) {
					str = str.replaceAll("-", "");
				}
				if(str.equals("let's")) {
					str = "let us";
				}
				else if(str.endsWith("'s")) {
					if(str.matches("[A-Z]\\w*'s")){
						str=str.replace("'s", "");
					} else {
						str = str.replace("'s", " is");
					}
				}
				else if(str.endsWith("s'")) {
					str = str.replace("s'", "s");
				}
				else if(str.equals("can't")){
					str = "cannot";
				}
				else if(str.equals("shan't")){
					str = "shall not";
				}
				else if(str.equals("won't")){
					str = "will not";
				}
				else if(str.endsWith("n't")) {
					str = str.replace("n't", " not");
				}
				else if(str.endsWith("'ve")) {
					str = str.replace("'ve", " have");
				}
				else if(str.endsWith("'re")) {
					str = str.replace("'re", " are");
				}
				else if(str.endsWith("'d")) {
					str = str.replace("'d", " would");
				}
				else if(str.endsWith("'ll")) {
					str = str.replace("'ll", " will");
				}
				else if(str.endsWith("'m")) {
					str = str.replace("'m", " am");
				}
				str = str.replace("'", "");
				getCurrentStream().getCurrent().setTermText( str );
				//getCurrentStream().getCurrent().setTermBuffer(str.toCharArray());    //modify term buffer

			}

		} catch(Exception exception) {

		}
	}


}
