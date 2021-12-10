package org.palladiosimulator.somox.cipm.modelrefinement.parameters;

import org.palladiosimulator.pcm.repository.Repository;

import org.palladiosimulator.somox.cipm.modelrefinement.parameters.arguments.impl.ArgumentEstimationImpl;
import org.palladiosimulator.somox.cipm.modelrefinement.parameters.branch.impl.BranchEstimationImpl;
import org.palladiosimulator.somox.cipm.modelrefinement.parameters.loop.impl.LoopEstimationImpl;
import org.palladiosimulator.somox.cipm.modelrefinement.parameters.optimization.genetic.OptimizationConfig;
import org.palladiosimulator.somox.cipm.modelrefinement.parameters.rd.impl.ResourceDemandEstimationImpl;

import org.palladiosimulator.somox.cipm.modelrefinement.parameters.MonitoringDataSet;
import org.palladiosimulator.somox.cipm.modelrefinement.parameters.SeffParameterEstimation;

/**
 * This is the main entry point for estimating SEFF parameters, like loop iterations, branch executions and resource
 * demands, based on monitoring data.
 *
 * @author JP
 *
 */
public class SeffParameterEstimation {

    private final LoopEstimationImpl loopEstimation;

    private final BranchEstimationImpl branchEstimation;

    private final ResourceDemandEstimationImpl resourceDemandEstimation;
    
    private final ArgumentEstimationImpl argumentEstimation;    
    
    //needed for the evaluation; can be removed later on
    private boolean withExtCalls;
    private boolean withFeatureSelection;
    private boolean withReturnValue;

    /**
     * Initializes a new instance of {@link SeffParameterEstimation}.
     */
    public SeffParameterEstimation(boolean withOpt, boolean withExtCalls, boolean withFeatureSelection, boolean withReturnValue, OptimizationConfig config) {
    	this.withExtCalls = withExtCalls;
    	this.withFeatureSelection = withFeatureSelection;
    	this.withReturnValue = withReturnValue;
    	
        this.loopEstimation = new LoopEstimationImpl(false, config);
        this.branchEstimation = new BranchEstimationImpl();
        this.resourceDemandEstimation = new ResourceDemandEstimationImpl(this.loopEstimation, this.branchEstimation, withOpt, config);
        this.argumentEstimation = new ArgumentEstimationImpl(withOpt, withFeatureSelection, withReturnValue, config);
    }

    /**
     * Updates the SEFF parameters in the PCM based on the monitoring data.
     *
     * @param pcm
     *            The Palladio Component Model Repository, including the SEFFs of the services we will estimate
     *            parameters for.
     * @param monitoringDataSet
     *            We use this monitoring data for estimating the SEFF Parameters.
     */
    public void update(final Repository pcm, final MonitoringDataSet monitoringDataSet) {
        this.loopEstimation.update(pcm, monitoringDataSet.getServiceCalls(), monitoringDataSet.getLoops());
        this.branchEstimation.update(pcm, monitoringDataSet.getServiceCalls(), monitoringDataSet.getBranches());
        this.resourceDemandEstimation.update(pcm, monitoringDataSet.getServiceCalls(),
        	monitoringDataSet.getResourceUtilizations(), monitoringDataSet.getResponseTimes());
        if(this.withExtCalls) {
        	this.argumentEstimation.update(pcm, monitoringDataSet.getExternalServiceCalls());
        }
    }
}
