package com.winga.xxl.classifier.demo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.winga.xxl.classifier.data.exception.SaxParseService;
import com.winga.xxl.classifier.data.store.IDocuments;
import com.winga.xxl.classifier.util.IClassifier;
import com.winga.xxl.classifier.util.KmeansCluster;

public class UnsupervisedClusterDemo {

	public static void main(String[] args) throws Exception {

		// 构建IK分词器，使用smart分词模式
		Analyzer analyzer = new IKAnalyzer(true);
		// KmeansCluster HierarchicalCluster
		IClassifier cluster = new KmeansCluster();

		// 设置文档集路径
		// HtmlDetail14.xml documents.xml HtmlDetail_20.xml myClass
//		File file = new File("xml" + File.separator + "HtmlDetail_20.xml");
		 
		File[] pendingFiles = new File("xml" + File.separator + "PendingDocs").listFiles();
		//The big sourse sample documents XML file
		File[] SampleFiles = new File("xml" + File.separator + "CategorySample").listFiles();
		//Output the last center XML file
		File outputLastCenterFile = new File("xml" + File.separator + "lastCenter.xml");
		
		File outputAllCategoryDirectory = new File("xml" + File.separator + "CategoryResult");
		
		//设置日期格式
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// new Date()为获取当前系统时间
		System.out.println("Now time is : " + df.format(new Date()));
		
		//获取分类数
		int k = SampleFiles.length;
		SaxParseService sax = new SaxParseService();
		//Load the  pending documents
//		IDocuments[] documents = sax.ReadTheXML(file);
		
		IDocuments[] documents = sax.ReadAllXML(pendingFiles);
		// Load the sample category documents
		IDocuments[] beginCenter = sax.ReadSampleCenterXML(SampleFiles);

		System.out.println("文档解析完毕！");
		double beginTime = System.nanoTime();

		// Cluster实现
		IDocuments[][] groups = ((KmeansCluster) cluster).cluster(documents, beginCenter, k);
		
//		IDocuments[][] groups = new Documents[1][documents.length];
//		for (int i = 0; i < groups[0].length; i++) {
//			groups[0][i] = documents[i];
//		}
		
		double endTime = System.nanoTime();
		System.out.println("聚类花费时间为：" + (endTime - beginTime) + "ns.");

		for (int i = 0; i < groups.length; ++i) {

			System.out.print("第" + (i + 1) + "组：\n");

			for (int j = 0; j < groups[i].length; j++) {

				System.out.println("documents[" + groups[i][j].getId() + "]\t"
						+ groups[i][j].getTitle());
			}
			System.out.println();
		}
		
		//Output the last centers' array to output XML file
		System.out.println("Start to output file ...");
		sax.outputLastCenterXML(groups , beginCenter , outputLastCenterFile);
		System.out.println("Finish to generate the lastCenter.xml file !");
		
		//Output every category's documents array to corresponding XML file in the specified directory
		sax.outputAllResultXML(groups , beginCenter , outputAllCategoryDirectory);
		System.out.println("Finish to generate every category XML file !");

		// // 测试一些数据
		// // Test for Cosine
		// IDocCalc docCalc = new docVectorCalc();
		// double angleCos = docCalc.angleCos(documents[0], documents[6]);
		// System.out.println("两向量cosine值为" + angleCos + "!");

		// // 查看部分doc的分词情况
		// documents[5].seeAnalyzerWords(analyzer, field);
		// documents[7].seeAnalyzerWords(analyzer, field);
	}
}
