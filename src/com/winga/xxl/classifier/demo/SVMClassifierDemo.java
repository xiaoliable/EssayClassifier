package com.winga.xxl.classifier.demo;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import com.winga.xxl.classifier.data.exception.SaxParseService;
import com.winga.xxl.classifier.data.store.IDocuments;
import com.winga.xxl.classifier.util.SvmClassifier;

public class SVMClassifierDemo {

	public static void main(String[] args) throws Exception {

		// trainDataPath
		File[] trainFiles = new File("xml" + File.separator + "NBCategoryResult").listFiles();

		String modelPath = "xml" + File.separator + "SVMClassifier"
				+ File.separator + "svmModel.xml";

		String outputPath = "xml" + File.separator + "SVMClassifier"
				+ File.separator + "svmOutput.xml";

		// "-b 0 -s 0 -c 0.01 " -g (default 1/k)核函数宽度
		String trainArgs = "-b 0 -s 0 -c 1" + " dfa " + modelPath;
		String predictArgs = "-b 0 sdf sdf " + outputPath;
		String[] predictArgsArray = predictArgs.split(" ");

		// train
		String[] trainArgsArray = trainArgs.split(" ");

		// Map the category name to double for SVM_prob's parameter.
		Map<String, Double> numCategoryMap = new TreeMap<String, Double>();
		SaxParseService sax = new SaxParseService();

		IDocuments[] sampleDocs = sax.ReadSVMSampleArrayXML(trainFiles,
				numCategoryMap);

		File[] testFiles = new File("xml" + File.separator + "SVMClassifier"
				+ File.separator + "SVMTest").listFiles();

		IDocuments[] testDocs = sax.ReadSVMSampleArrayXML(testFiles,
				numCategoryMap);

		// Search the best parameter.
		 SvmClassifier autoWrapper = new SvmClassifier();
		 
		 //autoWrapper.KERNEL_LINEAR,		 autoWrapper.KERNEL_POLYNOMIAL, autoWrapper.KERNEL_RBF,		 autoWrapper.KERNEL_PRECOMPUTED}
		 int[] kernelArray = {autoWrapper.KERNEL_LINEAR};
		 String bestCG = autoWrapper.getBestCG(numCategoryMap, sampleDocs,
		 testDocs, -3, 5, -17, -3, kernelArray);
		 System.out.println("The best C/G is : " + bestCG);

//		SVMDocWrapper wrapper = new SVMDocWrapper(trainArgsArray, sampleDocs,
//				numCategoryMap);
//		svm_model model = wrapper.trainModel();
//
//		 svm.svm_save_model(modelPath, model);
//		System.out.println("\nFinish to save the svm training model.");
//
//		// String testCategory = wrapper.predictOneModel(model,
//		// predictArgsArray, testDocs[0], numCategoryMap);
//		//
//		// System.out.println("\nTest document's category is predicted as: " +
//		// testCategory);
//		//
//		wrapper.predictModel(model, predictArgsArray, testDocs, numCategoryMap);
//		System.out.println("\nTrain arguments is : " + trainArgs);
	}
}
