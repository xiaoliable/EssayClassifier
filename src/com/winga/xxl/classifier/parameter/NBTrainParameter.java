package com.winga.xxl.classifier.parameter;

import com.winga.xxl.classifier.model.outputer.IModelOutputer;
import com.winga.xxl.classifier.model.outputer.NBXmlModelOutputer;

/**
 * Naive Bayes classifier's parameter.
 * */
public class NBTrainParameter extends TrainParameter{

	public int featureNum;
	
	public IModelOutputer outputer = null;
	
	public NBTrainParameter(){}
	
	public NBTrainParameter(int featureNum){
		this.featureNum = featureNum;
	}
	public NBTrainParameter(int featureNum, String outputPath){
		this.featureNum = featureNum;
		if(outputPath != null)
		this.outputer = new NBXmlModelOutputer(outputPath);
	}
}
