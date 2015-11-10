package edu.isu.ce.mdvgenerator;
//import edu.mit.jwi.IDictionary;
//import edu.mit.jwi.item.*;
import java.net.*;
//import edu.mit.jwi.Dictionary;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Hashtable;
import java.util.TreeMap;

public class contextFinder {
	/**
	 * PathFinder finds the shortest path in WordNet between two synsets (other methods to be implemented see: the equivalent Perl module).
	 * In theory, you can have any combination of synsets / parts of speech as we are using all the Pointer types and thus, somewhere in the
	 * space we should find a connection
	 *
		David Hope, 2008, University Of Sussex

	 */

	/**
	 * 
	 
	 	private IDictionary dict = null;

		public PathFinder(IDictionary dict)
		{
			System.out.println("... PathFinder");

			this.dict = dict;
		}*/

		private double getShortestPath(LandxmlNode a, LandxmlNode z)
		{
			double sp 		=	Double.MAX_VALUE;;
			HashSet<Integer>	A	= new HashSet<Integer>();
			A.add(a.getID());
			TreeMap<Integer, HashSet<Integer>>	AA	= new TreeMap<Integer, HashSet<Integer>>();
			getHypernyms(0, A, AA);

			HashSet<Integer>	Z	= new HashSet<Integer>();
			Z.add(z.getID());
			TreeMap<Integer, HashSet<Integer>>	ZZ	= new TreeMap<Integer, HashSet<Integer>>();
			getHypernyms(0, Z, ZZ);

			for(Integer i : AA.keySet())
			{
				HashSet<Integer> setA = AA.get(i);
				for(Integer j : ZZ.keySet())
				{
					HashSet<Integer> setZ = ZZ.get(j);
					HashSet<Integer>	join	=	new HashSet<Integer>();
					join.addAll(setA);
					join.retainAll(setZ);
					if(!join.isEmpty())
					{
						if((i+j) < sp)
						{
							sp = (i+j);
						}
					}
				}
			}
			return ( sp + 1.0 );
		}

		private void getHypernyms(int pathlength, HashSet<Integer> synsets, TreeMap<Integer, HashSet<Integer>> paths)
		{
			pathlength++;
			HashSet<Integer> 	hypernyms	=	new HashSet<Integer>();
			for(Integer s : synsets)
			{
				//LandxmlNode		synset 	= dict.getSynset(s);
				//hypernyms.addAll(synset.getRelatedSynsets(Pointer.HYPERNYM)); 					// get the <hypernyms> if there are any
	 			//hypernyms.addAll(synset.getRelatedSynsets(Pointer.HYPERNYM_INSTANCE));	// get the <hypernyms> (instances) if there are any
			}
			if(!hypernyms.isEmpty())
			{
				paths.put(pathlength, hypernyms);
				getHypernyms(pathlength, hypernyms, paths);
			}
			return;
		}
}
