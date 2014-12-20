package edu.buffalo.cse.irf14.query;

import java.util.regex.Pattern;

public class QueryConstants {

	public static final Pattern QUERY_PATTERN = Pattern.compile("(?i)([\\w\\d\\W]+)(:\\{)([\\w\\d\\W]+)(\\})");
	public static final Pattern QUERY_PATTERN_INDEX = Pattern.compile("(?i)(.* )(\\w+:\\(\\w.*\\))(.*)");
	public static final Pattern WILD_CARD_PATTERN = Pattern.compile("\\w+(\\*|\\?)\\w+");
	public static final String QUERY_AND = "AND";
	public static final String QUERY_OR = "OR";
	public static final String QUERY_NOT = "NOT";
	public static final String QUERY_TERM = "Term:";
	
}
