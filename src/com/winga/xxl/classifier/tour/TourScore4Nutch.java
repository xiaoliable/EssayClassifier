/**
 * Interface for nutch.
 * <p>CreateTime : 2014-12-28
 * */
package com.winga.xxl.classifier.tour;

import java.io.File;
import java.io.IOException;

import com.winga.xxl.classifier.data.store.VectorNode;
import com.winga.xxl.classifier.model.IModel;
import com.winga.xxl.classifier.model.parser.SvmXmlModelParser;
import com.winga.xxl.classifier.model.parser.XmlModelParser;
import com.winga.xxl.classifier.parameter.PredictParameter;
import com.winga.xxl.classifier.util.SvmClassifier;

public class TourScore4Nutch {

	public final static String MODEL_PATH = "xml" + File.separator + "Tour"
			+ File.separator + "Svm.model";

	public final static String PREDICT_ARGS = "abc acd result";

	/**
	 * <p>
	 * CreateTime : 2014-12-28
	 * */
	public static double score(TourDocuments doc, IModel model)
			throws IOException {

		PredictParameter predictParameter = new PredictParameter(PREDICT_ARGS);

		//Transform the tour document to the vactor node array.
		TourDocIndex indexs = new TourDocIndex();
		indexs.init(doc);
		VectorNode[] vectorNodes = indexs.vector();
		
		//Get the svm score.
		double score = new SvmClassifier().predict(vectorNodes, model,
				predictParameter);
		return score;
	}

	/**
	 * <p>
	 * CreateTime : 2014-12-28
	 * */
	public static double score(String content, String modelPath)
			throws Exception {

		// Parser the svm model...
		XmlModelParser svmParser = new SvmXmlModelParser();
		IModel model = svmParser.parser(modelPath);

		PredictParameter predictParameter = new PredictParameter(PREDICT_ARGS);

		//Transform the tour document to the vactor node array.
		TourDocIndex indexs = new TourDocIndex();
		indexs.init(content);
		VectorNode[] vectorNodes = indexs.vector();
		
		//Get the svm score.
		double score = new SvmClassifier().predict(vectorNodes, model,
				predictParameter);
		return score;
	}
}
