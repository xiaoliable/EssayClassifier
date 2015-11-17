package com.winga.xxl.classifier.calculation;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import com.winga.xxl.classifier.data.store.Documents;
import com.winga.xxl.classifier.data.store.IDocuments;

/**
 * <p>CreatDate : 2014-8-28</p>
 * <p>Description : Implement the universal underlying operation between documents' vector. </p>
 *  <p>Include vector's adding, product and length measurement.</p>
 * @author xiaoxiao
 * @version 1.0
 * */
public class DocVectorCalc implements IDocCalc {

	/** 计算相应向量的和 */
	public Map<Long, Integer> add(IDocuments IDocA,
			IDocuments IDocB, IDocVectorAccessor v) {

		if (IDocA != null && v.getVector(IDocA) != null) {
			Map<Long, Integer> vectorSum = v.getVector(IDocA);

			// 取出key值构成的序列
			for (Iterator<Long> itB = v.getVector(IDocB).keySet()
					.iterator(); itB.hasNext();) {
				//Pair<Long , String> keyB = itB.next();
				long keyB = itB.next().longValue();

				//boolean contain = false;
				//for (Iterator<Long> it = vectorSum.keySet()
				//		.iterator(); it.hasNext();) {
				//	if (it.next().equals(keyB)) {
				//		contain = true;
				//		break;
				//	}
				//}
				if (vectorSum.containsKey(keyB)) {
					vectorSum.put(keyB, vectorSum.get(keyB).intValue()
							+ v.getVector(IDocB).get(keyB).intValue());
				} else {
					vectorSum
							.put(keyB, v.getVector(IDocB).get(keyB).intValue());
				}
			}
			return vectorSum;
		} else if (IDocB != null && v.getVector(IDocB) != null)
			return v.getVector(IDocB);
		else {
			return new TreeMap<Long, Integer>();
		}
	}

	/**
	 * 生成含title、content等向量和新的doc
	 * */
	public IDocuments add(IDocuments IDocA, IDocuments IDocB,
			IDocVectorAccessor[] vectors) {

		IDocuments docSum = new Documents();
		for (int i = 0; i < vectors.length; ++i) {
			vectors[i].setVector(docSum, add(IDocA, IDocB, vectors[i]));
		}
		return docSum;
	}

	/** 计算相应向量叉乘积 */
	public double multi(IDocuments IDocA, IDocuments IDocB, IDocVectorAccessor v) {

		double multi = 0;
		for (Iterator<Long> itA = v.getVector(IDocA).keySet()
				.iterator(); itA.hasNext();) {

			Long keyA = itA.next();

			//boolean contain = false;
			//for (Iterator<Long> itB = v.getVector(IDocB).keySet()
			//		.iterator(); itB.hasNext();) {
			//	if (itB.next().equals(keyA)) {
			//		contain = true;
			//		break;
			//	}
			//}
			if (v.getVector(IDocB).containsKey(keyA)) {
				multi += v.getVector(IDocA).get(keyA).intValue()
						* v.getVector(IDocB).get(keyA).intValue();
			}
		}
		return multi;

	}

	/** 计算相应向量长度 */
	public double measure(IDocuments IDocA, IDocVectorAccessor v) {

		double squaresSum = 0;
		if (v.getVector(IDocA) != null) {

			Iterator<Long> it = v.getVector(IDocA).keySet()
					.iterator();
			while (it.hasNext()) {
				squaresSum += Math.pow(v.getVector(IDocA).get(it.next())
						.intValue(), 2);
			}
		}
		return Math.sqrt(squaresSum);
	}

	/**
	 * 计算docA、docB间相应向量的余弦值
	 * */
	public double cosine(IDocuments IDocA, IDocuments IDocB,
			IDocVectorAccessor v) {

		double cosine = 0.0;
		double measureA = measure(IDocA, v);
		double measureB = measure(IDocB, v);
		if (measureA != 0 && measureB != 0) {
			cosine = multi(IDocA, IDocB, v) / (measureA * measureB);
		} else if (measureA == 0 && measureB == 0) {
			cosine = 1.0;
		}
		return cosine;
	}

	/**
	 * 找出间距值数组中值最大的编号(余弦值越大，其间夹角越小，相似度越高)
	 * */
	public int minAngle(double[] distance) {

		int maxNum = 0;
		double maxValue = distance[0];
		for (int i = 0; i < distance.length; ++i) {
			if (distance[i] > maxValue) {
				maxValue = distance[i];
				maxNum = i;
			}
		}
		return maxNum;
	}

	/** 判断相应向量是否相同 */
	@Override
	public boolean equals(IDocuments IDocA, IDocuments IDocB,
			IDocVectorAccessor v) {

		if ((IDocA == null && IDocB == null) || (v.getVector(IDocA).size() == 0 && v.getVector(IDocB).size() == 0)) {
			return true;
		}

		if (IDocA == null || IDocB == null
				|| v.getVector(IDocA).size() != v.getVector(IDocB).size()
				|| v.getVector(IDocA).size() == 0
				|| v.getVector(IDocB).size() == 0) {
			return false;
		}

		// size相同，且向量维度不重复
		int keySize = 0;
		for (Iterator<Long> itB = v.getVector(IDocB).keySet()
				.iterator(); itB.hasNext();) {

			Long keyB = itB.next();

			for (Iterator<Long> itA = v.getVector(IDocA).keySet()
					.iterator(); itA.hasNext();) {
				if (itA.next().equals(keyB)) {
					keySize++;
					break;
				}
			}
		}
		return (keySize == v.getVector(IDocB).size()) ? true : false;
	}

	/** 计算文档间距值 */
	@Override
	public double distance(IDocuments IDocA, IDocuments IDocB,
			IDocVectorAccessor[] vectors, double[] weights) {

		double distance = new Double(0);
		for (int i = 0; i < vectors.length; ++i) {
			distance += weights[i] * cosine(IDocA, IDocB, vectors[i]);
		}
		return distance;
	}

	public IDocuments center(IDocuments[] group, IDocVectorAccessor[] vectors) {

		IDocuments sum = new Documents();

		for (int i = 0; i < vectors.length; ++i) {
			for (int j = 0; j < group.length; ++j) {
				sum = add(sum, group[j], vectors);
			}
		}
		return sum;
	}
}
