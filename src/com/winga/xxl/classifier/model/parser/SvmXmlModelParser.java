package com.winga.xxl.classifier.model.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import tw.edu.ntu.csie.libsvm.model.svm_node;
import tw.edu.ntu.csie.libsvm.model.svm_parameter;

import com.winga.xxl.classifier.model.IModel;
import com.winga.xxl.classifier.model.SvmModel;

public class SvmXmlModelParser implements XmlModelParser {

	public static final String svm_type_table[] = { "c_svc", "nu_svc",
			"one_class", "epsilon_svr", "nu_svr", };

	public static final String kernel_type_table[] = { "linear", "polynomial",
			"rbf", "sigmoid", "precomputed" };

	private boolean read_model_header(BufferedReader fp, SvmModel model) {
		svm_parameter param = new svm_parameter();
		try {
			model.param = param;
			while (true) {
				String cmd = fp.readLine();
				String arg = cmd.substring(cmd.indexOf(' ') + 1);

				if (cmd.startsWith("svm_type")) {
					int i;
					for (i = 0; i < svm_type_table.length; i++) {
						if (arg.indexOf(svm_type_table[i]) != -1) {
							param.svm_type = i;
							break;
						}
					}
					if (i == svm_type_table.length) {
						System.err.print("unknown svm type.\n");
						return false;
					}
				} else if (cmd.startsWith("kernel_type")) {
					int i;
					for (i = 0; i < kernel_type_table.length; i++) {
						if (arg.indexOf(kernel_type_table[i]) != -1) {
							param.kernel_type = i;
							break;
						}
					}
					if (i == kernel_type_table.length) {
						System.err.print("unknown kernel function.\n");
						return false;
					}
				} else if (cmd.startsWith("degree"))
					param.degree = atoi(arg);
				else if (cmd.startsWith("gamma"))
					param.gamma = atof(arg);
				else if (cmd.startsWith("coef0"))
					param.coef0 = atof(arg);
				else if (cmd.startsWith("nr_class"))
					model.nr_class = atoi(arg);
				else if (cmd.startsWith("total_sv"))
					model.l = atoi(arg);
				else if (cmd.startsWith("rho")) {
					int n = model.nr_class * (model.nr_class - 1) / 2;
					model.rho = new double[n];
					StringTokenizer st = new StringTokenizer(arg);
					for (int i = 0; i < n; i++)
						model.rho[i] = atof(st.nextToken());
				} else if (cmd.startsWith("label")) {
					int n = model.nr_class;
					model.label = new int[n];
					StringTokenizer st = new StringTokenizer(arg);
					for (int i = 0; i < n; i++)
						model.label[i] = atoi(st.nextToken());
				}

				// Parse the numCategoryMap.
				else if (cmd.startsWith("numCategoryMap")) {
					int n = model.nr_class;
					model.numCategoryMap = new HashMap<String, Double>();
					StringTokenizer st = new StringTokenizer(arg);
					for (int i = 0; i < n; i++)
						model.numCategoryMap.put(st.nextToken(),
								(double) model.label[i]);
				}

				else if (cmd.startsWith("probA")) {
					int n = model.nr_class * (model.nr_class - 1) / 2;
					model.probA = new double[n];
					StringTokenizer st = new StringTokenizer(arg);
					for (int i = 0; i < n; i++)
						model.probA[i] = atof(st.nextToken());
				} else if (cmd.startsWith("probB")) {
					int n = model.nr_class * (model.nr_class - 1) / 2;
					model.probB = new double[n];
					StringTokenizer st = new StringTokenizer(arg);
					for (int i = 0; i < n; i++)
						model.probB[i] = atof(st.nextToken());
				} else if (cmd.startsWith("nr_sv")) {
					int n = model.nr_class;
					model.nSV = new int[n];
					StringTokenizer st = new StringTokenizer(arg);
					for (int i = 0; i < n; i++)
						model.nSV[i] = atoi(st.nextToken());
				} else if (cmd.startsWith("SV")) {
					break;
				} else {
					System.err.print("unknown text in model file: [" + cmd
							+ "]\n");
					return false;
				}
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	private static double atof(String s) {
		return Double.valueOf(s).doubleValue();
	}

	private static long atol(String s) {
		return Long.parseLong(s);
	}
	
	private static int atoi(String s) {
		return Integer.parseInt(s);
	}

	/**
	 * Model parser for the HDFS system.
	 * @CreateTime : 2015-2-4
	 * @author Xiaoliable
	 * */
	@Override
	public IModel hdfsParser(String xmlFilePath) throws IOException {

		//HDFS reader.
		Configuration conf = new Configuration();
		FileSystem hdfs = FileSystem.get(URI.create(xmlFilePath), conf);
		FSDataInputStream fs = hdfs.open(new Path(xmlFilePath)); 
        BufferedReader fp = new BufferedReader(new InputStreamReader(fs,"GBK"));

        // read parameters
		SvmModel model = new SvmModel();
		model.rho = null;
		model.probA = null;
		model.probB = null;
		model.label = null;
		model.nSV = null;

		if (read_model_header(fp, model) == false) {
			System.err.print("ERROR: failed to read model\n");
			return null;
		}

		// read sv_coef and SV.

		int m = model.nr_class - 1;
		int l = model.l;
		model.sv_coef = new double[m][l];
		model.SV = new svm_node[l][];

		for (int i = 0; i < l; i++) {
			String line = fp.readLine();
			StringTokenizer st = new StringTokenizer(line, " \t\n\r\f:");

			for (int k = 0; k < m; k++)
				model.sv_coef[k][i] = atof(st.nextToken());
			int n = st.countTokens() / 2;
			model.SV[i] = new svm_node[n];
			for (int j = 0; j < n; j++) {
				model.SV[i][j] = new svm_node();
				model.SV[i][j].index = atol(st.nextToken());
				model.SV[i][j].value = atof(st.nextToken());
			}
		}

		fp.close();
		return model;
	}
	
	@Override
	public IModel parser(String xmlFilePath) throws IOException {

		BufferedReader fp = new BufferedReader(new FileReader(xmlFilePath));

        // read parameters
		SvmModel model = new SvmModel();
		model.rho = null;
		model.probA = null;
		model.probB = null;
		model.label = null;
		model.nSV = null;

		if (read_model_header(fp, model) == false) {
			System.err.print("ERROR: failed to read model\n");
			return null;
		}

		// read sv_coef and SV.

		int m = model.nr_class - 1;
		int l = model.l;
		model.sv_coef = new double[m][l];
		model.SV = new svm_node[l][];

		for (int i = 0; i < l; i++) {
			String line = fp.readLine();
			StringTokenizer st = new StringTokenizer(line, " \t\n\r\f:");

			for (int k = 0; k < m; k++)
				model.sv_coef[k][i] = atof(st.nextToken());
			int n = st.countTokens() / 2;
			model.SV[i] = new svm_node[n];
			for (int j = 0; j < n; j++) {
				model.SV[i][j] = new svm_node();
				model.SV[i][j].index = atol(st.nextToken());
				model.SV[i][j].value = atof(st.nextToken());
			}
		}

		fp.close();
		return model;
	}
}
