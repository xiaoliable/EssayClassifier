package com.winga.xxl.classifier.model;

import com.winga.xxl.classifier.data.store.IDocuments;

public class KMeansModel implements IModel{

	public IDocuments[] centers = null;
	
	public KMeansModel(){}
	
	public KMeansModel(IDocuments[] centers){
		this.centers = centers;
	}
}
