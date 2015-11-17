package com.winga.xxl.classifier.util;

import java.awt.List;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import tw.edu.ntu.csie.libsvm.function.svm_predict;
import tw.edu.ntu.csie.libsvm.function.svm_train;
import tw.edu.ntu.csie.libsvm.model.svm;
import tw.edu.ntu.csie.libsvm.model.svm_node;
import tw.edu.ntu.csie.libsvm.model.svm_parameter;
import tw.edu.ntu.csie.libsvm.model.svm_problem;

import com.winga.xxl.classifier.data.exception.ModelException;
import com.winga.xxl.classifier.data.exception.TrainParamException;
import com.winga.xxl.classifier.data.store.Documents;
import com.winga.xxl.classifier.data.store.IDocuments;
import com.winga.xxl.classifier.data.store.VectorNode;
import com.winga.xxl.classifier.data.store.VectorProblem;
import com.winga.xxl.classifier.model.IModel;
import com.winga.xxl.classifier.model.NBayesModel;
import com.winga.xxl.classifier.model.SvmModel;
import com.winga.xxl.classifier.model.outputer.IModelOutputer;
import com.winga.xxl.classifier.multi.CategoryPredict;
import com.winga.xxl.classifier.parameter.NBTrainParameter;
import com.winga.xxl.classifier.parameter.PredictParameter;
import com.winga.xxl.classifier.parameter.SvmTrainParameter;
import com.winga.xxl.classifier.parameter.TrainParameter;
import com.winga.xxl.classifier.tour.TourDocuments;

public class SvmClassifier implements IClassifier {

	// -t kernel_type : set type of kernel function (default 2)
	// 0 -- linear: u'*v
	// 1 -- polynomial: (gamma*u'*v + coef0)^degree
	// 2 -- radial basis function: exp(-gamma*|u-v|^2)
	// 3 -- sigmoid: tanh(gamma*u'*v + coef0)
	// 4 -- precomputed kernel (kernel values in

	public final int KERNEL_LINEAR = 0;
	public final int KERNEL_POLYNOMIAL = 1;
	public final int KERNEL_RBF = 2;
	public final int KERNEL_PRECOMPUTED = 3;

	private svm_train libsvmTrain = null;

	public double accuracy;

	public svm_train getTrain() {

		return libsvmTrain;
	}

	public SvmClassifier() {
	}

	@Override
	public IModel train(IDocuments[] sampleDocs, TrainParameter param)
			throws IOException {

		try {
			if (!(param instanceof SvmTrainParameter)) {
				throw new TrainParamException("Svm train parameter error !");
			}

			String[] argv = ((SvmTrainParameter) param).argument.split(" ");
			IModelOutputer outputer = ((SvmTrainParameter) param).outputer;
			svm_train libsvmTrain = new svm_train();
			IModel model = new SvmModel();

			// Build the category name map.
			Map<String, Double> numCategoryMap = new TreeMap<String, Double>();

			// Category number.
			int k = 0;
			ArrayList<Integer> labelsList = new ArrayList<Integer>();
			for (int i = 0; i < sampleDocs.length; i++) {
				String cateName = sampleDocs[i].getCategory();
				if (!numCategoryMap.containsKey(cateName)) {
					numCategoryMap.put(cateName, (double) k);
					labelsList.add(k);
					++k;
				}
			}
			int[] labels = new int[k];
			for (int i = 0; i < labels.length; i++) {
				labels[i] = labelsList.get(i);
			}

			Vector<Double> vy = new Vector<Double>();
			Vector<svm_node[]> vx = new Vector<svm_node[]>();
			long max_index = 0;

			for (int i = 0; i < sampleDocs.length; ++i) {
				if (sampleDocs.length == 0)
					break;

				Map<Long, Integer> pendingDoc = sampleDocs[i].getTitleVector();
				pendingDoc.putAll(sampleDocs[i].getContentVector());

				vy.addElement(numCategoryMap.get(sampleDocs[i].getCategory()));
				int m = pendingDoc.size();
				svm_node[] x = new svm_node[m];
				int j = 0;
				for (Iterator<Long> itHash = pendingDoc.keySet().iterator(); itHash
						.hasNext();) {
					long wordHash = itHash.next();
					x[j] = new svm_node();
					x[j].index = wordHash;
					x[j++].value = pendingDoc.get(wordHash);
				}
				if (m > 0)
					max_index = m;
				vx.addElement(x);
			}

			svm_problem prob = new svm_problem();
			prob.l = vy.size();
			prob.x = new svm_node[prob.l][];
			for (int i = 0; i < prob.l; i++)
				prob.x[i] = vx.elementAt(i);
			prob.y = new double[prob.l];
			for (int i = 0; i < prob.l; i++)
				prob.y[i] = vy.elementAt(i);

			libsvmTrain.setProb(prob);
			libsvmTrain.parse_command_line(argv);

			svm_parameter libsvmParam = libsvmTrain.getParam();
			if (libsvmParam.gamma == 0 && max_index > 0)
				libsvmParam.gamma = 1.0 / max_index;

			if (libsvmParam.kernel_type == svm_parameter.PRECOMPUTED)
				for (int i = 0; i < prob.l; i++) {
					if (prob.x[i][0].index != 0) {
						System.err
								.print("Wrong kernel matrix: first column must be 0:sample_serial_number\n");
						System.exit(1);
					}
					if ((int) prob.x[i][0].value <= 0
							|| (int) prob.x[i][0].value > max_index) {
						System.err
								.print("Wrong input format: sample_serial_number out of range\n");
						System.exit(1);
					}
				}

			model = svm
					.svm_train(libsvmTrain.getProb(), libsvmTrain.getParam());
			((SvmModel) model).numCategoryMap = numCategoryMap;
			((SvmModel) model).label = labels;
			if (outputer != null) {
				outputer.output(model);
			}
			return model;
		} catch (TrainParamException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String predict(IDocuments document, IModel model,
			PredictParameter param) throws IOException {

		try {
			if (!(model instanceof SvmModel)) {
				throw new ModelException("Svm model error !");
			}

			String[] predictArgsArray = param.arguments.split(" ");
			SvmModel svmModel = (SvmModel) model;
			Map<String, Double> numCategoryMap = svmModel.numCategoryMap;
			svm_predict svmPredict = new svm_predict();

			int i, predict_probability = 0;
			svmPredict.svm_print_string = svmPredict.svm_print_stdout;

			// parse options
			for (i = 0; i < predictArgsArray.length; i++) {
				if (predictArgsArray[i].charAt(0) != '-')
					break;
				++i;
				switch (predictArgsArray[i - 1].charAt(1)) {
				case 'b':
					predict_probability = svmPredict.atoi(predictArgsArray[i]);
					break;
				case 'q':
					svmPredict.svm_print_string = svmPredict.svm_print_null;
					i--;
					break;
				default:
					System.err.print("Unknown option: "
							+ predictArgsArray[i - 1] + "\n");
					svmPredict.exit_with_help();
				}
			}
			if (i >= predictArgsArray.length - 2)
				svmPredict.exit_with_help();
			try {
				DataOutputStream output = new DataOutputStream(
						new BufferedOutputStream(new FileOutputStream(
								predictArgsArray[i + 2])));

				if (svmModel == null) {
					System.err.print("can't open model file "
							+ predictArgsArray[i + 1] + "\n");
					System.exit(1);
				}
				if (predict_probability == 1) {
					if (svm.svm_check_probability_model(svmModel) == 0) {
						System.err
								.print("Model does not support probabiliy estimates\n");
						System.exit(1);
					}
				} else {
					if (svm.svm_check_probability_model(svmModel) != 0) {
						svm_predict
								.info("Model supports probability estimates, but disabled in prediction.\n");
					}
				}

				// svmPredict.predict(input,output,model,predict_probability);
				int correct = 0;
				int total = 0;
				double error = 0;
				double sumv = 0, sumy = 0, sumvv = 0, sumyy = 0, sumvy = 0;

				int svm_type = svm.svm_get_svm_type(svmModel);
				int nr_class = svm.svm_get_nr_class(svmModel);
				double[] prob_estimates = null;

				if (predict_probability == 1) {
					if (svm_type == svm_parameter.EPSILON_SVR
							|| svm_type == svm_parameter.NU_SVR) {
						svm_predict
								.info("Prob. model for test data: target value = predicted value + z,\nz: Laplace distribution e^(-|z|/sigma)/(2sigma),sigma="
										+ svm.svm_get_svr_probability(svmModel)
										+ "\n");
					} else {
						int[] labels = new int[nr_class];
						svm.svm_get_labels(svmModel, labels);
						prob_estimates = new double[nr_class];
						output.writeBytes("labels");
						for (int j = 0; j < nr_class; j++)
							output.writeBytes(" " + labels[j]);
						output.writeBytes("\n");
					}
				}
				// Get the document's title and content vector.
				Map<Long, Integer> pendingDoc = document.getTitleVector();
				pendingDoc.putAll(document.getContentVector());

				// Initial document's category should be contained in
				// numCategoryMap.
				// double target =
				// numCategoryMap.get(testDocuments.getCategory());

				// double target = numCategoryMap.get(document.getCategory());
				int m = pendingDoc.size();
				svm_node[] x = new svm_node[m];
				int j = 0;
				for (Iterator<Long> itHash = pendingDoc.keySet().iterator(); itHash
						.hasNext();) {
					long wordHash = itHash.next();
					x[j] = new svm_node();
					x[j].index = wordHash;
					x[j++].value = pendingDoc.get(wordHash);
				}
				double v;
				if (predict_probability == 1
						&& (svm_type == svm_parameter.C_SVC || svm_type == svm_parameter.NU_SVC)) {
					v = svm.svm_predict_probability(svmModel, x, prob_estimates);
					output.writeBytes(v + " ");
					for (int j2 = 0; j2 < nr_class; j2++)
						output.writeBytes(prob_estimates[j2] + " ");
					output.writeBytes("\n");
				} else {
					v = svm.svm_predict(svmModel, x);
					output.writeBytes(v + "\n");
				}

				// // Print some debug material.
				// System.out.println("Documents[ " + idoc
				// + " ] marked-category is : "
				// + getCategoryFromDouble(target, numCategoryMap)
				// + " ; and predict-category is : " + getCategoryFromDouble(v,
				// numCategoryMap));

				// if (v == target)
				// ++correct;
				// error += (v - target) * (v - target);
				// sumv += v;
				// sumy += target;
				// sumvv += v * v;
				// sumyy += target * target;
				// sumvy += v * target;
				// ++total;
				//
				// if (svm_type == svm_parameter.EPSILON_SVR
				// || svm_type == svm_parameter.NU_SVR) {
				// svm_predict.info("Mean squared error = " + error / total
				// + " (regression)\n");
				// svm_predict
				// .info("Squared correlation coefficient = "
				// + ((total * sumvy - sumv * sumy) * (total
				// * sumvy - sumv * sumy))
				// / ((total * sumvv - sumv * sumv) * (total
				// * sumyy - sumy * sumy))
				// + " (regression)\n");
				// } else
				// svm_predict.info("\nAccuracy = " + (double) correct / total
				// * 100 + "% (" + correct + "/" + total
				// + ") (classification)\n");
				// this.accuracy = (double) correct / total;

				String testCategory = getCategoryFromDouble(v, numCategoryMap);
				output.close();
				return testCategory;
			} catch (FileNotFoundException e) {
				svmPredict.exit_with_help();
			} catch (ArrayIndexOutOfBoundsException e) {
				svmPredict.exit_with_help();
			}
			return null;
		} catch (ModelException e) {
			e.printStackTrace();
		}
		return null;
	}

	public SvmClassifier(String[] trainArgs, IDocuments[] documents,
			Map<String, Double> numCategoryMap) {
		libsvmTrain = new svm_train();
		libsvmTrain.parse_command_line(trainArgs);
		// read_problem
		Vector<Double> vy = new Vector<Double>();
		Vector<svm_node[]> vx = new Vector<svm_node[]>();
		long max_index = 0;

		for (int i = 0; i < documents.length; ++i) {
			if (documents.length == 0)
				break;

			documents[i].getTitleVector().putAll(
					documents[i].getContentVector());
			Map<Long, Integer> pendingDoc = documents[i].getTitleVector();

			vy.addElement(numCategoryMap.get(documents[i].getCategory()));
			int m = pendingDoc.size();
			svm_node[] x = new svm_node[m];
			int j = 0;
			for (Iterator<Long> itHash = pendingDoc.keySet().iterator(); itHash
					.hasNext();) {
				long wordHash = itHash.next();
				x[j] = new svm_node();
				x[j].index = wordHash;
				x[j++].value = pendingDoc.get(wordHash);
			}
			if (m > 0)
				max_index = m;
			vx.addElement(x);
		}

		svm_problem prob = new svm_problem();
		prob.l = vy.size();
		prob.x = new svm_node[prob.l][];
		for (int i = 0; i < prob.l; i++)
			prob.x[i] = vx.elementAt(i);
		prob.y = new double[prob.l];
		for (int i = 0; i < prob.l; i++)
			prob.y[i] = vy.elementAt(i);

		libsvmTrain.setProb(prob);

		svm_parameter param = libsvmTrain.getParam();
		if (param.gamma == 0 && max_index > 0)
			param.gamma = 1.0 / max_index;

		if (param.kernel_type == svm_parameter.PRECOMPUTED)
			for (int i = 0; i < prob.l; i++) {
				if (prob.x[i][0].index != 0) {
					System.err
							.print("Wrong kernel matrix: first column must be 0:sample_serial_number\n");
					System.exit(1);
				}
				if ((int) prob.x[i][0].value <= 0
						|| (int) prob.x[i][0].value > max_index) {
					System.err
							.print("Wrong input format: sample_serial_number out of range\n");
					System.exit(1);
				}
			}
	}

	public SvmModel trainModel() {
		return svm.svm_train(libsvmTrain.getProb(), libsvmTrain.getParam());
	}

	public String predictOneModel(SvmModel model, String[] predictArgs,
			IDocuments testDocuments, Map<String, Double> numCategoryMap)
			throws IOException {
		svm_predict svmPredict = new svm_predict();

		int i, predict_probability = 0;
		svmPredict.svm_print_string = svmPredict.svm_print_stdout;

		// parse options
		for (i = 0; i < predictArgs.length; i++) {
			if (predictArgs[i].charAt(0) != '-')
				break;
			++i;
			switch (predictArgs[i - 1].charAt(1)) {
			case 'b':
				predict_probability = svmPredict.atoi(predictArgs[i]);
				break;
			case 'q':
				svmPredict.svm_print_string = svmPredict.svm_print_null;
				i--;
				break;
			default:
				System.err
						.print("Unknown option: " + predictArgs[i - 1] + "\n");
				svmPredict.exit_with_help();
			}
		}
		if (i >= predictArgs.length - 2)
			svmPredict.exit_with_help();
		try {
			DataOutputStream output = new DataOutputStream(
					new BufferedOutputStream(new FileOutputStream(
							predictArgs[i + 2])));

			if (model == null) {
				System.err.print("can't open model file " + predictArgs[i + 1]
						+ "\n");
				System.exit(1);
			}
			if (predict_probability == 1) {
				if (svm.svm_check_probability_model(model) == 0) {
					System.err
							.print("Model does not support probabiliy estimates\n");
					System.exit(1);
				}
			} else {
				if (svm.svm_check_probability_model(model) != 0) {
					svm_predict
							.info("Model supports probability estimates, but disabled in prediction.\n");
				}
			}

			// svmPredict.predict(input,output,model,predict_probability);

			int correct = 0;
			int total = 0;
			double error = 0;
			double sumv = 0, sumy = 0, sumvv = 0, sumyy = 0, sumvy = 0;

			int svm_type = svm.svm_get_svm_type(model);
			int nr_class = svm.svm_get_nr_class(model);
			double[] prob_estimates = null;

			if (predict_probability == 1) {
				if (svm_type == svm_parameter.EPSILON_SVR
						|| svm_type == svm_parameter.NU_SVR) {
					svm_predict
							.info("Prob. model for test data: target value = predicted value + z,\nz: Laplace distribution e^(-|z|/sigma)/(2sigma),sigma="
									+ svm.svm_get_svr_probability(model) + "\n");
				} else {
					int[] labels = new int[nr_class];
					svm.svm_get_labels(model, labels);
					prob_estimates = new double[nr_class];
					output.writeBytes("labels");
					for (int j = 0; j < nr_class; j++)
						output.writeBytes(" " + labels[j]);
					output.writeBytes("\n");
				}
			}

			Map<Long, Integer> pendingDoc = testDocuments.getContentVector();

			// Initial document's category should be contained in
			// numCategoryMap.
			// double target = numCategoryMap.get(testDocuments.getCategory());
			int m = pendingDoc.size();
			svm_node[] x = new svm_node[m];
			int j = 0;
			for (Iterator<Long> itHash = pendingDoc.keySet().iterator(); itHash
					.hasNext();) {
				long wordHash = itHash.next();
				x[j] = new svm_node();
				x[j].index = wordHash;
				x[j++].value = pendingDoc.get(wordHash);
			}
			double v;
			if (predict_probability == 1
					&& (svm_type == svm_parameter.C_SVC || svm_type == svm_parameter.NU_SVC)) {
				v = svm.svm_predict_probability(model, x, prob_estimates);
				output.writeBytes(v + " ");
				for (int j2 = 0; j2 < nr_class; j2++)
					output.writeBytes(prob_estimates[j2] + " ");
				output.writeBytes("\n");
			} else {
				v = svm.svm_predict(model, x);
				output.writeBytes(v + "\n");
			}

			// Get the predict category name.
			String testCategory = getCategoryFromDouble(v, numCategoryMap);
			output.close();
			return testCategory;
			// if (v == target)
			// ++correct;
			// error += (v - target) * (v - target);
			// sumv += v;
			// sumy += target;
			// sumvv += v * v;
			// sumyy += target * target;
			// sumvy += v * target;
			// ++total;
			//
			// if (svm_type == svm_parameter.EPSILON_SVR
			// || svm_type == svm_parameter.NU_SVR) {
			// svm_predict.info("Mean squared error = " + error / total
			// + " (regression)\n");
			// svm_predict
			// .info("Squared correlation coefficient = "
			// + ((total * sumvy - sumv * sumy) * (total
			// * sumvy - sumv * sumy))
			// / ((total * sumvv - sumv * sumv) * (total
			// * sumyy - sumy * sumy))
			// + " (regression)\n");
			// } else
			// svm_predict.info("Accuracy = " + (double) correct / total * 100
			// + "% (" + correct + "/" + total
			// + ") (classification)\n");
			//
			// output.close();
		} catch (FileNotFoundException e) {
			svmPredict.exit_with_help();
		} catch (ArrayIndexOutOfBoundsException e) {
			svmPredict.exit_with_help();
		}
		return null;
	}

	public String predictModel(SvmModel model, String[] predictArgsArray,
			IDocuments[] testDocuments, Map<String, Double> numCategoryMap)
			throws IOException {
		svm_predict svmPredict = new svm_predict();

		int i, predict_probability = 0;
		svmPredict.svm_print_string = svmPredict.svm_print_stdout;

		// parse options
		for (i = 0; i < predictArgsArray.length; i++) {
			if (predictArgsArray[i].charAt(0) != '-')
				break;
			++i;
			switch (predictArgsArray[i - 1].charAt(1)) {
			case 'b':
				predict_probability = svmPredict.atoi(predictArgsArray[i]);
				break;
			case 'q':
				svmPredict.svm_print_string = svmPredict.svm_print_null;
				i--;
				break;
			default:
				System.err.print("Unknown option: " + predictArgsArray[i - 1]
						+ "\n");
				svmPredict.exit_with_help();
			}
		}
		if (i >= predictArgsArray.length - 2)
			svmPredict.exit_with_help();
		try {
			DataOutputStream output = new DataOutputStream(
					new BufferedOutputStream(new FileOutputStream(
							predictArgsArray[i + 2])));

			if (model == null) {
				System.err.print("can't open model file "
						+ predictArgsArray[i + 1] + "\n");
				System.exit(1);
			}
			if (predict_probability == 1) {
				if (svm.svm_check_probability_model(model) == 0) {
					System.err
							.print("Model does not support probabiliy estimates\n");
					System.exit(1);
				}
			} else {
				if (svm.svm_check_probability_model(model) != 0) {
					svm_predict
							.info("Model supports probability estimates, but disabled in prediction.\n");
				}
			}

			// svmPredict.predict(input,output,model,predict_probability);
			int correct = 0;
			int total = 0;
			double error = 0;
			double sumv = 0, sumy = 0, sumvv = 0, sumyy = 0, sumvy = 0;

			int svm_type = svm.svm_get_svm_type(model);
			int nr_class = svm.svm_get_nr_class(model);
			double[] prob_estimates = null;

			if (predict_probability == 1) {
				if (svm_type == svm_parameter.EPSILON_SVR
						|| svm_type == svm_parameter.NU_SVR) {
					svm_predict
							.info("Prob. model for test data: target value = predicted value + z,\nz: Laplace distribution e^(-|z|/sigma)/(2sigma),sigma="
									+ svm.svm_get_svr_probability(model) + "\n");
				} else {
					int[] labels = new int[nr_class];
					svm.svm_get_labels(model, labels);
					prob_estimates = new double[nr_class];
					output.writeBytes("labels");
					for (int j = 0; j < nr_class; j++)
						output.writeBytes(" " + labels[j]);
					output.writeBytes("\n");
				}
			}
			for (int idoc = 0; idoc < testDocuments.length; ++idoc) {
				Map<Long, Integer> pendingDoc = testDocuments[idoc]
						.getContentVector();

				// Initial document's category should be contained in
				// numCategoryMap.
				// double target =
				// numCategoryMap.get(testDocuments.getCategory());

				double target = numCategoryMap.get(testDocuments[idoc]
						.getCategory());
				int m = pendingDoc.size();
				svm_node[] x = new svm_node[m];
				int j = 0;
				for (Iterator<Long> itHash = pendingDoc.keySet().iterator(); itHash
						.hasNext();) {
					long wordHash = itHash.next();
					x[j] = new svm_node();
					x[j].index = wordHash;
					x[j++].value = pendingDoc.get(wordHash);
				}
				double v;
				if (predict_probability == 1
						&& (svm_type == svm_parameter.C_SVC || svm_type == svm_parameter.NU_SVC)) {
					v = svm.svm_predict_probability(model, x, prob_estimates);
					output.writeBytes(v + " ");
					for (int j2 = 0; j2 < nr_class; j2++)
						output.writeBytes(prob_estimates[j2] + " ");
					output.writeBytes("\n");
				} else {
					v = svm.svm_predict(model, x);
					output.writeBytes(v + "\n");
				}

				// // Print some debug material.
				// System.out.println("Documents[ " + idoc
				// + " ] marked-category is : "
				// + getCategoryFromDouble(target, numCategoryMap)
				// + " ; and predict-category is : " + getCategoryFromDouble(v,
				// numCategoryMap));

				if (v == target)
					++correct;
				error += (v - target) * (v - target);
				sumv += v;
				sumy += target;
				sumvv += v * v;
				sumyy += target * target;
				sumvy += v * target;
				++total;
			}
			if (svm_type == svm_parameter.EPSILON_SVR
					|| svm_type == svm_parameter.NU_SVR) {
				svm_predict.info("Mean squared error = " + error / total
						+ " (regression)\n");
				svm_predict
						.info("Squared correlation coefficient = "
								+ ((total * sumvy - sumv * sumy) * (total
										* sumvy - sumv * sumy))
								/ ((total * sumvv - sumv * sumv) * (total
										* sumyy - sumy * sumy))
								+ " (regression)\n");
			} else
				svm_predict.info("\nAccuracy = " + (double) correct / total
						* 100 + "% (" + correct + "/" + total
						+ ") (classification)\n");
			this.accuracy = (double) correct / total;
			output.close();
		} catch (FileNotFoundException e) {
			svmPredict.exit_with_help();
		} catch (ArrayIndexOutOfBoundsException e) {
			svmPredict.exit_with_help();
		}
		return null;
	}

	/**
	 * Get the special value of the category name in the numCategoryMap.
	 * 
	 * @author xiaoxiao
	 * */
	public String getCategoryFromDouble(double value,
			Map<String, Double> numCategoryMap) {
		String testCategory = null;
		for (Iterator<String> categoryName = numCategoryMap.keySet().iterator(); categoryName
				.hasNext();) {
			String category = categoryName.next();
			if (numCategoryMap.get(category) == value) {
				testCategory = category;
				break;
			}
		}
		return testCategory;
	}

	public String getBestCG(Map<String, Double> numCategoryMap,
			IDocuments[] sampleDocs, IDocuments[] testDocs, int minLog,
			int maxLog, int[] kernelArray) throws IOException {
		return getBestCG(numCategoryMap, sampleDocs, testDocs, minLog, maxLog,
				minLog, maxLog, kernelArray);
	}

	public String getBestCG(Map<String, Double> numCategoryMap,
			IDocuments[] sampleDocs, IDocuments[] testDocs, int minLogCost,
			int maxLogCost, int minLogGamma, int maxLogGamma, int[] kernelArray)
			throws IOException {

		String everyTrainArgs;
		// Any words to match libsvm's formatting.
		String everyPredictArgs = "abc acd df";
		String[] everyPredictArgsArray = everyPredictArgs.split(" ");
		double cost = Math.pow(2, minLogCost), gamma = Math.pow(2, minLogCost);
		int count = 0, kernel = KERNEL_RBF, bestKernel = KERNEL_RBF;
		double bestCost = cost, bestGamma = gamma;
		double maxAccuracy = 0;
		for (int i = minLogCost; i < maxLogCost; ++i) {
			cost = Math.pow(2, i);
			for (int j = minLogGamma; j < maxLogGamma; ++j) {
				gamma = Math.pow(2, j);
				for (int k = 0; k < kernelArray.length; ++k) {
					kernel = kernelArray[k];
					++count;
					everyTrainArgs = "-c " + cost + " -g " + gamma + " zcxc";
					String[] everyTrainArgsArray = everyTrainArgs.split(" ");
					SvmClassifier everyWrapper = new SvmClassifier(
							everyTrainArgsArray, sampleDocs, numCategoryMap);
					everyWrapper.predictModel(everyWrapper.trainModel(),
							everyPredictArgsArray, testDocs, numCategoryMap);
					System.out.println("The " + count
							+ " -th train arguments are : -t " + kernel
							+ " -c " + cost + " -g " + gamma);
					System.out
							.println("----------------------------------------------------------------------------------");
					if (maxAccuracy < everyWrapper.accuracy) {
						maxAccuracy = everyWrapper.accuracy;
						bestCost = cost;
						bestGamma = gamma;
						bestKernel = kernel;
					}
				}
			}
		}

		return " -t " + bestKernel + " -c " + bestCost + " -g " + bestGamma
				+ "\nmaxAccuracy is : " + maxAccuracy;
	}

	/**
	 * <p>
	 * CreateTime : 2014-11-22
	 * */
	public String getTheBestCG(IDocuments[] sampleDocs, IDocuments[] testDocs,
			int minLogCost, int maxLogCost, int minLogGamma, int maxLogGamma,
			int[] kernelArray) throws IOException {

		String everyTrainArgs;
		// Any words to match libsvm's formatting.
		String everyPredictArgs = "abc acd df";
		double cost = Math.pow(2, minLogCost), gamma = Math.pow(2, minLogCost);
		int count = 0, kernel = KERNEL_RBF, bestKernel = KERNEL_RBF;
		double bestCost = cost, bestGamma = gamma;
		double maxAccuracy = 0;
		PredictParameter predictParameter = new PredictParameter(
				everyPredictArgs);
		for (int i = minLogCost; i <= maxLogCost; ++i) {
			cost = Math.pow(2, i);
			for (int j = minLogGamma; j <= maxLogGamma; ++j) {
				gamma = Math.pow(2, j);
				for (int k = 0; k < kernelArray.length; ++k) {
					kernel = kernelArray[k];
					++count;
					everyTrainArgs = "-c " + cost + " -g " + gamma + " zcxc";

					IClassifier svmClassifier = new SvmClassifier();
					TrainParameter trainParameter = new SvmTrainParameter(
							everyTrainArgs, null);
					IModel model = svmClassifier.train(sampleDocs,
							trainParameter);

					double accuracy = CategoryPredict.arrayPercentPredict(
							testDocs, model, predictParameter, svmClassifier);

					System.out.println("The " + count
							+ " -th train arguments are : -t " + kernel
							+ " -c " + cost + " -g " + gamma);
					System.out.println("The accuracy is : " + accuracy + ".");
					System.out
							.println("----------------------------------------------------------------------------------");
					if (maxAccuracy < accuracy) {
						maxAccuracy = accuracy;
						bestCost = cost;
						bestGamma = gamma;
						bestKernel = kernel;
					}
				}
			}
		}

		return " -t " + bestKernel + " -c " + bestCost + " -g " + bestGamma
				+ "\nmaxAccuracy is : " + maxAccuracy;
	}

	/**
	 * <p>
	 * CreateTime : 2014-12-18
	 * */
	public String getTheBestCG(VectorProblem sampleProb,
			VectorProblem testProb, int minLogCost, int maxLogCost,
			int minLogGamma, int maxLogGamma, int[] kernelArray)
			throws IOException {

		String everyTrainArgs;
		// Any words to match libsvm's formatting.
		String everyPredictArgs = "abc acd df";
		double cost = Math.pow(2, minLogCost), gamma = Math.pow(2, minLogCost);
		int count = 0, kernel = KERNEL_RBF, bestKernel = KERNEL_RBF;
		double bestCost = cost, bestGamma = gamma;
		double maxAccuracy = 0;
		PredictParameter predictParameter = new PredictParameter(
				everyPredictArgs);
		for (int i = minLogCost; i <= maxLogCost; ++i) {
			cost = Math.pow(2, i);
			for (int j = minLogGamma; j <= maxLogGamma; ++j) {
				gamma = Math.pow(2, j);
				for (int k = 0; k < kernelArray.length; ++k) {
					kernel = kernelArray[k];
					++count;
					everyTrainArgs = "-c " + cost + " -g " + gamma + " zcxc";

					IClassifier svmClassifier = new SvmClassifier();
					TrainParameter trainParameter = new SvmTrainParameter(
							everyTrainArgs, null);
					IModel model = svmClassifier.train(sampleProb,
							trainParameter);

					double accuracy = CategoryPredict.arrayPercentPredict(
							testProb, model, predictParameter, svmClassifier);

					System.out.println("The " + count
							+ " -th train arguments are : -t " + kernel
							+ " -c " + cost + " -g " + gamma);
					System.out.println("The accuracy is : " + accuracy + ".");
					System.out
							.println("----------------------------------------------------------------------------------");
					if (maxAccuracy < accuracy) {
						maxAccuracy = accuracy;
						bestCost = cost;
						bestGamma = gamma;
						bestKernel = kernel;
					}
				}
			}
		}

		return " -t " + bestKernel + " -c " + bestCost + " -g " + bestGamma
				+ "\nmaxAccuracy is : " + maxAccuracy;
	}

	@Override
	public double predict(VectorNode[] nodeArray, IModel model,
			PredictParameter predictParameter) throws IOException {

		try {
			if (!(model instanceof SvmModel)) {
				throw new ModelException("Svm model error !");
			}

			String[] predictArgsArray = predictParameter.arguments.split(" ");
			SvmModel svmModel = (SvmModel) model;
			Map<String, Double> numCategoryMap = svmModel.numCategoryMap;
			svm_predict svmPredict = new svm_predict();

			int i, predict_probability = 0;
			svmPredict.svm_print_string = svmPredict.svm_print_stdout;

			// parse options
			for (i = 0; i < predictArgsArray.length; i++) {
				if (predictArgsArray[i].charAt(0) != '-')
					break;
				++i;
				switch (predictArgsArray[i - 1].charAt(1)) {
				case 'b':
					predict_probability = svmPredict.atoi(predictArgsArray[i]);
					break;
				case 'q':
					svmPredict.svm_print_string = svmPredict.svm_print_null;
					i--;
					break;
				default:
					System.err.print("Unknown option: "
							+ predictArgsArray[i - 1] + "\n");
					svmPredict.exit_with_help();
				}
			}
			if (i >= predictArgsArray.length - 2)
				svmPredict.exit_with_help();
			try {
				DataOutputStream output = new DataOutputStream(
						new BufferedOutputStream(new FileOutputStream(
								predictArgsArray[i + 2])));

				if (svmModel == null) {
					System.err.print("can't open model file "
							+ predictArgsArray[i + 1] + "\n");
					System.exit(1);
				}
				if (predict_probability == 1) {
					if (svm.svm_check_probability_model(svmModel) == 0) {
						System.err
								.print("Model does not support probabiliy estimates\n");
						System.exit(1);
					}
				} else {
					if (svm.svm_check_probability_model(svmModel) != 0) {
						svm_predict
								.info("Model supports probability estimates, but disabled in prediction.\n");
					}
				}

				// svmPredict.predict(input,output,model,predict_probability);
				int correct = 0;
				int total = 0;
				double error = 0;
				double sumv = 0, sumy = 0, sumvv = 0, sumyy = 0, sumvy = 0;

				int svm_type = svm.svm_get_svm_type(svmModel);
				int nr_class = svm.svm_get_nr_class(svmModel);
				double[] prob_estimates = null;

				if (predict_probability == 1) {
					if (svm_type == svm_parameter.EPSILON_SVR
							|| svm_type == svm_parameter.NU_SVR) {
						svm_predict
								.info("Prob. model for test data: target value = predicted value + z,\nz: Laplace distribution e^(-|z|/sigma)/(2sigma),sigma="
										+ svm.svm_get_svr_probability(svmModel)
										+ "\n");
					} else {
						int[] labels = new int[nr_class];
						svm.svm_get_labels(svmModel, labels);
						prob_estimates = new double[nr_class];
						output.writeBytes("labels");
						for (int j = 0; j < nr_class; j++)
							output.writeBytes(" " + labels[j]);
						output.writeBytes("\n");
					}
				}
				int m = nodeArray.length;
				VectorNode[] x = new VectorNode[m];
				int j = 0;
				for (int k = 0; k < m; k++) {
					x[k] = new VectorNode();
					x[k].index = nodeArray[k].index;
					x[k].value = nodeArray[k].value;
				}
				double v;
				if (predict_probability == 1
						&& (svm_type == svm_parameter.C_SVC || svm_type == svm_parameter.NU_SVC)) {
					v = svm_predict_probability(svmModel, x, prob_estimates);
					output.writeBytes(v + " ");
					for (int j2 = 0; j2 < nr_class; j2++)
						output.writeBytes(prob_estimates[j2] + " ");
					output.writeBytes("\n");
				} else {
					v = svm_predict(svmModel, x);
					output.writeBytes(v + "\n");
				}

				// // Print some debug material.
				// System.out.println("Documents[ " + idoc
				// + " ] marked-category is : "
				// + getCategoryFromDouble(target, numCategoryMap)
				// + " ; and predict-category is : " + getCategoryFromDouble(v,
				// numCategoryMap));

				// if (v == target)
				// ++correct;
				// error += (v - target) * (v - target);
				// sumv += v;
				// sumy += target;
				// sumvv += v * v;
				// sumyy += target * target;
				// sumvy += v * target;
				// ++total;
				//
				// if (svm_type == svm_parameter.EPSILON_SVR
				// || svm_type == svm_parameter.NU_SVR) {
				// svm_predict.info("Mean squared error = " + error / total
				// + " (regression)\n");
				// svm_predict
				// .info("Squared correlation coefficient = "
				// + ((total * sumvy - sumv * sumy) * (total
				// * sumvy - sumv * sumy))
				// / ((total * sumvv - sumv * sumv) * (total
				// * sumyy - sumy * sumy))
				// + " (regression)\n");
				// } else
				// svm_predict.info("\nAccuracy = " + (double) correct / total
				// * 100 + "% (" + correct + "/" + total
				// + ") (classification)\n");
				// this.accuracy = (double) correct / total;

				// String testCategory = getCategoryFromDouble(v,
				// numCategoryMap);
				output.close();
				return v;
			} catch (FileNotFoundException e) {
				svmPredict.exit_with_help();
			} catch (ArrayIndexOutOfBoundsException e) {
				svmPredict.exit_with_help();
			}
		} catch (ModelException e) {
			e.printStackTrace();
		}
		return -1;
	}

	@Override
	public IModel train(VectorProblem problem, TrainParameter param)
			throws IOException {

		try {
			if (!(param instanceof SvmTrainParameter)) {
				throw new TrainParamException("Svm train parameter error !");
			}

			String[] argv = ((SvmTrainParameter) param).argument.split(" ");
			IModelOutputer outputer = ((SvmTrainParameter) param).outputer;
			svm_train libsvmTrain = new svm_train();
			IModel model = new SvmModel();

			Vector<Double> vy = new Vector<Double>();
			Vector<svm_node[]> vx = new Vector<svm_node[]>();
			long max_index = 0;
			int m = problem.l;
			for (int i = 0; i < m; ++i) {
				vy.addElement(problem.y[i]);

				svm_node[] x = new svm_node[problem.x[i].length];
				for (int k = 0; k < problem.x[i].length; k++) {
					x[k] = new svm_node();
					x[k].index = problem.x[i][k].index;
					x[k].value = problem.x[i][k].value;
				}

				vx.addElement(x);
			}

			svm_problem prob = new svm_problem();
			prob.l = vy.size();
			prob.x = new svm_node[prob.l][];
			for (int i = 0; i < prob.l; i++)
				prob.x[i] = vx.elementAt(i);
			prob.y = new double[prob.l];
			for (int i = 0; i < prob.l; i++)
				prob.y[i] = vy.elementAt(i);

			libsvmTrain.setProb(prob);
			libsvmTrain.parse_command_line(argv);

			svm_parameter libsvmParam = libsvmTrain.getParam();
			if (libsvmParam.gamma == 0 && max_index > 0)
				libsvmParam.gamma = 1.0 / max_index;

			if (libsvmParam.kernel_type == svm_parameter.PRECOMPUTED)
				for (int i = 0; i < prob.l; i++) {
					if (prob.x[i][0].index != 0) {
						System.err
								.print("Wrong kernel matrix: first column must be 0:sample_serial_number\n");
						System.exit(1);
					}
					if ((int) prob.x[i][0].value <= 0
							|| (int) prob.x[i][0].value > max_index) {
						System.err
								.print("Wrong input format: sample_serial_number out of range\n");
						System.exit(1);
					}
				}

			model = svm
					.svm_train(libsvmTrain.getProb(), libsvmTrain.getParam());
			((SvmModel) model).numCategoryMap = problem.numCategoryMap;

			// Build the category name map.
			Map<String, Double> numCategoryMap = new TreeMap<String, Double>();
			int k = 0;
			for (int i = 0; i < ((SvmModel) model).nr_class; i++) {
				numCategoryMap.put(Integer.toString(k), (double) k);
				++k;
			}
			if (outputer != null) {
				outputer.output(model);
			}
			return model;
		} catch (TrainParamException e) {
			e.printStackTrace();
		}
		return null;
	}

	public double svm_predict(SvmModel svmModel, VectorNode[] x) {
		svm_node[] svm_x = new svm_node[x.length];
		for (int i = 0; i < svm_x.length; i++) {
			svm_x[i] = new svm_node();
			svm_x[i].index = x[i].index;
			svm_x[i].value = x[i].value;
		}
		return svm.svm_predict(svmModel, svm_x);
	}

	public static double svm_predict_probability(SvmModel svmModel,
			VectorNode[] x, double[] prob_estimates) {
		svm_node[] svm_x = new svm_node[x.length];
		for (int i = 0; i < svm_x.length; i++) {
			svm_x[i].index = x[i].index;
			svm_x[i].value = x[i].value;
		}
		return svm.svm_predict_probability(svmModel, svm_x, prob_estimates);
	}
}
