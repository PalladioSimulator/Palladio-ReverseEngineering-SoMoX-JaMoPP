package org.palladiosimulator.somox.cipm.modelrefinement.parameters.rd;

import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.seff.seff_performance.ParametricResourceDemand;

import org.palladiosimulator.somox.cipm.modelrefinement.parameters.ServiceCallDataSet;
import org.palladiosimulator.somox.cipm.modelrefinement.parameters.monitoring.records.ResponseTimeRecord;
import org.palladiosimulator.somox.cipm.modelrefinement.parameters.monitoring.records.ServiceCallRecord;
import org.palladiosimulator.somox.cipm.modelrefinement.parameters.rd.utilization.ResourceUtilizationDataSet;

import org.palladiosimulator.somox.cipm.modelrefinement.parameters.rd.ResponseTimeDataSet;

/**
 * Interface for resource demand estimation implementations, which use {@link ServiceCallRecord},
 * {@link ResponseTimeRecord} and resource utilization records to estimate resource demands and update the PCM.
 * 
 * @author JP
 *
 */
public interface ResourceDemandEstimation {

    /**
     * Updates the stochastic expressions of {@link ParametricResourceDemand}, based on the monitored service call
     * parameters, loop resource utilization records and response time records.
     * 
     * @param pcmModel
     *            The model which will be updated.
     * @param serviceCalls
     *            The monitored service calls, including service call parameters.
     * @param resourceUtilizations
     *            The monitored resource utilization.
     * @param responseTimes
     *            The monitored response times.
     */
    void update(Repository pcmModel, ServiceCallDataSet serviceCalls,
            ResourceUtilizationDataSet resourceUtilizations, ResponseTimeDataSet responseTimes);
}