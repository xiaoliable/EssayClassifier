package com.winga.xxl.classifier.parameter;

import java.io.File;
import java.io.IOException;

import com.winga.xxl.classifier.data.exception.KMeansCenterNumException;
import com.winga.xxl.classifier.data.store.Documents;
import com.winga.xxl.classifier.data.store.DocumentsReader;
import com.winga.xxl.classifier.data.store.IDocuments;
import com.winga.xxl.classifier.model.outputer.IModelOutputer;
import com.winga.xxl.classifier.model.outputer.KMeansXmlModelOutputer;

public class KMeansTrainParameter extends TrainParameter {

	public int categoryNum = 0;
	public IDocuments[] startCenters = null;
	public IModelOutputer outputer = null;

	// TODO title content vector selector

	public KMeansTrainParameter(String outputPath) {
		IModelOutputer outputer = new KMeansXmlModelOutputer(outputPath);
		this.outputer = outputer;
	}

	public KMeansTrainParameter(int cateNum, String outputPath) {
		this.categoryNum = cateNum;
		IModelOutputer outputer = new KMeansXmlModelOutputer(outputPath);
		this.outputer = outputer;
	}

	public KMeansTrainParameter(int cateNum, String outputPath,
			IDocuments[] centers) {
		try {
			if (cateNum != centers.length) {
				throw new KMeansCenterNumException(
						"The given category number is not same as the length of the centers documents array !");
			}
			this.categoryNum = cateNum;
			if (outputPath != null)
				this.outputer = new KMeansXmlModelOutputer(outputPath);
			this.startCenters = centers;
		} catch (KMeansCenterNumException e) {
			e.printStackTrace();
		}
	}

	public KMeansTrainParameter(int cateNum, String outputPath,
			String centersXmlDir) {

		try {
			IDocuments[] sampleDocs = DocumentsReader
					.getFromXMLFileDirectory(centersXmlDir);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		File[] cateFiles = new File(centersXmlDir).listFiles();
		int k = cateFiles.length;
		try {
			if (cateNum != cateFiles.length) {
				throw new KMeansCenterNumException(
						"The given category number is not same as the length of the centers documents array !");
			}
			IDocuments[] centers = new Documents[k];

			centers = DocumentsReader.getSampleCenterXML(cateFiles);

			this.categoryNum = cateNum;
			this.startCenters = centers;
			if(outputPath != null)
			this.outputer = new KMeansXmlModelOutputer(outputPath);
		} catch (KMeansCenterNumException e) {
			e.printStackTrace();
		}
	}
}
