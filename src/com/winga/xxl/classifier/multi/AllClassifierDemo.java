package com.winga.xxl.classifier.multi;

import java.io.File;
import java.io.IOException;

import org.wltea.analyzer.lucene.IKAnalyzer;

import com.winga.xxl.classifier.data.store.Documents;
import com.winga.xxl.classifier.data.store.DocumentsReader;
import com.winga.xxl.classifier.data.store.IDocuments;
import com.winga.xxl.classifier.model.IModel;
import com.winga.xxl.classifier.model.parser.NBXmlModelParser;
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

public class AllClassifierDemo {

	public static void main(String[] args) throws Exception {

		String outputPath = "xml" + File.separator + "model" + File.separator
				+ "NbModel.xml";

		String sampleFilePath = "xml" + File.separator + "CategoryResult"
				+ File.separator + "workBase";
		
		//The test documents.
		String pendingDocsPath = "xml" + File.separator + "NBCategoryResult";
		
//		IDocuments[] pendingDocs = DocumentsReader.getFromXMLFileDirectory(pendingDocsPath);
//
//		IDocuments[] sampleDocs = DocumentsReader.getFromXMLFileDirectory(sampleFilePath);
//		IDocuments[] sampleCenterDocs = DocumentsReader
//				.getSampleCenterXML(sampleFilePath);

		String dicisionPercent = CategoryPredict.multiDecisionPercent(sampleFilePath, pendingDocsPath);
		System.out.println("The classifier dicision's accuracy rate is : " + dicisionPercent);
		
//		String sportTitle = "恒大本欲上诉亚足联足协劝停 里皮若追罚得不偿失_国内足球-中超_新浪竞技风暴_新浪网";
//		String sportContent = "一位熟悉亚足联纪律处罚流程的人士向北京青年报记者透露，一般来说，如当值裁判组或比赛监督不对比赛中发生的红牌行为提交违纪行为报告，那么领红者在自然停赛一场后，不会受到额外追罚。但如果恒大就郜林领红蒙冤提出撤牌申请，那么亚足联就会对比赛进行全面调查，领红的张琳及冲入场内质疑裁判的恒大主帅里皮也必成为被调查对象，他们被追罚的可能性会提高。恒大放弃申诉，实际也是避免受到更多人员损失。";
//
//		IDocuments doc = new Documents();
//		doc.setContent(sportContent);
//		doc.setTitle(sportTitle);
//		doc.init(new IKAnalyzer());
//
//		System.out.println("NB classifier : " + NBxmlclassify(doc));
//		System.out.println("SVM classifier : " + SVMxmlclassify(doc));
//		System.out.println("KMeans classifier : " + KMeansxmlclassify(doc));
	}

	public static String NBxmlclassify(IDocuments doc) throws Exception {

		// Naive bayes training example.
		String outputPath = "xml" + File.separator + "model" + File.separator
				+ "NbModel.xml";

		String sampleFilePath = "xml" + File.separator + "CategoryResult"
				+ File.separator + "workBase";

		IDocuments[] sampleDocs = DocumentsReader
				.getFromXMLFileDirectory(sampleFilePath);

		// Initize the NB parameter.
		int featureNum = 500;

		TrainParameter trainParameter = new NBTrainParameter(featureNum,
				outputPath);

		IClassifier nbClassifier = new NbClassifier();

//		// Get the NB model.
//		IModel model = nbClassifier.train(sampleDocs, trainParameter);

		// Parser the NB model...
		String xmlModelFilePath = "xml" + File.separator + "model"
				+ File.separator + "NbModel.xml";
		XmlModelParser nbParser = new NBXmlModelParser();
		IModel model = nbParser.parser(xmlModelFilePath);

		// Predict the document's category.
		PredictParameter predictParameter = new PredictParameter();

		return nbClassifier.predict(doc, model, predictParameter);
	}

	public static String SVMxmlclassify(IDocuments doc) throws IOException {
		// SVM training example.
		String outputPath = "xml" + File.separator + "model" + File.separator
				+ "SvmModel.xml";

		String sampleFilePath = "xml" + File.separator + "CategoryResult"
				+ File.separator + "workBase";

		IDocuments[] sampleDocs = DocumentsReader
				.getFromXMLFileDirectory(sampleFilePath);

		// Get the SVM model.
		String trainArgu = "-b 0 -s 0 -c 1 dfa " + outputPath;

		TrainParameter trainParameter = new SvmTrainParameter(trainArgu,
				outputPath);

		IClassifier svmClassifier = new SvmClassifier();

		IModel model = svmClassifier.train(sampleDocs, trainParameter);

		// Predict the document's category.
		String svmOutput = "xml" + File.separator + "model" + File.separator + "svmOutput";
		String predictArgu = "-b 0 sdf sdf " + svmOutput;
		PredictParameter predictParameter = new PredictParameter(predictArgu);

		return svmClassifier.predict(doc, model, predictParameter);
	}

	public static String KMeansxmlclassify(IDocuments doc) throws IOException {
		// SVM training example.
		String outputPath = "xml" + File.separator + "model" + File.separator
				+ "KMeansModel.xml";

		String sampleFilePath = "xml" + File.separator + "CategoryResult"
				+ File.separator + "workBase";

		IDocuments[] sampleDocs = DocumentsReader
				.getFromXMLFileDirectory(sampleFilePath);

		// Get the kmeans model.
		int k = (new File(sampleFilePath)).listFiles().length;// Get the category
														// number.
		TrainParameter trainParameter = new KMeansTrainParameter(k, outputPath);

		IClassifier kmeansClassifier = new KmeansCluster();

		IModel model = kmeansClassifier.train(sampleDocs, trainParameter);

		// Predict the document's category.
		PredictParameter predictParameter = new PredictParameter();

		return kmeansClassifier.predict(doc, model, predictParameter);
	}
}
