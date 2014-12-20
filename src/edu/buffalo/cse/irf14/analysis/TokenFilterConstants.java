package edu.buffalo.cse.irf14.analysis;

import java.util.regex.Pattern;

public class TokenFilterConstants {

	public static final Pattern patternCaps = Pattern.compile("[A-Z]+");
	public static final Pattern patternNotCaps = Pattern.compile("[^A-Z]+");
	public static final Pattern patternCamel =Pattern.compile("[A-Z][a-z]+");
	
	public static final Pattern patternHyphen = Pattern.compile("[\\d][\\d][\\d][\\d]\\-[\\d][\\d].*");
	public static final Pattern patternBC = Pattern.compile("[\\d]+BC.*");
	public static final Pattern patternAD = Pattern.compile("[\\d]+AD.*");
	public static final Pattern patternTime = Pattern.compile("[\\d]+:[\\d][\\d][apAP][mM].*");
	public static final Pattern patternTimeDD = Pattern.compile("[\\d][\\d]:[\\d][\\d]");
	public static final Pattern patternNumber = Pattern.compile("[\\d]+");
	public static final Pattern patternTimeNum = Pattern.compile("[\\d]+:[\\d][\\d]:[\\d][\\d]");
	public static final Pattern patternDate =Pattern.compile("[012][980][\\d][\\d][\\d][\\d][\\d][\\d]");
	public static final Pattern patternAlpha = Pattern.compile(".*[a-zA-Z]+.*");
	
	public static final Pattern patternWS = Pattern.compile("[^\\w\\s\\-.:]");
	public static final Pattern patternDHD =Pattern.compile("[\\d]+\\-[\\d]+");
	public static final Pattern patternTimeSplCh = Pattern.compile("[\\d]+:[\\d]+:[\\d]+");
	
	public static final Pattern patternCom = Pattern.compile(".*[\\.\\'\\?\\-\\!\"()<>].*");
	public static final Pattern patternHyphen1 =Pattern.compile("(?i)([a-zA-Z]+)(-)([a-zA-Z]+)");
	public static final Pattern patternHyphen2 = Pattern.compile("(?i)([a-zA-Z]+)(-)([a-zA-Z]+)(-)([a-zA-Z]+)");
	
	
}
