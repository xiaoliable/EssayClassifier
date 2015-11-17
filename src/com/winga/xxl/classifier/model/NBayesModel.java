package com.winga.xxl.classifier.model;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Store every word's conditional possibility in the marked-category sample documents.
 * */
public class NBayesModel implements IModel{

	public Map<String, Map<Long, Double>> condprob = new HashMap<String, Map<Long, Double>>();
	
	public Map<String, Double> priorProb = new HashMap<String, Double>();
	
	public NBayesModel(){}
	
	public NBayesModel(Map<String, Map<Long, Double>> condprob, Map<String, Double> priorProb){
		this.condprob = condprob;
		this.priorProb = priorProb;
	}
}
