package com.winga.xxl.classifier.calculation;

import java.util.Map;

import com.winga.xxl.classifier.data.store.IDocuments;

/**
 * <p>CreatDate : 2014-9-1</p>
 * <p>Description : Access the document's content vector map conveniently.</p>
 * @author xiaoxiao
 * @version 1.0
 * */
public class ContentVectorAccessor implements IDocVectorAccessor {

	@Override
	public Map<Long, Integer> getVector(IDocuments doc) {
		// TODO Auto-generated method stub
		return doc.getContentVector();
	}

	@Override
	public void setVector(IDocuments doc , Map<Long, Integer> v) {
		// TODO Auto-generated method stub
		doc.setContentVector(v);
	}

}
