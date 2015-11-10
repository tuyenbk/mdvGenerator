package edu.isu.ce.mdvgenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import com.google.common.collect.Lists;

import edu.ehu.galan.cvalue.CValueAlgortithm;
import edu.ehu.galan.cvalue.filters.ILinguisticFilter;
import edu.ehu.galan.cvalue.filters.english.AdjPrepNounFilter;
import edu.ehu.galan.cvalue.model.Document;
import edu.ehu.galan.cvalue.model.Token;

public class extractNounPhrase {

	public static List<String> fromTaggedData (String[] tokens, String[] tags) {
		
		List<String> nounPhrases = new ArrayList<>(); 

        LinkedList<Token> tokenList = new LinkedList<>();
		List<LinkedList<Token>> tokenizedSentenceList = new ArrayList<LinkedList<Token>>();
        int i=0; //String preToken = "";
        for (String t : tokens) {
        	
        	tokenList.add(new Token(tokens[i], tags[i]));
        	i++;
        	
        	/**
        	 * 
        	 
        	if (!tok.equals("") && !to.equals(" ") && !tok.equals(preToken))  { //ignore space, null or repeated tokens
        		String token=t.substring(0,t.lastIndexOf("/"));
        		String tag = t.substring(t.lastIndexOf("/")+1);
		        tokenList.add(new Token(token, tag));
		        j++;
		        //preToken=tagged_tokens.get(j-1); //to check repeated tokens that may not have meanings
        	}
        	*/
        	
        	
        }

        /**
         * extracting noun phrases
         */
        tokenizedSentenceList.add(tokenList);
        
    	Document doc=new Document("C:\\","terms.txt");
        doc.List(tokenizedSentenceList);
        
        CValueAlgortithm cvalue=new CValueAlgortithm();
        cvalue.init(doc); // initializes the algorithm for processing the desired document. 
        
        ILinguisticFilter pFilter = new AdjPrepNounFilter();
        cvalue.addNewProcessingFilter(pFilter);; //set noun phrase pattern, e.g. AdjNounFilter
        
        List<String> cList = cvalue.extractCandidate(); //extract NP candidate

        nounPhrases.addAll(cList);
		return nounPhrases;
	}
	
	public static List<String> fromRawData (String rawString) throws IOException {
		String[] tokens = OpenNLPTokenizer.tokenize(rawString);
		String[] tags = OpenNLPTagger.tag(tokens);
		return fromTaggedData(tokens, tags);
	}
	
}
