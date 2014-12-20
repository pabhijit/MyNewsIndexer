package edu.buffalo.cse.irf14.analysis.TokenFilters;

import edu.buffalo.cse.irf14.analysis.TokenFilter;
import edu.buffalo.cse.irf14.analysis.TokenFilterConstants;
import edu.buffalo.cse.irf14.analysis.TokenStream;
import edu.buffalo.cse.irf14.analysis.TokenizerException;

public class SpecialCharsFilter extends TokenFilter {

	public SpecialCharsFilter(TokenStream stream) {
		super(stream);
		// TODO Auto-generated constructor stub
	}

	public void applyFilter() throws TokenizerException
	{
		try
		{
			String str = getCurrentStream().getCurrent().getTermText();
			if(str != null && str.length()!=0) {
				str = TokenFilterConstants.patternWS.matcher(str).replaceAll("").replaceAll("_", "").replaceAll("$", "");
				if( str.indexOf("-") !=0 && !TokenFilterConstants.patternDHD.matcher(str).matches() ) {
					str = str.replaceAll("-", "");
				}
				//if it is in time format.
				if( !TokenFilterConstants.patternTimeSplCh.matcher(str).matches() ){
					str = str.replace(":", "");
				}
				getCurrentStream().getCurrent().setTermText(str);
			}
		}
		catch(Exception e){

		}
	}

}
