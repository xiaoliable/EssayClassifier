package com.winga.xxl.classifier.demo;

import java.io.File;
import java.io.IOException;
import com.winga.xxl.classifier.util.NbClassifier;

public class NaiveBeyesDemo {
	public static void main(String[] args) {
		String sportTitle = "恒大本欲上诉亚足联足协劝停 里皮若追罚得不偿失_国内足球-中超_新浪竞技风暴_新浪网";
		String sportContent = "一位熟悉亚足联纪律处罚流程的人士向北京青年报记者透露，一般来说，如当值裁判组或比赛监督不对比赛中发生的红牌行为提交违纪行为报告，那么领红者在自然停赛一场后，不会受到额外追罚。但如果恒大就郜林领红蒙冤提出撤牌申请，那么亚足联就会对比赛进行全面调查，领红的张琳及冲入场内质疑裁判的恒大主帅里皮也必成为被调查对象，他们被追罚的可能性会提高。恒大放弃申诉，实际也是避免受到更多人员损失。";

		String militaryTitle = "印度1艘护卫舰损坏螺旋桨 1年内已出15起事故|印度|海军|军舰_新浪军事";
		String militaryContent = "印度海军发言人D K Sharma上校在7月14日告诉简氏信息集团：“此次事故发生时的天气条件非常恶劣，当时印度海军“Kuthar”号正进入布莱尔港，准备停泊于两个港口码头中的一个。”他说，一个调查委员会正在调查此次事故，很快就会提交调查结果。他补充道，“Kuthar”号的排水量为1350吨，于1990年6月服役。此舰将在东海岸维沙卡帕特南的海军造船厂进行修理。";

		String financeTitle = "指数震荡反弹进入深水区 基金再战成长股|成长股|基金持仓|指数震荡_新浪财经_新浪网";
		String financeContent = "继7月金融数据和经济数据低于预期之后，8月汇丰PMI初值也大幅低于市场预期，创3个月以来新低，而8月新增信贷数据传闻依然不乐观。市场人士分析，当前经济数据不尽如人意，政策宽松的有效性受到质疑，以及接下来的“打新”对资金造成的抽水效应，将使市场再度承压。深圳市一位基金人士认为“这将使投资者对A股后市行情走向保持怀疑。但是，从另一方面来说，这又让投资者对刺激经济政策预期充满了期待。”";

		String dietTitle = "粗茶淡饭的真正含义-饮食典故-中华美食网";
		String dietContent = "您好，欢迎来到中华美食网 ！人们常说，“粗茶淡饭延年益寿”，那么粗茶淡饭到底是什么？营养学家研究发现，它并非大多数人所指的各种粗粮和素食。正确的理解应是以植物性食物为主，注意粮豆混食、米面混食，并辅以各种动物性食品，常喝粗茶。粗茶”是指较粗老的茶叶，与新茶相对。尽管粗茶又苦又涩，但含有的茶多酚、茶丹宁等物质，却对身体很有益处。因为，茶多酚是一种天然抗氧化剂，能抑制自由基在人体内造成的伤害，有抗衰老作用。它还能阻断亚硝胺等致癌物对身体的损害。茶丹宁则能降低血脂，防止血管硬化，保持血管畅通，维护心、脑血管的正常功能。茶多糖能缓解和减轻糖尿病症状，具有降血脂、降血压等作用。因此从健康角度来看，粗茶更适合老年人饮用。　　很多人把“淡饭”和粗粮、素食等同起来。其实，“淡饭”是指富含蛋白质的天然食物。它既包括丰富的谷类食物和蔬菜，也包括脂肪含量低的鸡肉、鸭肉、鱼肉、牛肉等。　　“淡饭”还有另外一层含义，就是饮食不能太咸。医学研究表明，饮食过咸容易引发骨质疏松、高血压，长期饮食过咸还可致中风和心脏病。喜欢粗茶淡饭的真正含义的朋友还浏览过以下文章 ：";

		String tempTitle = "　　穆雷目前正在为年终总决赛席位而战，而如果没能进入前八只能作为替补，那么英国人会参赛吗，对此穆雷表示：“老实说，我还没考虑过这事。现在我也说不准。从我住的地方到O2(比赛所在场馆)大概也就是1小时多一点的路程，所以我要参赛的话肯定比那些从欧洲大陆或美国过来的球员容易。但眼下我根本还没想过这个。”";
		String tempContent = "更让大家感到吃惊的是，这些蒙古的队员在代表国家队出战的比赛中从开始的每一分钟拼到最后，他们竟然一分钱工资也拿不到。“我们打国家队不会得到任何薪水酬劳，只有以前我效力过的一个俱乐部会在联赛休赛期时会给我一些薪水作为补助，但这也只有一个俱乐部，其他的俱乐部是不给队员支付任何薪水的。博尔没有任何抱怨的意思，他只是想表明自己打篮球不是为了任何酬劳。据蒙古代表团的官员向记者介绍，在蒙古，很多家长都非常支持年轻的孩子打篮球，很多学校都会组织篮球队，高中和大学学校之间会进行比赛，虽然整个国家的人口也只有294万，但在这里打业余篮球的人却超过10000人。“我们那里大部分人都喜欢看NBA，到了总决赛的时候，全国大部分年轻人都会守在家里看比赛。”来自蒙古电视台的记者说。如果想为蒙古男篮的撅起找一个真正的原因，我想，这应该是——热爱！";
		
		String sampleFile = "xml" + File.separator + "categoryResult" + File.separator + "workBase";
		
		//Set the number of feature to 500
		NbClassifier probClassifier = new NbClassifier(sampleFile, 500);

		System.out.println("\nThe cluster sportDoc end is :"
				+ probClassifier.applyMultiNormalNBwithDetail(sportTitle, sportContent) + "\n\n");
		System.out.println("\nThe cluster sportDoc end is :"
				+ probClassifier.applyMultiNormalNBwithDetail(militaryContent, militaryTitle) + "\n\n");
		System.out.println("\nThe cluster sportDoc end is :"
				+ probClassifier.applyMultiNormalNBwithDetail(financeContent, financeTitle) + "\n\n");
		System.out.println("\nThe cluster sportDoc end is :"
				+ probClassifier.applyMultiNormalNBwithDetail(dietContent, dietTitle) + "\n\n");
		System.out.println("\nThe cluster sportDoc end is :"
				+ probClassifier.applyMultiNormalNBwithDetail(tempContent, tempTitle) + "\n\n");
		
//		Documents doc = new Documents();
//		doc.setContent(sportContent);
//		doc.setTitle(sportTitle);
//		doc.printAnalyzerWords(new IKAnalyzer(), "field");
		
		File probFile = new File("xml" + File.separator + "categoryResult" + File.separator + "NBProb.xml");
		try {
			probClassifier.outputWordProb(probFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//{文化=0.1456141898811004, 财经=0.19821022263932003, 体育=0.12338822447348087, 饮食=0.06733723159355423, 出行=0.1174451155441121, 军事=0.09917944028683495, 购物=0.24882557558159743}
	//3740130.0  {文化=544616.0, 财经=741332.0, 体育=461488.0, 饮食=251850.0, 出行=439260.0, 军事=370944.0, 购物=930640.0}
}
/*
 * // applyMultiNormalNBwithDetail:::
//	Category <体育>'s weight is :	820.1923733204266
//	Category <军事>'s weight is :	703.8722820457452
//	Category <出行>'s weight is :	680.8375250959065
//	Category <文化>'s weight is :	662.9969517825142
//	Category <财经>'s weight is :	670.2545420339097
//	Category <购物>'s weight is :	706.148873194499
//	Category <饮食>'s weight is :	493.67328543453385
//
//	The cluster sportDoc end is :体育
//
//
//	Category <体育>'s weight is :	600.057238068358
//	Category <军事>'s weight is :	684.9906947538168
//	Category <出行>'s weight is :	585.4379877275941
//	Category <文化>'s weight is :	600.8790099873057
//	Category <财经>'s weight is :	609.3247337290312
//	Category <购物>'s weight is :	628.7201114220067
//	Category <饮食>'s weight is :	340.8413844768433
//
//	The cluster sportDoc end is :军事
//
//
//	Category <体育>'s weight is :	641.4538766421633
//	Category <军事>'s weight is :	699.1069678736968
//	Category <出行>'s weight is :	623.8358200127788
//	Category <文化>'s weight is :	671.5881623594247
//	Category <财经>'s weight is :	747.07479264539
//	Category <购物>'s weight is :	724.4193722186039
//	Category <饮食>'s weight is :	443.529315998595
//
//	The cluster sportDoc end is :财经
//
//
//	Category <体育>'s weight is :	934.2853598519914
//	Category <军事>'s weight is :	962.0744752872752
//	Category <出行>'s weight is :	1105.1361017484453
//	Category <文化>'s weight is :	1039.6694408120763
//	Category <财经>'s weight is :	1055.2751520478123
//	Category <购物>'s weight is :	1272.8988790507271
//	Category <饮食>'s weight is :	1317.0493126517092
//
//	The cluster sportDoc end is :饮食
//
//
//	Category <体育>'s weight is :	909.4213791393868
//	Category <军事>'s weight is :	952.1514016739437
//	Category <出行>'s weight is :	947.7044113872175
//	Category <文化>'s weight is :	921.0615273274992
//	Category <财经>'s weight is :	1158.9739578355707
//	Category <购物>'s weight is :	969.8578832155895
//	Category <饮食>'s weight is :	689.9352904595406
//
//	The cluster sportDoc end is :财经
	
	//featureNum is 1000
//	Category <文化>'s weight is :	88.4366670004918
//	Category <财经>'s weight is :	106.08116679798563
//	Category <体育>'s weight is :	175.348920931331
//	Category <饮食>'s weight is :	33.85536024318645
//	Category <出行>'s weight is :	20.605299644376302
//	Category <军事>'s weight is :	24.611299408570893
//	Category <购物>'s weight is :	130.2085869090107
//
//	The cluster sportDoc end is :体育
//
//
//	Category <文化>'s weight is :	81.61023169151801
//	Category <财经>'s weight is :	139.92777516174402
//	Category <体育>'s weight is :	45.66425866645504
//	Category <饮食>'s weight is :	9.456422533264089
//	Category <出行>'s weight is :	69.28114520900141
//	Category <军事>'s weight is :	150.7925635114807
//	Category <购物>'s weight is :	100.26786567208289
//
//	The cluster sportDoc end is :军事
//
//
//	Category <文化>'s weight is :	130.0766447676882
//	Category <财经>'s weight is :	269.5776613119494
//	Category <体育>'s weight is :	66.60886748120538
//	Category <饮食>'s weight is :	45.044944462308365
//	Category <出行>'s weight is :	54.49300514355995
//	Category <军事>'s weight is :	80.55653193454955
//	Category <购物>'s weight is :	136.57104691503585
//
//	The cluster sportDoc end is :财经
//
//
//	Category <文化>'s weight is :	63.323299540336436
//	Category <财经>'s weight is :	97.56463589027526
//	Category <体育>'s weight is :	38.11278716333098
//	Category <饮食>'s weight is :	482.0028753864432
//	Category <出行>'s weight is :	37.63754037301018
//	Category <军事>'s weight is :	54.51947545336152
//	Category <购物>'s weight is :	101.84069217277775
//
//	The cluster sportDoc end is :饮食
//
//
//	Category <文化>'s weight is :	131.1149319006654
//	Category <财经>'s weight is :	204.4553251937281
//	Category <体育>'s weight is :	180.02337877624487
//	Category <饮食>'s weight is :	40.15890177734599
//	Category <出行>'s weight is :	119.96215345472058
//	Category <军事>'s weight is :	128.41553937074968
//	Category <购物>'s weight is :	262.63698755409894
//
//	The cluster sportDoc end is :购物

//	feature  500
//	Category <文化>'s weight is :	46.24608057014378
//	Category <财经>'s weight is :	44.49448428301936
//	Category <体育>'s weight is :	143.76852200923736
//	Category <饮食>'s weight is :	9.462701988748933
//	Category <出行>'s weight is :	2.2760547942439486
//	Category <军事>'s weight is :	8.680447144957405
//	Category <购物>'s weight is :	53.042333435267
//
//	The cluster sportDoc end is :体育
//
//
//	Category <文化>'s weight is :	46.473004434572346
//	Category <财经>'s weight is :	44.9192566796986
//	Category <体育>'s weight is :	28.649149527478688
//	Category <饮食>'s weight is :	9.456422533264089
//	Category <出行>'s weight is :	25.468632101834572
//	Category <军事>'s weight is :	89.60867548993258
//	Category <购物>'s weight is :	27.773093507332227
//
//	The cluster sportDoc end is :军事
//
//
//	Category <文化>'s weight is :	51.17187247435781
//	Category <财经>'s weight is :	158.37274574707098
//	Category <体育>'s weight is :	18.38638057473338
//	Category <饮食>'s weight is :	17.133763150105764
//	Category <出行>'s weight is :	31.859519715446275
//	Category <军事>'s weight is :	22.710548350247002
//	Category <购物>'s weight is :	74.1251473516631
//
//	The cluster sportDoc end is :财经
//
//
//	Category <文化>'s weight is :	18.468218554152553
//	Category <财经>'s weight is :	39.6425262742996
//	Category <体育>'s weight is :	14.651688479533277
//	Category <饮食>'s weight is :	301.0725023960368
//	Category <出行>'s weight is :	19.051198987884817
//	Category <军事>'s weight is :	28.076199894760055
//	Category <购物>'s weight is :	86.4353286097144
//
//	The cluster sportDoc end is :饮食
//
//
//	Category <文化>'s weight is :	42.318405042548235
//	Category <财经>'s weight is :	101.16008362090224
//	Category <体育>'s weight is :	140.6148849203618
//	Category <饮食>'s weight is :	22.64578974134704
//	Category <出行>'s weight is :	83.50122925393623
//	Category <军事>'s weight is :	82.5142161795346
//	Category <购物>'s weight is :	174.22958469206554
//
//	The cluster sportDoc end is :购物

//	featureWordNum  500  naiveBeyesClassifier  with  * pendingDoc.get(wordHash)
//	Category <文化>'s weight is :	66.05833938733676
//	Category <财经>'s weight is :	78.33316369812637
//	Category <体育>'s weight is :	176.48205382174646
//	Category <饮食>'s weight is :	9.462701988748933
//	Category <出行>'s weight is :	2.2760547942439486
//	Category <军事>'s weight is :	8.680447144957405
//	Category <购物>'s weight is :	80.83278805160795
//
//	The cluster sportDoc end is :体育
//
//
//	Category <文化>'s weight is :	46.473004434572346
//	Category <财经>'s weight is :	52.53131101103778
//	Category <体育>'s weight is :	28.649149527478688
//	Category <饮食>'s weight is :	9.456422533264089
//	Category <出行>'s weight is :	31.66357980621681
//	Category <军事>'s weight is :	96.22191927511682
//	Category <购物>'s weight is :	27.773093507332227
//
//	The cluster sportDoc end is :军事
//
//
//	Category <文化>'s weight is :	101.91417889680244
//	Category <财经>'s weight is :	237.96465584768106
//	Category <体育>'s weight is :	28.42180205560272
//	Category <饮食>'s weight is :	17.133763150105764
//	Category <出行>'s weight is :	85.20139966174774
//	Category <军事>'s weight is :	31.362042519970483
//	Category <购物>'s weight is :	112.75994921357311
//
//	The cluster sportDoc end is :财经
//
//
//	Category <文化>'s weight is :	18.468218554152553
//	Category <财经>'s weight is :	62.93647722048056
//	Category <体育>'s weight is :	20.900152541711623
//	Category <饮食>'s weight is :	364.6835983895794
//	Category <出行>'s weight is :	19.051198987884817
//	Category <军事>'s weight is :	53.69380551417191
//	Category <购物>'s weight is :	94.52725446753207
//
//	The cluster sportDoc end is :饮食
//
//
//	Category <文化>'s weight is :	42.318405042548235
//	Category <财经>'s weight is :	114.9165804906909
//	Category <体育>'s weight is :	186.9653870342548
//	Category <饮食>'s weight is :	22.64578974134704
//	Category <出行>'s weight is :	89.62786412766019
//	Category <军事>'s weight is :	115.20490128770928
//	Category <购物>'s weight is :	181.0710074693553
//
//	The cluster sportDoc end is :体育
//
//


Nov 11, 2014 6:59:00 PM org.elasticsearch.ik-analyzer
INFO: [Dict Loading]ik\custom\mydict.dic
Nov 11, 2014 6:59:00 PM org.elasticsearch.ik-analyzer
INFO: [Dict Loading]ik\custom\single_word_low_freq.dic
Nov 11, 2014 6:59:00 PM org.elasticsearch.ik-analyzer
INFO: [Dict Loading]ik\custom\sougou.dic
Nov 11, 2014 6:59:02 PM org.elasticsearch.ik-analyzer
INFO: [Dict Loading]ik\custom\ext_stopword.dic
Category <文化>'s weight is :	66.05833938733676
Category <财经>'s weight is :	78.33316369812637
Category <体育>'s weight is :	176.48205382174646
Category <饮食>'s weight is :	9.462701988748933
Category <出行>'s weight is :	2.2760547942439486
Category <军事>'s weight is :	8.680447144957405
Category <购物>'s weight is :	80.83278805160795

The cluster sportDoc end is :体育


Category <文化>'s weight is :	46.473004434572346
Category <财经>'s weight is :	52.53131101103778
Category <体育>'s weight is :	28.649149527478688
Category <饮食>'s weight is :	9.456422533264089
Category <出行>'s weight is :	31.66357980621681
Category <军事>'s weight is :	96.22191927511682
Category <购物>'s weight is :	27.773093507332227

The cluster sportDoc end is :军事


Category <文化>'s weight is :	101.91417889680244
Category <财经>'s weight is :	237.96465584768106
Category <体育>'s weight is :	28.42180205560272
Category <饮食>'s weight is :	17.133763150105764
Category <出行>'s weight is :	85.20139966174774
Category <军事>'s weight is :	31.362042519970483
Category <购物>'s weight is :	112.75994921357311

The cluster sportDoc end is :财经


Category <文化>'s weight is :	18.468218554152553
Category <财经>'s weight is :	62.93647722048056
Category <体育>'s weight is :	20.900152541711623
Category <饮食>'s weight is :	364.6835983895794
Category <出行>'s weight is :	19.051198987884817
Category <军事>'s weight is :	53.69380551417191
Category <购物>'s weight is :	94.52725446753207

The cluster sportDoc end is :饮食


Category <文化>'s weight is :	42.318405042548235
Category <财经>'s weight is :	114.9165804906909
Category <体育>'s weight is :	186.9653870342548
Category <饮食>'s weight is :	22.64578974134704
Category <出行>'s weight is :	89.62786412766019
Category <军事>'s weight is :	115.20490128770928
Category <购物>'s weight is :	181.0710074693553

The cluster sportDoc end is :体育

 * 
 * */
