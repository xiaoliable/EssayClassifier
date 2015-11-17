package com.winga.xxl.classifier.data.exception;

/**
 * If the train parameter is not fit this classifier, it will be error.
 * <p>CreateTime : 2014-11-19
 * @author xiaoxiao
 * */
public class TrainParamException extends Exception{

	public TrainParamException(){}
	public TrainParamException(String msg){
		super(msg);
	}
}
