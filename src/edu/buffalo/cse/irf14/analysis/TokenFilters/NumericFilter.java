package edu.buffalo.cse.irf14.analysis.TokenFilters;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import edu.buffalo.cse.irf14.analysis.TokenFilter;
import edu.buffalo.cse.irf14.analysis.TokenFilterConstants;
import edu.buffalo.cse.irf14.analysis.TokenStream;
import edu.buffalo.cse.irf14.analysis.TokenizerException;

public class NumericFilter extends TokenFilter {

	public NumericFilter(TokenStream stream) {
		super(stream);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void applyFilter() throws TokenizerException {

		try {
			String currentToken = getCurrentStream().getCurrent().getTermText();
			if(null != currentToken) {
				if (!(TokenFilterConstants.patternDate.matcher(currentToken).matches() || TokenFilterConstants.patternTimeNum.matcher(currentToken).matches() || 
						TokenFilterConstants.patternAlpha.matcher(currentToken).matches())){
					if(isNumber(currentToken)) {
						getCurrentStream().remove();
					} else {
						getCurrentStream().getCurrent().setTermText(currentToken.replaceAll("[\\d]", "").replace(".", ""));
						if(getCurrentStream().getCurrent().getTermText().length() == 0) {
							getCurrentStream().remove();
						}
					}
				}
			} 

		}catch( Exception e ) {

		}
	}

	public static boolean isDate(String str) 
	{
		final String DATE_FORMAT = "yyyyMMdd";
		try {
			DateFormat dFormat = new SimpleDateFormat(DATE_FORMAT);
			dFormat.parse(str);
			return true;
		} catch (ParseException e) {
			return false;
		}
	}

	public static boolean isNumber(String str) 
	{
		try {
			Double.parseDouble(str.replace(",", ""));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
