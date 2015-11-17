package com.winga.xxl.classifier.demo;

import java.io.File;

import com.winga.xxl.classifier.data.store.IDocuments;
import com.winga.xxl.classifier.model.parser.KMeansXmlModelParser;

/**
 * <p>CreateDate : 2014-9-15</p>
 * <p>The demo to classify the document with the title and content strings. </p>
 * @author xiaoxiao
 * @version 1.0
 */
public class NutchClusterDemo {

	public static void main(String[] args) throws Exception {

		KMeansXmlModelParser sax = new KMeansXmlModelParser();
		String sportTitle = "恒大本欲上诉亚足联足协劝停 里皮若追罚得不偿失_国内足球-中超_新浪竞技风暴_新浪网";
		String sportContent = "一位熟悉亚足联纪律处罚流程的人士向北京青年报记者透露，一般来说，如当值裁判组或比赛监督不对比赛中发生的红牌行为提交违纪行为报告，那么领红者在自然停赛一场后，不会受到额外追罚。但如果恒大就郜林领红蒙冤提出撤牌申请，那么亚足联就会对比赛进行全面调查，领红的张琳及冲入场内质疑裁判的恒大主帅里皮也必成为被调查对象，他们被追罚的可能性会提高。恒大放弃申诉，实际也是避免受到更多人员损失。";

		String militaryTitle = "印度1艘护卫舰损坏螺旋桨 1年内已出15起事故|印度|海军|军舰_新浪军事";
		String militaryContent = "印度海军发言人D K Sharma上校在7月14日告诉简氏信息集团：“此次事故发生时的天气条件非常恶劣，当时印度海军“Kuthar”号正进入布莱尔港，准备停泊于两个港口码头中的一个。”他说，一个调查委员会正在调查此次事故，很快就会提交调查结果。他补充道，“Kuthar”号的排水量为1350吨，于1990年6月服役。此舰将在东海岸维沙卡帕特南的海军造船厂进行修理。";

		String financeTitle = "指数震荡反弹进入深水区 基金再战成长股|成长股|基金持仓|指数震荡_新浪财经_新浪网";
		String financeContent = "继7月金融数据和经济数据低于预期之后，8月汇丰PMI初值也大幅低于市场预期，创3个月以来新低，而8月新增信贷数据传闻依然不乐观。市场人士分析，当前经济数据不尽如人意，政策宽松的有效性受到质疑，以及接下来的“打新”对资金造成的抽水效应，将使市场再度承压。深圳市一位基金人士认为“这将使投资者对A股后市行情走向保持怀疑。但是，从另一方面来说，这又让投资者对刺激经济政策预期充满了期待。”";
		File file = new File("xml" + File.separator + "lastCenter.xml");
//		IDocuments[] centers = sax.ReadCenterXML(file);
//		System.out.println("\nThe cluster sportDoc end is :"
//				+ sax.docCluster(sportTitle, sportContent, centers));
//		System.out.println("The cluster militaryDoc end is :"
//				+ sax.docCluster(militaryTitle, militaryContent, centers));
//		System.out.println("The cluster financeDoc end is :"
//				+ sax.docCluster(financeTitle, financeContent, centers));

		// System.out.println("The cluster end is :"+ sax.docCluster(sportTitle
		// , sportContent, file));
	}
}