package org.palladiosimulator.somox.cipm.modelrefinement.parameters;

import org.palladiosimulator.somox.cipm.modelrefinement.parameters.branch.BranchDataSet;
import org.palladiosimulator.somox.cipm.modelrefinement.parameters.loop.LoopDataSet;
import org.palladiosimulator.somox.cipm.modelrefinement.parameters.rd.ResponseTimeDataSet;
import org.palladiosimulator.somox.cipm.modelrefinement.parameters.rd.utilization.ResourceUtilizationDataSet;

import org.palladiosimulator.somox.cipm.modelrefinement.parameters.SeffParameterEstimation;
import org.palladiosimulator.somox.cipm.modelrefinement.parameters.ServiceCallDataSet;

/**
 * This interface summarizes the monitoring data sets needed for the {@link SeffParameterEstimation}. 
 * The records of the loop, branch and response time reverence records of the service call data set.
 * 
 * @author JP
 *
 */
public interface MonitoringDataSet {

  /**
     * Gets the branches monitoring data.
     * 
     * @return branches monitoring data set.
     */
  BranchDataSet getBranches();

  /**
     * Gets the loops monitoring data.
     * 
     * @return loops monitoring data set.
     */
  LoopDataSet getLoops();

  /**
     * Gets the resource utilization monitoring data.
     * 
     * @return resource utilization monitoring data set.
     */
  ResourceUtilizationDataSet getResourceUtilizations();

  /**
     * Gets the response time monitoring data.
     * 
     * @return response time monitoring data set.
     */
  ResponseTimeDataSet getResponseTimes();

  /**
     * Gets the service call monitoring data. Records of this data set may be referenced by 
     * other monitoring data, like loop records.
     * 
     * @return service call monitoring data set.
     */
  ServiceCallDataSet getServiceCalls();

  /**
     * Gets the external calls monitoring data.
     * @return external calls monitoring data set.
     */
  ServiceCallDataSet getExternalServiceCalls();
}