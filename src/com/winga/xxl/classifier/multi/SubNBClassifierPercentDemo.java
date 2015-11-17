package com.winga.xxl.classifier.multi;

import java.io.File;

import com.winga.xxl.classifier.data.store.DocumentsReader;
import com.winga.xxl.classifier.data.store.IDocuments;
import com.winga.xxl.classifier.model.IModel;
import com.winga.xxl.classifier.model.parser.NBXmlModelParser;
import com.winga.xxl.classifier.model.parser.XmlModelParser;
import com.winga.xxl.classifier.parameter.NBTrainParameter;
import com.winga.xxl.classifier.parameter.PredictParameter;
import com.winga.xxl.classifier.parameter.TrainParameter;
import com.winga.xxl.classifier.util.IClassifier;
import com.winga.xxl.classifier.util.NbClassifier;

public class SubNBClassifierPercentDemo {
	public static void main(String[] args) throws Exception {

		// Naive bayes training example.
		String outputPath = "xml" + File.separator + "model" + File.separator
				+ "NbModel.xml";

		String sampleFilePath = "xml" + File.separator + "CategoryResult"
				+ File.separator + "workBase";

		// The test documents.
		String pendingDocsPath = "xml" + File.separator + "NBCategoryResult";
		IDocuments[] pendingDocs = DocumentsReader
				.getFromXMLFileDirectory(pendingDocsPath);

		IClassifier nbClassifier = new NbClassifier();

		// Initize the NB parameter.
		int featureNum = 1000;

		// IDocuments[] sampleDocs =
		// DocumentsReader.getFromXMLFileDirectory(sampleFilePath);
		IDocuments[] sampleCenterDocs = DocumentsReader
				.getSampleCenterXML(sampleFilePath);

		TrainParameter trainParameter = new NBTrainParameter(featureNum,
				outputPath);

//		 // Get the NB model.
//		 IModel model = nbClassifier.train(sampleCenterDocs, trainParameter);

		// Parser the NB model...
		String xmlModelFilePath = "xml" + File.separator + "model"
				+ File.separator + "nb_cate.model";
		XmlModelParser nbParser = new NBXmlModelParser();
		IModel model = nbParser.parser(xmlModelFilePath);

		// Predict the document's category.
		PredictParameter predictParameter = new PredictParameter();

		double accuracyNB = CategoryPredict.arrayPercentPredict(pendingDocs,
				model, predictParameter, new NbClassifier());

		System.out.println("The accuacy rate of NB is : " + accuracyNB);
	}
}
