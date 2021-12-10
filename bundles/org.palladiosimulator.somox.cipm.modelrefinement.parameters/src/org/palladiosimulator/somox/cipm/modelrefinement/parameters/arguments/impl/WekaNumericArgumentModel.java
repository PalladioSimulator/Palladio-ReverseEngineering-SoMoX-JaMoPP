package org.palladiosimulator.somox.cipm.modelrefinement.parameters.arguments.impl;

import org.palladiosimulator.somox.cipm.modelrefinement.parameters.arguments.ArgumentModel;
import org.palladiosimulator.somox.cipm.modelrefinement.parameters.util.Utils;
import org.palladiosimulator.somox.cipm.modelrefinement.parameters.util.WekaDataSet;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.attributeSelection.BestFirst;
import weka.attributeSelection.ClassifierSubsetEval;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.functions.LinearRegression;
import weka.core.Instances;
import weka.core.SelectedTag;

/**
 * Class for the argument model of numeric service parameter
 * 
 * @author SonyaV
 *
 */
public class WekaNumericArgumentModel implements ArgumentModel {

	private final LinearRegression linRegClassifier;
	private final WekaDataSet<?> dataset;

	private Instances filteredData;
	private final boolean integerOnly;

	private final boolean withFeatureSelection;

	public WekaNumericArgumentModel(final WekaDataSet<?> dataset, boolean intOnly, boolean withFeatureSelection)
			throws Exception {
		this.integerOnly = intOnly;
		this.withFeatureSelection = withFeatureSelection;

		this.dataset = dataset;
		this.linRegClassifier = new LinearRegression();
		// feature selection
		if (withFeatureSelection) {
			AttributeSelection filter = new AttributeSelection();
			ClassifierSubsetEval evaluator = new ClassifierSubsetEval();
			evaluator.setClassifier(linRegClassifier);
			BestFirst search = new BestFirst();
			search.setDirection(new SelectedTag(2, BestFirst.TAGS_SELECTION));
			filter.setEvaluator(evaluator);
			filter.setSearch(search);
			filter.setInputFormat(this.dataset.getDataSet());
			filteredData = Filter.useFilter(this.dataset.getDataSet(), filter);
			this.linRegClassifier.buildClassifier(filteredData);
			
		} else {
			this.linRegClassifier.buildClassifier(this.dataset.getDataSet());
		}
	}

	/**
	 * Gets the error of the classifier
	 * 
	 * @return relative absolute error(in %)
	 */
	@Override
	public double getError() {
		Evaluation evaluation = null;
		try {
			if (withFeatureSelection) {
				evaluation = new Evaluation(filteredData);
				evaluation.evaluateModel(this.linRegClassifier, filteredData);
			} else {
				evaluation = new Evaluation(this.dataset.getDataSet());
				evaluation.evaluateModel(this.linRegClassifier, this.dataset.getDataSet());
			}
			return Utils.round(evaluation.rootMeanSquaredError(), 3);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public LinearRegression getClassifier() {
		return linRegClassifier;
	}

	public Instances getInstancesDataSet() {
		if (withFeatureSelection) {
			return this.filteredData;
		} else {
			return this.dataset.getDataSet();
		}
	}

	@Override
	public String getStochasticExpression() {
		String stoEx;
		if (withFeatureSelection) {
			stoEx = Utils.getStoExLinReg(this.linRegClassifier, this.filteredData);
		} else {
			stoEx = Utils.getStoExLinReg(this.linRegClassifier, this.dataset.getDataSet());
		}
		return stoEx;
	}

	@Override
	public WekaDataSet<?> getWekaDataSet() {
		return this.dataset;
	}

	public boolean isIntegerOnly() {
		return integerOnly;
	}

}
