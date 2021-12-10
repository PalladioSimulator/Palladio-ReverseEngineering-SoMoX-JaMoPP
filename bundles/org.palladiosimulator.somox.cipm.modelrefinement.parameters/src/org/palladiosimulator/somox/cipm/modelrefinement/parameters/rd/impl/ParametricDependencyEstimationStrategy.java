package org.palladiosimulator.somox.cipm.modelrefinement.parameters.rd.impl;

import java.util.Map;

import org.palladiosimulator.somox.cipm.modelrefinement.parameters.ServiceParameters;

import org.palladiosimulator.somox.cipm.modelrefinement.parameters.rd.impl.ResourceDemandModel;

/**
 * Interface for parametric dependency estimation implementations.
 * 
 * @author JP
 *
 */
public interface ParametricDependencyEstimationStrategy {

    /**
     * Gets the estimated resource demand model for a internal action id and resource type id, by passing the resource
     * demands in relation to service parameters.
     * 
     * @param internalActionId
     *            The internal action id of the resource demand.
     * @param resourceId
     *            The resource type id.
     * @param resourceDemands
     *            The average resource demands in relation to service parameters.
     * @return The estimated resource demand model.
     */
    ResourceDemandModel estimateResourceDemandModel(String internalActionId, String resourceId,
            Map<ServiceParameters, Double> resourceDemands);

}