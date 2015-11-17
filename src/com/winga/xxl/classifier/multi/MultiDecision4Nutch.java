package com.winga.xxl.classifier.multi;

import java.io.File;

import com.winga.xxl.classifier.data.store.DocumentsReader;
import com.winga.xxl.classifier.data.store.IDocuments;

public class MultiDecision4Nutch {
	public static void main(String[] args) throws Exception {

		String sportTitle = "恒大本欲上诉亚足联足协劝停 里皮若追罚得不偿失_国内足球-中超_新浪竞技风暴_新浪网";
		String sportContent = "一位熟悉亚足联纪律处罚流程的人士向北京青年报记者透露，一般来说，如当值裁判组或比赛监督不对比赛中发生的红牌行为提交违纪行为报告，那么领红者在自然停赛一场后，不会受到额外追罚。但如果恒大就郜林领红蒙冤提出撤牌申请，那么亚足联就会对比赛进行全面调查，领红的张琳及冲入场内质疑裁判的恒大主帅里皮也必成为被调查对象，他们被追罚的可能性会提高。恒大放弃申诉，实际也是避免受到更多人员损失。";

		String militaryTitle = "我军驱逐舰支队坐镇南海前哨 与外军脸贴脸打招呼|南海|中国海军|舰队";
		String militaryContent = "";

		String travelTitle111 = "瑞士-铁力士雪山, 海的女儿旅游攻略 - 艺龙旅游社区";
		String travelContent111 = "   个性介绍：良好的心态是成功的法宝，调整好心态，学会放大身边的快乐。"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_1392087830WugN.jpg.big.jpg|||NOTITLE;"
 + "我们从琉森出发，车行不久，汽车就开始在盘山公路上崎岖而行，窗外下起了沥沥细雨，窗外小路弯弯,风景如画，山涧中雪松笔直茂盛,开阔地更是绿草茵茵。车子经过一个多小时的行驶，终于抵达了铁力士雪山脚下。"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_1392087833TBpO.jpg.big.jpg|||NOTITLE;"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_13920878368Y3j.jpg.big.jpg|||NOTITLE;"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_1392087839acDv.jpg.big.jpg|||NOTITLE;"
 + "经过了小雨的洗礼，整个铁力士雪山脚下的风景，云雾缭绕，风景如诗如画，犹如置身仙境一般。"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_13920878427cu4.jpg.big.jpg|||NOTITLE;"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_1392087845vClL.jpg.big.jpg|||NOTITLE;"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_1392087848xIV1.jpg.big.jpg|||NOTITLE;"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_1392087851hw0B.jpg.big.jpg|||NOTITLE;"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_1392087855XOCm.jpg.big.jpg|||NOTITLE;"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_1392087858J0zN.jpg.big.jpg|||NOTITLE;"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_1392087861ZSQx.jpg.big.jpg|||NOTITLE;"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_1392087864Msik.jpg.big.jpg|||NOTITLE;"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_1392087830WugN.jpg.big.jpg|||NOTITLE;"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_1392087868SvbV.jpg.big.jpg|||NOTITLE;"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_1392087871mYFV.jpg.big.jpg|||NOTITLE;"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_1392087874fuOM.jpg.big.jpg|||NOTITLE;"
 + "第二级索道是一个长方形的单厢大型缆车，一次可以搭乘40人，速度很快，上升时人的耳膜有收缩和微微的压痛感。飞跃终年不化的冰川，雪地的反光也格外强烈。"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_1392087877U49n.jpg.big.jpg|||NOTITLE;"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_1392087880Hbmz.jpg.big.jpg|||NOTITLE;"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_1392087884XB56.jpg.big.jpg|||NOTITLE;"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_1392087886AzRt.jpg.big.jpg|||NOTITLE;"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_1392087888E8av.jpg.big.jpg|||NOTITLE;"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_1392087891qrJR.jpg.big.jpg|||NOTITLE;"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_1392087894llzg.jpg.big.jpg|||NOTITLE;"
 + "铁力士山顶设有多家餐厅，冰淇淋站，纪念品商店，民族服饰和古典服饰照相馆以及欧洲惟一一家设在山顶的专业名表店。从里面出来就一脚踏进了白雪的世界，虽然是盛夏季节，山顶的温度还是在零度以下，在外时间长了还是挺冷的，户外能见度很差，不足50米。"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_1392087896s1zN.jpg.big.jpg|||NOTITLE;"
 + "估计是天气不好原因，铁力士雪山上“万年冰川洞”的居然关闭了，好遗憾。"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_13920884489uhl.jpg.big.jpg|||NOTITLE;"
 + "冰川飞渡椅，开放式的，天气很恶劣，不敢坐  。"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_1392087906Xhfr.jpg.big.jpg|||NOTITLE;"
 + "坐在下山的缆车上看群峰高耸，云起云落，可以让人生发出无穷的浩叹。"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_1392087909v8zH.jpg.big.jpg|||NOTITLE;"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_1392087912vKGu.jpg.big.jpg|||NOTITLE;"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_1392087915zbUg.jpg.big.jpg|||NOTITLE;"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_1392087919hbdD.jpg.big.jpg|||NOTITLE;"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_1392087922Wgb5.jpg.big.jpg|||NOTITLE;"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_1392087925cFWp.jpg.big.jpg|||NOTITLE;"
 + "人间仙境！"
 + "谢谢来踩。"
 + "寻找，瑞士！"
 + "七天走遍上帝最宠爱的国度——…"
 + "七天走遍上帝最宠爱的国度——…"
 + "【纯净瑞士19】在日内瓦湖畔…"
 + "【纯净瑞士18】琉森湖畔的山…"
 + "【纯净瑞士17】瑞士最古老城…"
 + "乘世界首创旋转缆车 登雪山之…"
 + "【纯净瑞士14】乘着冰川列车…"
 + "【瑞士】萌宠为伴游瑞士，格外…"
 + "瑞士2岁、3岁、4岁的孩子能…"
 + "【瑞士】因特拉肯：混在瑞士人…"
 + "【瑞士】因特拉肯小镇 宛如穿…"
 + "瑞士印象（二）：日内瓦湖的清…"
 + "瑞士之旅 六     美丽的…"
 + "欧洲之行三   瑞士首都伯尔…"
 + "【瑞士】péclard：邂逅…"
 + "古城、秀湖、奇山，瑞士缩影就…"
 + "瑞士治愈系玫瑰小镇weggi…"
 + "莱茵河畔的宝石：中世纪湿壁画…"
 + "";
		
		String travelTitle222 = "湖北大悟--双桥花山, 海的女儿旅游攻略 - 艺龙旅游社区";
		String travelContent222 = "个性介绍：良好的心态是成功的法宝，调整好心态，学会放大身边的快乐。"
 + "IMAGEURL:http://imgnew.trip.elong.com/home/attachment/201405/14/5195944_1400041764Vxuh.jpg.big.jpg|||NOTITLE;"
 + "一次偶然的机会看到了湖北大悟县花山的介绍和照片，很想去一睹真容。从武汉大道——金桥大道——岱黄高速——福银高速（汉十高速）——京珠高速（郑州、北京方向）——大悟出口———（右转）——双桥村 有个\"双桥花山\"的牌子处左转（5公里）——村级道路约一公里即到。全程大约2个小时。"
 + "本次大悟花山行三大误区：1、5月11号（今天）到花山看花，还是来早了点，没有看到漫山遍野的花很遗憾，要5月底和6月初来；2、下大悟高速右转，因为不注意路标很可能一下就跑过了，大家一定要注意“双桥花山”这个牌子左转一个很不起眼的小路上；3、如果你是去透透气的话，那早上出发可以，要是摄影爱好者那就中午出发最好，赶到下午3:00-4:00拍照最好，有光影的效果。"
 + "虽然没有看到漫山遍野的“盛景”，但是山上还是有部分的小黄菊盛开了，也算是不虚此行，漫步在黄花丛中，朵朵金钱菊伴着微风，摇曳多姿，似少女般羞涩，一股淡淡的花香沁入心脾。黄黄的花朵与蓝天白云交相辉映，构筑出一幅幅美丽温馨的画面。此黄花名“旋覆花”，别名“金钱菊”、“金钱花”、“小黄花”，这种植物喜光耐旱，具有顽强的生命力和超常的繁殖力。"
 + "IMAGEURL:http://imgnew.trip.elong.com/home/attachment/201405/14/5195944_1400041782f0aR.jpg.big.jpg|||NOTITLE;"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201405/14/5195944_1400041792TRSu.jpg.big.jpg|||NOTITLE;"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201405/14/5195944_1400041811ZJjS.jpg.big.jpg|||NOTITLE;"
 + "IMAGEURL:http://imgnew.trip.elong.com/home/attachment/201405/14/5195944_14000418254pV3.jpg.big.jpg|||NOTITLE;"
 + "IMAGEURL:http://imgnew.trip.elong.com/home/attachment/201405/14/5195944_1400041847yk2M.jpg.big.jpg|||NOTITLE;"
 + "IMAGEURL:http://imgnew.trip.elong.com/home/attachment/201405/14/5195944_1400041867mbbS.jpg.big.jpg|||NOTITLE;"
 + "IMAGEURL:http://imgnew.trip.elong.com/home/attachment/201405/14/5195944_1400041888Hobm.jpg.big.jpg|||NOTITLE;"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201405/14/5195944_1400041896YNKn.jpg.big.jpg|||NOTITLE;"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201405/14/5195944_1400041917qv1Y.jpg.big.jpg|||NOTITLE;"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201405/14/5195944_1400041932MjmT.jpg.big.jpg|||NOTITLE;"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201405/14/5195944_1400041935tgsX.jpg.big.jpg|||NOTITLE;"
 + "IMAGEURL:http://imgnew.trip.elong.com/home/attachment/201405/14/5195944_14000419400Cho.jpg.big.jpg|||NOTITLE;"
 + "IMAGEURL:http://imgnew.trip.elong.com/home/attachment/201405/14/5195944_1400041961Be4E.jpg.big.jpg|||NOTITLE;"
 + "IMAGEURL:http://imgnew.trip.elong.com/home/attachment/201405/14/5195944_1400041982UWg1.jpg.big.jpg|||NOTITLE;"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201405/14/5195944_1400042003eCwR.jpg.big.jpg|||NOTITLE;"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201405/14/5195944_1400042017JscJ.jpg.big.jpg|||NOTITLE;"
 + "IMAGEURL:http://imgnew.trip.elong.com/home/attachment/201405/14/5195944_1400042026dByz.jpg.big.jpg|||NOTITLE;"
 + "IMAGEURL:http://imgnew.trip.elong.com/home/attachment/201405/14/5195944_1400042047Qkci.jpg.big.jpg|||NOTITLE;"
 + "IMAGEURL:http://imgnew.trip.elong.com/home/attachment/201405/14/5195944_1400042068sLLh.jpg.big.jpg|||NOTITLE;"
 + "IMAGEURL:http://imgnew.trip.elong.com/home/attachment/201405/14/5195944_1400042089ewtC.jpg.big.jpg|||NOTITLE;"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201405/14/5195944_1400042109CKFJ.jpg.big.jpg|||NOTITLE;"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201405/14/5195944_14000421146k41.jpg.big.jpg|||NOTITLE;"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201405/14/5195944_1400042117JTHe.jpg.big.jpg|||NOTITLE;"
 + "IMAGEURL:http://imgnew.trip.elong.com/home/attachment/201405/14/5195944_1400042121znGq.jpg.big.jpg|||NOTITLE;"
 + "【湖北坪坝营】 北纬30度，…"
 + "清明时节：湖北省博（二）楚文…"
 + "【原创】灵秀湖北行之一 我…"
 + "■实拍·湖北襄阳遭遇强对流天…"
 + "【湖北】船进神农架，畅游新三…"
 + "到湖北齐岳山听风去【行摄西部…"
 + "【冰沁于心】带上老婆去湖北：…"
 + "【冰沁于心】带上老婆去湖北：…"
 + "【冰沁于心】带上老婆去湖北：…"
 + "■实拍·湖北襄阳，万名香客抢…"
 + "【冰沁于心】带上老婆去湖北：…"
 + "〖湖北〗襄阳那一口黄酒一口面…"
 + "【湖北】桃花古驿，孝感杨店万…"
 + "【冰沁于心】带上老婆去湖北：…"
 + "〖湖北〗那些留在齿间回味的乡…"
 + "【冰沁于心】带上老婆去湖北：…"
 + "〖湖北郧县〗在那樱桃花盛开醉…"
 + "【湖北郧县•古生物与中原文化…"
 + "【湖北郧县•樱桃花盛开映山村…"
 + "■实拍·湖北襄阳 看大戏闹元…"
 + "■实拍·湖北襄阳再现平流雾奇…";

		String eatTitle = "红酒入口之道-酒文化-中华美食网";
		String eatContent = "　　一瓶佳酿通常是尘封多年的，刚刚打开时会有异味出现，这时就需要“唤醒”这支酒，在将酒倒入精美的醒酒器后稍待十分钟，酒的异味散去，醒酒器一般要求让酒和空气的接触面最大，红酒充分氧化之后，浓郁的香味就流露出来了。"
				+ "　　红酒的那种红色足以撩人心扉，红酒斟酒时以酒杯横置，酒不溢出为基本要求。在光线充足的情况下将红酒杯横置在白纸上，观看红酒的边缘就能判断出酒的年龄。层次分明者多是新酒，颜色均匀的是有点岁数了，如果微微呈棕色，那有可能碰到了一瓶陈年佳酿。"
				+ "　　在酒入口之前，先深深在酒杯里嗅一下，此时已能领会到红酒的幽香，再吞入一口红酒，让红酒在口腔内多停留片刻，舌头上打两个滚，使感官充分体验红酒，最后全部咽下，一股幽香立即萦绕其中。"
				+ "　　一次品酒聚会通常会品尝两三支以上的红酒，以期达到对比的效果。喝酒时应按照新在先陈在后、淡在先浓在后的原则。";

		String cultureTitle = "    古代书画价格：到底是谁说了算？-书画文摘";
		String cultureContent = "    “一张画有没有被名家收藏可能对它价格的影响会在两倍到三倍，这就是我们平时常说的‘流传有序’的重要。”陈湘波解释说，《仿倪云林山水》就是目前市面上唯一由“二王”珍藏过的古书画，王方宇是全世界公认的研究八大山人的专家，他收藏的作品绝大部分都捐给了博物馆，流传到市场上的非常少。他之后另外一位收藏家是王己千，是海外中国书画鉴定的第一人。他们对这个作品的看重势必会影响其他买家。"
				+ "    据记者了解，古画的造假作伪在收藏市场上比较严重，有圈内人向记者透露，真伪比例甚至会在5%：95%左右。“买名家藏品实际上是在‘抄近道’，当然也就要多花钱。因为你是在借助前人的眼力买东西。”陈湘波说，“如果想凭自己的眼力‘捡漏’，可能会花小钱，但是能不能买到好东西就很难说了。”"
				+ "    除了作品本身的价值因素，在中央美术学院教授赵力看来，一幅古书画作品能否在好的时间出现在好的地点，然后创出好的价格，仍有许多偶然因素，宋徽宗《写生珍禽图》今春的拍卖就是一个很好的例证。“尤伦斯夫妇在2002年中国嘉德公司春季拍卖会上，以2530万元买下《写生珍禽图》手卷，时隔7年后，在今年北京保利公司春季拍卖会上以6171.2万元卖出。7年时间赚了3644.2万元，平均年回报率达到20%。显示了尤伦斯夫妇对拍卖市场的敏锐与时机的把握。”赵力分析说。";

		String shopTitle = "酸奶机选购锦囊_惠惠";
		String shopContent = "    Apple（苹果）在发布新一代iPhone 6的同时，也宣布推出Apple Pay进军移动支付的计划。国内第三方支付机构感受到潜在压力，纷纷加快发展步伐。不仅是聊天工具的腾讯微信，今天正式上线二维码“刷卡”功能（微信iOS 5.4.0.16支持），以后消费者可出示二维码供商铺扫码即可完成支付，无需密码和短信二次验证，更加快捷。"
				+ "具体来说，点击“刷卡”功能，它能生成一个动态二维码和条形码，给商铺收银员用带扫码功能的POS机扫一扫，即可从微信账户余额或绑定账户中扣款完成付款。为了提升安全性，二维码和条形码自动更新，而且给出了交易限制，每天交易10次以下，额度300元以内的才可使用（具体条款在此）。也是许刚刚试水，支付微信刷卡的商户数量比较有限，目前包括DQ冰雪皇后、国大药房、天虹、易初莲花、好邻居等9家商铺，相信未来会有更多商户源源不断进入。"
				+ "年初央行曾出于安全考虑叫停了二维码支付，目前看来政策有所放宽。而作为第三方支付的老大，支付宝也没闲着，不久前上线支付宝开放平台，开放二维码、Wi-Fi、卡券等共60多个API接口给互联网、电商、医疗、餐饮等领域的商家和开发者，打造条码付、当面付、NFC等多种形式的O2O商业支付模式。"
				+ "扫码支付不会涉及大额交易，在便利店、超市、药店买东西付款更加快捷，相信能省去不少排队找零的烦恼。"
				+ "什么值得买资讯中心，全景关注各行业的发展风向，集中报道新品发布、业界动态和海淘情报，致力于呈现时效性和价值性俱佳的精选资讯。";

		String financeTitle = "    人民币外汇牌价_新浪外汇_新浪财经";
		String financeContent = "   金道环球投资：非美货币仍在小区间内震荡!"
				+ "新浪声明与提示：此数据系转载自中国各银行网站，表格中部分货币报价为0.000，因中国银行不提供该货币此类报价信息。新浪网登载此数据出于传递更多信息之目的。此汇率表仅供参考，以中国银行各分行实际交易汇率为准，不构成投资建议。投资者据此买卖，风险自担。";

		String testTitle = "瑞士 ";
		String testContent = "/n        IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_1392087830WugN.jpg.big.jpg|||NOTITLE;\n"
 + "我们从琉森出发，车行不久，汽车就开始在盘山公路上崎岖而行，窗外下起了沥沥细雨，窗外小路弯弯,风景如画，山涧中雪松笔直茂盛,开阔地更是绿草茵茵。车子经过一个多小时的行驶，终于抵达了铁力士雪山脚下。\n"
 + " \n"
 + "铁力士山海拔3200米，位于瑞士中部，与国际大都市洛桑相毗邻，地处琉森的英格堡区，是阿尔卑斯山脉中的一座名山，亦是瑞士中部最高的旅游景点，以终年不融的冰川和冰川裂缝闻名世界。\n"
 + "近年来，英格堡和铁力士山的名声从欧洲传到了亚洲，每年来这里旅游观光的亚洲客人是络绎不绝。\n"
 + " \n"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_1392087833TBpO.jpg.big.jpg|||NOTITLE;\n"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_13920878368Y3j.jpg.big.jpg|||NOTITLE;\n"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_1392087839acDv.jpg.big.jpg|||NOTITLE;\n"
 + "经过了小雨的洗礼，整个铁力士雪山脚下的风景，云雾缭绕，风景如诗如画，犹如置身仙境一般。\n"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_13920878427cu4.jpg.big.jpg|||NOTITLE;\n"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_1392087845vClL.jpg.big.jpg|||NOTITLE;\n"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_1392087848xIV1.jpg.big.jpg|||NOTITLE;\n"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_1392087851hw0B.jpg.big.jpg|||NOTITLE;\n"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_1392087855XOCm.jpg.big.jpg|||NOTITLE;\n"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_1392087858J0zN.jpg.big.jpg|||NOTITLE;\n"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_1392087861ZSQx.jpg.big.jpg|||NOTITLE;\n"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_1392087864Msik.jpg.big.jpg|||NOTITLE;\n"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_1392087830WugN.jpg.big.jpg|||NOTITLE;\n"
 + "铁力士雪山高空缆车应该属于世界首创，它的设计十分独特，要想到达海拔3200米的最高峰，游客们必须换乘3条不同的缆车线路，中间两次中转,分段上行。 从踏上吊车的那一刻起，便开始了一段新鲜刺激的“空中旅行”，遨游在雄伟的阿尔卑斯山区上空。 \n"
 + "    \n"
 + "第一段基本和你能想象到的缆车并无二致。我们上山的时候，游客还很少，4个人可以享受一个索道吊箱。这一段是从山谷通向海拔1796米的高山湖泊——特吕布湖。雪山就倒影在湛蓝的湖水中。坐在索道缆车中，就好像行走在山路上，山野往后退去，路不断延伸，奇妙景象尽收眼底。 \n"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_1392087868SvbV.jpg.big.jpg|||NOTITLE;\n"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_1392087871mYFV.jpg.big.jpg|||NOTITLE;\n"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_1392087874fuOM.jpg.big.jpg|||NOTITLE;\n"
 + "第二级索道是一个长方形的单厢大型缆车，一次可以搭乘40人，速度很快，上升时人的耳膜有收缩和微微的压痛感。飞跃终年不化的冰川，雪地的反光也格外强烈。\n"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_1392087877U49n.jpg.big.jpg|||NOTITLE;\n"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_1392087880Hbmz.jpg.big.jpg|||NOTITLE;\n"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_1392087884XB56.jpg.big.jpg|||NOTITLE;\n"
 + "开缆车的瑞士美女\n"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_1392087886AzRt.jpg.big.jpg|||NOTITLE;\n"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_1392087888E8av.jpg.big.jpg|||NOTITLE;\n"
 + "第三级缆车是这里独有的，它是专门为这座雪山景区设计制造的360度旋转观光缆车，可承载80位乘客，在大约15分钟的乘行中正好让每一位游人无须转身 就能看到铁力士山顶的全貌。这个旋转缆车在1992年12月正式通车，路线横跨了壮丽的铁力士冰川，在缆车里我们饱览着白雪皑皑的群山，感慨着此行的不 虚。 \n"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_1392087891qrJR.jpg.big.jpg|||NOTITLE;\n"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_1392087894llzg.jpg.big.jpg|||NOTITLE;\n"
 + "铁力士山顶设有多家餐厅，冰淇淋站，纪念品商店，民族服饰和古典服饰照相馆以及欧洲惟一一家设在山顶的专业名表店。从里面出来就一脚踏进了白雪的世界，虽然是盛夏季节，山顶的温度还是在零度以下，在外时间长了还是挺冷的，户外能见度很差，不足50米。\n"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_1392087896s1zN.jpg.big.jpg|||NOTITLE;\n"
 + "估计是天气不好原因，铁力士雪山上“万年冰川洞”的居然关闭了，好遗憾。\n"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_13920884489uhl.jpg.big.jpg|||NOTITLE;\n"
 + "冰川飞渡椅，开放式的，天气很恶劣，不敢坐  。\n"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_1392087906Xhfr.jpg.big.jpg|||NOTITLE;\n"
 + "坐在下山的缆车上看群峰高耸，云起云落，可以让人生发出无穷的浩叹。\n"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_1392087909v8zH.jpg.big.jpg|||NOTITLE;\n"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_1392087912vKGu.jpg.big.jpg|||NOTITLE;\n"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_1392087915zbUg.jpg.big.jpg|||NOTITLE;\n"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_1392087919hbdD.jpg.big.jpg|||NOTITLE;\n"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_1392087922Wgb5.jpg.big.jpg|||NOTITLE;\n"
 + "IMAGEURL:http://img.trip.elong.com/home/attachment/201402/11/5195944_1392087925cFWp.jpg.big.jpg|||NOTITLE;";
		
		String kmeansModelPath = "xml" + File.separator + "model"
				+ File.separator + "kmeans_cate.model";
		String nbModelPath = "xml" + File.separator + "model" + File.separator
				+ "nb_cate.model";
		String svmModelPath = "xml" + File.separator + "model" + File.separator
				+ "svm_cate.model";

		String predict = CategoryPredict.multiDicision4Nutch(testTitle,
				 testContent, kmeansModelPath, nbModelPath, svmModelPath);

		String predict22 = CategoryPredict.multiDicision4Nutch(travelTitle111,
				 travelContent111, kmeansModelPath, nbModelPath, svmModelPath);
		
		System.out.println("Prediction is : " + predict + "Next prediction is : " + predict22);

//		 String testPath = "xml" + File.separator + "test";
//		
//		 test(testPath, kmeansModelPath, nbModelPath, svmModelPath);
	}

	public static void test(String testPath, String kmeansModelPath,
			String nbModelPath, String svmModelPath) throws Exception {

		IDocuments[] sampleDocs = DocumentsReader
				.getFromXMLFileDirectory(testPath);

		for (int i = 0; i < sampleDocs.length; i++) {
//			if (sampleDocs[i].getTitle().trim().equalsIgnoreCase(aTitle)) {
//				System.out.println("xxz title is equals!!!!!!");
//			}
//			
//			if (sampleDocs[i].getContent().trim().equalsIgnoreCase(aContent)) {
//				System.out.println("xxz Content is equals!!!!!!");
//			}
			
			String predict = CategoryPredict.multiDicision4Nutch(
					sampleDocs[i].getTitle().trim(), sampleDocs[i].getContent().trim(),
					kmeansModelPath, nbModelPath, svmModelPath);
			System.out.println("The " + i + "-th prediction is : " + predict);
		}
	}
}
