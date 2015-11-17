package com.winga.xxl.classifier.multi;

import java.io.File;

import com.winga.xxl.classifier.data.store.DocumentsReader;
import com.winga.xxl.classifier.data.store.IDocuments;
import com.winga.xxl.classifier.model.IModel;
import com.winga.xxl.classifier.model.parser.SvmXmlModelParser;
import com.winga.xxl.classifier.model.parser.XmlModelParser;
import com.winga.xxl.classifier.parameter.PredictParameter;
import com.winga.xxl.classifier.parameter.SvmTrainParameter;
import com.winga.xxl.classifier.parameter.TrainParameter;
import com.winga.xxl.classifier.util.IClassifier;
import com.winga.xxl.classifier.util.SvmClassifier;

public class SubSvmClassifierPercentDemo {
	public static void main(String[] args) throws Exception {

		// Svm training example.
		String outputPath = "xml" + File.separator + "model" + File.separator
				+ "SvmModel.xml";

		String sampleFilePath = "xml" + File.separator + "CategoryResult"
				+ File.separator + "workBase";

		// The test documents.
		String pendingDocsPath = "xml" + File.separator + "NBCategoryResult";

		IDocuments[] sampleDocs = DocumentsReader
				.getFromXMLFileDirectory(sampleFilePath);

		IDocuments[] pendingDocs = DocumentsReader
				.getFromXMLFileDirectory(pendingDocsPath);

		String modelPath = "xml" + File.separator + "model"
				+ File.separator + "svm_cate.model";

		String outputModelPath = "xml" + File.separator + "SVMClassifier"
				+ File.separator + "svmOutput.xml";
		
		IClassifier svmClassifier = new SvmClassifier();
		
		// "-b 0 -s 0 -c 0.01 " -g (default 1/k)核函数宽度
		//16.0 -g 0.0078125 0.00390625
		String trainArgs = "-b 0 -s 0 -c 16" + " dfa " + modelPath;

		TrainParameter trainParameter = new SvmTrainParameter(trainArgs,
				modelPath);

//		System.out.println("Start to output svm model.");
//		// Get the svm model.
//		IModel model = svmClassifier.train(sampleDocs, trainParameter);
//		System.out.println("Finish!.");

		 // Parser the svm model...
		 String xmlModelFilePath = "xml" + File.separator + "model"
		 + File.separator + "svm_cate.model";
		 XmlModelParser svmParser = new SvmXmlModelParser();
		 IModel model = svmParser.parser(xmlModelFilePath);
		
		
		// Predict the document's category.
		String predictArgs = "-b 0 sdf sdf " + outputModelPath;
		PredictParameter predictParameter = new PredictParameter(predictArgs);

		double accuracyKMeans = CategoryPredict.arrayPercentPredict(
				pendingDocs, model, predictParameter, svmClassifier);

		System.out.println("The accuacy rate of svm is : " + accuracyKMeans);
	}
}
