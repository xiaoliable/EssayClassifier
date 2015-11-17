package com.winga.xxl.classifier.util;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.wltea.analyzer.lucene.IKAnalyzer;

import com.winga.xxl.classifier.calculation.ContentVectorAccessor;
import com.winga.xxl.classifier.calculation.DocVectorCalc;
import com.winga.xxl.classifier.calculation.IDocCalc;
import com.winga.xxl.classifier.calculation.IDocVectorAccessor;
import com.winga.xxl.classifier.calculation.TitleVectorAccessor;
import com.winga.xxl.classifier.data.exception.ModelException;
import com.winga.xxl.classifier.data.exception.TrainParamException;
import com.winga.xxl.classifier.data.store.DocWordHashMap;
import com.winga.xxl.classifier.data.store.Documents;
import com.winga.xxl.classifier.data.store.IDocuments;
import com.winga.xxl.classifier.data.store.VectorNode;
import com.winga.xxl.classifier.data.store.VectorProblem;
import com.winga.xxl.classifier.model.IModel;
import com.winga.xxl.classifier.model.KMeansModel;
import com.winga.xxl.classifier.model.parser.KMeansXmlModelParser;
import com.winga.xxl.classifier.parameter.KMeansTrainParameter;
import com.winga.xxl.classifier.parameter.NBTrainParameter;
import com.winga.xxl.classifier.parameter.PredictParameter;
import com.winga.xxl.classifier.parameter.TrainParameter;
import com.winga.xxl.classifier.tour.TourDocuments;

/**
 * <p>
 * CreateDate : 2014-9-6
 * </p>
 * <p>
 * The KMeans clustering algorithms.
 * </p>
 * 
 * @author xiaoxiao
 * @see {@link http
 *      ://home.deib.polimi.it/matteucc/Clustering/tutorial_html/kmeans.html}
 * @version 1.0
 */
public class KmeansCluster implements IClassifier {

	// An entity class implementing the calculation method
	IDocCalc docCalc = new DocVectorCalc();
	// The vector indicators for calculating the documents' distance
	IDocVectorAccessor[] vectors = null;
	// The weight of the corresponding vector indicators
	double[] weights = null;
	// 定义临界相似度
	// 2014-9-18以前： 0.99999999
	final static double CRITICAL = 0.99;

	public KmeansCluster() {

		// TODO key point
		// 初始化标题、正文向量
		IDocVectorAccessor titleV = new TitleVectorAccessor();
		IDocVectorAccessor contentV = new ContentVectorAccessor();
		// 定义计算similarity，minAngle时使用的vector向量及权重数组
		IDocVectorAccessor[] newVectors = { titleV, contentV };
		vectors = newVectors;
		weights = new double[vectors.length];
		// for (int i = 0; i < weights.length; ++i) {
		// weights[i] = 1.0 / weights.length;
		// }
		weights[0] = 0.6;
		weights[1] = 0.4;
	}

	/**
	 * <p>
	 * CreateTime : 2014-11-13
	 * */
	@Override
	public String predict(IDocuments document, IModel model,
			PredictParameter param) {
		try {
			if (!(model instanceof KMeansModel)) {
				throw new ModelException("KMeans model error !");
			}
			IDocuments[] centers = ((KMeansModel) model).centers;
			int alloc = minDistance(document, centers);
			return centers[alloc].getCategory();
		} catch (ModelException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * <p>
	 * CreateTime : 2014-11-13
	 * 
	 * @throws IOException
	 * */
	@Override
	public IModel train(IDocuments[] sampleDocs, TrainParameter param)
			throws IOException {
		try {
			if (!(param instanceof KMeansTrainParameter)) {
				throw new TrainParamException("KMeans train parameter error !");
			}
			KMeansTrainParameter theParameter = (KMeansTrainParameter) param;
			// Get the category number.
			int k = theParameter.categoryNum;
			// The start center documents.
			IDocuments[] centers = new Documents[k];

			if (theParameter.startCenters != null) {
				centers = theParameter.startCenters;
			} else {
				for (int i = 0; i < centers.length; ++i) {
					centers[i] = sampleDocs[i];
				}
			}

			if (k <= sampleDocs.length) {
				// 存放新计算的聚类中心
				IDocuments[] newCenters = new Documents[k];
				// 记录迭代次数
				int count = 0;

				// 存放聚类结果
				IDocuments[][] groups = new Documents[k][];

				// 循环聚类，更新聚类中心
				// 到聚类中心不变为止
				System.out.println("进入KmeansCluster聚类过程...\tdoc总个数为："
						+ sampleDocs.length);

				while (true) {

					++count;
					// 根据聚类中心将元素逐个分类
					groups = group(sampleDocs, centers);
					// 计算分类后的聚类中心
					for (int i = 0; i < groups.length; ++i) {
						newCenters[i] = center(groups[i]);
						
						//将旧center 传递给newcenter
						//TODO  初始向量需要准确度比较高
						newCenters[i].setCategory(centers[i].getCategory());
					}

					System.out.println("已完成" + count + "次迭代:");
					for (int i = 0; i < k; ++i) {
						System.out.print("\t第" + i + "组中有" + groups[i].length
								+ "个文档     ");
						System.out.println("\tcenter相似度为："
								+ docCalc.distance(centers[i], newCenters[i],
										vectors, weights));
					}

					// 大文件debug数据 2014-9-20
					// testForMoreThanNTimesBigPendingData(center, newCenter,
					// count
					// ,2);

					// 设置日期格式 new Date()为获取当前系统时间
					System.out.println("Now time is : "
							+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
									.format(new Date()));

					// 当采用余弦相似度时，目标函数一般为最大化对象到其簇质心的余弦相似度和
					// 如果聚类中心不同
					if (!similarity(newCenters, centers)) {
						// 为下一次聚类准备
						centers = newCenters;
						newCenters = new Documents[k];
					} else {
						// 聚类结束
						break;
					}
				}
				System.out.println("共迭代" + count + "次!");

				// Initize the model from training sample documents.

				IModel model = new KMeansModel(centers);
				// Outputer the model.
				if (theParameter.outputer != null) {
					theParameter.outputer.output(model);
				}

				return model;
			} else {
				System.out
						.println("K is bigger than the numbers of documents!");
				return null;
			}
		} catch (TrainParamException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String docCluster(String title, String content, File file)
			throws Exception {
		IDocuments[] centerArray = KMeansXmlModelParser.readXMLCenterDocs(file);
		System.out.println("Finish to parse the lastCenter XML file !");
		IDocuments doc = new Documents();
		doc.setTitle(title);
		doc.setContent(content);
		doc.init(new IKAnalyzer());

		IClassifier cluster = new KmeansCluster();
		int category = ((KmeansCluster) cluster).minDistance(doc, centerArray);
		return centerArray[category].getTitle();
	}

	public String docCluster(String title, String content, IDocuments[] centers)
			throws Exception {
		IDocuments doc = new Documents();
		doc.setTitle(title);
		doc.setContent(content);
		doc.init(new IKAnalyzer());

		IClassifier cluster = new KmeansCluster();
		int category = ((KmeansCluster) cluster).minDistance(doc, centers);
		return centers[category].getTitle();
	}

	// If we don't have the specified center documents array, we set the first k
	// documents to the center documents
	public IDocuments[][] cluster(IDocuments[] documents, int k) {
		IDocuments[] centerDocs = new IDocuments[k];
		for (int i = 0; i < k; i++) {
			centerDocs[i] = documents[i];
		}
		return cluster(documents, centerDocs, k);
	}

	// Classify documents with given centerDocs
	public IDocuments[][] cluster(IDocuments[] documents,
			IDocuments[] centerDocs, int k) {

		if (k <= documents.length) {
			// 存放聚类旧的聚类中心
			// 泛型是没有数组的。编译器不承认 HashMap<K,V>[]这种形式,泛型在编译阶段有一个类型擦除的问题
			IDocuments[] center = centerDocs;
			// 存放新计算的聚类中心
			IDocuments[] newCenter = new Documents[k];
			// 记录迭代次数
			int count = 0;

			// 存放聚类结果
			IDocuments[][] groups = new Documents[k][];

			// 循环聚类，更新聚类中心
			// 到聚类中心不变为止
			System.out.println("进入KmeansCluster聚类过程...\tdoc总个数为："
					+ documents.length);

			while (true) {

				++count;
				// 根据聚类中心将元素逐个分类
				groups = group(documents, center);
				// 计算分类后的聚类中心
				for (int i = 0; i < groups.length; ++i) {
					newCenter[i] = center(groups[i]);

					// 将对应序号的旧center document的类别 传递给new center.
					// TODO 这种情况只适合初始center比较准的时候（比较适合聚类的数据）。
					newCenter[i].setCategory(center[i].getCategory());
				}

				System.out.println("已完成" + count + "次迭代:");
				for (int i = 0; i < k; ++i) {
					System.out.print("\t第" + i + "组中有" + groups[i].length
							+ "个文档     ");
					System.out.println("\tcenter相似度为："
							+ docCalc.distance(center[i], newCenter[i],
									vectors, weights));
				}

				// 大文件debug数据 2014-9-20
				// testForMoreThanNTimesBigPendingData(center, newCenter, count
				// ,2);

				// 设置日期格式 new Date()为获取当前系统时间
				System.out.println("Now time is : "
						+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
								.format(new Date()));

				// 当采用余弦相似度时，目标函数一般为最大化对象到其簇质心的余弦相似度和
				// 如果聚类中心不同
				if (!similarity(newCenter, center)) {
					// 为下一次聚类准备
					center = newCenter;
					newCenter = new Documents[k];
				} else {
					// 聚类结束
					break;
				}

			}// while true iterative loop
			System.out.println("共迭代" + count + "次!");

			// for (int i = 0; i < k; ++i) {
			// System.out.println("\t新旧center向量组中对应第" + i + "个向量间距离为"
			// + docCalc.cosine(center[i], newCenter[i], contentV));
			// }

			//
			// //Test for Cosine
			// double angleCos = docCalc.angleCos(documents[1] , documents[9]);
			// System.out.println("无交叉IDoc angleCos值为" + angleCos + "!");
			//
			// double angleCos1 = docCalc.angleCos(documents[9] ,
			// documents[10]);
			// System.out.println("差别较大IDoc angleCos值为" + angleCos1 + "!");
			//
			// double angleCos2 = docCalc.angleCos(documents[9] , documents[9]);
			// System.out.println("相同IDoc angleCos值为" + angleCos2 + "!");
			//

			// 返回聚类结果
			return groups;
		} else {
			System.out.println("K is bigger than the numbers of documents!");
			return null;
		}
	}

	/**
	 * 大文件debug数据
	 * */
	private void testForMoreThanNTimesBigPendingData(IDocuments[] center,
			IDocuments[] newCenter, int count, int times) {
		// TODO Auto-generated method stub

		if (count >= times) {
			// Global data dictionary
			DocWordHashMap wordHash = DocWordHashMap.getInstance();
			// Print the center[2]'s titleVector difference
			int titleDifference = 0;
			Map<Long, Integer> oldTitleVector = center[2].getTitleVector();
			Map<Long, Integer> newTitleVector = newCenter[2].getTitleVector();

			System.out.println("Center[2] documents' titleVector difference: ");
			for (Iterator<Long> it = oldTitleVector.keySet().iterator(); it
					.hasNext();) {
				long key = it.next();
				if (!newTitleVector.containsKey(key)) {
					System.out.println(key + "\t"
							+ wordHash.getWordStringHash().get(key) + "\t次数："
							+ oldTitleVector.get(key));
					++titleDifference;
				}
			}
			for (Iterator<Long> it = newTitleVector.keySet().iterator(); it
					.hasNext();) {
				long key = it.next();
				if (!oldTitleVector.containsKey(key)) {
					System.out.println(key + "\t"
							+ wordHash.getWordStringHash().get(key) + "\t次数："
							+ newTitleVector.get(key));
					++titleDifference;
				}
			}
			System.out.println("迭代前后center title中共有" + titleDifference
					+ "个差异单词！");

			// Print the center[2]'s titleVector difference
			int contentDifference = 0;
			Map<Long, Integer> oldContentVector = center[2].getContentVector();
			Map<Long, Integer> newContentVector = newCenter[2]
					.getContentVector();

			System.out
					.println("Center[2] documents' contentVector difference: ");
			for (Iterator<Long> it = oldContentVector.keySet().iterator(); it
					.hasNext();) {
				long key = it.next();
				if (!newContentVector.containsKey(key)) {
					System.out.println(key + "\t"
							+ wordHash.getWordStringHash().get(key) + "\t次数："
							+ oldContentVector.get(key));
					++contentDifference;
				}
			}
			for (Iterator<Long> it = newContentVector.keySet().iterator(); it
					.hasNext();) {
				long key = it.next();
				if (!oldContentVector.containsKey(key)) {
					System.out.println(key + "\t"
							+ wordHash.getWordStringHash().get(key) + "\t次数："
							+ newContentVector.get(key));
					++contentDifference;
				}
			}
			System.out.println("迭代前后center content中共有" + contentDifference
					+ "个差异单词！");

		}
	}

	public IDocuments[][] group(IDocuments[] documents, IDocuments[] center) {

		// 中间变量，用来分组标记
		int[] groupAlloc = new int[documents.length];
		// 考察每一个元素 pi 同聚类中心 cj 的距离
		// pi 与 cj 的距离最小则归为 j 类
		for (int i = 0; i < documents.length; ++i) {
			// 标记属于哪一组
			groupAlloc[i] = minDistance(documents[i], center);
		}

		// 存放分组结果
		IDocuments[][] groups = new Documents[center.length][];

		// 遍历每个聚类中心，分组
		for (int i = 0; i < center.length; ++i) {

			// 中间变量，记录聚类后每一组的大小
			int centerSize = 0;
			// 计算每一组的长度
			for (int j = 0; j < groupAlloc.length; ++j) {
				if (groupAlloc[j] == i)
					centerSize++;
			}

			// 存储每一组的成员
			groups[i] = new Documents[centerSize];
			centerSize = 0;
			// 根据分组标记将各元素归位
			for (int j = 0; j < groupAlloc.length; ++j) {
				if (groupAlloc[j] == i) {
					groups[i][centerSize] = documents[j];
					centerSize++;
				}
			}
		}
		// 返回分组结果
		return groups;
	}

	public boolean identical(IDocuments[] newCenter, IDocuments[] center) {

		for (int i = 0; i < vectors.length; ++i) {
			for (int j = 0; j < center.length; ++j) {
				if (!docCalc.equals(newCenter[i], center[i], vectors[i])) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean similarity(IDocuments[] newCenter, IDocuments[] center) {

		for (int i = 0; i < center.length; i++) {
			double cosine = docCalc.distance(center[i], newCenter[i], vectors,
					weights);
			if (cosine < CRITICAL) {
				return false;
			}
		}
		return true;
	}

	public IDocuments center(IDocuments[] group) {

		IDocuments sum = new Documents();
		IDocCalc docCalc = new DocVectorCalc();

		// 只更新title、content vector...
		for (int i = 0; i < vectors.length; ++i) {
			for (int j = 0; j < group.length; ++j) {
				sum = docCalc.add(sum, group[j], vectors);
			}
		}
		return sum;
	}

	public int minDistance(IDocuments doc, IDocuments[] center) {
		// 存放距离
		double[] distance = new double[center.length];
		// 计算到每个聚类中心的距离
		for (int j = 0; j < center.length; j++) {
			distance[j] = docCalc.distance(doc, center[j], vectors, weights);
		}
		// 找出最小夹角编号,最近组号
		return docCalc.minAngle(distance);
	}

	@Override
	public double predict(VectorNode[] nodeArray, IModel model,
			PredictParameter predictParameter) throws IOException {
		// TODO Auto-generated method stub
		System.out.println("Kmeans vecotr-problem predicting have not complete.");
		return -1;
	}

	@Override
	public IModel train(VectorProblem problem, TrainParameter param)
			throws IOException {
		// TODO Auto-generated method stub
		System.out.println("Kmeans vecotr-problem training have not complete.");

		return null;
	}

}
