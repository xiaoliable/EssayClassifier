package com.winga.xxl.classifier.model.outputer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.winga.xxl.classifier.data.exception.ModelException;
import com.winga.xxl.classifier.model.IModel;
import com.winga.xxl.classifier.model.NBayesModel;

public class NBXmlModelOutputer extends XmlModelOutputer {

	public String outputPath;
	
	public NBXmlModelOutputer() {
	}

	public NBXmlModelOutputer(String string) {
		this.outputPath = string;
	}

	@Override
	public void output(IModel model) throws IOException {

		try {
			if (!(model instanceof NBayesModel)) {
				throw new ModelException("Naive bayes model error !");
			}
			File outputFile = new File(this.outputPath);
			if (!outputFile.exists()) {
				outputFile.createNewFile();
			}

			Map<String, Map<Long, Double>> condprob = new HashMap<String, Map<Long, Double>>();
			Map<String, Double> categoryPriorProb = new HashMap<String, Double>();

			condprob = ((NBayesModel) model).condprob;
			categoryPriorProb = ((NBayesModel) model).priorProb;

//			DataOutputStream fp = new DataOutputStream(
//					new BufferedOutputStream(new FileOutputStream(
//							this.outputPath)));
			
			FileWriter out = new FileWriter(new File(this.outputPath));
			BufferedWriter bout = new BufferedWriter(out);

			bout.write("<model>\n");
			// Output this category documents to XML file
			for (Iterator<String> itCate = condprob.keySet().iterator(); itCate
					.hasNext();) {
				String categoryName = itCate.next();
				bout.write("<category>\n<categoryName>" + categoryName
						+ "</categoryName>\n");

				bout.write("<categoryPriorProb>"
						+ categoryPriorProb.get(categoryName)
						+ "</categoryPriorProb>\n");

				for (Iterator<Long> itWord = condprob.get(categoryName)
						.keySet().iterator(); itWord.hasNext();) {
					long wordHash = itWord.next();
					bout.write("<wordHash>" + wordHash
							+ "</wordHash>\t<wordProb>"
							+ condprob.get(categoryName).get(wordHash)
							+ "</wordProb>\n");
				}
				bout.write("</category>\n");
				bout.flush();
			}
			bout.write("</model>");
			bout.close();
		} catch (ModelException e) {
			e.printStackTrace();
		}
	}
}
