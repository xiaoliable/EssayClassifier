package com.winga.xxl.classifier.model.outputer;

import java.io.IOException;

import com.winga.xxl.classifier.model.IModel;

public interface IModelOutputer {
	public void output(IModel model) throws IOException;
}
