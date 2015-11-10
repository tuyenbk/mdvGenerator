package edu.isu.ce.mdvgenerator;

/**
 * Hello world!
 *
 */

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
//import java.util.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Iterator;

import javatools.parsers.PlingStemmer;

import org.apache.hadoop.net.SocksSocketFactory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.pdfbox.util.PDFTextStripperByArea;
import org.apache.spark.SparkContext;
import org.apache.spark.ml.feature.StopWords;
import org.apache.spark.mllib.feature.Word2Vec;
import org.apache.spark.mllib.feature.Word2VecModel;
import org.apache.thrift.TException;
import org.hcmut.tuyen.Word2Vec_Medallia;
import org.jets3t.service.io.TempFile;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

import scala.Tuple2;
//import scala.collection.immutable.Map;
import spire.optional.genericEq;

import com.google.common.collect.Lists;
import com.medallia.word2vec.Searcher.UnknownWordException;
//import com.medallia.word2vec.word2vec;

import edu.ehu.galan.cvalue.CValueAlgortithm;
import edu.ehu.galan.cvalue.Candidate;
import edu.ehu.galan.cvalue.filters.ILinguisticFilter;
import edu.ehu.galan.cvalue.filters.english.AdjPrepNounFilter;
import edu.ehu.galan.cvalue.model.Document;
import edu.ehu.galan.cvalue.model.Term;
import edu.ehu.galan.cvalue.model.Token;

import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.stemmer.snowball.*;
//import org.junit.After;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

public class mdvGenerator 
{
	
	/**
	 * folder structure
	 * 'data'--> ('stemmed', 'stop_removed', and 'stemmed_and_tagged' sub folders)
	 * each sub folder contains two folders: 'singles' folder (individual files) and 'full' folder (one unique file)
	 */
	//directory
	static String dataDir="data";
	static String rawDataDir = dataDir+"\\raw";
    static String stemmedFullDir = dataDir+"\\stemmed\\full";
    static String stemmedSingleDir = dataDir+"\\stemmed\\singles";
    static String tagged_stemmedFullDir = dataDir+"\\tagged_stemmed\\full";
    static String tagged_stemmedSingleDir = dataDir+"\\tagged_stemmed\\singles";
    static String tokenizedSingleDir = dataDir+"\\tokenized\\singles";
    static String stopRemovedFullDir = dataDir+"\\stop_removed\\full";
    static String stopRemovedSingleDir = dataDir+"\\stop_removed\\singles";
    static String landxmlDir="LandXML_2.0";
    
    static String stemmedFullFile = stemmedFullDir + "\\" + 
    "stemmed_full_data.txt";
 	static String tagged_stemmedFullFile = tagged_stemmedFullDir + "\\" + 
 		    "000_tagged_stemmed_full_data.txt";
 	static String stopRemovedFullFile = stopRemovedFullDir + "\\" + 
    "stopremoved_full_data.txt";
 	static DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");

 	static String outputDir ="output";
 	static String phraseFile = outputDir+"\\extracted_noun_phrases"+".txt";
 	static String termFile = outputDir+"\\extracted_terms"+".txt";
 	
 	
 	static String phraseCorpusFile = stopRemovedFullDir+"\\phrase_corpus.txt";
 	
 	static String termSpaceDir = "term space";
 	
 	static Logger logger = Logger.getLogger("MyLog");
 
 	
 	static String landxmlSchemaPath = landxmlDir+"\\LandXML-2.0.xsd";
 	//static String landxmlLexiconDataPath="LandXML_2.0\\Landxml-2.0.lxc";
 	//static String landxmlLexiconIndexPath="LandXML_2.0\\Landxml-2.0.idx";
 	
 	
    //static String modelpath="highway_word_space_1.0";
    //static String modelpath="highway_term_space_5.0";
    static String modelpath="phrase_corpus.model";
    //static String modelpath="stemmed_full_data.model";
    
    
    static HashSet<Node> fullChildren = new HashSet<Node>();
    

    
    public static void main( String[] args ) throws Exception
    {
 	     
 	   String paragraph = "Some of the more common inventory data include "
 				+ "roadway functional classification , "
 				+ "pavement width , pavement type , number of traffic lanes , "
 				+ "and shoulder type and width . For each these , "
 				+ "locations , references data must include limits both along "
 				+ "the highway and across the various lanes . "
 				+ "Additional detailed data may include variables such as : "
 				+ "(1) layer thickness , (2) subgrade classification "
 				+ "or properties ; (3) layer material properties ; "
 				+ "(4) joint spacing (PPC) ; (5) load transfer type (PPC) ; "
 				+ "(6) resilient modulus of the subgrade ; "
 				+ "(7) drainage information ; and (8) climate factors or "
 				+ "climactical region , precipitation and freeze thaw . "
 				+ "Optional inventory data items for describing the pavement "
 				+ "network include such "
 				+ "items as grade , cross-slope superelevation, measure of curvature . ";
 	   String sentence ="Optional 1.2 inventory [data items] for describing the pavement network include such "
 				+ "items as grade , p/e s.a cross-slope superelevation, measure of curvature 1.25.";   
 	   
 	   String[] words = {"pavement/NN"}; //word = word+"/NN";
 	   
 	   //if (words[0].contains("temporary")) System.out.println("included");;
 	   
 	   //String term = " high traffic volume ";  //traffic volume : high traffic volume 
 	   
 	   //term=term.replaceAll("(high)|(traffic)", "-");System.out.println(term);
 	   
 	   //if (term.contains(" traffic volume ")) 
		 /**
		  * 
		  
 	  Stringsimilarity strSim = new Stringsimilarity();
 	  double sim = strSim.similarity("consideration","condition");
 	  System.out.println(sim);*/
 	   
 	   
 	   //String[] tokens = opennlp_tokenizer(termFile);
 	   
 	   //processRawData();

 	   
 	   //extractNounPhrases();
 	   
 	   //extractTerm(phraseFile);
 	   
 	   //replacePhrase(termFile, stemmedFullFile, phraseCorpusFile);
 	   
 	   //trainModel(phraseCorpusFile,modelpath);

 	   //List<List<String>> syns = getSynonyms(Arrays.asList(words), modelpath);
 	   
 	   //LandxmlLexicon lexicon = readLandXML();

 	   //System.out.println("point".contains("p"));
 	  //List<Concept> cons = Concept.getConcepts("vertical alignment");
 	  
 	  MDVgenerate(modelpath);	   
 	   
 	   //com.medallia.word2vec.Word2VecModel model = Word2Vec_Medallia.loadModel(modelpath);

 	   //w2vExample.skipGram("phrase_corpus2");

 	   //Word2Vec_Medallia.multipleSearch(modelpath,50);;
  	   
 	   //Stringsimilarity strSim = new Stringsimilarity();
 	   
 	   //strSim.main(args);

 	   /**re
 	    *  
 	   
	   Stemmer stemmer = new Stemmer();
	   
	   String[] words1 ={"drainage.txt"};
	   
	   stemmer.main(words1);

	   stemmer.stem();
	   
	   System.out.print(stemmer.toString());*/
 	   
 	   /**
 	    * 
 	    
 	   String phrase = "Spirals are used to transition the horizontal alignment from tangent to curve .";
 	   String[] tokens = phrase.split(" ");
 	   System.out.println(String.join("\n",OpenNLPTagger.tag(tokens)));*/
 	   
    }
    

	public static double distance (double[] vectorA, double[] vectorB) {
    	double dis=0.0;
    	double norm=0.0;
        for (int i = 0; i < vectorA.length; i++) {
            norm+= Math.pow(vectorA[i]-vectorB[i], 2);
        }
        return  Math.sqrt(norm);
    }
    
    public static double cosineSimilarity(double[] vectorA, double[] vectorB) {
    	
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);
        }   
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
    
    
    public static double cos50(String word,Tuple2<String, Object>[] syms, Word2VecModel model) {
		
    	
    	double[] v1 = model.transform(word).toArray();
    	
    	//Tuple2<String, Object>[] syms2 = model.findSynonyms(word, 20);
    	
    	
    	int count=0; double cosSum =0; double cosAvg=0;
    	for (Tuple2<String, Object> t: syms ) {
    		double[] v2 = model.transform(t._1).toArray();
    		cosSum=cosSum+cosineSimilarity(v1, v2);
    		//cosSum=cosSum+distance(v1, v2);
    	}
    	
    	cosAvg=cosSum/syms.length;

    	return cosAvg;
    	
    }
    
 
    public static List<List<String>> getSynonyms(List<String> words, String modelpath) throws IOException {
	    /**
	     * 
	    
    	//read term file to get the list of phrases
    	List<String> terms = new ArrayList<>();
   	     logger.log(Level.INFO, "*********Reading term file...."); 
	     String combline=""; String line ="";    
	     try {
			BufferedReader br = new BufferedReader(new FileReader(termFile));	
			int i=0;    
			while ((line=br.readLine()) != null) {
				String phr = line.replaceAll("[^A-Za-z]"," ").trim();
				terms.add(phr);
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} */
 
    	
    	
    	SparkContext sc = new SparkContext("local", "appName");
    	
    	//load a trained model
    	Word2VecModel model = Word2VecModel.load(sc, modelpath);
    	
   	    //find similar words
    	List<List<String>> synsSet= new ArrayList<List<String>>();
    	
    	
    	for (String word:words) {
		    	word=word.replaceAll("\\s", "-");	
		    	Tuple2<String, Object>[] syms1 = model.findSynonyms(word, 50);
	
		 
		    	//cos50 value of the target word
		    	//double cos50Value = cos50(word,syms, model);
		    	//System.out.println("CosAvg-50 value of the target word " + word +":"+cos50Value);

		   	    HashMap<String,Double> closeList = new HashMap<String,Double>();
		    	
		    	System.out.println("The nearests of the word " + word +":");
		    	int count=0; double cos50Value1=0;double cos50Value2=0;
		    	List<String> syns = new ArrayList<>();
		    	syns.add(word); //add itself
		    	for (Tuple2<String, Object> t: syms1 ) {
				    		syns.add(t._1);
				    		
				    		
				    		/**
				    		 * 
				    		 
				    		String term="";
				    		int termIndex=0;
				    		if (t._1.contains("_")){
				    		termIndex = Integer.parseInt( t._1.substring(t._1.indexOf("_")+1));
				    		if (termIndex != 0) term = terms.get(termIndex);}*/
				    		
				    		
				    		//cos50Value1 = cos50(word,syms1, model);
				    		cos50Value1=cosineSimilarity(model.transform(word).toArray(), model.transform(t._1).toArray());
				    		
				    		//cos50Value1=distance(model.transform(word).toArray(), model.transform(t._1).toArray());
				        	cos50Value2 = 0;
				        	
				    		//Tuple2<String, Object>[] syms2 = model.findSynonyms(t._1,10);
				 
				    		//cos50Value1 = cos50(word,syms2, model);
				    		//cos50Value2 = cos50(t._1,syms2, model);
				    		if (t._1.contains("/NN") || t._1.contains("/NNS")) {
				    		//if (!word.contains(t._1) && !t._1.contains(word)) {
				    		//if (!term.contains(word)) {
				    			count=count+1;
				    			System.out.println("No " + count+ ": " + t._1);}
				    		double dif = Math.abs(cos50Value1-cos50Value2);
				    		closeList.put(t._1, dif);
				    		
				    		
		    	}
		    	synsSet.add(syns);
		    	
		    	
		    	/**
		    	 * 
			     //sort the list by difference
		    	 System.out.println("--------------------------------------");
		    	 System.out.println("The nearests of the word " + word +":");
			     Set<Entry<String, Double>> set = closeList.entrySet();
			     List<Entry<String, Double>> sortedList = new ArrayList<Entry<String, Double>>(
			             set);
			     Collections.sort(sortedList, new Comparator<Map.Entry<String, Double>>() {
			         public int compare(Map.Entry<String, Double> o1,
			                 Map.Entry<String, Double> o2) {
			             return o2.getValue().compareTo(o1.getValue());
			         }
			     });
			     count=0;
			    for (Entry<String, Double> entry : sortedList) {
			    	count=count+1;
			    	
		    		System.out.println("No " + count+ ": " +entry.getKey() + " " + entry.getValue());
			    }
		    	
		
		    	
		        
		    	//retrieve vector space
		    	scala.collection.immutable.Map<String,float[]> m = model.getVectors();
		    	
		    	double[] v1 = model.transform(word).toArray();
		    	
		    	System.out.println(Arrays.toString(v1));*/
    	}
    	return synsSet;

    } 

    public static void trainModel(String filteredDataPath, String modelpath) throws IOException {

    	
    	String line = "";
        String combline = "";
        //read and process raw data
        BufferedReader br = new BufferedReader(new FileReader(filteredDataPath));
        
    	while ((line=br.readLine()) != null) 
			combline=combline+" "+line;	
    	
    	
    	List<String> words = Lists.newArrayList(combline.split(" "));
        List<List<String>> localDoc = Lists.newArrayList(words, words);    
        
        //build a context object
        JavaSparkContext sc = new JavaSparkContext("local", "Word2VecSuite");
        JavaRDD<List<String>> doc = sc.parallelize(localDoc);
 
        //training settings
        Word2Vec word2vec = new Word2Vec()
          .setVectorSize(100)
          .setMinCount(50)
          .setSeed(42L);
        
        //train
        Word2VecModel model = word2vec.fit(doc);
        
        //save model 
        SparkContext sc1 = sc.toSparkContext(sc); 
        model.save(sc1, modelpath);
        System.out.println("Model has been saved in folder: "+modelpath);
    }

    /**
     * save a the list of filtered words to file
     * @param fileName
     * @param words
     * @throws IOException 
    */
    public static void processRawData() throws IOException {
        
	
    	File folder = new File(rawDataDir);
    	File[] listOfFiles = folder.listFiles();
    	//List<String> stemmedWords = new ArrayList<String>();
    	//List<String> taggedWords = new ArrayList<String>();
    	//List<String> filteredWords = new ArrayList<String>();
    	
    	List<List<String>> stemmedWords = new ArrayList<>();
    	List<List<String>> tagged_stemmedWords = new ArrayList<>();
    	List<List<String>> filteredWords = new ArrayList<>();
    	
    	int filecount = listOfFiles.length;
    	int i=0;
    	for (File file : listOfFiles) {
    	    if (file.isFile()) {
    	       i++;
    	       System.out.println(i+": "+file.getName());

				
				 
    	       //get input/output data files names
    	       String inputFile = folder.getPath() + "\\"+ file.getName();
    	       String tokenizedFile = tokenizedSingleDir + "\\tokenized_"+ file.getName();
    	       String stemmedFile = stemmedSingleDir + "\\stemmed_"+ file.getName();
    	       String tagged_stemmedFile = tagged_stemmedSingleDir + "\\tagged_stemmed_" + file.getName();
    	       
    	       /**
    	        * tokenizing
    	        * 
			   logger.log(Level.INFO, "Tokenizing...");   
    	       String[] tokens = opennlp_tokenizer(inputFile);    	       
    	       PrintWriter pw0 = new PrintWriter(new FileOutputStream(tokenizedFile));
    	       
    	       for(String w: tokens) {
							
    		  		pw0.print(w+" ");
    		  	
    	    		if (w.equals(".")) pw0.println();
    	       }
    	       pw0.close();*/
    	       
    	       
    	       /**
    	        * stemming raw data
    	        * */

    	       logger.log(Level.INFO, "Stemming...");
    	       List<String> wl1 = cleanAndstemmer(tokenizedFile);
    	       PrintWriter pw1 = new PrintWriter(new FileOutputStream(stemmedFile));
    
    	       for(String w: wl1) {
    	    		pw1.print(w+" ");
    	    		if (w.equals(".")) pw1.println();
    	    		}
    	       pw1.close();
    	       
    	       //add the full list to print a full file of stemmed words
    	       stemmedWords.add(wl1);
    	       
    	       /**
    	        * tagging stemmed data
    	        *
    	       
    	       logger.log(Level.INFO, "Tagging...");
    	       
    	       List<String> wl2= taggerAndStemmer(tokenizedFile);
	           PrintWriter pw2 = new PrintWriter(new FileOutputStream(tagged_stemmedFile));
	        	
    	       for(String t: wl2) {
    	    		pw2.print(t+" ");
    	    		}
    	       pw2.close(); 
    	       
    	       tagged_stemmedWords.add(wl2);*/
    	       
    	       
    	       /**
    	        * 
    	        * 

    	       //removing stop words from the tagged and stemmed list
    	       logger.log(Level.INFO, "Removing stop word ...");
    	       List<String> wl3 = stopRemover(wl2);
    	       String stopRemovedFile = stopRemovedSingleDir + "\\stopRemoved_"+ file.getName();
    	       
    	       PrintWriter pw3 = new PrintWriter(new FileOutputStream(stopRemovedFile));
    	       for(String t: wl3) {
    	    		pw3.print(t+" ");
    	    		}
    	       pw3.close();
    	       //add to the full list to print a full file of filtered words
    	       filteredWords.add(wl3); */
    	       
    	       logger.log(Level.INFO,i+" out of " + filecount+" files are done");

    	    }
    	}
    	/**
    	 * */

    	PrintWriter pw1 = new PrintWriter(new FileOutputStream(stemmedFullFile));
        
    	for (List<String> wl : stemmedWords) {
    		for (String w : wl) pw1.print(w+" ");
    		pw1.println();        
    	}
    	pw1.close();
        
        /**
         * 
         
    	PrintWriter pw2 = new PrintWriter(new FileOutputStream(tagged_stemmedFullFile));
        for (List<String> wl : tagged_stemmedWords)
            for (String w : wl) pw2.print(w+" ");
        	pw2.println();
        pw2.close();*/
        
    	/**
    	 *
    	 
    	PrintWriter pw3 = new PrintWriter(new FileOutputStream(stopRemovedFullFile));
        
    	for (List<String> wl : filteredWords) {
    		for (String w : wl) pw3.print(w+" ");
    		pw3.println();        
    	}
    	pw3.close();*/

        
        logger.log(Level.INFO,"Raw data is processed and saved.");

    } 
    
    /**
     * stemmer (remove stop words)
     * remove special characters, duplicate spaces, numbers, stop words
     * return a list of words
     * @param rawdatapath
     * @return
     * @throws IOException
     */
    public static List<String> cleanAndstemmer(String rawdatapath) throws IOException {
    	String line = "";
        String combline = "";

        //read input data
        try {
			BufferedReader br = new BufferedReader(new FileReader(rawdatapath));	
		    
			while ((line=br.readLine()) != null) 
			combline=combline+" "+line;	
		    
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        /**
         * */
         
        
        //remove url
        //combline = combline.replaceAll("https?://\\S+\\s?", "");
        
        //replace "- " with a white space
        //combline = combline.replaceAll("-", " ");
        
        
        //remove non letter
        //combline = combline.replaceAll("[^a-zA-Z\\s]", "");
        //one other way: combline=combline.replaceAll("[-+^,\\.\\;:?()'><*&*#%{}=$\\d]", "");
       
        
        //remove duplicate spaces
        combline=combline.replaceAll("\\s+", " ").trim();

        List<String> words = Lists.newArrayList(combline.split(" "));
        
        //stemming
	  	List<String> stemmedwords = new ArrayList<String>();

	 	PlingStemmer stemmer = new PlingStemmer();
	 	
	  	for (String w :words) {
	  		w=w.toLowerCase();
	    	if (!w.equals("'s")) w=stemmer.stem(w);
	    	stemmedwords.add(w);	   

	  	} 
   
        return stemmedwords;

    	
    }
    
    
    /**
     * not used any more
     * remove stop words
     * @param words
     * @return
     * @throws IOException 
     * */
	 public static List<String> stopRemover(List<String> words) throws IOException {
	  	
		List<String> stopwords = Arrays.asList(StopWords.EnglishStopWords());
	  	List<String> filterwords =  new ArrayList<String>();
    	    	    	
    	for (String w :words) {
    		String w1=w.substring(0, w.lastIndexOf("/"));
	        String w2 = w1.replaceAll("[^a-zA-Z]"," ").trim();
	  		if (!w2.equals("") && !stopwords.contains(w1)  ) {//check if a word is in stop list
	  														// or a white space														
	  			filterwords.add(w);
	  		}
	  	}
    	
    	return filterwords;
	  			
	 }
	 
    

    
    
    public static String[] tokenizer(String stemmedFile) throws IOException {

    	String[] tokens = null;
    	String combline ="";
    	String line;
     
    	try {
			BufferedReader br = new BufferedReader(new FileReader(stemmedFile));	
		    
			while ((line=br.readLine()) != null) 
				if (!line.equals("")) combline=combline+" "+line;	
		    
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	//remove url
        combline = combline.replaceAll("https?://\\S+\\s?", "");

        //combline = "shows the relationship between the highest hourly volumes and adt.";
        try {
        	InputStream modelInputStream1 = new FileInputStream(
    	        new File("openNLPmodels\\en-token.bin"));
    	    TokenizerModel modelToken = new TokenizerModel(modelInputStream1);
    	    Tokenizer tokenizer = new TokenizerME(modelToken);
    	    tokens = tokenizer.tokenize(combline); 
    	   
    	} catch (IOException ex) {
    	    // Handle the exception
    	}
        
    	   
    	return tokens;
    	
    }
    /**
     * opennlp part of speech tagging
     * @param tokens
     * @return 
     * @return
     * @throws IOException 
     */
    public static List<String> taggerAndStemmer(String inputFile) throws IOException {
    			
    	String tags[] = null;
    	String[] tokens = tokenizer(inputFile);
    	List<String> token_tag = new ArrayList<String>();
	  	List<String> stemmedwords = new ArrayList<String>();
	 	PlingStemmer stemmer = new PlingStemmer();
    	
    	
    	try (
    	    InputStream posModelStream = new FileInputStream("openNLPmodels\\en-pos-maxent.bin");
    	    InputStream chunkerStream = new FileInputStream("openNLPmodels\\en-chunker.bin");) {
    		POSModel modelTagger = new POSModel(posModelStream);
    		POSTaggerME tagger = new POSTaggerME(modelTagger);
    		tags = tagger.tag(tokens);
	
    		for(int i=0; i<tags.length; i++) {
    			
		  		String w=tokens[i].toLowerCase();  //lowercase phrase
		  		w=stemmer.stem(w);	//stemming phrase 
		  		if (tags[i].equals("NNS")) token_tag.add(w + "/" + "NN");
		  		else token_tag.add(w + "/" + tags[i]);
    		} 

    	    } catch (IOException ex) {
    	    // Handle exceptions
    	    
    	    }
    	return token_tag;

    	}
  

	/**
	 * opennlp sentence segmentation
	 * @param paragraph
	 * @return
	 */
    public static  String[] opennlp_sentSegmentation(String paragraph) {
    	String[] sentences = null;
    	try (InputStream is = new FileInputStream(
    	        new File("openNLPmodels\\en-sent.bin"))) {
    	
    	   SentenceModel modelSegt = new SentenceModel(is);
    	   SentenceDetectorME detector = new SentenceDetectorME(modelSegt);
    	   sentences = detector.sentDetect(paragraph);
    	   } catch (FileNotFoundException ex) {
    	    	    // Handle exception
    	  } catch (IOException ex) {
    	    	    // Handle exception
    	  }
    	System.out.println("Sentences are segemented.");
    	return sentences;
    }
    
    
    /**
     * extract a list of noun phrases and save it on the disk
     * noun phrases with length > 10 are ignored
     * 
     * @throws IOException
     */
    public static void  extractNounPhrases() throws IOException {
    	

	   /**
	    * initial declaration
	    */
	   List<String> nounPhrases = new ArrayList<>(); 
	   File folder = new File(tagged_stemmedSingleDir);
	   File[] listOfFiles = folder.listFiles();
	   int filecount = listOfFiles.length;
	   int i=0;
	   
	   /**
	    * scan the input folder that contains tokenized and tagged corpus
	    * extract noun phrases in accordance with a pattern
	    */
	   
	   logger.log(Level.INFO, "Data files are being read. Please wait...");
	   for (File file : listOfFiles) {
	   	 if (file.isFile()) {
		  	   LinkedList<Token> tokenList = new LinkedList<>();

			   List<LinkedList<Token>> tokenizedSentenceList = 
			    		new ArrayList<LinkedList<Token>>();
	           i++;
	           String filepath = tagged_stemmedSingleDir + "\\"+ file.getName();
	    	   String combline=""; String line ="";    
		       try {
					BufferedReader br = new BufferedReader(new FileReader(filepath));	
				    
					while ((line=br.readLine()) != null) 
					combline=combline+" "+line;	
				    
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		       
		        logger.log(Level.INFO, "The array of tagged tokens are being generated. Please wait...");
		        
		        //remove duplicate spaces
		        combline=combline.replaceAll("\\s+", " ").trim();
		        
		        //save string to array list
		        List<String> tagged_tokens = Lists.newArrayList(combline.split(" "));
       

		        logger.log(Level.INFO, "Tokenlist is being generated. Please wait...");
		        int j=0; String preToken = "";
		        for (String t : tagged_tokens) {
 
		        	if (!t.equals("") && !t.equals(" ") && !t.equals(preToken))  { //ignore space, null or repeated tokens
		        		String token=t.substring(0,t.lastIndexOf("/"));
		        		String tag = t.substring(t.lastIndexOf("/")+1);
				        tokenList.add(new Token(token, tag));
				        j++;
				        preToken=tagged_tokens.get(j-1); //to check repeated tokens that may not have meanings
		        	}
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
		        
		        logger.log(Level.INFO, "---------" + i + " out of " + filecount + " files processed------");
		        
		        }
	   	 }
	   
	    /**
	    *writing noun phrases
	    *stemming
	    */
 
	    logger.log(Level.INFO, "*********Writting noun phrases. Please wait...");
	   	PrintWriter pw1 = new PrintWriter(new FileOutputStream(phraseFile));
	  	List<String> stemmedwords = new ArrayList<String>();
	 	PlingStemmer stemmer = new PlingStemmer();

	   	for(String c: nounPhrases) {
	   		
	   		/**
			 * //stemming phrase 
	   		List<String> words = Lists.newArrayList(c.split("\\s"));
	   		String stemmedPhrase ="";
	   		if (words.size()<=10) //ignore noun phrases with length >10
				for (String w :words) {
			  		w=w.toLowerCase();  //lowercase phrase
			    	w=stemmer.stem(w);	//stemming phrase 
			    	stemmedPhrase=stemmedPhrase+" " +w;   
			  	}
				pw1.println(stemmedPhrase);*/
	   		pw1.println(c);
	   	}
	    pw1.close(); 
	    
	    logger.log(Level.INFO, "DONE!"); 
        
    }

    /**
     * extract technical terms using C-value method
     * @param phraseFile
     * @throws IOException
     */
    public static void extractTerm(String phraseFile) throws IOException {
    	
    	
    	logger.log(Level.INFO, "*********Collecting and cleaning candidates. Please wait...");
    	
    	HashMap<Candidate,Integer> map = new HashMap<Candidate,Integer>();//map candiates and their frequency
    	String line =""; int percentComplete1=0;int i=0;    
    	try {
				BufferedReader br = new BufferedReader(new FileReader(phraseFile));	
			    long size = br.lines().count();
			    br = new BufferedReader(new FileReader(phraseFile));	
			    
				while ((line=br.readLine()) != null) {
					if(!line.equals("")) { //check empty line
						
						Candidate cand = new Candidate(line, line.split("\\s").length);
		                 
		                if (map.containsKey(cand)) {	                	
		                	map.put(cand, map.get(cand)+1); }
		                else map.put(cand, 1);
					}
	
		             //reporting the progress
	                i++;
	                if (i*100/size > percentComplete1) {
	                    percentComplete1=percentComplete1+1;
	                   logger.log(Level.INFO, percentComplete1 + " percent of temp candidates processed.");
	                } 
				}
			    
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
    	
          
         
        logger.log(Level.INFO,"*********Removing unfrequent noun phrases. Please wait...");
        //List<Candidate> cleanedCand = new ArrayList<>();
        
        Candidate [] cleanedCand = map.keySet().toArray(new Candidate[map.size()]);
        
        List<Candidate> candidates = new ArrayList<>(); 

        for (Candidate c : cleanedCand) {
         	if (map.get(c)>=50) { //ignore phrases occurring less than 50 times in the corpus
         		c.incrementFreq(map.get(c));
         		candidates.add(c);
         	}
        } 


     	Document doc=new Document("C:\\","terms.txt");
         //doc.List(tokenizedSentenceList);
         
        CValueAlgortithm cvalue=new CValueAlgortithm();
        cvalue.init(doc); // initializes the algorithm for processing the desired document. 
        ILinguisticFilter pFilter = new AdjPrepNounFilter(); //filter
        cvalue.addNewProcessingFilter(pFilter);; //for example the AdjNounFilter
        logger.log(Level.INFO, "*********Cvalue algorithm is running...");
        cvalue.setCandidates(candidates);//set candidates to the algorithm
        cvalue.runAlgorithm(); //process the CValue algorithm with the provided filters
        
         
        doc.getTermList(); //get the results
        List<Term> termList = doc.getTermList();


     	logger.log(Level.INFO, "*********Terms being written..."); 
     	
     	PrintWriter pw2 = new PrintWriter(new FileOutputStream(termFile));
     	int k=0;
     	for(Term t: termList) {
     		k++;
     		pw2.println(t.toString());
     	}
         pw2.close(); 
         
         logger.log(Level.INFO, "Terms are saved.");
         
                        
         System.out.println("Top 20 technical terms:");
         
         for (int l=0; l<21; l++) System.out.println(termList.get(l).toString());

    }

/**
 * replacing noun phrases as unique tokens
 * removing stopwords, non-letter tokens from the stemmed corpus
 * @param termFile
 * @param stemmedFile
 * @param term_corpusFile
 * @throws IOException
 */
    public static void replacePhrase(String termFile, String stemmedFile, String term_corpusFile) throws IOException {
    	

		 //HashMap<String,String> terms = new HashMap<String,String>();
		 List<String> terms1 = new ArrayList<>();
		 List<String> terms2= new ArrayList<>(); 
		 
		 
		 //read term file to get the list of phrases
		 logger.log(Level.INFO, "*********Reading term file...."); 
		 String combline=""; String line ="";    
		 BufferedReader br1 = new BufferedReader(new FileReader(termFile));	  
		 while ((line=br1.readLine()) != null) {
		 String phr = line.replaceAll("[^A-Za-z]"," ").trim();
		 terms1.add(phr);}
				/**
				 * 
				 
				List<String> words = Lists.newArrayList(phr.split(" "));
				String mainWord ="";
				if (words.contains("of")) {
					int pos = words.indexOf("of");
					mainWord = words.get(pos-1);					
				}
				else  mainWord = words.get(words.size()-1);
				String newMainWord=mainWord+"_"+i;
				phr=phr.replace(mainWord, newMainWord);
				terms2.add(phr);*/				

				

	     
    	 //read the corpus file
	     logger.log(Level.INFO, "*********Reading corpus file...."); 
    	 combline=""; line ="";
    	 BufferedReader br2 = new BufferedReader(new FileReader(stemmedFile));	
			    
		 while ((line=br2.readLine()) != null) {	
			 combline=combline+" "+line;	
			 combline=combline.replaceAll("\\s+", " ").trim();}
	     
	     /**
	      * 
	      
	     //sort the list by term length
	     logger.log(Level.INFO, "*********Sorting term list by length...."); 
	     Set<Entry<String, Integer>> set = terms.entrySet();
	     List<Entry<String, Integer>> sortedList = new ArrayList<Entry<String, Integer>>(
	             set);
	     Collections.sort(sortedList, new Comparator<Map.Entry<String, Integer>>() {
	         public int compare(Map.Entry<String, Integer> o1,
	                 Map.Entry<String, Integer> o2) {
	             return o2.getValue().compareTo(o1.getValue());
	         }
	     });

    	//replacing a noun phrases with a unique token in corpus
	    logger.log(Level.INFO, "*********Replacing terms with tokens...."); 
    	int size = sortedList.size(); int i =0;
	    for (Entry<String, Integer> entry : sortedList) {
	        String phr =  " "+entry.getKey()+" ";
	   		String linkedphr = " "+ entry.getKey().replaceAll("\\s", "-")+" ";
    		combline=combline.replaceAll(phr, linkedphr);
    		
    		i++;
    		System.out.println(i + " out of " + size +" phrases are replaced.");
	    }*/
	    
    	//replacing a noun phrases with a unique token in corpus
	    logger.log(Level.INFO, "*********Replacing terms with tokens...."); 
    	int size = terms1.size(); int i =0;
    	
	    for (String t : terms1) {
	        String phr1 =  " "+t+" ";
	        String phr2 = " "+t+" term:"+ t.replaceAll("\\s", "-")+" ";
    		combline=combline.replaceAll(phr1, phr2);	
    		i++;
    		System.out.println(i + " out of " + size +" phrases are replaced.");
	    }

	     
	    //removing stop words, number, non-letter tokens from the corpus
	    //writing corpus on disk
	    logger.log(Level.INFO, "*********Rewritting the modified corpus...");
	    List<String> stopwords = Arrays.asList(StopWords.EnglishStopWords());
	    List<String> words =Lists.newArrayList(combline.split(" "));
     	PrintWriter pw = new PrintWriter(new FileOutputStream(term_corpusFile));
    	for (String w :words) {
    		
    		if (w.equals(".")) pw.println();
    		
    		String w2 = w.replaceAll("[^a-zA-Z]"," ").trim();// to remove non-letter tokens
	  		if (!w2.equals("") && !stopwords.contains(w)  ) {// to remove stop words	  																										
	  			pw.print(w+" ");
	  		}

	  	}
    	pw.close();
 
        logger.log(Level.INFO, "DONE!"); 

    }
    
    public static void MDVgenerate(String modelPath) throws ParserConfigurationException, 
    SAXException, IOException, TException, UnknownWordException{

		com.medallia.word2vec
					.Word2VecModel model = Word2Vec_Medallia.loadModel(modelPath);
       
        LandxmlLexicon lexicon = readLandXML();
    	HashMap<Integer,LandxmlNode> landXMLEnt = lexicon.getMap();

		try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
			while (true) {
				System.out.print("Enter word or term (press ENTER to break): ");
				String phrase = br.readLine().trim();
				if (phrase.equals("")) {
					System.out.println("Programe is shut off.");
					break;
				}
				
				//interpreting input string
				List<Concept> cons = Concept.getConcepts(phrase);
				TreeMap<String, Double> matchEntries=new TreeMap<String,Double>();
				for (Concept con: cons) {
					matchEntries.putAll(lexicon.searchAlgorithm2(model, con, 30,10, 0.3));
				}
				
				//print the results
		 	   	System.out.println("********Mapping result**********************************");
		 	    System.out.println("--------Required data---------------------LandXML entity");


		    	matchEntries = (TreeMap<String, Double>) shortTreeMap.sortByValues(matchEntries);
		    	int i=0;
		    	for (Entry e: matchEntries.entrySet()){
		    		i++; if (i>5) break;
		    		String sourceEnt= e.getKey()+":"+e.getValue();
		      	    System.out.println(sourceEnt);	
		    	}
		    	System.out.println("********************************************************");

				}
			}	
    }

    public static LandxmlLexicon readLandXML() throws ParserConfigurationException, SAXException, IOException {

	
    	LandxmlLexicon landxmlLexicon = new LandxmlLexicon("LandxmlSchema");
    	
    	HashMap<Integer,LandxmlNode> landxmlNodeList = new HashMap<Integer, LandxmlNode>();

    	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        File file = new File(landxmlSchemaPath);
	    if (file.exists()) {
	          org.w3c.dom.Document doc = db.parse(file);
	          Element docEle = doc.getDocumentElement();

              HashSet<Node> nodeList = NodeList2List(docEle.getElementsByTagName("xs:element"));
              nodeList.addAll(NodeList2List(docEle.getElementsByTagName("xs:simpleType")));
              landxmlLexicon.addNodes(nodeList);
	    }

	    landxmlLexicon.print(landxmlDir);	    
	    return landxmlLexicon;
        
    }

    private static HashSet<Node> NodeList2List(NodeList nodes)
    {
      HashSet<Node> set = new HashSet<Node>(nodes.getLength());
      for (int i = 0, l = nodes.getLength(); i < l; i++)
        set.add(nodes.item(i));
      return set;
    }

}
