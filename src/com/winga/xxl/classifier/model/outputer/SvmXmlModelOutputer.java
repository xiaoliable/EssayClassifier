package com.winga.xxl.classifier.model.outputer;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import tw.edu.ntu.csie.libsvm.model.svm;
import tw.edu.ntu.csie.libsvm.model.svm_node;
import tw.edu.ntu.csie.libsvm.model.svm_parameter;

import com.winga.xxl.classifier.data.exception.ModelException;
import com.winga.xxl.classifier.model.IModel;
import com.winga.xxl.classifier.model.NBayesModel;
import com.winga.xxl.classifier.model.SvmModel;

public class SvmXmlModelOutputer extends XmlModelOutputer {

	public String outputPath;

	public SvmXmlModelOutputer() {
	}

	public SvmXmlModelOutputer(String filePath) {
		this.outputPath = filePath;
	}

	@Override
	public void output(IModel model) throws IOException {

		try {
			if (!(model instanceof SvmModel)) {
				throw new ModelException("Svm model error !");
			}
			SvmModel theModel = (SvmModel) model;

			FileWriter out = new FileWriter(new File(this.outputPath));
			BufferedWriter bout = new BufferedWriter(out);

			svm_parameter param = theModel.param;

			bout.write("svm_type " + svm.svm_type_table[param.svm_type] + "\n");
			bout.write("kernel_type "
					+ svm.kernel_type_table[param.kernel_type] + "\n");

			if (param.kernel_type == svm_parameter.POLY)
				bout.write("degree " + param.degree + "\n");

			if (param.kernel_type == svm_parameter.POLY
					|| param.kernel_type == svm_parameter.RBF
					|| param.kernel_type == svm_parameter.SIGMOID)
				bout.write("gamma " + param.gamma + "\n");

			if (param.kernel_type == svm_parameter.POLY
					|| param.kernel_type == svm_parameter.SIGMOID)
				bout.write("coef0 " + param.coef0 + "\n");

			int nr_class = theModel.nr_class;
			int l = theModel.l;
			bout.write("nr_class " + nr_class + "\n");
			bout.write("total_sv " + l + "\n");

			{
				bout.write("rho");
				for (int i = 0; i < nr_class * (nr_class - 1) / 2; i++)
					bout.write(" " + theModel.rho[i]);
				bout.write("\n");
			}

			if (theModel.label != null) {
				bout.write("label");
				for (int i = 0; i < nr_class; i++)
					bout.write(" " + theModel.label[i]);
				bout.write("\n");
			}

			// Output the numCategoryMap
			Map<String, Double> numCateMap = theModel.numCategoryMap;
			if (numCateMap != null) {
				bout.write("numCategoryMap");
				for (int i = 0; i < nr_class; i++) {
					for (Iterator<String> itCate = numCateMap.keySet()
							.iterator(); itCate.hasNext();) {
						String cateName = itCate.next();
						// TODO
						if ((int) (double) (numCateMap.get(cateName)) == theModel.label[i]) {
							bout.write(" " + cateName);
							break;
						}
					}
				}
				bout.write("\n");
			}

			if (theModel.probA != null) // regression has probA only
			{
				bout.write("probA");
				for (int i = 0; i < nr_class * (nr_class - 1) / 2; i++)
					bout.write(" " + theModel.probA[i]);
				bout.write("\n");
			}
			if (theModel.probB != null) {
				bout.write("probB");
				for (int i = 0; i < nr_class * (nr_class - 1) / 2; i++)
					bout.write(" " + theModel.probB[i]);
				bout.write("\n");
			}

			if (theModel.nSV != null) {
				bout.write("nr_sv");
				for (int i = 0; i < nr_class; i++)
					bout.write(" " + theModel.nSV[i]);
				bout.write("\n");
			}

			bout.write("SV\n");
			double[][] sv_coef = theModel.sv_coef;
			svm_node[][] SV = theModel.SV;

			for (int i = 0; i < l; i++) {
				for (int j = 0; j < nr_class - 1; j++)
					bout.write(sv_coef[j][i] + " ");

				svm_node[] p = SV[i];
				if (param.kernel_type == svm_parameter.PRECOMPUTED)
					bout.write("0:" + (int) (p[0].value));
				else
					for (int j = 0; j < p.length; j++)
						bout.write(p[j].index + ":" + p[j].value + " ");
				bout.write("\n");
			}
			bout.close();
		} catch (ModelException e) {
			e.printStackTrace();
		}
	}
}
