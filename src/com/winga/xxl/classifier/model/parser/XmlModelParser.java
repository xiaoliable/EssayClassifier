package com.winga.xxl.classifier.model.parser;

import com.winga.xxl.classifier.model.IModel;

public interface XmlModelParser {

	public IModel parser(String xmlModelFilePath) throws Exception;
	
	public IModel hdfsParser(String xmlModelFilePath) throws Exception;
}
