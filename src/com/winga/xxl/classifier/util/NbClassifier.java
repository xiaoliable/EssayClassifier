package com.winga.xxl.classifier.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.elasticsearch.common.inject.internal.InternalFactory.Instance;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.winga.xxl.classifier.data.exception.ModelException;
import com.winga.xxl.classifier.data.exception.SaxParseService;
import com.winga.xxl.classifier.data.exception.TrainParamException;
import com.winga.xxl.classifier.data.store.DocWordHashMap;
import com.winga.xxl.classifier.data.store.DocWordProbMap;
import com.winga.xxl.classifier.data.store.Documents;
import com.winga.xxl.classifier.data.store.IDocuments;
import com.winga.xxl.classifier.data.store.VectorNode;
import com.winga.xxl.classifier.data.store.VectorProblem;
import com.winga.xxl.classifier.model.IModel;
import com.winga.xxl.classifier.model.KMeansModel;
import com.winga.xxl.classifier.model.NBayesModel;
import com.winga.xxl.classifier.model.outputer.IModelOutputer;
import com.winga.xxl.classifier.parameter.NBTrainParameter;
import com.winga.xxl.classifier.parameter.PredictParameter;
import com.winga.xxl.classifier.parameter.TrainParameter;

/**
 * The higher layer class for operating the lower calculation
 * layer---DocWordProbMap.
 * <p>
 * Version : 2014-9-28
 * <p>
 * Version : 2014-11-11
 * 
 * @author xiaoxiao
 * */
public class NbClassifier implements IClassifier {

	Map<String, Map<Long, Double>> condprob = new HashMap<String, Map<Long, Double>>();
	Map<String, Double> prior = new HashMap<String, Double>();

	public NbClassifier() {
	}

	/**
	 * Finish to initialize the need intermediate conditional probability
	 * variable with the whole words' conditional probability in every category.
	 * <p>
	 * Version : 2014-9-26
	 * <p>
	 * Version : 2014-11-15
	 * */
	public NbClassifier(String sampleFilePath) {

		File sampleFile = new File(sampleFilePath);

		DocWordProbMap NBClassifier = DocWordProbMap.getInstance();
		NBClassifier.init(sampleFile);
		NBClassifier.trainMultiNormalNB();
		this.condprob = NBClassifier.getCondprob();
		this.prior = NBClassifier.getPrior();
	}

	/**
	 * Finish to initialize the need intermediate conditional probability
	 * variable with the appointed feature word numbers.
	 * <p>
	 * Version : 2014-9-26
	 * <p>
	 * Version : 2014-11-15
	 * 
	 * @author xiaoxiao
	 * @param featureWordNum
	 *            Initialize the number of feature-words.
	 * */
	public NbClassifier(String sampleFilePath, int featureWordNum) {

		File sampleFile = new File(sampleFilePath);
		DocWordProbMap NBClassifier = DocWordProbMap.getInstance();
		NBClassifier.chiSquareNBclassifier(sampleFile, featureWordNum);
		this.condprob = NBClassifier.getCondprob();
		this.prior = NBClassifier.getPrior();
	}

	public NbClassifier(File sampleFile, int featureWordNum) {

		DocWordProbMap NBClassifier = DocWordProbMap.getInstance();
		NBClassifier.chiSquareNBclassifier(sampleFile, featureWordNum);
		this.condprob = NBClassifier.getCondprob();
		this.prior = NBClassifier.getPrior();
	}

	@Override
	public IModel train(IDocuments[] sampleDocs, TrainParameter param)
			throws IOException {
		try {
			if (!(param instanceof NBTrainParameter)) {
				throw new TrainParamException("NB train parameter error !");
			}
			int featureNum = ((NBTrainParameter) param).featureNum;
			IModelOutputer outputer = ((NBTrainParameter) param).outputer;
			DocWordProbMap NBproblem = DocWordProbMap.getInstance();
			NBproblem.chiSquareNBclassifier(sampleDocs, featureNum);
			IModel model = new NBayesModel(NBproblem.getCondprob(),
					NBproblem.getPrior());
			if (outputer != null) {
				outputer.output(model);
			}
			return model;
		} catch (TrainParamException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * <p>
	 * Title: Apply multinormalNB
	 * <p>
	 * Input: Plenty of intermediate variable
	 * <p>
	 * We combine title and content domain into the generative process of NB
	 * intermediate variable acquiescently.
	 * 
	 * @param doc
	 *            : The webpage documents
	 * 
	 * @return The category name
	 * */

	@Override
	public String predict(IDocuments doc, IModel model, PredictParameter param) {

		try {
			if (!(model instanceof NBayesModel)) {
				throw new ModelException("Naive bayes model error !");
			}
			// logger.info("Start to apply MultiNormalNB ...");
			Map<String, Map<Long, Double>> condprob = ((NBayesModel) model).condprob;
			Map<String, Double> categoryPriorProb = ((NBayesModel) model).priorProb;

			doc.getTitleVector().putAll(doc.getContentVector());
			Map<Long, Integer> pendingDoc = doc.getTitleVector();

			Map<String, Double> categoryWeight = new HashMap<String, Double>();
			for (Iterator<String> itCategory = condprob.keySet().iterator(); itCategory
					.hasNext();) {
				String categoryName = itCategory.next();
				double weight = -Math.log(categoryPriorProb.get(categoryName));

				for (Iterator<Long> itWord = pendingDoc.keySet().iterator(); itWord
						.hasNext();) {
					long wordHash = itWord.next();
					if (condprob.get(categoryName).containsKey(wordHash)) {
						weight += -Math.log(condprob.get(categoryName).get(
								wordHash))
								* pendingDoc.get(wordHash);
						// * pendingDoc.get(wordHash);
					}
				}
				categoryWeight.put(categoryName, weight);
			}

			// Catch the max category
			Iterator<String> iteratorCategory = categoryWeight.keySet()
					.iterator();
			String endCategory = iteratorCategory.next();
			double maxValue = categoryWeight.get(endCategory);
			while (iteratorCategory.hasNext()) {
				String tempCategory = iteratorCategory.next();
				if (categoryWeight.get(tempCategory) > maxValue) {
					maxValue = categoryWeight.get(tempCategory);
					endCategory = tempCategory;
				}
			}
			return endCategory;
		} catch (ModelException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String predict(IDocuments doc) {
		doc.getTitleVector().putAll(doc.getContentVector());
		Map<Long, Integer> pendingDoc = doc.getTitleVector();

		Map<String, Double> categoryWeight = new HashMap<String, Double>();
		for (Iterator<String> itCategory = condprob.keySet().iterator(); itCategory
				.hasNext();) {
			String categoryName = itCategory.next();
			double weight = -Math.log(prior.get(categoryName));

			for (Iterator<Long> itWord = pendingDoc.keySet().iterator(); itWord
					.hasNext();) {
				long wordHash = itWord.next();
				if (condprob.get(categoryName).containsKey(wordHash)) {
					weight += -Math.log(condprob.get(categoryName)
							.get(wordHash)) * pendingDoc.get(wordHash);
					// * pendingDoc.get(wordHash);
				}
			}
			categoryWeight.put(categoryName, weight);
		}

		// Catch the max category
		Iterator<String> iteratorCategory = categoryWeight.keySet().iterator();
		String endCategory = iteratorCategory.next();
		double maxValue = categoryWeight.get(endCategory);
		while (iteratorCategory.hasNext()) {
			String tempCategory = iteratorCategory.next();
			if (categoryWeight.get(tempCategory) > maxValue) {
				maxValue = categoryWeight.get(tempCategory);
				endCategory = tempCategory;
			}
		}
		return endCategory;
	}

	/**
	 * Calculate the product of every words of content and title's condition
	 * probability.
	 * 
	 * @return The classification result
	 * */
	public String applyMultiNormalNB(String title, String content) {
		IDocuments doc = new Documents();
		doc.setTitle(title);
		doc.setContent(content);
		doc.init(new IKAnalyzer());

		return predict(doc);
	}

	/**
	 * Calculate the product of every words of content and title's condition
	 * probability.
	 * <p>
	 * And show every category's weight value details.
	 * 
	 * @return The classification result
	 * */
	public String applyMultiNormalNBwithDetail(String title, String content) {
		IDocuments doc = new Documents();
		doc.setTitle(title);
		doc.setContent(content);
		doc.init(new IKAnalyzer());

		// logger.info("Start to apply MultiNormalNB ...");
		doc.getTitleVector().putAll(doc.getContentVector());
		Map<Long, Integer> pendingDoc = doc.getTitleVector();

		Map<String, Double> categoryWeight = new HashMap<String, Double>();
		for (Iterator<String> itCategory = condprob.keySet().iterator(); itCategory
				.hasNext();) {
			String categoryName = itCategory.next();
			double weight = -Math.log(prior.get(categoryName));

			for (Iterator<Long> itWord = pendingDoc.keySet().iterator(); itWord
					.hasNext();) {
				long wordHash = itWord.next();
				if (condprob.get(categoryName).containsKey(wordHash)) {
					weight += -Math.log(condprob.get(categoryName)
							.get(wordHash)) * pendingDoc.get(wordHash);
					// * pendingDoc.get(wordHash);
				}
			}

			categoryWeight.put(categoryName, weight);
		}

		// Catch the max category
		Iterator<String> iteratorCategory = categoryWeight.keySet().iterator();
		String endCategory = iteratorCategory.next();
		double maxValue = categoryWeight.get(endCategory);
		while (iteratorCategory.hasNext()) {
			String tempCategory = iteratorCategory.next();
			if (categoryWeight.get(tempCategory) > maxValue) {
				maxValue = categoryWeight.get(tempCategory);
				endCategory = tempCategory;
			}
		}
		// Print every category's weight value
		for (Iterator<String> itCategory = categoryWeight.keySet().iterator(); itCategory
				.hasNext();) {
			String categoryName = itCategory.next();
			System.out.println("Category <" + categoryName
					+ ">'s weight is :\t" + categoryWeight.get(categoryName));
		}
		return endCategory;
	}

	/**
	 * Output every category's words' conditional probability in this NaiveBeyes
	 * calculation result for other documents classification.
	 * */
	public void outputWordProb(File outputFile) throws IOException {

		if (condprob.size() == 0) {
			System.out
					.println("You need to use the NBClassifier function to initialize all the intermediate variable firstly!");
			return;
		}

		try {
			if (!outputFile.exists()) {
				outputFile.createNewFile();
			}
		} catch (Exception e) {
			System.out.print("Fail to create this specified XML file.");
		}

		FileWriter out = new FileWriter(outputFile);
		BufferedWriter bout = new BufferedWriter(out);
		DocWordHashMap wordsDictionary = DocWordHashMap.getInstance();

		bout.write("<naiveBeyes>\n");
		for (Iterator<String> itCategory = condprob.keySet().iterator(); itCategory
				.hasNext();) {
			String categoryName = itCategory.next();
			bout.write("<category>\n<categoryName>\n" + categoryName
					+ "\n</categoryName>\n");

			for (Iterator<Long> itWord = condprob.get(categoryName).keySet()
					.iterator(); itWord.hasNext();) {
				long wordHash = itWord.next();
				String word = wordsDictionary.getWordStringHash().get(wordHash);
				bout.write("<word>\n<wordHash>\n" + wordHash
						+ "\n</wordHash>\n");
				bout.write("<string>\n" + word + "\n</string>\n");
				bout.write("<chiSquare>\n"
						+ condprob.get(categoryName).get(wordHash)
						+ "\n</chiSquare>\n</word>\n");
			}
			bout.write("\n</category>\n");
		}
		bout.write("\n</naiveBeyes>\n");
		bout.flush();
		out.close();
	}

	/**
	 * Generate the beginCenter and groups Documents array to invoke the
	 * SaxParseService class' outputAllResultXML method.
	 * <p>
	 * Version : 2014-9-28
	 * */
	public IDocuments[][] outputAllResultXML(IDocuments[] documents,
			File outputAllCategoryDirectory) throws IOException {
		if (!outputAllCategoryDirectory.isDirectory()
				|| !outputAllCategoryDirectory.exists()) {
			outputAllCategoryDirectory.mkdirs();
		}

		// Generate the beginCenter and groups Documents array to invoke the
		// SaxParseService class's outputAllResultXML method.
		SaxParseService sax = new SaxParseService();
		Map<String, List<IDocuments>> groupsMap = new HashMap<String, List<IDocuments>>();
		for (int i = 0; i < documents.length; i++) {

			String categoryName = predict(documents[i]);
			if (groupsMap.keySet().contains(categoryName)) {
				groupsMap.get(categoryName).add(documents[i]);
			} else {
				List<IDocuments> categoryList = new ArrayList<IDocuments>();
				categoryList.add(documents[i]);
				groupsMap.put(categoryName, categoryList);
			}
		}
		int k = prior.keySet().size();
		IDocuments[] beginCenter = new Documents[k];
		IDocuments[][] groups = new Documents[k][];
		int i = 0;
		for (Iterator<String> itCategory = prior.keySet().iterator(); itCategory
				.hasNext();) {
			String categoryName = itCategory.next();
			beginCenter[i] = new Documents();
			int iNum = groupsMap.get(categoryName).size();
			groups[i] = new Documents[iNum];
			beginCenter[i].setTitle(categoryName);
			// Exception in thread "main" java.lang.ClassCastException:
			// [Ljava.lang.Object; cannot be cast to
			// [Lcom.winga.xxl.cluster.data.IDocuments;
			// groups[i] = (IDocuments[])groupsMap.get(categoryName).toArray();

			for (int j = 0; j < iNum; j++) {
				groups[i][j] = groupsMap.get(categoryName).get(j);
			}
			++i;
		}
		// Invoke the existing outputResultXML method from SaxParseService class
		sax.outputAllResultXML(groups, beginCenter, outputAllCategoryDirectory);
		return groups;
	}

	@Override
	public double predict(VectorNode[] nodeArray, IModel model,
			PredictParameter predictParameter) throws IOException {
		// TODO Auto-generated method stub
		System.out.println("Naive bayes vecotr-problem predicting have not complete.");

		return -1;
	}

	@Override
	public IModel train(VectorProblem problem, TrainParameter param)
			throws IOException {
		// TODO Auto-generated method stub
		System.out.println("Naive bayes vecotr-problem training have not complete.");
		return null;
	}
}