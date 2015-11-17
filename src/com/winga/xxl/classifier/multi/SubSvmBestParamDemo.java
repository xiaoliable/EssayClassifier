package com.winga.xxl.classifier.multi;

import java.io.File;
import java.io.IOException;

import com.winga.xxl.classifier.data.store.DocumentsReader;
import com.winga.xxl.classifier.data.store.IDocuments;
import com.winga.xxl.classifier.util.SvmClassifier;

public class SubSvmBestParamDemo {

	public static void main(String[] args) throws IOException {

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

		SvmClassifier svmClassifier = new SvmClassifier();

		// svmClassifier.KERNEL_LINEAR, svmClassifier.KERNEL_POLYNOMIAL,
		// svmClassifier.KERNEL_PRECOMPUTED, svmClassifier.KERNEL_RBF
		int[] kernelArray = { svmClassifier.KERNEL_LINEAR };

		String bestCG = svmClassifier.getTheBestCG(sampleDocs, pendingDocs, 0,
				4, -15, -5, kernelArray);
		System.out.println("The best C/G is : " + bestCG);
	}
}
