package com.winga.xxl.classifier.calculation;

import java.util.Map;

import com.winga.xxl.classifier.data.store.IDocuments;

/**
 * <p>CreatDate : 2014-9-1</p>
 * <p>Description : Access the document's title or content vector map conveniently. </p>
 * <p>( Title and content are indicators currently. )</p>
 * @author xiaoxiao
 * @version 1.0
 * */
public interface IDocVectorAccessor {

	Map<Long, Integer> getVector(IDocuments doc);
	void setVector(IDocuments doc , Map<Long, Integer> v);
}
