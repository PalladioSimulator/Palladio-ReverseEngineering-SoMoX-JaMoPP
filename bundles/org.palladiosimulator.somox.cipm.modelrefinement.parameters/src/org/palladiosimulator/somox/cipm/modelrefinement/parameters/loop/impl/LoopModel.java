package org.palladiosimulator.somox.cipm.modelrefinement.parameters.loop.impl;

import org.palladiosimulator.somox.cipm.modelrefinement.parameters.ParameterModel;
import org.palladiosimulator.somox.cipm.modelrefinement.parameters.ServiceCall;

/**
 * Estimated model for a loop.
 * 
 * @author JP
 *
 */
public interface LoopModel extends ParameterModel{

    /**
     * Predicts the number of loop iterations for this loop based on a service call context.
     * 
     * @param serviceCall
     *            Context information, like service call parameters and service execution ID.
     * @return A predicted number of loop iterations for this loop.
     */
    double predictIterations(ServiceCall serviceCall);

    double getError();
}