package edu.buffalo.cse.irf14.analysis.TokenFilters;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import edu.buffalo.cse.irf14.analysis.TokenFilter;
import edu.buffalo.cse.irf14.analysis.TokenFilterConstants;
import edu.buffalo.cse.irf14.analysis.TokenStream;
import edu.buffalo.cse.irf14.analysis.TokenizerException;

public class DateFilter extends TokenFilter {

	public DateFilter(TokenStream stream) {
		super(stream);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void applyFilter() throws TokenizerException {
		// TODO Auto-generated method stub

		try {
			String currentToken = getCurrentStream().getCurrent().getTermText();
			String trailingChar = null;
			if( TokenFilterConstants.patternHyphen.matcher(currentToken).matches() ){
				if( (trailingChar = hasTrailingChar(currentToken)) != null ){
					currentToken = currentToken.replace(trailingChar, "");
				}
				String currentTokenSplits[] = currentToken.split("-");
				if(Integer.valueOf(currentTokenSplits[0])>=1900 && Integer.valueOf(currentTokenSplits[0])<=2100){
					StringBuffer strNewDate = new StringBuffer();
					strNewDate.append(currentTokenSplits[0]);
					strNewDate.append("0101-");
					if(Integer.valueOf(currentTokenSplits[0].substring(2, 4))<Integer.valueOf(currentTokenSplits[1])){
						strNewDate.append(currentTokenSplits[0].substring(0, 2));
					} else {
						strNewDate.append(String.valueOf(Integer.valueOf(currentTokenSplits[0].substring(0, 2))+1));
					}
					strNewDate.append(currentTokenSplits[1]);
					strNewDate.append("0101"+trailingChar);
					getCurrentStream().getCurrent().setTermText(strNewDate.toString());
				}
			}else if( TokenFilterConstants.patternBC.matcher(currentToken).matches() ){
				StringBuffer strBuffer = new StringBuffer();
				strBuffer.append("-");
				for(int i=0;i<(4-currentToken.replaceAll("BC", "").length());i++){
					strBuffer.append("0");
				}
				strBuffer.append(currentToken.replaceAll("BC", "")+"0101");
				getCurrentStream().getCurrent().setTermText(strBuffer.toString());
			}else if(currentToken.matches("BC.*")){
				if( (trailingChar = hasTrailingChar(currentToken)) != null ){
					currentToken = currentToken.replace(trailingChar, "");
				}
				String previousToken = getCurrentStream().previous().getTermText();
				if( TokenFilterConstants.patternNumber.matcher(previousToken).matches() ) {
					StringBuffer strBuffer = new StringBuffer();
					strBuffer.append("-");
					for(int i=0;i<(4-getCurrentStream().getCurrent().getTermText().length());i++){
						strBuffer.append("0");
					}
					strBuffer.append(getCurrentStream().getCurrent().getTermText()+"0101");
					getCurrentStream().remove();
					getCurrentStream().next().setTermText(strBuffer.toString());
				} else {
					getCurrentStream().next();
				}
			}else if( TokenFilterConstants.patternAD.matcher(currentToken).matches() ) {
				if( (trailingChar = hasTrailingChar(currentToken)) != null ){
					currentToken = currentToken.replace(trailingChar, "");
				}
				StringBuffer strBuffer = new StringBuffer();
				for(int i=0;i<(4-currentToken.replaceAll("AD", "").length());i++){
					strBuffer.append("0");
				}
				strBuffer.append(currentToken.replaceAll("AD", "")+"0101");
				if (trailingChar != null) {
					strBuffer.append(trailingChar);
				}
				getCurrentStream().getCurrent().setTermText(strBuffer.toString());
			}else if(currentToken.matches("AD.*")){
				if( TokenFilterConstants.patternNumber.matcher(getCurrentStream().previous().getTermText()).matches() ) {
					StringBuffer strBuffer = new StringBuffer();
					for(int i=0;i<(4-getCurrentStream().getCurrent().getTermText().length());i++){
						strBuffer.append("0");
					}
					strBuffer.append(getCurrentStream().getCurrent().getTermText()+"0101");
					getCurrentStream().remove();
					getCurrentStream().next().setTermText(strBuffer.toString());
				} else {
					getCurrentStream().next();
				}
			}else if( TokenFilterConstants.patternTime.matcher( currentToken ).matches() ) {
				if( (trailingChar = hasTrailingChar(currentToken)) != null ){
					currentToken.replace(trailingChar, "");
				}
				currentToken = new SimpleDateFormat("HH:mm:ss").format(new SimpleDateFormat("hh:mma").parse(currentToken));
				if(trailingChar != null) {
					currentToken = currentToken+trailingChar;
				}
				getCurrentStream().getCurrent().setTermText( currentToken );
			} else if( TokenFilterConstants.patternTimeDD.matcher(currentToken).matches() ) {
				String tokenNext = getCurrentStream().next().getTermText();
				if( (trailingChar = hasTrailingChar(tokenNext)) != null ){
					tokenNext = tokenNext.substring(0,tokenNext.length()-1);
				}
				if(tokenNext.equalsIgnoreCase("am") || tokenNext.equalsIgnoreCase("pm")){
					currentToken = new SimpleDateFormat("HH:mm:ss").format(new SimpleDateFormat("hh:mma").parse(currentToken+tokenNext));
					getCurrentStream().remove();
					if(trailingChar != null) {
						currentToken = currentToken+trailingChar;
					}
					getCurrentStream().previous().setTermText( currentToken );
				} else {
					getCurrentStream().previous();
				}
			}else {
				String date=null; String month=null; String year=null;
				String monthString[] = new DateFormatSymbols().getMonths();
				List<String> months = Arrays.asList(monthString);
				//check if the token is digit or a month.
				if( TokenFilterConstants.patternNumber.matcher(currentToken).matches() && Integer.valueOf(currentToken)>=1 && Integer.valueOf(currentToken)<=31 ){
					//Fetch previous and next here if required.
					if(months.contains(getCurrentStream().next().getTermText())){
						date = currentToken;
						month= getCurrentStream().getCurrent().getTermText();
						getCurrentStream().remove();
						String next = getCurrentStream().next().getTermText();
						if ( TokenFilterConstants.patternNumber.matcher(next).matches() && Integer.valueOf(next) >= 1900 && Integer.valueOf(next) <= 2100 ) {  //may need to change the range		
							year= next;
							getCurrentStream().remove();
						} else {
							year= "1900";
						}
					} 
					getCurrentStream().previous();
					//if the current token is a month
				} else if(months.contains(currentToken)){
					month = currentToken;
					boolean noRemove = false; 

					String nextToken = getCurrentStream().next().getTermText();
					String nextToken1 = getCurrentStream().next().getTermText();
					if(nextToken.contains(",") && nextToken1.contains(",")){
						nextToken = nextToken.replaceAll(",", "");
						nextToken1 = nextToken1.replaceAll(",", "");
						trailingChar = ",";
					} else if(nextToken.contains(",")){
						nextToken = nextToken.replaceAll(",", "");
					} else if(nextToken.contains(",") && nextToken1.contains(",")){
						nextToken1 = nextToken1.replaceAll(",", "");
						trailingChar = ",";
					}
					if( TokenFilterConstants.patternNumber.matcher(nextToken1).matches() && Integer.valueOf(nextToken1)>=1900 && Integer.valueOf(nextToken1)<=2100){  //may need to change the range
						year= nextToken1;
						getCurrentStream().remove(); getCurrentStream().previous(); noRemove = true;
					}else if( TokenFilterConstants.patternNumber.matcher(nextToken).matches() && Integer.valueOf(nextToken)>=1900 && Integer.valueOf(nextToken)<=2100){  //may need to change the range
						year= nextToken;
						getCurrentStream().previous(); getCurrentStream().remove(); 
					} else {
						year = "1900";
						getCurrentStream().previous();
						noRemove = true;
					}
					if( TokenFilterConstants.patternNumber.matcher(nextToken).matches() && Integer.valueOf(nextToken)>=1 && Integer.valueOf(nextToken)<=31){
						date= nextToken;
						if(noRemove){
							getCurrentStream().remove();
						}
					} else {
						date="01";
					}
					getCurrentStream().previous();		
				} else if( TokenFilterConstants.patternNumber.matcher(currentToken).matches() && Integer.valueOf(currentToken)>=1900 && Integer.valueOf(currentToken)<=2100){
					year=currentToken;
					if(months.contains(getCurrentStream().next().getTermText())){
						month=getCurrentStream().getCurrent().getTermText();
					} else{
						month="January";
					}
					date="01";
					getCurrentStream().previous();
				}

				Date newDt=parseDate(date+"-"+month+"-"+year);
				String formattedDate=null;
				if(newDt!=null){
					formattedDate = new SimpleDateFormat("yyyyMMdd").format(newDt);
					if(trailingChar != null){
						formattedDate = formattedDate + trailingChar;
					}
					getCurrentStream().getCurrent().setTermText(formattedDate);
				} 
			} 
		} catch(Exception exception){

		}
	}

	public Date parseDate(String date) 
	{
		final String DATE_FORMAT = "dd-MMM-yyyy";
		try {
			DateFormat df = new SimpleDateFormat(DATE_FORMAT);

			return df.parse(date);
		} catch (ParseException e) {
			return null;
		}
	}

	public String hasTrailingChar(String currentToken){
		if(currentToken.endsWith(".") ||currentToken.endsWith(",")
				||currentToken.endsWith("?")){
			return currentToken.substring(currentToken.length()-1);
		} else
			return null;
	}

}
