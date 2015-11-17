package com.winga.xxl.classifier.model.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.winga.xxl.classifier.calculation.ContentVectorAccessor;
import com.winga.xxl.classifier.calculation.IDocVectorAccessor;
import com.winga.xxl.classifier.calculation.TitleVectorAccessor;
import com.winga.xxl.classifier.data.store.Documents;
import com.winga.xxl.classifier.data.store.IDocuments;
import com.winga.xxl.classifier.model.IModel;
import com.winga.xxl.classifier.model.KMeansModel;

/**
 * <p>
 * CreateDate : 2014-9-12
 * </p>
 * <p>
 * CreateDate : 2014-11-15
 * </p>
 * <p>
 * This class contains the method to judge a document's category and parsing the
 * last center documents XML file generated by the KMeans cluster.
 * </p>
 * 
 * @author xiaoxiao
 * @version 1.0
 */
public class KMeansXmlModelParser extends DefaultHandler implements XmlModelParser{
	private List<IDocuments> centers = null;
	private IDocuments center = null;

	private IDocVectorAccessor dva = null;// The title and content's flag
	private Long hashNum = null;
	private Integer countNum = null;
	private String temp = "";// Record the element's content
	private String preTag = null;// Record the element's name

	public KMeansXmlModelParser() {
		centers = new ArrayList<IDocuments>();
	}

	/**
	 * Use SAX to parse the lastCenter.xml file and initialize the last center
	 * document
	 * */
	public static IDocuments[] readXMLCenterDocs(File file) throws Exception {

		InputStream xmlStream = new FileInputStream(file);
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();

			SAXParser parser = factory.newSAXParser();

			KMeansXmlModelParser handler = new KMeansXmlModelParser();
			parser.parse(xmlStream, handler);
			xmlStream.close();

			IDocuments[] documents = handler.centers
					.toArray(new Documents[handler.centers.size()]);

			return documents;
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Error in the KMeans XML model parser.");
		return null;
	}

	@Override
	public void startDocument() throws SAXException {
		centers = new ArrayList<IDocuments>();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if ("center".equals(qName)) {
			center = new Documents();
		} else if ("titleMap".equals(qName)) {
			dva = new TitleVectorAccessor();
		} else if ("contentMap".equals(qName)) {
			dva = new ContentVectorAccessor();
		} else if ("key".equals(qName)) {
			hashNum = new Long(0);
			countNum = new Integer(0);
		}
		preTag = qName;// 将正在解析的节点名称赋给preTag
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {

		// Parse the relevant XML centent
		if (center != null) {
			if ("centerName".equals(qName)) {
				center.setCategory(temp);
				temp = "";
			} else if ("hash".equals(qName)) {
				hashNum = Long.valueOf(temp).longValue();
				temp = "";
			} else if ("count".equals(qName)) {
				countNum = Integer.valueOf(temp).intValue();
				temp = "";
			} else if ("key".equals(qName)) {
				// Add the the word's hash and count into the dva specified
				// (title or content) vector.
				if (hashNum != null && countNum != null)
					dva.getVector(center).put(hashNum, countNum);
				hashNum = null;
				countNum = null;
				temp = "";
			} else if (("titleMap".equals(qName) || "contentMap".equals(qName))) {
				dva = null;
			} else if ("center".equals(qName)) {
				centers.add(center);
				center = null;
			}
		}
		preTag = null;
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {

		if (center != null) {
			String content = new String(ch, start, length);

			// if (content != null && !content.trim().equals("")
			// && !content.trim().equals("\n")) {

			if ("hash".equals(preTag) || "count".equals(preTag)
					|| "centerName".equals(preTag)) {
				temp += content;
			}
		}
	}
	
	public IModel parser(String modelPath) throws Exception{
		File modelFile = new File(modelPath);
		
		IModel model = new KMeansModel(readXMLCenterDocs(modelFile));
		return model;
	}

	/**
	 * Model parser for the HDFS system.
	 * @CreateTime : 2015-2-4
	 * @author Xiaoliable
	 * */
	@Override
	public IModel hdfsParser(String xmlModelFilePath) throws Exception {
		
		Configuration conf = new Configuration();
		FileSystem hdfs = FileSystem.get(URI.create(xmlModelFilePath), conf);
		
		try {
			InputStream xmlStream = hdfs.open(new Path(xmlModelFilePath));
			SAXParserFactory factory = SAXParserFactory.newInstance();

			SAXParser parser = factory.newSAXParser();

			KMeansXmlModelParser handler = new KMeansXmlModelParser();
			parser.parse(xmlStream, handler);
			xmlStream.close();

			IDocuments[] documents = handler.centers
					.toArray(new Documents[handler.centers.size()]);

			IModel model = new KMeansModel(documents);
			return model;
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Error in the KMeans XML model parser.");
		return null;
	}
}
