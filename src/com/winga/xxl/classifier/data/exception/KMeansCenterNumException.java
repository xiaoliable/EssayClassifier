package com.winga.xxl.classifier.data.exception;

/**
 * If the given category number is not same as the length of centers documents array, it will be error.
 * <p>CreateTime : 2014-11-19
 * @author xiaoxiao
 * */
public class KMeansCenterNumException extends Exception{

	public KMeansCenterNumException(){}
	
	public KMeansCenterNumException(String msg){
		super(msg);
	}
}
