package org.palladiosimulator.somox.cipm.modelrefinement.parameters.rd.impl;

import org.palladiosimulator.somox.cipm.modelrefinement.parameters.ParameterModel;
import org.palladiosimulator.somox.cipm.modelrefinement.parameters.ServiceCall;

/**
 * Estimated model for a resource demand.
 * 
 * @author JP
 *
 */
public interface ResourceDemandModel extends ParameterModel {

    /**
     * Predicts the resource demand in seconds for this resource demand based on a service call context.
     * 
     * @param serviceCall
     *            Context information, like service call parameters and service execution ID.
     * @return A predicted resource demand in seconds for this resource demand.
     */
    double predictResourceDemand(ServiceCall serviceCall);

    /**
     * Gets the stochastic expression of this estimated model.
     * 
     * @return The stochastic expression string.
     */
    String getResourceDemandStochasticExpression();
    
    double getError();
}