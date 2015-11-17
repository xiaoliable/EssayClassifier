package com.winga.xxl.classifier.data.exception;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.lucene.analysis.Analyzer;
import org.wltea.analyzer.lucene.IKAnalyzer;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.winga.xxl.classifier.data.store.DocWordHashMap;
import com.winga.xxl.classifier.data.store.Documents;
import com.winga.xxl.classifier.data.store.IDocuments;
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
public class SaxParseService extends DefaultHandler {
	private List<IDocuments> IDocs = null;
	private IDocuments IDoc = null;
	private Analyzer analyzer = new IKAnalyzer();

	private String temp = "";// Record the element's content
	private String preTag = null;// Record the element's name

	// @SuppressWarnings("resource")
	public SaxParseService() {
		IDocs = new ArrayList<IDocuments>();
	}

	/**
	 * <p>
	 * CreateDate : 2014-9-12
	 * </p>
	 * <p>
	 * Parse every category's documents XML file.
	 * </p>
	 * 
	 * @param sampleFiles
	 *            the sample XML file name array
	 */
	public IDocuments[] ReadSampleCenterXML(File[] sampleFiles) {
		// Get the initialized center array
		int k = sampleFiles.length;

		IDocuments[] beginCenter = new Documents[k];
		for (int i = 0; i < k; i++) {
			SaxParseService sax = new SaxParseService();
			IDocuments[] categoryArray = null;
			try {
				categoryArray = sax.ReadTheXML(sampleFiles[i]);
				beginCenter[i] = new KmeansCluster().center(categoryArray);

				// Put category name into document's title
				String fileName = sampleFiles[i].getName();
				String categoryName = fileName.substring(0,
						fileName.lastIndexOf("."));
				beginCenter[i].setTitle(categoryName);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return beginCenter;
	}

	/**
	 * @param trainFiles
	 *            the sample XML file name array
	 * */
	public IDocuments[] ReadSVMSampleArrayXML(File[] trainFiles,
			Map<String, Double> numCaregoryMap) {
		// Get the initialized center array
		int k = trainFiles.length;

		IDocuments[] documents = null;
		for (int i = 0; i < k; i++) {
			SaxParseService sax = new SaxParseService();
			IDocuments[] categoryArray = null;

			categoryArray = sax.ReadTheSVMXML(trainFiles[i]);

			// Put category name into document's title
			String fileName = trainFiles[i].getName();
			String categoryName = fileName.substring(0,
					fileName.lastIndexOf("."));
			numCaregoryMap.put(categoryName, (double) i);
			if (documents == null) {
				documents = categoryArray;
			} else {
				IDocuments[] tempDocuments = new Documents[categoryArray.length
						+ documents.length];
				System.arraycopy(categoryArray, 0, tempDocuments, 0,
						categoryArray.length);
				System.arraycopy(documents, 0, tempDocuments,
						categoryArray.length, documents.length);
				documents = tempDocuments;
			}
		}
		return documents;
	}

	/**
	 * <p>
	 * CreateDate : 2014-10-28
	 * </p>
	 * <p>
	 * Set the category name to every document's title.
	 * </p>
	 * <p>
	 * Use the titleVector to calculate subsequently.
	 * </p>
	 */
	public IDocuments[] ReadTheSVMXML(File testFile) {
		SaxParseService sax = new SaxParseService();
		IDocuments[] categoryDocs = null;
		try {
			IDocuments[] docsArray = sax.ReadTheXML(testFile);

			// Put category name into CategoryDocument's category
			String fileName = testFile.getName();
			String categoryName = fileName.substring(0,
					fileName.lastIndexOf("."));
			Documents[] tempDocuments = new Documents[docsArray.length];

			for (int i = 0; i < docsArray.length; ++i) {
				// Use the contentVector to calculate subsequently.
				Map<Long, Integer> temp = new TreeMap<Long, Integer>();
				temp.putAll(docsArray[i].getTitleVector());
				temp.putAll(docsArray[i].getContentVector());
				docsArray[i].setContentVector(temp);
				
//				tempDocuments[i] = new Documents();
//				tempDocuments[i].readFromDoc(docsArray[i]);
				docsArray[i].setCategory(categoryName);
			}
			categoryDocs = tempDocuments;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return categoryDocs;
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
			bout.write("<category>\r\n" + documents[i].getCategory() + "</category>\r\n");
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
	public IDocuments[] ReadAllXML(File[] files) throws Exception {

		IDocuments[] docs = null;

		int i = 0;
		if (files.length < 1) {
			return null;
		} else {
			docs = new SaxParseService().ReadTheXML(files[i++]);
			while (i < files.length) {
				String fileName = files[i].getName();
				String fileSuffix = fileName.substring(fileName
						.lastIndexOf(".") + 1);
				// "==" Reference is equal.
				if (fileSuffix.equals("xml")) {
					SaxParseService sax = new SaxParseService();
					IDocuments[] thisDocs = sax.ReadTheXML(files[i]);
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
	public IDocuments[] ReadTheXML(File file) throws Exception {

		// 使用SAX解析xml文件，初始化documents数组中的基本成员变量值

		InputStream xmlStream = new FileInputStream(file);
		// InputStream input =
		// SaxParseService.class.getClassLoader().getResourceAsStream("document.xml");

		try {
			// 创建一个解析XML的工厂对象
			SAXParserFactory factory = SAXParserFactory.newInstance();
			// 创建一个解析XML的对象
			SAXParser parser = factory.newSAXParser();

			// 设置解析器的相关特性，http://xml.org/sax/features/namespaces = true
			// 表示开启命名空间特性
			// saxParser.setProperty("http://xml.org/sax/features/namespaces",true);

			// 创建一个解析助手类
			SaxParseService handler = new SaxParseService();
			parser.parse(xmlStream, handler);
			xmlStream.close();

			// Remove the empty document in the docs arrayList
			// Iterator<IDocuments> itr = handler.IDocs.iterator();
			int docsLength = handler.IDocs.size();

			for (int i = 0; i < docsLength; i++) {
				// Use the regex to delete the images URL
				// 可直接注释此部分代码，显示image URL
				String content = handler.IDocs.get(i).getContent();
				Pattern pattern = Pattern.compile("IMAGEURL:.*?\n");
				Matcher matcher = pattern.matcher(content);
				while (matcher.find()) {
					String temp = matcher.group(0);
					content = content.replace(temp, "");
				}
				handler.IDocs.get(i).setContent(content);

				if (handler.IDocs.get(i).getTitle().trim().equals("")
						|| handler.IDocs.get(i).getContent().trim().equals("")) {
					handler.IDocs.remove(i);
					i--;
					docsLength--;
				}
			}

			IDocuments[] documents = handler.IDocs
					.toArray(new Documents[handler.IDocs.size()]);

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
		if (IDoc != null && "url".equals(qName)) {
			IDoc.setURL(temp);
			temp = "";

		} else if (IDoc != null && "content".equals(qName)) {
			IDoc.setContent(temp);
			temp = "";
		}

		else if (IDoc != null && "title".equals(qName)) {
			IDoc.setTitle(temp);
			temp = "";
		}
		// else if (IDoc != null && "category".equals(qName)) {
		// IDoc.setCategory(temp);
		// temp = "";
		// }

		else if (IDoc != null && "document".equals(qName)) {
			IDocs.add(IDoc);
			IDoc = null;
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
}