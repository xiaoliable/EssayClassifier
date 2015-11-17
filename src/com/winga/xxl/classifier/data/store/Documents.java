package com.winga.xxl.classifier.data.store;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.store.LockObtainFailedException;

/**
 * <p>CreateDate : 2014-8-25</p>
 * <p>Title : 向量文档类</p>
 * <p>其中存储基于网站title、content内容的向量TreeMap。</p>
 * @author xiaoxiao
 * @version 1.0
 */
public class Documents implements IDocuments {

	private String id = "";

	private String url = "";

	private String title = "";

	private String content = "";
	
	private String category = "";

	private Map<Long, Integer> contentVector = new TreeMap<Long, Integer>();

	private Map<Long, Integer> titleVector = new TreeMap<Long, Integer>();

	public Documents(){}
	
	public void init(Analyzer analyzer) {
		contentVector = vectorize(analyzer, "content", this.content);
		titleVector = vectorize(analyzer, "title", this.title);
	}

	/**
	 * 文本向量化
	 * 
	 * @param analyzer
	 *            - 选择的分词器对象
	 * @param field
	 *            - lucene域名
	 * @param content
	 *            - 文本内容
	 */
	public Map<Long, Integer> vectorize(Analyzer analyzer,
			String field, String content) {

		Map<Long, Integer> map = new TreeMap<Long, Integer>();
		DocWordHashMap wordHash = DocWordHashMap.getInstance();
		TokenStream ts = null;
		try {
			ts = analyzer.tokenStream(field, content);

			// 迭代获取分词结果

			// 重置TokenStream
			ts.reset();

			while (ts.incrementToken()) {

				String word = ts.addAttribute(CharTermAttribute.class)
						.toString();
				// 逐个MurmurHash词元
				long hash = MurmurHash.hash64(word);
				if(!wordHash.isContainKey(hash)){
					wordHash.setWordStringHash(hash, word);
				}
				if (!map.containsKey(hash)) {
					map.put(hash, 1);
				} else {
					map.put(hash, map.get(hash) + 1);
				}
			}

			// 关闭TokenStream
			ts.end(); // Perform end-of-stream operations, e.g. set the final
						// offset.
		} catch (CorruptIndexException e) {
			e.printStackTrace();
			map.clear();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
			map.clear();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			map.clear();
		} finally {
			// 释放TokenStream的所有资源
			if (ts != null) {
				try {
					ts.close();
				} catch (IOException e) {
					e.printStackTrace();
					map.clear();
				}
			}
		}
		return map;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getURL() {
		return this.url;
	}

	public void setURL(String url) {
		this.url = url;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Map<Long, Integer> getContentVector() {
		return this.contentVector;
	}

	public void setContentVector(Map<Long, Integer> ContentVector) {
		this.contentVector = ContentVector;
	}

	public Map<Long, Integer> getTitleVector() {
		return this.titleVector;
	}

	public void setTitleVector(Map<Long, Integer> TitleVector) {
		this.titleVector = TitleVector;
	}
	
	public void setCategory(String category){
		this.category = category;
	}
	
	public String getCategory(){
		return category;
	}

	public void printAnalyzerWords(Analyzer analyzer, String field) {

		// 获取Lucene的TokenStream对象
		TokenStream ts = null;
		try {
			ts = analyzer.tokenStream(field, this.content);
			// 获取词元位置属性
			OffsetAttribute offset = ts.addAttribute(OffsetAttribute.class);
			// 获取词元文本属性
			CharTermAttribute term = ts.addAttribute(CharTermAttribute.class);
			// 获取词元文本属性
			TypeAttribute type = ts.addAttribute(TypeAttribute.class);

			// 重置TokenStream（重置StringReader）
			ts.reset();
			// 迭代获取分词结果
			while (ts.incrementToken()) {
				System.out.println("documents[" + this.id + "]");
				System.out.println(offset.startOffset() + " - "
						+ offset.endOffset() + " : " + term.toString() + " | "
						+ type.type());
			}
			// 关闭TokenStream（关闭StringReader）
			ts.end(); // Perform end-of-stream operations, e.g. set the final
						// offset.

		}  catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// 释放TokenStream的所有资源
			if (ts != null) {
				try {
					ts.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
//	public void readFromDoc(IDocuments document){
//	this.setId(document.getId());
//	this.setContent(document.getContent());
//	this.setContentVector(document.getContentVector());
//	this.setTitle(document.getTitle());
//	this.setTitleVector(document.getTitleVector());
//	this.setURL(document.getURL());
// }
}
