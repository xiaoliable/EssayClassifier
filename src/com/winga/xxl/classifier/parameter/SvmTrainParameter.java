package com.winga.xxl.classifier.parameter;

import com.winga.xxl.classifier.model.outputer.IModelOutputer;
import com.winga.xxl.classifier.model.outputer.SvmXmlModelOutputer;

public class SvmTrainParameter extends TrainParameter{

	public String argument;
	public IModelOutputer outputer = null;
	
	public SvmTrainParameter(){}
	
	public SvmTrainParameter(String argument){
		this.argument = argument;
	}
	
	public SvmTrainParameter(String argument, String outputPath){
		this.argument = argument;
		if(outputPath != null)
		this.outputer = new SvmXmlModelOutputer(outputPath);
	}
}
