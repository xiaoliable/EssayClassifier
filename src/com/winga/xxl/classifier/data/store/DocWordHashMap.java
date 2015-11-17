package com.winga.xxl.classifier.data.store;

import java.util.Map;
import java.util.TreeMap;

/**
 * <p>For the naive bayes classifier.</p>
 * <p>CreateDate : 2014-9-5</p>
 * <p>The global corresponding murmurHash value map to the String dictionary. </p>
 * <p>This class contains all the IK word segmentation in the title and content String.</p>
 * <p>Function: Hash the plenty of IK word string segmentation so as to normalization.</p>
 * <p>For instance, print the debug word string in the lastCenter.xml file.</p>
 * @author xiaoxiao
 * @version 1.0
 */
public class DocWordHashMap {

	private static DocWordHashMap singleton = new DocWordHashMap();
    private DocWordHashMap (){}
    public static DocWordHashMap getInstance(){return singleton;}
    
	private Map<Long, String> wordStringHash = new TreeMap<Long, String>();
	
	public Map<Long, String> getWordStringHash(){
		return this.wordStringHash;
	}
	
	public void setWordStringHash(Long hash , String word){
		wordStringHash.put(hash, word);
	}
	
	public boolean isContainKey(Long hash){
		return wordStringHash.containsKey(hash);
	}
}
