package org.palladiosimulator.somox.cipm.modelrefinement.parameters.arguments.impl;

import org.palladiosimulator.somox.cipm.modelrefinement.parameters.util.WekaDataSet;

/**
 * Interface for the arguments estimation model (can be numeric or nominal)
 * @author SonyaV
 *
 */
public interface ArgumentModel {

    String getArgumentStochasticExpression();

    WekaDataSet getDataSet();
    
    double getError();
}