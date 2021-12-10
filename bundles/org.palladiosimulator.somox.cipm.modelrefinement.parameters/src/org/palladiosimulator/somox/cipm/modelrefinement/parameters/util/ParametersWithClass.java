package org.palladiosimulator.somox.cipm.modelrefinement.parameters.util;

import org.palladiosimulator.somox.cipm.modelrefinement.parameters.ServiceParameters;

public class ParametersWithClass<T> {
    private final ServiceParameters parameters;
    
    private final T classValue;
    
    private double weight;

    public ParametersWithClass(ServiceParameters parameters, T classValue) {
        this(parameters, classValue, 1.0);
    }
    
    public ParametersWithClass(ServiceParameters parameters, T classValue, double weight) {
        this.parameters = parameters;
        this.classValue = classValue;
        this.weight = weight;
    }

    public ServiceParameters getParameters() {
        return parameters;
    }

    public T getClassValue() {
        return classValue;
    }
    
    public double getWeight() {
        return weight;
    }
    
}
