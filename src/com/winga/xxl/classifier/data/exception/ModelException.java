package com.winga.xxl.classifier.data.exception;

/**
 * If the model is not fit this classifier, it will be error.
 * <p>CreateTime : 2014-11-19
 * @author xiaoxiao
 * */
public class ModelException extends Exception{

	public ModelException(){}
	
	public ModelException(String msg){
		super(msg);
	}
}
