package org.palladiosimulator.somox.cipm.modelrefinement.parameters.branch;

import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.seff.BranchAction;
import org.palladiosimulator.pcm.seff.GuardedBranchTransition;

import org.palladiosimulator.somox.cipm.modelrefinement.parameters.ServiceCallDataSet;
import org.palladiosimulator.somox.cipm.modelrefinement.parameters.monitoring.records.BranchRecord;
import org.palladiosimulator.somox.cipm.modelrefinement.parameters.monitoring.records.ServiceCallRecord;

import org.palladiosimulator.somox.cipm.modelrefinement.parameters.branch.BranchDataSet;

/**
 * Interface for branch estimation implementations, which use {@link ServiceCallRecord} and {@link BranchRecord} to
 * estimate branch executions and update the PCM.
 * 
 * @author JP
 *
 */
public interface BranchEstimation {

    /**
     * Updates the stochastic expressions of branch transitions {@link GuardedBranchTransition} of {@link BranchAction},
     * based on the monitored service call parameters and branch records.
     * 
     * @param pcmModel
     *            The model which will be updated.
     * @param serviceCalls
     *            The monitored service calls, including service call parameters.
     * @param branchExecutions
     *            The monitored branch records.
     */
    void update(Repository pcmModel, ServiceCallDataSet serviceCalls, BranchDataSet branchExecutions);
}