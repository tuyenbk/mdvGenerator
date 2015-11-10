package edu.isu.ce.mdvgenerator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

public class OpenNLPTokenizer {
	
	public static String[] tokenize(String string) throws IOException {

    	String[] tokens = null;
    	String line;
    	
    	//remove url
        string = string.replaceAll("https?://\\S+\\s?", "");
        
        //string = "shows the relationship between the highest hourly volumes and adt.";
    	InputStream modelInputStream1 = new FileInputStream(
	        new File("openNLPmodels\\en-token.bin"));
	    TokenizerModel modelToken = new TokenizerModel(modelInputStream1);
	    Tokenizer tokenizer = new TokenizerME(modelToken);
	    tokens = tokenizer.tokenize(string); 

    	return tokens;
    	
    }
}
