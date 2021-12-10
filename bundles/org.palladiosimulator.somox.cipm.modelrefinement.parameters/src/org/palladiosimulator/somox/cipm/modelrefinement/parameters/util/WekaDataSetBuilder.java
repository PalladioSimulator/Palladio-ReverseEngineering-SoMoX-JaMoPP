package org.palladiosimulator.somox.cipm.modelrefinement.parameters.util;

import java.util.ArrayList;
import java.util.List;

import org.palladiosimulator.somox.cipm.modelrefinement.parameters.ServiceCallDataSet;
import org.palladiosimulator.somox.cipm.modelrefinement.parameters.ServiceParameters;

import org.palladiosimulator.somox.cipm.modelrefinement.parameters.util.ParametersWithClass;
import org.palladiosimulator.somox.cipm.modelrefinement.parameters.util.WekaDataSet;
import org.palladiosimulator.somox.cipm.modelrefinement.parameters.util.WekaDataSetMode;

/**
 * Helper class for weka model estimations.
 * 
 * @author JP
 *
 */
public class WekaDataSetBuilder<T> {

    private final ServiceCallDataSet serviceCalls;

    private final List<ParametersWithClass<T>> values;
    
    private final WekaDataSetMode mode;
    
    /**
     * Initializes a new instance of {@link WekaDataSet}.
     * 
     * @param serviceCalls
     *            The service call data set.
     * @param initialServiceExecutionId
     *            A service execution id for getting service parameters. These parameters are used to build the weka
     *            attribute.
     * @param classAttribute
     *            The class attribute for the weka data set.
     */
    public WekaDataSetBuilder(
            final ServiceCallDataSet serviceCalls,
            final WekaDataSetMode mode) {
        this.serviceCalls = serviceCalls;
        this.mode = mode;
        this.values = new ArrayList<ParametersWithClass<T>>();
    }
    
    public WekaDataSetBuilder(final WekaDataSetMode mode) {
        this(null, mode);
    }

    /**
     * Adds a data instance, consisting of the service parameters and the class value.
     * 
     * @param serviceExecutionId
     *            The service execution id (of the caller) is used to get the service parameters.
     * @param classValue
     *            The class value, like resource demand or loop iteration.
     */
    public void addInstance(final String serviceExecutionId, final T classValue, double weight) {
        if (serviceCalls == null) {
            throw new IllegalArgumentException("No service call data set was specified.");
        }
        ServiceParameters callerParameters = this.serviceCalls.getParametersOfServiceCall(serviceExecutionId);
        
        this.addInstance(callerParameters, classValue, 1.0);
    }
    
    public void addInstanceWithReturnValues(final String serviceExecutionId, ServiceParameters returnValues, final T classValue) {
    	ServiceParameters callerParameters = this.serviceCalls.getParametersOfServiceCall(serviceExecutionId);
    	addInstance(callerParameters.merge(returnValues), classValue);
    }
    public void addInstance(final ServiceParameters recordParameters, final T classValue) {
        this.addInstance(recordParameters, classValue, 1.0);
    }
    
    public void addInstance(final ServiceParameters recordParameters, final T classValue, double weight) {
        ParametersWithClass<T> newValue = new ParametersWithClass<T>(recordParameters, classValue, weight);
        this.values.add(newValue);
    }
    
    public void addInstance(final String serviceExecutionId, final T classValue) {
        this.addInstance(serviceExecutionId, classValue, 1.0);
    }
    
    public WekaDataSet<T> build() {
    	WekaDataSet<T> dataSet = new WekaDataSet<T>(this.values, this.mode);
        return dataSet;
    }

	public WekaDataSetMode getMode() {
		return mode;
	}
	
}
