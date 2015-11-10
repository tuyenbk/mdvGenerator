package edu.isu.ce.mdvgenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Concept {

	private String name="";
	//private String mainPart="";
	private String att="";
	private String phrase="";
	
	public Concept(String phrase, String name) {
		this.name=name;
		this.phrase=phrase;
	}
		
	public String Name(){
		return this.name;
	}
	
	public String Phrase() {
		return this.phrase;
	}
	
	public String Attribute(){
		return this.att;
	}
	public void setAttribute(String value) {
		this.att=value;
	}
	
	
	
	public static List<Concept> getConcepts(String keyword) throws IOException{
	
		
		
	List<Concept> cons	= new ArrayList<>();
	Concept con = null;
	
	//keyword is just a noun
	if (keyword.split(" ").length==1) {
	con=new Concept(keyword,keyword);con.setAttribute("");
	cons.add(con);
	return cons;
	}
	//keyword is a phrase
	String phrase=keyword;
	String[] tokens = OpenNLPTokenizer.tokenize(phrase);
	String[] tags	 = OpenNLPTagger.tag(tokens);
	boolean onlyNouns = true;
	
	//check valid phrase
	String lastTag=tags[tags.length-1];
	if (!lastTag.equals("NN")) return null;
	
	
	
	//check if phrases contain only NN
	for (String t:tags) {
		if (!t.equals("NN")) {onlyNouns=false;break;}
	}
	
	//case 1 - NN+NN
	
	if (onlyNouns==true) {
		
		if  (phrase.contains(" of ")) {
			con = new Concept(keyword,tokens[2]);
			con.setAttribute(tokens[0]);
			cons.add(con);
			
			con = new Concept(phrase,phrase);
			con.setAttribute("");
			cons.add(con);
			
		}
		else {
			
			con = new Concept(phrase,tokens[0]);
			con.setAttribute(tokens[1]);
			cons.add(con);
			
			con = new Concept(phrase,tokens[1]);
			con.setAttribute(tokens[0]);
			cons.add(con);
			
			con = new Concept(phrase,phrase);
			con.setAttribute("");
			cons.add(con);
		
		}
	}
	else {
		con = new Concept(phrase,tokens[1]);
		con.setAttribute(tokens[0]);
		cons.add(con);
		
		con = new Concept(phrase,phrase);
		con.setAttribute("");
		cons.add(con);
	}
	return cons;


	}
	
}
