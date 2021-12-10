package org.palladiosimulator.somox.cipm.modelrefinement.parameters.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.palladiosimulator.somox.cipm.modelrefinement.parameters.util.Utils;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.trees.J48;
import weka.core.Instances;

public class Utils {

	private static final String PLUS_PARENTHESIS = " + (";
	private static final String MULTIPLICATION = " * ";
	private static final String OPENING_PARENTHESIS = "(";
	private static final String CLOSING_PARENTHESIS = ")";
	private static final String NEW_LINE = "\n";
	private static final String SPACES = "\\s+";
	private static final char PIPE = '|';
	private static final String COLON = ":";
	private static final String QUESTION = "?";

	public static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		BigDecimal bd = BigDecimal.valueOf(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	public static String replaceUnderscoreWithDot(String stoEx) {
		return stoEx.replaceAll("_VALUE", ".VALUE").replaceAll("_BYTESIZE", ".BYTESIZE")
				.replaceAll("_NUMBER_OF_ELEMENTS", ".NUMBER_OF_ELEMENTS");
	}

	/**
	 * Parses the coefficients of a classifier to generate a stoex
	 * @param classifier 
	 * @param filteredData dataset
	 * @return
	 */
	public static String getStoExLinReg(LinearRegression classifier, Instances filteredData) {
		StringJoiner result = new StringJoiner(PLUS_PARENTHESIS);
		double[] coefficients = classifier.coefficients();
		int braces = 0;
		for (int i = 0; i < coefficients.length - 2; i++) {
			double coefficient = Utils.round(coefficients[i], 3);
			if (coefficient == 0) {
				continue;
			}
			StringBuilder coefficientPart = new StringBuilder();
			String paramStoEx = filteredData.attribute(i).name();
			coefficientPart.append(coefficient).append(MULTIPLICATION).append(paramStoEx);
			result.add(coefficientPart.toString());
			braces++;
		}
		result.add(String.valueOf(Utils.round(coefficients[coefficients.length - 1], 3)));
		StringBuilder strBuilder = new StringBuilder().append(result.toString());
		for (int i = 0; i < braces; i++) {
			strBuilder.append(CLOSING_PARENTHESIS);
		}
		return strBuilder.toString();
	}

	/** 
	 * Parses the coefficients of the tree to generate a stoex
	 * @param j48Tree decision tree
	 * @return
	 */
	public static String getStoExTree(J48 j48Tree) {
		String[] lines = j48Tree.toString().split(NEW_LINE);
		if (lines.length == 6) { // constant value (6 is the min size ot the tree)
			return lines[3].replace(COLON, "").replaceAll(SPACES, "");
		}

		StringBuilder builder = new StringBuilder();
		int numPipes = 0;
		for (int currentLine = 3; !lines[currentLine].isEmpty(); currentLine++) {
			String line = PIPE + lines[currentLine];

			if (!line.contains(COLON)) { // line contains condition (no assignment)
				if (countPipes(line) == numPipes) { // closing
					builder.append(COLON);
				} else { // opening
					builder.append(OPENING_PARENTHESIS + OPENING_PARENTHESIS + line.replaceAll(SPACES, "")
							+ CLOSING_PARENTHESIS + QUESTION);
					numPipes++;
				}

			} else {
				if (countPipes(line) == numPipes) { // closing
					String[] lineParts = line.split(COLON);
					builder.append(COLON + lineParts[1].replaceAll(SPACES, "") + CLOSING_PARENTHESIS);
					numPipes--;
				} else { // opening
					String[] lineParts = lines[currentLine].split(COLON);
					builder.append(OPENING_PARENTHESIS + OPENING_PARENTHESIS + lineParts[0].replaceAll(SPACES, "")
							+ CLOSING_PARENTHESIS + QUESTION + lineParts[1].replaceAll(SPACES, ""));
					numPipes++;
				}
			}
		}
//		for (int i = 0; i < numPipes-1; i++) {
//		builder.append(")");
//	}
		return cleanStoEx(builder.toString()); 
	}

	private static String cleanStoEx(String stoEx) {
		// remove all pipe symbols that come from the decision tree
		String cleanStoEx = stoEx.replaceAll(Character.toString(PIPE), "");
		// replace all = with == (except the ones in !=)
		cleanStoEx = cleanStoEx.replaceAll("(?<!!)=", "==");
		// remove all strings e.g. (4.0/8.0) which come from the decision tree
		cleanStoEx = cleanStoEx.replaceAll("[(]\\d+\\.\\d+[)]", "");
		return cleanStoEx;
	}

	private static int countPipes(String str) {
		return (int) str.chars().filter(ch -> ch == PIPE).count();
	}

	/**
	 * Replaces all doubles by distribution functions with integers
	 * @param stoEx input stoex
	 * @return
	 */
	public static String replaceDoubles(String stoEx) {
		DecimalFormat df = new DecimalFormat( "0.00" );

	    Pattern p = Pattern.compile( "(\\d+\\.\\d+)" );
	    Matcher m = p.matcher( stoEx );
	    String result = "";
	    StringJoiner stringJoiner = new StringJoiner( "+" );
	    String[] splitString = stoEx.split( "\\+" );
	    for( String subString: splitString )
	    {
	        if( m.find() )
	        {
	            String fracPart = m.group( 1 ).split( "\\." )[1];
	            int lowerInt = Integer.valueOf(m.group( 1 ).split( "\\." )[0]);
	            int upperInt = lowerInt + 1;
	            String fracOne = df.format( 1 - ( Integer.parseInt( fracPart ) / Math.pow( 10.0, fracPart.length() ) ) );
	            String fracTwo = df.format( ( Integer.parseInt( fracPart ) / Math.pow( 10.0, fracPart.length() ) ) );
	            String resultEx = "IntPMF[(" + lowerInt + ";" + fracOne + ")(" + upperInt + ";" + fracTwo + ")]";
	            result = subString.replaceFirst( m.group( 1 ), resultEx );
	            stringJoiner.add( result );
	        }
	    }
	    return stringJoiner.toString();
	}

}
