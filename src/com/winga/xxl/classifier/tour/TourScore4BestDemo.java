package com.winga.xxl.classifier.tour;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.winga.xxl.classifier.data.store.VectorProblem;
import com.winga.xxl.classifier.model.IModel;
import com.winga.xxl.classifier.model.parser.SvmXmlModelParser;
import com.winga.xxl.classifier.model.parser.XmlModelParser;
import com.winga.xxl.classifier.parameter.PredictParameter;
import com.winga.xxl.classifier.parameter.SvmTrainParameter;
import com.winga.xxl.classifier.parameter.TrainParameter;
import com.winga.xxl.classifier.util.SvmClassifier;

/**
 * <p>
 * CreateTime : 11-29-2014
 * 
 * @author xiaoxiao
 * */
public class TourScore4BestDemo {

	public static void main(String[] args) throws Exception {

		String tourSampleDocsXmlPath = "xml" + File.separator + "Tour"
				+ File.separator + "SvmSample";

		String tourTestDocsXmlPath = "xml" + File.separator + "Tour"
				+ File.separator + "SvmTest";

		TourDocParser parser = new TourDocParser();

		ArrayList<TourDocuments> tourSampleDocs = parser
				.multiParser(tourSampleDocsXmlPath);

		ArrayList<TourDocuments> tourTestDocs = new TourDocParser()
				.multiParser(tourTestDocsXmlPath);

		SvmClassifier svmClassifier = new SvmClassifier();

		String modelPath = "xml" + File.separator + "Tour" + File.separator
				+ "Svm.model";

		// Few score 1 : -b 0 -s 0 -c 0.125 -g 0.000000059604644775390625
		// More score 1 : -b 0 -s 0 -c 0.125 -g 0.000000029802322387695312
		// Apart from 5 docs (score 1) : The best C/G is : -t 0 -c 4.0 -g
		// 3.0517578125E-5 maxAccuracy is : 0.9322033898305084
		String trainArgs = "-b 0 -s 0 -c 4 -g 0.000030517578125"
				+ " trainArgs " + modelPath;

		TrainParameter trainParameter = new SvmTrainParameter(trainArgs,
				modelPath);

		VectorProblem sampleProblem = new VectorProblem();
		sampleProblem.tourInit(tourSampleDocs);

		//// Add the marked test tour documents.
		// tourSampleDocs.addAll(tourTestDocs);
		
		//Generate the svm classifier's model.
//		IModel model = svmClassifier.train(sampleProblem, trainParameter);
		IModel model = getSvmModel(modelPath);

		VectorProblem testProblem = new VectorProblem();
		testProblem.tourInit(tourTestDocs);

		String predictResultPath = "xml" + File.separator + "Tour" + File.separator
				+ "predict.result";
		String everyPredictArgs = "abc acd " + predictResultPath;
		PredictParameter predictParameter = new PredictParameter(
				everyPredictArgs);

//		searchBestCG(sampleProblem, testProblem);
		predictPercent(testProblem, model, predictParameter);
	}
	

	private static IModel getSvmModel(String modelPath) throws Exception {
		
		 // Parser the svm model...
		 XmlModelParser svmParser = new SvmXmlModelParser();
		 IModel model = svmParser.parser(modelPath);

		return model;
	}

	public static void searchBestCG(VectorProblem sampleProblem, VectorProblem testProblem) throws IOException  {

		SvmClassifier svmClassifier = new SvmClassifier();
		// Search the best c、g parameter.
		// svmClassifier.KERNEL_LINEAR,svmClassifier.KERNEL_POLYNOMIAL,svmClassifier.KERNEL_PRECOMPUTED,svmClassifier.KERNEL_RBF
		int[] kernelArray = { svmClassifier.KERNEL_LINEAR };
		//
		// //Normalize the sample training-problem.
		// sampleProblem.normalize(5);
		String bestCG = svmClassifier.getTheBestCG(sampleProblem, testProblem,
				-5, 5, -20, -7, kernelArray);
		System.out.println("The best C/G is : " + bestCG);
	}
	
	public static void predictPercent(VectorProblem testProblem, IModel model, PredictParameter predictParameter) throws IOException{

		SvmClassifier svmClassifier = new SvmClassifier();
		 //Output the SVM predict-score.
		 int match = 0;
		 for (int i = 0; i < testProblem.l; i++) {
		 double score = svmClassifier.predict(testProblem.x[i], model,
		 predictParameter);
		 if (score == testProblem.y[i]) {
		 ++match;
		 }
		 System.out.println("The " + i + "-th document's score is : " +
		 testProblem.y[i] + " , predict-score is : " + score);
		 }
		 double accuracy = match / (double) testProblem.l;
		 System.out.println("Match accuracy is : " + accuracy);
	}
}
