package com.winga.xxl.classifier.tour;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * <p>
 * CreateDate : 2014-12-12
 * </p>
 * <p>
 * </p>
 * 
 * @author xiaoxiao
 * @version 1.0
 */
public class TourDocParser extends DefaultHandler{


	ArrayList<TourDocuments> docArrayList = null;
	TourDocuments doc = null;
	
	int score = -1;
	String tripTime;
	String titleInfo;
	String publishTime;
	String pageCategory;
	String days2Tour;
	String averageCost;
	String tstamp;
	String segment;
	String destination;
	String digest;
	String hostName;
	String peopleNum;
	String boost;
	
	String title;
	String url;
	String content;
	String category;

	private String temp = "";// Record the element's content
	private String preTag = null;// Record the element's name
	
	public ArrayList<TourDocuments> multiParser(String tourDocsDirecPath) throws IOException, ParserConfigurationException, SAXException{
		File[] tourXMLFiles = new File(tourDocsDirecPath).listFiles();
		ArrayList<TourDocuments> tourDocs = new ArrayList<TourDocuments>();
		for (int i = 0; i < tourXMLFiles.length; i++) {
			if (tourXMLFiles[i].getName().lastIndexOf(".xml") != -1) {
				tourDocs.addAll(parser(tourXMLFiles[i].getAbsolutePath()));
			}
		}
		return tourDocs;
	}
	
	/**
	 * 
	 * <p>CreateTime : 12-13-2014
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 * */
	public ArrayList<TourDocuments> parser(String tourDocsPath) throws IOException, ParserConfigurationException, SAXException{
		
		File tourFile = new File(tourDocsPath);
		if (!tourFile.exists()) {
			return null;
		}
		InputStream xmlStream = new FileInputStream(tourFile);
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser(); 
			TourDocParser handler = new TourDocParser();
			
			parser.parse(xmlStream, handler);
			xmlStream.close();
			
			return handler.docArrayList;
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Error in the tour XML documents parser.");
		return null;
	}
	
	@Override
	public void startDocument() throws SAXException {
		docArrayList = new ArrayList<TourDocuments>();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if ("document".equals(qName)) {
			doc = new TourDocuments();
		}
		preTag = qName;// 将正在解析的节点名称赋给preTag
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {

		// Parse the relevant XML centent
		if (doc != null) {
			if ("TIME_OF_TRIP".equals(qName)) {
				tripTime = temp;
				temp = "";
			} else if ("score".equals(qName)) {
				score = Integer.parseInt(temp.trim());
				temp = "";
			} else if ("TITLE_INFO".equals(qName)) {
				titleInfo = temp;
				temp = "";
			} else if ("publishTime".equals(qName)) {
				publishTime = temp;
				temp = "";
			} else if ("pageCategory".equals(qName)) {
				pageCategory = temp;
				temp = "";
			} else if ("DAYS_TO_TRAVEL".equals(qName)) {
				days2Tour = temp;
				temp = "";
			} else if ("AVERAGE_COST".equals(qName)) {
				averageCost = temp;
				temp = "";
			} else if ("title".equals(qName)) {
				title = temp;
				temp = "";
			} else if ("url".equals(qName)) {
				url = temp;
				temp = "";
			} else if ("content".equals(qName)) {
				content = temp;
				temp = "";
			} else if ("category".equals(qName)) {
				category = temp;
				temp = "";
			} else if ("tstamp".equals(qName)) {
				tstamp = temp;
				temp = "";
			} else if ("segment".equals(qName)) {
				segment = temp;
				temp = "";
			} else if ("destination".equals(qName)) {
				destination = temp;
				temp = "";
			} else if ("digest".equals(qName)) {
				digest = temp;
				temp = "";
			} else if ("host".equals(qName)) {
				hostName = temp;
				temp = "";
			} else if ("NUMS_OF_PEOPLE".equals(qName)) {
				peopleNum = temp;
				temp = "";
			} else if ("boost".equals(qName)) {
				boost = temp;
				temp = "";
			} else if ("document".equals(qName)) {
				doc.tripTime = tripTime;
				doc.titleInfo = titleInfo;
				doc.publishTime = publishTime;
				doc.pageCategory = pageCategory;
				doc.days2Tour = days2Tour;
				doc.averageCost = averageCost;
				doc.tstamp = tstamp;
				doc.segment = segment;
				doc.destination = destination;
				doc.digest = digest;
				doc.hostName = hostName;
				doc.peopleNum = peopleNum;
				doc.boost = boost;
				doc.title = title;
				doc.url = url;
				doc.content = content;
				doc.category = category;
				doc.score = score;

				docArrayList.add(doc);
				tripTime = null;
				titleInfo = null;
				publishTime = null;
				pageCategory = null;
				days2Tour = null;
				averageCost = null;
				tstamp = null;
				segment = null;
				destination = null;
				digest = null;
				hostName = null;
				peopleNum = null;
				boost = null;
				title = null;
				url = null;
				content = null;
				category = null;
				doc = null;
				score = -1;
			}
		}
		preTag = null;
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {

		if (doc != null) {
			String content = new String(ch, start, length);

			temp += content.trim();
		}
	}
}
