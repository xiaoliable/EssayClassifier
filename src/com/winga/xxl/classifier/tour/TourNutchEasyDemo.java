package com.winga.xxl.classifier.tour;

import java.io.File;

public class TourNutchEasyDemo {

	public static void main(String[] args) throws Exception {

		String modelPath = "xml" + File.separator + "Tour" + File.separator
				+ "Svm.model";

		String content = "         行程单 1 弘益大学周边商圈 Trickeye特丽爱3D美术馆 首尔love museum Hello Kitty Cafe 弘益大学 新村大学街 汝矣岛公园 63大厦 鹭梁津水产市场 2 狎鸥亭 新沙洞林荫道 梨泰院 明洞 乐天超市 3 明洞 明洞饺子 N首尔塔 回基站"
				+ "第1天 2014-04-10"
				+ "接着上一篇游记，第四天我们是学术游。主要是去了韩国著名的美术学院弘益大学以及以培养优秀杰出女性著称的梨花女子大学。"
				+ "以大学为中心的一带商业圈也非常热闹,很多小店可以慢慢逛。";

		double score = TourScore4Nutch.score(content, modelPath);

		System.out.println(score);
	}
}
