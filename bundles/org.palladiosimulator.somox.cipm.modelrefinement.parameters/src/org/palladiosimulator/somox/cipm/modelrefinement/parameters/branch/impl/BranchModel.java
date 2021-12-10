package org.palladiosimulator.somox.cipm.modelrefinement.parameters.branch.impl;

import java.util.Optional;

import org.palladiosimulator.somox.cipm.modelrefinement.parameters.ParameterModel;
import org.palladiosimulator.somox.cipm.modelrefinement.parameters.ServiceCall;

/**
 * Estimated model for a branch.
 * 
 * @author JP
 *
 */
public interface BranchModel extends ParameterModel {

    /**
     * Predicts the branch transition id for this branch based on a service call context.
     * 
     * @param serviceCall
     *            Context information, like service call parameters and service execution ID.
     * @return A predicted branch transition id or empty, if the prediction is, that no branch transition is executed.
     */
    Optional<String> predictBranchId(ServiceCall serviceCall);

    /**
     * Gets the stochastic expression of this estimated model.
     * 
     * @param transitionId
     *            The transition id the stochastic expression is generated for.
     * @return The stochastic expression string.
     */
    String getBranchStochasticExpression(String transitionId);

}