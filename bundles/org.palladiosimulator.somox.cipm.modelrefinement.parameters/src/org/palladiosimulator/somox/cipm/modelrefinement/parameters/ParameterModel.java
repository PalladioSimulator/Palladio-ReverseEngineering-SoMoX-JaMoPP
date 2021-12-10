package org.palladiosimulator.somox.cipm.modelrefinement.parameters;

import org.palladiosimulator.somox.cipm.modelrefinement.parameters.util.WekaDataSet;
import weka.core.Instances;

public interface ParameterModel {

	public Instances getInstancesDataSet();
	public WekaDataSet<?> getWekaDataSet();
	public String getStochasticExpression();
}
