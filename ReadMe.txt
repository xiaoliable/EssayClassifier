Author：lixx13
WriteTime：2014-9-22

标题：此工程主要基于ES-IK分词的KMeans聚类算法实现了网页在线分类。

简介：KMeans是思想简单有效的机器学习无监督自动聚类算法。
	考虑到精准性，每个类别我们选取了比较准确的样本documents XML文件（路径为：工程根目录\xml\CategorySample），来计算这K个类别的初始center documents以待接下来的聚类主过程。
	然后基于继续上方的center documents来 聚类计算 从准备好的大量待聚类的pending documents XML文件（路径为：工程根目录\xml\PendingDocs）解析到的documents初始数据，经过KMeans聚类计算后得到groups已分好类的二维数组。
	最终基于分类结果groups数组，可以计算到每个类最终的中心文档（center documents）输出到lastCenter.xml中以供基于nutch爬取的网页内容的在线类别匹配。


WriteTime：2014-11-25

标题：

此工程为基于网页内容ES-IK分词的KMeans聚类、朴素贝叶斯和支持向量机算法实现的四个分类程序。

简介：

一、KMeans是思想简单有效的机器学习无监督自动聚类算法。
	考虑到精准性，每个类别我们选取了比较准确的样本documents XML文件（路径为：工程根目录\xml\CategorySample），来计算这K个类别的初始center documents以待接下来的聚类主过程。
	然后基于继续上方的center documents来 聚类计算 从准备好的大量待聚类的pending documents XML文件（路径为：工程根目录\xml\PendingDocs）解析到的documents初始数据，经过KMeans聚类计算后得到groups已分好类的二维数组。
	最终基于分类结果groups数组，可以计算到每个类最终的中心文档（center documents）输出到lastCenter.xml中以供基于nutch爬取的网页内容的在线类别匹配。

二、Naive Bayes借助先验概率进行分类。
	朴素贝叶斯可以调整初始特征属性的个数，会影响模型的生成。

三、SVM通过超平面划分思想进行分类。
	支持向量机借助核函数可以处理多维属性样本的分类，这里我们基于libsvm实现SVM分类器。libsvm提供了很多参数以供调整模型的生成，所以每次用于分类都可以有一个寻找最佳参数的过程，结合时间来说参数c、g的调整空间较大。

四、实现了一个简单的基于前三种分类器的分类投票决策器。
	简单的三选二投票实现。


关键的两个documents集定义：
 
训练样本集：EssayClassifier\xml\CategoryResult\workBase
     此目录下为之前人工验证的七个类的样本documents集的分类情况，由于其准确度很高，这次测试中主要作为训练样本集 对各分类器进行训练得到各模型；
    样本数：5810
 
预测测试集：EssayClassifier\xml\NBCategoryResult
   此目录下为之前项目训练预测后人工检验后标记的pending documents集分类情况，其集合比训练样本集大，且已标记预期分类适合用于预测准确率计算。
    样本数：6363
	
