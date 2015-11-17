package com.winga.xxl.classifier.tour;

import java.util.regex.Pattern;

import com.winga.xxl.classifier.data.store.VectorNode;


/**
 * <p>
 * CreateTime : 11-29-2014 Store the tour note document's feature index. Such as
 * the number of pictures, words, paragraph.
 * 
 * @author xiaoxiao
 * */
public class TourDocIndex {

	//Initialize the array of tour document's indexs.
	public int indexNum = 11;
	public double[] indexArray;
	
	public double wordNum = 0;
	public double paragraphNum = 0;
	public double pictureTotalNum = 0;
	public double picturePatchMeanNum = 0;
	public double urlNum = 0;

	public double domain = 0;
	public double wopaRatio = 0;
	public double papiRatio = 0;
	public double wopiRatio = 0;

	/** The maximum continuous numbers of picture. */
	public double pictureMCN = 0;

	public double serialNum = 0;

	/** The distribution of pictures and words. */
	public double piwoDistr = 0;
	public double pawoDistr = 0;
	
	/**
	 * <p>
	 * CreateTime : 12-15-2014
	 * */
	public void init(TourDocuments doc) {
		init(doc.content);
	}
	
	/**
	 * <p>
	 * CreateTime : 12-6-2014
	 * <p>
	 * UpdateTime : 12-15-2014
	 * <p>
	 * UpdateTime : 12-21-2014
	 * */
	public void init(String content) {
		
		indexArray = new double[indexNum];

		// The document's content do not contains the continuous line feeds.
		String inputString = content.trim();

		String[] splitParaStrings = inputString.split("\n");

		//Mostly "|||NOTITLE;".
		Pattern pattern = Pattern.compile("IMAGEURL:http://\\S+\\|\\|\\|\\S+;");
		
		String[] splitPictureTotalStrings = pattern
				.split(inputString);

		pictureTotalNum = splitPictureTotalStrings.length - 1;
		indexArray[2] = pictureTotalNum;

		int flag = 0, pictruePatchNum = 1;
		for (int i = 0; i < splitPictureTotalStrings.length; i++) {
			splitPictureTotalStrings[i] = splitPictureTotalStrings[i].trim();
			
			//Count the picture patch number.
			if (splitPictureTotalStrings[i].equals("")) {
				if(flag == 0){
					++pictruePatchNum;
					flag = 1;
				}
			}else {
				flag = 0;
			}
			
			//Count the number of words.
			wordNum += splitPictureTotalStrings[i].length();
		}
		indexArray[0] = wordNum;
		
		picturePatchMeanNum = pictureTotalNum / (double)pictruePatchNum;
		indexArray[3] = picturePatchMeanNum;

		// TODO
		// pictureMCN =

		paragraphNum = splitParaStrings.length - pictureTotalNum;
		indexArray[1] = paragraphNum;

		String[] splitUrlStrings = inputString.split("http://");
		urlNum = splitUrlStrings.length - pictureTotalNum - 1;
		indexArray[4] = urlNum;
		
//		//MurmurHash.
//		domain = MurmurHash.hash64(doc.hostName);
//		indexArray[5] = domain;

		// Wipe out the words of image-urls and line feeds.
		wordNum = inputString.length() - paragraphNum;

//		for (int i = 0; i < splitParaStrings.length; i++) {
//			splitParaStrings[i] = splitParaStrings[i].trim();
//
//			if (splitParaStrings[i].startsWith("IMAGEURL:http://")) {
//				wordNum -= splitParaStrings[i].length();
//			}
//		}

		wopaRatio = wordNum / (paragraphNum + 1);
		indexArray[5] = wopaRatio;

		papiRatio = paragraphNum / (pictureTotalNum + 1);
		indexArray[6] = papiRatio;
		
		wopiRatio = wordNum / (pictureTotalNum + 1);
		indexArray[7] = wopiRatio;
		

		//TODO serialNum
		for (int i = 0; i < splitParaStrings.length; i++) {
			if (splitParaStrings[i].startsWith("第一天")
					||splitParaStrings[i].startsWith("第1天")
					||splitParaStrings[i].startsWith("第一")
					||splitParaStrings[i].startsWith("（一）")
					||splitParaStrings[i].startsWith("Day1")
					||splitParaStrings[i].startsWith("PART1")
					||splitParaStrings[i].startsWith("PART-1")
					||splitParaStrings[i].startsWith("DAY1")
					||splitParaStrings[i].startsWith("D1")
					||splitParaStrings[i].startsWith("Route1")) {
				++serialNum;
			}
		}
		indexArray[8] = serialNum;

		//piwoDistr
		int[] piwoDistrArray = new int[splitParaStrings.length];
		int index = 0;
		for (int i = 0; i < splitParaStrings.length; i++) {
			if (!splitParaStrings[i].trim().equals("")
					&& !splitParaStrings[i].startsWith("IMAGEURL:http://")) {
				piwoDistrArray[index++] = splitParaStrings[i].length();
			}
		}
		int[] piwpDistrShortArray = new int[index];
		System.arraycopy(piwoDistrArray, 0, piwpDistrShortArray, 0, index);
		piwoDistr = standardDeviation(piwoDistrArray);
		indexArray[9] = piwoDistr;

		//pawoDistr
		int[] pawoDistrArray = new int[splitPictureTotalStrings.length];
		index = 0;
		for (int i = 0; i < splitPictureTotalStrings.length; i++) {
			if (!splitPictureTotalStrings[i].trim().equals("")) {
				pawoDistrArray[index++] = splitPictureTotalStrings.length;
			}
		}
		int[] pawoDistrShortArray = new int[index];
		System.arraycopy(pawoDistrArray, 0, pawoDistrShortArray, 0, index);
		pawoDistr = standardDeviation(pawoDistrArray);
		indexArray[10] = pawoDistr;
	}
	
	public double average(int[] averageArray){
		double sum = 0;
		if(averageArray.length == 0){
			return 0;
		}else{
			for (int i = 0; i < averageArray.length; i++) {
				sum += averageArray[i];
			}
			return sum / averageArray.length;
		}
	}
	
	public double standardDeviation(int[] intArray){
		double sumOfDeviation = 0;
		if(intArray.length == 0){
			System.out.println("The array's length equals zero.");
			return -1;
		}else{
			double average = average(intArray);
			for (int i = 0; i < intArray.length; i++) {
				sumOfDeviation += (intArray[i] - average) * (intArray[i] - average);
			}
			double standardDeviation = Math.sqrt(sumOfDeviation) / intArray.length;
			return standardDeviation;
		}
	}
	
	public VectorNode[] vector(){
		VectorNode[] vectorNodes = new VectorNode[indexNum];
		
		for (int i = 0; i < indexArray.length; i++) {
			vectorNodes[i] = new VectorNode();
			vectorNodes[i].index = i;
			vectorNodes[i].value = indexArray[i];
		}
		return vectorNodes;
	}
}
