package com.winga.xxl.classifier.multi;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.wltea.analyzer.lucene.IKAnalyzer;

import com.winga.xxl.classifier.data.store.Documents;
import com.winga.xxl.classifier.data.store.DocumentsReader;
import com.winga.xxl.classifier.data.store.IDocuments;
import com.winga.xxl.classifier.data.store.VectorProblem;
import com.winga.xxl.classifier.model.IModel;
import com.winga.xxl.classifier.model.parser.KMeansXmlModelParser;
import com.winga.xxl.classifier.model.parser.NBXmlModelParser;
import com.winga.xxl.classifier.model.parser.SvmXmlModelParser;
import com.winga.xxl.classifier.model.parser.XmlModelParser;
import com.winga.xxl.classifier.parameter.KMeansTrainParameter;
import com.winga.xxl.classifier.parameter.NBTrainParameter;
import com.winga.xxl.classifier.parameter.PredictParameter;
import com.winga.xxl.classifier.parameter.SvmTrainParameter;
import com.winga.xxl.classifier.parameter.TrainParameter;
import com.winga.xxl.classifier.util.IClassifier;
import com.winga.xxl.classifier.util.KmeansCluster;
import com.winga.xxl.classifier.util.NbClassifier;
import com.winga.xxl.classifier.util.SvmClassifier;

public class CategoryPredict {

	public static double arrayPercentPredict(IDocuments[] document,
			IModel model, PredictParameter predictParameter,
			IClassifier classifier) throws IOException {

		double match = 0;
		for (int i = 0; i < document.length; i++) {
			String result = classifier.predict(document[i], model, predictParameter);
			if (result.equals(document[i].getCategory())) {
				++match;
			}
		}
		double accuracy = match / document.length;
		System.out.println("Total documents number is : " + document.length
				+ " , and right prediction is : " + match + " .");
		return accuracy;
	}

	public static String multiDecise(String[] subDecisionArray) {
		int length = subDecisionArray.length;

		// //Odd number of sub-classifiers.
		// if (length % 2 == 1) {
		//
		// }
		// else{
		//
		// }

		if (length != 3) {
			System.out.println("The number of sub-classifiers is not 3 !");
			return null;
		}
		// Assume the number of sub-classifiers is 3.
		if (subDecisionArray[0].equals(subDecisionArray[1])) {
			return subDecisionArray[0];
		} else if (subDecisionArray[0].equals(subDecisionArray[2])) {
			return subDecisionArray[0];
		} else if (subDecisionArray[1].equals(subDecisionArray[2])) {
			return subDecisionArray[1];
		} else {
			System.out
					.println("The sub-classifier's decisions are different !");
			return "random";
		}
	}

	public static String multiDecisionPercent(String sampleFilePath,
			String pendingDocsPath) throws IOException {

		IDocuments[] testDocs = DocumentsReader
				.getFromXMLFileDirectory(pendingDocsPath);

		IDocuments[] sampleDocs = DocumentsReader
				.getFromXMLFileDirectory(sampleFilePath);

		IDocuments[] sampleCenterDocs = DocumentsReader
				.getSampleCenterXML(sampleFilePath);

		// Get SVM model.
		String argument = "-c " + "16.0" + " -g " + "0.0078125" + " zcxc";
		TrainParameter svmParameter = new SvmTrainParameter(argument);
		IModel svmModel = new SvmClassifier().train(sampleDocs, svmParameter);

		// Get the NB model.
		int featureNum = 500;
		TrainParameter nbParameter = new NBTrainParameter(featureNum);
		IModel nbModel = new NbClassifier()
				.train(sampleCenterDocs, nbParameter);

		// Get the KMeans model.
		int cateNum = 7;
		TrainParameter kmeansParameter = new KMeansTrainParameter(cateNum,
				null, sampleFilePath);
		IModel kmeansModel = new KmeansCluster().train(sampleDocs,
				kmeansParameter);

		// Get the SVM model.
		String predictArgv = "-b 0 sdf sdf sdf";
		PredictParameter predictParameter = new PredictParameter(predictArgv);

		// Statistics of multi-predict detail.
		double match = 0;
		for (int i = 0; i < testDocs.length; i++) {
			String nbPredict = new NbClassifier().predict(testDocs[i], nbModel,
					predictParameter);
			String svmPredict = new SvmClassifier().predict(testDocs[i],
					svmModel, predictParameter);
			String kmeansPredict = new KmeansCluster().predict(testDocs[i],
					kmeansModel, predictParameter);

			String[] predictArray = { nbPredict, svmPredict, kmeansPredict };
			String predictDicision = multiDecise(predictArray);

			System.out
					.println("The " + i + "-th doc's nbpred is " + nbPredict
							+ ", svmpred is " + svmPredict + ", kmeanspred is "
							+ kmeansPredict + " ; the dicision is : "
							+ predictDicision);
			if (predictDicision.equals(testDocs[i].getCategory())) {
				++match;
			}
		}
		double accuracy = match / testDocs.length;
		return "" + accuracy;
	}

	/**
	 * <p>
	 * CreateTime : 2014-12-18
	 * 
	 * @throws IOException
	 * */
	public static double arrayPercentPredict(VectorProblem testProb,
			IModel model, PredictParameter predictParameter,
			IClassifier svmClassifier) throws IOException {
		// TODO Auto-generated method stub

		double match = 0;
		int length = testProb.l;
		for (int i = 0; i < length; i++) {
			double predict = svmClassifier.predict(testProb.x[i], model,
					predictParameter);
			if (predict == testProb.y[i]) {
				++match;
			}
		}
		double accuracy = match / length;
		System.out.println("Total documents number is : " + length
				+ " , and right prediction is : " + match + " .");
		return accuracy;
	}
	
	/**
	 * The multi-decision method for demo.
	 * 
	 * @throws Exception
	 * @date 2015-1-29
	 * */
	public static String multiDicision4Nutch(String title, String content)
			throws Exception {

		String kmeansModelPath = "xml" + File.separator + "model"
				+ File.separator + "kmeans_cate.model";
		String nbModelPath = "xml" + File.separator + "model" + File.separator
				+ "nb_cate.model";
		String svmModelPath = "xml" + File.separator + "model" + File.separator
				+ "svm_cate.model";
		return multiDicision4Nutch(title, content, kmeansModelPath, nbModelPath, svmModelPath);
	}
	/**
	 * The multi-decision method for Nutch demo.
	 * 
	 * @throws Exception
	 * @date 2014-12-29
	 * */
	public static String multiDicision4Nutch(String title, String content,
			String kmeansModelPath, String nbModelPath, String svmModelPath)
			throws Exception {

		// Parser the kmeans model...
		XmlModelParser kmeansParser = new KMeansXmlModelParser();
		IModel kmeansModel = kmeansParser.parser(kmeansModelPath);

		// Parser the NB model...
		XmlModelParser nbParser = new NBXmlModelParser();
		IModel nbModel = nbParser.parser(nbModelPath);

		// Parser the svm model...
		XmlModelParser svmParser = new SvmXmlModelParser();
		IModel svmModel = svmParser.parser(svmModelPath);

		// Initialize the new pending document.
		Documents doc = new Documents();
		doc.setContent(content);
		doc.setTitle(title);
		doc.init(new IKAnalyzer());

		// Get the SVM model.
		String predictArgv = "-b 0 sdf sdf sdf";
		PredictParameter predictParameter = new PredictParameter(predictArgv);

		// Statistics of multi-predict detail.
		double match = 0;
		String kmeansPredict = new KmeansCluster().predict(doc, kmeansModel,
				predictParameter);
		String nbPredict = new NbClassifier().predict(doc, nbModel,
				predictParameter);
		String svmPredict = new SvmClassifier().predict(doc, svmModel,
				predictParameter);

		// Multi-decision..
		String predictDicision = null;
		if (kmeansPredict.equals(nbPredict)) {
			predictDicision = kmeansPredict;
		} else if (kmeansPredict.equals(svmPredict)) {
			predictDicision = kmeansPredict;
		} else if (nbPredict.equals(svmPredict)) {
			predictDicision = nbPredict;
		} else {
//			// Random selection.
//			Random r = new Random();
//			int x = r.nextInt(3);
//			if (x == 0) {
//				predictDicision = kmeansPredict;
//			} else if (x == 1) {
//				predictDicision = nbPredict;
//			} else {
//				predictDicision = svmPredict;
//			}
			predictDicision = kmeansPredict;
		}

		 System.out
		 .println("The  doc's nbpred is " + nbPredict
		 + ", svmpred is " + svmPredict + ", kmeanspred is "
		 + kmeansPredict + " ; the dicision is : "
		 + predictDicision);

		return predictDicision;
	}
	
	/**
	 * The multi-decision method for HDFS demo.
	 * 
	 * @throws Exception
	 * @date 2014-2-4
	 * */
	public static String multiDicision4Hdfs(String title, String content,
			String kmeansModelPath, String nbModelPath, String svmModelPath)
			throws Exception {

		// Parser the kmeans model...
		XmlModelParser kmeansParser = new KMeansXmlModelParser();
		IModel kmeansModel = kmeansParser.hdfsParser(kmeansModelPath);

		// Parser the NB model...
		XmlModelParser nbParser = new NBXmlModelParser();
		IModel nbModel = nbParser.hdfsParser(nbModelPath);

		// Parser the svm model...
		XmlModelParser svmParser = new SvmXmlModelParser();
		IModel svmModel = svmParser.hdfsParser(svmModelPath);

		// Initialize the new pending document.
		Documents doc = new Documents();
		doc.setContent(content);
		doc.setTitle(title);
		doc.init(new IKAnalyzer());

		// Get the SVM model.
		String predictArgv = "-b 0 sdf sdf sdf";
		PredictParameter predictParameter = new PredictParameter(predictArgv);

		// Statistics of multi-predict detail.
		double match = 0;
		String kmeansPredict = new KmeansCluster().predict(doc, kmeansModel,
				predictParameter);
		String nbPredict = new NbClassifier().predict(doc, nbModel,
				predictParameter);
		String svmPredict = new SvmClassifier().predict(doc, svmModel,
				predictParameter);

		// Multi-decision..
		String predictDicision = null;
		if (kmeansPredict.equals(nbPredict)) {
			predictDicision = kmeansPredict;
		} else if (kmeansPredict.equals(svmPredict)) {
			predictDicision = kmeansPredict;
		} else if (nbPredict.equals(svmPredict)) {
			predictDicision = nbPredict;
		} else {
//			// Random selection.
//			Random r = new Random();
//			int x = r.nextInt(3);
//			if (x == 0) {
//				predictDicision = kmeansPredict;
//			} else if (x == 1) {
//				predictDicision = nbPredict;
//			} else {
//				predictDicision = svmPredict;
//			}
			predictDicision = kmeansPredict;
		}

		 System.out
		 .println("The  doc's nbpred is " + nbPredict
		 + ", svmpred is " + svmPredict + ", kmeanspred is "
		 + kmeansPredict + " ; the dicision is : "
		 + predictDicision);

		return predictDicision;
	}

	/**
	 * The multi-decision method for multi-class path.
	 * If the flag is 0, it will load the model files from the local Jar file;
	 * If the flag is 1, it will load the model files from the HDFS file;
	 * @throws Exception
	 * @date 2015-2-3
	 * */
	public static String multiDicision4Nutch(String title, String content,
			String kmeansModelPath, String nbModelPath, String svmModelPath,int flag)
			throws Exception {
		//If the flag is 0, it will load the model files from the local Jar file.
		if(flag==0){
			return multiDicision4Nutch(title, content, kmeansModelPath, nbModelPath, svmModelPath);
		}
		//If the flag is 1, it will load the model files from the HDFS file.
		else if(flag==1){
			return multiDicision4Hdfs(title, content, kmeansModelPath, nbModelPath, svmModelPath);
		}
		else{
			return null;
		}
	}
}
