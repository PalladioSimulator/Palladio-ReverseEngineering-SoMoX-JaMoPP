  
package org.palladiosimulator.somox.cipm.modelrefinement.parameters.rd.impl;

import java.util.StringJoiner;

import org.palladiosimulator.somox.cipm.modelrefinement.parameters.ServiceCall;
import org.palladiosimulator.somox.cipm.modelrefinement.parameters.util.Utils;
import org.palladiosimulator.somox.cipm.modelrefinement.parameters.util.WekaDataSet;

import org.palladiosimulator.somox.cipm.modelrefinement.parameters.rd.impl.ResourceDemandModel;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.functions.LinearRegression;
import weka.core.Instance;
import weka.core.Instances;

public class WekaResourceDemandModel implements ResourceDemandModel {

    private final LinearRegression classifier;
    private final WekaDataSet<Double> dataset;

    public WekaResourceDemandModel(final WekaDataSet<Double> dataset) throws Exception {
        this.dataset = dataset;
        this.classifier = new LinearRegression();
        this.classifier.buildClassifier(dataset.getDataSet());
    }

    
    public LinearRegression getClassifier() {
        return classifier;
    }

    @Override
    public double predictResourceDemand(final ServiceCall serviceCall) {
        Instance parametersInstance = this.dataset.buildTestInstance(serviceCall.getParameters());
        try {
            return this.classifier.classifyInstance(parametersInstance);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getResourceDemandStochasticExpression() {
        StringJoiner result = new StringJoiner(" + (");
        double[] coefficients = this.classifier.coefficients();
        int braces = 0;
        for (int i = 0; i < coefficients.length - 2; i++) {
            if (coefficients[i] == 0.0) {
                continue;
            }
            StringBuilder coefficientPart = new StringBuilder();
            String paramStoEx = this.dataset.getStochasticExpressionForIndex(i);
            coefficientPart.append(coefficients[i]).append(" * ").append(paramStoEx);
            result.add(coefficientPart.toString());
            braces++;
        }
        result.add(String.valueOf(coefficients[coefficients.length - 1]));
        StringBuilder strBuilder = new StringBuilder().append(result.toString());
        for (int i = 0; i < braces; i++) {
            strBuilder.append(")");
        }
        return strBuilder.toString();
    }


	@Override
	public WekaDataSet<Double> getWekaDataSet() {
		return this.dataset;
	}


	@Override
	public String getStochasticExpression() {
		return getResourceDemandStochasticExpression();
	}


	@Override
	public double getError() {
		Evaluation evaluation = null;
		try {
			evaluation = new Evaluation(this.dataset.getDataSet());
			evaluation.evaluateModel(this.classifier, this.dataset.getDataSet());
			return Utils.round(evaluation.rootMeanSquaredError(),3);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}


	@Override
	public Instances getInstancesDataSet() {
		return this.dataset.getDataSet();
	}
}