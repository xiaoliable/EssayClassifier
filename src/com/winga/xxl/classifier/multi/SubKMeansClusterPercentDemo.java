package com.winga.xxl.classifier.multi;

import java.io.File;

import com.winga.xxl.classifier.data.store.DocumentsReader;
import com.winga.xxl.classifier.data.store.IDocuments;
import com.winga.xxl.classifier.model.IModel;
import com.winga.xxl.classifier.model.parser.KMeansXmlModelParser;
import com.winga.xxl.classifier.model.parser.XmlModelParser;
import com.winga.xxl.classifier.parameter.KMeansTrainParameter;
import com.winga.xxl.classifier.parameter.PredictParameter;
import com.winga.xxl.classifier.parameter.TrainParameter;
import com.winga.xxl.classifier.util.IClassifier;
import com.winga.xxl.classifier.util.KmeansCluster;

public class SubKMeansClusterPercentDemo {
	public static void main(String[] args) throws Exception {

		// KMeans training example.
		String modelPath = "xml" + File.separator + "model" + File.separator
				+ "KMeansModel.xml";
		
		String sampleFilePath = "xml" + File.separator + "CategoryResult"
				+ File.separator + "workBase";
		
		String sampleCenterFilePath = "xml" + File.separator + "CategorySample";

		IDocuments[] sampleDocs = DocumentsReader
				.getFromXMLFileDirectory(sampleFilePath);

		int cateNum = new File(sampleFilePath).listFiles().length;
		
		TrainParameter trainParameter = new KMeansTrainParameter(cateNum,
				modelPath, sampleFilePath);

		IClassifier kmeansClassifier = new KmeansCluster();

//		// Get the kmeans model.
//		IModel model = kmeansClassifier.train(sampleDocs, trainParameter);

		 // Parser the kmeans model...
		 String xmlModelFilePath = "xml" + File.separator + "model"
		 + File.separator + "KMeansModel.xml";
		 XmlModelParser kmeansParser = new KMeansXmlModelParser();
		 IModel model = kmeansParser.parser(xmlModelFilePath);
		
		// The test documents.
		String pendingDocsPath = "xml" + File.separator + "NBCategoryResult";
		IDocuments[] pendingDocs = DocumentsReader
				.getFromXMLFileDirectory(pendingDocsPath);

		// Predict the document's category.
		PredictParameter predictParameter = new PredictParameter();

		double accuracyKMeans = CategoryPredict.arrayPercentPredict(
				pendingDocs, model, predictParameter, kmeansClassifier);

		System.out.println("The accuacy rate of kmeans is : " + accuracyKMeans);
	}
}
