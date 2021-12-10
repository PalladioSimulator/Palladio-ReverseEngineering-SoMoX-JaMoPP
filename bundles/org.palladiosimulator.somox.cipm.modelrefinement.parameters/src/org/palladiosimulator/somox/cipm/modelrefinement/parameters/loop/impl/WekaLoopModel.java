package org.palladiosimulator.somox.cipm.modelrefinement.parameters.loop.impl;

import org.palladiosimulator.somox.cipm.modelrefinement.parameters.ServiceCall;
import org.palladiosimulator.somox.cipm.modelrefinement.parameters.util.Utils;
import org.palladiosimulator.somox.cipm.modelrefinement.parameters.util.WekaDataSet;

import org.palladiosimulator.somox.cipm.modelrefinement.parameters.loop.impl.LoopModel;
import weka.attributeSelection.BestFirst;
import weka.attributeSelection.ClassifierSubsetEval;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.functions.LinearRegression;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;

public class WekaLoopModel implements LoopModel {

	private final LinearRegression classifier;
	private final WekaDataSet<Long> dataset;
	//private final AttributeSelection filter;
	//private final ClassifierSubsetEval evaluator;
	//private final BestFirst search;
	//private final Instances filteredData;

	public WekaLoopModel(final WekaDataSet<Long> dataset) throws Exception {
		this.dataset = dataset;
		this.classifier = new LinearRegression();
//Feature selection doesn't work very well here		

//		this.filter = new AttributeSelection();
//		this.evaluator = new ClassifierSubsetEval();
//		evaluator.setClassifier(classifier);
//		this.search = new BestFirst();
//		search.setDirection(new SelectedTag(2, BestFirst.TAGS_SELECTION));
//		filter.setEvaluator(evaluator);
//		filter.setSearch(search);
//		filter.setInputFormat(this.dataset.getDataSet());
//		filteredData = Filter.useFilter(this.dataset.getDataSet(), filter);
		this.classifier.buildClassifier(this.dataset.getDataSet());
	}

	public LinearRegression getClassifier() {
		return classifier;
	}

	@Override
	public double predictIterations(final ServiceCall serviceCall) {
		Instance parametersInstance = this.dataset.buildTestInstance(serviceCall.getParameters());
		try {
			return this.classifier.classifyInstance(parametersInstance);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public WekaDataSet<Long> getWekaDataSet() {
		return this.dataset;
	}

	@Override
	public double getError() {
		Evaluation evaluation = null;
		try {
			evaluation = new Evaluation(this.dataset.getDataSet());
			evaluation.evaluateModel(this.classifier, this.dataset.getDataSet());
			return Utils.round(evaluation.rootMeanSquaredError(), 3);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	@Override
	public String getStochasticExpression() {
		return Utils.getStoExLinReg(classifier, this.dataset.getDataSet());
	}

	@Override
	public Instances getInstancesDataSet() {
		return this.dataset.getDataSet();
	}
}