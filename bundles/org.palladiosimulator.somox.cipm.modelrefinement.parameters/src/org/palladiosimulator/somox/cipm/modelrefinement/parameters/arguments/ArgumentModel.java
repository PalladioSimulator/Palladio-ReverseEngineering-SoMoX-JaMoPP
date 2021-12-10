package org.palladiosimulator.somox.cipm.modelrefinement.parameters.arguments;

import org.palladiosimulator.somox.cipm.modelrefinement.parameters.ParameterModel;
import weka.core.Instances;

/**
 * Interface for the arguments estimation model (can be numeric or nominal)
 * @author SonyaV
 *
 */
public interface ArgumentModel extends ParameterModel {
  
    public double getError();
    
    public boolean isIntegerOnly();
}