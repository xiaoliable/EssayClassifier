package com.winga.xxl.classifier.data.store;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import com.winga.xxl.classifier.tour.TourDocIndex;
import com.winga.xxl.classifier.tour.TourDocuments;

public class VectorProblem implements java.io.Serializable
{
	public int l;
	public double[] y;
	public VectorNode[][] x;
	
	public Map<String, Double> numCategoryMap = new TreeMap<String, Double>();

	/**
	 * <p>CreateTime : 2014-12-21
	 * */
	public void normalize(){
		if (x.length <= 1) {
			return ;
		}
		int indexNum = x[0].length;
		double nodeMin[] = new double[indexNum];
		double nodeMax[] = new double[indexNum];
		for (int j = 0; j < indexNum; j++) {
			nodeMax[j] = x[0][j].value;
			nodeMin[j] = x[0][j].value;
		}
		for (int i = 1; i < x.length; i++) {
			
			for (int j = 0; j < indexNum; j++) {
				if (x[i][j].value > nodeMax[j]) {
					nodeMax[j] = x[i][j].value;
				}
				if (x[i][j].value < nodeMin[j]) {
					nodeMin[j] = x[i][j].value;
				}
			}
		}
	}
	/**
	 * <p>CreateTime : 2014-12-21
	 * */
	public void normalize(int indexSerialNo){
		if (x.length <= 1) {
			return ;
		}
		double nodeMax = x[0][indexSerialNo].value;
		double nodeMin = x[0][indexSerialNo].value;
		
		for (int i = 1; i < x.length; i++) {
			
			if (x[i][indexSerialNo].value > nodeMax) {
				nodeMax = x[i][indexSerialNo].value;
			}
			if (x[i][indexSerialNo].value < nodeMin) {
				nodeMin = x[i][indexSerialNo].value;
			}
		}
		
		for (int i = 0; i < x.length; i++) {
			x[i][indexSerialNo].value = (x[i][indexSerialNo].value - nodeMin) / (nodeMax - nodeMin);
		}
	}
	
	public void tourInit(ArrayList<TourDocuments> tourDocs) {
		// TODO Auto-generated method stub
		int length = tourDocs.size();
		this.y = new double[length];
		this.x = new VectorNode[length][];
		int index = 0;
		for (Iterator<TourDocuments> itTourDoc = tourDocs.iterator();itTourDoc.hasNext();) {
			TourDocuments tourDoc = itTourDoc.next();
			int score = tourDoc.score;
			
			this.y[index] = (double)score;
			
			TourDocIndex indexs = new TourDocIndex();
			indexs.init(tourDoc);
			this.x[index] = indexs.vector();
			++index;
		}
		this.l = index;
	}
}
