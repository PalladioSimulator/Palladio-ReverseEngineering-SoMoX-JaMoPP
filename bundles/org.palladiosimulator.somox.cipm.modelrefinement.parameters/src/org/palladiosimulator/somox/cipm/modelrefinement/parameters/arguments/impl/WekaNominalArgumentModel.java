package org.palladiosimulator.somox.cipm.modelrefinement.parameters.arguments.impl;

import org.palladiosimulator.somox.cipm.modelrefinement.parameters.arguments.ArgumentModel;
import org.palladiosimulator.somox.cipm.modelrefinement.parameters.branch.impl.WekaBranchModel.StochasticExpressionJ48;
import org.palladiosimulator.somox.cipm.modelrefinement.parameters.util.Utils;
import org.palladiosimulator.somox.cipm.modelrefinement.parameters.util.WekaDataSet;
import weka.attributeSelection.BestFirst;
import weka.attributeSelection.ClassifierSubsetEval;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;

/**
 * Class for the argument model of nominal service parameter
 * 
 * @author SonyaV
 *
 */
public class WekaNominalArgumentModel implements ArgumentModel {

	private final J48 j48Tree;
	private final WekaDataSet<?> dataset;

	private Instances filteredData;

	private final boolean withFeatureSelection;

	public WekaNominalArgumentModel(final WekaDataSet<?> dataset, boolean withFeatureSelection) throws Exception {
		this.withFeatureSelection = withFeatureSelection;

		this.dataset = dataset;
		this.j48Tree = new J48();
		// feature selection
		if (withFeatureSelection) {
			AttributeSelection filter = new AttributeSelection();
			ClassifierSubsetEval evaluator = new ClassifierSubsetEval();
			evaluator.setClassifier(j48Tree);
			BestFirst search = new BestFirst();
			search.setDirection(new SelectedTag(2, BestFirst.TAGS_SELECTION));
			filter.setEvaluator(evaluator);
			filter.setSearch(search);
			filter.setInputFormat(this.dataset.getDataSet());
			filteredData = Filter.useFilter(this.dataset.getDataSet(), filter);

			this.j48Tree.buildClassifier(filteredData);
		} else {
			this.j48Tree.buildClassifier(this.dataset.getDataSet());
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
				evaluation.evaluateModel(this.j48Tree, filteredData);
			} else {
				evaluation = new Evaluation(this.dataset.getDataSet());
				evaluation.evaluateModel(this.j48Tree, this.dataset.getDataSet());
			}
			return Utils.round(evaluation.rootMeanSquaredError(), 3);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
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
		return Utils.getStoExTree(this.j48Tree);
//		String[] attrExpr = new String[this.filteredData.numAttributes()];
//		for (int i = 0; i < this.filteredData.numAttributes(); i++) {
//			Attribute attr = this.filteredData.attribute(i);
//			attrExpr[i] = attr.name();
//		}
//		return j48Tree.getBranchStochasticExpression(0, attrExpr);
	}

	@Override
	public WekaDataSet<?> getWekaDataSet() {
		return this.dataset;
	}

	@Override
	public boolean isIntegerOnly() {
		return false;
	}

}
