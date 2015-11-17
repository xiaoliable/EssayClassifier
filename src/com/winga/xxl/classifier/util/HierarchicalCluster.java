package com.winga.xxl.classifier.util;

import com.winga.xxl.classifier.calculation.ContentVectorAccessor;
import com.winga.xxl.classifier.calculation.DocVectorCalc;
import com.winga.xxl.classifier.calculation.IDocCalc;
import com.winga.xxl.classifier.calculation.IDocVectorAccessor;
import com.winga.xxl.classifier.calculation.TitleVectorAccessor;
import com.winga.xxl.classifier.data.store.Documents;
import com.winga.xxl.classifier.data.store.IDocuments;

/**
 * <p>CreateDate : 2014-9-5</p>
 * <p>Implement the hierarchical clustering algorithms. </p>
 * @author xiaoxiao
 * @see {@link http://home.deib.polimi.it/matteucc/Clustering/tutorial_html/hierarchical.html}
 * @version 1.0
 */
public class HierarchicalCluster{

	IDocCalc docCalc = new DocVectorCalc();

	/**
	 * 自顶向上聚类函数
	 * */
	public IDocuments[][] cluster(IDocuments[] documents, int k) {

		int docLen = documents.length;
		if (k <= docLen) {

			IDocVectorAccessor[] vectors = { new TitleVectorAccessor(),
					new ContentVectorAccessor() };
			double[] weights = new double[vectors.length];

			// 分配各属性向量权重
			for (int i = 0; i < weights.length; ++i) {
				weights[i] = 1.0 / weights.length;
			}
			//
			// // 记录doc分组情况
			// int[] groupAlloc = new int[docLen];
			// for (int i = 0; i < groupAlloc.length; ++i) {
			// groupAlloc[i] = i;
			// }

			// 定义聚类结果
			IDocuments[][] groups = new Documents[docLen][];
			for (int i = 0; i < groups.length; ++i) {
				IDocuments[] tmp = new Documents[1];
				tmp[0] = documents[i];
				groups[i] = tmp;
			}

			IDocuments[] center = new Documents[docLen];

			for (int i1 = 0; i1 < documents.length; ++i1)
				center[i1] = documents[i1];

			// 聚类主过程
			// 开始聚类
			for (int i = 0; i < docLen - k; ++i) {

				// 存放文档间余弦距离
				double[][] cosMatrix = cosMatrix(groups , center, vectors, weights);

				// 存储最近距离值
				double latest = new Double(0);
				// 存储最近距离文档号
				int latestA = 0, latestB = 0;
				for (int i1 = 0; i1 < cosMatrix.length; ++i1) {
					for (int j = 0; j < cosMatrix[i1].length; ++j) {
						if (cosMatrix[i1][j] > latest && i1 != j) {
							latest = cosMatrix[i1][j];
							latestA = (i1 > j) ? j : i1;
							latestB = (i1 > j) ? i1 : j;
						}
					}
				}
				if (latest == 0)
					latestB = 1;

				
				groups = updateGroups(groups , latestA , latestB);

				center = updateCenter(groups , center , vectors , latestA , latestB);
				
				System.out.println("已迭代第"+i+"次...");

			}// for (int i = 0; i < docLen - k; ++i) 聚类开始
			return groups;
		} else {
			System.out.println("K is bigger than the number of documents!");
			return null;
		}
	}// cluster方法
	

	/**
	 * 将latestB类中的doc加到latestA类中，且groups的length减一
	 * */
	private IDocuments[][] updateGroups(IDocuments[][] groups, int latestA,
			int latestB) {

		// 定义新的聚类结果
		IDocuments[][] newGroups = new Documents[groups.length - 1][];

		// 处理得到新聚类结果
		for (int j = 0; j < newGroups.length; j++) {
			if (j < latestB) {
				newGroups[j] = groups[j];
			} else {
				newGroups[j] = groups[j + 1];
			}
		}

		for (int j = 0; j < newGroups.length; j++) {
			//Merge the latestB category array to the new center docs array
			if (j == latestA) {
				IDocuments[] tmpGroup = new Documents[newGroups[j].length
						+ groups[latestB].length];
				System.arraycopy(newGroups[j], 0, tmpGroup, 0,
						newGroups[j].length);
				System.arraycopy(groups[latestB], 0, tmpGroup,
						newGroups[j].length, groups[latestB].length);
				newGroups[j] = tmpGroup;
			}
		}
		return newGroups;
	}
	

	/**
	 * 合并center向量集，
	 * 将第latestB类的center Documents类向量加到第latestA类的center Documents中
	 * */
	private IDocuments[] updateCenter(IDocuments[][] groups,
			IDocuments[] center, IDocVectorAccessor[] vectors, int latestA,
			int latestB) {

		IDocuments[] newCenter = new Documents[center.length - 1];

		for (int i = 0; i < newCenter.length; i++) {
			if (i < latestB) {
				newCenter[i] = center[i];
			} else {
				newCenter[i] = center[i + 1];
			}
		}
		newCenter[latestA] = docCalc.center(groups[latestA], vectors);

		return newCenter;

	}

	
	/**
	 * 返回对应doc数组生成的doc间distance矩阵
	 * @param groups 
	 * */
	public double[][] cosMatrix(IDocuments[][] groups, IDocuments[] center,
			IDocVectorAccessor[] vectors, double[] weights) {
		int docLen = center.length;
		double[][] cosMatrix = new double[docLen][docLen];

		for (int i = 0; i < docLen; ++i) {
			for (int j = 0; j < docLen; ++j) {
				if (i > j) {
					cosMatrix[i][j] = docCalc.distance(center[i], center[j],
							vectors, weights) / Math.sqrt(groups[i].length);
				}
			}
		}
		return cosMatrix;
	}

	
	/**
	 * 亦或法更新doc间distance矩阵
	 * */
	private double[][] updateMatrix(double[][] cosMatrix, int latestA,
			int latestB) {

		double[][] newMatrix = new double[cosMatrix.length - 1][cosMatrix.length - 1];
		// 先将latestB行、列删除
		for (int j = 0; j < newMatrix.length; ++j) {
			for (int j2 = 0; j2 < newMatrix.length; ++j2) {
				if (j < latestB && j2 < latestB) {
					newMatrix[j][j2] = cosMatrix[j][j2];
				} else if (j < latestB && j2 >= latestB) {
					newMatrix[j][j2] = cosMatrix[j][j2 + 1];
				} else if (j >= latestB && j2 < latestB) {
					newMatrix[j][j2] = cosMatrix[j + 1][j2];
				} else if (j >= latestB && j2 >= latestB) {
					newMatrix[j][j2] = cosMatrix[j + 1][j2 + 1];
				}
			}
		}

		// 将第latestB行、列加到第latestA行、列上
		for (int j = 0; j < newMatrix.length; ++j) {
			for (int j2 = 0; j2 < newMatrix.length; ++j2) {
				// 将第latestB列加到第latestA列上
				if (j2 == latestA && j < latestB) {
					if (cosMatrix[j][latestB] > cosMatrix[j][latestA]) {
						newMatrix[j][j2] = cosMatrix[j][latestB];
					}
				} else if (j2 == latestA && j >= latestB) {
					if (cosMatrix[j][latestB] > cosMatrix[j][latestA]) {
						newMatrix[j][j2] = cosMatrix[j + 1][latestB];
					}
				}
				// 将第latestB行加到第latestA行上
				if (j == latestA && j2 < latestB) {
					if (cosMatrix[latestB][j2] > cosMatrix[latestA][j2]) {
						newMatrix[j][j2] = cosMatrix[latestB][j2];
					}
				} else if (j == latestA && j2 >= latestB) {
					if (cosMatrix[latestB][j2] > cosMatrix[latestA][j2]) {
						newMatrix[j][j2] = cosMatrix[latestB][j2];
					}
				}
			}
		}

		return newMatrix;
	}
}