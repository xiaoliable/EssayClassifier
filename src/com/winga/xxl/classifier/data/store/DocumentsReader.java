package com.winga.xxl.classifier.data.store;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.lucene.analysis.Analyzer;
import org.wltea.analyzer.lucene.IKAnalyzer;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.winga.xxl.classifier.util.KmeansCluster;

/**
 * <p>
 * CreateDate : 2014-9-12
 * </p>
 * <p>
 * This class contains the method to output the last center XML file and all the
 * different category documents XML files.
 * </p>
 * <p>
 * And the method to parsing every category's XML file and big pending XML files
 * documents XML file.
 * </p>
 * 
 * @author xiaoxiao
 * @version 1.0
 */
/**
 * Get the sample documents from the specified XML file or directory.
 * <p>
 * CreateTime : 2014-11-11
 * */
public class DocumentsReader extends DefaultHandler {
	private List<IDocuments> IDocs = new ArrayList<IDocuments>();
	private IDocuments IDoc = null;
	private static Analyzer analyzer = new IKAnalyzer();

	private String temp = "";// Record the element's content
	private String preTag = null;// Record the element's name

	public static IDocuments[] getFromTheXMLFile(String theXMLFilePath)
			throws IOException {
		if (theXMLFilePath == null) {
			return null;
		}
		File sampleXMLFile = new File(theXMLFilePath);
		return getFromTheXMLFile(sampleXMLFile);
	}

	/**
	 * <p>
	 * CreateDate : 2014-10-28
	 * </p>
	 * <p>
	 * UpdateDate : 2014-11-14
	 * </p>
	 * <p>
	 * Use the titleVector to calculate subsequently.
	 * </p>
	 * Get documents from the specified XML file.
	 */
	public static IDocuments[] getFromTheXMLFile(File sampleXMLFile)
			throws IOException {
		IDocuments[] categoryDocs = null;

		if (!sampleXMLFile.exists()) {
			return null;
		}

		// 使用SAX解析xml文件，初始化documents数组中的基本成员变量值

		InputStream xmlStream = new FileInputStream(sampleXMLFile);
		// InputStream input =
		// DocumentsReader.class.getClassLoader().getResourceAsStream("document.xml");

		try {
			// 创建一个解析XML的工厂对象
			SAXParserFactory factory = SAXParserFactory.newInstance();
			// 创建一个解析XML的对象
			SAXParser parser = factory.newSAXParser();

			// 设置解析器的相关特性，http://xml.org/sax/features/namespaces = true
			// 表示开启命名空间特性
			// saxParser.setProperty("http://xml.org/sax/features/namespaces",true);

			// 创建一个解析助手类
			DocumentsReader dr = new DocumentsReader();
			parser.parse(xmlStream, dr);
			xmlStream.close();

			// Remove the empty document in the docs arrayList
			// Iterator<IDocuments> itr = dr.IDocs.iterator();
			int docsLength = dr.IDocs.size();

			for (int i = 0; i < docsLength; i++) {
				// // Use the regex to delete the images URL
				// // 可直接注释此部分代码，显示image URL
				// String content = dr.IDocs.get(i).getContent();
				// Pattern pattern = Pattern.compile("IMAGEURL:.*?\n");
				// Matcher matcher = pattern.matcher(content);
				// while (matcher.find()) {
				// String temp = matcher.group(0);
				// content = content.replace(temp, "");
				// }
				// dr.IDocs.get(i).setContent(content);

				if (dr.IDocs.get(i).getTitle().trim().equals("")
						|| dr.IDocs.get(i).getContent().trim().equals("")) {
					dr.IDocs.remove(i);
					i--;
					docsLength--;
				}
			}

			IDocuments[] documents = dr.IDocs.toArray(new Documents[dr.IDocs
					.size()]);

			// 初始化得到每个doc的文本特征向量
			for (int i = 0; i < documents.length; ++i) {

				// Initialize the document's title and content's vector map.
				// TODO
				documents[i].init(analyzer);
				documents[i].setId(i + "");
			}

			return documents;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Output every category's documents array to the corresponding XML file in
	 * the specified directory with the settled format.
	 * 
	 * @param groups
	 *            - the classification result ( double dimensional array )
	 * @param beginCenter
	 *            - the original center array according to the different
	 *            categories' sample XML files
	 * @param outputFile
	 *            - the output XML file address
	 * */
	public void outputLastCenterXML(IDocuments[][] groups,
			IDocuments[] beginCenter, File outputFile) throws IOException {

		DocWordHashMap wordHash = DocWordHashMap.getInstance();
		// Get the last center array
		IDocuments[] lastCenter = new Documents[groups.length];
		for (int i = 0; i < groups.length; i++) {
			lastCenter[i] = new KmeansCluster().center(groups[i]);
			lastCenter[i].setTitle(beginCenter[i].getTitle());
		}

		try {
			if (!outputFile.exists())
				outputFile.createNewFile();
		} catch (Exception e) {
			System.out.print("Fail to create the output XML file.");
		}
		FileWriter out = new FileWriter(outputFile);
		BufferedWriter bout = new BufferedWriter(out);

		bout.write("<centers>\r\n");
		// Output the last center array to XML file
		for (int i = 0; i < lastCenter.length; i++) {
			bout.write("<center>\r\n");

			bout.write("<centerName>"
					+ lastCenter[i].getTitle().replace('&', ' ')
					+ "</centerName>\r\n");

			bout.write("<contentMap>\r\n");
			bout.flush();
			for (Iterator<Long> itContent = lastCenter[i].getContentVector()
					.keySet().iterator(); itContent.hasNext();) {
				Long keyContent = itContent.next();
				bout.write("<key>\r\n<hash>" + keyContent
						+ "</hash>\r\n<string>");
				bout.write(wordHash.getWordStringHash().get(keyContent)
						.replace('&', ' ')
						+ "</string>\r\n<count>");
				bout.write(lastCenter[i].getContentVector().get(keyContent)
						+ "</count>\r\n</key>\r\n");
				bout.flush();
				// FileOutputStream fos = new FileOutputStream(outputFile);
				// fos.write(lastCenterString.getBytes(),0,lastCenterString.length());
			}

			bout.write("</contentMap>\r\n<titleMap>\r\n");
			for (Iterator<Long> itTitle = lastCenter[i].getTitleVector()
					.keySet().iterator(); itTitle.hasNext();) {
				Long keyTitle = itTitle.next();
				bout.write("<key>\r\n<hash>" + keyTitle + "</hash>\r\n<string>");

				String wordXML = wordHash.getWordStringHash().get(keyTitle)
						.replace("&", " ");
				bout.write(wordXML + "</string>\r\n<count>");
				bout.write(lastCenter[i].getTitleVector().get(keyTitle)
						+ "</count>\r\n</key>\r\n");
			}
			bout.write("</titleMap>\r\n</center>\r\n");
			bout.flush();
		}
		bout.write("</centers>");
		bout.flush();

		out.close();
		// System.out.println("Finish to create the last center XML file.");
	}

	/**
	 * Output every category's documents array to XML file.
	 * 
	 * @param groups
	 *            - the classification result ( double dimensional array )
	 * @param beginCenter
	 *            - the original center array according to the different
	 *            categories' sample XML files
	 * @param outputFile
	 *            - the output file directory
	 * @throws IOException
	 * */
	public void outputAllResultXML(IDocuments[][] groups,
			IDocuments[] beginCenter, File outputAllCategoryDirectory)
			throws IOException {
		if (!outputAllCategoryDirectory.isDirectory()) {
			System.out.println("Directory "
					+ outputAllCategoryDirectory.getName() + " is not exist !");
		}

		for (int i = 0; i < beginCenter.length; i++) {
			File outputCategoryFile = new File(
					outputAllCategoryDirectory.getAbsolutePath()
							+ File.separator + beginCenter[i].getTitle()
							+ ".xml");
			outputTheXML(groups[i], outputCategoryFile);
		}
	}

	/**
	 * <p>
	 * CreateDate : 2014-9-12
	 * </p>
	 * <p>
	 * The underlying method to output the documents XML file.
	 * </p>
	 */
	public void outputTheXML(IDocuments[] documents, File outputCategoryFile)
			throws IOException {
		try {
			if (!outputCategoryFile.exists())
				outputCategoryFile.createNewFile();
		} catch (Exception e) {
			System.out.print("Fail to create this specified XML file.");
		}

		FileWriter out = new FileWriter(outputCategoryFile);
		BufferedWriter bout = new BufferedWriter(out);

		bout.write("<documents>\r\n");
		// Output this category documents to XML file
		for (int i = 0; i < documents.length; i++) {
			bout.write("<document>\r\n");
			bout.write("<category>\r\n" + documents[i].getCategory()
					+ "</category>\r\n");
			bout.write("<url>\r\n" + documents[i].getURL() + "</url>\r\n");
			bout.write("<title>\r\n" + documents[i].getTitle() + "</title>\r\n");
			bout.write("<content>\r\n" + documents[i].getContent()
					+ "</content>\r\n");
			bout.write("</document>\r\n");
			bout.flush();
		}
		bout.write("</documents>");
		bout.flush();

		out.close();
	}

	/**
	 * <p>
	 * CreateDate : 2014-9-12
	 * </p>
	 * <p>
	 * Parse big pending XML files documents XML file.
	 * </p>
	 */
	public IDocuments[] readAllXMLDocs(File[] files) throws Exception {

		IDocuments[] docs = null;

		int i = 0;
		if (files.length < 1) {
			return null;
		} else {
			docs = new DocumentsReader().readTheXMLDocs(files[i++]);
			while (i < files.length) {
				String fileName = files[i].getName();
				String fileSuffix = fileName.substring(fileName
						.lastIndexOf(".") + 1);
				// "==" Reference is equal.
				if (fileSuffix.equals("xml")) {
					DocumentsReader sax = new DocumentsReader();
					IDocuments[] thisDocs = sax.readTheXMLDocs(files[i]);
					IDocuments[] tempDocs = new Documents[docs.length
							+ thisDocs.length];
					System.arraycopy(docs, 0, tempDocs, 0, docs.length);
					System.arraycopy(thisDocs, 0, tempDocs, docs.length,
							thisDocs.length);
					docs = tempDocs;
				}
				++i;
			}
		}
		return docs;
	}

	/**
	 * <p>
	 * CreateDate : 2014-9-12
	 * </p>
	 * <p>
	 * The underlying method to parse the specified documents XML file.
	 * </p>
	 */
	public IDocuments[] readTheXMLDocs(File file) throws Exception {

		// 使用SAX解析xml文件，初始化documents数组中的基本成员变量值

		InputStream xmlStream = new FileInputStream(file);
		// InputStream input =
		// DocumentsReader.class.getClassLoader().getResourceAsStream("document.xml");

		try {
			// 创建一个解析XML的工厂对象
			SAXParserFactory factory = SAXParserFactory.newInstance();
			// 创建一个解析XML的对象
			SAXParser parser = factory.newSAXParser();

			// 设置解析器的相关特性，http://xml.org/sax/features/namespaces = true
			// 表示开启命名空间特性
			// saxParser.setProperty("http://xml.org/sax/features/namespaces",true);

			// 创建一个解析助手类
			DocumentsReader dr = new DocumentsReader();
			parser.parse(xmlStream, dr);
			xmlStream.close();

			// Remove the empty document in the docs arrayList
			// Iterator<IDocuments> itr = dr.IDocs.iterator();
			int docsLength = dr.IDocs.size();

			for (int i = 0; i < docsLength; i++) {
				// Use the regex to delete the images URL
				// 可直接注释此部分代码，显示image URL
				String content = dr.IDocs.get(i).getContent();
				Pattern pattern = Pattern.compile("IMAGEURL:.*?\n");
				Matcher matcher = pattern.matcher(content);
				while (matcher.find()) {
					String temp = matcher.group(0);
					content = content.replace(temp, "");
				}
				dr.IDocs.get(i).setContent(content);

				if (dr.IDocs.get(i).getTitle().trim().equals("")
						|| dr.IDocs.get(i).getContent().trim().equals("")) {
					dr.IDocs.remove(i);
					i--;
					docsLength--;
				}
			}

			IDocuments[] documents = dr.IDocs.toArray(new Documents[dr.IDocs
					.size()]);

			// 初始化得到每个doc的文本特征向量
			for (int i = 0; i < documents.length; ++i) {

				// Initialize the document's title and content's vector map
				documents[i].init(analyzer);
				documents[i].setId(i + "");
			}

			return documents;
			// parser.parse(xmlStream, this);
			// return this.getIDocuments();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

		}
		return null;
	}

	public List<IDocuments> getIDocuments() {
		return IDocs;
	}

	@Override
	public void startDocument() throws SAXException {

		IDocs = new ArrayList<IDocuments>();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if ("document".equals(qName)) {
			IDoc = new Documents();
		}
		preTag = qName;// 将正在解析的节点名称赋给preTag
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {

		// Parse the relevant XML centent
		if (IDoc != null) {
			if ("url".equals(qName)) {
				IDoc.setURL(temp.trim());
				temp = "";

			} else if ("content".equals(qName)) {
				IDoc.setContent(temp);
				temp = "";
			}

			else if ("title".equals(qName)) {
				IDoc.setTitle(temp.trim());
				temp = "";
			}

			else if ("category".equals(qName)) {
				IDoc.setCategory(temp.trim());
				temp = "";
			}

			else if ("document".equals(qName)) {
				IDocs.add(IDoc);
				IDoc = null;
			}
		}
		preTag = null;
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {

		if (IDoc != null) {
			String content = new String(ch, start, length);

			// if (content != null && !content.trim().equals("")
			// && !content.trim().equals("\n")) {

			if ("url".equals(preTag) || "content".equals(preTag)
					|| "title".equals(preTag) || "category".equals(preTag)) {
				temp += content;
			}
		}
	}

	// public static List<IDocuments> getListFromTheXMLFile(File sampleXMLFile)
	// throws IOException{
	// IDocuments[] sampleDocs = getFromTheXMLFile(sampleXMLFile);
	// //Arrays.asList方法返回的List大小是固定的，不能执行add，remove等操作。
	// List<IDocuments> docsList = new
	// ArrayList<IDocuments>(Arrays.asList(sampleDocs));
	// return docsList;
	// }

	/**
	 * Get documents from the XML files in the specified directory.
	 * 
	 * @throws IOException
	 * */
	public static IDocuments[] getFromXMLFileDirectory(String theXMLFile)
			throws IOException {

		File[] sampleXMLFiles = new File(theXMLFile).listFiles();
		IDocuments[] allCateDocs = null;

		// Put category name into Document's category
		for (int i = 0; i < sampleXMLFiles.length; ++i) {

			IDocuments[] theCateDocs = getFromTheXMLFile(sampleXMLFiles[i]);

			String fileName = sampleXMLFiles[i].getName();
			String categoryName = fileName.substring(0,
					fileName.lastIndexOf("."));

			for (int j = 0; j < theCateDocs.length; ++j) {

				theCateDocs[j].setCategory(categoryName);
			}

			// Add array theCateDocs to array allCateDocs.
			if (allCateDocs == null) {
				allCateDocs = theCateDocs;
			} else {
				IDocuments[] tempDocs = new Documents[allCateDocs.length
						+ theCateDocs.length];
				System.arraycopy(allCateDocs, 0, tempDocs, 0,
						allCateDocs.length);
				System.arraycopy(theCateDocs, 0, tempDocs, allCateDocs.length,
						theCateDocs.length);

				allCateDocs = tempDocs;
			}
		}
		return allCateDocs;
	}

	/**
	 * <p>
	 * CreateDate : 2014-9-12
	 * </p>
	 * <p>
	 * Parse every category's documents XML file.
	 * </p>
	 * 
	 * @param cateFiles
	 *            the sample XML file name array
	 * @return The array of every category's center document.
	 */
	public static IDocuments[] getSampleCenterXML(File[] cateFiles) {
		// Get the initialized center array
		int k = cateFiles.length;

		IDocuments[] beginCenter = new Documents[k];
		for (int i = 0; i < k; i++) {
			IDocuments[] categoryArray = null;
			try {
				categoryArray = DocumentsReader.getFromTheXMLFile(cateFiles[i]);
				beginCenter[i] = new KmeansCluster().center(categoryArray);

				// Do not forget to initize the center document's category.
				String fileName = cateFiles[i].getName();
				String categoryName = fileName.substring(0,
						fileName.lastIndexOf("."));
				beginCenter[i].setCategory(categoryName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return beginCenter;
	}

	/**
	 * <p>
	 * CreateDate : 2014-11-20
	 * </p>
	 * <p>
	 * Parse every category's documents XML file.
	 * </p>
	 * 
	 * @param sampleFilePath
	 *            the sample XML files' directory name.
	 * @return The array of every category's center document.
	 */
	public static IDocuments[] getSampleCenterXML(String sampleFilePath) {
		File[] cateFiles = new File(sampleFilePath).listFiles();
		return getSampleCenterXML(cateFiles);
	}
}
