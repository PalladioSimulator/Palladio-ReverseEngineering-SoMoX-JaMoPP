package org.palladiosimulator.somox.cipm.modelrefinement.parameters.rd.utilization;

import org.palladiosimulator.somox.cipm.modelrefinement.parameters.rd.utilization.ResourceUtilizationDataSet;

/**
 * Interface for estimating remaining resource utilization, by subtracting the resource demands of not monitored
 * internal actions from the total resource utilization.
 * 
 * @author JP
 *
 */
public interface ResourceUtilizationEstimation {

    /**
     * Gets the utilization of the monitored internal actions, by subtracting the estimated utilization of not monitored
     * internal actions from the total resource utilization.
     * 
     * @param totalResourceUtilization
     *            The total resource utilization.
     * @return The utilization of the monitored internal actions.
     */
    ResourceUtilizationDataSet estimateRemainingUtilization(ResourceUtilizationDataSet totalResourceUtilization);

}