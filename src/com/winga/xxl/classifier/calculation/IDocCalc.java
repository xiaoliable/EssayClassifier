package com.winga.xxl.classifier.calculation;

import com.winga.xxl.classifier.data.store.IDocuments;

/**
 * <p>CreatDate : 2014-8-28</p>
 * <p>Description : Need to implement the universal underlying operation between documents' vector. </p>
 * @author xiaoxiao
 * @version 1.0
 * */
public interface IDocCalc {

	double cosine(IDocuments IDoc1, IDocuments IDoc2 , IDocVectorAccessor V);

	int minAngle(double[] angelCos);

	IDocuments add(IDocuments IDoc1, IDocuments IDoc2 , IDocVectorAccessor[] vectors);

	double distance(IDocuments IDocA, IDocuments IDocB, IDocVectorAccessor[] vectors,
			double[] weights);

	IDocuments center(IDocuments[] iDocuments, IDocVectorAccessor[] vectors);

	boolean equals(IDocuments IDocA, IDocuments IDocB, IDocVectorAccessor v);

}
