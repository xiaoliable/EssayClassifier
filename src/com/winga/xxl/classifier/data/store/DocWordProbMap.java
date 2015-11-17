package com.winga.xxl.classifier.data.store;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Comparator;
import java.util.Map.Entry;

import com.winga.xxl.classifier.data.exception.SaxParseService;

/**
 * <p>
 * For the naive bayes classifier.
 * </p>
 * <p>
 * The underlayer calculation class.(Singleton pattern)
 * </p>
 * <p>
 * Version : 2014-9-22
 * </p>
 * 
 * @author xiaoxiao
 * @version 1.0
 */
public class DocWordProbMap {

	private static DocWordProbMap singleton = new DocWordProbMap();

	private DocWordProbMap() {
	}

	public static DocWordProbMap getInstance() {
		return singleton;
	}

	// Map< categoryName, Map< docID, Map< wordHash, countWordInDoc>>>
	// private Map<String, Map<Long, Map<Long, Long>>> wordNumInDocInClass = new
	// HashMap<String, Map<Long, Map<Long, Long>>>();

	// chi-square calculation variable
	/** Total words' number in all the documents. */
	private long totalDifferWordNum = 0;

	/** Total words' occurrence number in all the documents. */
	private double totalWordOccurNum = 0;
	// The appointed feature word's number for every category
	// public final static int FEATURE_WORD_NUM = 1000;

	/** Every class' total words number. */
	private Map<String, Double> totalWordNumInClass = new HashMap<String, Double>();

	/** Every word's total occurrence number in all the documents. */
	private Map<Long, Double> wordNumInTotalClass = new HashMap<Long, Double>();

	/** Every word's total occurrence number in every class. */
	private Map<String, Map<Long, Long>> wordNumInClass = new HashMap<String, Map<Long, Long>>();

	// private Map<Long, Map<String, Long>> allWordChiSquareInClass = new
	// HashMap<Long, Map<String, Long>>();
	// private List<Long> allFeatureWord = new ArrayList<Long>();

	// Train multinormalNB
	private Map<String, Map<Long, Double>> condprob = new HashMap<String, Map<Long, Double>>();
	private Map<String, Double> prior = new HashMap<String, Double>();

	/**
	 * Set the temporary data for the follow calculation.
	 */
	public void init(File categoryDir) {

		SaxParseService sax = new SaxParseService();
		IDocuments[] beginCenter = sax.ReadSampleCenterXML(categoryDir
				.listFiles());
		init(beginCenter);
	}

	/**
	 * Read the different sample center documents and calculate the NB model.
	 * 
	 * @param documents
	 *            : The sample center documents array.
	 * */
	public void init(IDocuments[] documents) {

		for (int i = 0; i < documents.length; i++) {

			// Put the content map into the title map.
			String categoryName = documents[i].getCategory();
			documents[i].getTitleVector().putAll(
					documents[i].getContentVector());
			Map<Long, Integer> contentMap = documents[i].getTitleVector();

			// Update every class' total number, every word's total occurrence
			// number, the total number of all the diffrent words,
			Map<Long, Long> wordNumInThisClass = new HashMap<Long, Long>();
			totalWordNumInClass.put(categoryName, 0D);
			for (Iterator<Long> it = contentMap.keySet().iterator(); it
					.hasNext();) {
				Long wordHash = it.next();
				int wordCount = contentMap.get(wordHash);

				// If this word never appear in the whole sample documents, we
				// should initialize some intermediate variable.
				if (wordNumInTotalClass.containsKey(wordHash)) {
					wordNumInTotalClass.put(wordHash,
							wordNumInTotalClass.get(wordHash) + wordCount);
				} else {
					wordNumInTotalClass.put(wordHash, (double) wordCount);

					++totalDifferWordNum;
				}

				// If this word never appear in this category sample documents,
				// we should initialize some intermediate variable.
				// (In the document's contentMap, there isn't two same words.)
				wordNumInThisClass.put(wordHash, (long) wordCount);
				totalWordNumInClass.put(categoryName,
						totalWordNumInClass.get(categoryName)
								+ (double) wordCount);

				totalWordOccurNum += (double) wordCount;
			}

			wordNumInClass.put(categoryName, wordNumInThisClass);
		}

		// Calculate the prior prob of every category
		for (int i = 0; i < documents.length; i++) {
			String categoryName = documents[i].getCategory();
			prior.put(categoryName, totalWordNumInClass.get(categoryName)
					/ totalWordOccurNum);
		}
	}

	/**
	 * Set the member variable allWordChiSquare, which is all the words'
	 * chi-square weight.
	 * <p>
	 * Formula : X^2(D, t, c) = ( ( N11 + N10 + N01 + N00 ) * ( N11 * N00 - N10
	 * * N01 )^2 ) / ( ( N11 + N01 ) * ( N11 + N10 ) * ( N10 + N00 ) * ( N01 +
	 * N00 ) )
	 * </p>
	 * <p>
	 * Here we use the approximate formula : chi-square-value = (N11 * N00 - N01
	 * * N10)^2 / ( N11 + N10 ) * ( N01 + N00)
	 * <p>
	 * From : 《信息检索导论》 13章5小节：特征选择 P194
	 * */
	public Map<String, Map<Long, Double>> chiSquare(int featureWordNum) {
		Map<String, Map<Long, Double>> featureWordInClass = new HashMap<String, Map<Long, Double>>();

		// Calculate every category's chiSquare feature
		for (Iterator<String> itCategory = wordNumInClass.keySet().iterator(); itCategory
				.hasNext();) {
			String categoryName = itCategory.next();
			Map<Long, Double> chiSquareInThisClassMap = new HashMap<Long, Double>();

			Map<Long, Long> wordNumInThisClass = wordNumInClass
					.get(categoryName);
			for (Iterator<Long> itWord = wordNumInThisClass.keySet().iterator(); itWord
					.hasNext();) {
				long wordHash = itWord.next();
				double N11 = wordNumInThisClass.get(wordHash);
				double N10 = wordNumInTotalClass.get(wordHash) - N11;
				double N01 = totalWordNumInClass.get(categoryName) - N11;
				double N00 = totalWordOccurNum - N11 - N10 - N01;

				// 分子
				double numerator = Math.pow(N11 * N00 - N10 * N01, 2);
				// 分母
				double denominator = wordNumInTotalClass.get(wordHash)
						* (totalWordOccurNum - wordNumInTotalClass
								.get(wordHash));

				double chiSquareInThisClass = numerator / denominator;
				chiSquareInThisClassMap.put(wordHash, chiSquareInThisClass);
			}

			// Sort every category's words to get the first FEATURE_WORD_NUM
			// number chiSquare feature words
			List<Map.Entry<Long, Double>> mappingList = new ArrayList<Map.Entry<Long, Double>>(
					chiSquareInThisClassMap.entrySet());
			Collections.sort(mappingList,
					new Comparator<Map.Entry<Long, Double>>() {
						public int compare(Map.Entry<Long, Double> mapping1,
								Map.Entry<Long, Double> mapping2) {
							// Descending sort
							return mapping2.getValue().compareTo(
									mapping1.getValue());
						}
					});
			// Record featureWordNum word's chiSquare value in every category to
			// allWordChiSquateInClass featureWordInClass
			Iterator<Entry<Long, Double>> itWord = mappingList.iterator();

			Map<Long, Double> featureWordInThisCLass = new HashMap<Long, Double>();

			for (int i = 0; i < featureWordNum && itWord.hasNext(); ++i) {
				Entry<Long, Double> wordEntry = itWord.next();
				featureWordInThisCLass.put(wordEntry.getKey(),
						chiSquareInThisClassMap.get(wordEntry.getKey()));
			}

			featureWordInClass.put(categoryName, featureWordInThisCLass);
		}
		return featureWordInClass;
	}

	/**
	 * <p>
	 * Title: Train multinormalNB
	 * <p>
	 * 
	 * @param allFeatureWord
	 *            : chi-square feature words
	 * @param wordNumInClass
	 *            : category set
	 * @return conprob: P( word | c )
	 * */
	public void chiSquareTrainMultiNormalNB(
			Map<String, Map<Long, Double>> featureWordInClass) {

		for (Iterator<String> itCategory = wordNumInClass.keySet().iterator(); itCategory
				.hasNext();) {
			String categoryName = itCategory.next();
			Map<Long, Double> conprobWordInThisClass = new HashMap<Long, Double>();

			// Every feature word should calculate its chiSquare value in this
			// category
			for (Iterator<Long> itWord = featureWordInClass.get(categoryName)
					.keySet().iterator(); itWord.hasNext();) {
				long wordHash = itWord.next();
				if (wordNumInClass.get(categoryName).containsKey(wordHash)) {
					conprobWordInThisClass
							.put(wordHash,
									(wordNumInClass.get(categoryName).get(
											wordHash) + 1)
											/ (totalWordNumInClass
													.get(categoryName) + totalDifferWordNum));
				} else {
					conprobWordInThisClass.put(wordHash,
							(1 / (totalWordNumInClass.get(categoryName))));
				}
			}
			condprob.put(categoryName, conprobWordInThisClass);
		}
	}

	public void trainMultiNormalNB() {

		for (Iterator<String> itCategory = wordNumInClass.keySet().iterator(); itCategory
				.hasNext();) {
			String categoryName = itCategory.next();
			Map<Long, Double> conprobWordInThisClass = new HashMap<Long, Double>();

			// Every feature word should calculate its chiSquare value in this
			// category
			for (Iterator<Long> itWord = wordNumInClass.get(categoryName)
					.keySet().iterator(); itWord.hasNext();) {
				long wordHash = itWord.next();

				conprobWordInThisClass.put(wordHash,
						(wordNumInClass.get(categoryName).get(wordHash) + 1)
								/ (totalWordNumInClass.get(categoryName)));

			}
			condprob.put(categoryName, conprobWordInThisClass);
		}
	}

	//
	// public void outputChiSquareProb(File outputFile) throws IOException {
	//
	// if (condprob.size() == 0) {
	// System.out
	// .println("You need to use the NBClassifier function to initialize all the intermediate variable firstly!");
	// return;
	// }
	//
	// try {
	// if (!outputFile.exists()) {
	// outputFile.createNewFile();
	// }
	// } catch (Exception e) {
	// System.out.print("Fail to create this specified XML file.");
	// }
	//
	// FileWriter out = new FileWriter(outputFile);
	// BufferedWriter bout = new BufferedWriter(out);
	// DocWordHashMap wordsDictionary = DocWordHashMap.getInstance();
	//
	// bout.write("<naiveBeyes>\n");
	// for (Iterator<String> itCategory = featureWordInClass.keySet()
	// .iterator(); itCategory.hasNext();) {
	// String categoryName = itCategory.next();
	// bout.write("<category>\n<categoryName>\n" + categoryName
	// + "\n</categoryName>\n");
	//
	// for (Iterator<Long> itWord = featureWordInClass.get(categoryName)
	// .keySet().iterator(); itWord.hasNext();) {
	// long wordHash = itWord.next();
	// String word = wordsDictionary.getWordStringHash().get(wordHash);
	// bout.write("<featureWord>\n<wordHash>\n" + wordHash
	// + "\n</wordHash>\n");
	// bout.write("<string>\n" + word + "\n</string>\n");
	// bout.write("<chiSquare>\n"
	// + featureWordInClass.get(categoryName).get(wordHash)
	// + "\n</chiSquare>\n</featureWord>\n");
	// }
	// bout.write("\n</category>\n");
	// }
	// bout.write("\n</naiveBeyes>\n");
	// bout.flush();
	// out.close();
	// }

	/**
	 * Initialize all the intermediate variable.
	 * 
	 * @param file
	 *            : The directory of all the category sample documents
	 * */
	public void chiSquareNBclassifier(File file, int featureWordNum) {
		init(file);
		chiSquareTrainMultiNormalNB(chiSquare(featureWordNum));
	}

	/**
	 * <p>
	 * CreateTime : 2014-11-20
	 * 
	 * */
	public void chiSquareNBclassifier(IDocuments[] docs, int featureWordNum) {

		// initialize the naive bayes word prob map and the category word prob map.
		init(docs);

		// Generate the condprob and prior map of the model.
		chiSquareTrainMultiNormalNB(chiSquare(featureWordNum));
	}

	public Map<String, Map<Long, Double>> getCondprob() {
		return condprob;
	}

	public Map<String, Double> getPrior() {
		return prior;
	}
}
