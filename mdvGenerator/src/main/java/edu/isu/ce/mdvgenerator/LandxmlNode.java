package edu.isu.ce.mdvgenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.text.WordUtils;

import javatools.parsers.PlingStemmer;

public class LandxmlNode {
	private int id;
	private String name ="";
	private String fullName="";
	private String desc ="";
	private List<Integer> children = new ArrayList<>();
	private List<Integer> parents = new ArrayList<>();
	private List<Integer> siblings = new ArrayList<>();
	private List<Integer> hyponyms = new ArrayList<>();//to add children those are <xs:enumeration> under 'simple' type elements
	private List<Integer> hypernyms = new ArrayList<>();
	private List<Integer> attibutes = new ArrayList<>();//children those are <xs:
	
	public LandxmlNode(String name, int ID) {
		this.name=name;
		this.fullName=getFullName(name);
		this.id=ID;
	}
	
	public int LandxmlNodeID(){
		return this.id;
	}

	public String Name(){
		return this.name;
	}
	
	public String FullName(){
		return this.fullName;
	}
	
	public String getDescription(){
		return this.desc;
	}
	
	public void setName(String name) {
		this.name=name;
	}
	
	public void setID(int ID) {
		this.id=ID;
	}
	
	public int getID(){
		return this.id;
	}
	
	public void setDescription(String description) {
		this.desc=description;
	}
	
	public void setFullName(String fullName) {
		this.fullName=fullName;
	}
	
	public List<Integer> Children(){
		return this.children;
	}

	public List<Integer> Parents() {
		return this.parents;
	}
	
	public List<Integer> Siblings() {
		return this.siblings;
	}
	
	public List<Integer> Attributes() {
		return this.attibutes;
	}
	
	public List<Integer> Hypernyms() {
		return this.hypernyms;
	}
	
	public List<Integer> Hyponyms() {
		return this.hyponyms;
	}
	
	/**
	 * //adding white spaces into landxml node
	 * @param shortName
	 * @return
	 */
	
	private String getFullName(String name) {
		
		String fullName ="";
		
	   	for (int i=0; i <name.length();i++ ) {
	   		char c =name.charAt(i);
	   		if (Character.isUpperCase(c)==true ) {
	   			fullName=fullName+" "+c;
	   		}
	   		else fullName=fullName+c;
	   	}
	   	fullName=fullName.trim();
	   	fullName = fullName.replaceAll("-", " ");
	   	return fullName;
	}

	public static String guessTerm(String name, String spacedName, String desc) throws IOException {
		//guessing the full name for landxml node name
		PlingStemmer stemmer = new PlingStemmer();
		String term =spacedName;
		if (spacedName!="") {	

		   	//finding matching term given in the documentation element
			if (spacedName.split(" ").length==1) {
			return stemmer.stem(term);	
			}
			
			else if (spacedName.split(" ").length>1 && !desc.equals("")) {
		        //remove duplicate spaces
		        desc = WordUtils.capitalize(desc).replaceAll("\\s+", " ").trim();
		        String[] words = OpenNLPTokenizer.tokenize(desc);
		        
		        String tempTerm="";
		        for (String n:spacedName.split(" ")) {
		        	n=stemmer.stem(n);
		        	//boolean match =false;
		        	String matchWord=n;
		        	for (String w:words) {
		        		w=stemmer.stem(w);
		        		if (isShortName.check(w,n) && !w.equals(name)) {			        			
		        			//match=true;
		        			matchWord=w;}
		        		if (w.contains(n) && !w.equals(name)) {//higher priority of matching criteria
		        			//match=true;
		        			matchWord=w; break;}}
		        	//System.out.println(n+";"+matchWord);

		        	//if (match==true) 
		        		tempTerm=tempTerm+" "+matchWord;
		        	//else break; 
		        	}

		        if (!tempTerm.equals("")) term=tempTerm.trim();}}
		return term;
	}
}
