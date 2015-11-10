package edu.isu.ce.mdvgenerator;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang3.text.WordUtils;
import org.hcmut.tuyen.Word2Vec_Medallia;
//import org.hcmut.dl4j.App;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



import com.google.common.collect.Lists;

import java.util.Map.Entry;

import com.medallia.word2vec.Searcher.UnknownWordException;
import com.medallia.word2vec.Word2VecModel;

public class LandxmlLexicon {
	Logger log = LoggerFactory.getLogger(LandxmlLexicon.class);
	private HashMap<Integer,LandxmlNode> landxmlNodeList = new HashMap<Integer, LandxmlNode>();
	private HashSet<Integer> IDList	 = new HashSet<Integer>();
	private int rootID;
	int tempID ;

	public LandxmlLexicon(String rootNodeName) {
		rootID = generateID();
		LandxmlNode root = new LandxmlNode(rootNodeName,rootID) ;
		landxmlNodeList.put(rootID, root);
	}
	
	public HashMap<Integer,LandxmlNode> getMap(){
		return this.landxmlNodeList;
	}
	
	public void addNodes (HashSet<Node> list) throws IOException {
		
		String ref  = "";
		String name = "";
		String fullName="";
		String type = "";
		String value = "";
		String desc ="";
		int ID = 0;
		
		for (Node node: list) {

			Element ele = (Element)node;
			name = ele.getAttribute("name").trim();
			
			
			
			type = ele.getAttribute("type").trim();
			desc = ele.getTextContent().trim();
			
			

			//add new landxml term to the lexicon
			if (name!="") {
				addChildID(rootID, name,true);
				ID=tempID;
				//guessing the full name for landxml node name
				fullName=this.landxmlNodeList.get(ID).FullName();			
				fullName=LandxmlNode.guessTerm(name, fullName, desc);
				this.landxmlNodeList.get(ID).setDescription(desc);
				this.landxmlNodeList.get(ID).setFullName(fullName);

			}
			
			
			if (type!="") {
				addChildID(ID, type,true);
			}
			
			

			HashSet<Node> fullChildren = getFullChildren(node);
			for (Node child : fullChildren) {
				
					short nodetype = child.getNodeType();
					name=""; ref=""; type=""; value="";
					
 
					Element childEle = (Element)child;

					ref  = childEle.getAttribute("ref");
					name = childEle.getAttribute("name");
					type = childEle.getAttribute("type");
					value = childEle.getAttribute("value");
	
					if (name!="") {  
						if(childEle.getNodeName().equals("xs:attribute")) {
							addChildID(ID,name,false);	
						}
						else addChildID(ID,name,true);			  
					}
					
					else if (ref!="") {
						addChildID(ID,ref,true);
					}
					
					else if (value!="") {
						addChildID(ID,value,false);
					}
					
					int childID=tempID;

					if (type !="") {
						addChildID(childID, type, true);
					}

	  
			  }
		}
	}
	

	public void addChildID(Integer parentID, String childName, Boolean checkDuplicate) {
	

		//check if the list already contains this type
		boolean isExisted=false;	
		int childID = 0;
		if (checkDuplicate!=false) {
			for (Entry e : this.landxmlNodeList.entrySet()) {
				LandxmlNode node = (LandxmlNode) e.getValue();
				
				if (node.Name().equals(childName)) {
					childID= (Integer) e.getKey();
					if (parentID!=rootID){
					this.landxmlNodeList.get(childID).Parents().add(parentID);}
					
					isExisted =true; break;
				}
			}
		}

		if (isExisted==false) {
			childID =generateID();
			LandxmlNode lxn = new LandxmlNode(childName, childID);
			if (parentID!=rootID){
			lxn.Parents().add(parentID);}
			this.landxmlNodeList.put( childID, lxn);
			
		}
		
		this.tempID=childID;
		this.landxmlNodeList.get(parentID).Children().add(childID);

	}

    private int generateID(){
    	int id;
    	Random rand = new Random();
  	  	while (true) {
		  int size = this.IDList.size();
		  id= rand.nextInt(99999999)+10000000;  //8 digit ID
		  this.IDList.add(id);
		  if (this.IDList.size()>size) break; //check unique ID	
  	  	}
    	return id;
    }
    
    private static HashSet<Node> getFullChildren(Node n)
    {
      HashSet<Node> children = NodeList2List(n.getChildNodes());
      HashSet<Node> fullChildren = new HashSet<Node>();
      for (Node child: children) {

    	  if (child.hasChildNodes()){
    		  fullChildren.addAll(getFullChildren(child));
    	  }
    	  else if (child.getNodeType() == Node.ELEMENT_NODE) {
    		  fullChildren.add(child);
    	  }
      	}
      return fullChildren;
    }
    
    private static HashSet<Node> NodeList2List(NodeList nodes)

    {
      HashSet<Node> set = new HashSet<Node>(nodes.getLength());
      for (int i = 0, l = nodes.getLength(); i < l; i++)
        set.add(nodes.item(i));
      return set;
    }   
    
    public List<String> getChildrenFullNames(LandxmlNode node) {

    	List<String> contexts = new ArrayList<String>();

  	    //add children

      	if (node.Children().size()==1) {//expand up to grand children level
  	    	int childID = node.Children().get(0);
  	    	LandxmlNode childNode = this.landxmlNodeList.get(childID);
  	    	//add grand children instead of children
  	    	for (int c: childNode.Children()) {
  	    		LandxmlNode gradChildNode = this.landxmlNodeList.get(c);
  	    		contexts.add(gradChildNode.FullName());
  	    		}
      	}
  	    else {
      	    for (int c:node.Children()) {
      	    	LandxmlNode childrenNode = this.landxmlNodeList.get(c);	
      	    	contexts.add(childrenNode.FullName());}  	      	    	
  	    }


    	return contexts;
    }
    
    public List<String> getParentNames(LandxmlNode node) {
    	
    	List<String> contexts = new ArrayList<String>();

  	    //add parent
  	    for (int p:node.Parents()) {
  	    	LandxmlNode parentNode = landxmlNodeList.get(p);
  	    	contexts.add(parentNode.Name());
  	    } 
	
    	return contexts;
    }
    
    public List<String> getFullContext(int LandxmlNodeID) {
    	
    	List<String> contexts = new ArrayList<String>();

  		for (Entry e: landxmlNodeList.entrySet()) {
  			
      	    LandxmlNode node = (LandxmlNode) e.getValue();
      	    if (node.getID()==LandxmlNodeID) {
      	    //add itself
      	    contexts.add(node.Name()); 
      	    //add parent
      	    for (int p:node.Parents()) {
      	    	LandxmlNode parentNode = landxmlNodeList.get(p);
      	    	contexts.add(parentNode.Name());
      	    } 
      	    //add children
      	    for (int c:node.Children()) {
      	    	LandxmlNode childrenNode = landxmlNodeList.get(c);
      	    	contexts.add(childrenNode.Name());
      	    } }
	    } 	
    	return contexts;
    }
    
    public void print(String dataFolder) throws FileNotFoundException{
 
	    //print landxml lexicon to files
    	PrintWriter printIndex = new PrintWriter(new FileOutputStream(dataFolder+"\\landxml_term_index.txt"));
	    PrintWriter printData = new PrintWriter(new FileOutputStream(dataFolder+"\\landxml_lexicon.txt"));
	    
	 	for (Entry e: this.landxmlNodeList.entrySet()) {
	      	    LandxmlNode node = (LandxmlNode) e.getValue();
	      	    
	      	    String strNode = node.getID()+" "+node.Name()+" <"+node.FullName()+"> <desc:"+node.getDescription()+">";
	      	    
	      	    printIndex.println(strNode);

	      	    String strParents ="";
	      	    for (int p:node.Parents()) {
	      	    	LandxmlNode parentNode = this.landxmlNodeList.get(p);
	      	    	strParents=strParents +" "+parentNode.getID()+" "+ parentNode.Name();//add parents
	      	    }
	      	    
	      	    if (!strParents.equals("")) strNode=strNode+" "+"p"+"("+strParents.trim()+")";
	      	    
	      	    String strChildren ="";
	      	    for (int c:node.Children()) {
	      	    	LandxmlNode childrenNode = this.landxmlNodeList.get(c);
	      	    	strChildren=strChildren+" "+childrenNode.getID()+" "+ childrenNode.Name();//add parents
	      	    }
	      	    if (!strChildren.equals("")) strNode = strNode+" "+"c"+"("+strChildren.trim()+")";
	      	    printData.println(strNode);
		}
	 	System.out.println("Landxml lexicon has been saved at "+dataFolder);
    }

    public String searchAlgorithm1(Word2VecModel model, String term) throws IOException, UnknownWordException {
    	String landxmlnode="";
    	
    	NearestWords set = Word2Vec_Medallia.singleSearch(model.forSearch(), term,30);

 	   	double maxTotal= Double.MIN_VALUE;
 	   
 	   	double minSim =0.5;

 	   	String sourceEnt =""; String tempsourceEnt ="";String sourceChain="";
   
  	    System.out.println("********Mapping result**********************************");
  	    System.out.println("--------Required data---------------------LandXML entity");
  	    
 	   	for (Entry e: this.landxmlNodeList.entrySet()) {
      	    LandxmlNode node = (LandxmlNode) e.getValue();  
      	    sourceChain=node.FullName();
      	    
      	    int count=0;double sim=0;double simTotal =0;double simTotalAvg=0; 
      	    
      	    //itself
      	    TreeSet<String> contexts = new TreeSet<>();
      	    contexts.add(node.FullName());
      	    sim=findSim(contexts, set.getSynonymList().keySet(),minSim);
      	    if (sim>minSim) {count++;
      	    simTotal = simTotal+ sim;} //more weight for itself match, less weight for children matches.
   	    
      	    //parents
      	    String strParents ="";
      	    contexts.clear();
      	    for (int p:node.Parents()) {
      	    	LandxmlNode parentNode = this.landxmlNodeList.get(p);
      	    	contexts.add(parentNode.FullName()); 	
      	    	strParents=strParents+", "+parentNode.FullName();
      	    }
      	    sourceChain="p"+"("+strParents.trim()+")"+"-->"+sourceChain;
      	    sim=findSim(contexts, set.getHypernymList().keySet(),minSim);
      	    if (sim>minSim) {count++;
      	    simTotal = simTotal+ sim;}

      	    
      	    //children
      	    String strChildren="";
   	      	contexts.clear();
  
   	      	if (node.Children().size()==1) {
      	    	int childID = node.Children().get(0);
      	    	LandxmlNode childNode = this.landxmlNodeList.get(childID);
      	    	
      	    	for (int c: childNode.Children()) {
      	    		LandxmlNode gradChildNode = this.landxmlNodeList.get(c);
      	    		contexts.add(gradChildNode.FullName());
      	    		strChildren=strChildren+", "+gradChildNode.FullName();}
   	      	}
      	    else {
  	      	    for (int c:node.Children()) {
  	      	    	LandxmlNode childrenNode = this.landxmlNodeList.get(c);	
  	      	    	contexts.add(childrenNode.FullName());
  	      	    	strChildren=strChildren+", "+childrenNode.FullName();}  	      	    	
      	    }
      	    sourceChain=sourceChain+"-->"+"c"+"("+strChildren.trim()+")";
      	    
      	    sim=findSim(contexts, set.getSynonymList().keySet(),minSim);
      	    
      	    if (sim>minSim) {count++;
      	    simTotal = simTotal+ sim;}
      	    
      	    simTotalAvg=simTotal/Math.pow(count,1/2); 
      	    
      	    if (simTotalAvg>maxTotal*0.8 && node.Name()!="LandxmlSchema") {
      	    	if (simTotalAvg>maxTotal)	{
      	    		maxTotal=simTotalAvg;
      	    		sourceEnt =sourceChain;	
      	    	}
      	    	tempsourceEnt =sourceChain;	
      	    	System.out.println(tempsourceEnt+":"+simTotalAvg+":"+count);
      	    }  	
      	    
	    }
 	   	//System.out.println(maxTotal);

  	    System.out.println("        "+term+"                "+sourceEnt);
    	System.out.println("*****************************************************");
    	return sourceEnt; 
    }

    public TreeMap<String, Double> searchAlgorithm2 (Word2VecModel model, 
    		Concept concept, Integer topSyns, Integer topContexts, Double minSim) throws IOException, UnknownWordException {
    	
    	//log.info("Initilizing ....");	

    	String conName = concept.Name();
    	String conAtt = concept.Attribute();
    	String phrase = concept.Phrase();
    	
    	NearestWords attNearests = Word2Vec_Medallia.singleSearch(model.forSearch(), phrase, topSyns);
    	
    	
    	NearestWords conNearests = Word2Vec_Medallia.singleSearch(model.forSearch(), conName,topSyns);
    	//Word2Vec_Medallia.printSynonyms(conNearests);

    	//Word2Vec_Medallia.printSynonyms(attNearests);
    	
 	   	TreeSet<String> sourceContexts = new TreeSet<>();
 	   	Set<String> targetContexts =null;
 	   	List<LandxmlNode> matches = new ArrayList<LandxmlNode>();
 	   	TreeMap<String,Double> matchEntries = new TreeMap<String,Double>();
 	   	//LandxmlNode match=null;
 	   	Map.Entry<String, Double> mainMatchEntry=null;
 	   	Map.Entry<String, Double> subMatchEntry=null;
 	   	String sourceEnt ="NO result";String sourceChain="";
 	   	String attMatch=""; double attSim=0;
 	   	
 	   	//String[] words = term.split(" ");
  	    //String part1 =words[0];
  	    //String part2=""; if (words.length>1) part2=words[1];
  	    Double simMax1=Double.MIN_VALUE; 
  	    
  	    
  	    //case 1: term has form NN+NN
  	    
  	    //System.out.println("Stage 1 <name based search> ....");
  	    
  	    /**Stage 1a - find a list of name matches
  	     * find synonyms of word1
  	     * find candidate match of each synonym
  	     */
  	    
  	    int i=0;
  	    for (String syn : conNearests.getSynonymList().keySet()) {
  	    	
  	    	i++; if (i>15) break; //consider top 5 synonyms only
  	    	if (syn.contains("[")) {
  				syn=syn.substring(0, syn.indexOf("[")-1);}
  		
  			if (syn.contains("syn:")) {
  				syn = syn.replaceAll("syn:", "");
  				syn = syn.replaceAll("-", " ");}
  			
  	    	for (Entry e: this.landxmlNodeList.entrySet()) {
  	 	   		double sim=0;
  	      	    LandxmlNode node = (LandxmlNode) e.getValue();  
  	      	    
  	      	    String nodeName=node.Name();
  	      	    String nodeFName = node.FullName(); 
  	      	    
  	      	    if (nodeFName.length()==2*nodeName.length()-1) nodeFName=nodeName; //e.g., ADT --> A D T; 
  	      	    
  	      	    if ((" "+nodeFName.toLowerCase()+" ").contains((" "+syn.toLowerCase()+" "))) {//make sure roadway not contain road
  	      	    sim = Stringsimilarity.similarity(syn, nodeFName);	
  	      	    }
  	      	    
  	      	    if (sim<minSim) {mainMatchEntry=null;}
  	      	    else {
  	      	    	mainMatchEntry = new AbstractMap.SimpleEntry<String,Double>(nodeName,sim);

  	      	    	String strParents =String.join(", ", getParentNames(node)); 
  	      	    	String chain="p"+"("+strParents+")"+"-->"+mainMatchEntry.getKey();

  	      	    	targetContexts= attNearests.getSynonymList().keySet();
  	      	    	
  	      	    	//check if sharing attributes
  	      	    	subMatchEntry= matchContext(conName, conAtt, node, targetContexts, topContexts, minSim);
  	      	    	if (subMatchEntry!=null) {
  	      	    		attMatch=subMatchEntry.getKey();
  	      	    		attSim =subMatchEntry.getValue();
  	      	    	}
  	      	    	else {
  	      	    		attMatch="";
	      	    		attSim =1;
  	      	    	}
  	      	    	
  	      	    	if (subMatchEntry!=null || conAtt.equals("")) {
  	      	    		chain=chain+"-->"+"c"+"("+attMatch+")";
  	      	    		matchEntries.put(conName+"(~"+syn+"):"+chain, mainMatchEntry.getValue()*attSim);
  	      	    		matches.add(node);
  	      	    	}

  	      	    }
  		    }
  	    }
  	    /**
		 * 
 	   	System.out.println("********Mapping result**********************************");
 	    System.out.println("--------Required data---------------------LandXML entity");

		
		 
    	matchEntries=(TreeMap<String, Double>) shortTreeMap.sortByValues(matchEntries);
    	for (Entry e: matchEntries.entrySet()){
    		sourceEnt= e.getKey()+":"+e.getValue();
      	    System.out.println(sourceEnt);	
    	}
    	System.out.println("********************************************************");*/
    	
  	    return matchEntries;
    }
    
    private Map.Entry<String,Double> matchContext(String concept, 
    													String attribute,
    													LandxmlNode sourceNode, 
    													Set<String> targetContexts,
    													Integer topContexts, Double minSim){
    	
    	Set<String> sourceContexts= new TreeSet<String>();
    	sourceContexts.addAll(getChildrenFullNames(sourceNode));
    	Map.Entry<String, Double> match=null;	
    	String attMatch="";
    	
    	String strChildren =String.join(", ", getChildrenFullNames(sourceNode));
	      	
    	/**
      	 * check the attributes of each match candidate
      	 */
 	   	if (sourceNode!=null && !attribute.equals("")) {
 	   		//log.info("Searching for attribute ....");
	 	   	
	 	   	Map.Entry<String, Double> matchEntry=null;
	 	   	matchEntry= simpleSearch(attribute, sourceContexts, minSim);
	 	   	
	 	   	if (matchEntry!=null) match=matchEntry;
	 
	 	   	else {
	 	   		int j=0; Double simMax=Double.MIN_VALUE;
	 	   		for (String target:targetContexts) {
	 	   			
	 	   			j++; if (j>topContexts) break;//check top 5 target contexts only
	 	   			if (!target.contains(concept)){
		   	      		matchEntry = simpleSearch(target, sourceContexts, minSim);
		   	      		if (matchEntry!=null && matchEntry.getValue()>simMax) {
		   	      			simMax=matchEntry.getValue();
		   	      			match=matchEntry;
		   	      			//System.out.println(target+":"+subMatchEntry.getKey()+":"+subMatchEntry.getValue());
		   	      		}
		   	      	}
	 	   		}

	 	   	}
 	   	}   	
 	   	return match; 	   
    }
    
    private Map.Entry<String,Double>  simpleSearch(String term, Set<String> sourceTerms, Double minSim){

    	Map.Entry<String, Double> match =null; double simMax=Double.MIN_VALUE;
    	String matchValue = ""; double matchScore=0;
    	
    	if (term.contains("[")) {
			term=term.substring(0, term.indexOf("[")-1);}
	
		if (term.contains("term:")) {
			term = term.replaceAll("term:", "");
			term = term.replaceAll("-", " ");}
    	
    	term=term.toLowerCase();
    	List<String> targets = Lists.newArrayList(term.split(" "));
    	targets.add(term);//add itself, other combination should be added

    	double sim=0;
    	for (String target: targets) {
    		for (String source: sourceTerms) {	
    			source =source.toLowerCase();//" "+ +
    			if ((" "+source+" ").contains(" "+target+" ")) {//" ", make sure roadway not contain road
    				//System.out.println(source);
    				sim = Stringsimilarity.similarity(term,source);}
	      	    if (sim>simMax)  {simMax=sim ; matchValue=source;}}
    	}
    	if (simMax<minSim) match=null;
    	else match = new AbstractMap.SimpleEntry<String,Double>(matchValue,simMax);
    	return match;
    }
    
	private Double findSim(TreeSet<String> sourceContexts, Set<String> targetContexts, Double minSim){
		
			Stringsimilarity stringSim = new Stringsimilarity();
			double simTotal =0; int pos=0;String word ="";
			for (String source:sourceContexts) {
					//seperate words in landxml nodes
					String separatedNode =source.toLowerCase();
		   	
					double sim=0;double max=0;
				   	
				   	String[] arr1 = separatedNode.trim().split(" ");
					int i=0;
			    	for (String target: targetContexts) {	
			    		i++; if (i>5) break; //consider only top 5 context terms
			    		word = target;
			    		if (target.contains("[")) {
						 word=target.toLowerCase().substring(0, target.indexOf("[")-1);}
						
						if (word.contains("term:")) {
							word = word.replaceAll("term:", "");
							word = word.replaceAll("-", " ");}
						if (source.toLowerCase().contains(word)) {
						sim = stringSim.similarity(word,source);}
		
		  	      	    if (sim>max)  max=sim ; }
			    	
			    	if (max>minSim)  simTotal=simTotal+max;
			
			    }
			
			return simTotal;
		}

}
