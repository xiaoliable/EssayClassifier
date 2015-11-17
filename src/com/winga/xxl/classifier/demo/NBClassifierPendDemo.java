package com.winga.xxl.classifier.demo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.winga.xxl.classifier.data.exception.SaxParseService;
import com.winga.xxl.classifier.data.store.IDocuments;
import com.winga.xxl.classifier.util.NbClassifier;

public class NBClassifierPendDemo {

	public static void main(String[] args) throws Exception {

		// 设置文档集路径
		// HtmlDetail14.xml documents.xml HtmlDetail_20.xml myClass
		// File file = new File("xml" + File.separator + "HtmlDetail_20.xml");

		File sampleFile = new File("xml" + File.separator + "categoryResult"
				+ File.separator + "workBase");

		File[] pendingFiles = new File("xml" + File.separator + "PendingDocs")
				.listFiles();

		File outputAllCategoryDirectory = new File("xml" + File.separator
				+ "NBCategoryResult" + File.separator);

		// 设置日期格式
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// new Date()为获取当前系统时间
		System.out.println("Now time is : " + df.format(new Date()));

		SaxParseService sax = new SaxParseService();

		IDocuments[] documents = sax.ReadAllXML(pendingFiles);

		NbClassifier probClassifier = new NbClassifier(sampleFile, 500);
		System.out.println("文档解析完毕！");
		double beginTime = System.nanoTime();

		// Classifier实现
		// Output every category's documents array to corresponding XML file in
		// the specified directory
		IDocuments[][] groups = probClassifier.outputAllResultXML(documents,
				outputAllCategoryDirectory);
		System.out.println("Finish to generate every category XML file !");

		double endTime = System.nanoTime();
		System.out.println("分类花费时间为：" + (endTime - beginTime) + "ns.");

		for (int i = 0; i < groups.length; ++i) {

			System.out.print("第" + (i + 1) + "组：\n");

			for (int j = 0; j < groups[i].length; j++) {

				System.out.println("documents[" + groups[i][j].getId() + "]\t"
						+ groups[i][j].getTitle());
			}
			System.out.println();
		}
	}
}
