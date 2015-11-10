package edu.isu.ce.mdvgenerator;

public class isShortName {

	public static boolean check(String fullName, String shortName) {

		fullName=fullName.toLowerCase();
		shortName=shortName.toLowerCase();
	   	for (int i=0; i <shortName.length();i++ ) {
	   		char c =shortName.charAt(i);
	   		if (!fullName.contains(String.valueOf(c))) return false;
	   	}
		return true;
	}
	
}
