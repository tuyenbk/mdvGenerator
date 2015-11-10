package edu.isu.ce.mdvgenerator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

public class NearestWords {
private TreeMap<String,Double> fullSet = new TreeMap<String,Double>();
private TreeMap<String,Double> synSet =  new TreeMap<String,Double>();
private TreeMap<String,Double> attSet =  new TreeMap<String,Double>();
private TreeMap<String,Double> hypeSet =  new TreeMap<String,Double>();
private TreeMap<String,Double> hypoSet =  new TreeMap<String,Double>();
private TreeMap<String,Double> relSet =  new TreeMap<String,Double>();
private String	word="";

public static final String Syn_Tag = "Synonym";
public static final String Hype_Tag = "Hypernym";
public static final String Hypo_Tag = "Hyponym";
public static final String Att_Tag = "Attribute";
public static final String Rel_Tag = "Relation";

public NearestWords(String word){
	this.word=word;
}

public String getWord(){
	return this.word;
}

public void setWord(String word){
	this.word=word;
}

public TreeMap<String,Double> getFullList(){
	return this.fullSet;
}

public TreeMap<String,Double> getSynonymList(){
	return this.synSet;
}

public TreeMap<String,Double> getAttributeList(){
	return this.attSet;
}

public TreeMap<String,Double> getHypernymList(){
	return this.hypeSet;
}

public TreeMap<String,Double> getHyponymList(){
	return this.hypoSet;
}

public TreeMap<String,Double> getRelateList(){
	return this.relSet;
}

/**
 * map<String tag, String word>
 * @param map
 */
public void add(String entryKey, String entryValue) {
		//String word = entryKey.substring(0, entryKey.indexOf("[")-1);
		String word = entryKey;
		Double simScore=Double.valueOf(entryKey.substring(entryKey.indexOf("[")+1,entryKey.indexOf("]")-1));
		this.fullSet.put(word, simScore);
	
		if (entryValue.equals(this.Att_Tag)) {this.attSet.put(word, simScore);}
		else if (entryValue.equals(this.Hype_Tag)) {this.hypeSet.put(word, simScore);}
		else if (entryValue.equals(this.Hypo_Tag)) {this.hypoSet.put(word, simScore);}
		else if (entryValue.equals(this.Rel_Tag)) {this.relSet.put(word, simScore);}
		else  {this.synSet.put(word, simScore);}
		
		//descending sort by value
		//sortByValues(this.fullSet);
		//sortByValues(this.attSet);
		//sortByValues(this.hypeSet);
		//sortByValues(this.hypoSet);
		//sortByValues(this.relSet);
		//sortByValues(this.synSet);

}

public void sortAllMapsByValues(){
	this.fullSet=(TreeMap<String, Double>) sortByValues(this.fullSet);
	this.synSet=(TreeMap<String, Double>) sortByValues(this.synSet);
	this.hypeSet=(TreeMap<String, Double>) sortByValues(this.hypeSet);
	this.hypoSet=(TreeMap<String, Double>) sortByValues(this.hypoSet);
	this.attSet=(TreeMap<String, Double>) sortByValues(this.attSet);
	this.relSet=(TreeMap<String, Double>) sortByValues(this.relSet);
	
}

public static <K, V extends Comparable<V>> Map<K, V> sortByValues(final Map<K, V> map) {
    Comparator<K> valueComparator =  new Comparator<K>() {
        public int compare(K k1, K k2) {
            int compare = map.get(k2).compareTo(map.get(k1));
            if (compare == 0) return 1;
            else return compare;
        }
    };
    Map<K, V> sortedByValues = new TreeMap<K, V>(valueComparator);
    sortedByValues.putAll(map);
    return sortedByValues;
}

}
