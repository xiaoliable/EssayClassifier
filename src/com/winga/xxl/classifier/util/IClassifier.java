package com.winga.xxl.classifier.util;

import java.io.IOException;

import com.winga.xxl.classifier.data.store.IDocuments;
import com.winga.xxl.classifier.data.store.VectorNode;
import com.winga.xxl.classifier.data.store.VectorProblem;
import com.winga.xxl.classifier.model.IModel;
import com.winga.xxl.classifier.parameter.PredictParameter;
import com.winga.xxl.classifier.parameter.TrainParameter;

/**
 * <p>CreateDate : 2014-11-11</p>
 * @author xiaoxiao
 * @version 1.1
 */
public interface IClassifier{

	public String predict(IDocuments document, IModel model, PredictParameter predictParameter) throws IOException;
	public double predict(VectorNode[] nodeArray, IModel model, PredictParameter predictParameter) throws IOException;
	IModel train(IDocuments[] sampleDocs, TrainParameter param)
			throws IOException;
	IModel train(VectorProblem problem, TrainParameter param)
			throws IOException;
}


