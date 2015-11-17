package com.winga.xxl.classifier.model.outputer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import com.winga.xxl.classifier.data.exception.ModelException;
import com.winga.xxl.classifier.data.store.DocWordHashMap;
import com.winga.xxl.classifier.data.store.IDocuments;
import com.winga.xxl.classifier.model.IModel;
import com.winga.xxl.classifier.model.KMeansModel;

public class KMeansXmlModelOutputer extends XmlModelOutputer {

	public String outputPath;

	public KMeansXmlModelOutputer() {
	}

	public KMeansXmlModelOutputer(String string) {
		if(string != null)
		this.outputPath = string;
	}

	@Override
	public void output(IModel model) throws IOException {

		try {
			if (!(model instanceof KMeansModel)) {
				throw new ModelException("KMeans model error !");
			}
			IDocuments[] centers = ((KMeansModel) model).centers;
			String outputFilePath = this.outputPath;
			File outputFile = new File(outputFilePath);
			if (!outputFile.exists()) {
				outputFile.createNewFile();
			}
			FileWriter out = new FileWriter(outputFile);
			BufferedWriter bout = new BufferedWriter(out);
			DocWordHashMap allWordHash = DocWordHashMap.getInstance();

			bout.write("<centers>\n");
			// Output the last center array to XML file
			for (int i = 0; i < centers.length; i++) {
				bout.write("<center>\n");

				bout.write("<centerName>"
						+ centers[i].getCategory().replace('&', ' ')
						+ "</centerName>\n");

				bout.write("<contentMap>\n");
				bout.flush();
				for (Iterator<Long> itContent = centers[i].getContentVector()
						.keySet().iterator(); itContent.hasNext();) {
					Long keyContent = itContent.next();
					bout.write("<key>\n<hash>" + keyContent
							+ "</hash>\n<string>");
					bout.write(allWordHash.getWordStringHash().get(keyContent)
							.replace('&', ' ')
							+ "</string>\n<count>");
					bout.write(centers[i].getContentVector().get(keyContent)
							+ "</count>\n</key>\n");
					bout.flush();
					// FileOutputStream fos = new FileOutputStream(outputFile);
					// fos.write(lastCenterString.getBytes(),0,lastCenterString.length());
				}

				bout.write("</contentMap>\n<titleMap>\n");
				for (Iterator<Long> itTitle = centers[i].getTitleVector()
						.keySet().iterator(); itTitle.hasNext();) {
					Long keyTitle = itTitle.next();
					bout.write("<key>\n<hash>" + keyTitle + "</hash>\n<string>");

					String wordXML = allWordHash.getWordStringHash()
							.get(keyTitle).replace("&", " ");
					bout.write(wordXML + "</string>\n<count>");
					bout.write(centers[i].getTitleVector().get(keyTitle)
							+ "</count>\n</key>\n");
				}
				bout.write("</titleMap>\n</center>\n");
				bout.flush();
			}
			bout.write("</centers>");
			bout.flush();

			out.close();
			// System.out.println("Finish to create the last center XML file.");
		} catch (ModelException e) {
			e.printStackTrace();
		}
	}
}
