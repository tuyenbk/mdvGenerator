package edu.isu.ce.mdvgenerator;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javatools.parsers.PlingStemmer;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

public class OpenNLPTagger {


    public static String[] tag(String[] tokens) throws IOException {
    			
    	String tags[] = null;
    	List<String> token_tag = new ArrayList<String>();
	 	PlingStemmer stemmer = new PlingStemmer();


	    InputStream posModelStream = new FileInputStream("openNLPmodels\\en-pos-maxent.bin");
	    InputStream chunkerStream = new FileInputStream("openNLPmodels\\en-chunker.bin");
		POSModel modelTagger = new POSModel(posModelStream);
		POSTaggerME tagger = new POSTaggerME(modelTagger);
		tags = tagger.tag(tokens);	
		/**
		 * 
		 
		for(int i=0; i<tags.length; i++) {
			
	  		String w=tokens[i].toLowerCase();  //lowercase phrase
	  		//w=stemmer.stem(w);	//stemming phrase 
	  		//if (tags[i].equals("NNS")) token_tag.add(w + "/" + "NN");
	  		//else 
			token_tag.add(w + "/" + tags[i]);
		} */
    	return tags;

    	} 
}
