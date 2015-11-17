package com.winga.xxl.classifier.data.store;

import java.util.Map;

import org.apache.lucene.analysis.Analyzer;

/**
 * <p>CreateDate : 2014-8-25</p>
 * <p>This class contains all the method to operate every document based on vector notation.</p>
 * @author xiaoxiao
 * @version 1.0
 */
public interface IDocuments {
	
	String getTitle();
	
	void setTitle(String title);
	
	String getURL();
	
	void setURL(String url);
		
	String getContent();	
	
	void setContent(String content);
	
	 void init(Analyzer analyzer);
	
	Map<Long, Integer> getTitleVector();

	Map<Long, Integer> getContentVector();

	void setTitleVector( Map<Long, Integer> titleVector);

	void setContentVector( Map<Long, Integer> contentVector);

	String getId();
	
	void setId(String id);

	void setCategory(String temp);

	String getCategory();
	
//	Map<Long, Integer> vectorize(Analyzer analyzer , String field , String content);

//	void printAnalyzerWords(Analyzer analyzer , String field);

}
